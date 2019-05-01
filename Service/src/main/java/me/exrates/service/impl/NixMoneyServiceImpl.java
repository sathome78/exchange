package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.RefillRequestDao;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.RefillRequestFlatDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.AlgorithmService;
import me.exrates.service.CurrencyService;
import me.exrates.service.GtagService;
import me.exrates.service.MerchantService;
import me.exrates.service.NixMoneyService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.NotImplimentedMethod;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.exception.RefillRequestIdNeededException;
import me.exrates.service.exception.RefillRequestNotFoundException;
import me.exrates.service.util.WithdrawUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;

@Service
@PropertySource("classpath:/merchants/nixmoney.properties")
@Conditional(MonolitConditional.class)
@Log4j2(topic = "nixmoney_log")
public class NixMoneyServiceImpl implements NixMoneyService {

    private @Value("${nixmoney.url}")
    String url;
    private @Value("${nixmoney.payeeAccountUSD}")
    String payeeAccountUSD;
    private @Value("${nixmoney.payeeAccountEUR}")
    String payeeAccountEUR;
    private @Value("${nixmoney.payeeName}")
    String payeeName;
    private @Value("${nixmoney.payeePassword}")
    String payeePassword;
    private @Value("${nixmoney.paymentUrl}")
    String paymentUrl;
    private @Value("${nixmoney.noPaymentUrl}")
    String noPaymentUrl;
    private @Value("${nixmoney.statustUrl}")
    String statustUrl;


    private static final Logger logger = LogManager.getLogger(NixMoneyServiceImpl.class);

    @Autowired
    private AlgorithmService algorithmService;
    @Autowired
    private RefillRequestDao refillRequestDao;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private RefillService refillService;
    @Autowired
    private WithdrawUtils withdrawUtils;
    @Autowired
    private GtagService gtagService;

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) {
        throw new NotImplimentedMethod("for " + withdrawMerchantOperationDto);
    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        Integer requestId = request.getId();
        if (requestId == null) {
            throw new RefillRequestIdNeededException(request.toString());
        }
        BigDecimal sum = request.getAmount();
        String currency = request.getCurrencyName();
        BigDecimal amountToPay = sum.setScale(2, BigDecimal.ROUND_HALF_UP);
        /**/
        Properties properties = new Properties() {{
            if (currency.equals("USD")) {
                put("PAYEE_ACCOUNT", payeeAccountUSD);
            }
            if (currency.equals("EUR")) {
                put("PAYEE_ACCOUNT", payeeAccountEUR);
            }
            put("PAYMENT_ID", requestId);
            put("PAYEE_NAME", payeeName);
            put("PAYMENT_AMOUNT", amountToPay);
            put("PAYMENT_URL", paymentUrl);
            put("NOPAYMENT_URL", noPaymentUrl);
            put("BAGGAGE_FIELDS", "PAYEE_ACCOUNT PAYMENT_AMOUNT PAYMENT_ID");
            put("STATUS_URL", statustUrl);
        }};
        /**/
        return generateFullUrlMap(url, "POST", properties);
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        try {
            Integer requestId = Integer.valueOf(params.get("PAYMENT_ID"));
            String merchantTransactionId = params.get("PAYMENT_BATCH_NUM");
            Currency currency = currencyService.findByName(params.get("PAYMENT_UNITS"));
            Merchant merchant = merchantService.findByName("Nix Money");
            BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(params.get("PAYMENT_AMOUNT"))).setScale(9);

            RefillRequestFlatDto refillRequest = refillRequestDao.getFlatByIdAndBlock(requestId)
                    .orElseThrow(() -> new RefillRequestNotFoundException(String.format("refill request id: %s", requestId)));

            String passwordMD5 = algorithmService.computeMD5Hash(payeePassword).toUpperCase();
            ;
            String V2_HASH = algorithmService.computeMD5Hash(params.get("PAYMENT_ID") + ":" + params.get("PAYEE_ACCOUNT")
                    + ":" + params.get("PAYMENT_AMOUNT") + ":" + params.get("PAYMENT_UNITS") + ":" + params.get("PAYMENT_BATCH_NUM")
                    + ":" + params.get("PAYER_ACCOUNT") + ":" + passwordMD5 + ":" + params.get("TIMESTAMPGMT")).toUpperCase();

            if (V2_HASH.equals(params.get("V2_HASH")) && refillRequest.getAmount().equals(amount)) {
                RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                        .requestId(requestId)
                        .merchantId(merchant.getId())
                        .currencyId(currency.getId())
                        .amount(amount)
                        .merchantTransactionId(merchantTransactionId)
                        .toMainAccountTransferringConfirmNeeded(this.toMainAccountTransferringConfirmNeeded())
                        .build();

                refillService.autoAcceptRefillRequest(requestAcceptDto);

                final String gaTag = refillService.getUserGAByRequestId(requestId);

                logger.debug("Process of sending data to Google Analytics...");
                gtagService.sendGtagEvents(amount.toString(), currency.getName(), gaTag);
            }
        } catch (Throwable e){
            log.error(ExceptionUtils.getStackTrace(e));
            throw e;
        }
    }

    @Override
    public boolean isValidDestinationAddress(String address) {

        return withdrawUtils.isValidDestinationAddress(address);
    }

}
