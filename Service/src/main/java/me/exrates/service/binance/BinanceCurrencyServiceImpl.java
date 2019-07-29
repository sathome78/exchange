package me.exrates.service.binance;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.condition.MonolitConditional;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Log4j2
@Service
@Conditional(MonolitConditional.class)
public class BinanceCurrencyServiceImpl implements BinanceCurrencyService {

    private RestTemplate restTemplate;

    @Autowired
    public BinanceCurrencyServiceImpl (){
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
                .build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        restTemplate = new RestTemplate(requestFactory);
    }

    public String getAddressInfo(String address){
        UriComponents builder = UriComponentsBuilder
                .fromHttpUrl("https://dex.binance.org/api/v1/account/{address}")
                .build();
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = restTemplate.getForEntity(builder.toUriString(), String.class, address);
            if (responseEntity.getStatusCodeValue() != 200) {
                log.error("Error : {}", responseEntity.getStatusCodeValue());
            }
        } catch (Exception ex) {
            log.error("Error : {}", ex.getMessage());
        }

        return responseEntity.getBody();
    }

    public String checkTransaction(String hash){
        UriComponents builder = UriComponentsBuilder
                .fromHttpUrl("https://dex.binance.org/api/v1/tx/{hash}?format=json")
                .build();
        ResponseEntity<String> responseEntity = null;
        try {
            responseEntity = restTemplate.getForEntity(builder.toUriString(), String.class, hash);
            if (responseEntity.getStatusCodeValue() != 200) {
                log.error("Error : {}", responseEntity.getStatusCodeValue());
            }
        } catch (Exception ex) {
            log.error("Error : {}", ex.getMessage());
        }

        return responseEntity.getBody();
    }

    public static void main(String[] args) {
        BinanceCurrencyServiceImpl binanceCurrencyService = new BinanceCurrencyServiceImpl();
        System.out.println(binanceCurrencyService.checkTransaction("EFC93BEA474BB22DC028D5D4338F67B6AE06D2912E47209C475796395B405DA8"));
    }
}
