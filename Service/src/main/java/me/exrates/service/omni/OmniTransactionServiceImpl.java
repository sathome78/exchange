package me.exrates.service.omni;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.model.dto.*;
import me.exrates.model.dto.merchants.omni.OmniTxDto;
import me.exrates.service.RefillService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@PropertySource("classpath:/merchants/omni.properties")
@Log4j2(topic = "omni_log")
@Service
@Conditional(MonolitConditional.class)
public class OmniTransactionServiceImpl implements OmniTransactionService {

    private final MerchantSpecParamsDao specParamsDao;
    private final OmniNodeService omniNodeService;
    private final OmniService omniService;
    private final RefillService refillService;
    private final ObjectMapper objectMapper;

    private static final String LAST_BLOCK_PARAM = "LastScannedBlock";
    private static final Integer LAST_BLOCK_TO_SCAN = 999999999;
    private static final Integer TX_COUNT_TO_SCAN = 99999;
    private static final String ALL_ADDRESSES_SCAN = "*";
    private static final Integer OFFSET_TO_SCAN = 0;
    private static final Integer TIME_TO_ONE_TRANSFER_SECONDS = 20;

    private @Value("${omni.hotwallet.address}")String mainAddress;
    private @Value("${omni.comission.address}")String comissionAddress;

    private ScheduledExecutorService txScheduler = Executors.newScheduledThreadPool(1);
    private ScheduledExecutorService unconfScheduler = Executors.newScheduledThreadPool(1);

    @Autowired
    public OmniTransactionServiceImpl(MerchantSpecParamsDao specParamsDao, OmniNodeService omniNodeService, OmniService omniService, RefillService refillService, ObjectMapper objectMapper) {
        this.specParamsDao = specParamsDao;
        this.omniNodeService = omniNodeService;
        this.omniService = omniService;
        this.refillService = refillService;
        this.objectMapper = objectMapper;
    }

    @PostConstruct
    private void init() {
        txScheduler.scheduleAtFixedRate(this::checkTransactions, 3, 7, TimeUnit.MINUTES);
        unconfScheduler.scheduleAtFixedRate(this::checkUnconfirmed, 3, 8, TimeUnit.MINUTES);
        unconfScheduler.scheduleAtFixedRate(this::sendToMainAddressJob, 10, 90, TimeUnit.MINUTES);
    }

    private void checkTransactions() {
        try {
            AtomicInteger lastBlock = new AtomicInteger(loadLastBlock() + 1);
            String rawTxs = omniNodeService.listTransactions(ALL_ADDRESSES_SCAN, TX_COUNT_TO_SCAN, OFFSET_TO_SCAN, lastBlock.get(), LAST_BLOCK_TO_SCAN);
            JSONArray jsonArray = new JSONArray(rawTxs);
            jsonArray.forEach(p -> {
                try {
                    OmniTxDto transactionDto = objectMapper.readValue(p.toString(), new TypeReference<OmniTxDto>(){});
                    if (lastBlock.get() < transactionDto.getBlock()) {
                        saveLastBlock(transactionDto.getBlock());
                        lastBlock.set(transactionDto.getBlock());
                    }
                    if (transactionDto.isValid() && transactionDto.isIsmine() && transactionDto.getPropertyid() == omniService.getUsdtPropertyId()
                            && !transactionDto.getSendingaddress().equals(transactionDto.getReferenceaddress()))  {
                        processTransaction(transactionDto);
                    }
                } catch (Exception e) {
                    log.error(e);
                }
            });
        } catch (Exception e) {
            log.error("error while checking new transactions {}", e);
        }
    }

    @Synchronized
    private void processTransaction(OmniTxDto dto) throws RefillRequestAppropriateNotFoundException {
        switch (dto.getTxType()) {
            case Simple_Send : {
                RefillRequestAcceptDto acceptDto = omniService.createRequest(dto.getReferenceaddress(), dto.getTxid(), dto.getAmount());
                if (dto.getConfirmations() < omniService.minConfirmationsRefill()) {
                    omniService.putOnBchExam(RefillRequestPutOnBchExamDto.builder()
                            .requestId(acceptDto.getRequestId())
                            .merchantId(omniService.getMerchant().getId())
                            .currencyId(omniService.getCurrency().getId())
                            .address(acceptDto.getAddress())
                            .amount(acceptDto.getAmount())
                            .hash(acceptDto.getMerchantTransactionId())
                            .blockhash(dto.getBlockhash())
                            .confirmations(dto.getConfirmations())
                            .build());
                } else {
                    autoAcceptPayment(acceptDto.getRequestId().toString(), acceptDto.getAddress(), acceptDto.getMerchantTransactionId(), acceptDto.getAmount().toPlainString());
                }
                break;
            }
            case Freeze_Transaction : {
                omniService.frozeCoins(dto.getReferenceaddress(), dto.getAmount());
                break;
            }
        }
    }

