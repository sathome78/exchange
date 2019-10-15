package me.exrates.service.usdx;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.exrates.service.AlgorithmService;
import me.exrates.service.exception.UsdxApiException;
import me.exrates.service.usdx.model.UsdxAccountBalance;
import me.exrates.service.usdx.model.UsdxApiResponse;
import me.exrates.service.usdx.model.UsdxHistoryTransaction;
import me.exrates.service.usdx.model.UsdxTransaction;
import me.exrates.service.usdx.model.enums.UsdxApiRequestStatus;
import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log4j2(topic = "usdx_log")
@Service
@PropertySource("classpath:/merchants/usdx.properties")
public class UsdxRestApiServiceImpl implements UsdxRestApiService {

    private final static String GET_ACCOUNT_BALANCE = "exchange/:exchangeId/balance";
    private final static String GET_TRANSACTIONS_HISTORY = "exchange/:exchangeId/history";
    private final static String GET_TRANSACTION = "exchange/:exchangeId/transfer/{transferId}";
    private final static String SEND_TRANSACTION = "exchange/:exchangeId/transfer";

    private final static String EXCHANGE_ID_TEXT_FOR_REPLACE = ":exchangeId";

    private final static String SECURITY_HEADER_NAME = "x-usdx-signature";

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Value("${usdx.server.url}")
    private String baseUrl;

    @Value("${usdx.account.name}")
    private String accountName;

    @Value("${usdx.api.key}")
    private String apiKey;

    @Value("${usdx.exchange.id}")
    private String exchangeId;

    @Autowired
    private AlgorithmService algorithmService;

    @Override
    public UsdxTransaction transferAssetsToUserAccount(UsdxTransaction usdxTransaction) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity entity = new HttpEntity<>(usdxTransaction, headers);

        RestTemplate restTemplate = getRestTemplateWithHeaderForRequest(getStringJsonUsdxTransaction(usdxTransaction));

        UsdxApiResponse<UsdxTransaction> transactionUsdxApiResponse = restTemplate.exchange(baseUrl + SEND_TRANSACTION.replace(EXCHANGE_ID_TEXT_FOR_REPLACE, exchangeId), HttpMethod.POST,
                entity, new ParameterizedTypeReference<UsdxApiResponse<UsdxTransaction>>() {}).getBody();

        log.debug("Method: transferAssetsToUserAccount. Params (object): {}. Status: {}. Response (object): {}",
                usdxTransaction, transactionUsdxApiResponse.getStatus(), transactionUsdxApiResponse.getData());

        checkResponseAndThrowUsdxApiExceptionWhenHasErrorOrFail(transactionUsdxApiResponse.getStatus(), transactionUsdxApiResponse.getMessage(),
                transactionUsdxApiResponse.getData().getErrorCode(), transactionUsdxApiResponse.getData().getFailReason());

