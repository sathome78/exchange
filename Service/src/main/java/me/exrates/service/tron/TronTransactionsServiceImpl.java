package me.exrates.service.tron;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.model.dto.*;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.bitshares.memo.Preconditions;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import retrofit2.http.Query;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;


@Log4j2(topic = "tron")
@PropertySource("classpath:/merchants/tron.properties")
@Service
@Conditional(MonolitConditional.class)
public class TronTransactionsServiceImpl implements TronTransactionsService {

    @Autowired
    public TronTransactionsServiceImpl(TronNodeService tronNodeService, TronService tronService, RefillService refillService, TronTokenContext tronTokenContext) {
        this.tronNodeService = tronNodeService;
        this.tronService = tronService;
        this.refillService = refillService;
        this.tronTokenContext = tronTokenContext;
    }



    private @Value("${tron.mainAccountHEXAddress}")String MAIN_ADDRESS_HEX;
    private @Value("${tron.trc20ContractAddress}")String CONTRACT_ADDRESS_HEX;
    private @Value("${tron.functionSelector}")String FUNCTION_SELECTOR;
    private final TronNodeService tronNodeService;
    private final TronService tronService;
    private final RefillService refillService;
    private final TronTokenContext tronTokenContext;

    @Autowired
    private MerchantService merchantService;

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledExecutorService transferScheduler = Executors.newScheduledThreadPool(3);

    @PostConstruct
    private void init() {
        //TODO transfer to main account
        scheduler.scheduleAtFixedRate(this::checkUnconfirmedJob, 5, 2, TimeUnit.MINUTES);
        transferScheduler.scheduleAtFixedRate(this::transferToMainAccountJob, 2, 5, TimeUnit.MINUTES);
        transferScheduler.scheduleAtFixedRate(this::transferTokensToMainAccountJob, 2, 5, TimeUnit.MINUTES);
    }

    private void checkUnconfirmedJob() {
        List<RefillRequestFlatDto> dtos = refillService.getInExamineWithChildTokensByMerchantIdAndCurrencyIdList(tronService.getMerchantId(), tronService.getCurrencyId());
        dtos.forEach(p->{
            try {
                if (checkIsTransactionConfirmed(p.getMerchantTransactionId())) {
                    processTransaction(p.getId(), p.getAddress(), p.getMerchantTransactionId(), p.getAmount().toString(), p.getMerchantId(), p.getCurrencyId());
                }
            } catch (Exception e) {
                log.error(e);
            }
        });

    }

    private void transferToMainAccountJob() {
        Integer usdtMerchant = merchantService.findByName("USDT(TRX)").getId();
        List<RefillRequestAddressDto> listRefillRequestAddressDto = refillService.findAllAddressesNeededToTransfer(tronService.getMerchantId(), tronService.getCurrencyId());
        listRefillRequestAddressDto.forEach(p->{
            try {
                if(p.getMerchantId().equals(usdtMerchant)){  //need to set TRON TRC20 id from merchant
                    log.debug("Start transfer founds for USDT(TRX)");
                    transferToMainAccountTRC20(p);
                }else {
                    transferToMainAccount(p);
                }
                refillService.updateAddressNeedTransfer(p.getAddress(), tronService.getMerchantId(), tronService.getCurrencyId(), false);
            } catch (Exception e) {
                log.error(e);
            }
        });
    }

    private void transferTokensToMainAccountJob() {
        List<TronTrc10Token> tokensList = tronTokenContext.getAll();
        List<RefillRequestAddressDto> listRefillRequestAddressDto = new ArrayList<>();
        tokensList.forEach(p -> listRefillRequestAddressDto.addAll(refillService.findAllAddressesNeededToTransfer(p.getMerchantId(), p.getCurrencyId())));
        listRefillRequestAddressDto.forEach(p->{
            try {
                TronTrc10Token token = tronTokenContext.getByCurrencyId(p.getCurrencyId());
                transferTokenToMainAccount(p, token.getNameDescription(), token.getBlockchainName());
                refillService.updateAddressNeedTransfer(p.getAddress(), p.getMerchantId(), p.getCurrencyId(), false);
            } catch (Exception e) {
                log.error(e);
            }
        });

    }

    private void transferToMainAccount(RefillRequestAddressDto dto) {
        Long accountAmount = tronNodeService.getAccount(dto.getPubKey()).getLong("balance");
        log.debug("balance {} {}", dto.getAddress(), accountAmount);
        easyTransferByPrivate(dto.getPrivKey(), MAIN_ADDRESS_HEX, accountAmount);
    }

    //this method only for TRON trc20 until developers won`t create id for coin
    private void transferToMainAccountTRC20(RefillRequestAddressDto dto) {
        Long accountAmount = tronNodeService.getAccount(dto.getPubKey()).getLong("balance");
        log.debug("balance {} {}", dto.getAddress(), accountAmount);
        transferFoundsTRC20(dto.getPrivKey(), MAIN_ADDRESS_HEX, dto.getAddress(), accountAmount);
    }

