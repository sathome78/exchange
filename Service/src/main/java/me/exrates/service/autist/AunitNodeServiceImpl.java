package me.exrates.service.autist;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestFlatDto;
import me.exrates.service.RefillService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
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
import java.util.concurrent.TimeUnit;

import static me.exrates.service.autist.MemoDecryptor.decryptBTSmemo;

@Log4j2(topic = "aunit")
@PropertySource("classpath:/merchants/aunit.properties")
@ClientEndpoint
@Service
public class AunitNodeServiceImpl {

    private @Value("${tron.mainAccountHEXAddress}")String MAIN_ADDRESS_HEX;
    private String wsUrl = "ws://ec2-18-223-213-72.us-east-2.compute.amazonaws.com:8090";
    private URI WS_SERVER_URL;
    private Session session;
    private volatile RemoteEndpoint.Basic endpoint = null;
    /*todo get it from outer file*/
    String privateKey = "";

    private long latIrreversableBlocknumber = 0;

    @Autowired
    private AunitService aunitService;
    @Autowired
    private RefillService refillService;

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void init() {
        WS_SERVER_URL = URI.create(wsUrl);
        connectAndSubscribe();
        scheduler.scheduleAtFixedRate(this::checkUnconfirmed, 3, 5, TimeUnit.MINUTES);
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
        subscribe.put("params", new JSONArray().put(2).put("set_subscribe_callback").put(new JSONArray().put(0).put(true)));


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

    @OnMessage
    public void onMessage(String msg) {
        System.out.println(msg);
       /* if (msg.contains("trx")) {

        } else if (msg.contains("last_irreversible_block_num")) {
            parseAndSetIrreversableBlock(msg);
        }*/
    }

    private void parseAndProcessTransaction(String trx) {
        String memo = "";
        String address = decryptBTSmemo(privateKey, memo);
        long rawAmount = 1;
        String txHash = "fwefwef";
        long txBlock = 2;
        BigDecimal amount = reduceAmount(rawAmount);
        RefillRequestAcceptDto requestAcceptDto = aunitService.createRequest(txHash, address, amount);
        if (txBlock >= latIrreversableBlocknumber) {
            prepareAndProcessTx(txHash, address, amount);
        } else {
            aunitService.putOnBchExam(requestAcceptDto);
        }

    }

    private void checkUnconfirmed() {
        List<RefillRequestFlatDto> dtos = refillService.getInExamineWithChildTokensByMerchantIdAndCurrencyIdList(aunitService.getMerchant().getId(), aunitService.getCurrency().getId());
        dtos.forEach(p->{
            try {
                if (checkIsTransactionConfirmed(p.getMerchantTransactionId())) {
                   prepareAndProcessTx(p.getAddress(), p.getMerchantTransactionId(), p.getAmount());
                }
            } catch (Exception e) {
                log.error(e);
            }
        });
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

    /*todo*/
    private boolean checkIsTransactionConfirmed(String txHash) {
        return true;
    }

    private BigDecimal reduceAmount(long amount) {
        return new BigDecimal(amount).multiply(new BigDecimal(Math.pow(10, -5))).setScale(5, RoundingMode.HALF_DOWN);
    }

    private void parseAndSetIrreversableBlock(String msg) {
        JSONObject message = new JSONObject(msg);
        long blockNumber = message.getJSONArray("params").getJSONArray(1).getJSONArray(0).getJSONObject(0).getLong("last_irreversible_block_num");
        synchronized (this) {
            if (blockNumber > latIrreversableBlocknumber) {
                latIrreversableBlocknumber = blockNumber;
            }
        }
    }

}
