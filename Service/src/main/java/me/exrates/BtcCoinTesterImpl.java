package me.exrates;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import com.neemre.btcdcli4j.core.client.BtcdClient;
import com.neemre.btcdcli4j.core.client.BtcdClientImpl;
import com.neemre.btcdcli4j.core.domain.Transaction;
import lombok.NoArgsConstructor;
import me.exrates.model.CreditsOperation;
import me.exrates.model.CurrencyPair;
import me.exrates.model.Payment;
import me.exrates.model.WithdrawRequest;
import me.exrates.model.dto.*;
import me.exrates.model.dto.merchants.btc.BtcPaymentResultDetailedDto;
import me.exrates.model.dto.merchants.btc.BtcWalletPaymentItemDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.model.enums.invoice.RefillStatusEnum;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;
import me.exrates.service.*;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.exception.api.OrderParamsWrongException;
import me.exrates.service.merchantStrategy.IRefillable;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static me.exrates.model.enums.OperationType.INPUT;
import static me.exrates.model.enums.OperationType.OUTPUT;
import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.CREATE_BY_USER;

@NoArgsConstructor
public class BtcCoinTesterImpl implements CoinTester {

    public static final String principalEmail = "mikita.malykov@upholding.biz";
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

    private int currencyId;
    private int merchantId;
    private String name;
    private final static Integer TIME_FOR_REFILL = 10000;
    private BtcdClient btcdClient;
    private Object withdrawTest = new Object();
    private int withdrawStatus = 0;

    public void initBot(String name) throws BitcoindException, IOException, CommunicationException {
        merchantId = merchantService.findByName(name).getId();
        currencyId = currencyService.findByName(name).getId();
        this.name = name;
        btcdClient = prepareBtcClient(name);
    }

    @Override
    public void testCoin(double refillAmount) throws IOException, BitcoindException, CommunicationException, InterruptedException {
        RefillRequestCreateDto request = prepareRefillRequest(merchantId, currencyId);

        testAddressGeneration();
//        checkRefill(refillAmount*10, merchantId, currencyId, request);

//        testAutoWithdraw(refillAmount/4);
//        testManualWithdraw(refillAmount / 4);
        testOrder(OperationType.SELL, new BigDecimal(0.001), new BigDecimal(0.001), OrderBaseType.LIMIT, name + "/BTC", new BigDecimal(0.00));

    }

    private void testOrder(OperationType orderType, BigDecimal amount, BigDecimal rate, OrderBaseType baseType, String currencyPair, BigDecimal stop) {
        try {
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
            OrderCreateDto orderCreateDto = orderService.prepareNewOrder(activeCurrencyPair, orderType, principalEmail, amount, rate, baseType);
            orderCreateDto.setOrderBaseType(baseType);
            orderCreateDto.setStop(stop);
            /**/
            OrderValidationDto orderValidationDto = orderService.validateOrder(orderCreateDto, userService.getUserRoleFromSecurityContext());
            Map<String, Object> errorMap = orderValidationDto.getErrors();
            orderCreateSummaryDto = new OrderCreateSummaryDto(orderCreateDto, new Locale("en"));
            if (!errorMap.isEmpty()) {
                for (Map.Entry<String, Object> pair : errorMap.entrySet()) {
                    pair.setValue("message");
                }
                errorMap.put("order", orderCreateSummaryDto);
                throw new OrderParamsWrongException();
            } else {
            }
            //{"currencyPairName":"KOD/BTC","operationTypeName":"SELL","balance":"9999.00","amount":"1.00","exrate":"1.00","total":"1.00",
            // "commission":"0.002","totalWithComission":"0.998","stop":"0.00","baseType":"LIMIT"}
            assert orderCreateSummaryDto.getAmount().equals(amount);
            assert orderCreateDto.getCurrencyPair().getName().equals(currencyPair);
            assert orderCreateDto.getOperationType().equals(orderType);
            assert orderCreateDto.getExchangeRate().equals(rate);
            assert orderCreateDto.getTotal().equals(amount.multiply(rate));
            assert orderCreateDto.getOrderBaseType().equals(baseType);
            System.out.println("Order " + currencyPair + " works!");
        } catch (Exception e){
            throw e;
        }
    }

