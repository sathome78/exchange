package me.exrates.service.autist;

import lombok.SneakyThrows;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.MerchantSpecParamDto;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.websocket.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static me.exrates.service.autist.AunitServiceImpl.AUNIT_CURRENCY;
import static me.exrates.service.autist.AunitServiceImpl.AUNIT_MERCHANT;
import static me.exrates.service.autist.MemoDecryptor.decryptBTSmemo;

//@Log4j2(topic = "aunit")
@PropertySource("classpath:/merchants/aunit.properties")
@ClientEndpoint
@Service
public class AunitNodeServiceImpl {

    private @Value("${aunit.node.ws}")String wsUrl;
    private @Value("${aunit.mainAddress}")String systemAddress;
    private @Value("${aunit.pk.path}")String pkFilePath;
    private URI WS_SERVER_URL;
    private Session session;
    private volatile RemoteEndpoint.Basic endpoint = null;
    private final Merchant merchant;
    private final Currency currency;

    private final MerchantService merchantService;
    private final CurrencyService currencyService;
    private final MerchantSpecParamsDao merchantSpecParamsDao;
    private final AunitService aunitService;
    private final RefillService refillService;

    /*todo get it from outer file*/
    static String privateKey = "5JZ4ZrZ7GXKGKVgqJ6ZKHNDfJAe2K1B58sUVHspA9iLQ3UBG6Lh";
    static final String accountAddress = "1.2.23845"; //todo

    private int latIrreversableBlocknumber = 0;
    private final String lastIrreversebleBlock = "last_irreversible_block_num";

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Autowired
    public AunitNodeServiceImpl(MerchantService merchantService, CurrencyService currencyService, MerchantSpecParamsDao merchantSpecParamsDao, AunitService aunitService, RefillService refillService) {
        this.merchant = merchantService.findByName(AUNIT_MERCHANT);
        this.currency = currencyService.findByName(AUNIT_CURRENCY);
        MerchantSpecParamDto byMerchantIdAndParamName = merchantSpecParamsDao.getByMerchantIdAndParamName(merchant.getId(), lastIrreversebleBlock);
        latIrreversableBlocknumber = Integer.valueOf(byMerchantIdAndParamName.getParamValue());
        this.merchantService = merchantService;
        this.currencyService = currencyService;
        this.merchantSpecParamsDao = merchantSpecParamsDao;
        this.aunitService = aunitService;
        this.refillService = refillService;
    }

    @PostConstruct
    public void init() {
        WS_SERVER_URL = URI.create(wsUrl);
        connectAndSubscribe();
    }

    private void connectAndSubscribe() {
        try {
            session = ContainerProvider.getWebSocketContainer()
                    .connectToServer(this, WS_SERVER_URL);
            session.setMaxBinaryMessageBufferSize(5012000);
            session.setMaxTextMessageBufferSize(5012000);
            session.setMaxIdleTimeout(Long.MAX_VALUE);

            endpoint = session.getBasicRemote();
            subscribeToTransactions();
        } catch (Exception e) {
            System.out.println("gabella");
            e.printStackTrace();
        }
    }

    private void subscribeToTransactions() throws IOException, NoSuchAlgorithmException {
        JSONObject login = new JSONObject();
        login.put("id", 0);
        login.put("method", "call");
        login.put("params", new JSONArray().put(1).put("login").put(new JSONArray().put("").put("")));

        JSONObject db = new JSONObject();
        db.put("id", 1);
        db.put("method", "call");
        db.put("params", new JSONArray().put(1).put("database").put(new JSONArray()));

        JSONObject netw = new JSONObject();
        netw.put("id", 2);
        netw.put("method", "call");
        netw.put("params", new JSONArray().put(1).put("network_broadcast").put(new JSONArray()));

        JSONObject history = new JSONObject();
        history.put("id", 3);
        history.put("method", "call");
        history.put("params", new JSONArray().put(1).put("history").put(new JSONArray()));

        JSONObject orders = new JSONObject();
        orders.put("id", 4);
        orders.put("method", "call");
        orders.put("params", new JSONArray().put(1).put("orders").put(new JSONArray()));

        JSONObject chainId = new JSONObject();
        chainId.put("id", 5);
        chainId.put("method", "call");
        chainId.put("params", new JSONArray().put(2).put("get_chain_id").put(new JSONArray().put(new JSONArray())));

        JSONObject get_object = new JSONObject();
        get_object.put("id", 6);
        get_object.put("method", "call");
        get_object.put("params", new JSONArray().put(2).put("get_objects").put(new JSONArray().put(new JSONArray().put("2.1.0"))));


        JSONObject subscribe = new JSONObject();
        subscribe.put("id", 7);
        subscribe.put("method", "call");
        subscribe.put("params", new JSONArray().put(2).put("set_subscribe_callback").put(new JSONArray().put(0).put(false)));


        System.out.println(login);
        endpoint.sendText(login.toString());

        System.out.println(db.toString());
        endpoint.sendText(db.toString());

        System.out.println(netw);
        endpoint.sendText(netw.toString());

        System.out.println(history);
        endpoint.sendText(history.toString());

        System.out.println(orders);
        endpoint.sendText(orders.toString());

        System.out.println(chainId);
        endpoint.sendText(chainId.toString());

        System.out.println(subscribe);
        endpoint.sendText(subscribe.toString());

        System.out.println(get_object);
        endpoint.sendText(get_object.toString());

       /* System.out.println("block with tx " + block.toString());
        endpoint.sendText(block.toString());*/

    }

