package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.model.dto.merchants.enfins.EnfinsResponseDto;
import me.exrates.model.dto.merchants.enfins.EnfinsResponsePaymentDto;
import me.exrates.model.dto.merchants.enfins.EnfinsResponsePaymentRefillDto;
import me.exrates.service.CurrencyService;
import me.exrates.service.EnfinsMerchantService;
import me.exrates.service.GtagService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.WithdrawService;
import me.exrates.service.exception.MerchantInternalException;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.exception.RefillRequestRevokeException;
import me.exrates.service.exception.WithdrawRequestPostException;
import me.exrates.service.exception.invoice.MerchantException;
import me.exrates.service.http.EnfinsHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
@Log4j2(topic = "enfins_log")
@PropertySource({"classpath:/merchants/enfins.properties", "classpath:/angular.properties"})
public class EnfinsMerchantServiceImpl implements EnfinsMerchantService {

    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    private final String baseUrl;
    private final String ident;
    private final String secretKey;
    private final String serverHost;
    private final String frontHost;
    private final String refillDescription;
    private final String payOutDescription;
    private final EnfinsHttpClient enfinsHttpClient;
    private final MerchantService merchantService;
    private final RefillService refillService;
    private final GtagService gtagService;
    private final boolean validateIncomePayment;
    private final CurrencyService currencyService;
    private final WithdrawService withdrawService;

