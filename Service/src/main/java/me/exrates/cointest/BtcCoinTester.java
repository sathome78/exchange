package me.exrates.cointest;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import com.neemre.btcdcli4j.core.client.BtcdClient;
import com.neemre.btcdcli4j.core.client.BtcdClientImpl;
import com.neemre.btcdcli4j.core.domain.Transaction;
import lombok.NoArgsConstructor;
import me.exrates.dao.WalletDao;
import me.exrates.model.*;
import me.exrates.model.dto.*;
import me.exrates.model.dto.merchants.btc.BtcPaymentResultDetailedDto;
import me.exrates.model.dto.merchants.btc.BtcWalletPaymentItemDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.invoice.RefillStatusEnum;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;
import me.exrates.service.*;
import me.exrates.service.exception.CoinTestException;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.exception.api.OrderParamsWrongException;
import me.exrates.service.job.invoice.RefillRequestJob;
import me.exrates.service.merchantStrategy.IRefillable;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static me.exrates.model.enums.OperationType.INPUT;
import static me.exrates.model.enums.OperationType.OUTPUT;
import static me.exrates.model.enums.OrderActionEnum.CREATE;
import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.CREATE_BY_USER;

@NoArgsConstructor
@Component("btcCoinTester")
@Scope("prototype")
public class BtcCoinTester implements CoinTester {

    private String principalEmail = "mikita.malykov@upholding.biz";
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
    private RefillRequestJob refillRequestJob;

    private int currencyId;
    private int merchantId;
    private String name;
    private final static Integer TIME_FOR_REFILL = 10000;
    private BtcdClient btcdClient;
    private Object withdrawTest = new Object();
    private int withdrawStatus = 0;
    private StringBuilder stringBuilder;

    public void initBot(String name, StringBuilder stringBuilder, String email) throws BitcoindException, IOException, CommunicationException {
        merchantId = merchantService.findByName(name).getId();
        currencyId = currencyService.findByName(name).getId();
        this.name = name;
        btcdClient = prepareBtcClient(name);
        this.stringBuilder = stringBuilder;
        if(email != null) this.principalEmail = email;
        stringBuilder.append("Init success for coin " + name).append("<br>");
    }

    @Override
    public String testCoin(String refillAmount) throws Exception {
        try {
            testNodeInfo();
            RefillRequestCreateDto request = prepareRefillRequest(merchantId, currencyId);
            setMinConfirmation(1);
            testAddressGeneration();
            checkRefill(refillAmount, merchantId, currencyId, request);
            testAutoWithdraw(refillAmount);
//            testManualWithdraw(refillAmount);
            testOrder(BigDecimal.valueOf(0.000001), BigDecimal.valueOf(0.000001), name + "/BTC", BigDecimal.valueOf(0.00));
            testOrder(BigDecimal.valueOf(0.000001), BigDecimal.valueOf(0.000001), name + "/USD", BigDecimal.valueOf(0.00));
            testOrder(BigDecimal.valueOf(0.000001), BigDecimal.valueOf(0.000001), name + "/ETH", BigDecimal.valueOf(0.00));
            stringBuilder.append("Everything works fine!<br>");
            return "Works fine";
        } catch (Exception e){
            e.printStackTrace();
            stringBuilder.append(e.toString());
            return e.getMessage();
        }
    }

    private void testNodeInfo() throws BitcoindException, CommunicationException {
        stringBuilder.append("------TEST NODE INFO-----").append("<br>")
                .append("Current balance = " + btcdClient.getBalance()).append("<br>")
                .append("You can refill test node on address = " + btcdClient.getNewAddress()).append("<br>");
    }

    private void setMinConfirmation(int i) throws NoSuchFieldException {
        BitcoinService btcService = (BitcoinService) getMerchantServiceByName(name, reffilableServiceMap);
        btcService.setConfirmationNeededCount(i);
    }

    private void testOrder(BigDecimal amount, BigDecimal rate, String currencyPair, BigDecimal stop) throws CoinTestException {
        try {
            int walletId = walletDao.getWalletId(userService.getIdByEmail(principalEmail), currencyId);
            walletDao.addToWalletBalance(walletId,amount.multiply(new BigDecimal(100)), new BigDecimal(0));
            sellOrder(amount, rate, currencyPair, stop);
            buyOrder(amount, rate, currencyPair, stop);
        } catch (Exception e) {
            throw e;
        }
    }