    private void autoAcceptPayment(String requestId, String address, String hash, String amount) throws RefillRequestAppropriateNotFoundException {
        refillService.updateAddressNeedTransfer(address, omniService.getMerchant().getId(), omniService.getCurrency().getId(), true);
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("txId", hash);
        paramsMap.put("address", address);
        paramsMap.put("amount", amount);
        paramsMap.put("id", requestId);
        omniService.processPayment(paramsMap);
    }

    private void checkUnconfirmed() {
        try {
            List<RefillRequestFlatDto> dtos = refillService.getInExamineWithChildTokensByMerchantIdAndCurrencyIdList(omniService.getMerchant().getId(), omniService.getCurrency().getId());
            dtos.forEach(p->{
                try {
                    OmniTxDto transactionDto = getTransaction(p.getMerchantTransactionId());
                    if (transactionDto.isValid() && transactionDto.isIsmine() && transactionDto.getPropertyid() == omniService.getUsdtPropertyId()) {
                        Integer lastConf = refillService.getAdditionalData(p.getId()).getConfirmations();
                        if (transactionDto.getConfirmations() != lastConf) {
                            refillService.setConfirmationCollectedNumber(RefillRequestSetConfirmationsNumberDto.builder()
                                    .requestId(p.getId())
                                    .address(p.getAddress())
                                    .amount(p.getAmount())
                                    .confirmations(transactionDto.getConfirmations())
                                    .currencyId(omniService.getCurrency().getId())
                                    .merchantId(omniService.getMerchant().getId())
                                    .hash(p.getMerchantTransactionId())
                                    .blockhash(transactionDto.getBlockhash()).build());
                        }
                        if (transactionDto.getConfirmations() >= omniService.minConfirmationsRefill()) {
                            autoAcceptPayment(String.valueOf(p.getId()), p.getAddress(), p.getMerchantTransactionId(), p.getAmount().toPlainString());
                        }
                    } else {
                        refillService.declineMerchantRefillRequest(p.getId());
                    }
                } catch (Exception e) {
                    log.error(e);
                }
            });
        } catch (Exception e) {
            log.error(e);
        }
    }

    private void sendToMainAddressJob() {
        try {
            log.debug("send to main job");
            List<RefillRequestAddressDto> listRefillRequestAddressDto = refillService.findAllAddressesNeededToTransfer(omniService.getMerchant().getId(), omniService.getCurrency().getId());
            if (!listRefillRequestAddressDto.isEmpty()) {
                unlockWallet(omniService.getWalletPassword(), TIME_TO_ONE_TRANSFER_SECONDS * listRefillRequestAddressDto.size());
            }
            listRefillRequestAddressDto.forEach(p->{
                try {
                    transferToMainAccount(p);
                    refillService.updateAddressNeedTransfer(p.getAddress(), omniService.getMerchant().getId(), omniService.getCurrency().getId(), false);
                } catch (Exception e) {
                    log.error(e);
                }
            });
        } catch (Exception e) {
            log.error(e);
        }
    }

    private void transferToMainAccount(RefillRequestAddressDto dto) {
        log.debug("transfer to main " + dto);
        String accountBalance = getActiveBalance(dto.getAddress());
        JSONObject res = omniNodeService.sendFunded(dto.getAddress(), mainAddress, omniService.getUsdtPropertyId(), accountBalance, comissionAddress);
        log.debug("send to main result {}", res);
        if (!res.isNull("error")) {
            log.error("error send funds {}", res.getString("result"));
            throw new RuntimeException("error transfer");
        }
        String innerTXHash = res.getString("result");
        /*refillService.setInnerTransferHash(dto.getId(), innerTXHash);*/ /*todo set hash to request, not to address*/
        log.debug("tx hash {}", innerTXHash);
    }

    private void unlockWallet(String password, int seconds) {
        Preconditions.checkState(omniNodeService.unlockWallet(password, seconds), "Wallet unlocking error");
    }

    @Override
    public String getActiveBalance(String address) {
        String response = omniNodeService.getBalance(address, omniService.getUsdtPropertyId());
        return new JSONObject(response).getString("balance");
    }

    private OmniTxDto getTransaction(String hash) throws IOException {
        return objectMapper.readValue(omniNodeService.getTransaction(hash), new TypeReference<OmniTxDto>(){});
    }

    private void saveLastBlock(int blockNum) {
        specParamsDao.updateParam(omniService.getMerchant().getName(), LAST_BLOCK_PARAM, String.valueOf(blockNum));
    }

    private int loadLastBlock() {
        MerchantSpecParamDto specParamsDto = specParamsDao.getByMerchantNameAndParamName(omniService.getMerchant().getName(), LAST_BLOCK_PARAM);
        return StringUtils.isEmpty(specParamsDto.getParamValue()) ? 0 : Integer.parseInt(specParamsDto.getParamValue());
    }
}
