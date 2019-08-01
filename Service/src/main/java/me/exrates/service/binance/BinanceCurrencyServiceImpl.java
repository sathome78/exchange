package me.exrates.service.binance;

import com.binance.dex.api.client.domain.broadcast.Transaction;
import com.binance.dex.api.client.impl.BinanceDexApiNodeClientImpl;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.condition.MonolitConditional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Log4j2
@Service
@Conditional(MonolitConditional.class)
public class BinanceCurrencyServiceImpl implements BinanceCurrencyService {

    private RestTemplate restTemplate;
    BinanceDexApiNodeClientImpl binanceDexApiNodeClient;

    @Autowired
    public BinanceCurrencyServiceImpl(){
        restTemplate = new RestTemplate();
        binanceDexApiNodeClient = new BinanceDexApiNodeClientImpl("http://172.31.30.170:27147","BNB");
    }

    public static void main(String[] args) {
        BinanceCurrencyServiceImpl binanceCurrencyService = new BinanceCurrencyServiceImpl();
        long value = 6760515L;
        System.out.println("..........................");
//        while(true){
//            value++;
//            if (binanceCurrencyService.binanceDexApiNodeClient.getBlockMetaByHeight(value).getHeader().getNumTxs()>0) {
//                System.out.println(binanceCurrencyService.binanceDexApiNodeClient.getBlockMetaByHeight(value).getHeader().getHeight());
//                System.out.println(binanceCurrencyService.binanceDexApiNodeClient.getBlockMetaByHeight(value).getHeader().getNumTxs());
//                break;
//            }
//        }
//        System.out.println(binanceCurrencyService.binanceDexApiNodeClient.getBlockMetaByHeight(5153537L).getHeader().getHeight());
//        binanceCurrencyService.binanceDexApiNodeClient.getBlockTransactions( 5153537L).forEach(transaction -> System.out.println(transaction.getHash()));
//        System.out.println(binanceCurrencyService.binanceDexApiNodeClient.getTransaction("ADB8928498FAC144D0EB6275320A93997760A34DED974A5DA6F35242C7F64E26").getRealTx());
        System.out.println("..........................");
        System.out.println(binanceCurrencyService.binanceDexApiNodeClient.getBlockTransactions( 1780695L).size());
       }

    public String getTransactions(String hash){
        UriComponents builder = UriComponentsBuilder
                .fromHttpUrl("http://172.31.30.170:27147/tx_search?query=\"tx.height=4556289\"&prove=true")
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

    @Override
    public List<Transaction> getBlockTransactions(long num){
        return binanceDexApiNodeClient.getBlockTransactions(num);
    }

    @Override
    public Transaction getTransaction(String hash){
        binanceDexApiNodeClient.getTransaction("ADB8928498FAC144D0EB6275320A93997760A34DED974A5DA6F35242C7F64E26");
        return null;
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


}
