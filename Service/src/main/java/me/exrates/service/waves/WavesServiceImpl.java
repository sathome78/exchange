package me.exrates.service.waves;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Currency;
import me.exrates.model.Email;
import me.exrates.model.Merchant;
import me.exrates.model.dto.*;
import me.exrates.model.dto.merchants.waves.WavesPayment;
import me.exrates.model.dto.merchants.waves.WavesTransaction;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.SendMailService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.exception.UnknownAssetIdException;
import me.exrates.service.exception.WavesPaymentProcessingException;
import me.exrates.service.exception.WavesRestException;
import me.exrates.service.exception.invoice.InsufficientCostsInWalletException;
import me.exrates.service.exception.invoice.InvalidAccountException;
import me.exrates.service.exception.invoice.MerchantException;
import me.exrates.service.util.ParamMapUtils;
import me.exrates.service.util.WithdrawUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;

@Log4j2(topic = "waves_log")
public class WavesServiceImpl implements WavesService {

    @Autowired
    private WavesRestClient restClient;

    @Autowired
    private RefillService refillService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private SendMailService sendMailService;

    @Autowired
    private WithdrawUtils withdrawUtils;

    private Integer minConfirmations;
    private String mainAccount;
    private String feeAccount;
    private String notifyEmail;
    private  final Locale notifyEmailLocale = new Locale("RU");

    private final int WAVES_AMOUNT_SCALE = 8;
    private final long WAVES_DEFAULT_FEE = 100000L;
    private final long TRANSIT_FEE_RESERVE = WAVES_DEFAULT_FEE * 10L;

    //IMPORTANT!! WAVES does not accept capital letters in attachments. lower case only!!!
    private final String FEE_TRANSFER_ATTACHMENT = "inner";


    private Map<String, MerchantCurrencyBasicInfoDto> tokenMerchantCurrencyMap;


    private String currencyBaseName;
    private String merchantBaseName;
    private String propertySource;

    private Currency currencyBase;
    private Merchant merchantBase;

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ExecutorService sendFeePool = Executors.newSingleThreadExecutor();


    public WavesServiceImpl(String currencyBaseName, String merchantBaseName, String propertySource) {
        this.currencyBaseName = currencyBaseName;
        this.merchantBaseName = merchantBaseName;
        this.propertySource = propertySource;
    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        String address = restClient.generateNewAddress();
        String message = messageSource.getMessage("merchants.refill.btc",
                new Object[]{address}, request.getLocale());
        return new HashMap<String, String>() {{
            put("message", message);
            put("address", address);
            put("qr", address);
        }};
    }

    @PostConstruct
    private void init() {
        Properties props = new Properties();
        try {
            props.load(getClass().getClassLoader().getResourceAsStream(propertySource));
            this.minConfirmations = Integer.parseInt(props.getProperty("waves.min.confirmations"));
            this.mainAccount = props.getProperty("waves.main.account");
            this.feeAccount = props.getProperty("waves.fee.account");
            this.notifyEmail = props.getProperty("waves.notify.email");
            restClient.init(props);
            initAssets(props);
            long processFixedDelay = Long.parseLong(props.getProperty("waves.process.delay"));

            scheduler.scheduleAtFixedRate(this::processWavesTransactionsForKnownAddresses, 3L, processFixedDelay, TimeUnit.MINUTES);

        } catch (Exception e) {
            log.error(e);
        }
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        String address = ParamMapUtils.getIfNotNull(params, "address");
        String txId = ParamMapUtils.getIfNotNull(params, "txId");
        int blockHeight = restClient.getCurrentBlockHeight();
        WavesTransaction wavesTransaction = restClient.getTransactionById(txId).orElseThrow(() ->
                new WavesPaymentProcessingException("Transaction not found"));
        if (!address.equals(wavesTransaction.getRecipient())) {
            throw new WavesPaymentProcessingException(String.format("Transaction with id %s has different recipient!", txId));
        }
        processWavesPayment(wavesTransaction, blockHeight);
    }

