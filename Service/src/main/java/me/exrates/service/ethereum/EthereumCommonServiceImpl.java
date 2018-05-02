package me.exrates.service.ethereum;

import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.dto.*;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.invoice.InvoiceActionTypeEnum;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;
import me.exrates.model.util.AtomicBigInteger;
import me.exrates.service.*;
import me.exrates.service.ethereum.ethTokensWrappers.EthToken;
import me.exrates.service.exception.*;
import me.exrates.service.exception.invoice.InsufficientCostsInWalletException;
import me.exrates.service.exception.invoice.InvalidAccountException;
import me.exrates.service.exception.invoice.MerchantException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.request.Transaction;
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
import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.*;
import java.util.concurrent.*;

/**
 * Created by ajet
 */
//@Service
@Log4j2(topic = "ethereum_log")
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

    private String destinationDir;

    private String password;

    private String mainAddress;

    private final List<String> accounts = new ArrayList<>();

    private final List<String> pendingTransactions = new ArrayList<>();

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

    private BigDecimal ethComissionPrice = new BigDecimal(0.0003);
    private BigDecimal ethComissionTokeWithdraw = new BigDecimal(0.003);

    private static final BigInteger ETH_TANSFER_GAS = BigInteger.valueOf(31000);
    private static final BigInteger TOKENS_TANSFER_GAS = BigInteger.valueOf(300000);

    @Override
    public boolean asyncAutoWithdraw() {
        return merchantName.equals("Ethereum");
    }

    @Override
    public Web3j getWeb3j() {
        return web3j;
    }

    @Override
    public List<String> getAccounts() {
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

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    private final ScheduledExecutorService checkerScheduler = Executors.newScheduledThreadPool(1);

    private static final String LAST_BLOCK_PARAM = "LastRecievedBlock";

    private final Object ethSynchronizer = new Object();
    private  final Object tokensSynchronizer = new Object();

    private AtomicBigInteger lastNonce = new AtomicBigInteger(BigInteger.ZERO);

    private BlockingQueue<EthTokenWithdrawInfoDto> blockingQueue = new LinkedBlockingDeque<>();

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
            if (merchantName.equals("Ethereum")){
                this.transferAccAddress = props.getProperty("ethereum.transferAccAddress");
                this.transferAccPrivateKey = props.getProperty("ethereum.transferAccPrivateKey");
                this.transferAccPublicKey = props.getProperty("ethereum.transferAccPublicKey");
                this.withdrawAccAddress = props.getProperty("ethereum.withdrawAccAddress");
                this.withdrawAccPrivateKey = props.getProperty("ethereum.withdrawAccPrivateKey");
                this.withdrawAccPublicKey = props.getProperty("ethereum.withdrawAccPublicKey");
                this.needToCheckTokens = true;
                credentialsWithdrawAcc = Credentials.create(new ECKeyPair(new BigInteger(withdrawAccPrivateKey),
                        new BigInteger(withdrawAccPublicKey)));
                CompletableFuture.runAsync(() -> {
                    while (true) {
                        try {
                            EthTokenWithdrawInfoDto dto = blockingQueue.take();
                            withdrawTokens(dto.getWithdrawMerchantOperationDto(), (EthTokenService) dto.getTokenService(), dto.getMerchantName());
                        } catch (Exception e) {
                            log.error(e);
                        }
                    }
                });
            }
        } catch (IOException e) {
            log.error(e);
        }
    }

    @PostConstruct
    void start() {

        merchantId = merchantService.findByName(merchantName).getId();

        web3j = Web3j.build(new HttpService(url));
        web3jForEthWithdr = Web3j.build(new HttpService(url));
        web3jForTokensWithdr = Web3j.build(new HttpService(url));


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
            return withdrawEth(withdrawMerchantOperationDto);
        } else {
            Currency currency = currencyService.findByName(withdrawMerchantOperationDto.getCurrency());
            EthTokenService tokenService = ethTokensContext
                    .getByCurrencyId(currency.getId());
            List<MerchantCurrency> merchant = merchantService
                    .getAllUnblockedForOperationTypeByCurrencies(Collections.singletonList(currency.getId()), OperationType.OUTPUT);
            if (tokenService != null || merchant.size() !=1) {
                return withdrawTokens(EthTokenWithdrawInfoDto.builder()
                        .withdrawMerchantOperationDto(withdrawMerchantOperationDto)
                        .tokenService(tokenService)
                        .merchantName(merchant.get(0).getName())
                        .build());
            }
        }
        throw new WithdrawRequestPostException("Currency not supported by merchant " + withdrawMerchantOperationDto.getCurrency());
    }


    private HashMap<String, String> withdrawEth(WithdrawMerchantOperationDto withdrawMerchantOperationDto) {
        BigDecimal ethBalance = null;
        try {
            ethBalance = Convert.fromWei(String.valueOf(web3jForEthWithdr.ethGetBalance(getMainAddress(), DefaultBlockParameterName.LATEST).send().getBalance()), Convert.Unit.ETHER);
        } catch (IOException e) {
            log.error("error checking eth balance");
            throw new MerchantException("error checking balance");
        }
        BigDecimal withdrawAmount = new BigDecimal(withdrawMerchantOperationDto.getAmount());
        if (ethBalance.compareTo(withdrawAmount.add(ethComissionPrice)) < 0) {
            throw new InsufficientCostsInWalletException("ETH BALANCE LOW");
        }
        try {
            synchronized (ethSynchronizer) {
                log.info("try autowithdraw {}", withdrawMerchantOperationDto);
                BigInteger gasPrice = web3jForEthWithdr.ethGasPrice().send().getGasPrice();
                BigInteger gasLimit = ETH_TANSFER_GAS;
                BigInteger nonce = resolveNonce();
                BigInteger amount = ExConvert.toWei(withdrawMerchantOperationDto.getAmount(), ExConvert.Unit.ETHER).toBigInteger();
                log.info("amount {}, nonce {}, gas {} ", amount, nonce, gasLimit);
                RawTransaction rawTransaction  = RawTransaction.createEtherTransaction(
                        nonce, gasPrice, gasLimit, withdrawMerchantOperationDto.getAccountTo(), amount);
                byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentialsWithdrawAcc);
                String hexValue = Numeric.toHexString(signedMessage);
                web3jForEthWithdr.ethSendRawTransaction(hexValue).sendAsync().handleAsync((res, ex) -> {
                    log.info("result async {} {}", res.getResult(), ex);
                    if (ex != null || res.hasError() || StringUtils.isEmpty(res.getTransactionHash().trim())) {
                        if (res.getError() != null) {
                            log.info(res.getError().getMessage());
                        }
                        sendWithdrToReview(ex, withdrawMerchantOperationDto.getRequestId());
                    } else {
                        withdrawService.setWithdrawHashAndStatus(res.getTransactionHash(),
                                withdrawMerchantOperationDto.getRequestId(),
                                (WithdrawStatusEnum) WithdrawStatusEnum.SENDED_WAITING_EXECUTION.nextState(InvoiceActionTypeEnum.FINALIZE_POST));
                    }
                    return res;
                });
            }
            return new HashMap<String, String>() {{
            }};
        } catch (Exception e) {
            log.error("error sending tx {}", e);
            throw new MerchantException(e);
        }
    }

    private void sendWithdrToReview(Throwable ex, int requestId) {
        log.error(ex);
        log.error("send to review");
        withdrawService.setWithdrawHashAndStatus("error", requestId, WithdrawStatusEnum.WAITING_REVIEWING);

    }

    private BigInteger resolveNonce() throws IOException {
        EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(
                credentialsWithdrawAcc.getAddress(), DefaultBlockParameterName.LATEST).send();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        if (nonce.compareTo(lastNonce.get()) <= 0) {
            nonce = lastNonce.incrementAndGet();
        }
        lastNonce = new AtomicBigInteger(nonce);
        return nonce;
    }


    private Map withdrawTokens(EthTokenWithdrawInfoDto withdrawInfoDto) {
        try {
            blockingQueue.put(withdrawInfoDto);
        } catch (InterruptedException e) {
            log.error(e);
        }
        return new HashMap();
    }


    private HashMap<String, String> withdrawTokens(WithdrawMerchantOperationDto withdrawMerchantOperationDto,
                                                   EthTokenService tokenService, String merchantName) {
        BigDecimal withdarwAmount = null;
        EthToken contract = null;
        BigDecimal balance = null;
        BigDecimal ethBalance = null;
        BigInteger GAS_PRICE;
        try {
            GAS_PRICE = web3jForTokensWithdr.ethGasPrice().send().getGasPrice().multiply(BigInteger.valueOf(2));
            Class clazz = Class.forName("me.exrates.service.ethereum.ethTokensWrappers." + merchantName);
            Method method = clazz.getMethod("load", String.class, Web3j.class, Credentials.class, BigInteger.class, BigInteger.class);
            withdarwAmount = new BigDecimal(withdrawMerchantOperationDto.getAmount());
            contract = (EthToken)method.invoke(null, tokenService.getContractAddress().get(0),
                    web3jForTokensWithdr, credentialsWithdrawAcc, GAS_PRICE, TOKENS_TANSFER_GAS);
            balance = ExConvert.fromWei(contract.balanceOf(withdrawAccAddress).send().toString(), tokenService.getUnit());
            ethBalance = Convert.fromWei(String.valueOf(web3jForTokensWithdr.ethGetBalance(withdrawAccAddress, DefaultBlockParameterName.LATEST).send().getBalance()), Convert.Unit.ETHER);
            log.info("balance {}, eth balance {}", balance, ethBalance);
        } catch (Exception e) {
            log.error("transfer token error {}" , e);
            throw new MerchantException();
        }
        if (ethBalance.compareTo(ethComissionTokeWithdraw) <= 0 || balance.compareTo(withdarwAmount) < 0) {
            throw new InsufficientCostsInWalletException("ETH BALANCE LOW for withdraw " + merchantName);
        }
        log.info("withdraw {} amount {}, converted {}",
                merchantName, withdarwAmount, ExConvert.toWei(withdarwAmount, tokenService.getUnit()).toBigInteger());
        try {
            BigInteger convertedAmount = ExConvert.toWei(withdarwAmount, tokenService.getUnit()).toBigInteger();
            Function function = new Function(
                    "transfer",  // function we're calling
                    Arrays.asList(new Address(withdrawMerchantOperationDto.getAccountTo()), new Uint(convertedAmount)),
                    Arrays.asList(new TypeReference<Bool>(){}));

            String encodedFunction = FunctionEncoder.encode(function);
            RawTransaction transaction = RawTransaction.createContractTransaction(resolveNonce(), GAS_PRICE, TOKENS_TANSFER_GAS,
                    convertedAmount, encodedFunction);
            byte[] signedMessage = TransactionEncoder.signMessage(transaction, credentialsWithdrawAcc);
            String hexValue = Numeric.toHexString(signedMessage);
            EthSendTransaction res = web3jForTokensWithdr.ethSendRawTransaction(hexValue).send();
            log.info("response {} {}", res.getTransactionHash(), res.getRawResponse());
//            TransactionReceipt res = contract.transfer(withdrawMerchantOperationDto.getAccountTo(),
//                        ExConvert.toWei(withdarwAmount, tokenService.getUnit()).toBigInteger())
//                        .send();
//            log.info("result async {} {}", res.getStatus(), res.getTransactionHash());
//            if (!res.getResult().equals("0x1") || StringUtils.isEmpty(res.getTransactionHash().trim())) {
            if (res.hasError() || StringUtils.isEmpty(res.getTransactionHash().trim())) {
                sendWithdrToReview(null, withdrawMerchantOperationDto.getRequestId());
            } else {
                withdrawService.setWithdrawHashAndStatus(res.getTransactionHash(),
                        withdrawMerchantOperationDto.getRequestId(),
                        (WithdrawStatusEnum) WithdrawStatusEnum.SENDED_WAITING_EXECUTION.nextState(InvoiceActionTypeEnum.FINALIZE_POST));
            }
        } catch (Exception e) {
            log.error("error sending tx {}", e);
            sendWithdrToReview(e, withdrawMerchantOperationDto.getRequestId());
            throw new MerchantException(e);
        }
        return new HashMap();
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

            observable = web3j.catchUpToLatestAndSubscribeToNewTransactionsObservable(new DefaultBlockParameterNumber(Long.parseLong(loadLastBlock())));
            subscription = observable.subscribe(ethBlock -> {
                if (merchantName.equals("Ethereum")) {
                    if (ethBlock.getFrom().equals(credentialsMain.getAddress())) {
                        return;
                    }
                }

                if (!currentBlockNumber.equals(ethBlock.getBlockNumber())){
                    log.info(merchantName + " Current block number: " + ethBlock.getBlockNumber());

                    List<RefillRequestFlatDto> providedTransactions = new ArrayList<RefillRequestFlatDto>();
                    pendingTransactions.forEach(transaction ->
                            {
                                try {
                                    if (web3j.ethGetTransactionByHash(transaction.getMerchantTransactionId()).send().getResult()==null){
                                        return;
                                    }
                                    BigInteger transactionBlockNumber = web3j.ethGetTransactionByHash(transaction.getMerchantTransactionId()).send().getResult().getBlockNumber();
                                    if (ethBlock.getBlockNumber().subtract(transactionBlockNumber).intValue() > minConfirmations){
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
//                log.debug(merchantName + " block: " + ethBlock.getBlockNumber());

/*-------------Tokens--------------*/
                if (ethBlock.getTo() != null && ethTokensContext.isContract(ethBlock.getTo()) && merchantName.equals("Ethereum")){
                    ethTokensContext.getByContract(ethBlock.getTo()).tokenTransaction(ethBlock);
                }
/*---------------------------------*/

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

                });

        } catch (Exception e) {
            subscribeCreated = false;
            log.error(merchantName + " " + e);
        }
    }

    public void checkSession() {

        try {
            web3j.netVersion().send();
            if (subscribeCreated == false){
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
