package me.exrates;

import com.neemre.btcdcli4j.core.BitcoindException;
import com.neemre.btcdcli4j.core.CommunicationException;
import com.neemre.btcdcli4j.core.client.BtcdClient;
import com.neemre.btcdcli4j.core.client.BtcdClientImpl;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.dto.RefillRequestAddressDto;
import me.exrates.model.dto.RefillRequestBtcInfoDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.RefillRequestParamsDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.invoice.RefillStatusEnum;
import me.exrates.service.BitcoinService;
import me.exrates.service.InputOutputService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.merchantStrategy.IRefillable;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static me.exrates.model.enums.OperationType.INPUT;
import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.CREATE_BY_USER;

@Service
public class BtcCoinTesterImpl implements CoinTester {

    @Autowired
    Map<String, IRefillable> reffilableServiceMap;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private InputOutputService inputOutputService;

    @Autowired
    RefillService refillService;

    @Override
    public void testCoin(String name, double refillAmount) throws IOException, BitcoindException, CommunicationException, InterruptedException {
        BitcoinService bitcoinService = (BitcoinService) getMerchantServiceByName(name, reffilableServiceMap);
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        CloseableHttpClient httpProvider = HttpClients.custom().setConnectionManager(cm)
                .build();

        Properties properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream(bitcoinService.getNodePropertySource()));
        properties.setProperty("node.bitcoind.rpc.port", "8089");

        Properties passPropertySource = merchantService.getPassMerchantProperties(name);

        properties.setProperty("node.bitcoind.rpc.user", passPropertySource.getProperty("node.bitcoind.rpc.user"));
        properties.setProperty("node.bitcoind.rpc.password", passPropertySource.getProperty("node.bitcoind.rpc.password"));

        BtcdClient btcdClient = new BtcdClientImpl(httpProvider, properties);
        System.out.println("WORKING " + btcdClient.getNewAddress());


        RefillRequestParamsDto requestParamsDto = new RefillRequestParamsDto();
        requestParamsDto.setChildMerchant("");
        requestParamsDto.setCurrency(309);//todo
        requestParamsDto.setGenerateNewAddress(true);
        requestParamsDto.setMerchant(321);//todo
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

        RefillRequestCreateDto request = new RefillRequestCreateDto(requestParamsDto, creditsOperation, beginStatus, locale);

        Map<String, Object> refillRequest = refillService.createRefillRequest(request);
        String addressForRefill = (String)((Map)refillRequest.get("params")).get("address");
        List<RefillRequestAddressDto> byAddressMerchantAndCurrency = refillService.findByAddressMerchantAndCurrency(addressForRefill, 321, 309);//todo id
        assert byAddressMerchantAndCurrency.size() > 0;


//        Thread.sleep(3*60*1000);
//
//        refillService.
        System.out.println("ADDRESS FRO REFILL 8090 " + addressForRefill);
        System.out.println("BALANCE = " + btcdClient.getBalance());
        String txHash = btcdClient.sendToAddress(addressForRefill, new BigDecimal(refillAmount));

        System.out.println("hash is " + txHash);

        Optional<RefillRequestBtcInfoDto> acceptedRequest = Optional.empty();
        do{
            acceptedRequest = refillService.findRefillRequestByAddressAndMerchantIdAndCurrencyIdAndTransactionId(addressForRefill,321, 309, txHash);
            if(!acceptedRequest.isPresent()) Thread.sleep(1000);
            else {
                System.out.println("accepted amount " + acceptedRequest.get().getAmount());
                System.out.println("refill amount " + refillAmount);
                assert acceptedRequest.get().getAmount().equals(BigDecimal.valueOf(refillAmount));
            }
        } while (!acceptedRequest.isPresent());

        System.out.println("REQUEST FINDED");
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
