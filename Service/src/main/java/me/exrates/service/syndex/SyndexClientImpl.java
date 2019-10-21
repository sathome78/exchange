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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

    /*X-Auth-Sign = sha256 (concated, sorted by keys values + api_secret)*/
    @Value("${x-auth-token}")
    private String token;
    @Value(("${x-auth-sign}"))
    private String secretKey;

    private OkHttpClient client;
    private ObjectMapper objectMapper;

    private static final String POST_COUNTRY = "https://api.syndex.io/merchant/api/get-country-list";
    private static final String POST_CURRENCY = "https://api.syndex.io/merchant/api/get-currency-list";
    private static final String POST_PAYMENT_SYSTEM = "https://api.syndex.io/merchant/api/get-payment-list";
    private static final String POST_CREATE_ORDER = "https://api.syndex.io/merchant/api/create-refill-order";
    private static final String POST_CONFIRM_ORDER = "https://api.syndex.io/merchant/api/confirm-order";
    private static final String POST_CANCEL_ORDER = "https://api.syndex.io/merchant/api/cancel-order";
    private static final String POST_OPEN_DISPUTE = "https://api.syndex.io/merchant/api/open-dispute";
    private static final String POST_ORDER_INFO = "https://api.syndex.io/merchant/api/info-order";

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    @PostConstruct
    private void init() {
        objectMapper = new ObjectMapper();

        client = new OkHttpClient
                .Builder()
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

    public static void main(String[] args) {
        SyndexClientImpl syndexClient = new SyndexClientImpl();
        syndexClient.objectMapper = new ObjectMapper();
        syndexClient.client =  new OkHttpClient
                .Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    String fieldsForSign = syndexClient.getFieldsForSign(original);

                    Request request = original.newBuilder()
                            .header("X-Auth-Token", "579802d19d191598daf828ba99cfc4075296ffee8fc0c14b6613b0bc1592050f")
                            .header("X-Auth-Sign", DigestUtils.sha256Hex(fieldsForSign.concat("9e20266ed1442e7dfd227ec2bb8a4c431d16416fa56a9888dfbe17819bb24ca3")))
                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                })
                .build();

        Request request = new Request.Builder()
                .post(syndexClient.buildRequestBody(new BaseRequestEntity()))
                .url("https://api.syndex.io/merchant/api/get-list")
                .build();
        String response = syndexClient.executeRequest(request, String.class);
        System.out.println(response);
    }

    @SneakyThrows
    private RequestBody buildRequestBody(Object payload) {
        return RequestBody.create(JSON, objectMapper.writeValueAsString(payload));
    }

    private String getSignature(String requestValues) {
        return DigestUtils.sha256Hex(requestValues.concat(secretKey));
    }
}
