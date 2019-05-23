package me.exrates.service.usdx;

import lombok.SneakyThrows;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.CurrencyService;
import me.exrates.service.GtagService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.*;
import me.exrates.service.usdx.model.UsdxTransaction;
import me.exrates.service.usdx.model.enums.UsdxWalletAsset;
import me.exrates.service.util.CryptoUtils;
import me.exrates.service.util.WithdrawUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Log4j2(topic = "usdx_log")
@Service
@PropertySource("classpath:/merchants/usdx.properties")
public class LightHouseServiceImpl implements UsdxService {

    private static final String LIGHTHOUSE_CURRENCY_NAME = UsdxWalletAsset.LHT.name();
    private static final int MAX_TAG_DESTINATION_DIGITS = 8;

    @Autowired
    private MerchantService merchantService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private RefillService refillService;
    @Autowired
    private WithdrawUtils withdrawUtils;
    @Autowired
    private GtagService gtagService;

    @Autowired
    private UsdxRestApiService usdxRestApiService;

    private Merchant merchant;
    private Currency currency;
    private static final String DESTINATION_TAG_ERR_MSG = "message.usdx.tagError";

    @PostConstruct
    public void init() {
        currency = currencyService.findByName(LIGHTHOUSE_CURRENCY_NAME);
        merchant = merchantService.findByName(LIGHTHOUSE_CURRENCY_NAME);
    }

    @Transactional
    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        String destinationTag = CryptoUtils.generateUniqDestinationTagForUserForSpecificCurrency(request.getUserId(),
                MAX_TAG_DESTINATION_DIGITS, refillService, currency.getName(), currency.getId(), merchant.getId());

        String message = messageSource.getMessage("merchants.refill.usdx", new Object[]{usdxRestApiService.getAccountName(), destinationTag}, request.getLocale());

        return new HashMap<String, String>() {{
            put("address", destinationTag);
            put("message", message);
            put("qr", usdxRestApiService.getAccountName());
        }};
    }

    @Transactional
    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        throw new RuntimeException("Not implemented");
    }

    @Synchronized
    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        String merchantTransactionId = params.get("transferId");
        String memo = params.get("memo");
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(params.get("amount")));

        if(checkTransactionForDuplicate(merchantTransactionId)){
            log.warn("USDX Wallet transaction with transfer id: {} already accepted", merchantTransactionId);
            throw new RefillRequestAlreadyAcceptedException(String.format("USDX Wallet transaction with transfer id: %s already accepted", merchantTransactionId));
        }
        if(StringUtils.isEmpty(memo)){
            log.warn("USDX Wallet transaction. MEMO is NULL");
            throw new RefillRequestMemoIsNullException(String.format("USDX Wallet transaction with transfer id: %s. MEMO is NULL", merchantTransactionId));
        }

        UsdxTransaction usdxTransaction = usdxRestApiService.getTransactionStatus(merchantTransactionId);

        if(usdxTransaction == null){
            log.warn("USDX Wallet transaction with transfer id {} not exists in transactions history.", merchantTransactionId);
            throw new RefillRequestFakePaymentReceivedException(String.format("USDX Wallet transaction with transfer id {} not exists in transactions history. Params: %s",
                    params.toString()));
        }

        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .address(usdxTransaction.getMemo())
                .merchantId(merchant.getId())
                .currencyId(currency.getId())
                .amount(usdxTransaction.getAmount())
                .merchantTransactionId(usdxTransaction.getTransferId())
                .toMainAccountTransferringConfirmNeeded(this.toMainAccountTransferringConfirmNeeded())
                .build();

        Integer requestId;

        try {
            requestId = refillService.getRequestId(requestAcceptDto);
            requestAcceptDto.setRequestId(requestId);

            refillService.autoAcceptRefillRequest(requestAcceptDto);
        } catch (RefillRequestAppropriateNotFoundException e) {
            log.debug("RefillRequestAppropriateNotFoundException: " + params);
            requestId = refillService.createRefillRequestByFact(requestAcceptDto);
            requestAcceptDto.setRequestId(requestId);

            refillService.autoAcceptRefillRequest(requestAcceptDto);
        }
        final String gaTag = refillService.getUserGAByRequestId(requestId);

        log.debug("Process of sending data to Google Analytics...");
        gtagService.sendGtagEvents(amount.toString(), currency.getName(), gaTag);
    }

    private boolean checkTransactionForDuplicate(String usdxTransactionTransferId) {
        return refillService.getRequestIdByMerchantIdAndCurrencyIdAndHash(merchant.getId(), currency.getId(), usdxTransactionTransferId).isPresent();
    }

    @Override
    public String getMainAddress() {
        return usdxRestApiService.getAccountName();
    }

    /*must bee only unsigned int = Memo.id - unsigned 64-bit number, MAX_SAFE_INTEGER  memo 0 - 9007199254740991*/
    @Override
    public void checkDestinationTag(String destinationTag) {
        if (!(NumberUtils.isDigits(destinationTag) && Long.valueOf(destinationTag) <= 9007199254740991L) || destinationTag.length() > 26) {
            throw new CheckDestinationTagException(DESTINATION_TAG_ERR_MSG, this.additionalWithdrawFieldName());
        }
    }

    @Override
    public boolean isValidDestinationAddress(String address) {
        return withdrawUtils.isValidDestinationAddress(usdxRestApiService.getAccountName(), address);
    }

    @Override
    public Merchant getMerchant(){
        return merchant;
    }

    @Override
    public Currency getCurrency(){
        return currency;
    }

    @Override
    public UsdxRestApiService getUsdxRestApiService(){
        return usdxRestApiService;
    }

    @Override
    public void checkHeaderOnValidForSecurity(String securityHeaderValue, UsdxTransaction usdxTransaction) {
        String timestampFromRequest = securityHeaderValue.substring(securityHeaderValue.indexOf("t=")+2, securityHeaderValue.indexOf(","));
        String usdxTransactionValueForSignature = usdxRestApiService.generateSecurityHeaderValue(timestampFromRequest, usdxRestApiService.getStringJsonUsdxTransaction(usdxTransaction));

        if(!securityHeaderValue.equals(usdxTransactionValueForSignature)){
            log.error("USDX Wallet ERROR with transfer id: {} IS FAKE. Header value: {}", usdxTransaction.getTransferId(), usdxTransactionValueForSignature);
            throw new RefillRequestFakePaymentReceivedException(String.format("USDX Wallet ERROR with transfer id: %s IS FAKE. Header value: %s",
                    usdxTransaction.getTransferId(), usdxTransactionValueForSignature));
        }
    }

    @SneakyThrows
    @Override
    public void createRefillRequestAdmin(Map<String, String> params){
        String merchantTransactionId = params.get("txId");

        UsdxTransaction usdxTransaction = usdxRestApiService.getTransactionStatus(merchantTransactionId);

        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("transferId", usdxTransaction.getTransferId());
        paramsMap.put("memo", usdxTransaction.getMemo());
        paramsMap.put("amount", usdxTransaction.getAmount().toPlainString());

        processPayment(paramsMap);
    }

    @Override
    public UsdxTransaction sendUsdxTransactionToExternalWallet(String password, UsdxTransaction usdxTransaction){
        if(!password.equals(merchantService.getPassMerchantProperties(merchant.getName()).getProperty("wallet.password"))){
            log.info("USDX Wallet. Invalid password. Time to try: {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            throw new IncorrectCoreWalletPasswordException("Invalid password");
        }
        return usdxRestApiService.transferAssetsToUserAccount(usdxTransaction);
    }


}
