package me.exrates.service.lisk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.model.dto.merchants.lisk.LiskAccount;
import me.exrates.model.dto.merchants.lisk.LiskOpenAccountDto;
import me.exrates.model.dto.merchants.lisk.LiskSendTxDto;
import me.exrates.model.dto.merchants.lisk.LiskTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static me.exrates.service.lisk.LiskRestUtils.extractListFromResponseAdditional;
import static me.exrates.service.lisk.LiskRestUtils.extractObjectFromResponse;
import static me.exrates.service.lisk.LiskRestUtils.extractObjectFromResponseAdditional;
import static me.exrates.service.lisk.LiskRestUtils.extractTargetNodeFromLiskResponseAdditional;
import static me.exrates.service.lisk.LiskRestUtils.getURIWithParams;

@Log4j2(topic = "lisk_log")
@Conditional(MonolitConditional.class)
public class LiskRestClientImpl implements LiskRestClient {

    @Autowired
    private RestTemplate restTemplate;

    private String baseUrl;
    private String microserviceUrl;
    private int maxTransactionQueryLimit;
    private JsonNodeType countNodeType;

    private ObjectMapper objectMapper = new ObjectMapper();

    private final String newAccountEndpoint = "/api/accounts/open";
    private final String getAccountByAddressEndpoint = "/api/accounts";
    private final String getTransactionsEndpoint = "/api/transactions";
    private final String getTransactionByIdEndpoint = "/api/transactions";
    private final String getSignedTransactionWithData = "/api/transfer";
    private final String sendTransactionEndpoint = "/api/transactions";
    private final String getFeeEndpoint = "/api/node/constants";



    @Override
    public void initClient(String propertySource) {
        Properties props = new Properties();
        try {
            props.load(getClass().getClassLoader().getResourceAsStream(propertySource));
            String host = props.getProperty("lisk.node.host");
            String mainPort = props.getProperty("lisk.node.port");
            String microserviceHost = props.getProperty("lisk.microservice.host");
            String microservicePort = props.getProperty("lisk.microservice.port");

            this.microserviceUrl = String.join(":", microserviceHost, microservicePort);
            this.baseUrl = String.join(":", host, mainPort);
            this.maxTransactionQueryLimit = Integer.parseInt(props.getProperty("lisk.tx.queryLimit"));
            this.countNodeType = JsonNodeType.valueOf(props.getProperty("lisk.tx.count.nodeType"));

        } catch (IOException e) {
            log.error(e);
        }
    }

    @Override
    public LiskTransaction getTransactionById(String txId) {
        String response = restTemplate.getForObject(getURIWithParams(absoluteURI(getTransactionByIdEndpoint), Collections.singletonMap("id", txId)),
                    String.class);

        log.info("*** Lisk *** getTransactionById: "+response);

        return extractListFromResponseAdditional(objectMapper, response, "data", LiskTransaction.class).get(0);
    }

    @Override
    public List<LiskTransaction> getTransactionsByRecipient(String recipientAddress) {
            Map<String, String> params = new HashMap<String, String>() {{
               put("recipientId", recipientAddress);
               put("sort", "timestamp:asc");
            }};
            String response = restTemplate.getForObject(getURIWithParams(absoluteURI(getTransactionsEndpoint), params),
                    String.class);

            log.info("*** Lisk *** getTransactionsByRecipient: "+response);

            return extractListFromResponseAdditional(objectMapper, response, "data", LiskTransaction.class);
    }

    @Override
    public List<LiskTransaction> getAllTransactionsByRecipient(String recipientAddress, int offset) {
        log.info("Retrieving transactions: address {} offset {}", recipientAddress, offset);
        List<LiskTransaction> result = new ArrayList<>();
        int newOffset = offset;
        int count;
        do {
            String response = sendGetTransactionsRequest(recipientAddress, newOffset);
            count = Integer.parseInt(extractTargetNodeFromLiskResponseAdditional(objectMapper, response, "count", countNodeType).asText());
            result.addAll(extractListFromResponseAdditional(objectMapper, response, "data", LiskTransaction.class));
            newOffset += result.size();
        } while (newOffset < count);
        return result;
    }

    private String sendGetTransactionsRequest(String recipientAddress, int offset) {
        Map<String, String> params = new HashMap<String, String>() {{
            put("recipientId", recipientAddress);
            put("limit", String.valueOf(maxTransactionQueryLimit));
            put("offset", String.valueOf(offset));
            put("sort", "timestamp:asc");
        }};
        URI targetURI = getURIWithParams(absoluteURI(getTransactionsEndpoint), params);

        log.info("*** Lisk *** getTransactionsByRecipient: "+targetURI);

        return restTemplate.getForObject(targetURI, String.class);
    }

    @Override
    public Long getFee() {
        String response = restTemplate.getForObject(absoluteURI(getFeeEndpoint), String.class);
        return Long.parseLong(extractTargetNodeFromLiskResponseAdditional(objectMapper, response, "send", JsonNodeType.STRING).textValue());
    }


    @Override
    public String sendTransaction(LiskSendTxDto dto) {

        //Get signed transaction with data
        String responseFromMicroservice = restTemplate.postForObject(microserviceUrl.concat(getSignedTransactionWithData), dto, String.class);

//        log.info("*** Lisk *** Signed transactions (/api/transfer) from microservice: "+responseFromMicroservice);

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(responseFromMicroservice, headers);

        //Post signed transaction with data into network
        String postSignedTrans = restTemplate.postForObject(absoluteURI(sendTransactionEndpoint), entity, String.class);

//        log.info("*** Lisk *** Posted signed transactions: "+postSignedTrans);

        return extractTargetNodeFromLiskResponseAdditional(objectMapper, responseFromMicroservice, "id", JsonNodeType.STRING).textValue();
    }

    @Override
    public LiskAccount createAccount(String secret) {
        LiskOpenAccountDto dto = new LiskOpenAccountDto();
        dto.setSecret(secret);
        ResponseEntity<String> response = restTemplate.exchange(microserviceUrl.concat(newAccountEndpoint), HttpMethod.POST, new HttpEntity<>(dto), String.class);
        return extractObjectFromResponse(objectMapper, response.getBody(), "account", LiskAccount.class);
    }

    @Override
    public LiskAccount getAccountByAddress(String address) {
        String response = restTemplate.getForObject(getURIWithParams(absoluteURI(getAccountByAddressEndpoint), Collections.singletonMap("address", address)),
                String.class);

        log.info("*** Lisk *** getTransactionsByRecipient: "+response);

        return extractObjectFromResponseAdditional(objectMapper, response, "data", LiskAccount.class);
    }




    private String absoluteURI(String relativeURI) {
        return String.join("", baseUrl, relativeURI);
    }


}
