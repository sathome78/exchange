package me.exrates.service.lisk;

import com.mysql.jdbc.StringUtils;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.RefillRequestFlatDto;
import me.exrates.model.dto.RefillRequestPutOnBchExamDto;
import me.exrates.model.dto.RefillRequestSetConfirmationsNumberDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.model.dto.merchants.lisk.LiskAccount;
import me.exrates.model.dto.merchants.lisk.LiskTransaction;
import me.exrates.service.CurrencyService;
import me.exrates.service.GtagService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.LiskCreateAddressException;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.exception.WithdrawRequestPostException;
import me.exrates.service.util.ParamMapUtils;
import me.exrates.service.util.WithdrawUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bitcoinj.crypto.MnemonicCode;
import org.bitcoinj.crypto.MnemonicException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Conditional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Log4j2(topic = "lisk_log")
@Conditional(MonolitConditional.class)
public class LiskServiceImpl implements LiskService {

    private final BigDecimal DEFAULT_LSK_TX_FEE = BigDecimal.valueOf(0.1);

    @Autowired
    private RefillService refillService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private WithdrawUtils withdrawUtils;
    @Autowired
    private GtagService gtagService;

    private LiskRestClient liskRestClient;

    private LiskSpecialMethodService liskSpecialMethodService;

    private final String merchantName;
    private final String currencyName;
    private String propertySource;
    private String mainAddress;
    private String mainSecret;
    private Integer minConfirmations;

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();


    public LiskServiceImpl(LiskRestClient liskRestClient, LiskSpecialMethodService liskSpecialMethodService, String merchantName, String currencyName, String propertySource) {
        this.liskRestClient = liskRestClient;
        this.liskSpecialMethodService = liskSpecialMethodService;
        this.merchantName = merchantName;
        this.currencyName = currencyName;
        this.propertySource = propertySource;
        Properties props = new Properties();
        try {
            props.load(getClass().getClassLoader().getResourceAsStream(propertySource));
            this.mainAddress = props.getProperty("lisk.main.address");
            this.mainSecret = props.getProperty("lisk.main.secret");
            this.minConfirmations = Integer.parseInt(props.getProperty("lisk.min.confirmations"));

        } catch (IOException e) {
            log.error(e);
        }
    }

    @PostConstruct
    private void init() {
        liskRestClient.initClient(propertySource);
        scheduler.scheduleAtFixedRate(this::processTransactionsForKnownAddresses, 3L, 30L, TimeUnit.MINUTES);
    }

    @PreDestroy
    private void shutdown() {
        scheduler.shutdown();
    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        try {
            String secret = String.join(" ", MnemonicCode.INSTANCE.toMnemonic(SecureRandom.getSeed(16)));
            LiskAccount account = createNewLiskAccount(secret);
            String address = account.getAddress();

            String message = messageSource.getMessage("merchants.refill.btc",
                    new Object[]{address}, request.getLocale());
            Map<String, String> result = new HashMap<String, String>() {{
                put("message", message);
                put("address", address);
                put("pubKey", account.getPublicKey());
                put("brainPrivKey", secret);
                put("qr", address);
            }};
            return result;
        } catch (MnemonicException.MnemonicLengthException e) {
            throw new LiskCreateAddressException(e);
        }


    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        Optional<String> refillRequestIdResult = Optional.ofNullable(params.get("requestId"));
        Integer currencyId = Integer.parseInt(ParamMapUtils.getIfNotNull(params, "currencyId"));
        Integer merchantId = Integer.parseInt(ParamMapUtils.getIfNotNull(params, "merchantId"));
        String address = ParamMapUtils.getIfNotNull(params, "address");
        String txId = ParamMapUtils.getIfNotNull(params, "txId");
        LiskTransaction transaction = getTransactionById(txId);
        long txFee = liskRestClient.getFee();
        BigDecimal scaledAmount = LiskTransaction.scaleAmount(transaction.getAmount() - txFee);

        if (!refillRequestIdResult.isPresent()) {
            Integer requestId = refillService.createRefillRequestByFact(RefillRequestAcceptDto.builder()
                    .address(address)
                    .amount(scaledAmount)
                    .merchantId(merchantId)
                    .currencyId(currencyId)
                    .merchantTransactionId(txId).build());
            if (transaction.getConfirmations() >= 0 && transaction.getConfirmations() < minConfirmations) {
                try {
                    log.debug("put on bch exam {}", transaction);
                    refillService.putOnBchExamRefillRequest(RefillRequestPutOnBchExamDto.builder()
                            .requestId(requestId)
                            .merchantId(merchantId)
                            .currencyId(currencyId)
                            .address(address)
                            .amount(scaledAmount)
                            .hash(txId)
                            .blockhash(transaction.getBlockId()).build());
                } catch (RefillRequestAppropriateNotFoundException e) {
                    log.error(e);
                }
            } else {
                changeConfirmationsOrProvide(RefillRequestSetConfirmationsNumberDto.builder()
                        .requestId(requestId)
                        .address(address)
                        .amount(scaledAmount)
                        .confirmations(transaction.getConfirmations())
                        .currencyId(currencyId)
                        .merchantId(merchantId)
                        .hash(txId)
                        .blockhash(transaction.getBlockId()).build());
            }
        } else {
            Integer requestId = Integer.parseInt(refillRequestIdResult.get());
            changeConfirmationsOrProvide(RefillRequestSetConfirmationsNumberDto.builder()
                    .requestId(requestId)
                    .address(address)
                    .amount(scaledAmount)
                    .confirmations(transaction.getConfirmations())
                    .currencyId(currencyId)
                    .merchantId(merchantId)
                    .hash(txId)
                    .blockhash(transaction.getBlockId()).build());
        }
    }

