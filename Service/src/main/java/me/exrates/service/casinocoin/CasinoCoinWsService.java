package me.exrates.service.casinocoin;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Merchant;
import me.exrates.service.MerchantService;
import me.exrates.service.WithdrawService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

@Log4j2(topic = "casinocoin_log")
@ClientEndpoint
@Service
@PropertySource("classpath:/merchants/casinocoin.properties")
public class CasinoCoinWsService {

    private static final String SUBSCRIBE_COMAND_ID = "watch main account transactions";
    private static final String GET_TX_COMMAND_ID = "get transaction";

    @Value("${casinocoin.ws}")
    private String wsUrl;

    @Value("${casinocoin.account.address}")
    private String mainAddress;

    private URI WS_SERVER_URL;
    private Session session;

    private boolean access = false;
    private volatile RemoteEndpoint.Basic endpoint = null;
    private volatile boolean shutdown = false;

    @Autowired
    private CasinoCoinService casinoCoinService;

    @PostConstruct
    public void init() {
        WS_SERVER_URL = URI.create(wsUrl);
        connectAndSubscribe();
    }

    @OnMessage
    public void onMessage(String msg) {
        log.debug("Income message {}", msg);

        try {
            JSONObject jsonMessage;
            try {
                jsonMessage = new JSONObject(msg);
            } catch (Exception e) {
                log.error(e);
                return;
            }
            Object messageType = jsonMessage.get("type");
            Object status = jsonMessage.get("status");
            if ("transaction".equals(messageType)) {

                log.debug("Transaction | income message type: ", messageType);

                JSONObject transaction = jsonMessage.getJSONObject("transaction");
                if(jsonMessage.getBoolean("validated") && transaction.get("TransactionType")
                        .equals("Payment") && transaction.get("Destination").equals(mainAddress)) {
                    if (transaction.has("SendMax")) {

                        log.debug("Transaction | Not supported or fake transaction!!!");

                        return;
                    }
                    /*its refill transaction, we can process it*/
                    getTransaction(transaction.getString("hash"));

                }
            }
            if ("response".equals(messageType)) {
                if(!status.equals("success")) {
                    String command = jsonMessage.getJSONObject("request").getString("command");
                    if (command.equals("subscribe")) {
                        try {
                            subscribeToTransactions();
                        } catch (Exception e) {
                           log.error("Error | WebSocket error {}", e);
                        }
                    }
                    return;
                }
                if (jsonMessage.get("id").equals(SUBSCRIBE_COMAND_ID)) {
                    access = true;

                    log.debug("INFO | CasinoCoin WebSocket subscribe confirmed");

                } else if (jsonMessage.get("id").equals(GET_TX_COMMAND_ID)) {

                    log.debug("TRANSACTION | Process transaction from response");

                    processIncomeTransaction(jsonMessage.getJSONObject("result"));
                }
            }
        } catch (Exception e) {
            log.error("ERROR | Exception {}", e);
        }
    }

    private void processIncomeTransaction(JSONObject result) {
        log.debug("TRANSACTION | Process income transaction (method) {}", result);

        if (!result.get("Destination").equals(mainAddress) || StringUtils.isEmpty(result.getInt("DestinationTag"))) {
            return;
        }

        Integer destinationTag = result.getInt("DestinationTag");
        String amount = result.getJSONObject("meta").getString("delivered_amount");
        String hash = result.getString("hash");

        log.debug("TRANSACTION | Income transaction | Hash: {} DestinationTag: {} Amount: {}", hash, destinationTag, amount);

        casinoCoinService.onTransactionReceive(hash, destinationTag, amount);
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

            log.debug("INFO | CasinoCoin WebSocket connection established");

            subscribeToTransactions();
        } catch (Exception e) {
            log.error("ERROR | Error connection to CasinoCoin WebSocket {}", e);
        }
    }

    private void subscribeToTransactions () throws IOException {
        JSONObject object = new JSONObject();
        object.put("id", SUBSCRIBE_COMAND_ID);
        object.put("command", "subscribe");
        object.put("accounts", new JSONArray().put(mainAddress));

        log.debug("INFO | Subscribe to transactions message to send {}" + object.toString() );

        endpoint.sendText(object.toString());
    }

     private void getTransaction(String hash) throws IOException {
        JSONObject object = new JSONObject();
        object.put("id", GET_TX_COMMAND_ID);
        object.put("command", "tx");
        object.put("transaction", hash);

        log.debug("TRANSACTION | Message to send {}" + object.toString() );

        endpoint.sendText(object.toString());
    }

    @Scheduled(initialDelay = 120000, fixedDelay = 120000)
    public void checkSessionAndReconnect() {
        if (access && !session.isOpen()) {
            connectAndSubscribe();
        }
    }

    @OnClose
    public void close(final Session session, final CloseReason reason) {
        log.error("ERROR | Connection lost. Session closed : {}. Reason : {}", session, reason);
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
            log.error("ERROR | Closing session");
        }
    }


}
