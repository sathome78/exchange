package me.exrates.service.impl;

import me.exrates.dao.RefillRequestDao;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.RefillRequestFlatDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.AlgorithmService;
import me.exrates.service.CurrencyService;
import me.exrates.service.InterkassaService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.*;
import me.exrates.service.util.WithdrawUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

@Service
@PropertySource("classpath:/merchants/interkassa.properties")
public class InterkassaServiceImpl implements InterkassaService {

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
    @Value("${intekassa.secret.url}")
    private String interKassaSecretUrl;
    @Value("${intekassa.username}")
    private String interKassaUsername;
    @Value("${intekassa.password}")
    private String interKassaPassword;

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
    private RestTemplate restTemplate;

    private static final Logger LOG = LogManager.getLogger("merchant");

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

        final String interKassaId = getInterKassaMerchantId(request);
        final Map<String, String> map = new TreeMap<>();

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

        map.put("ik_pw_via", interKassaId);
        map.put("ik_sign", getSignature(map));

        Properties properties = new Properties();
        properties.putAll(map);
        /**/

        return generateFullUrlMap(url, "POST", properties);
    }

    private String getInterKassaMerchantId(final RefillRequestCreateDto request) {
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(interKassaUsername, interKassaPassword));

        ResponseEntity<String> forEntity = restTemplate.getForEntity(interKassaSecretUrl + checkoutId, String.class);

        JSONObject jsonObject = new JSONObject(forEntity.getBody());
        JSONObject dataObject = jsonObject.getJSONObject("data");

        Iterator<String> keys = dataObject.keys();

        while (keys.hasNext()) {
            String interkassaMerchantId = keys.next();

            JSONObject interkassaMerchantObject = dataObject.getJSONObject(interkassaMerchantId);

            if (interkassaMerchantObject.getString("ps").equalsIgnoreCase(request.getChildMerchant()) &&
                    interkassaMerchantObject.getString("curAls").equalsIgnoreCase(request.getCurrencyName())) {
                return interkassaMerchantId;
            }
        }
        throw new InterKassaMerchantNotFound("Unable to find child interkassaid");
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
