package me.exrates.service.decred;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Merchant;
import me.exrates.service.MerchantService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.websocket.*;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.neemre.btcdcli4j.core.util.CollectionUtils.asList;

@Log4j2(topic = "decred")
@ClientEndpoint(configurator = DecredWSConfig.class)
@Service
@PropertySource("classpath:/merchants/decred.properties")
public class DecredWsService {

    /*@Autowired
    private MerchantService merchantService;

    private @Value("${decred.server.ws.url}") String wsUrl;
    private @Value("${decred.mainAddress}") String address;
    private URI WS_SERVER_URL;

    private Session session;
    private boolean access = false;
    private volatile RemoteEndpoint.Basic endpoint = null;
    private volatile boolean shutdown = false;
    private Merchant merchant;
    private static final String MERCHANT = "DCR";

    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);


    @PostConstruct
    private void init() {
        merchant = merchantService.findByName(MERCHANT);
        WS_SERVER_URL = URI.create(wsUrl);
        scheduler.scheduleAtFixedRate(this::connectAndSubscribe, 5, 300, TimeUnit.SECONDS);
        connectAndSubscribe();
    }


    public void connectAndSubscribe() {
        if (session == null || !session.isOpen()) {
            System.out.println(session);
            connect();
            subscribeToPayments();
            session.isOpen();
        }
    }

    private void connect() {
        try {
            log.debug("url {}", WS_SERVER_URL);
            session = ContainerProvider.getWebSocketContainer()
                    .connectToServer(this, WS_SERVER_URL);
            session.setMaxBinaryMessageBufferSize(5012000);
            session.setMaxTextMessageBufferSize(5012000);
            session.setMaxIdleTimeout(Long.MAX_VALUE);
            log.debug("session {}", session);
            endpoint = session.getBasicRemote();
            access = true;
            log.debug("decred node ws connection established");
        } catch (Exception e) {
            log.error("error connection to decred node {}", e);
        }
    }

    private void subscribeToPayments() {
        *//*RpcRequest rpcRequest = new RpcRequest("notifyreceived", new String[]{address}, null);*//*
        RpcRequest rpcRequest = new RpcRequest("notifyblocks", new Object[]{}, "1");
        JSONArray addressesArray = new JSONArray().put(address);
        JSONArray outpointsArray = new JSONArray();
        RpcRequest loadFilter = new RpcRequest("loadtxfilter", new Object[]{true, addressesArray, outpointsArray},"2");
        try {
            endpoint.sendText(rpcRequest.encode());
            endpoint.sendText(loadFilter.encode());
        } catch (Exception e) {
            log.error("error subscribe {}", e);
            throw new RuntimeException(e);
        }
    }

    @OnMessage
    public void onMessage(String msg) {
        log.debug("income decred message {}", msg);


    }

    @OnClose
    public void close(final Session session, final CloseReason reason) {
        log.error("Connection lost. Session closed : {}. Reason : {}", session, reason);
        if (!shutdown) {
            connectAndSubscribe();
        }
    }

    @PreDestroy
    public void onShutdown() {
        try {
            shutdown = true;
            session.close();
        } catch (IOException e) {
            log.error("error closing session");
        }
    }


    private class RpcRequest {

        private String jsonrpc = "1.0";

        private String method;

        private Object[] params;

        private String id;

        RpcRequest(String method, Object[] params, String id) {
            this.method = method;
            this.params = params;
            this.id = id;
        }

        protected String encode() {
            JSONObject object = new JSONObject();
            object.put("jsonrpc", jsonrpc);
            object.put("method", method);
            object.put("params", params == null? null : new JSONArray(params));
            object.put("id", id);
            log.debug(object);
            return object.toString();
        }
    }*/


}
