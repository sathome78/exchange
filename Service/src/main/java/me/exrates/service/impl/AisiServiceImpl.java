package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.impl.AisiCurrencyServiceImpl.Transaction;
import me.exrates.service.*;
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

@Log4j2 (topic = "aisi_log")
@Service
public class AisiServiceImpl implements AisiService {

    public final static String MERCHANT_NAME = "AISI";
    public final static String STATUS_OK = "1";

    @Autowired
    private MerchantService merchantService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private RefillService refillService;
    @Autowired
    private WithdrawUtils withdrawUtils;
    @Autowired
    private final AisiCurrencyService aisiCurrencyService;

    @Autowired
    private MessageSource messageSource;

    private Merchant merchant;
    private Currency currency;

    @PostConstruct
    public void init() {
        currency = currencyService.findByName(MERCHANT_NAME);
        merchant = merchantService.findByName(MERCHANT_NAME);
    }

    public AisiServiceImpl(AisiCurrencyService aisiCurrencyService) {
        this.aisiCurrencyService = aisiCurrencyService;
    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        String address = aisiCurrencyService.generateNewAddress();
        String message = messageSource.getMessage("merchants.refill.aisi",
                new Object[] {address}, request.getLocale());
        return new HashMap<String, String>(){{
            put("address", address);
            put("message", message);
            put("qr", address);
        }};
    }

    public void checkAddressForTransactionReceive(List<String> listOfAddress, Transaction transaction){
      try {
          if (listOfAddress.contains(transaction.getRecieverAddress())) {
              onTransactionReceive(transaction);
          }
      } catch (Exception e){
          log.error(e + " error in AisiServiceImpl.checkAddressForTransactionReceive()");
      }
    }

    public void onTransactionReceive(Transaction transaction){
        log.info("*** Aisi *** Income transaction {} " + transaction);
        if (checkTransactionForDuplicate(transaction)) {
            log.warn("*** Aisi *** transaction {} already accepted", transaction.getTransaction_id());
            return;
        }
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("hash", transaction.getTransaction_id());
        paramsMap.put("address", transaction.getRecieverAddress());
        paramsMap.put("amount", transaction.getAmount());
        try {
            this.processPayment(paramsMap);
        } catch (RefillRequestAppropriateNotFoundException e) {
            log.error("*** Aisi *** refill address not found {}", transaction);
        }
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        String address = params.get("address");
        String hash = params.get("hash");
        BigDecimal fullAmount = new BigDecimal(params.get("amount"));
        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .address(address)
                .merchantId(merchant.getId())
                .currencyId(currency.getId())
                .amount(fullAmount)
                .merchantTransactionId(hash)
                .toMainAccountTransferringConfirmNeeded(this.toMainAccountTransferringConfirmNeeded())
                .build();

        String tempStatus = aisiCurrencyService.createNewTransaction(address, fullAmount);
        // compares status ok (1) with actual "Result" given by API
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
            log.error("STATUS is not OK = " + tempStatus + ". Error in aisiCurrencyService.createNewTransaction(address, fullAmount)");
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
