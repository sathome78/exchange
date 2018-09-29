package me.exrates.service.aidos;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.merchants.btc.BtcTransactionDto;
import me.exrates.model.dto.merchants.btc.BtcWalletPaymentItemDto;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2(topic = "adk_log")
@Service
@PropertySource("classpath:/merchants/adk.properties")
public class AidosNodeServiceImpl implements AidosNodeService {

    private @Value("${node.adk.rpc.host}")String nodeHost;
    private @Value("${node.adk.rpc.user}")String rpcUser;
    private @Value("${node.adk.rpc.password}")String rpcPassword;
    private URI nodeURI;

    private static final String[] EMPTY_PARAMS = {};

    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;

    @PostConstruct
    private void init() {
        objectMapper = new ObjectMapper();
        restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(rpcUser, rpcPassword));
        try {
            nodeURI = new URI(nodeHost);
        } catch (URISyntaxException e) {
            log.error("wrong ADK url");
        }
    }

    @Override
    public String generateNewAddress() {
        RequestEntity requestEntity = RequestEntity
                .post(nodeURI)
                .body(createRequestBody("getnewaddress", "1", EMPTY_PARAMS).toString());
        JSONObject response = makeRequest(requestEntity);
        return response.getString("result");
    }

    @Override
    public BigDecimal getBalance() {
        RequestEntity requestEntity = RequestEntity
                .post(nodeURI)
                .body(createRequestBody("getbalance", "2", EMPTY_PARAMS).toString());
        JSONObject response = makeRequest(requestEntity);
        return response.getBigDecimal("result");
    }


    @Override
    public BtcTransactionDto getTransaction(String txId) {
        RequestEntity requestEntity = RequestEntity
                .post(nodeURI)
                .body(createRequestBody("gettransaction", "3", new Object[]{txId}).toString());
        JSONObject result = makeRequest(requestEntity).getJSONObject("result");
        try {
            return objectMapper.readValue(result.toString(), BtcTransactionDto.class);
        } catch (IOException e) {
            log.error(e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public JSONArray getAllTransactions() {
        return getAllTransactions(999999999 ,0);
    }

    @Override
    public JSONArray getAllTransactions(Integer count, Integer from) {
        List<Object> args = new ArrayList<>();
        if (count != null && from != null) {
            args.add("");
            args.add(count);
            args.add(from);
        }
        RequestEntity requestEntity = RequestEntity
                .post(nodeURI)
                .body(createRequestBody("listtransactions", "4", args.toArray()).toString());
        JSONObject response = makeRequest(requestEntity);
        return response.getJSONArray("result");
    }

    @Override
    public JSONObject sendToAddress(String address, BigDecimal amount) {
        RequestEntity requestEntity = RequestEntity
                .post(nodeURI)
                .body(createRequestBody("sendtoaddress", "5", new Object[]{address, amount}).toString());
        JSONObject response = makeRequest(requestEntity);
        return response.getJSONObject("result");
    }

    @Override
    public JSONObject sendMany(List<BtcWalletPaymentItemDto> payments) {
        String convertedPayments =
                new JSONObject(payments
                                .stream()
                                .collect(Collectors.toMap(BtcWalletPaymentItemDto::getAddress, BtcWalletPaymentItemDto::getAmount))).toString();
        RequestEntity requestEntity = RequestEntity
                .post(nodeURI)
                .body(createRequestBody("sendmany", "6", new Object[]{"", convertedPayments}).toString());
        JSONObject response = makeRequest(requestEntity);
        log.info("send many response {}", response);
        return response;
    }

    @Override
    public boolean unlockWallet(String pass, int seconds) {
        RequestEntity requestEntity = RequestEntity
                .post(nodeURI)
                .body(createRequestBody("walletpassphrase", "7", new Object[]{pass, seconds}).toString());
        JSONObject response = makeRequest(requestEntity);
        return response.isNull("error");
    }

    private JSONObject createRequestBody(String method, String id, Object[] params) {
        return new JSONObject() {{
            put("method", method);
            put("id", id);
            put("params", new JSONArray(params));
        }};
    }

    private JSONObject makeRequest(RequestEntity requestEntity) {
        return new JSONObject(makeRequest(requestEntity, String.class));
    }

    private <T> T makeRequest(RequestEntity requestEntity, Class<T> clazz) {
        return restTemplate.exchange(requestEntity, clazz).getBody();
    }

}
