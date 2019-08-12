package me.exrates.service.binance;

import com.binance.dex.api.client.domain.broadcast.Transaction;
import com.binance.dex.api.client.domain.broadcast.TxType;
import com.binance.dex.api.client.impl.BinanceDexApiNodeClientImpl;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.condition.MonolitConditional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Log4j2
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

    public static void main(String[] args) {
        BinanceCurrencyServiceImpl binanceCurrencyService = new BinanceCurrencyServiceImpl();
        long value = 26670174L;
        //https://explorer.binance.org/txs?asset=ARN-71B
        System.out.println("..........................");
        while (true) {
            List<Transaction> transactions = binanceCurrencyService.getBlockTransactions(++value);
            long long1 = value;
            transactions.forEach(transaction -> {
                if (transaction.getTxType() == TxType.TRANSFER &&
                        !binanceCurrencyService.getToken(transaction).equals("BNB")) {
                    System.out.print(transaction.getHeight() + "  ");
                    System.out.println(binanceCurrencyService.getToken(transaction));
                    System.out.println(binanceCurrencyService.getAmount(transaction));
                    System.out.println(binanceCurrencyService.binanceDexApiNodeClient.getBlockMetaByHeight(long1).getHeader().getHeight());
                    System.out.println(binanceCurrencyService.binanceDexApiNodeClient.getBlockMetaByHeight(long1).getBlockId().getHash());
                }
            });
        }

//        long value = 26077660L;
//        System.out.println("..........................");
//        while(true){
//            List<Transaction> transactions = binanceCurrencyService.getBlockTransactions(++value);
//
//            transactions.forEach(transaction -> {
//                if (transaction.getTxType() == TxType.TRANSFER){
//                    System.out.println(binanceCurrencyService.getToken(transaction));
//                }
//            if (binanceCurrencyService.binanceDexApiNodeClient.getBlockMetaByHeight(value).getHeader().getNumTxs()>0) {
//                System.out.println(binanceCurrencyService.binanceDexApiNodeClient.getBlockMetaByHeight(value).getHeader().getHeight());
//                System.out.println(binanceCurrencyService.binanceDexApiNodeClient.getBlockMetaByHeight(value).getHeader().getNumTxs());
//                break;
//            });
        }
//        System.out.println(binanceCurrencyService.binanceDexApiNodeClient.getNodeInfo().getSyncInfo().getLatestBlockHeight());
//        System.out.println(binanceCurrencyService.binanceDexApiNodeClient.getTransaction("DBA8BD55160F809FABF75D2E6164C55BF18059C5EF0B22F675D21717EEC26EC8").getRealTx().toString());
//        System.out.println("-="+ binanceCurrencyService.binanceDexApiNodeClient.getBlockMetaByHeight(6760515L)+"=-");
//        System.out.println(binanceCurrencyService.getBlockTransactions( 6760625L).size());
//        System.out.println("..........................");
//       }



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