    private void changeConfirmationsOrProvide(RefillRequestSetConfirmationsNumberDto dto) {
        try {
            refillService.setConfirmationCollectedNumber(dto);
            if (dto.getConfirmations() >= minConfirmations) {
                log.debug("Providing transaction!");
                Integer requestId = dto.getRequestId();

                RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                        .address(dto.getAddress())
                        .amount(dto.getAmount())
                        .currencyId(dto.getCurrencyId())
                        .merchantId(dto.getMerchantId())
                        .merchantTransactionId(dto.getHash())
                        .build();

                if (Objects.isNull(requestId)) {
                    requestId = refillService.getRequestId(requestAcceptDto);
                }
                requestAcceptDto.setRequestId(requestId);

                refillService.autoAcceptRefillRequest(requestAcceptDto);
                RefillRequestFlatDto flatDto = refillService.getFlatById(requestId);
                sendTransaction(flatDto.getBrainPrivKey(), dto.getAmount(), mainAddress);

                final String gaTag = refillService.getUserGAByRequestId(requestId);
                log.debug("Process of sending data to Google Analytics...");
                gtagService.sendGtagEvents(requestAcceptDto.getAmount().toString(), currencyName, gaTag);
            }
        } catch (RefillRequestAppropriateNotFoundException e) {
            log.error(e);
        }
    }


    @Override
    public void processTransactionsForKnownAddresses() {
        log.info("Start checking {} transactions", currencyName);
        Currency currency = currencyService.findByName(currencyName);
        Merchant merchant = merchantService.findByName(merchantName);
        refillService.findAllAddresses(merchant.getId(), currency.getId()).forEach(address -> {
            try {
                int offset = refillService.getTxOffsetForAddress(address);
                List<LiskTransaction> userTransactions = liskRestClient.getAllTransactionsByRecipient(address, offset);
                log.debug("Address {}, Transactions found: {}", address, userTransactions);
                boolean containsUnconfirmedTransactions = false;
                int newOffset = offset;
                for (LiskTransaction transaction : userTransactions) {
                    Optional<RefillRequestFlatDto> refillRequestResult = refillService.findFlatByAddressAndMerchantIdAndCurrencyIdAndHash(transaction.getRecipientId(),
                            merchant.getId(), currency.getId(), transaction.getId());
                    if ((refillRequestResult.isPresent() && refillRequestResult.get().getStatus().isSuccessEndStatus())) {
                        if (!containsUnconfirmedTransactions) {
                            newOffset++;
                        }
                    } else {
                        if (!containsUnconfirmedTransactions) {
                            containsUnconfirmedTransactions = true;
                        }
                        Map<String, String> params = new HashMap<String, String>() {{
                            put("merchantId", String.valueOf(merchant.getId()));
                            put("currencyId", String.valueOf(currency.getId()));
                            put("address", transaction.getRecipientId());
                            put("txId", transaction.getId());
                        }};
                        refillRequestResult.ifPresent(request -> params.put("requestId", String.valueOf(request.getId())));

                        try {
                            processPayment(params);
                        } catch (RefillRequestAppropriateNotFoundException e) {
                            log.error(e);
                        }
                    }
                }
                if (newOffset != offset) {
                    refillService.updateTxOffsetForAddress(address, newOffset);
                }
            } catch (Exception e) {
                log.error("Exception for currency {} merchant {}: {}", currencyName, merchantName, ExceptionUtils.getStackTrace(e));
            }
        });
    }


    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        if (!"LSK".equalsIgnoreCase(withdrawMerchantOperationDto.getCurrency())) {
            throw new WithdrawRequestPostException("Currency not supported by merchant");
        }
        BigDecimal txFee = LiskTransaction.scaleAmount(liskRestClient.getFee());
        if (StringUtils.isEmptyOrWhitespaceOnly(mainSecret)) {
            throw new WithdrawRequestPostException("Main secret not defined");
        }
        String txId = sendTransaction(mainSecret, new BigDecimal(withdrawMerchantOperationDto.getAmount()).subtract(txFee),
                withdrawMerchantOperationDto.getAccountTo());
        return Collections.singletonMap("hash", txId);
    }

    @Override
    public LiskTransaction getTransactionById(String txId) {
        return liskRestClient.getTransactionById(txId);
    }

    @Override
    public List<LiskTransaction> getTransactionsByRecipient(String recipientAddress) {
        return liskRestClient.getTransactionsByRecipient(recipientAddress);
    }

    @Override
    public String sendTransaction(String secret, Long amount, String recipientId) {
        return liskSpecialMethodService.sendTransaction(secret, amount, recipientId);
    }

    @Override
    public String sendTransaction(String secret, BigDecimal amount, String recipientId) {
        return sendTransaction(secret, LiskTransaction.unscaleAmountToLiskFormat(amount), recipientId);
    }


    @Override
    public LiskAccount createNewLiskAccount(String secret) {
        return liskSpecialMethodService.createAccount(secret);
    }

    @Override
    public LiskAccount getAccountByAddress(String address) {
        return liskRestClient.getAccountByAddress(address);
    }


    @Override
    public boolean isValidDestinationAddress(String address) {

        return withdrawUtils.isValidDestinationAddress(address);
    }

}
