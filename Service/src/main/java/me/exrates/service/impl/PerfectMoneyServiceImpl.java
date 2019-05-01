package me.exrates.service.impl;

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
import me.exrates.service.PerfectMoneyService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.NotImplimentedMethod;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.exception.RefillRequestNotFoundException;
import me.exrates.service.util.WithdrawUtils;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Properties;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
@PropertySource("classpath:/merchants/perfectmoney.properties")
@Conditional(MonolitConditional.class)
public class PerfectMoneyServiceImpl implements PerfectMoneyService {

    private @Value("${perfectmoney.url}")
    String url;
    private @Value("${perfectmoney.accountId}")
    String accountId;
    private @Value("${perfectmoney.accountPass}")
    String accountPass;
    private @Value("${perfectmoney.payeeName}")
    String payeeName;
    private @Value("${perfectmoney.paymentSuccess}")
    String paymentSuccess;
    private @Value("${perfectmoney.paymentFailure}")
    String paymentFailure;
    private @Value("${perfectmoney.paymentStatus}")
    String paymentStatus;
    private @Value("${perfectmoney.USDAccount}")
    String usdCompanyAccount;
    private @Value("${perfectmoney.EURAccount}")
    String eurCompanyAccount;
    private @Value("${perfectmoney.alternatePassphrase}")
    String alternatePassphrase;

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(PerfectMoneyServiceImpl.class);

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
        Integer orderId = request.getId();
        BigDecimal sum = request.getAmount();
        String currency = request.getCurrencyName();
        Number amountToPay = "GOLD".equals(currency) ? sum.toBigInteger() : sum.setScale(2, BigDecimal.ROUND_HALF_UP);
        /**/
        Properties properties = new Properties() {
            {
                put("PAYEE_ACCOUNT", currency.equals("USD") ? usdCompanyAccount : eurCompanyAccount);
                put("PAYEE_NAME", payeeName);
                put("PAYMENT_AMOUNT", amountToPay);
                put("PAYMENT_UNITS", currency);
                put("PAYMENT_ID", orderId);
                put("PAYMENT_URL", paymentSuccess);
                put("NOPAYMENT_URL", paymentFailure);
                put("STATUS_URL", paymentStatus);
                put("FORCED_PAYMENT_METHOD", "account");
            }
        };
        /**/
        return generateFullUrlMap(url, "POST", properties);
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        Integer requestId = Integer.valueOf(params.get("PAYMENT_ID"));
        String merchantTransactionId = params.get("PAYMENT_BATCH_NUM");
        Currency currency = params.get("PAYEE_ACCOUNT").equals(usdCompanyAccount) ? currencyService.findByName("USD") : currencyService.findByName("EUR");
        Merchant merchant = merchantService.findByName("Perfect Money");
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(params.get("PAYMENT_AMOUNT"))).setScale(9);

        String hash = computePaymentHash(params);
        RefillRequestFlatDto refillRequest = refillRequestDao.getFlatByIdAndBlock(requestId)
                .orElseThrow(() -> new RefillRequestNotFoundException(String.format("refill request id: %s", requestId)));

        if (params.get("V2_HASH").equals(hash) && refillRequest.getAmount().equals(amount)) {
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
    }

    private String computePaymentHash(Map<String, String> params) {
        final String passpphraseHash = algorithmService.computeMD5Hash(alternatePassphrase).toUpperCase();
        final String hashParams = params.get("PAYMENT_ID") +
                ":" + params.get("PAYEE_ACCOUNT") +
                ":" + params.get("PAYMENT_AMOUNT") +
                ":" + params.get("PAYMENT_UNITS") +
                ":" + params.get("PAYMENT_BATCH_NUM") +
                ":" + params.get("PAYER_ACCOUNT") +
                ":" + passpphraseHash +
                ":" + params.get("TIMESTAMPGMT");
        return algorithmService.computeMD5Hash(hashParams).toUpperCase();
    }

    @Override
    public boolean isValidDestinationAddress(String address) {

        return withdrawUtils.isValidDestinationAddress(address);
    }


}