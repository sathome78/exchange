package me.exrates.service.tron;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.RefillRequestAddressDto;
import me.exrates.model.dto.RefillRequestFlatDto;
import me.exrates.model.dto.TronReceivedTransactionDto;
import me.exrates.model.dto.TronTransferDto;
import me.exrates.service.RefillService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Log4j2(topic = "tron")
@PropertySource("classpath:/merchants/tron.properties")
@Service
public class TronTransactionsServiceImpl implements TronTransactionsService {


    @Autowired
    public TronTransactionsServiceImpl(TronNodeService tronNodeService, TronService tronService, RefillService refillService) {
        this.tronNodeService = tronNodeService;
        this.tronService = tronService;
        this.refillService = refillService;
    }



    private @Value("${tron.mainAccountHEXAddress}")String MAIN_ADDRESS_HEX;
    private final TronNodeService tronNodeService;
    private final TronService tronService;
    private final RefillService refillService;

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledExecutorService transferScheduler = Executors.newScheduledThreadPool(1);

    @PostConstruct
    private void init() {
        scheduler.scheduleAtFixedRate(this::checkUnconfirmedJob, 3, 5, TimeUnit.MINUTES);
        transferScheduler.scheduleAtFixedRate(this::transferToMainAccountJob, 3, 20, TimeUnit.MINUTES);
    }

    private void checkUnconfirmedJob() {
        List<RefillRequestFlatDto> dtos = refillService.getInExamineWithChildTokensByMerchantIdAndCurrencyIdList(tronService.getMerchantId(), tronService.getCurrencyId());
        dtos.forEach(p->{
            try {
                if (checkIsTransactionConfirmed(p.getMerchantTransactionId())) {
                    processTransaction(p.getAddress(), p.getMerchantTransactionId(), p.getAmount().toString());
                }
            } catch (Exception e) {
                log.error(e);
            }
        });

    }

    private void transferToMainAccountJob() {
        List<RefillRequestAddressDto> listRefillRequestAddressDto = refillService.findAllAddressesNeededToTransfer(tronService.getMerchantId(), tronService.getCurrencyId());
        listRefillRequestAddressDto.forEach(p->{
            try {
                transferToMainAccount(p);
                refillService.updateAddressNeedTransfer(p.getAddress(), tronService.getMerchantId(), tronService.getCurrencyId(), false);
            } catch (Exception e) {
                log.error(e);
            }
        });
    }

    private void transferToMainAccount(RefillRequestAddressDto dto) {
        Long accountAmount = tronNodeService.getAccount(dto.getAddress()).getJSONObject("data").getLong("balance");
        easyTransferByPrivate(dto.getPrivKey(), MAIN_ADDRESS_HEX, accountAmount);
    }

    @Override
    public boolean checkIsTransactionConfirmed(String txHash) {
        JSONObject rawResponse = tronNodeService.getTransaction(txHash);
        return rawResponse.getBoolean("confirmed");
    }

    @Override
    public void processTransaction(TronReceivedTransactionDto p) {
        processTransaction(p.getAddressBase58(), p.getHash(), p.getAmount());
    }

    @Override
    public void processTransaction(String address, String hash, String amount) {
        Map<String, String> map = new HashMap<>();
        map.put("address", address);
        map.put("hash", hash);
        map.put("amount", amount);
        try {
            tronService.processPayment(map);
            refillService.updateAddressNeedTransfer(address, tronService.getMerchantId(), tronService.getCurrencyId(), true);
        } catch (RefillRequestAppropriateNotFoundException e) {
            log.error("request not found {}", address);
        }
    }

    private void easyTransferByPrivate(String pk, String addressTo, long amount) {
        TronTransferDto tronTransferDto = new TronTransferDto(pk, addressTo, amount);
        JSONObject object = tronNodeService.transferFunds(tronTransferDto);
        boolean result = object.getJSONObject("result").getBoolean("result");
        if (!result) {
            throw new RuntimeException("erro trnasfer to main account");
        }
    }


    /*
    interface CommonConstant {
        byte ADD_PRE_FIX_BYTE_MAINNET = (byte) 0x41;   //41 + address
        byte ADD_PRE_FIX_BYTE_TESTNET = (byte) 0xa0;   //a0 + address
        int ADDRESS_SIZE = 21;
    }

    private static boolean addressValid(byte[] address) {
        if (ArrayUtils.isEmpty(address)) {
            log.warn("Warning: Address is empty !!");
            return false;
        }
        if (address.length != CommonConstant.ADDRESS_SIZE) {
            log.warn(
                    "Warning: Address length need " + CommonConstant.ADDRESS_SIZE + " but " + address.length
                            + " !!");
            return false;
        }
        byte preFixbyte = address[0];
        if (preFixbyte != CommonConstant.ADD_PRE_FIX_BYTE_MAINNET) {
            log.warn("Warning: Address need prefix with " + CommonConstant.ADD_PRE_FIX_BYTE_MAINNET + " but "
                            + preFixbyte + " !!");
            return false;
        }
        //Other rule;
        return true;
    }*/

}
