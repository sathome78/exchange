package me.exrates.service.impl;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import me.exrates.service.GapiCurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;

@Service
@Log4j2(topic = "gapi_log")
public class GapiCurrencyServiceImpl implements GapiCurrencyService {

    private RestTemplate restTemplate;

    @Autowired
    public GapiCurrencyServiceImpl (){
        restTemplate = new RestTemplate();
    }

    public List<String> generateNewAddress() {
        UriComponents builder = UriComponentsBuilder
                .fromHttpUrl("http://18.217.228.135/api/v1/createnewwallet")
                .build();
        ResponseEntity<Wallet> responseEntity = null;
        try {
            responseEntity = restTemplate.getForEntity(builder.toUriString(), Wallet.class);
            if (responseEntity.getStatusCodeValue() != 200) {
                log.warn("Error : {}", responseEntity.getStatusCodeValue());
            }
        } catch (Exception ex) {
            log.warn("Error : {}", ex.getMessage());
        }

        String address = responseEntity.getBody().wallet.address;
        String privateKey = responseEntity.getBody().wallet.private_key;
        return Arrays.asList(address, privateKey);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class Wallet {

        @JsonProperty("wallet")
        private Address wallet;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class Address {

        @JsonProperty("wallet_id")
        private String address;
        //        TODO save private key in database
        @JsonProperty("private_key")
        private String private_key;
    }

    public List<Transaction> getAccountTransactions(){
//        TODO send 18.217.228.135 to properties
        UriComponents builder = UriComponentsBuilder
                .fromHttpUrl("http://18.217.228.135/api/v1/alltransactions")
                .build();
        ResponseEntity<Transactions> responseEntity = null;
        try {
            responseEntity = restTemplate.getForEntity(builder.toUriString(), Transactions.class);
            if (responseEntity.getStatusCodeValue() != 200) {
                log.warn("Error : {}", responseEntity.getStatusCodeValue());
            }
        } catch (Exception ex) {
            log.warn("Error : {}", ex.getMessage());
        }

        return Arrays.asList(responseEntity.getBody().transactions);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class Transactions {

        @JsonProperty("alltestsarecomplated")
        private Transaction[] transactions;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    @Data
    public static class Transaction {

        @JsonProperty("senderwallet")
        private String senderAddress;
        @JsonProperty("receiver")
        private String recieverAddress;
        @JsonProperty("amount")
        private String amount;
        @JsonProperty("blockhash")
        private String transaction_id;
    }

    public String createNewTransaction(String privKey, String address, String amount){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();

        map.add("sprikey", privKey);
        map.add("receiverwalletallows", address);
        map.add("amount", amount);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://18.217.228.135/api/v1/sendgapicoin", request , String.class);

        return response.getBody();
    }

    public static void main(String... args){
//        List<Transaction> transactions = new GapiCurrencyServiceImpl().getAccountTransactions();
//        for (Transaction elem : transactions){
//            System.out.println(elem.recieverAddress);
//        }
        GapiCurrencyServiceImpl gapiCurrencyService = new GapiCurrencyServiceImpl();
//        System.out.println(gapiCurrencyService.createNewTransaction());
    }

}