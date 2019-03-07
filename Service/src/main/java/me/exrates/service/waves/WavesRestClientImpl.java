package me.exrates.service.waves;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.merchants.waves.WavesAddress;
import me.exrates.model.dto.merchants.waves.WavesPayment;
import me.exrates.model.dto.merchants.waves.WavesTransaction;
import me.exrates.service.exception.WavesRestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Scope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2(topic = "waves_log")
@Service
@Scope("prototype")
@Conditional(MonolitConditional.class)
public class WavesRestClientImpl implements WavesRestClient {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private String host;
    private String port;
    private String apiKey;

    private final String API_KEY_HEADER_NAME = "api_key";

    private final int MAX_TRANSACTION_QUERY_LIMIT = 50;

    private final String newAddressEndpoint = "/addresses";
    private final String transferCostsEndpoint = "/assets/transfer";
    private final String accountTransactionsEndpoint = "/transactions/address/{address}/limit/{limit}";
    private final String transactionByIdEndpoint = "/transactions/info/{id}";
    private final String accountBalanceEndpoint = "/addresses/balance/{id}";

    @Override
    public void init(Properties props) {
        this.host = props.getProperty("waves.rest.host");
        this.port = props.getProperty("waves.rest.port");
        this.apiKey = props.getProperty("waves.rest.api.key");
    }

    @Override
    public String generateNewAddress() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(API_KEY_HEADER_NAME, apiKey);
        HttpEntity<String> entity = new HttpEntity<>("", headers);
        return restTemplate.postForObject(generateBaseUrl() + newAddressEndpoint, entity, WavesAddress.class).getAddress();
    }

    @Override
    public Integer getCurrentBlockHeight() {
        Integer height = restTemplate.exchange(generateBaseUrl() + "/blocks/height", HttpMethod.GET,
                new HttpEntity<>(""), new ParameterizedTypeReference<Map<String, Integer>>() {}).getBody().get("height");
        if (height == null) {
            throw new WavesRestException("Cannot obtain block height");
        }
        return height;
    }


    @Override
    public String transferCosts(WavesPayment payment) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(API_KEY_HEADER_NAME, apiKey);
        HttpEntity<WavesPayment> entity = new HttpEntity<>(payment, headers);
        try {
            return restTemplate.postForObject(generateBaseUrl() + transferCostsEndpoint, entity, WavesTransaction.class).getId();
        } catch (HttpClientErrorException e) {
            try {
                JsonNode error = objectMapper.readTree(e.getResponseBodyAsString()).get("error");
                if (error == null) {
                    throw new WavesRestException(e);
                }
                throw new WavesRestException(e, error.asInt());
            } catch (IOException e1) {
                throw new WavesRestException(e1);
            }
        }
    }

    @Override
    public List<WavesTransaction> getTransactionsForAddress(String address) {
        Map<String, Object> params = new HashMap<>();
        params.put("address", address);
        params.put("limit", MAX_TRANSACTION_QUERY_LIMIT);

        ResponseEntity<List<List<WavesTransaction>>> transactionsResult;
        try {
            transactionsResult = restTemplate.exchange(generateBaseUrl() + accountTransactionsEndpoint,
                    HttpMethod.GET, new HttpEntity<>(""), new ParameterizedTypeReference<List<List<WavesTransaction>>>() {}, params);
        }catch (Exception jsonEx) {
                log.error(jsonEx);
                return null;
        }
        return transactionsResult.getBody().stream().flatMap(List::stream).collect(Collectors.toList());

    }


    @Override
    public Optional<WavesTransaction> getTransactionById(String id) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        try {
            return Optional.of(restTemplate.getForObject(generateBaseUrl() + transactionByIdEndpoint, WavesTransaction.class, params));
        } catch (Exception e) {
            log.error(e);
            return Optional.empty();
        }
    }

    @Override
    public Long getAccountWavesBalance(String account) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", account);
        Long balance = (Long) restTemplate.exchange(generateBaseUrl() + accountBalanceEndpoint, HttpMethod.GET,
                new HttpEntity<>(""), new ParameterizedTypeReference<Map<String, Object>>() {}, params).getBody().get("balance");
        if (balance == null) {
            throw new WavesRestException("Cannot obtain balance for account " + account);
        }
        return balance;
    }


   /* public static void main(String[] args) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        String apiKey = "ridethewaves!";
        String baseUrl = "http://127.0.0.1:6869";
        String address = "3MrhJ3a5E1ATKxU15nKW59vfGdyqC2Rt5Xb";
        String txHistoryEP = "/transactions/address/{address}/limit/{limit}";
        String transferEP = "/assets/transfer";
        String txByIdEP = "/transactions/info/{id}";
        Map<String, Object> params = new HashMap<>();
        *//*params.put("address", address);
        params.put("limit", 50);
        ResponseEntity<List<List<WavesTransaction>>> transactionsResult = restTemplate.exchange(baseUrl + txHistoryEP, HttpMethod.GET,
                new HttpEntity<>(""), new ParameterizedTypeReference<List<List<WavesTransaction>>>() {}, params);
        List<List<WavesTransaction>> transactions = transactionsResult.getBody();
        transactions.stream().flatMap(List::stream).forEach(System.out::println);*//*
       WavesPayment wavesPayment = new WavesPayment();
       wavesPayment.setAmount(1839600000L);
       wavesPayment.setFee(100000L);
       wavesPayment.setSender("ЫЫ3MrhJ3a5E1ATKxU15nKW59vfGdyqC2Rt5Xb");
       wavesPayment.setRecipient("3N8DRLQKAc9arr6q5G9AagYUExXLhDJeGH3");
       wavesPayment.setAttachment("");

        HttpHeaders headers = new HttpHeaders();
        headers.add("api_key", apiKey);
        HttpEntity<WavesPayment> entity = new HttpEntity<>(wavesPayment, headers);
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(baseUrl + transferEP, HttpMethod.POST, entity, String.class);
        } catch (HttpClientErrorException e) {
            String body = e.getResponseBodyAsString();
            System.out.println(body);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode error = objectMapper.readTree(body).get("error");
            System.out.println(error.asInt());


        }

    //    System.out.println(response.getBody());
        *//*params.put("id", "64PmAooyNtDswN6cCjLXhu5mLfYSNVkqWG4dqVYXxBkv");
        System.out.println(restTemplate.getForObject(baseUrl+txByIdEP, WavesTransaction.class, params));*//*
        ;
        *//*System.out.println(restTemplate.exchange(baseUrl + "/blocks/height", HttpMethod.GET,
                new HttpEntity<>(""), new ParameterizedTypeReference<Map<String, Integer>>() {}).getBody().get("height"));
*//*

    }
*/








    private String generateBaseUrl() {
        return String.join(":", host, port);
    }







}
