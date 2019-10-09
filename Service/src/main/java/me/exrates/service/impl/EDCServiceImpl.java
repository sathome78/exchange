package me.exrates.service.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.CurrencyService;
import me.exrates.service.EDCService;
import me.exrates.service.EDCServiceNode;
import me.exrates.service.GtagService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.MerchantInternalException;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.exception.RefillRequestFakePaymentReceivedException;
import me.exrates.service.exception.RefillRequestMerchantException;
import me.exrates.service.util.WithdrawUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import sun.net.www.http.HttpClient;
import sun.security.util.SecurityConstants;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static me.exrates.service.util.RequestUtil.getSSlFactory;

@Log4j2(topic = "edc_log")
@Service
@PropertySource({"classpath:/merchants/edcmerchant.properties"})
@Conditional(MonolitConditional.class)
public class EDCServiceImpl implements EDCService {

    private @Value("${edcmerchant.token}")
    String token;
    private @Value("${edcmerchant.main_account}")
    String main_account;
    private @Value("${edcmerchant.hook}")
    String hook;
    private @Value("${edcmerchant.history}")
    String history;
    private @Value("${edcmerchant.new_account}")
    String urlCreateNewAccount;

    private final MessageSource messageSource;
    private final RefillService refillService;
    private final MerchantService merchantService;
    private final CurrencyService currencyService;
    private final WithdrawUtils withdrawUtils;
    private final EDCServiceNode edcServiceNode;
    private final GtagService gtagService;
    private RestTemplate restTemplate;

    @Autowired
    public EDCServiceImpl(MessageSource messageSource,
                          RefillService refillService,
                          MerchantService merchantService,
                          CurrencyService currencyService,
                          WithdrawUtils withdrawUtils,
                          EDCServiceNode edcServiceNode,
                          GtagService gtagService) {
        this.messageSource = messageSource;
        this.refillService = refillService;
        this.merchantService = merchantService;
        this.currencyService = currencyService;
        this.withdrawUtils = withdrawUtils;
        this.edcServiceNode = edcServiceNode;
        this.gtagService = gtagService;
        try {
            this.restTemplate = restTemplate();
        } catch (Exception e) {
            log.error("Error creating edc rest-client");
        }
    }


    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        log.info("Withdraw EDC: " + withdrawMerchantOperationDto.toString());
        edcServiceNode.transferFromMainAccount(
                withdrawMerchantOperationDto.getAccountTo(),
                withdrawMerchantOperationDto.getAmount());
        return new HashMap<>();
    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        String address = getAddress();
        log.info("EDC. Generate new refill address: {}", address);

        String message = messageSource.getMessage("merchants.refill.edr", new Object[]{address}, request.getLocale());

        return new HashMap<String, String>() {{
            put("address", address);
            put("message", message);
            put("qr", address);
        }};
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        checkTransactionByHistory(params);
        String merchantTransactionId = params.get("id");
        String address = params.get("address");
        String hash = params.get("hash");
        Currency currency = currencyService.findByName("EDC");
        Merchant merchant = merchantService.findByName("EDC");
        BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(params.get("amount")));

        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .address(address)
                .merchantId(merchant.getId())
                .currencyId(currency.getId())
                .amount(amount)
                .merchantTransactionId(StringUtils.isEmpty(merchantTransactionId) ? hash : merchantTransactionId)
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

    private void checkTransactionByHistory(Map<String, String> params) {
        if (StringUtils.isEmpty(history)) {
            return;
        }
        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(history + token + "/" + params.get("address"))
                .build();
        final String returnResponse;

        try {
            returnResponse = client
                    .newCall(request)
                    .execute()
                    .body()
                    .string();
        } catch (IOException e) {
            log.error("EDC coin. Error: {}" + e);
            throw new MerchantInternalException(e);
        }

        JsonParser parser = new JsonParser();
        try {
            JsonArray jsonArray = parser.parse(returnResponse).getAsJsonArray();
            for (JsonElement element : jsonArray) {
                if (element.getAsJsonObject().get("id").getAsString().equals(params.get("id"))) {
                    if (element.getAsJsonObject().get("amount").getAsString().equals(params.get("amount"))) {
                        if (((JsonObject) element).getAsJsonObject("asset").get("symbol").getAsString().equals("EDC")) {
                            return;
                        }
                    }
                }
            }
        } catch (IllegalStateException e) {
            if ("Address not found".equals(parser.parse(returnResponse).getAsJsonObject().get("message").getAsString())) {
                log.info("EDC coin. Address not found. Fake transaction error: {}", e);
                throw new RefillRequestFakePaymentReceivedException(params.toString());
            } else {
                log.error("EDC coin. Error in parse: {}", e);
                throw new RefillRequestMerchantException(params.toString());
            }
        }
        throw new RefillRequestFakePaymentReceivedException(params.toString());
    }

    private String getAddress() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");

        JSONObject request = new JSONObject();
        request.put("account", main_account);
        request.put("hook", hook);

        HttpEntity<String> entity = new HttpEntity<>(request.toString(), headers);

        log.debug("Url (create new account): {}", urlCreateNewAccount + token);
        log.debug("Headers (create new account): {}", headers);
        log.debug("Request json (create new account): {}", request);

        final ResponseEntity<String> returnResponse;
        try {
            returnResponse = restTemplate.exchange(urlCreateNewAccount + token, HttpMethod.POST, entity, String.class);
            log.debug("Return response (create new account): {}", returnResponse);

            String bodyResponse = returnResponse.getBody();
            log.debug("Body of response (create new account): {}", bodyResponse);

            JsonParser parser = new JsonParser();
            JsonObject object = parser.parse(bodyResponse).getAsJsonObject();

            log.debug("JsonObject (create new account): {}", object);
            return object.get("address").getAsString();

        } catch (Exception e) {
            log.error("EDC coin. Error in generate new address for refill: {}", e);
            throw new MerchantInternalException("Unfortunately, the operation is not available at the moment, please try again later!");
        }
    }

    @Override
    public boolean isValidDestinationAddress(String address) {

        return withdrawUtils.isValidDestinationAddress(address);
    }


    public RestTemplate restTemplate() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        HttpClientBuilder b = HttpClientBuilder.create();
        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (chain, authType) -> true).build();
        b.setSslcontext(sslContext);
        HostnameVerifier hostnameVerifier = SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", sslSocketFactory)
                .build();
        PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager( socketFactoryRegistry);
        b.setConnectionManager( connMgr);
        CloseableHttpClient client = b.build();
        RestTemplate restTemplate = new RestTemplate();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(client);
        requestFactory.setConnectionRequestTimeout(20000);
        requestFactory.setReadTimeout(25000);
        restTemplate.setRequestFactory(requestFactory);
        return restTemplate;
    }

}
