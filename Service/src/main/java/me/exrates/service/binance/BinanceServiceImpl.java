package me.exrates.service.binance;

import com.binance.dex.api.client.domain.broadcast.Transaction;
import com.binance.dex.api.client.domain.broadcast.TxType;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.model.dto.MerchantSpecParamDto;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.util.CryptoUtils;
import me.exrates.service.util.WithdrawUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
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
@PropertySource("classpath:/merchants/binance.properties")
public class BinanceServiceImpl implements BinanceService {

    private static final int CONFIRMATIONS = 40;
    private static final String LAST_BLOCK_PARAM = "LastScannedBlock";
    private static final String MERCHANT_NAME = "BNB";

    private Merchant merchant;
    private Currency currency;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Value("${binance.main.address}")
    private String mainAddress;

    @Autowired
    private CurrencyService currencyService;
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

    @PostConstruct
    public void init() {
        currency = currencyService.findByName(MERCHANT_NAME);
        merchant = merchantService.findByName(MERCHANT_NAME);
        scheduler.scheduleAtFixedRate(this::checkRefills, 5, 20, TimeUnit.MINUTES);
    }

    //TODO check method...
    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        String destinationTag = CryptoUtils.generateDestinationTag(request.getUserId(),
                9 + request.getUserId().toString().length(), currency.getName());
        String message = messageSource.getMessage("merchants.refill.xlm",
                new Object[]{mainAddress, destinationTag}, request.getLocale());
        return new HashMap<String, String>() {{
            put("address", destinationTag);
            put("message", message);
            put("qr", mainAddress);
        }};
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        String address = params.get("address");
        String hash = params.get("hash");
        BigDecimal amount = new BigDecimal(params.get("amount"));

        if (checkTransactionForDuplicate(hash)) {
            log.warn("*** binance *** transaction {} already accepted", hash);
            return;
        }

        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .address(address)
                .merchantId(merchant.getId())
                .currencyId(currency.getId())
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

    private boolean checkTransactionForDuplicate(String hash){
        return StringUtils.isEmpty(hash) || refillService.getRequestIdByMerchantIdAndCurrencyIdAndHash(merchant.getId(), currency.getId(), hash).isPresent();
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

                    Map<String, String> map = new HashMap<>();
                    map.put("address",binanceCurrencyService.getMemo(transaction));
                    map.put("hash",binanceCurrencyService.getHash(transaction));
                    map.put("amount",binanceCurrencyService.getAmount(transaction));

                    try {
                        processPayment(map);
                    } catch (RefillRequestAppropriateNotFoundException e) {
                        e.printStackTrace();
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
