package me.exrates.service.qiwi;

import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.exception.DuplicatedMerchantTransactionIdOrAttemptToRewriteException;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.model.dto.qiwi.response.QiwiResponseTransaction;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Log4j2(topic = "Qiwi")
@Service
@PropertySource("classpath:/merchants/qiwi.properties")
public class QiwiServiceImpl implements QiwiService {

    private final static String MERCHANT_NAME = "QIWI";
    private final static String CURRENCY_NAME = "RUB";

    @Autowired
    private MerchantService merchantService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private RefillService refillService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private QiwiExternalService qiwiExternalService;

    private Merchant merchant;
    private Currency currency;

    @PostConstruct
    public void init() {
        currency = currencyService.findByName(CURRENCY_NAME);
        merchant = merchantService.findByName(MERCHANT_NAME);
    }

    @Value("${qiwi.account.address}")
    private String mainAddress;

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        String destinationTag = qiwiExternalService.generateUniqMemo(request.getUserId());
        String message = messageSource.getMessage("merchants.refill.qiwi",
                new Object[]{mainAddress, destinationTag}, request.getLocale());
        return new HashMap<String, String>() {{
            put("address",  destinationTag);
            put("message", message);
            put("qr", mainAddress);
        }};
    }

    @Override
    public String getMainAddress(){
        return mainAddress;
    }

    @Synchronized
    @Override
    public void onTransactionReceive(QiwiResponseTransaction transaction, String amount, String currencyName, String merchant){
        log.info("*** Qiwi *** Income transaction {} ", transaction.getNote() + " " + amount);
        if (checkTransactionForDuplicate(transaction)) {
                log.warn("*** Qiwi *** transaction {} already accepted", transaction.get_id());
                return;
        }
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("hash", transaction.get_id());
        String memo = transaction.getNote().substring(transaction.getNote().indexOf(":")+1);
        if(memo == null) {
            log.warn("*** Qiwi *** Memo is null");
            return;
        }
        paramsMap.put("currency", currencyName);
        paramsMap.put("merchant", merchant);
        paramsMap.put("address", memo);
        paramsMap.put("amount", amount);
        try {
            this.processPayment(paramsMap);
        } catch (RefillRequestAppropriateNotFoundException e) {
            log.error("*** Qiwi *** refill address not found {}", transaction);
        }
    }

    private boolean checkTransactionForDuplicate(QiwiResponseTransaction transaction) {
        return StringUtils.isEmpty(transaction.getNote()) || StringUtils.isEmpty(transaction.get_id()) || refillService.getRequestIdByMerchantIdAndCurrencyIdAndHash(merchant.getId(), currency.getId(),
                transaction.get_id()).isPresent();
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        String address = params.get("address");
        String hash = params.get("hash");
        Currency currency = currencyService.findByName(params.get("currency"));
        Merchant merchant = merchantService.findByName(MERCHANT_NAME);
        BigDecimal fullAmount = new BigDecimal(params.get("amount"));
        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .address(address)
                .merchantId(merchant.getId())
                .currencyId(currency.getId())
                .amount(fullAmount)
                .merchantTransactionId(hash)
                .toMainAccountTransferringConfirmNeeded(this.toMainAccountTransferringConfirmNeeded())
                .build();
        try {
            refillService.autoAcceptRefillRequest(requestAcceptDto);
        } catch (RefillRequestAppropriateNotFoundException e) {
            log.debug("RefillRequestNotFountException: " + params);
            Integer requestId = refillService.createRefillRequestByFact(requestAcceptDto);
            requestAcceptDto.setRequestId(requestId);
            refillService.autoAcceptRefillRequest(requestAcceptDto);
        }
    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        throw new RuntimeException("Not implemented for qiwi.");
    }

    @Override
    public boolean isValidDestinationAddress(String address) {
        return !address.equals(mainAddress);
    }
}
