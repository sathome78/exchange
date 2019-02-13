package me.exrates.service.impl;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.log4j.Log4j2;
import me.exrates.service.AisiCurrencyService;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;

@Log4j2
@Service
public class AisiCurrencyServiceImpl implements AisiCurrencyService {

    private RestTemplate restTemplate;

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
        requestParameters.add("api_key", "970E22216DA4C486CC22EEF9A58CD30E5B3A8A0D22A62F5D5B57222D16337814CEF3E7B1D7227C4754C733FE39F433F5C4E4E0F8B6D9D8F76F893BBA4");
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
 /*
  *  getBalanceByAddress(); method is not using for now. It will be available in next up
  */
    public String getBalanceByAddress(String address){
        final MultiValueMap<String, String> requestParameters = new LinkedMultiValueMap<>();
        requestParameters.add("api_key", "970E22216DA4C486CC22EEF9A58CD30E5B3A8A0D22A62F5D5B57222D16337814CEF3E7B1D7227C4754C733FE39F433F5C4E4E0F8B6D9D8F76F893BBA4");
        UriComponents builder = UriComponentsBuilder
                .fromHttpUrl("https://api.aisi.io/account/" + address + "/balance")
                .queryParams(requestParameters)
                .build();
        ResponseEntity<Balance> responseEntity = null;
        try {
            responseEntity = restTemplate.getForEntity(builder.toUriString(), Balance.class);
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

    public int getTotalBalance() {
        return 0;
    }

    private void test(){

    }
}
