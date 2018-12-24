package me.exrates.service.ethereum;

import me.exrates.dao.EthereumNodeDao;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.*;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.TransactionService;
import me.exrates.service.exception.EthereumException;
import me.exrates.service.exception.NotImplimentedMethod;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.util.WithdrawUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import rx.Observable;
import rx.Subscription;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by ajet
 */
//@Service
public class EthereumCommonServiceImpl implements EthereumCommonService {

    @Autowired
    private EthereumNodeDao ethereumNodeDao;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private PlatformTransactionManager txManager;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private RefillService refillService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private MerchantSpecParamsDao specParamsDao;

    @Autowired
    private EthTokensContext ethTokensContext;

    @Autowired
    private WithdrawUtils withdrawUtils;

    private String url;

    private String destinationDir;

    private String password;

    private String mainAddress;

    private final Set<String> accounts = new HashSet<>();

    private final Set<String> pendingTransactions = new HashSet<>();

    private Web3j web3j;

    private Observable<org.web3j.protocol.core.methods.response.Transaction> observable;

    private Subscription subscription;

    private boolean subscribeCreated = false;

    private BigInteger currentBlockNumber;

    private String merchantName;

    private String currencyName;

    private Integer minConfirmations;

    private Credentials credentialsMain;

    private String transferAccAddress;

    private String transferAccPrivateKey;

    private String transferAccPublicKey;

    private BigDecimal minBalanceForTransfer;

    private int merchantId;

    private boolean needToCheckTokens = false;

    private BigDecimal minSumOnAccount;

    private Logger log;

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

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final ScheduledExecutorService checkerScheduler = Executors.newScheduledThreadPool(1);

    private final ScheduledExecutorService etiReconnectScheduler = Executors.newScheduledThreadPool(1);

    private static final String LAST_BLOCK_PARAM = "LastRecievedBlock";

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
                this.transferAccAddress = props.getProperty("ethereum.transferAccAddress");
                this.transferAccPrivateKey = props.getProperty("ethereum.transferAccPrivateKey");
                this.transferAccPublicKey = props.getProperty("ethereum.transferAccPublicKey");
                this.needToCheckTokens = true;
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

        scheduler.scheduleAtFixedRate(new Runnable() {
            public void run() {
                checkSession();
            }
        }, 3, 8, TimeUnit.MINUTES);

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

        if (currencyName.equals("ETI")) {
            scheduler.scheduleWithFixedDelay(() -> {
                try {
                    checkConnection();
                }catch (Exception e){
                    log.error(e);
                }
            }, 4, 3, TimeUnit.MINUTES);
        }
    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) {
        return new HashMap<>();
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        throw new NotImplimentedMethod("for " + params);
    }

    private void checkConnection() {
        observable = null;
        web3j.shutdown();
        web3j = Web3j.build(new HttpService(url));
        String lastBlock = loadLastBlock();
        if (currentBlockNumber.compareTo(new BigInteger(lastBlock)) > 0) {
            saveLastBlock(currentBlockNumber.toString());
            createSubscribe(currentBlockNumber.toString());
        } else {
            createSubscribe(lastBlock);
        }
    }

    public void createSubscribe(String lastBlock){
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

            observable = web3j.catchUpToLatestAndSubscribeToNewTransactionsObservable(new DefaultBlockParameterNumber(Long.parseLong(lastBlock)));
            log.info("start subscribe method");
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

            log.info("dubscr {}", subscription.isUnsubscribed());

        } catch (Exception e) {
            subscribeCreated = false;
            log.error(merchantName + " " + e);
        }
    }

    public void checkSession() {

        try {
            web3j.netVersion().send();
            if (subscription == null || subscribeCreated == false || subscription.isUnsubscribed()){

                createSubscribe(loadLastBlock());
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

    @Override
    public boolean isValidDestinationAddress(String address) {

        return withdrawUtils.isValidDestinationAddress(address);
    }

}