    private void buyOrder(BigDecimal amount, BigDecimal rate, String currencyPair, BigDecimal stop) throws CoinTestException {
        CreateOrder createOrder = new CreateOrder(OperationType.BUY, amount, rate, OrderBaseType.LIMIT, currencyPair, stop).invoke();
        String response = createOrder.getResponse();
        if (!response.equals("{\"result\":\"The 1 orders have been accepted successfully; \"}"))
            throw new CoinTestException("unexpected order buy response:<br>" + response);
    }

    private void sellOrder(BigDecimal amount, BigDecimal rate, String currencyPair, BigDecimal stop) throws CoinTestException {
        CreateOrder createOrder = new CreateOrder(OperationType.SELL, amount, rate, OrderBaseType.LIMIT, currencyPair, stop).invoke();
        OrderCreateDto orderCreateDto = createOrder.getOrderCreateDto();
        String response = createOrder.getResponse();
        if (orderService.getOrderByOrderCreateDtoAndTime(orderCreateDto, LocalDateTime.now().minusSeconds(30), LocalDateTime.now(), principalEmail) == null) {
            throw new CoinTestException("Order were not found in db!");
        }
        if (!response.equals("{\"result\":\"Your order was placed in common stack\"}"))
            throw new CoinTestException("unexpected order create response:<br>" + response);
    }

    private void testAddressGeneration() throws BitcoindException, CommunicationException {
        stringBuilder.append("Starting test generating address...<br>");
        String newAddress = btcdClient.getNewAddress();
        assert newAddress.length() > 10;
        assert btcdClient.getNewAddress().length() > 10;
        stringBuilder.append("Address generation works fine. Example = " + newAddress + " <br>");

    }

    private void testAutoWithdraw(String refillAmount) throws BitcoindException, CommunicationException, InterruptedException, CoinTestException {
        synchronized (withdrawTest) {
            String withdrawAddress = btcdClient.getNewAddress();
            stringBuilder.append("address for withdraw " + withdrawAddress).append("<br>");;

            WithdrawRequestParamsDto withdrawRequestParamsDto = new WithdrawRequestParamsDto();
            withdrawRequestParamsDto.setCurrency(currencyId);
            withdrawRequestParamsDto.setMerchant(merchantId);
            withdrawRequestParamsDto.setDestination(withdrawAddress);
            withdrawRequestParamsDto.setDestinationTag("");
            withdrawRequestParamsDto.setOperationType(OperationType.OUTPUT);
            withdrawRequestParamsDto.setSum(new BigDecimal(refillAmount));

            Payment payment = new Payment(OUTPUT);
            payment.setCurrency(withdrawRequestParamsDto.getCurrency());
            payment.setMerchant(withdrawRequestParamsDto.getMerchant());
            payment.setSum(withdrawRequestParamsDto.getSum().doubleValue());
            payment.setDestination(withdrawRequestParamsDto.getDestination());
            payment.setDestinationTag(withdrawRequestParamsDto.getDestinationTag());

            merchantService.setMinSum(merchantId, currencyId, 0.00000001);
            CreditsOperation creditsOperation = inputOutputService.prepareCreditsOperation(payment, principalEmail, new Locale("en"))
                    .orElseThrow(InvalidAmountException::new);
            WithdrawStatusEnum beginStatus = (WithdrawStatusEnum) WithdrawStatusEnum.getBeginState();

            WithdrawRequestCreateDto withdrawRequestCreateDto = new WithdrawRequestCreateDto(withdrawRequestParamsDto, creditsOperation, beginStatus);
            setAutoWithdraw(true);
            withdrawService.createWithdrawalRequest(withdrawRequestCreateDto, new Locale("en"));

            Optional<WithdrawRequest> withdrawRequestByAddressOptional = withdrawService.getWithdrawRequestByAddress(withdrawAddress);
            if (!withdrawRequestByAddressOptional.isPresent())
                throw new CoinTestException("Empty withdrawRequestByAddressOptional");

            Integer requestId = withdrawRequestByAddressOptional.get().getId();
            WithdrawRequestFlatDto flatWithdrawRequest;

            do {
                try {
                    flatWithdrawRequest = withdrawService.getFlatById(requestId).get();
                    withdrawStatus = flatWithdrawRequest.getStatus().getCode();
                    Thread.sleep(5000);
                    if (withdrawStatus == 10) {
                        Transaction transaction = btcdClient.getTransaction(flatWithdrawRequest.getTransactionHash());
                        if (!compareObjects(transaction.getAmount(), (flatWithdrawRequest.getAmount().subtract(flatWithdrawRequest.getCommissionAmount()))))
                            throw new CoinTestException("Amount expected " + transaction.getAmount() + ", but was " + flatWithdrawRequest.getAmount().min(flatWithdrawRequest.getCommissionAmount()));
                    }
                    stringBuilder.append("Checking withdraw...current status = " + withdrawStatus).append("<br>");;
                    Thread.sleep(2000);
                } catch (BitcoindException e) {
                    stringBuilder.append(e).append("<br>");;
                }
            } while (withdrawStatus != 10);

            stringBuilder.append("Auto-withdraw works").append("<br>");


        }
    }

