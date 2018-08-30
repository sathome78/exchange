package me.exrates.service.ethereum;

import lombok.Synchronized;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.dto.*;
import me.exrates.model.enums.OperationType;
import me.exrates.model.util.AtomicBigInteger;
import me.exrates.service.*;
import me.exrates.service.ethereum.ethTokensWrappers.EthToken;
import me.exrates.service.exception.*;
import me.exrates.service.exception.invoice.InsufficientCostsInWalletException;
import me.exrates.service.exception.invoice.InvalidAccountException;
import me.exrates.service.exception.invoice.MerchantException;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;
import rx.Observable;
import rx.Subscription;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by ajet
 */
//@Service
public class EthereumCommonServiceImpl implements EthereumCommonService {

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private RefillService refillService;

    @Autowired
    private MerchantSpecParamsDao specParamsDao;

    @Autowired
    private EthTokensContext ethTokensContext;

    @Autowired
    private WithdrawService withdrawService;

    private String url;

    private String withdrawNodeUrl;

    private String destinationDir;

    private String password;

    private String mainAddress;

    private final Set<String> accounts = new HashSet<>();

    private final Set<String> pendingTransactions = new HashSet<>();

    private Web3j web3j;

    private Web3j web3jForEthWithdr;

    private Web3j web3jForTokensWithdr;

    private Observable<org.web3j.protocol.core.methods.response.Transaction> observable;

    private Subscription subscription;

    private boolean subscribeCreated = false;

    private BigInteger currentBlockNumber;

    private String merchantName;

    private String currencyName;

    private Integer minConfirmations;

    private Credentials credentialsMain;

    private Credentials credentialsWithdrawAcc;

    private String transferAccAddress;

    private String transferAccPrivateKey;

    private String transferAccPublicKey;

    private String withdrawAccAddress;

    private String withdrawAccPrivateKey;

    private String withdrawAccPublicKey;

    private BigDecimal minBalanceForTransfer;

    private int merchantId;

    private boolean needToCheckTokens = false;

    private BigDecimal minSumOnAccount;

    private Logger log;

    private BigDecimal ethComissionPrice = new BigDecimal(0.0003);
    private BigDecimal ethComissionTokeWithdraw = new BigDecimal(0.003);

    private static final BigInteger ETH_TANSFER_GAS = BigInteger.valueOf(31000);
    private static final BigInteger TOKENS_TANSFER_GAS = BigInteger.valueOf(300000);


    @Override
    public Web3j getWeb3j() {
        return web3j;
    }

    @Override
    public Set<String> getAccounts() {
        return accounts;
    }

    @Override
    public String getMainAddress() {
        return mainAddress;
    }

    @Override
    public String getWithdrawAddress() {
        return withdrawAccAddress;
    }

    @Override
    public Credentials getCredentialsMain() {
        return credentialsMain;
    }

    @Override
    public Integer minConfirmationsRefill() {
        return minConfirmations;
    }

    @Override
    public String getTransferAccAddress() {
        return transferAccAddress;
    }

    @Override
    public Boolean withdrawTransferringConfirmNeeded() {
        return true;
    }

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    private final ScheduledExecutorService checkerScheduler = Executors.newScheduledThreadPool(1);

    private static final String LAST_BLOCK_PARAM = "LastRecievedBlock";

    private final Object ethSynchronizer = new Object();
    private final Object tokensSynchronizer = new Object();

    private AtomicBigInteger lastNonce = new AtomicBigInteger(BigInteger.ZERO);

    private Semaphore tokensWithdrawSemaphore = new Semaphore(2, true);
    private Semaphore ethWithdrawSemaphore = new Semaphore(2, true);

