package me.exrates.service.autist;

import lombok.SneakyThrows;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;
import java.io.IOException;
import java.net.URI;

//@ClientEndpoint
//@Service
public class AunitWsServiceImpl {

    private String wsUrl = "ws://ec2-18-223-213-72.us-east-2.compute.amazonaws.com:8092";
    private URI WS_SERVER_URL;
    private Session session;
    private volatile RemoteEndpoint.Basic endpoint = null;

    @PostConstruct
    public void init() {


        WS_SERVER_URL = URI.create(wsUrl);
        connectAndSubscribe();
        System.out.println("okay");
    }

    @SneakyThrows
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
            throw e;
        }
    }

    private void subscribeToTransactions() throws IOException {
        JSONObject object = new JSONObject();
        object.put("id", 1);
        object.put("method", "call");
        object.put("params", new JSONArray().put("database_api").put("set_block_applied_callback").put(new int[]{0}));
//        /* object.put("streams", new JSONArray().put("transactions"));*/
//        log.debug("message to send {}" + object.toString() );
        System.out.println(object);
        endpoint.sendText(object.toString());
    }
}
