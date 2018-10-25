package me.exrates.service.autist;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;

@ClientEndpoint
@Service
public class AunitNodeServiceImpl {
    private String wsUrl = "ws://ec2-18-223-213-72.us-east-2.compute.amazonaws.com:8090";
    private URI WS_SERVER_URL;
    private Session session;
    private volatile RemoteEndpoint.Basic endpoint = null;

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
        subscribe.put("params", new JSONArray().put(2).put("set_subscribe_callback").put(new JSONArray().put(0).put(true)));


        JSONObject block = new JSONObject();
        block.put("id", 10);
        block.put("method", "call");
        block.put("params", new JSONArray().put(2).put("get_block").put(new JSONArray().put(2568997)));

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

        System.out.println("block with tx " + block.toString());
        endpoint.sendText(block.toString());

    }

    @OnMessage
    public void onMessage(String msg) {
        System.out.println(msg);

    }
}