    private void processWavesPayment(WavesTransaction transaction, int lastBlockHeight) {
        log.debug("Processing tx: " + transaction);
        int merchantId;
        int currencyId;
        BigDecimal requestAmount;
        String assetId = transaction.getAssetId();
        if (assetId == null) {
            merchantId = merchantBase.getId();
            currencyId = currencyBase.getId();
            requestAmount = scaleFromLong(transaction.getAmount() - WAVES_DEFAULT_FEE, WAVES_AMOUNT_SCALE);
        } else {
            MerchantCurrencyBasicInfoDto assetInfo = tokenMerchantCurrencyMap.get(assetId);
            if (assetInfo == null) {
                throw new UnknownAssetIdException("Unknown asset: " + assetId);
            }
            merchantId = assetInfo.getMerchantId();
            currencyId = assetInfo.getCurrencyId();
            requestAmount = scaleFromLong(transaction.getAmount(), assetInfo.getRefillScale());
        }
        Optional<RefillRequestFlatDto> refillRequestResult =
                refillService.findFlatByAddressAndMerchantIdAndCurrencyIdAndHash(transaction.getRecipient(),
                        merchantId, currencyId, transaction.getId());
        int confirmations = lastBlockHeight - transaction.getHeight();

        if (!refillRequestResult.isPresent()) {
            Integer requestId = refillService.createRefillRequestByFact(RefillRequestAcceptDto.builder()
                    .address(transaction.getRecipient())
                    .amount(requestAmount)
                    .merchantId(merchantId)
                    .currencyId(currencyId)
                    .merchantTransactionId(transaction.getId()).build());
            if (confirmations >= 0 && confirmations < minConfirmations) {
                try {
                    log.debug("put on bch exam {}", transaction);
                    refillService.putOnBchExamRefillRequest(RefillRequestPutOnBchExamDto.builder()
                            .requestId(requestId)
                            .merchantId(merchantId)
                            .currencyId(currencyId)
                            .address(transaction.getRecipient())
                            .amount(requestAmount)
                            .hash(transaction.getId()).build());
                } catch (RefillRequestAppropriateNotFoundException e) {
                    log.error(e);
                }
            } else {
                changeConfirmationsOrProvide(RefillRequestSetConfirmationsNumberDto.builder()
                        .requestId(requestId)
                        .address(transaction.getRecipient())
                        .amount(requestAmount)
                        .confirmations(confirmations)
                        .currencyId(currencyId)
                        .merchantId(merchantId)
                        .hash(transaction.getId()).build());
            }
        } else {
            RefillRequestFlatDto flatDto = refillRequestResult.get();
            if (!flatDto.getStatus().isSuccessEndStatus()) {
                changeConfirmationsOrProvide(RefillRequestSetConfirmationsNumberDto.builder()
                        .requestId(refillRequestResult.get().getId())
                        .address(transaction.getRecipient())
                        .amount(requestAmount)
                        .confirmations(confirmations)
                        .currencyId(currencyId)
                        .merchantId(merchantId)
                        .hash(transaction.getId()).build());
            }

        }
    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        /*if (!"WAVES".equalsIgnoreCase(withdrawMerchantOperationDto.getCurrency())) {
            throw new WithdrawRequestPostException("Currency not supported by merchant");
        }*/
        try {
            String assetId = tokenMerchantCurrencyMap.entrySet().stream()
                    .filter(entry -> entry.getValue().getCurrencyName().equals(withdrawMerchantOperationDto.getCurrency()))
                    .map(Map.Entry::getKey).findFirst().orElse(null);

            String txId = sendTransaction(mainAccount, withdrawMerchantOperationDto.getAccountTo(),
                    new BigDecimal(withdrawMerchantOperationDto.getAmount()), assetId);
            return Collections.singletonMap("hash", txId);
        } catch (WavesRestException e) {
            if (e.getCode() == 112) {
                throw new InsufficientCostsInWalletException(e);
            }
            if (e.getCode() == 102) {
                throw new InvalidAccountException(e);
            }
            throw new MerchantException(e);
        } catch (Exception e) {
            throw new MerchantException(e);
        }
    }

    @Override
    public void processWavesTransactionsForKnownAddresses() {
        try {
            log.debug("Start checking {} transactions", currencyBaseName);

            int blockHeight = restClient.getCurrentBlockHeight();

            refillService.findAllAddresses(merchantBase.getId(), currencyBase.getId()).parallelStream()
                    .flatMap(address -> restClient.getTransactionsForAddress(address).stream()
                            .filter(transaction -> address.equals(transaction.getRecipient()) && !feeAccount.equals(transaction.getSender()))
                            .filter(wavesTransaction -> !FEE_TRANSFER_ATTACHMENT.equals(wavesTransaction.getAttachment())))
                    .map(transaction -> restClient.getTransactionById(transaction.getId()))
                    .filter(Optional::isPresent).map(Optional::get)
                    .forEach(transaction -> {
                        try {
                            processWavesPayment(transaction, blockHeight);
                        } catch (Exception e) {
                            log.error(e);
                        }
                    });
        } catch (Exception e) {
            log.error(e);
        }
    }

    private void changeConfirmationsOrProvide(RefillRequestSetConfirmationsNumberDto dto) {
        try {
            refillService.setConfirmationCollectedNumber(dto);
            if (dto.getConfirmations() >= minConfirmations) {
                String assetId = tokenMerchantCurrencyMap.entrySet().stream()
                        .filter(entry -> entry.getValue().getCurrencyId().equals(dto.getCurrencyId()) &&
                                entry.getValue().getMerchantId().equals(dto.getMerchantId()))
                        .map(Map.Entry::getKey).findFirst().orElse(null);
                if (assetId == null) {
                    sendAndProvideTransaction(dto, assetId);
                } else {
                    CompletableFuture.runAsync(() -> {
                        sendCommissionAddressAndWaitUntilConfirmed(dto.getAddress());
                    }).thenRun(() -> sendAndProvideTransaction(dto, assetId));
                }
            }
        } catch (Exception e) {
            log.error(e);
        }
    }

