package me.exrates.service.zil;

import com.firestack.laksaj.exception.ZilliqaAPIException;
import com.firestack.laksaj.jsonrpc.HttpProvider;
import com.firestack.laksaj.jsonrpc.Rep;
import com.firestack.laksaj.transaction.Transaction;
import com.firestack.laksaj.utils.Bech32;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.model.dto.MerchantSpecParamDto;
import me.exrates.model.dto.RefillRequestAddressDto;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
@Conditional(MonolitConditional.class)
public class ZilRecieveService {

    private static HttpProvider client;

    private static final String MERCHANT_NAME = "ZIL";
    private static final String LAST_BLOCK_PARAM = "LastScannedBlock";
    private static final int CONFIRMATIONS = 3;


    @Autowired
    private MerchantSpecParamsDao specParamsDao;
    @Autowired
    private RefillService refillService;
    @Autowired
    private ZilService zilService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private ZilCurrencyService zilCurrencyService;

    private Merchant merchant;
    private Currency currency;

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @PostConstruct
    private void init(){
        client = new HttpProvider("https://api.zilliqa.com/");
        currency = currencyService.findByName(MERCHANT_NAME);
        merchant = merchantService.findByName(MERCHANT_NAME);
        scheduler.scheduleAtFixedRate(this::checkRefills, 3, 3, TimeUnit.MINUTES);
    }

    private void checkRefills() {
        long lastblock = getLastBaseBlock();
        long blockchainHeight = 0;
        try {
            blockchainHeight = getBlockchainHeigh();
        } catch (IOException e) {
            log.error(e);
        } catch (ZilliqaAPIException e) {
            log.error(e);
        }
        List<String> listOfAddress = refillService.getListOfValidAddressByMerchantIdAndCurrency(merchant.getId(), currency.getId());

        while (lastblock < blockchainHeight - CONFIRMATIONS){
            List<List<String>> transactions = getTransactionsList(++lastblock);
            if (transactions != null){
                transactions.forEach(list -> {
                    if (list != null){
                        for (String hash: list) {
                            try {
                                Transaction transaction = client.getTransaction(hash).getResult();
                                String address = Bech32.toBech32Address(transaction.getToAddr());

                                if (listOfAddress.contains(address)){
                                    processTransaction(transaction);
                                }
                            } catch (Exception e) {
                                log.error(e);
                            }
                        }
                    }
                });
            }
        }
        saveLastBlock(lastblock);

        transferToMainAccountJob();
    }

    private long getLastBaseBlock(){
        MerchantSpecParamDto specParamsDto = specParamsDao.getByMerchantNameAndParamName(MERCHANT_NAME, LAST_BLOCK_PARAM);
        return specParamsDto == null ? 0 : Long.valueOf(specParamsDto.getParamValue());
    }

    private int getBlockchainHeigh() throws IOException, ZilliqaAPIException {
        Rep<String> numTxBlocks = client.getNumTxBlocks();
        return Integer.valueOf(numTxBlocks.getResult());
    }

    private List<List<String>> getTransactionsList(long block) {
        Rep<List<List<String>>> blockTransactions = null;
        try {
            blockTransactions = client.getTransactionsForTxBlock(String.valueOf(block));
        } catch (IOException e) {
            log.error(e);
        }
        return blockTransactions.getResult();
    }

    private void processTransaction(Transaction transaction) throws Exception {
        Map<String, String> param = new HashMap<>();
        param.put("address", Bech32.toBech32Address(transaction.getToAddr()));
        param.put("hash", transaction.getID());
        param.put("amount", transaction.getAmount());

        zilService.processPayment(param);
        refillService.updateAddressNeedTransfer(Bech32.toBech32Address(transaction.getToAddr()), merchant.getId(), currency.getId(), true);
    }

    private void saveLastBlock(long lastBlock){
        specParamsDao.updateParam(MERCHANT_NAME, LAST_BLOCK_PARAM, String.valueOf(lastBlock));
    }

    private void transferToMainAccountJob(){
        List<RefillRequestAddressDto> listRefillRequestAddressDto = refillService.findAllAddressesNeededToTransfer(merchant.getId(), currency.getId());
        listRefillRequestAddressDto.forEach(p->{
            try {
                BigDecimal amount = zilCurrencyService.getAmount(Bech32.fromBech32Address(p.getAddress()));
                if (amount.toString().equals("0")){
                    refillService.updateAddressNeedTransfer(p.getAddress(), merchant.getId(), currency.getId(), false);
                } else {
                    transferToMainAccount(p);
                }
            } catch (Exception e) {
                log.error(e);
            }
        });
    }

    private void transferToMainAccount(RefillRequestAddressDto dto) throws Exception {
        zilCurrencyService.createTransaction(dto);
    }
}
