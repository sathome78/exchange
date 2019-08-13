package me.exrates.service.binance;

import com.binance.dex.api.client.domain.broadcast.Transaction;
import com.binance.dex.api.client.impl.BinanceDexApiNodeClientImpl;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.condition.MonolitConditional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Log4j2 (topic = "binance_log")
@Service
@Conditional(MonolitConditional.class)
public class BinanceCurrencyServiceImpl implements BinanceCurrencyService {

    private static final String RECEIVER_ADDRESS_CODE = "outputs=[InputOutput[address=";
    private static final String TOKEN_CODE = "coins=[Token[denom=";
    private static final String AMOUNT_CODE = "amount=";
    private static final BigDecimal FACTOR = new BigDecimal(100000000);

    private BinanceDexApiNodeClientImpl binanceDexApiNodeClient;


    @Autowired
    public BinanceCurrencyServiceImpl(){
        // TODO HRP????
        binanceDexApiNodeClient = new BinanceDexApiNodeClientImpl("http://172.31.30.170:27147","BNB");
    }

    @Override
    public List<Transaction> getBlockTransactions(long num){
        return binanceDexApiNodeClient.getBlockTransactions(num);
    }

    @Override
    public String getReceiverAddress(Transaction transaction){
        String transferInfo = transaction.getRealTx().toString();
        transferInfo = transferInfo.substring(transferInfo.indexOf(RECEIVER_ADDRESS_CODE) + RECEIVER_ADDRESS_CODE.length());
        transferInfo = transferInfo.substring(0, transferInfo.indexOf(","));
        return transferInfo;
    }

    @Override
    public String getToken(Transaction transaction){
        String transferInfo = transaction.getRealTx().toString();
        transferInfo = transferInfo.substring(transferInfo.indexOf(TOKEN_CODE) + TOKEN_CODE.length());
        transferInfo = transferInfo.substring(0, transferInfo.indexOf(","));
        return transferInfo;
    }

    @Override
    public String getHash(Transaction transaction){
        return transaction.getHash();
    }

    @Override
    public String getAmount(Transaction transaction){
        String transferInfo = transaction.getRealTx().toString();
        transferInfo = transferInfo.substring(transferInfo.indexOf(AMOUNT_CODE) + AMOUNT_CODE.length());
        transferInfo = transferInfo.substring(0, transferInfo.indexOf("]"));
        return scaleToBinanceFormat(transferInfo);
    }

    @Override
    public String getMemo(Transaction transaction){
        return transaction.getMemo();
    }

    @Override
    public long getBlockchainHeigh() {
        return binanceDexApiNodeClient.getNodeInfo().getSyncInfo().getLatestBlockHeight();
    }

    private String scaleToBinanceFormat(String amount){
        BigDecimal scaledNum = new BigDecimal(amount);
        return scaledNum.divide(FACTOR).toString();
    }
}