    private void testAddressGeneration() throws BitcoindException, CommunicationException {
        assert btcdClient.getNewAddress().length() > 10;
        assert btcdClient.getNewAddress().length() > 10;

    }

    private void testAutoWithdraw(double refillAmount) throws BitcoindException, CommunicationException, InterruptedException {
        synchronized (withdrawTest) {
            String withdrawAddress = btcdClient.getNewAddress();
            System.out.println("address for withdraw " + withdrawAddress);

            WithdrawRequestParamsDto withdrawRequestParamsDto = new WithdrawRequestParamsDto();
            withdrawRequestParamsDto.setCurrency(currencyId);
            withdrawRequestParamsDto.setMerchant(merchantId);
            withdrawRequestParamsDto.setDestination(withdrawAddress);
            withdrawRequestParamsDto.setDestinationTag("");
            withdrawRequestParamsDto.setOperationType(OperationType.OUTPUT);
            withdrawRequestParamsDto.setSum(BigDecimal.valueOf(refillAmount));

            Payment payment = new Payment(OUTPUT);
            payment.setCurrency(withdrawRequestParamsDto.getCurrency());
            payment.setMerchant(withdrawRequestParamsDto.getMerchant());
            payment.setSum(withdrawRequestParamsDto.getSum().doubleValue());
            payment.setDestination(withdrawRequestParamsDto.getDestination());
            payment.setDestinationTag(withdrawRequestParamsDto.getDestinationTag());
            CreditsOperation creditsOperation = inputOutputService.prepareCreditsOperation(payment, principalEmail, new Locale("en"))
                    .orElseThrow(InvalidAmountException::new);
            WithdrawStatusEnum beginStatus = (WithdrawStatusEnum) WithdrawStatusEnum.getBeginState();

            WithdrawRequestCreateDto withdrawRequestCreateDto = new WithdrawRequestCreateDto(withdrawRequestParamsDto, creditsOperation, beginStatus);
            setAutoWithdraw(true);
            withdrawService.createWithdrawalRequest(withdrawRequestCreateDto, new Locale("en"));

            Optional<WithdrawRequest> withdrawRequestByAddressOptional = withdrawService.getWithdrawRequestByAddress(withdrawAddress);
            assert withdrawRequestByAddressOptional.isPresent();

            Integer requestId = withdrawRequestByAddressOptional.get().getId();
            WithdrawRequestFlatDto flatWithdrawRequest;

            do {
                flatWithdrawRequest = withdrawService.getFlatById(requestId).get();
                withdrawStatus = flatWithdrawRequest.getStatus().getCode();
                if (withdrawStatus == 10) {
                    Transaction transaction = btcdClient.getTransaction(flatWithdrawRequest.getTransactionHash());
                    System.out.println("trx from btc " + transaction);
                    assert transaction.getAmount().equals(flatWithdrawRequest.getAmount().min(flatWithdrawRequest.getCommissionAmount()));
                }
                System.out.println("Checking withdraw...");
                Thread.sleep(2000);
            } while (withdrawStatus != 10);

            System.out.println("Withdraw works");


        }
    }

