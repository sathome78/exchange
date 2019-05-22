package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.AlgorithmService;
import me.exrates.service.CurrencyService;
import me.exrates.service.GapiCurrencyService;
import me.exrates.service.impl.GapiCurrencyServiceImpl.Transaction;
import me.exrates.service.GapiService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.util.WithdrawUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Log4j2(topic = "gapi_log")
public class GapiServiceImpl implements GapiService {

    public static final String MERCHANT_NAME = "GAPI";
    private static final String STATUS_OK = "ok";

    @Autowired
    private MerchantService merchantService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private RefillService refillService;
    @Autowired
    private WithdrawUtils withdrawUtils;
    @Autowired
    private AlgorithmService algorithmService;

    @Autowired
    private final GapiCurrencyService gapiCurrencyService;

    @Autowired
    private MessageSource messageSource;

    private Merchant merchant;
    private Currency currency;

    @PostConstruct
    public void init() {
        currency = currencyService.findByName(MERCHANT_NAME);
        merchant = merchantService.findByName(MERCHANT_NAME);
    }

    public GapiServiceImpl(GapiCurrencyService gapiCurrencyService) {
        this.gapiCurrencyService = gapiCurrencyService;
    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        List<String> list = gapiCurrencyService.generateNewAddress();
        String address = list.get(0);
        String encodedStr = algorithmService.encodeByKey(list.get(1));
        String message = messageSource.getMessage("merchants.refill.gapi",
                new Object[] {address}, request.getLocale());
        return new HashMap<String, String>(){{
            put("privKey", encodedStr);
            put("address", address);
            put("message", message);
            put("qr", address);
        }};
    }

    @Override
    public void checkAddressForTransactionReceive(List<String> listOfAddress, Transaction transaction) {
        try {
            if (listOfAddress.contains(transaction.getRecieverAddress())) {
                onTransactionReceive(transaction);
            }
        } catch (Exception e){
            log.error(e + " error in GapiServiceImpl.checkAddressForTransactionReceive()");
        }
    }

    @Override
    public void onTransactionReceive(Transaction transaction) {
        log.info("*** Gapi *** Income transaction {} " + transaction);
        if (checkTransactionForDuplicate(transaction)) {
            log.warn("*** Gapi *** transaction {} already accepted", transaction.getTransaction_id());
            return;
        }
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("hash", transaction.getTransaction_id());
        paramsMap.put("address", transaction.getRecieverAddress());
        paramsMap.put("amount", transaction.getAmount());
        try {
            this.processPayment(paramsMap);
        } catch (RefillRequestAppropriateNotFoundException e) {
            log.error("*** Gapi *** refill address not found {}", transaction);
        }
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        String address = params.get("address");
        String amount = params.get("amount");
        String hash = params.get("hash");
        BigDecimal fullAmount = new BigDecimal(amount);
        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .address(address)
                .merchantId(merchant.getId())
                .currencyId(currency.getId())
                .amount(fullAmount)
                .merchantTransactionId(hash)
                .toMainAccountTransferringConfirmNeeded(this.toMainAccountTransferringConfirmNeeded())
                .build();

        String encodedStr = refillService.getPrivKeyByAddress(address);
        String privKey = algorithmService.decodeByKey(encodedStr);
        String tempStatus = gapiCurrencyService.createNewTransaction(privKey, amount);
        if (tempStatus.equals(STATUS_OK)) {
            try {
                refillService.autoAcceptRefillRequest(requestAcceptDto);
            } catch (RefillRequestAppropriateNotFoundException e) {
                log.debug("RefillRequestNotFountException: " + params);
                Integer requestId = refillService.createRefillRequestByFact(requestAcceptDto);
                requestAcceptDto.setRequestId(requestId);
                refillService.autoAcceptRefillRequest(requestAcceptDto);
            }
        } else {
            log.error("STATUS is not OK = " + tempStatus + ". Error in gapiCurrencyService.createNewTransaction(privKey, fullAmount)");
        }
    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        throw new RuntimeException("Not supported");
    }

    @Override
    public boolean isValidDestinationAddress(String address) {
        return withdrawUtils.isValidDestinationAddress(address);
    }

    private boolean checkTransactionForDuplicate(Transaction transaction) {
        return StringUtils.isEmpty(transaction.getTransaction_id()) || StringUtils.isEmpty(transaction.getRecieverAddress()) || refillService.getRequestIdByMerchantIdAndCurrencyIdAndHash(merchant.getId(), currency.getId(),
                transaction.getTransaction_id()).isPresent();
    }
}
