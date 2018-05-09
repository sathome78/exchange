package me.exrates.service.ripple;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Merchant;
import me.exrates.service.MerchantService;
import me.exrates.service.WithdrawService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by maks on 11.05.2017.
 */
@Log4j2(topic = "ripple_log")
@ClientEndpoint
@Service
@PropertySource("classpath:/merchants/ripple.properties")
public class RippleWsServiceImpl {

    private @Value("${ripple.rippled.ws}") String wsUrl;
    private @Value("${ripple.account.address}") String address;
    private URI WS_SERVER_URL;

    private Session session;
    private boolean access = false;
    private volatile RemoteEndpoint.Basic endpoint = null;
    private static final String SUBSCRIBE_COMAND_ID = "watch main account transactions";
    private volatile boolean shutdown = false;
    private Merchant merchant;
    private static final String XRP_MERCHANT = "Ripple";

    @Autowired
    private RippleService rippleService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private WithdrawService withdrawService;


    @PostConstruct
    public void init() throws IOException, DeploymentException {
     /*   WS_SERVER_URL = URI.create(wsUrl);
        connectAndSubscribe();
        merchant = merchantService.findByName(XRP_MERCHANT);*/
    }

    @OnMessage
    public void onMessage(String msg) {
        log.debug("income ripple message {}", msg);
        try {
            JSONObject jsonMessage = null;
            try {
                jsonMessage = new JSONObject(msg);
            } catch (Exception e) {
                return;
            }
            Object messageType = jsonMessage.get("type");
            Object status = jsonMessage.get("status");
            if ("transaction".equals(messageType)) {
                JSONObject transaction = jsonMessage.getJSONObject("transaction");
                if(jsonMessage.getBoolean("validated") && transaction.get("TransactionType")
                        .equals("Payment") && transaction.get("Destination").equals(address)) {
                    /*its refill transaction, we can process it*/
                    rippleService.onTransactionReceive(transaction);
                } else if(jsonMessage.getBoolean("validated") && transaction.get("TransactionType")
                        .equals("Payment") && transaction.get("Account").equals(address)) {
                    /*its withdraw transaction, we can finalize it*/
                    String hash = transaction.getString("hash");
                    Optional<Integer> requestId = withdrawService.getRequestIdByHashAndMerchantId(hash, merchant.getId());
                    requestId.ifPresent(integer -> withdrawService.finalizePostWithdrawalRequest(requestId.get()));
                }
            }
            if ("response".equals(messageType)) {
                if(!status.equals("success")) {
                    String command = jsonMessage.getJSONObject("request").getString("command");
                    if (command.equals("subscribe")) {
                        try {
                            subscribeToTransactions();
                        } catch (Exception e) {
                           log.error("ripple ws error {}", e);
                        }
                    }
                    return;
                }
                    if (jsonMessage.get("id").equals(SUBSCRIBE_COMAND_ID)) {
                    access = true;
                    log.debug("ripple node ws subscribe confirmed");
                }
            }
        } catch (Exception e) {
            log.error("exception {}", e);
        }
    }

    private void connectAndSubscribe() {
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
            log.debug("ripple node ws connection established");
            subscribeToTransactions();
        } catch (Exception e) {
            log.error("error connection to ripple node {}", e);
        }
    }

    private void subscribeToTransactions () throws IOException {
        JSONObject object = new JSONObject();
        object.put("id", SUBSCRIBE_COMAND_ID);
        object.put("command", "subscribe");
        object.put("accounts", new JSONArray().put(address));
       /* object.put("streams", new JSONArray().put("transactions"));*/
        log.debug("message to send {}" + object.toString() );
        endpoint.sendText(object.toString());
    }

    @Scheduled(initialDelay = 120000, fixedDelay = 120000)
    public void checkSessionAndReconnect() throws IOException {
        if (access && !session.isOpen()) {
            connectAndSubscribe();
        }
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


}
