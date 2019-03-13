package me.exrates.service.impl;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import me.exrates.service.AisiCurrencyService;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Log4j2 (topic = "aisi_log")
@Service
@PropertySource("classpath:/merchants/aisi_wallet.properties")
public class AisiCurrencyServiceImpl implements AisiCurrencyService {

    private RestTemplate restTemplate;

    @Value("${aisi.apikey}")
    private String apiKey;

    @Value("${aisi.mainaddress}")
    private String mainaddress;

    @Autowired
    public AisiCurrencyServiceImpl() {
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        restTemplate = new RestTemplate(requestFactory);

    }

    public String generateNewAddress() {
        final MultiValueMap<String, String> requestParameters = new LinkedMultiValueMap<>();
        requestParameters.add("api_key", apiKey);
        UriComponents builder = UriComponentsBuilder
                .fromHttpUrl("https://api.aisi.io/account/address/new")
                .queryParams(requestParameters)
                .build();
        ResponseEntity<Address> responseEntity = null;
        try {
            responseEntity = restTemplate.getForEntity(builder.toUriString(), Address.class);
            if (responseEntity.getStatusCodeValue() != 200) {
                log.warn("Error : {}", responseEntity.getStatusCodeValue());
            }
        } catch (Exception ex) {
            log.warn("Error : {}", ex.getMessage());
        }
        return responseEntity.getBody().address;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class Address {

        @JsonProperty("Address")
        String address;

    }

    public List<Transaction> getAccountTransactions(){
        Integer max = Integer.MAX_VALUE;
        final MultiValueMap<String, String> requestParameters = new LinkedMultiValueMap<>();
        requestParameters.add("api_key", apiKey);
        UriComponents builder = UriComponentsBuilder
                .fromHttpUrl("https://api.aisi.io/transaction/account/{max}")
                .queryParams(requestParameters)
                .build();
        ResponseEntity<Transactions> responseEntity = null;
        try {
            responseEntity = restTemplate.getForEntity(builder.toUriString(), Transactions.class, max);
            if (responseEntity.getStatusCodeValue() != 200) {
                log.warn("Error : {}", responseEntity.getStatusCodeValue());
            }
        } catch (Exception ex) {
            log.warn("Error : {}", ex.getMessage());
        }

        Transaction[] transactions = responseEntity.getBody().transaction;
        return Arrays.asList(transactions);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class Transactions {

        @JsonProperty("Transactions")
        Transaction[] transaction;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    @Data
    public static class Transaction {

        @JsonProperty("Transaction_ID")
        private String transaction_id;
        @JsonProperty("TimeStamp")
        private String timeStamp;
        @JsonProperty("SenderAddress")
        private String senderAddress;
        @JsonProperty("RecieverAddress")
        private String recieverAddress;
        @JsonProperty("Amount")
        private String amount;
    }

    public String createNewTransaction(String address, BigDecimal amount){
        final MultiValueMap<String, String> requestParameters = new LinkedMultiValueMap<>();
        requestParameters.add("api_key", apiKey);
        UriComponents builder = UriComponentsBuilder
                .fromHttpUrl("https://api.aisi.io/transaction/create/{FROM_ADDRESS}/{TO_ADDRESS}/{AMOUNT}")
                .queryParams(requestParameters)
                .build();
        ResponseEntity<CreatedTransaction> responseEntity = null;
        try {
            responseEntity = restTemplate.getForEntity(builder.toUriString(), CreatedTransaction.class,
                    address, mainaddress, amount.toString());
            if (responseEntity.getStatusCodeValue() != 200) {
                log.warn("Error : {}", responseEntity.getStatusCodeValue());
            }
        } catch (Exception ex) {
            log.warn("Error : {}", ex.getMessage());
        }
        return responseEntity.getBody().result;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class CreatedTransaction {

        @JsonProperty("Transaction_Identifier")
        String transactionIdentifier;
        @JsonProperty("Result")
        String result;
        @JsonProperty("ResultMessage")
        String resultMessage;

    }

  /*
  *  getBalanceByAddress(); method is not using for now. It will be available in next up
  */
    public String getBalanceByAddress(String address){
        final MultiValueMap<String, String> requestParameters = new LinkedMultiValueMap<>();
        requestParameters.add("api_key", apiKey);
        UriComponents builder = UriComponentsBuilder
                .fromHttpUrl("https://api.aisi.io/account/{address}/balance")
                .queryParams(requestParameters)
                .build();
        ResponseEntity<Balance> responseEntity = null;
        try {
            responseEntity = restTemplate.getForEntity(builder.toUriString(), Balance.class, address);
            if (responseEntity.getStatusCodeValue() != 200) {
                log.warn("Error : {}", responseEntity.getStatusCodeValue());
            }
        } catch (Exception ex) {
            log.warn("Error : {}", ex.getMessage());
        }
        return responseEntity.getBody().balance;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class Balance {

        @JsonProperty("Balance")
        String balance;
    }

}
