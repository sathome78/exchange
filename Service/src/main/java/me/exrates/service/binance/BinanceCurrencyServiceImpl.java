package me.exrates.service.binance;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
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
    public BinanceCurrencyServiceImpl(){
//        CloseableHttpClient httpClient = HttpClients.custom()
//                .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
//                .build();
//        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
//        requestFactory.setHttpClient(httpClient);
        restTemplate = new RestTemplate();
    }

    public int getAddressInfo(int num){
        UriComponents builder = UriComponentsBuilder
                .fromHttpUrl("http://172.31.30.170:27147/block?height={num}")
                .build();
        ResponseEntity<Response> responseEntity = null;
        try {
            responseEntity = restTemplate.getForEntity(builder.toUriString(), Response.class, num);
            if (responseEntity.getStatusCodeValue() != 200) {
                log.error("Error : {}", responseEntity.getStatusCodeValue());
            }
        } catch (Exception ex) {
            log.error("Error : {}", ex.getMessage());
        }

        return responseEntity.getBody().result.block_meta.header.num_txs;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    @Data
    private static class Response {

        @JsonProperty("result")
        Result result;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class Result {

        @JsonProperty("block_meta")
        BlockMeta block_meta;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class BlockMeta {

        @JsonProperty("header")
        Header header;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class Header {

        @JsonProperty("num_txs")
        int num_txs;
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
        int numb = 580500;
        while (++numb <= 800000){
            if (binanceCurrencyService.getAddressInfo(numb) > 0){
                System.out.println(numb + " : " + binanceCurrencyService.getAddressInfo(numb));
            }
        }
//        System.out.println(binanceCurrencyService.checkTransaction("CA4394B114376FF06AEA55866DFF5CD058F591AD3A18E28B34DF502E66AE796B"));
    }
}