    public EthereumCommonServiceImpl(String propertySource, String merchantName, String currencyName, Integer minConfirmations) {
        Properties props = new Properties();
        try {
            props.load(getClass().getClassLoader().getResourceAsStream(propertySource));
            this.url = props.getProperty("ethereum.url");
            this.destinationDir = props.getProperty("ethereum.destinationDir");
            this.password = props.getProperty("ethereum.password");
            this.mainAddress = props.getProperty("ethereum.mainAddress");
            this.minSumOnAccount = new BigDecimal(props.getProperty("ethereum.minSumOnAccount"));
            this.minBalanceForTransfer = new BigDecimal(props.getProperty("ethereum.minBalanceForTransfer"));
            this.merchantName = merchantName;
            this.currencyName = currencyName;
            this.minConfirmations = minConfirmations;
            this.log = LogManager.getLogger(props.getProperty("ethereum.log"));
            if (merchantName.equals("Ethereum")){
                this.withdrawNodeUrl = props.getProperty("ethereum.withdraw.url");
                this.transferAccAddress = props.getProperty("ethereum.transferAccAddress");
                this.transferAccPrivateKey = props.getProperty("ethereum.transferAccPrivateKey");
                this.transferAccPublicKey = props.getProperty("ethereum.transferAccPublicKey");
                this.needToCheckTokens = true;
                File initialFile = new File(props.getProperty("ethereum.withdrawAccPropsPath"));
                Properties withdrProps = new Properties();
                withdrProps.load(new FileInputStream(initialFile));
                this.withdrawAccAddress = withdrProps.getProperty("ethereum.withdrawAccAddress");
                this.withdrawAccPrivateKey = withdrProps.getProperty("ethereum.withdrawAccPrivateKey");
                this.withdrawAccPublicKey = withdrProps.getProperty("ethereum.withdrawAccPublicKey");
                credentialsWithdrawAcc = Credentials.create(new ECKeyPair(new BigInteger(withdrawAccPrivateKey),
                        new BigInteger(withdrawAccPublicKey)));
            }
        } catch (IOException e) {
            log.error(e);
        }
    }

