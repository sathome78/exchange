package me.exrates.service.ripple;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Merchant;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.service.MerchantService;
import me.exrates.service.WithdrawService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
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

/**
 * Created by maks on 11.05.2017.
 */
@Log4j2(topic = "ripple_log")
@ClientEndpoint
@Service
@PropertySource("classpath:/merchants/ripple.properties")
@Conditional(MonolitConditional.class)
public class RippleWsServiceImpl {

    private @Value("${ripple.rippled.ws}") String wsUrl;
    private @Value("${ripple.account.address}") String address;
    private URI WS_SERVER_URL;

    private Session session;
    private boolean access = false;
    private volatile RemoteEndpoint.Basic endpoint = null;
    private static final String SUBSCRIBE_COMAND_ID = "watch main account transactions";
    private static final String GET_TX_COMMAND_ID = "get transaction";
    private volatile boolean shutdown = false;
    private Merchant merchant;
    private static final String XRP_MERCHANT = "Ripple";

    private final RippleService rippleService;
    private final MerchantService merchantService;
    private final WithdrawService withdrawService;

    @Autowired
    public RippleWsServiceImpl(RippleService rippleService, MerchantService merchantService, WithdrawService withdrawService) {
        this.rippleService = rippleService;
        this.merchantService = merchantService;
        this.withdrawService = withdrawService;
    }


    @PostConstruct
    public void init() {
        WS_SERVER_URL = URI.create(wsUrl);
        connectAndSubscribe();
        merchant = merchantService.findByName(XRP_MERCHANT);
    }

    public String getAddress() {
        return address;
    }

    @OnMessage
    public void onMessage(String msg) {
        log.debug("income ripple message {}", msg);
        try {
            JSONObject jsonMessage = null;
            try {
                jsonMessage = new JSONObject(msg);
            } catch (Exception e) {
                log.error(e);
                return;
            }
            Object messageType = jsonMessage.get("type");
            Object status = jsonMessage.get("status");
            if ("transaction".equals(messageType)) {
                log.debug(messageType);
                JSONObject transaction = jsonMessage.getJSONObject("transaction");
                if(jsonMessage.getBoolean("validated") && transaction.get("TransactionType")
                        .equals("Payment") && transaction.get("Destination").equals(getAddress())) {
                    if (transaction.has("SendMax")) {
                        log.debug("not supported or fake transaction!!!");
                        return;
                    }
                    /*its refill transaction, we can process it*/
                    getTransaction(transaction.getString("hash"));
                } else if(jsonMessage.getBoolean("validated") && transaction.get("TransactionType")
                        .equals("Payment") && transaction.get("Account").equals(getAddress())) {
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
                } else if (jsonMessage.get("id").equals(GET_TX_COMMAND_ID)) {
                    log.debug("process transaction from response");
                    processIncomeTransaction(jsonMessage.getJSONObject("result"));
                }
            }
        } catch (Exception e) {
            log.error("exception {}", e);
        }
    }



    void processIncomeTransaction(JSONObject result) {
        log.debug("process {}", result);
        if (!result.get("Destination").equals(getAddress()) || StringUtils.isEmpty(result.getInt("DestinationTag"))) {
            return;
        }
        Integer destinationTag = result.getInt("DestinationTag");
        String amount = result.getJSONObject("meta").getString("delivered_amount");
        String hash = result.getString("hash");
        log.debug("{} {} {}", hash, destinationTag, amount);
        rippleService.onTransactionReceive(hash, destinationTag, amount);
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
        object.put("accounts", new JSONArray().put(getAddress()));
       /* object.put("streams", new JSONArray().put("transactions"));*/
        log.debug("message to send {}" + object.toString() );
        endpoint.sendText(object.toString());
    }

     void getTransaction (String hash) throws IOException {
        JSONObject object = new JSONObject();
        object.put("id", GET_TX_COMMAND_ID);
        object.put("command", "tx");
        object.put("transaction", hash);
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
