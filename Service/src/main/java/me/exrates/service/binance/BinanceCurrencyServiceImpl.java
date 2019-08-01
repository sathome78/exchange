package me.exrates.service.binance;

import com.binance.dex.api.client.domain.broadcast.Transaction;
import com.binance.dex.api.client.domain.broadcast.TxType;
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

        System.out.println(binanceCurrencyService.binanceDexApiNodeClient.getTransaction("DBA8BD55160F809FABF75D2E6164C55BF18059C5EF0B22F675D21717EEC26EC8").getRealTx().toString());
        System.out.println(binanceCurrencyService.binanceDexApiNodeClient.getTransaction("DBA8BD55160F809FABF75D2E6164C55BF18059C5EF0B22F675D21717EEC26EC8").getResultData());
        System.out.println(binanceCurrencyService.binanceDexApiNodeClient.getTransaction("269EE2C587335F8FFD84A8C411A0C4C17E98398A40076F8DD1D6D70060F8657A").getRealTx());
        System.out.println(binanceCurrencyService.binanceDexApiNodeClient.getTransaction("269EE2C587335F8FFD84A8C411A0C4C17E98398A40076F8DD1D6D70060F8657A").getTxType());
//        System.out.println(binanceCurrencyService.getBlockTransactions( 6760625L).size());
        System.out.println("..........................");
       }


    @Override
    public List<Transaction> getBlockTransactions(long num){
        return binanceDexApiNodeClient.getBlockTransactions(num);
    }

    @Override
    public String getReceiverAddress(Transaction transaction){
        String transferInfo = transaction.getRealTx().toString();
        return null;
    }

    @Override
    public String getTocken(Transaction transaction){

        return null;
    }
}