    @PostConstruct
    void start() {
        merchantId = merchantService.findByName(merchantName).getId();

        log.info("start " + merchantName);

        web3j = Web3j.build(new HttpService(url));
        web3jForEthWithdr = Web3j.build(new HttpService(withdrawNodeUrl));
        web3jForTokensWithdr = Web3j.build(new HttpService(withdrawNodeUrl));


        scheduler.scheduleAtFixedRate(new Runnable() {
            public void run() {
                checkSession();
            }
        }, 1, 8, TimeUnit.MINUTES);

        scheduler.scheduleWithFixedDelay(() -> {
            try {
                transferFundsToMainAccount();
            }catch (Exception e){
                log.error(e);
            }
        }, 4, 20, TimeUnit.MINUTES);

        scheduler.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                try {
                    if (subscribeCreated == true) {
                       saveLastBlock(currentBlockNumber.toString());
                    }
                }catch (Exception e){
                    log.error(e);
                }
            }
        }, 1, 24, TimeUnit.HOURS);

        checkerScheduler.scheduleWithFixedDelay(() -> {
            if (needToCheckTokens) {
                checkUnconfirmedTokensTransactions(currentBlockNumber);
            }
        }, 5, 5, TimeUnit.MINUTES);
    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) {
        if (!WalletUtils.isValidAddress(withdrawMerchantOperationDto.getAccountTo())) {
            throw new InvalidAccountException(withdrawMerchantOperationDto.getAccountTo());
        }
        if (withdrawMerchantOperationDto.getCurrency().equalsIgnoreCase("ETH")) {
            if (ethWithdrawSemaphore.tryAcquire()) {
                try {
                    withdrawEth(withdrawMerchantOperationDto);
                } catch (Exception e){
                    ethWithdrawSemaphore.release();
                    throw e;
                }
                return Collections.emptyMap();
            } else {
                throw new RuntimeException("withdraw queue is busy");
            }
        } else {
            Currency currency = currencyService.findByName(withdrawMerchantOperationDto.getCurrency());
            EthTokenService tokenService = ethTokensContext
                    .getByCurrencyId(currency.getId());
            List<MerchantCurrency> merchant = merchantService
                    .getAllUnblockedForOperationTypeByCurrencies(Collections.singletonList(currency.getId()), OperationType.OUTPUT);
            if (tokenService != null && merchant.size() == 1) {
                if (tokensWithdrawSemaphore.tryAcquire()) {
                    EthTokenWithdrawInfoDto dto = EthTokenWithdrawInfoDto.builder()
                            .withdrawMerchantOperationDto(withdrawMerchantOperationDto)
                            .tokenService(tokenService)
                            .merchantName(merchant.get(0).getName())
                            .build();
                        try {
                            withdrawTokens(dto.getWithdrawMerchantOperationDto(),
                                    (EthTokenService) dto.getTokenService(), dto.getMerchantName());
                        } catch (Exception e){
                            tokensWithdrawSemaphore.release();
                            throw e;
                        }
                    return Collections.emptyMap();
                } else {
                    throw new RuntimeException("withdraw queue is busy");
                }
            }
        }
        throw new WithdrawRequestPostException("Currency not supported by merchant " + withdrawMerchantOperationDto.getCurrency());
    }


    @Synchronized(value = "ethSynchronizer")
    private void withdrawEth(WithdrawMerchantOperationDto withdrawMerchantOperationDto) {
        BigDecimal ethBalance = null;
        String hexValue;
        try {
            ethBalance = Convert.fromWei(String.valueOf(web3jForEthWithdr.ethGetBalance(withdrawAccAddress, DefaultBlockParameterName.LATEST).send().getBalance()), Convert.Unit.ETHER);
        } catch (IOException e) {
            log.error("error checking eth balance");
            throw new RuntimeException("error checking balance");
        }
        BigDecimal withdrawAmount = new BigDecimal(withdrawMerchantOperationDto.getAmount());
        if (ethBalance.compareTo(withdrawAmount.add(ethComissionPrice)) < 0) {
            throw new InsufficientCostsInWalletException("ETH BALANCE LOW");
        }
        try {
            log.info("try autowithdraw {}", withdrawMerchantOperationDto);
            BigInteger gasPrice = web3jForEthWithdr.ethGasPrice().send().getGasPrice().multiply(BigInteger.valueOf(2));
            BigInteger gasLimit = ETH_TANSFER_GAS;
            BigInteger nonce = resolveNonce();
            BigInteger amount = ExConvert.toWei(withdrawMerchantOperationDto.getAmount(), ExConvert.Unit.ETHER).toBigInteger();
            log.info("amount {}, nonce {}, gas {} ", amount, nonce, gasLimit);
            RawTransaction rawTransaction  = RawTransaction.createEtherTransaction(
                    nonce, gasPrice, gasLimit, withdrawMerchantOperationDto.getAccountTo(), amount);
            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentialsWithdrawAcc);
            hexValue = Numeric.toHexString(signedMessage);
        } catch (Exception e) {
            log.error("error sending tx {}", e);
            throw new RuntimeException(e);
        }
        sendWithdraw(web3jForEthWithdr, hexValue, withdrawMerchantOperationDto.getRequestId(), ethWithdrawSemaphore);
    }


    @Synchronized(value = "tokensSynchronizer")
    private void withdrawTokens(WithdrawMerchantOperationDto withdrawMerchantOperationDto,
                                                   EthTokenService tokenService, String merchantName) {
        BigDecimal withdarwAmount = null;
        EthToken contract = null;
        BigDecimal balance = null;
        BigDecimal ethBalance = null;
        BigInteger GAS_PRICE;
        String hexValue;
        try {
            GAS_PRICE = web3jForEthWithdr.ethGasPrice().send().getGasPrice();
            Class clazz = Class.forName("me.exrates.service.ethereum.ethTokensWrappers." + merchantName);
            Method method = clazz.getMethod("load", String.class, Web3j.class, Credentials.class, BigInteger.class, BigInteger.class);
            withdarwAmount = new BigDecimal(withdrawMerchantOperationDto.getAmount());
            contract = (EthToken)method.invoke(null, tokenService.getContractAddress().get(0),
                    web3jForEthWithdr, credentialsWithdrawAcc, GAS_PRICE, TOKENS_TANSFER_GAS);
            balance = ExConvert.fromWei(contract.balanceOf(withdrawAccAddress).send().toString(), tokenService.getUnit());
            ethBalance = Convert.fromWei(String.valueOf(web3jForEthWithdr.ethGetBalance(withdrawAccAddress, DefaultBlockParameterName.LATEST).send().getBalance()), Convert.Unit.ETHER);
            log.info("balance {}, eth balance {}", balance, ethBalance);
        } catch (Exception e) {
            log.error("transfer token error {}" , e);
            throw new RuntimeException(e);
        }
        if (ethBalance.compareTo(ethComissionTokeWithdraw) <= 0 || balance.compareTo(withdarwAmount) < 0) {
            throw new InsufficientCostsInWalletException("ETH BALANCE LOW for withdraw " + merchantName);
        }
        try {
            BigInteger convertedAmount = ExConvert.toWei(withdarwAmount, tokenService.getUnit()).toBigInteger();
            Function function = new Function(
                    "transfer",  // function we're calling
                    Arrays.asList(new Address(withdrawMerchantOperationDto.getAccountTo()), new Uint(convertedAmount)),
                    Arrays.asList(new TypeReference<Bool>(){}));
            String encodedFunction = FunctionEncoder.encode(function);
            RawTransaction transaction = RawTransaction.createTransaction(resolveNonce(), GAS_PRICE, TOKENS_TANSFER_GAS,
                     tokenService.getContractAddress().get(0), encodedFunction);
            byte[] signedMessage = TransactionEncoder.signMessage(transaction, credentialsWithdrawAcc);
            hexValue = Numeric.toHexString(signedMessage);
        } catch (Exception e) {
            log.error("error sending tx {}", e);
            throw new RuntimeException(e);
        }
        sendWithdraw(web3jForEthWithdr, hexValue, withdrawMerchantOperationDto.getRequestId(), tokensWithdrawSemaphore);
    }

    private void sendWithdraw(Web3j web3j, String hex, int withdrawId, Semaphore semaphore) {
        System.out.println("sending withdraw");
        try {
            web3j.ethSendRawTransaction(hex).sendAsync().handleAsync((result, ex) -> {
                try {
                    processWithdrawResult(result, ex, withdrawId);
                    withdrawService.finalizePostWithdrawalRequest(withdrawId);
                    Thread.sleep(10000);
                } catch (InterruptedException ignore) {
                }
                catch (Exception e) {
                    withdrawService.rejectToReview(withdrawId);
                }
                finally {
                    semaphore.release();
                }
                return result;
            });
        } catch (Exception e) {
            log.error(e);
            throw new MerchantException(e);
        }
    }

    private void processWithdrawResult(EthSendTransaction result, Throwable ex, int withdrawRequstId) {
        log.info("response {} {}", result.getTransactionHash(), result.getRawResponse());
        if (ex != null || result.hasError() || StringUtils.isEmpty(result.getTransactionHash().trim())) {
            if (result.getError() != null) {
                log.error(result.getError().getMessage());
            }
            throw new MerchantException();
        } else {
            withdrawService.setHash(withdrawRequstId, result.getTransactionHash());
            waitForTxReceipt(result.getTransactionHash());
        }
    }

    @Synchronized
    private BigInteger resolveNonce() throws IOException {
        EthGetTransactionCount ethGetTransactionCount = web3jForEthWithdr.ethGetTransactionCount(
                credentialsWithdrawAcc.getAddress(), DefaultBlockParameterName.LATEST).send();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        if (nonce.compareTo(lastNonce.get()) < 0) {
            nonce = lastNonce.incrementAndGet();
        }
        lastNonce = new AtomicBigInteger(nonce);
        log.info("nonce {}", nonce);
        return nonce;
    }

    private void waitForTxReceipt(String hash) {
        TransactionReceipt receipt = null;
        Instant start = Instant.now();
        do {
            if (Duration.between(start, Instant.now()).compareTo(Duration.ofMinutes(7)) > 0) {
                throw new RuntimeException("timeout execution");
            }
            try {
                Thread.sleep(5000);
                receipt = web3jForTokensWithdr.ethGetTransactionReceipt(hash).send().getTransactionReceipt().get();
            } catch (Exception ignored) {
            }
        } while (receipt == null);
        log.info("status {}", receipt.getStatus());
        log.info("tx {}", receipt);
        if (!receipt.getStatus().equals("0x1")) {
            throw new MerchantException();
        }
    }

    @Override
    public BigDecimal countSpecCommission(BigDecimal amount, String destinationTag, Integer merchantId, Integer currencyId) {
        MerchantCurrency merchantCurrency = merchantService.findByMerchantAndCurrency(merchantId, currencyId).orElseThrow(()->new MerchantNotFoundException(merchantId.toString()));
        return merchantCurrency.getOutputCommission();
    }


    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        throw new NotImplimentedMethod("for " + params);
    }

    public void createSubscribe(){
        try {
            log.debug(merchantName + " Connecting ethereum...");

            Merchant merchant = merchantService.findByName(merchantName);
            Currency currency = currencyService.findByName(currencyName);
            if (merchantName.equals("Ethereum")) {
                credentialsMain = Credentials.create(new ECKeyPair(new BigInteger(transferAccPrivateKey),
                        new BigInteger(transferAccPublicKey)));
            }

            refillService.findAllAddresses(merchant.getId(), currency.getId()).forEach(address -> accounts.add(address));
            List<RefillRequestFlatDto> pendingTransactions = refillService.getInExamineByMerchantIdAndCurrencyIdList(merchant.getId(), currency.getId());
            subscribeCreated = true;
            currentBlockNumber = new BigInteger("0");

            final int[] counter = {0};
            final String[] currentHash = new String[1];
            currentHash[0] = "";

            observable = web3j.catchUpToLatestAndSubscribeToNewTransactionsObservable(new DefaultBlockParameterNumber(Long.parseLong(loadLastBlock())));
            subscription = observable.subscribe(ethBlock -> {
                log.info("new block {}", ethBlock.getBlockNumber());
                if (merchantName.equals("Ethereum")) {
                    if (ethBlock.getFrom().equals(credentialsMain.getAddress())) {
                        counter[0]++;
                        return;
                    }
                }

//                log.debug(merchantName + " block: " + ethBlock.getBlockNumber());

/*-------------Tokens--------------*/
                if (ethBlock.getTo() != null && ethTokensContext.isContract(ethBlock.getTo()) && merchantName.equals("Ethereum")){
                    ethTokensContext.getByContract(ethBlock.getTo()).tokenTransaction(ethBlock);
                }

                if (ethBlock.getTo() != null && ethBlock.getInput().contains("0xb61d27f6")
                        && merchantName.equals("Ethereum") && ethTokensContext.isContract("0x" + ethBlock.getInput().substring(34,74))){
                    ethTokensContext.getByContract("0x" + ethBlock.getInput().substring(34,74)).tokenTransaction(ethBlock);
                }
/*---------------------------------*/

                counter[0]++;

                String recipient = ethBlock.getTo();

                if (accounts.contains(recipient)){
                    if (!refillService.getRequestIdByAddressAndMerchantIdAndCurrencyIdAndHash(recipient, merchant.getId(), currency.getId(), ethBlock.getHash()).isPresent()){
                        BigDecimal amount = Convert.fromWei(String.valueOf(ethBlock.getValue()), Convert.Unit.ETHER);
                        log.debug(merchantName + " recipient: " + recipient + ", amount: " + amount);

                        Integer requestId = refillService.createRefillRequestByFact(RefillRequestAcceptDto.builder()
                                        .address(recipient)
                                        .amount(amount)
                                        .merchantId(merchant.getId())
                                        .currencyId(currency.getId())
                                        .merchantTransactionId(ethBlock.getHash()).build());

                        try {
                            refillService.putOnBchExamRefillRequest(RefillRequestPutOnBchExamDto.builder()
                                    .requestId(requestId)
                                    .merchantId(merchant.getId())
                                    .currencyId(currency.getId())
                                    .address(recipient)
                                    .amount(amount)
                                    .hash(ethBlock.getHash())
                                    .blockhash(ethBlock.getBlockNumber().toString()).build());
                        } catch (RefillRequestAppropriateNotFoundException e) {
                            log.error(e);
                        }

                        pendingTransactions.add(refillService.getFlatById(requestId));

                    }
                }

                if (!currentBlockNumber.equals(ethBlock.getBlockNumber())){
                    log.info(merchantName + " Current block number: " + ethBlock.getBlockNumber());

                    try {
                        if (!String.valueOf(counter[0]).equals(web3j.ethGetBlockTransactionCountByHash(currentHash[0]).send().getTransactionCount().toString())){

                            log.info(merchantName + " Block number for review: " + currentBlockNumber.add(new BigInteger("1")));
                            log.info(merchantName + " Txs counter: " + counter[0]);
                            log.info(merchantName + " Txs block: " + web3j.ethGetBlockTransactionCountByHash(currentHash[0]).send().getTransactionCount());

                        }

                    } catch (Exception e) {
                        log.error(e);
                    }
                    counter[0] = 0;

                    List<RefillRequestFlatDto> providedTransactions = new ArrayList<RefillRequestFlatDto>();
                    pendingTransactions.forEach(transaction ->
                            {
                                try {
                                    if (web3j.ethGetTransactionByHash(transaction.getMerchantTransactionId()).send().getResult()==null){
                                        return;
                                    }
                                    BigInteger transactionBlockNumber = web3j.ethGetTransactionByHash(transaction.getMerchantTransactionId()).send().getResult().getBlockNumber();
                                    if (ethBlock.getBlockNumber().subtract(transactionBlockNumber).intValue() >= minConfirmations){
                                        provideTransactionAndTransferFunds(transaction.getAddress(), transaction.getMerchantTransactionId());
                                        saveLastBlock(ethBlock.getBlockNumber().toString());
                                        log.debug(merchantName + " Transaction: " + transaction + " - PROVIDED!!!");
                                        log.debug(merchantName + " Confirmations count: " + ethBlock.getBlockNumber().subtract(transactionBlockNumber).intValue());
                                        providedTransactions.add(transaction);
                                    }
                                } catch (EthereumException | IOException e) {
                                    subscribeCreated = false;
                                    log.error(merchantName + " " + e);
                                }

                            }

                    );
                    providedTransactions.forEach(transaction -> pendingTransactions.remove(transaction));
                }

                currentBlockNumber = ethBlock.getBlockNumber();
                currentHash[0] = ethBlock.getBlockHash();

                });

        } catch (Exception e) {
            subscribeCreated = false;
            log.error(merchantName + " " + e);
        }
    }

    public void checkSession() {

        try {
            web3j.netVersion().send();
            if (subscription == null || subscribeCreated == false || subscription.isUnsubscribed()){
                createSubscribe();
            }
            subscribeCreated = true;
        } catch (IOException e) {
            log.error(merchantName + " " + e);
            subscribeCreated = false;
        }
    }

    @Override
    @Transactional
    public Map<String, String> refill(RefillRequestCreateDto request) {

        Map<String, String> mapAddress = new HashMap<>();
        try {

            File destination = new File(destinationDir);
            log.debug(merchantName + " " + destinationDir);

            String fileName = "";
            fileName = WalletUtils.generateLightNewWalletFile(password, destination);
            log.debug(merchantName + " " + fileName);
            Credentials credentials = WalletUtils.loadCredentials(password, destinationDir + fileName);
            String address = credentials.getAddress();

            accounts.add(address);
            log.debug(merchantName + " " + address);
            mapAddress.put("address", address);
            mapAddress.put("privKey", String.valueOf(credentials.getEcKeyPair().getPrivateKey()));
            mapAddress.put("pubKey", String.valueOf(credentials.getEcKeyPair().getPublicKey()));

        }catch (EthereumException | IOException | NoSuchAlgorithmException
                | InvalidAlgorithmParameterException | NoSuchProviderException | CipherException e){
            log.error(merchantName + " " + e);
        }


        String message = messageSource.getMessage("merchants.refill.btc",
                new Object[]{mapAddress.get("address")}, request.getLocale());

        mapAddress.put("message", message);
        mapAddress.put("qr", mapAddress.get("address"));

        return mapAddress;
    }

    private void provideTransactionAndTransferFunds(String address, String merchantTransactionId){
        try {
            Optional<RefillRequestBtcInfoDto> refillRequestInfoDto = refillService.findRefillRequestByAddressAndMerchantTransactionId(address, merchantTransactionId, merchantName, currencyName);
            log.debug("Providing transaction!");
            RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                        .requestId(refillRequestInfoDto.get().getId())
                        .address(refillRequestInfoDto.get().getAddress())
                        .amount(refillRequestInfoDto.get().getAmount())
                        .currencyId(currencyService.findByName(currencyName).getId())
                        .merchantId(merchantService.findByName(merchantName).getId())
                        .merchantTransactionId(merchantTransactionId)
                        .build();
                refillService.autoAcceptRefillRequest(requestAcceptDto);
                log.debug(merchantName + " Ethereum transaction " + requestAcceptDto.toString() + " --- PROVIDED!!!");

                refillService.updateAddressNeedTransfer(requestAcceptDto.getAddress(), merchantService.findByName(merchantName).getId(), currencyService.findByName(currencyName).getId(), true);

        } catch (Exception e) {
            log.error(e);
        }

    }

    private void transferFundsToMainAccount(){
        List<RefillRequestAddressDto> listRefillRequestAddressDto = refillService.findAllAddressesNeededToTransfer(merchantService.findByName(merchantName).getId(), currencyService.findByName(currencyName).getId());
        for (RefillRequestAddressDto refillRequestAddressDto : listRefillRequestAddressDto) {
            try {
                log.info("Start method transferFundsToMainAccount...");
                BigDecimal ethBalance = Convert.fromWei(String.valueOf(web3j.ethGetBalance(refillRequestAddressDto.getAddress(), DefaultBlockParameterName.LATEST).send().getBalance()), Convert.Unit.ETHER);

                if ( ethBalance.compareTo(minBalanceForTransfer) <= 0){
                    refillService.updateAddressNeedTransfer(refillRequestAddressDto.getAddress(), merchantService.findByName(merchantName).getId(),
                            currencyService.findByName(currencyName).getId(), false);
                    continue;
                }
                Credentials credentials = Credentials.create(new ECKeyPair(new BigInteger(refillRequestAddressDto.getPrivKey()),
                        new BigInteger(refillRequestAddressDto.getPubKey())));
                log.info("Credentials pubKey: " + credentials.getEcKeyPair().getPublicKey());
                Transfer.sendFunds(
                        web3j, credentials, mainAddress, (ethBalance
                                .subtract(Convert.fromWei(Transfer.GAS_LIMIT.multiply(web3j.ethGasPrice().send().getGasPrice()).toString(), Convert.Unit.ETHER)))
                                .subtract(minSumOnAccount), Convert.Unit.ETHER).sendAsync();
                log.debug(merchantName + " Funds " + ethBalance + " sent to main account!!!");
            } catch (Exception e) {
                subscribeCreated = false;
                log.error(merchantName + " " + e);
            }
        }
    }

    @PreDestroy
    public void destroy() {
        log.debug("Destroying " + merchantName);
        scheduler.shutdown();
        subscription.unsubscribe();
        log.debug(merchantName + " destroyed");
    }

    public void saveLastBlock(String block) {
        specParamsDao.updateParam(merchantName, LAST_BLOCK_PARAM, block);
    }

    public String loadLastBlock() {
        MerchantSpecParamDto specParamsDto = specParamsDao.getByMerchantNameAndParamName(merchantName, LAST_BLOCK_PARAM);
        return specParamsDto == null ? null : specParamsDto.getParamValue();
    }


    private void checkUnconfirmedTokensTransactions(BigInteger blockNumber) {
        List<Integer> currencyNames = refillService.getUnconfirmedTxsCurrencyIdsForTokens(merchantId);
        currencyNames.forEach(p->{
            log.debug("unconfirmed for {}", p);
            getByCurrencyId(p).checkTransaction(blockNumber);
        });
    }

    private EthTokenService getByCurrencyId(int currencyId) {
        return ethTokensContext.getByCurrencyId(currencyId);
    }

}