    @OnMessage()
    public void onMessage(String msg) {
        if(msg.contains("notice")) setIrreversableBlock(msg);
        else if (msg.contains("previous")) processIrreversebleBlock(msg);
        else System.out.println("unrecogrinzed msg aunit \n" + msg);;

        System.out.println(msg);
    }

    @SneakyThrows
    private void getBlock(int blockNum) {
        JSONObject block = new JSONObject();
        block.put("id", 10);
        block.put("method", "call");
        block.put("params", new JSONArray().put(2).put("get_block").put(new JSONArray().put(blockNum)));
        endpoint.sendText(block.toString());
    }

    private  void processIrreversebleBlock(String trx) {
        System.out.println("json for process trx \n " + trx);
        JSONObject block = new JSONObject(trx);
        if(block.getJSONObject("result").getJSONArray("transactions").length() == 0) return;
        JSONArray transactions = block.getJSONObject("result").getJSONArray("transactions");

        List<String> lisfOfMemo = refillService.getListOfValidAddressByMerchantIdAndCurrency(merchant.getId(), currency.getId());
    try {
        for (int i = 0; i < transactions.length(); i++) {
            JSONObject transaction = transactions.getJSONObject(i).getJSONArray("operations").getJSONArray(0).getJSONObject(1);

            if(transaction.getString("to").equals(accountAddress)) makeRefill(lisfOfMemo, transaction);

        }

    } catch (JSONException e){
        System.out.println("JSON exception while parsing \n " + trx);
        e.printStackTrace();
    }

    }


    @SneakyThrows
    private void makeRefill(List<String> lisfOfMemo, JSONObject transaction) {
        System.out.println("start make refill");
        JSONObject memo = transaction.getJSONObject("memo");
        try {
            String memoText = decryptBTSmemo(privateKey, memo.toString());
            System.out.println("decoded memo = " + memoText);
            if(lisfOfMemo.contains(memoText)){
                BigDecimal amount = reduceAmount(transaction.getJSONObject("amount").getInt("amount"));
                prepareAndProcessTx("", memoText, amount);
            }
        } catch (NoSuchAlgorithmException e){
            System.out.println(e.getClass());
        }
    }

    private void prepareAndProcessTx(String hash, String address, BigDecimal amount) {
        Map<String, String> map = new HashMap<>();
        map.put("address", address);
        map.put("hash", hash);
        map.put("amount", amount.toString());
        RefillRequestAcceptDto requestAcceptDto = aunitService.createRequest(hash, address, amount);
        try {
            aunitService.processPayment(map);
        } catch (RefillRequestAppropriateNotFoundException e) {
            e.printStackTrace();
        }
    }

    private BigDecimal reduceAmount(int amount) {
        return new BigDecimal(amount).multiply(new BigDecimal(Math.pow(10, -5))).setScale(5, RoundingMode.HALF_DOWN);
    }

    private void setIrreversableBlock(String msg) {
        JSONObject message = new JSONObject(msg);
        int blockNumber = message.getJSONArray("params").getJSONArray(1).getJSONArray(0).getJSONObject(0).getInt("last_irreversible_block_num");
        synchronized (this) {
            if (blockNumber > latIrreversableBlocknumber) {
                for (;latIrreversableBlocknumber <= blockNumber; latIrreversableBlocknumber++){
                    getBlock(latIrreversableBlocknumber);
                }
                merchantSpecParamsDao.updateParam(merchant.getName(), lastIrreversebleBlock, String.valueOf(latIrreversableBlocknumber));
            }
        }
    }

    @PreDestroy
    public void onShutdown() {
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
//            log.error("error closing session");
        }
    }

//    public static void main(String[] args) throws NoSuchAlgorithmException {
//        String memoText = decryptBTSmemo(privateKey, "{\"from\":\"AUNIT7k3nL56J7hh2yGHgWTUk9bGdjG2LL1S7egQDJYZ71MQtU3CqB5\",\"to\":\"AUNIT6Y1omrtPmYEHBaK7gdAeqdGASPariaCXGm83Phjc2NDEuxYfzV\",\"nonce\":\"394455594312810\",\"message\":\"b823f75a3f2572bbc327ab3423d0cbf9\"}");
//
//        System.out.println(memoText);
//
//
//    }

    public static void main(String[] args) throws NoSuchAlgorithmException {
        String s = decryptBTSmemo("5JZ4ZrZ7GXKGKVgqJ6ZKHNDfJAe2K1B58sUVHspA9iLQ3UBG6Lh",
                "{\"from\":\"AUNIT7k3nL56J7hh2yGHgWTUk9bGdjG2LL1S7egQDJYZ71MQtU3CqB5\",\"to\":\"AUNIT6Y1omrtPmYEHBaK7gdAeqdGASPariaCXGm83Phjc2NDEuxYfzV\",\"nonce\":\"394357881684245\",\"message\":\"70c9c5459c69e2182693c604f6102dee\"}");
        System.out.println(s);
    }

}

