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
import org.springframework.context.annotation.Scope;
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
@Scope(scopeName = "prototype")
public class AunitNodeServiceImpl {

    private @Value("${aunit.node.ws}")String wsUrl;
    private @Value("${aunit.mainAddress}")String systemAddress;
    private @Value("${aunit.pk.path}")String pkFilePath;
    private URI WS_SERVER_URL;
    private Session session;
    private volatile RemoteEndpoint.Basic endpoint = null;
    private final Merchant merchaintId;
    private final Currency currencyId;

    @Autowired
    private MerchantService merchantService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private MerchantSpecParamsDao merchantSpecParamsDao;
    @Autowired
    private AunitService aunitService;
    @Autowired
    private RefillService refillService;

    /*todo get it from outer file*/
    String privateKey = "5J15nNH6AvjLY6kryEA1VNZ9s6zkqFsFzHZGGtYBwL3BF5gG9Qd";
    final String accountAddress = "1.2.20683"; //todo

    private int latIrreversableBlocknumber = 0;
    private final String lastIrreversebleBlock = "last_irreverseble_block";

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public AunitNodeServiceImpl() {
        this.merchaintId = merchantService.findByName(AUNIT_CURRENCY);
        this.currencyId = currencyService.findByName(AUNIT_MERCHANT);
        latIrreversableBlocknumber = Integer.valueOf(merchantSpecParamsDao.getByMerchantNameAndParamName(merchaintId.getName(), lastIrreversebleBlock).getParamValue());
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
        String attributes = new JSONObject(msg).names().toString();

        if(attributes.contains("method")) setIrreversableBlock(msg);
        else if (attributes.contains("id")) processIrreversebleBlock(msg);
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
        JSONObject block = new JSONObject(trx);
        JSONArray transactions = block.getJSONObject("result").getJSONArray("operations");
        List<String> lisfOfMemo = refillService.getListOfValidAddressByMerchantIdAndCurrency(merchaintId.getId(), currencyId.getId());

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
                for (int from = blockNumber; from <= latIrreversableBlocknumber; from++){
                    getBlock(from);
                }
                latIrreversableBlocknumber = blockNumber;
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
}