    public void testManualWithdraw(double amount) throws BitcoindException, CommunicationException, InterruptedException {
        synchronized (withdrawTest) {
            setAutoWithdraw(false);
        }
        String withdrawAddress = btcdClient.getNewAddress();
        System.out.println("address for manual withdraw " + withdrawAddress);

        BitcoinService walletService = (BitcoinService) getMerchantServiceByName(name, reffilableServiceMap);
        List<BtcWalletPaymentItemDto> payments = new LinkedList<>();
        payments.add(new BtcWalletPaymentItemDto(withdrawAddress, BigDecimal.valueOf(amount)));
        BtcPaymentResultDetailedDto btcPaymentResultDetailedDto = walletService.sendToMany(payments).get(0);

        Transaction transaction = null;
        do {
            try {
                transaction = btcdClient.getTransaction(btcPaymentResultDetailedDto.getTxId());
            } catch (BitcoindException e){
                System.out.println("Error from btc " + e.getMessage());
            }
            Thread.sleep(2000);
            System.out.println("Checking manual transaction");
            if(transaction != null){
                System.out.println("Manual trx = " + transaction);
                assert new BigDecimal(btcPaymentResultDetailedDto.getAmount()).equals(transaction.getAmount());
            }
        } while (transaction == null);


    }

    private void setAutoWithdraw(boolean isEnabled) {
        MerchantCurrencyOptionsDto merchantCurrencyOptionsDto = new MerchantCurrencyOptionsDto();
        merchantCurrencyOptionsDto.setCurrencyId(currencyId);
        merchantCurrencyOptionsDto.setMerchantId(merchantId);
        merchantCurrencyOptionsDto.setWithdrawAutoDelaySeconds(1);
        merchantCurrencyOptionsDto.setWithdrawAutoEnabled(isEnabled);
        merchantCurrencyOptionsDto.setWithdrawAutoThresholdAmount(BigDecimal.valueOf(999999));
        withdrawService.setAutoWithdrawParams(merchantCurrencyOptionsDto);
    }

    private void checkRefill(double refillAmount, int merchantId, int currencyId, RefillRequestCreateDto request) throws BitcoindException, CommunicationException, InterruptedException {
        Map<String, Object> refillRequest = refillService.createRefillRequest(request);
        String addressForRefill = (String) ((Map) refillRequest.get("params")).get("address");
        List<RefillRequestAddressDto> byAddressMerchantAndCurrency = refillService.findByAddressMerchantAndCurrency(addressForRefill, merchantId, currencyId);
        assert byAddressMerchantAndCurrency.size() > 0;

        System.out.println("ADDRESS FRO REFILL FROM BIRZHA " + addressForRefill);
        System.out.println("BALANCE = " + btcdClient.getBalance());
        try {
            btcdClient.walletPassphrase("pass123", 2000);
        } catch (Exception e) {
            System.out.println("Error while trying encrypted wallet " + e.getMessage());
        }
        System.out.println("balance = " + btcdClient.getBalance());
        System.out.println("refill sum = " + refillAmount);
        String txHash = btcdClient.sendToAddress(addressForRefill, new BigDecimal(refillAmount));

        Optional<RefillRequestBtcInfoDto> acceptedRequest;
        Integer minConfirmation = ((BitcoinService) getMerchantServiceByName(name, reffilableServiceMap)).minConfirmationsRefill();

        do {
            boolean isConfirmationsEnough = false;
            acceptedRequest = refillService.findRefillRequestByAddressAndMerchantIdAndCurrencyIdAndTransactionId(merchantId, currencyId, txHash);
            if (!acceptedRequest.isPresent()) {
                if (isConfirmationsEnough) throw new RuntimeException("Confirmation enough, but refill not working!");
                System.out.println("NOT NOW(");
                Thread.sleep(2000);
                Transaction transaction = btcdClient.getTransaction(txHash);
                if (transaction.getConfirmations() >= minConfirmation) {
                    Thread.sleep(TIME_FOR_REFILL);
                    isConfirmationsEnough = true;
                }
                System.out.println("GET TRANSACTION " + transaction);
            } else {
                System.out.println("accepted amount " + acceptedRequest.get().getAmount());
                System.out.println("refill amount " + refillAmount);
                assert acceptedRequest.get().getAmount().equals(BigDecimal.valueOf(refillAmount));
            }
        } while (!acceptedRequest.isPresent());

        System.out.println("REQUEST FINDED");
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

    public static void main(String[] args) {
//        CoinTester rimeTest = new BtcCoinTesterImpl();
//        rimeTest.testCoin("RIME", 0.1);
    }


}
