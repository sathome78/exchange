package me.exrates.service.syndex;

import com.fasterxml.jackson.core.type.TypeReference;
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
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


@PropertySource(value = "classpath:/merchants/syndex.properties")
@Log4j2(topic = "syndex")
@Component
public class SyndexClientImpl implements SyndexClient {

    /*X-Auth-Sign = sha256 (request_id + api_secret)*/
    @Value("${x-auth-tokхen}")
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

    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    @PostConstruct
    private void init() {
        objectMapper = new ObjectMapper();



        client = new OkHttpClient
                .Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    String requestId = getRequestId(original);

                    Request request = original.newBuilder()
                            .header("X-Auth-Token", token)
                            .header("X-Auth-Sign", getSignature(requestId))
                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                })
                .build();
    }

    private String getRequestId(Request request) {
        if (request.body().contentType().type().equals(JSON.type())) {
            JSONObject reqJson = new JSONObject(bodyToString(request));
            return reqJson.getString(REQUEST_ID_FIELD);
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
            return "did not work";
        }
    }


    @Override
    public OrderInfo createOrder(CreateOrderRequest orderDto) {
        Request request = new Request.Builder()
                .post(buildRequestBody(orderDto))
                .url(POST_CREATE_ORDER)
                .build();

        return executeRequest(request);
    }

    @Override
    public OrderInfo getOrderInfo(long orderId) {
        Request request = new Request.Builder()
                .post(buildRequestBody(new OrderRequest(orderId)))
                .url(POST_ORDER_INFO)
                .build();

        return executeRequest(request);
    }

    @Override
    public String cancelOrder(long orderId) {
        Request request = new Request.Builder()
                .post(buildRequestBody(new OrderRequest(orderId)))
                .url((POST_CANCEL_ORDER))
                .build();

        return executeRequest(request);
    }

    @Override
    public String confirmOrder(long orderId) {
        Request request = new Request.Builder()
                .post(buildRequestBody(new OrderRequest(orderId)))
                .url(POST_CONFIRM_ORDER)
                .build();

        return executeRequest(request);
    }


    @Override
    public String openDispute(long orderId, String comment) {
        Request request = new Request.Builder()
                .post(buildRequestBody(new OpenDisputeRequest(String.valueOf(orderId), comment)))
                .url(POST_OPEN_DISPUTE)
                .build();
        return executeRequest(request);
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
    public List<PaymentSystem> getPaymentSystems(String countryCode) {

        Request request = new Request.Builder()
                .post(buildRequestBody(new GetPaymentSystemRequest(countryCode)))
                .url(POST_PAYMENT_SYSTEM)
                .build();

        List<PaymentSystemWrapper> paymentSystems = executeRequest(request);
        return paymentSystems.get(0).getPaymentSystems();
    }

    private <T> T executeRequest(Request request) {
        try {
            Response response = client
                    .newCall(request)
                    .execute();
            return handleResponse(response);

        } catch (SyndexCallException e) {
            log.error(e);
            throw e;
        } catch (Exception e) {
            log.error(e);
            throw new SyndexCallException("Error getting payment-systems", e);
        }
    }

    private <T> T handleResponse(Response response) throws IOException {
        if (response.isSuccessful()) {
            String body = Objects.requireNonNull(response.body()).string();
            log.debug(body);
            BaseResponse<T> baseResponse = objectMapper.readValue(body, new TypeReference<BaseResponse<T>>() {});
            return baseResponse
                    .getResult();
        } else if (response.code() == 400) {
            throw new SyndexCallException(objectMapper.readValue(Objects.requireNonNull(response.body()).string(), new TypeReference<BaseError<Error>>() {}));
        } else {
            throw new RuntimeException();
        }
    }


    @SneakyThrows
    private RequestBody buildRequestBody(Object payload) {
        return RequestBody.create(JSON, objectMapper.writeValueAsString(payload));
    }

    private String getSignature(String requestId) {
        return DigestUtils.sha256Hex(requestId.concat(secretKey));
    }

}
