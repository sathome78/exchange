package me.exrates.service.binance;

import com.binance.dex.api.client.domain.broadcast.Transaction;
import com.binance.dex.api.client.domain.broadcast.TxType;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.model.dto.MerchantSpecParamDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Log4j2
@Service
@Conditional(MonolitConditional.class)
public class BinanceServiceImpl implements BinanceService {

    private static final int CONFIRMATIONS = 20;
    private static final String LAST_BLOCK_PARAM = "LastScannedBlock";

    private static final String MERCHANT_NAME = "BNB";

    private String mainAddress;

    @Autowired
    private MerchantSpecParamsDao specParamsDao;

    @Autowired
    private BinanceCurrencyService binanceCurrencyService;


    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        return null;
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        return null;
    }

    @Override
    public boolean isValidDestinationAddress(String address) {
        return false;
    }




    private void checkRefills(){
        long lastblock = getLastBaseBlock();
        long blockchainHeight = getBlockchainHeigh();

        while (lastblock < blockchainHeight - CONFIRMATIONS){
            List<Transaction> transactions = binanceCurrencyService.getBlockTransactions(++lastblock);
            transactions.forEach(transaction -> {
                if (transaction.getTxType() == TxType.TRANSFER &&
                        binanceCurrencyService.getReceiverAddress(transaction).equalsIgnoreCase(mainAddress) &&
                        binanceCurrencyService.getToken(transaction).equalsIgnoreCase(MERCHANT_NAME)){

//                transaction.getTxType().
                }
            });




            if (lastblock % 500 == 0){
                saveLastBlock(lastblock);
            }
        }
    }





    private long getLastBaseBlock() {
        MerchantSpecParamDto specParamsDto = specParamsDao.getByMerchantNameAndParamName(MERCHANT_NAME, LAST_BLOCK_PARAM);
        return specParamsDto == null ? 0 : Long.valueOf(specParamsDto.getParamValue());
    }

    private long getBlockchainHeigh() {
        return 0;
    }

    private void saveLastBlock(long blockNum) {
        specParamsDao.updateParam(MERCHANT_NAME, LAST_BLOCK_PARAM, String.valueOf(blockNum));
    }
}
