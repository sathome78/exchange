package me.exrates.service.autist;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MerchantSpecParamsDao;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.json.JSONArray;
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

@Log4j2(topic = "aunit")
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
    String privateKey = "5J15nNH6AvjLY6kryEA1VNZ9s6zkqFsFzHZGGtYBwL3BF5gG9Qd";
    final String accountAddress = "1.2.20683"; //todo

    private int latIrreversableBlocknumber = 0;
    private final String lastIrreversebleBlock = "last_irreverseble_block";

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Autowired
    public AunitNodeServiceImpl(MerchantService merchantService, CurrencyService currencyService, MerchantSpecParamsDao merchantSpecParamsDao, AunitService aunitService, RefillService refillService) {
        this.merchant = merchantService.findByName(AUNIT_MERCHANT);
        this.currency = currencyService.findByName(AUNIT_CURRENCY);
        latIrreversableBlocknumber = Integer.valueOf(merchantSpecParamsDao.getByMerchantIdAndParamName(merchant.getId(), lastIrreversebleBlock).getParamValue());
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
        else log.error("Unrecognized msg from node: " + msg);

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

    private void processIrreversebleBlock(String trx) {
        System.out.println("json for process trx \n " + trx);
        JSONObject block = new JSONObject(trx);

        if(!trx.contains("operations"))return;//todo

        JSONArray transactions = block.getJSONObject("result").getJSONArray("operations");
        List<String> lisfOfMemo = refillService.getListOfValidAddressByMerchantIdAndCurrency(merchant.getId(), currency.getId());

        for (int i = 0; i < transactions.length(); i++) {
            JSONObject transaction = transactions.getJSONArray(i).getJSONObject(1);
            if(!transaction.getString("to").equals(accountAddress)){
                System.out.println("Not our accountAddress, continue");
                continue;
            }
            makeRefill(lisfOfMemo, transaction);
        }

    }

    @SneakyThrows
    private void makeRefill(List<String> lisfOfMemo, JSONObject transaction) {
        JSONObject memo = transaction.getJSONObject("memo");
        try {
            String memoText = decryptBTSmemo(privateKey, memo.toString());
            if(lisfOfMemo.contains(memoText)){
                BigDecimal amount = reduceAmount(transaction.getJSONObject("amount").getInt("amount"));
                prepareAndProcessTx(transaction.getString("signatures"), memoText, amount);
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
        try {
            aunitService.processPayment(map);
        } catch (RefillRequestAppropriateNotFoundException e) {
            log.error(e);
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
            log.error("error closing session");
        }
    }

    public static void main(String[] args) {
        String toParse = "{\"id\":22,\"jsonrpc\":\"2.0\",\"result\":{\"ref_block_num\":19644,\"ref_block_prefix\":3511477450,\"expiration\":\"2018-10-23T18:45:00\",\"operations\":[[0,{\"fee\":{\"amount\":12022,\"asset_id\":\"1.3.0\"},\"from\":\"1.2.20683\",\"to\":\"1.2.23845\",\"amount\":{\"amount\":1,\"asset_id\":\"1.3.0\"},\"memo\":{\"from\":\"AUNIT7k3nL56J7hh2yGHgWTUk9bGdjG2LL1S7egQDJYZ71MQtU3CqB5\",\"to\":\"AUNIT5kCUGorUo7KCT5uCRP8BdLMqVaDPukpbKayJ9WXXFXoDSmUKBp\",\"nonce\":\"394321991978825\",\"message\":\"a71db7dd9930357813c510f3be1ca608\"},\"extensions\":[]}]],\"extensions\":[],\"signatures\":[\"1f78f043114ec90ffe7c928755a6bf63bab8fac7336568765f06e61b4af39682ee430041e3a1c62de338b5e917cf69fe22d13028c412b821bc6b231cf37ce60acf\"],\"operation_results\":[[0,{}]]}}\n";
        JSONObject block = new JSONObject(toParse);
        JSONArray transactions = block.getJSONObject("result").getJSONArray("operations");
        System.out.println(transactions);
    }
}