    private void testManualWithdraw(String amount) throws BitcoindException, CommunicationException, InterruptedException, CoinTestException {
        synchronized (withdrawTest) {
            setAutoWithdraw(false);

        String withdrawAddress = btcdClient.getNewAddress();
        stringBuilder.append("address for manual withdraw " + withdrawAddress).append("<br>");;
        walletPassphrase();
        BitcoinService walletService = (BitcoinService) getMerchantServiceByName(name, reffilableServiceMap);
        List<BtcWalletPaymentItemDto> payments = new LinkedList<>();
        payments.add(new BtcWalletPaymentItemDto(withdrawAddress, new BigDecimal(amount)));
        BtcPaymentResultDetailedDto btcPaymentResultDetailedDto = walletService.sendToMany(payments).get(0);
        stringBuilder.append("BtcPaymentResultDetailedDto = " + btcPaymentResultDetailedDto.toString()).append("<br>");

        if(btcPaymentResultDetailedDto.getTxId() == null){
            stringBuilder.append("Cannot check manual withdraw, use walletpassphrase first!");
            return;
        }
        Transaction transaction = null;
        do {
            try {
                transaction = btcdClient.getTransaction(btcPaymentResultDetailedDto.getTxId());
            } catch (BitcoindException e) {
                stringBuilder.append("Error from btc " + e.getMessage()).append("<br>");;
            }
            Thread.sleep(2000);
            stringBuilder.append("Checking manual transaction").append("<br>");;
            if (transaction != null) {
                if (!compareObjects(btcPaymentResultDetailedDto.getAmount(), (transaction.getAmount())))
                    throw new CoinTestException("btcPaymentResultDetailedDto.getAmount() = " + btcPaymentResultDetailedDto.getAmount()
                            + " not equals with transaction.getAmount() " + transaction.getAmount());
            }
        } while (transaction == null);
        }
    }

    private void setAutoWithdraw(boolean isEnabled) {
        MerchantCurrencyOptionsDto merchantCurrencyOptionsDto = new MerchantCurrencyOptionsDto();
        merchantCurrencyOptionsDto.setCurrencyId(currencyId);
        merchantCurrencyOptionsDto.setMerchantId(merchantId);
        merchantCurrencyOptionsDto.setWithdrawAutoDelaySeconds(1);
        merchantCurrencyOptionsDto.setWithdrawAutoEnabled(isEnabled);
        merchantCurrencyOptionsDto.setWithdrawAutoThresholdAmount(new BigDecimal(999999));
        withdrawService.setAutoWithdrawParams(merchantCurrencyOptionsDto);
    }

