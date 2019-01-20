package me.exrates;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import com.neemre.btcdcli4j.core.client.BtcdClient;
import com.neemre.btcdcli4j.core.client.BtcdClientImpl;
import com.neemre.btcdcli4j.core.domain.Transaction;
import lombok.NoArgsConstructor;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.dto.*;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.invoice.RefillStatusEnum;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;
import me.exrates.service.*;
import me.exrates.service.exception.InvalidAmountException;
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

    @Autowired
    private Map<String, IRefillable> reffilableServiceMap;
    @Autowired
    private  MerchantService merchantService;
    @Autowired
    private InputOutputService inputOutputService;
    @Autowired
    private RefillService refillService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    WithdrawService withdrawService;

    private int currencyId;
    private int merchantId;
    private String name;
    private final static Integer TIME_FOR_REFILL = 10000;
    private BtcdClient btcdClient;

    public void initBot(String name) throws BitcoindException, IOException, CommunicationException {
        merchantId = merchantService.findByName(name).getId();
        currencyId = currencyService.findByName(name).getId();
        this.name = name;
        btcdClient = prepareBtcClient(name);
    }

    @Override
    public void testCoin(double refillAmount) throws IOException, BitcoindException, CommunicationException, InterruptedException {
        RefillRequestCreateDto request = prepareRefillRequest(merchantId, currencyId);
//        checkRefill(refillAmount, btcdClient, merchantId, currencyId, request);

        //WithdrawRequestCreateDto(id=null, userId=17050, userEmail=mikita.malykov@upholding.biz, userWalletId=1583504, currencyId=309,
        // currencyName=KOD, amount=0.10000000, commission=0.00020000, commissionId=20,
        // destinationWallet=RXD4rVSxhQnyC5hoSQ3wF9LWDvdXvG9nMM, destinationTag=,
        // merchantId=321, merchantDescription=KODCoin, statusId=1, recipientBankName=null,
        // recipientBankCode=null, userFullName=null, remark=null, autoEnabled=null, autoThresholdAmount=null, merchantCommissionAmount=0E-8)
        testWithdraw(refillAmount);
    }

    private void testWithdraw(double refillAmount) throws BitcoindException, CommunicationException {
        String withdrawAddress = btcdClient.getNewAddress();
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
        CreditsOperation creditsOperation = inputOutputService.prepareCreditsOperation(payment, "mikita.malykov@upholding.biz", new Locale("en"))
                .orElseThrow(InvalidAmountException::new);
        WithdrawStatusEnum beginStatus = (WithdrawStatusEnum) WithdrawStatusEnum.getBeginState();

        WithdrawRequestCreateDto withdrawRequestCreateDto = new WithdrawRequestCreateDto(withdrawRequestParamsDto, creditsOperation, beginStatus);
        withdrawService.createWithdrawalRequest(withdrawRequestCreateDto, new Locale("en"));
        //TODO retrieve заявку с базы
        //ждём статус 10
        //если статус не 10, а какая-то фигня - исключение
        //когда статус 10 - провряем чтобы пришли бабки
    }

    private void checkRefill(double refillAmount, BtcdClient btcdClient, int merchantId, int currencyId, RefillRequestCreateDto request) throws BitcoindException, CommunicationException, InterruptedException {
        Map<String, Object> refillRequest = refillService.createRefillRequest(request);
        String addressForRefill = (String)((Map)refillRequest.get("params")).get("address");
        List<RefillRequestAddressDto> byAddressMerchantAndCurrency = refillService.findByAddressMerchantAndCurrency(addressForRefill, merchantId, currencyId);
        assert byAddressMerchantAndCurrency.size() > 0;

        System.out.println("ADDRESS FRO REFILL 8090 " + addressForRefill);
        System.out.println("BALANCE = " + btcdClient.getBalance());
        try {
            btcdClient.walletPassphrase("pass123", 2000);
        } catch (Exception e){
            System.out.println("Error while trying encrypted wallet " + e.getMessage());
        }
        String txHash = btcdClient.sendToAddress(addressForRefill, new BigDecimal(refillAmount));

        Optional<RefillRequestBtcInfoDto> acceptedRequest;
        Integer minConfirmation = ((BitcoinService) getMerchantServiceByName(name, reffilableServiceMap)).minConfirmationsRefill();

        do{
            boolean isConfirmationsEnough = false;
            acceptedRequest = refillService.findRefillRequestByAddressAndMerchantIdAndCurrencyIdAndTransactionId(merchantId, currencyId, txHash);
            if(!acceptedRequest.isPresent()){
                if(isConfirmationsEnough) throw new RuntimeException("Confirmation enough, but refill not working!");
                System.out.println("NOT NOW(");
                Thread.sleep(2000);
                Transaction transaction = btcdClient.getTransaction(txHash);
                if(transaction.getConfirmations() >= minConfirmation){
                    Thread.sleep(TIME_FOR_REFILL);
                    isConfirmationsEnough = true;
                }
                System.out.println("GET TRANSACTION " + transaction);
            }
            else {
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
        CreditsOperation creditsOperation = inputOutputService.prepareCreditsOperation(payment, "mikita.malykov@upholding.biz", locale)
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
            if(e.getValue().getMerchantName().equals(name)) return e.getValue();
        }
        throw new RuntimeException("BitcoinService with ticker " + name + " not found!");
    }

    public static void main(String[] args) {
//        CoinTester rimeTest = new BtcCoinTesterImpl();
//        rimeTest.testCoin("RIME", 0.1);
    }
}