        return transactionUsdxApiResponse.getData();
    }

    @Override
    public List<UsdxTransaction> getAllTransactions(){
        RestTemplate restTemplate = getRestTemplateWithHeaderForRequest("");

        UsdxApiResponse<UsdxHistoryTransaction> transactionUsdxApiResponse = restTemplate.exchange(baseUrl + GET_TRANSACTIONS_HISTORY.replace(EXCHANGE_ID_TEXT_FOR_REPLACE, exchangeId),
                HttpMethod.GET, null, new ParameterizedTypeReference<UsdxApiResponse<UsdxHistoryTransaction>>() {}).getBody();

        log.debug("Method: getAllTransactions(). Status: {}", transactionUsdxApiResponse.getStatus());

        checkResponseAndThrowUsdxApiExceptionWhenHasErrorOrFail(transactionUsdxApiResponse.getStatus(), transactionUsdxApiResponse.getMessage(),
                transactionUsdxApiResponse.getData().getErrorCode(), transactionUsdxApiResponse.getData().getFailReason());

        return Arrays.asList(transactionUsdxApiResponse.getData().getHistory());
    }

    @Override
    public List<UsdxTransaction> getTransactionsHistory(String fromId, int limit) {
        RestTemplate restTemplate = getRestTemplateWithHeaderForRequest("");

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + GET_TRANSACTIONS_HISTORY.replace(EXCHANGE_ID_TEXT_FOR_REPLACE, exchangeId))
                .queryParam("fromId", fromId)
                .queryParam("limit", limit);


        UsdxApiResponse<UsdxHistoryTransaction> transactionUsdxApiResponse = restTemplate.exchange(builder.toUriString(), HttpMethod.GET,
                null, new ParameterizedTypeReference<UsdxApiResponse<UsdxHistoryTransaction>>() {}).getBody();

        log.debug("Method: getTransactionsHistory(String fromId, int limit). Params: fromId {}, limit {}. Status: {}",
                fromId, limit, transactionUsdxApiResponse.getStatus());

        checkResponseAndThrowUsdxApiExceptionWhenHasErrorOrFail(transactionUsdxApiResponse.getStatus(), transactionUsdxApiResponse.getMessage(),
                transactionUsdxApiResponse.getData().getErrorCode(), transactionUsdxApiResponse.getData().getFailReason());

        return Arrays.asList(transactionUsdxApiResponse.getData().getHistory());
    }

    @Override
    public UsdxAccountBalance getAccountBalance() {
        RestTemplate restTemplate = getRestTemplateWithHeaderForRequest("");

        UsdxApiResponse<UsdxAccountBalance> accountBalanceUsdxApiResponse = restTemplate.exchange(baseUrl + GET_ACCOUNT_BALANCE.replace(EXCHANGE_ID_TEXT_FOR_REPLACE, exchangeId), HttpMethod.GET,
                null, new ParameterizedTypeReference<UsdxApiResponse<UsdxAccountBalance>>() {}).getBody();

        log.debug("Method: getAccountBalance(). Status: {}", accountBalanceUsdxApiResponse.getStatus());

        checkResponseAndThrowUsdxApiExceptionWhenHasErrorOrFail(accountBalanceUsdxApiResponse.getStatus(), accountBalanceUsdxApiResponse.getMessage(),
                accountBalanceUsdxApiResponse.getData().getErrorCode(), accountBalanceUsdxApiResponse.getData().getFailReason());

        return accountBalanceUsdxApiResponse.getData();
    }

    @Override
    public UsdxTransaction getTransactionStatus(String transferId) {
        RestTemplate restTemplate = getRestTemplateWithHeaderForRequest("");

        UsdxApiResponse<UsdxTransaction> transactionUsdxApiResponse = restTemplate.exchange(baseUrl + GET_TRANSACTION.replace(EXCHANGE_ID_TEXT_FOR_REPLACE, exchangeId), HttpMethod.GET,
                null, new ParameterizedTypeReference<UsdxApiResponse<UsdxTransaction>>() {}, transferId).getBody();

        log.debug("Method: getTransactionStatus(String transferId). Params: transferId {}. Status: {}. Response (object): {}",
                transferId, transactionUsdxApiResponse.getStatus(), transactionUsdxApiResponse.getData());

        checkResponseAndThrowUsdxApiExceptionWhenHasErrorOrFail(transactionUsdxApiResponse.getStatus(), transactionUsdxApiResponse.getMessage(),
                transactionUsdxApiResponse.getData().getErrorCode(), transactionUsdxApiResponse.getData().getFailReason());

        return transactionUsdxApiResponse.getData();
    }

    @Override
    public String getAccountName(){
        return accountName;
    }

    @Override
    public String getSecurityHeaderName(){
        return SECURITY_HEADER_NAME;
    }

    @Override
    public String generateSecurityHeaderValue(String timestamp, String body){
        String value = algorithmService.sha256(body + apiKey + timestamp);

        return "t=" + timestamp + ", v1=" + value;
    }

    @Override
    public String getStringJsonUsdxTransaction(UsdxTransaction usdxTransaction){
        String transactionJsonAsString = "";
        try {
            transactionJsonAsString = objectMapper.writeValueAsString(usdxTransaction);
        } catch (JsonProcessingException e) {
            log.error("Error:" + e);
        }
        return transactionJsonAsString;
    }

    private RestTemplate getRestTemplateWithHeaderForRequest(String body){
        RestTemplate restTemplate = new RestTemplate();

        HttpClientBuilder b = HttpClientBuilder.create();
        List<Header> headers = new ArrayList<>();

        headers.add(new BasicHeader(SECURITY_HEADER_NAME, generateSecurityHeaderValue(String.valueOf(System.currentTimeMillis()), body)));

        b.setDefaultHeaders(headers);

        HttpClient client = b.build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(client);

        restTemplate.setRequestFactory(requestFactory);

        return restTemplate;
    }

    private void checkResponseAndThrowUsdxApiExceptionWhenHasErrorOrFail(String status, String message, String errorCode, String failReason) {
        if (status.equals(UsdxApiRequestStatus.ERROR.getName())) {
            log.error("USDX Wallet. Error message: " + message);
            throw new UsdxApiException(message);
        } else if (status.equals(UsdxApiRequestStatus.FAIL.getName())) {
            log.error("USDX Wallet. Error code: " + errorCode + " | Fail reasone: " + failReason);
            throw new UsdxApiException("Error code: " + errorCode + " | Fail reasone: " + failReason);
        }
    }

}