    private void checkRefill(String refillAmount, int merchantId, int currencyId, RefillRequestCreateDto request) throws BitcoindException, CommunicationException, InterruptedException, CoinTestException {
        Map<String, Object> refillRequest = refillService.createRefillRequest(request);
        String addressForRefill = (String) ((Map) refillRequest.get("params")).get("address");
        List<RefillRequestAddressDto> byAddressMerchantAndCurrency = refillService.findByAddressMerchantAndCurrency(addressForRefill, merchantId, currencyId);
        if (byAddressMerchantAndCurrency.size() == 0)
            throw new CoinTestException("byAddressMerchantAndCurrency.size() == 0");

        stringBuilder.append("ADDRESS FRO REFILL FROM BIRZHA " + addressForRefill).append("<br>");;
        stringBuilder.append("BALANCE = " + btcdClient.getBalance()).append("<br>");;
        walletPassphrase();
        stringBuilder.append("balance = " + btcdClient.getBalance()).append("<br>");;
        stringBuilder.append("refill sum = " + refillAmount).append("<br>");;
        stringBuilder.append("DEBUG: new BigDecimal(refillAmount)" + new BigDecimal(refillAmount)).append("<br>");
        String txHash = btcdClient.sendToAddress(addressForRefill, new BigDecimal(refillAmount));

        Optional<RefillRequestBtcInfoDto> acceptedRequest;
        Integer minConfirmation = getMerchantServiceByName(name, reffilableServiceMap).minConfirmationsRefill();

        do {
            acceptedRequest = refillService.findRefillRequestByAddressAndMerchantIdAndCurrencyIdAndTransactionId(merchantId, currencyId, txHash);
            if (!acceptedRequest.isPresent()) {
                refillRequestJob.forceCheckPaymentsForCoin(name);
                stringBuilder.append("NOT NOW").append("<br>");;
                Thread.sleep(2000);
                Transaction transaction = btcdClient.getTransaction(txHash);
                if (transaction.getConfirmations() >= minConfirmation) {
                    Thread.sleep(TIME_FOR_REFILL);
                }
                String trxConf = "Transaction consfirmation = ";
//                int confirmationIndex = stringBuilder.indexOf("Transaction consfirmation =");
//                if(confirmationIndex != -1) stringBuilder = new StringBuilder(stringBuilder.substring(0, confirmationIndex));
                stringBuilder.append(trxConf).append(transaction.getConfirmations()).append("<br>");;
            } else {
                stringBuilder.append("accepted amount ").append(acceptedRequest.get().getAmount()).append("<br>");;
                stringBuilder.append("refill amount ").append(refillAmount).append("<br>");;
                RefillRequestBtcInfoDto refillRequestBtcInfoDto = acceptedRequest.get();
                refillRequestBtcInfoDto.setAmount(new BigDecimal(refillRequestBtcInfoDto.getAmount().doubleValue()));
                if (!compareObjects(refillRequestBtcInfoDto.getAmount(), (refillAmount)))
                    throw new CoinTestException("!acceptedRequest.get().getAmount().equals(new BigDecimal(refillAmount)), expected " + refillAmount + " but was " + acceptedRequest.get().getAmount());
            }
        } while (!acceptedRequest.isPresent());

        stringBuilder.append("REQUEST FINDED").append("<br>");;
        stringBuilder.append("Node balance after refill = " + btcdClient.getBalance()).append("<br>");
        Map<String, String> a = new HashMap<>();
    }

    private void walletPassphrase() {
        try {
            btcdClient.walletPassphrase("pass123", 2000);
        } catch (Exception e) {
            stringBuilder.append("Error while trying encrypted wallet: <br>" + e.getMessage()).append("<br>");;
        }
    }

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

    private BtcdClient prepareBtcClient(String name) throws IOException, BitcoindException, CommunicationException {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        CloseableHttpClient httpProvider = HttpClients.custom().setConnectionManager(cm)
                .build();

        Properties properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream(((BitcoinService) getMerchantServiceByName(name, reffilableServiceMap)).getNodePropertySource()));
        properties.setProperty("node.bitcoind.rpc.port", "8089");

        Properties passPropertySource = merchantService.getPassMerchantProperties(name);

        properties.setProperty("node.bitcoind.rpc.user", passPropertySource.getProperty("node.bitcoind.rpc.user"));
        properties.setProperty("node.bitcoind.rpc.password", passPropertySource.getProperty("node.bitcoind.rpc.password"));

