package me.exrates.service.binance;

import com.binance.dex.api.client.domain.broadcast.Transaction;
import com.binance.dex.api.client.domain.broadcast.TxType;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.Merchant;
import me.exrates.model.dto.MerchantSpecParamDto;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.util.WithdrawUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Log4j2
public class BinanceServiceImpl implements BinanceService {

    private static final String LAST_BLOCK_PARAM = "LastScannedBlock";

    private String merchantName;
    private int confirmations;
    private static Map<String, BinTokenServiceImpl> tokenMap = new HashMap<String, BinTokenServiceImpl>(){{
        put("BNB", new BinTokenServiceImpl("merchants/binance.properties", "BinanceCoin","BNB"));
        put("ARN-71B", new BinTokenServiceImpl("merchants/binance.properties", "ARN","ARN"));
    }};

    private Merchant merchant;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private String mainAddress;

    @Autowired
    private MerchantService merchantService;
    @Autowired
    private RefillService refillService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private WithdrawUtils withdrawUtils;

    @Autowired
    private MerchantSpecParamsDao specParamsDao;

    @Autowired
    private BinanceCurrencyService binanceCurrencyService;

    public BinanceServiceImpl(String propertySource, String merchantName, int confirmations){
        Properties props = new Properties();

        try {
            props.load(getClass().getClassLoader().getResourceAsStream(propertySource));
            this.mainAddress = props.getProperty("binance.main.address");
        } catch (IOException e) {
            log.error(e);
        }
        this.merchantName = merchantName;
        this.confirmations = confirmations;
    }

    @PostConstruct
    public void init() {
        merchant = merchantService.findByName(merchantName);
        scheduler.scheduleAtFixedRate(this::checkRefills, 1, 1, TimeUnit.MINUTES);
    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        return null;
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        String address = params.get("address");
        String hash = params.get("hash");
        String merchantId = params.get("merchantId");
        String currencyId = params.get("currencyId");
        BigDecimal amount = new BigDecimal(params.get("amount"));

        if (checkTransactionForDuplicate(hash, Integer.valueOf(merchantId), Integer.valueOf(currencyId))) {
            log.warn("*** binance *** transaction {} already accepted", hash);
            return;
        }

        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .address(address)
                .merchantId(Integer.valueOf(merchantId))
                .currencyId(Integer.valueOf(currencyId))
                .amount(amount)
                .merchantTransactionId(hash)
                .toMainAccountTransferringConfirmNeeded(this.toMainAccountTransferringConfirmNeeded())
                .build();

        refillService.createAndAutoAcceptRefillRequest(requestAcceptDto);
    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        throw new RuntimeException("not supported");
    }

    @Override
    public boolean isValidDestinationAddress(String address) {
        return withdrawUtils.isValidDestinationAddress(address);
    }

    private boolean checkTransactionForDuplicate(String hash, Integer merchantId, Integer currencyId){
        return StringUtils.isEmpty(hash) || refillService.getRequestIdByMerchantIdAndCurrencyIdAndHash(merchantId, currencyId, hash).isPresent();
    }

    private void checkRefills(){
        long lastblock = getLastBaseBlock();
        long blockchainHeight = getBlockchainHeigh();

        while (lastblock < blockchainHeight - confirmations){
            List<Transaction> transactions = binanceCurrencyService.getBlockTransactions(++lastblock);
            transactions.forEach(transaction -> {
                if (transaction.getTxType() == TxType.TRANSFER &&
                        binanceCurrencyService.getReceiverAddress(transaction).equalsIgnoreCase(mainAddress) &&
                        tokenMap.containsKey(binanceCurrencyService.getToken(transaction))){

                    BinTokenServiceImpl binTokenService = tokenMap.get(binanceCurrencyService.getToken(transaction));

                    Map<String, String> map = new HashMap<>();
                    map.put("address",binanceCurrencyService.getMemo(transaction));
                    map.put("hash",binanceCurrencyService.getHash(transaction));
                    map.put("amount",binanceCurrencyService.getAmount(transaction));
                    map.put("merchantId", String.valueOf(binTokenService.getMerchant().getId()));
                    map.put("currencyId", String.valueOf(binTokenService.getCurrency().getId()));

                    try {
                        processPayment(map);
                    } catch (RefillRequestAppropriateNotFoundException e) {
                        log.error(e);
                    }
                }
            });
            if (lastblock % 500 == 0){
                saveLastBlock(lastblock);
            }
        }
        saveLastBlock(lastblock);
    }

    private long getLastBaseBlock() {
        MerchantSpecParamDto specParamsDto = specParamsDao.getByMerchantNameAndParamName(merchantName, LAST_BLOCK_PARAM);
        return specParamsDto == null ? 0 : Long.valueOf(specParamsDto.getParamValue());
    }

    private long getBlockchainHeigh() {
        return binanceCurrencyService.getBlockchainHeigh();
    }

    private void saveLastBlock(long blockNum) {
        specParamsDao.updateParam(merchantName, LAST_BLOCK_PARAM, String.valueOf(blockNum));
    }
}