    private void transferTokenToMainAccount(RefillRequestAddressDto dto, String tokenName, String tokenBchName) {
        JSONArray tokensBalances = tronNodeService.getAccount(dto.getPubKey()).getJSONArray("assetV2");
        long balance = StreamSupport.stream(tokensBalances.spliterator(), true)
                                    .map(JSONObject.class::cast)
                                    .filter(p -> p.getString("key").equals(tokenBchName))
                                    .findFirst()
                                    .map(p -> p.getLong("value"))
                                    .orElseThrow(() -> new RuntimeException("token balance not found"));
        log.debug("balance {} {} {}", dto.getAddress(), balance, tokenBchName);
        easyTransferAssetByPrivate(dto.getPrivKey(), MAIN_ADDRESS_HEX, balance, tokenName);
    }

    @Override
    public boolean checkIsTransactionConfirmed(String txHash) {
        JSONObject rawResponse = tronNodeService.getTransaction(txHash);
        return rawResponse.getBoolean("confirmed");
    }

    @Override
    public void processTransaction(TronReceivedTransactionDto p) {
        processTransaction(p.getId(), p.getAddressBase58(), p.getHash(), p.getAmount(), p.getMerchantId(), p.getCurrencyId());
    }

    @Override
    public void processTransaction(int id, String address, String hash, String amount, Integer merchantId, Integer currencyId) {
        Map<String, String> map = new HashMap<>();
        map.put("address", address);
        map.put("hash", hash);
        map.put("amount", amount);
        map.put("id", String.valueOf(id));
        map.put("currency", currencyId.toString());
        map.put("merchant", merchantId.toString());
        try {
            tronService.processPayment(map);
            refillService.updateAddressNeedTransfer(address, merchantId, currencyId, true);
        } catch (RefillRequestAppropriateNotFoundException e) {
            log.error("request not found {}", address);
        }
    }

    private void easyTransferByPrivate(String pk, String addressTo, long amount) {
        Preconditions.checkArgument(amount > 0, "invalid amount " + amount);
        TronTransferDto tronTransferDto = new TronTransferDto(pk, addressTo, amount);
        JSONObject object = tronNodeService.transferFunds(tronTransferDto);
        boolean result = object.getJSONObject("result").getBoolean("result");
        if (!result) {
            throw new RuntimeException("error transfer to main account");
        }
    }

    private void easyTransferAssetByPrivate(String pk, String addressTo, long amount, String tokenName) {
        log.debug("transfer token {} to main account {}", tokenName, addressTo);
        Preconditions.checkArgument(amount > 0, "invalid amount " + amount);
        Preconditions.checkNotNull(tokenName);
        TronTransferDto tronTransferDto = new TronTransferDto(pk, addressTo, amount, tokenName);
        JSONObject object = tronNodeService.transferAsset(tronTransferDto);
        boolean result = object.getJSONObject("result").getBoolean("result");
        if (!result) {
            throw new RuntimeException("error transfer to main account");
        }
    }

    //this method only for TRON trc20 until developers won`t create id for coin
    private void transferFoundsTRC20(String privatKey, String addressTo, String ownerAdress, long amount) {
        log.debug("create transaction to transfer founds {} to main account {}","TRX_TRC20", addressTo);
        Preconditions.checkArgument(amount > 0, "invalid amount " + amount);
        String parametr = "0000000000000000000000"+MAIN_ADDRESS_HEX;
        String vmParametr = "0000000000000000000000000000000000000000000000000000000000000002";
        String fullParametr = parametr + vmParametr;
        String fee = "1000000";
        TronTransferDtoTRC20 tronTransferDto = new TronTransferDtoTRC20(CONTRACT_ADDRESS_HEX,FUNCTION_SELECTOR, fullParametr, fee,
                amount, ownerAdress);
        freezeBalanceForTransaction(ownerAdress);
        JSONObject object = tronNodeService.transferFundsTRC20(tronTransferDto);
        log.info("Send request for transaction for USDT()TRX");
        JSONObject transaction = object.getJSONObject("transaction").put("privateKey", privatKey);
        log.info("Send request for signin transaction for USDT()TRX");
        JSONObject signTransaction = tronNodeService.signTransferFundsTRC20(transaction);
        log.info("Send request for broadcast transaction for USDT()TRX");
        JSONObject completedObject = tronNodeService.broadcastTransferFundsTRC20(signTransaction);
        boolean result = completedObject.getJSONObject("result").getBoolean("result");
        if (!result) {
            throw new RuntimeException("error transfer to main account");
        }
    }


    private void freezeBalanceForTransaction(String ownerAccount){
        log.debug("freeze TRX for smart contract for account ", ownerAccount);
        Integer amount = 100000;
        String resource = "ENERGY";
        Integer freezeDuration = 3;
        TronFreezeBalance tronFreezeBalance = new TronFreezeBalance(ownerAccount, amount, freezeDuration, resource, ownerAccount);
        JSONObject freezeBalance = tronNodeService.freezeBalance(tronFreezeBalance);
        log.info("Send request for freeze trx for trigerSmartContract");
        boolean result = freezeBalance.getJSONObject("result").getBoolean("result");
        if (!result) {
            throw new RuntimeException("error freezing trx");
        }
    }
}
