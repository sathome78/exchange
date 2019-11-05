package me.exrates.service.syndex;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;


@PropertySource(value = "classpath:/merchants/syndex.properties")
@Log4j2(topic = "syndex")
@Component
public class SyndexClientImpl implements SyndexClient {

    @Value("${syndex_public_key}")
    private String token;
    @Value("${syndex_secret_key}")
    private String secretKey;

    private OkHttpClient client;
    private ObjectMapper objectMapper;

    private final String POST_COUNTRY;
    private final String POST_CURRENCY;
    private final String POST_PAYMENT_SYSTEM;
    private final String POST_CREATE_ORDER;
    private final String POST_CONFIRM_ORDER;
    private final String POST_CANCEL_ORDER;
    private final String POST_OPEN_DISPUTE;
    private final String POST_ORDER_INFO;

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public SyndexClientImpl(@Value("${syndex_public_key}") String token,
                            @Value("${syndex_secret_key}") String secretKey,
                            @Value("${syndex_host}") String host) {
        this.token = token;
        this.secretKey = secretKey;

        POST_COUNTRY = host.concat("/merchant/api/get-country-list");
        POST_CURRENCY = host.concat("/merchant/api/get-currency-list");
        POST_PAYMENT_SYSTEM = host.concat("/merchant/api/get-payment-list");
        POST_CREATE_ORDER = host.concat("/merchant/api/create-refill-order");
        POST_CONFIRM_ORDER = host.concat("/merchant/api/confirm-order");
        POST_CANCEL_ORDER = host.concat("/merchant/api/cancel-order");
        POST_OPEN_DISPUTE = host.concat("/merchant/api/open-dispute");
        POST_ORDER_INFO = host.concat("/merchant/api/info-order");
    }

    @PostConstruct
    private void init() {

        objectMapper = new ObjectMapper();

        client = getUnsafeOkHttpClient()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    String fieldsForSign = getFieldsForSign(original);

                    Request request = original.newBuilder()
                            .header("X-Auth-Token", token)
                            .header("X-Auth-Sign", getSignature(fieldsForSign))
                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                })
                .build();
    }

    @SneakyThrows
    private String getFieldsForSign(Request request) {
        if (request.body().contentType().type().equals(JSON.type())) {
            String body = bodyToString(request);
            log.debug(body);
            Map<String, String> result = objectMapper.readValue(body, new TypeReference<Map<String, String>>() {});
            return result.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(Map.Entry::getValue)
                    .collect(Collectors.joining( "" ));
        }
        throw new RuntimeException("error getting requestid");
    }

    private String bodyToString(final Request request){
        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer);
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "crap, it's didn't work";
        }
    }


    @Override
    public OrderInfo createOrder(CreateOrderRequest orderDto) {
        Request request = new Request.Builder()
                .post(buildRequestBody(orderDto))
                .url(POST_CREATE_ORDER)
                .build();

        try {
            return executeRequest(request, OrderInfo.class);
        } catch (SyndexCallUnknownException e) {
            throw new SyndexCallUnknownException("Error creating sybex order");
        }
    }

    @Override
    public OrderInfo getOrderInfo(long orderId) {
        Request request = new Request.Builder()
                .post(buildRequestBody(new OrderRequest(orderId)))
                .url(POST_ORDER_INFO)
                .build();

        return executeRequest(request, OrderInfo.class);
    }

    @Override
    public String cancelOrder(long orderId) {
        Request request = new Request.Builder()
                .post(buildRequestBody(new OrderRequest(orderId)))
                .url((POST_CANCEL_ORDER))
                .build();

        try {
            return executeRequest(request);
        } catch (SyndexCallUnknownException e) {
            throw new SyndexCallUnknownException("Error cancel order");
        }
    }

    @Override
    public String confirmOrder(long orderId) {
        Request request = new Request.Builder()
                .post(buildRequestBody(new OrderRequest(orderId)))
                .url(POST_CONFIRM_ORDER)
                .build();

        try {
            return executeRequest(request);
        } catch (SyndexCallUnknownException e) {
            throw new SyndexCallUnknownException("Error confirm order");
        }
    }


    @Override
    public String openDispute(long orderId, String comment) {
        Request request = new Request.Builder()
                .post(buildRequestBody(new OpenDisputeRequest(String.valueOf(orderId), comment)))
                .url(POST_OPEN_DISPUTE)
                .build();
        try {
            return executeRequest(request);
        } catch (SyndexCallUnknownException e) {
            throw new SyndexCallUnknownException("Error open dispute exception");
        }
    }

    @Override
    public List<Country> getCountryList() {
        Request request = new Request.Builder()
                .post(buildRequestBody(new BaseRequestEntity()))
                .url(POST_COUNTRY)
                .build();

        return executeRequest(request);
    }

    @Override
    public List<Currency> getCurrencyList() {
        Request request = new Request.Builder()
                .post(buildRequestBody(new BaseRequestEntity()))
                .url(POST_CURRENCY)
                .build();

        return executeRequest(request);
    }

    @Override
    public List<PaymentSystemWrapper> getPaymentSystems(String countryCode) {

        Request request = new Request.Builder()
                .post(buildRequestBody(new GetPaymentSystemRequest(countryCode)))
                .url(POST_PAYMENT_SYSTEM)
                .build();

        return executeRequest(request);
    }

    private <T> T executeRequest(Request request) {
        return executeRequest(request, null);
    }

    private <T> T executeRequest(Request request, Class<T> classType) {
        try {
            Response response = client
                    .newCall(request)
                    .execute();
            return handleResponse(response, classType);

        } catch (SyndexCallException e) {
            log.error(e);
            throw e;
        } catch (Exception e) {
            log.error(e);
            throw new SyndexCallException("Merchant error", e);
        }
    }

    private <T> T handleResponse(Response response, Class<T> classType) throws IOException {
        if (response.isSuccessful()) {

            String body = new String(Objects.requireNonNull(response.body()).bytes(), StandardCharsets.UTF_8);
            log.debug("url {}, response {}", response.request().url().toString(), body);

            BaseResponse<T> baseResponse;
            if (isNull(classType)) {
                baseResponse = objectMapper.readValue(body, new TypeReference<BaseResponse<T>>(){});
            } else {
                JavaType javaType = objectMapper.getTypeFactory().constructParametricType(BaseResponse.class, classType);
                baseResponse = objectMapper.readValue(body, javaType);
            }

            if (baseResponse.isError()) {
                throw new SyndexCallException(baseResponse.getError());
            }
            return baseResponse
                    .getResult();
        } else if (response.code() == 400) {
            throw new SyndexCallException(objectMapper.readValue(Objects.requireNonNull(response.body()).string(), new TypeReference<BaseError<Error>>() {}));
        } else {
            log.debug("error code {}, body {}", response.code(), response.body() != null ? response.body().string() : "");
            throw new SyndexCallUnknownException();
        }
    }

    @SneakyThrows
    private RequestBody buildRequestBody(Object payload) {
        return RequestBody.create(JSON, objectMapper.writeValueAsString(payload));
    }

    private String getSignature(String requestValues) {
        return DigestUtils.sha256Hex(requestValues.concat(secretKey));
    }

    private static OkHttpClient.Builder getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
                                                       String authType) {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
                                                       String authType) {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
