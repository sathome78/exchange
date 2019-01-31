package me.exrates;

import me.exrates.dao.WalletDao;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.dto.RefillRequestAddressDto;
import me.exrates.model.dto.RefillRequestBtcInfoDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.RefillRequestParamsDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.invoice.RefillStatusEnum;
import me.exrates.service.*;
import me.exrates.service.ethereum.EthereumCommonService;
import me.exrates.service.ethereum.ethTokensWrappers.ethTokenERC20;
import me.exrates.service.exception.CoinTestException;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.merchantStrategy.IRefillable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static me.exrates.BtcCoinTesterImpl.compareObjects;
import static me.exrates.model.enums.OperationType.INPUT;
import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.CREATE_BY_USER;
import static me.exrates.service.ethereum.EthTokenServiceImpl.GAS_LIMIT;

//ethereum.properties
@Service("ethTokenTester")
@Scope("prototype")
@PropertySource(value = "classpath:/merchants/ethereum.properties")
public class EthTokenTester implements CoinTester {

    private static final String principalEmail = "mikita.malykov@upholding.biz";
    private static final int MIN_CONFIRMATION_FOR_REFILL = 1;
    @Autowired
    private Map<String, IRefillable> reffilableServiceMap;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private InputOutputService inputOutputService;
    @Autowired
    private RefillService refillService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private WithdrawService withdrawService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private WalletDao walletDao;
    @Autowired
    @Qualifier(value = "ethereumServiceImpl")
    private EthereumCommonService ethereumCommonService;

    @Value("${ethereum.url}")
    private String url;

    private int currencyId;
    private int merchantId;
    private String name;
    private final static Integer TIME_FOR_REFILL = 10000;
    private Object withdrawTest = new Object();
    private int withdrawStatus = 0;
    private StringBuilder stringBuilder;
    private static String mainTestAccountAddress = "0x0b958aa9601f1ca594fbe76bf879d8c29d578144";
    private static String mainTestPrivateKey = "92562730201626919127666680751712739048456177233249322255821751422413958671494";
    private static String mainTestPublicKey = "4170443246532098761497715728719234481946975701057637192159203344434254472506085562498955849273074226120269972199365634457382511964193135837279887855130261";
    private static String contractTestAddress = "0x1C83501478f1320977047008496DACBD60Bb15ef";
    private static String coin = "DGTX";
    private ethTokenERC20 contract;

    public void initBot(String name, StringBuilder stringBuilder) throws Exception {
        merchantId = merchantService.findByName(name).getId();
        currencyId = currencyService.findByName(name).getId();
        this.name = name;
        this.stringBuilder = stringBuilder;
        prepareContract();
        stringBuilder.append("Bot init success").append("\n");
    }

    private void prepareContract() throws Exception {

        ECKeyPair ecKeyPair = new ECKeyPair(new BigInteger(mainTestPrivateKey), new BigInteger(mainTestPublicKey));

        Credentials credentials = Credentials.create(ecKeyPair);

        Web3j web3j = Web3j.build(new HttpService("http://172.10.13.51:8549"));

        Class clazz = Class.forName("me.exrates.service.ethereum.ethTokensWrappers." + coin);
        Method method = clazz.getMethod("load", String.class, Web3j.class, Credentials.class, BigInteger.class, BigInteger.class);
        BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
        contract = (ethTokenERC20)method.invoke(null, contractTestAddress, web3j, credentials, gasPrice, GAS_LIMIT);
    }


    @Override
    public String testCoin(double refillAmount) throws Exception {
        try {
            RefillRequestCreateDto request = prepareRefillRequest(merchantId, currencyId);
            setMinConfirmation(MIN_CONFIRMATION_FOR_REFILL);
            checkNodeConnection();
            checkRefill(request, "1000000000000000", merchantId, currencyId);
            return stringBuilder.toString();
        } catch (Exception e){
            return stringBuilder.append(e.getMessage()).append("\n").toString();
        }
    }

