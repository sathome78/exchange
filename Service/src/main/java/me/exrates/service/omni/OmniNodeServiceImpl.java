package me.exrates.service.omni;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.service.handler.RestResponseErrorHandler;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.URISyntaxException;

@Log4j2(topic = "omni_log")
@Service
@PropertySource("classpath:/merchants/omni.properties")
@Conditional(MonolitConditional.class)
public class OmniNodeServiceImpl implements OmniNodeService {

    private @Value("${node.omni.rpc.host}")String nodeHost;
    private @Value("${node.omni.rpc.user}")String rpcUser;
    private @Value("${node.omni.rpc.password}")String rpcPassword;
    private static final String[] EMPTY_PARAMS = {""};
    private URI nodeURI;

    private RestTemplate restTemplate;

    @PostConstruct
    private void init() {
        restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new RestResponseErrorHandler());
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(rpcUser, rpcPassword));
        try {
            nodeURI = new URI(nodeHost);
        } catch (URISyntaxException e) {
            log.error("wrong ADK url");
        }
    }

    @Override
    public boolean unlockWallet(String pass, int seconds) {
        RequestEntity requestEntity = RequestEntity
                .post(nodeURI)
                .header("Content-Type", "application/json")
                .body(createRequestBody("walletpassphrase", "7", new Object[]{pass, seconds}).toString());
        JSONObject response = makeRequest(requestEntity);
        return response.isNull("error");
    }

    @Override
    public String generateNewAddress() {
        RequestEntity requestEntity = RequestEntity
                .post(nodeURI)
                .header("Content-Type", "application/json")
                .body(createRequestBody("getnewaddress", "1", EMPTY_PARAMS).toString());
        JSONObject response = makeRequest(requestEntity);
        return response.getString("result");
    }

    @Override
    public String getBalance(String address, int propertyId) {
        RequestEntity requestEntity = RequestEntity
                .post(nodeURI)
                .header("Content-Type", "application/json")
                .body(createRequestBody("omni_getbalance", "getbalance", new Object[]{address, propertyId}).toString());
        JSONObject response = makeRequest(requestEntity);
        return response.getJSONObject("result").toString();
    }

    @Override
    public String getBtcInfo() {
        RequestEntity requestEntity = RequestEntity
                .post(nodeURI)
                .header("Content-Type", "application/json")
                .body(createRequestBody("getinfo", "getinfo", new Object[]{}).toString());
        JSONObject response = makeRequest(requestEntity);
        return response.getJSONObject("result").toString();
    }

    @Override
    public String getOmniBalances() {
        RequestEntity requestEntity = RequestEntity
                .post(nodeURI)
                .header("Content-Type", "application/json")
                .body(createRequestBody("omni_getwalletbalances", "getwalletbalances", new Object[]{}).toString());
        JSONObject response = makeRequest(requestEntity);
        return response.getJSONArray("result").toString();
    }

    @Override
    public String getTransaction(String txId) {
        RequestEntity requestEntity = RequestEntity
                .post(nodeURI)
                .header("Content-Type", "application/json")
                .body(createRequestBody("omni_gettransaction", "gettransaction", new Object[]{txId}).toString());
        JSONObject response = makeRequest(requestEntity);
        return response.getJSONObject("result").toString();
    }

    @Override
    public String listAllTransactions() {
        return listTransactions("*", 999999, 0, 0, 99999999);
    }

    @Override
    public String listTransactions(String address, int count, int offset, int startBlock, int endblock) {
        RequestEntity requestEntity = RequestEntity
                .post(nodeURI)
                .header("Content-Type", "application/json")
                .body(createRequestBody("omni_listtransactions", "listtransactions", new Object[]{address, count, offset, startBlock, endblock}).toString());
        JSONObject response = makeRequest(requestEntity);
        return response.getJSONArray("result").toString();
    }

    @Override
    public JSONObject sendFunded(String from, String to, int propertyId, String amount, String feeAdddr) {
        RequestEntity requestEntity = RequestEntity
                .post(nodeURI)
                .header("Content-Type", "application/json")
                .body(createRequestBody("omni_funded_send", "sendFunded", new Object[]{from, to,  propertyId, amount, feeAdddr}).toString());
        return  makeRequest(requestEntity);
    }

    private JSONObject createRequestBody(String method, String id, Object[] params) {
        return new JSONObject() {{
            put("jsonrpc", "1.0");
            put("method", method);
            put("id", id);
            put("params", new JSONArray(params));
        }};
    }

    private JSONObject makeRequest(RequestEntity requestEntity) {
        return new JSONObject(makeRequest(requestEntity, String.class));
    }

    private <T> T makeRequest(RequestEntity requestEntity, Class<T> clazz) {
        ResponseEntity<T> responseEntity = restTemplate.exchange(requestEntity, clazz);
        log.debug(responseEntity.getBody());
        return responseEntity.getBody();
    }


    /*test*/
    public static void main(String[] args) throws URISyntaxException {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new RestResponseErrorHandler());
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor("wayfarer88", "Xlo8JfnWMur__7nZkEWxz6GgcX8SnzY6ZvJDr4qHU4c="));
        JSONObject body = new JSONObject() {{
            put("jsonrpc", "1.0");
            put("method", "omni_gettransaction");
            put("id", "1");
            put("params", new String[]{"63ac737dc8637a58c66473b7a0e12d2383b42e8493ba3f70db6a37639929fcb2"});
        }};
        JSONObject getBalance = new JSONObject() {{
            put("jsonrpc", "1.0");
            put("method", "omni_getbalance");
            put("id", "1");
            put("params", new Object[]{"1Po1oWkD2LmodfkBYiAktwh76vkF93LKnh", 31});
        }};
        JSONObject getInfo = new JSONObject() {{
            put("jsonrpc", "1.0");
            put("method", "getinfo");
            put("id", "1");
            put("params", new String[]{});
        }};
        RequestEntity requestEntity = RequestEntity
                .post(new URI("http://localhost:8332"))
                .header("Content-Type", "application/json")
                .body(getInfo.toString());
        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
        System.out.println("response " + responseEntity.getStatusCode());
        System.out.println(responseEntity.getBody());
        /*String result = new JSONObject(responseEntity.getBody()).getJSONObject("result").toString();
        System.out.println(result);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            OmniTxDto transaction = objectMapper.readValue(result, new TypeReference<OmniTxDto>(){});
            System.out.println("success " + transaction);
        } catch (IOException e) {
            System.out.println(e);
        }*/
    }
}
