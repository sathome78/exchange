package me.exrates.service.impl;

import me.exrates.dao.RefillRequestDao;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.InterkassaActionUrlDto;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.RefillRequestFlatDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.AlgorithmService;
import me.exrates.service.CurrencyService;
import me.exrates.service.GtagService;
import me.exrates.service.InterkassaService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.InterKassaMerchantException;
import me.exrates.service.exception.InterKassaMerchantNotFoundException;
import me.exrates.service.exception.NotImplimentedMethod;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.exception.RefillRequestIdNeededException;
import me.exrates.service.exception.RefillRequestNotFoundException;
import me.exrates.service.util.WithdrawUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import static java.util.Objects.isNull;

@Service
@PropertySource("classpath:/merchants/interkassa.properties")
public class InterkassaServiceImpl implements InterkassaService {

    private static final Logger logger = LogManager.getLogger(InterkassaServiceImpl.class);

    private static final String POST = "post";

    @Value("${interkassa.url}")
    private String url;
    @Value("${interkassa.checkoutId}")
    private String checkoutId;
    @Value("${interkassa.statusUrl}")
    private String statustUrl;
    @Value("${interkassa.successUrl}")
    private String successtUrl;
    @Value("${interkassa.secretKey}")
    private String secretKey;
    @Value("${interkassa.secret.url}")
    private String interkassaSecretUrl;
    @Value("${interkassa.username}")
    private String interkassaUsername;
    @Value("${interkassa.password}")
    private String interkassaPassword;

    @Autowired
    private AlgorithmService algorithmService;
    @Autowired
    private RefillService refillService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private RefillRequestDao refillRequestDao;
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
        if (isNull(requestId)) {
            throw new RefillRequestIdNeededException(request.toString());
        }
        final String currency = request.getCurrencyName();
        final BigDecimal amountToPay = request.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP);

        final String interkassaId = getInterkassaMerchantId(request);

        Map<String, String> map = new TreeMap<>();
        map.put("ik_am", String.valueOf(amountToPay));
        map.put("ik_co_id", checkoutId);
        map.put("ik_cur", currency);
        map.put("ik_desc", "Exrates input");
        map.put("ik_ia_m", POST);
        map.put("ik_ia_u", statustUrl);
        map.put("ik_pm_no", String.valueOf(requestId));
        map.put("ik_pnd_m", POST);
        map.put("ik_pnd_u", statustUrl);
        map.put("ik_suc_u", successtUrl);
        map.put("ik_suc_m", POST);
        map.put("ik_int", "json");
        map.put("ik_act", "process");
        map.put("ik_pw_via", interkassaId);

        map.put("ik_sign", getSignature(map));

        final InterkassaActionUrlDto actionUrlDto = getActionUrlDto(map);

        Properties properties = new Properties();
        properties.putAll(actionUrlDto.getParameters());

        return generateFullUrlMap(actionUrlDto.getActionURL(), actionUrlDto.getMethod(), properties);
    }

    private String getInterkassaMerchantId(RefillRequestCreateDto request) {
        final String childMerchant = request.getChildMerchant();
        final String currencyName = request.getCurrencyName();

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(interkassaUsername, interkassaPassword));

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(interkassaSecretUrl + checkoutId, String.class);
        if (responseEntity.getStatusCodeValue() != 200) {
            throw new InterKassaMerchantException(
                    String.format("Attention! Problem with interkassa: %s (%d)",
                            responseEntity.getStatusCode().getReasonPhrase(),
                            responseEntity.getStatusCodeValue()));
        }
        JSONObject dataObject = new JSONObject(responseEntity.getBody())
                .getJSONObject("data");

        Iterator<String> keys = dataObject.keys();

        while (keys.hasNext()) {
            final String interkassaMerchantId = keys.next();

            JSONObject interkassaMerchantObject = dataObject.getJSONObject(interkassaMerchantId);

            if (interkassaMerchantObject.getString("ser").equalsIgnoreCase(childMerchant)
                    && interkassaMerchantObject.getString("curAls").equalsIgnoreCase(currencyName)) {
                return interkassaMerchantId;
            }
        }
        throw new InterKassaMerchantNotFoundException(
                String.format("Attention! Currency %s is not available for merchant %s",
                        currencyName,
                        childMerchant));
    }

    private InterkassaActionUrlDto getActionUrlDto(Map<String, String> map) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> headersMap = new LinkedMultiValueMap<>();
        map.forEach(headersMap::add);

        HttpEntity<MultiValueMap<String, String>> requestBody = new HttpEntity<>(headersMap, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, requestBody, String.class);
        if (responseEntity.getStatusCodeValue() != 200) {
            throw new InterKassaMerchantException(
                    String.format("Attention! Problem with interkassa: %s (%d)",
                            responseEntity.getStatusCode().getReasonPhrase(),
                            responseEntity.getStatusCodeValue()));
        }
        JSONObject paymentFormObject = new JSONObject(responseEntity.getBody())
                .getJSONObject("resultData")
                .getJSONObject("paymentForm");
        String actionUrl = paymentFormObject.getString("action").replace("\\", "");
        String method = paymentFormObject.getString("method").toUpperCase();
        JSONObject parametersObject = paymentFormObject.getJSONObject("parameters");

        Iterator<String> keys = parametersObject.keys();

        Map<String, Object> parameters = new HashMap<>();
        while (keys.hasNext()) {
            final String key = keys.next();

            Object value = parametersObject.get(key);

            parameters.put(key, value);
        }
        return InterkassaActionUrlDto.builder()
                .actionURL(actionUrl)
                .method(method)
                .parameters(parameters)
                .build();
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        Integer requestId = Integer.valueOf(params.get("ik_pm_no"));
        String merchantTransactionId = params.get("ik_trn_id");
        Currency currency = currencyService.findByName(params.get("ik_cur"));
        Merchant merchant = merchantService.findByName("Interkassa");
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(params.get("ik_am"))).setScale(9);
        String signature = params.get("ik_sign");

        params.remove("ik_sign");

        TreeMap<String, String> sortedParams = new TreeMap<>(params);

        String checkSignature = getSignature(sortedParams);

        RefillRequestFlatDto refillRequest = refillRequestDao.getFlatByIdAndBlock(requestId)
                .orElseThrow(() -> new RefillRequestNotFoundException(String.format("refill request id: %s", requestId)));
        if (checkSignature.equals(signature)
                && params.get("ik_co_id").equals(checkoutId)
                && params.get("ik_inv_st").equals("success")
                && refillRequest.getAmount().equals(amount)) {
            RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                    .requestId(requestId)
                    .merchantId(merchant.getId())
                    .currencyId(currency.getId())
                    .amount(amount)
                    .merchantTransactionId(merchantTransactionId)
                    .toMainAccountTransferringConfirmNeeded(this.toMainAccountTransferringConfirmNeeded())
                    .build();

            refillService.autoAcceptRefillRequest(requestAcceptDto);

            final String username = refillService.getUsernameByRequestId(requestId);

            logger.debug("Process of sending data to Google Analytics...");
            gtagService.sendGtagEvents(amount.toString(), currency.getName(), username);
        }
    }

    private String getSignature(final Map<String, String> params) {
        ArrayList<String> listValues = new ArrayList<>(params.values());

        listValues.add(secretKey);
        String stringValues = StringUtils.join(listValues, ":");

        byte[] signMD5 = algorithmService.computeMD5Byte(stringValues);

        return Base64.getEncoder().encodeToString(signMD5);
    }

    @Override
    public boolean isValidDestinationAddress(String address) {
        return withdrawUtils.isValidDestinationAddress(address);
    }
}