        return new BtcdClientImpl(httpProvider, properties);
    }

    private IRefillable getMerchantServiceByName(String name, Map<String, IRefillable> merchantServiceMap) {
        for (Map.Entry<String, IRefillable> e : merchantServiceMap.entrySet()) {
            if (e.getValue().getMerchantName().equals(name)) return e.getValue();
        }
        throw new RuntimeException("BitcoinService with ticker " + name + " not found!");
    }


    public static boolean compareObjects(Object A, Object B) {
        return normalize(A).equals(normalize(B));
    }

    public static void main(String[] args) {
        System.out.println(compareObjects(new BigDecimal("0.000001"), new BigDecimal("0.0000010")));
    }

    private static String normalize(Object B) {
        BigDecimal A = new BigDecimal(String.valueOf(B));
        StringBuilder first = new StringBuilder(String.valueOf(A));
        String check = String.valueOf(A);
        if (!check.contains(".")) return check;

        String substring = "";
        substring = check.substring(check.indexOf(".") + 1);

        if (substring.length() > 8) {
            first = new StringBuilder(substring.substring(0, 8));
        } else first = new StringBuilder(substring.substring(0, substring.length()));


        for (int i = first.length() - 1; i != -1; i--) {
            if (String.valueOf(first.charAt(i)).equals("0")) {
                first.deleteCharAt(i);
            } else break;
        }
        String result = check.substring(0, check.indexOf(".") + 1) + first.toString();
        return result;
    }

    private class CreateOrder {
        private OperationType orderType;
        private BigDecimal amount;
        private BigDecimal rate;
        private OrderBaseType baseType;
        private String currencyPair;
        private BigDecimal stop;
        private OrderCreateDto orderCreateDto;
        private String response;

        public CreateOrder(OperationType orderType, BigDecimal amount, BigDecimal rate, OrderBaseType baseType, String currencyPair, BigDecimal stop) {
            this.orderType = orderType;
            this.amount = amount;
            this.rate = rate;
            this.baseType = baseType;
            this.currencyPair = currencyPair;
            this.stop = stop;
        }

        public OrderCreateDto getOrderCreateDto() {
            return orderCreateDto;
        }

        public String getResponse() {
            return response;
        }

        public CreateOrder invoke() throws CoinTestException {
            OrderCreateSummaryDto orderCreateSummaryDto;
            if (amount == null) amount = BigDecimal.ZERO;
            if (rate == null) rate = BigDecimal.ZERO;
            if (baseType == null) baseType = OrderBaseType.LIMIT;
            CurrencyPair activeCurrencyPair = currencyService.getNotHiddenCurrencyPairByName(currencyPair);
            if (activeCurrencyPair == null) {
                throw new RuntimeException("Wrong currency pair");
            }
            if (baseType == OrderBaseType.STOP_LIMIT && stop == null) {
                throw new RuntimeException("Try to create stop-order without stop rate");
            }
            orderCreateDto = orderService.prepareNewOrder(activeCurrencyPair, orderType, principalEmail, amount, rate, baseType);
            orderCreateDto.setOrderBaseType(baseType);
            orderCreateDto.setStop(stop);
            /**/
            OrderValidationDto orderValidationDto = orderService.validateOrder(orderCreateDto, UserRole.ADMINISTRATOR);
            Map<String, Object> errorMap = orderValidationDto.getErrors();
            orderCreateSummaryDto = new OrderCreateSummaryDto(orderCreateDto, new Locale("en"));
            if (!errorMap.isEmpty()) {
                stringBuilder.append("Error map: <br>");
                for (Map.Entry<String, Object> pair : errorMap.entrySet()) {
                    stringBuilder.append(pair.getKey() + "  " + pair.getValue() + "<br>");
                    pair.setValue("message");
                }
                errorMap.put("order", orderCreateSummaryDto);
                throw new OrderParamsWrongException();
            } else {
            }
            stringBuilder
                    .append(" amount: " + orderCreateSummaryDto.getAmount() + " " + amount + " " + compareObjects(orderCreateSummaryDto.getAmount(), amount)).append("<br>")
                    .append(" getCurrencyPair: " + orderCreateDto.getCurrencyPair().getName() + " " + currencyPair + " " + orderCreateDto.getCurrencyPair().getName().equals(currencyPair)).append("<br>")
                    .append(" getOperationType: " + orderCreateDto.getOperationType() + " " + orderType + " " + orderCreateDto.getOperationType().equals(orderType)).append("<br>")
                    .append(" orderCreateDto.getExchangeRate(): " + orderCreateDto.getExchangeRate() + " " + rate + " " + compareObjects(orderCreateDto.getExchangeRate(), rate)).append("<br>")
                    .append(" orderCreateDto.getOrderBaseType(): " + orderCreateDto.getOrderBaseType() + " " + baseType + " " + orderCreateDto.getOrderBaseType().equals(baseType)).append("<br>")
                    .append(" orderCreateDto.getTotal(): " + orderCreateDto.getTotal() + " " + amount.multiply(rate) + " " + compareObjects(orderCreateDto.getTotal(), amount.multiply(rate))).append("<br>");


            boolean isOrderCreateCorrect = compareObjects(orderCreateSummaryDto.getAmount(), amount)
                    && orderCreateDto.getCurrencyPair().getName().equals(currencyPair)
                    && orderCreateDto.getOperationType().equals(orderType)
                    && compareObjects(orderCreateDto.getExchangeRate(), rate)
                    && compareObjects(orderCreateDto.getTotal(), amount.multiply(rate))
                    && orderCreateDto.getOrderBaseType().equals(baseType);
            if (!isOrderCreateCorrect) throw new CoinTestException("orderCreateDto incorrect!");
            stringBuilder.append("Order " + currencyPair + " works!").append("<br>");

            response = orderService.createOrder(orderCreateDto, CREATE, new Locale("en"));
            return this;
        }
    }
}