    private void sendAndProvideTransaction(RefillRequestSetConfirmationsNumberDto dto, String assetId) {
        try {
            sendTransaction(dto.getAddress(), mainAccount, dto.getAmount(), assetId);
            log.debug("Providing transaction!");
            RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.of(dto);
            refillService.autoAcceptRefillRequest(requestAcceptDto);
        } catch (Exception e) {
            log.error(e);
        }
    }


    private void sendCommissionAddressAndWaitUntilConfirmed(String transitAddress) {
        try {
            log.debug("Refill transit address {} with commission", transitAddress);
            String refillFeeTxId = refillUserAccountCommissionBalance(transitAddress);
            Optional<WavesTransaction> refillFeeTx;
            int numOfAttempts = 0;
            do {
                Thread.sleep(20_000);
                refillFeeTx = restClient.getTransactionById(refillFeeTxId);
                numOfAttempts++;
            } while (!refillFeeTx.isPresent() && numOfAttempts < 30);
        } catch (InsufficientCostsInWalletException e) {

            Email email = new Email();
            email.setTo(notifyEmail);
            email.setSubject(messageSource.getMessage("fee.wallet.insufficientCosts.title", null,
                    notifyEmailLocale));
            email.setMessage(messageSource.getMessage("fee.wallet.insufficientCosts.body", new Object[] {currencyBase.getName(),
                    feeAccount}, notifyEmailLocale));

            sendMailService.sendInfoMail(email);

        }  catch (Exception e) {
            log.error(e);
        }
    }


    private String sendTransaction(String senderAddress, String recipientAddress, BigDecimal amount, @Nullable String assetId) {
        int scale;
        if (assetId == null) {
            scale = WAVES_AMOUNT_SCALE;
        } else {
            MerchantCurrencyBasicInfoDto assetInfo = tokenMerchantCurrencyMap.get(assetId);
            if (assetInfo == null) {
                throw new UnknownAssetIdException("Unknown asset: " + assetId);
            }
            scale = assetInfo.getRefillScale();
        }

        WavesPayment payment = new WavesPayment();
        payment.setAssetId(assetId);
        payment.setSender(senderAddress);
        payment.setRecipient(recipientAddress);
        payment.setAmount(unscaleToLong(amount, scale));
        payment.setFee(WAVES_DEFAULT_FEE);
  //      payment.setFeeAssetId(assetId);
        return restClient.transferCosts(payment);
    }

    private BigDecimal scaleFromLong(Long unscaled, int scale) {
        return BigDecimal.valueOf(unscaled, scale);
    }

    private long unscaleToLong(BigDecimal scaledAmount, int scale) {
        return scaledAmount.scaleByPowerOfTen(scale).setScale(0, BigDecimal.ROUND_HALF_UP).longValueExact();
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
    }

    @Override
    public boolean isValidDestinationAddress(String address) {

        return withdrawUtils.isValidDestinationAddress(address);
    }


    void initAssets(Properties wavesProps) {
        currencyBase = currencyService.findByName(currencyBaseName);
        merchantBase = merchantService.findByName(merchantBaseName);
        List<MerchantCurrencyBasicInfoDto> tokenMerchants = merchantService.findTokenMerchantsByParentId(merchantBase.getId());
        Map<String, MerchantCurrencyBasicInfoDto> tokenMap = new HashMap<>();
        for (MerchantCurrencyBasicInfoDto tokenMerchant : tokenMerchants) {
            String assetId = wavesProps.getProperty(String.format("waves.token.%s.id", tokenMerchant.getMerchantName()));
            if (assetId != null) {
                tokenMap.put(assetId, tokenMerchant);
            }
        }
        this.tokenMerchantCurrencyMap = Collections.unmodifiableMap(tokenMap);
    }

    private String refillUserAccountCommissionBalance(String account) {
        try {
            WavesPayment payment = new WavesPayment();
            payment.setSender(feeAccount);
            payment.setRecipient(account);
            payment.setAmount(WAVES_DEFAULT_FEE);
            payment.setFee(WAVES_DEFAULT_FEE);
            payment.setAttachment(FEE_TRANSFER_ATTACHMENT);

            String txId = restClient.transferCosts(payment);
            log.debug("Fee costs transferred, tx id: " + txId);
            return txId;
        } catch (WavesRestException e) {
            if (e.getCode() == 112) {
                throw new InsufficientCostsInWalletException(e);
            }
            if (e.getCode() == 102) {
                throw new InvalidAccountException(e);
            }
            throw new MerchantException(e);
        } catch (Exception e) {
            throw new MerchantException(e);
        }
    }

    void setMinConfirmations(Integer minConfirmations) {
        this.minConfirmations = minConfirmations;
    }

    void setMainAccount(String mainAccount) {
        this.mainAccount = mainAccount;
    }

    void setFeeAccount(String feeAccount) {
        this.feeAccount = feeAccount;
    }

    void setNotifyEmail(String notifyEmail) {
        this.notifyEmail = notifyEmail;
    }

    /*
    Use to create encode wallet seed

    public static void main(String[] args) {

        System.out.println(Base58.encode("box armed repair shoot grid give slide eagle kite excess fruit earn hill one legal".getBytes(Charset.forName("UTF-8"))));
    }*/


}