    @Autowired
    public EnfinsMerchantServiceImpl(@Value("${base_url_request}") String baseUrl,
                                     @Value("${ident}") String ident,
                                     @Value("${secret_key}") String secretKey,
                                     @Value("${server-host}") String serverHost,
                                     @Value("${front-host}") String frontHost,
                                     @Value("${refill_description}") String refillDescription,
                                     @Value("${payout_description}") String payOutDescription,
                                     EnfinsHttpClient enfinsHttpClient,
                                     MerchantService merchantService,
                                     RefillService refillService,
                                     GtagService gtagService,
                                     @Value("${validate_income_payment}") boolean validateIncomePayment,
                                     CurrencyService currencyService,
                                     WithdrawService withdrawService) {
        this.baseUrl = baseUrl;
        this.ident = ident;
        this.secretKey = secretKey;
        this.serverHost = serverHost;
        this.frontHost = frontHost;
        this.refillDescription = refillDescription;
        this.payOutDescription = payOutDescription;
        this.enfinsHttpClient = enfinsHttpClient;
        this.merchantService = merchantService;
        this.refillService = refillService;
        this.gtagService = gtagService;
        this.validateIncomePayment = validateIncomePayment;
        this.currencyService = currencyService;
        this.withdrawService = withdrawService;
    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        log.info("Starting refill {}", request);
        BigDecimal sum = request.getAmount();
        BigDecimal amountToPay = sum.setScale(2, BigDecimal.ROUND_HALF_UP);
        String desc = refillDescription + request.getId();
        URIBuilder uriBuilder = new URIBuilder()
                .addParameter("ident", ident)
                .addParameter("currency", request.getCurrencyName())
                .addParameter("amount", amountToPay.toPlainString())
                .addParameter("m_order", String.valueOf(request.getId()))
                .addParameter("description", desc);

        String uri = removeFirstChar(uriBuilder.toString());
        log.info("URI before encore {}", uri);
        String sign = "";
        try {
            sign = encode(uri, secretKey);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Error encode sign", e);
            throw new MerchantInternalException("Error create sign " + e.getMessage());
        }
        log.info("Sign encoded \n {} \n -> {}", uri, sign);
        uri += "&sign=" + sign;
        String url = baseUrl + "create_bill?" + uri;
        EnfinsResponseDto<EnfinsResponsePaymentRefillDto> response = enfinsHttpClient.createRefillRequest(url);

        if (response.isResult() && response.getError() == null) {
            request.setMerchantTransactionId(response.getData().getBillId());
            request.setMerchantRequestSign(sign);
            Properties properties = new Properties();
            log.info("Finish refill url {}", response.getData().getUrl());
            return generateFullUrlMap(response.getData().getUrl(), "GET", properties);
        } else {
            log.error("NOT SUCCESS MERCHANT OPERATION: {}", response.getError().getMessage());
            throw new WithdrawRequestPostException("NOT SUCCESS MERCHANT OPERATION, ERROR CODE "
                    + response.getError().getCode());
        }
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        log.info("Starting process payment {}", params);
        if (validateIncomePayment) {
            if (!validatePayment(params)) {
                log.error("Not valid payment {}", params);
                throw new MerchantInternalException("NOT VALID REQUEST");
            }
        }
        String status = params.get("status");
        if (!status.equalsIgnoreCase("paid")) {
            log.error("Not paid payment {}", params);
            throw new RefillRequestRevokeException("NOT PAID REQUEST");
        }

        Merchant merchant = merchantService.findByName("Enfins");
        int requestId = Integer.parseInt(params.get("m_order"));
        BigDecimal amount = new BigDecimal(params.get("amount"));
        String sign = params.get("sign");
        String remark = "Payment method " + params.get("p_method") + ", payment account " + params.get("p_account");
        String currencyName = params.get("currency");
        Currency currency = currencyService.findByName(currencyName);

        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .requestId(requestId)
                .currencyId(currency.getId())
                .merchantId(merchant.getId())
                .amount(amount)
                .address(StringUtils.EMPTY)
                .toMainAccountTransferringConfirmNeeded(this.toMainAccountTransferringConfirmNeeded())
                .merchantTransactionId(sign)
                .remark(remark)
                .build();

        log.info("processPayment() requestAcceptDto {}", requestAcceptDto);
        refillService.acceptRefillRequest(requestAcceptDto);

        final String gaTag = refillService.getUserGAByRequestId(requestId);
        log.info("Process of sending data to Google Analytics...");
        gtagService.sendGtagEvents(params.get("amount"), currencyName, gaTag);
        log.info("Finished process payment");
    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        log.info("Starting withdraw by CoinPay {}", withdrawMerchantOperationDto);
        String desc = payOutDescription + withdrawMerchantOperationDto.getId();

        URIBuilder uriBuilder = new URIBuilder()
                .addParameter("ident", ident)
                .addParameter("currency", withdrawMerchantOperationDto.getCurrency())
                .addParameter("amount", withdrawMerchantOperationDto.getAmount())
                .addParameter("card_number", withdrawMerchantOperationDto.getAccountTo())
                .addParameter("m_order", withdrawMerchantOperationDto.getId())
                .addParameter("description", desc);

        String uri = removeFirstChar(uriBuilder.toString());
        log.info("withdraw() rawSign before encore {}", uri);
        String sign = "";
        try {
            sign = encode(uri, secretKey);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Error encode sign", e);
            throw new MerchantInternalException("Error create sign " + e.getMessage());
        }
        log.info("withdraw() sign encode {}", sign);
        uri += "&sign=" + sign;
        String url = baseUrl + "payout_card?" + uri;
        log.info("withdraw() final url {}", url);
        EnfinsResponseDto<EnfinsResponsePaymentDto> response = enfinsHttpClient.createPayOut(url);
        if (response.isResult() && response.getError() == null) {
            withdrawService.finalizePostWithdrawalRequest(Integer.parseInt(withdrawMerchantOperationDto.getId()));
            Map<String, String> result = new HashMap<>();
            result.put("hash", String.valueOf(response.getData().getBillId()));
            result.put("params", StringUtils.EMPTY);
            return result;
        } else {
            log.error("NOT SUCCESS MERCHANT OPERATION: {}", response.getError().getMessage());
            throw new MerchantException("NOT SUCCESS MERCHANT OPERATION, ERROR CODE " + response.getError().getCode());
        }
    }

    @Override
    public boolean isValidDestinationAddress(String address) {
        return false;
    }

    private boolean validatePayment(Map<String, String> params) {
        URIBuilder checkString = new URIBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!entry.getKey().equalsIgnoreCase("sign")) {
                checkString.addParameter(entry.getKey(), entry.getValue());
            }
        }
        String toEncode = removeFirstChar(checkString.toString());
        String encodeString = "";
        try {
            encodeString = encode(toEncode, secretKey);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("validatePayment() error", e);
            throw new MerchantInternalException("Error create sign " + e.getMessage());
        }
        String signRequest = params.get("sign");
        return encodeString.equalsIgnoreCase(signRequest);
    }

    private String encode(String data, String key)
            throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        mac.init(signingKey);
        return toHexString(mac.doFinal(data.getBytes()));
    }

    private String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

    private String removeFirstChar(String str) {
        return str.substring(1);
    }
}