    private void checkRefill(RefillRequestCreateDto request, String refillAmount, int merchantId, int currencyId) throws Exception {
        Map<String, Object> refillRequest = refillService.createRefillRequest(request);
        String addressForRefill = (String) ((Map) refillRequest.get("params")).get("address");
        List<RefillRequestAddressDto> byAddressMerchantAndCurrency = refillService.findByAddressMerchantAndCurrency(addressForRefill, merchantId, currencyId);
        if (byAddressMerchantAndCurrency.size() == 0)
            throw new CoinTestException("byAddressMerchantAndCurrency.size() == 0");

        stringBuilder.append("ADDRESS FRO REFILL FROM BIRZHA " + addressForRefill).append("\n");;

        String transactionHash = contract.transfer(addressForRefill, new BigInteger(refillAmount)).send().getTransactionHash();
        stringBuilder.append("Transaction hash = " + transactionHash).append("\n");


        stringBuilder.append("Checking our transaction in explorer...");
        BigInteger blockNumber = null;
        do {
            Thread.sleep(3000);
            Optional<Transaction> transaction = getTransactionByHash(transactionHash);
            if(!transaction.isPresent()){
                stringBuilder.append("Couldn't find tx...\n");
                continue;
            }
            blockNumber = transaction.get().getBlockNumber();

        } while (blockNumber == null);



        Optional<RefillRequestBtcInfoDto> acceptedRequest;
        do {
            boolean isConfirmationsEnough = false;
            acceptedRequest = refillService.findRefillRequestByAddressAndMerchantIdAndCurrencyIdAndTransactionId(merchantId, currencyId, transactionHash);
            if (!acceptedRequest.isPresent()) {
                if (isConfirmationsEnough) throw new RuntimeException("Confirmation enough, but refill not working!");
                stringBuilder.append("NOT NOW(").append("\n");;
                Thread.sleep(2000);


                if (ethereumCommonService.getWeb3j().ethBlockNumber().send().getBlockNumber().subtract(getTransactionByHash(transactionHash).get().getBlockNumber()).compareTo(new BigInteger(String.valueOf(MIN_CONFIRMATION_FOR_REFILL))) >= 0) {
                    stringBuilder.append("Enough confirmation, checking refill request...");
                    Thread.sleep(TIME_FOR_REFILL);
                    isConfirmationsEnough = true;
                }
                stringBuilder.append("Checking transaction... ").append("\n");
            } else {
                stringBuilder.append("accepted amount " + acceptedRequest.get().getAmount()).append("\n");;
                stringBuilder.append("refill amount " + refillAmount).append("\n");;
                RefillRequestBtcInfoDto refillRequestBtcInfoDto = acceptedRequest.get();
                refillRequestBtcInfoDto.setAmount(new BigDecimal(refillRequestBtcInfoDto.getAmount().doubleValue()));
                if (!compareObjects(refillRequestBtcInfoDto.getAmount(), (refillAmount)))
                    throw new CoinTestException("!acceptedRequest.get().getAmount().equals(new BigDecimal(refillAmount)), expected " + refillAmount + " but was " + acceptedRequest.get().getAmount());
            }
        } while (!acceptedRequest.isPresent());

        stringBuilder.append("REQUEST FINDED").append("\n");;
    }

    private Optional<Transaction> getTransactionByHash(String transactionHash) throws IOException {
        return ethereumCommonService.getWeb3j().ethGetTransactionByHash(transactionHash).send().getTransaction();
    }

    private void checkNodeConnection() throws IOException {
        ethereumCommonService.getWeb3j().ethGasPrice().send();
    }

    private void setMinConfirmation(int i) {
        ethereumCommonService.setConfirmationNeededCount(1);
    }


    //TODO make abstract class and extract method
    private RefillRequestCreateDto prepareRefillRequest(int merchantId, int currencyId) {
        RefillRequestParamsDto requestParamsDto = new RefillRequestParamsDto();
        requestParamsDto.setChildMerchant("");
        requestParamsDto.setCurrency(currencyId);
        requestParamsDto.setGenerateNewAddress(true);
        requestParamsDto.setMerchant(merchantId);
        requestParamsDto.setOperationType(OperationType.INPUT);
        requestParamsDto.setSum(null);

        Payment payment = new Payment(INPUT);
        payment.setCurrency(requestParamsDto.getCurrency());
        payment.setMerchant(requestParamsDto.getMerchant());
        payment.setSum(requestParamsDto.getSum() == null ? 0 : requestParamsDto.getSum().doubleValue());

        Locale locale = new Locale("en");
        CreditsOperation creditsOperation = inputOutputService.prepareCreditsOperation(payment, principalEmail, locale)
                .orElseThrow(InvalidAmountException::new);
        RefillStatusEnum beginStatus = (RefillStatusEnum) RefillStatusEnum.X_STATE.nextState(CREATE_BY_USER);

        return new RefillRequestCreateDto(requestParamsDto, creditsOperation, beginStatus, locale);
    }

//        String blockHash = contract.transfer("0x81fb419ACFDA6F40173b4032215101B09c4933c5", new BigInteger("1")).send().getBlockHash();
//        System.out.println(blockHash);
}
