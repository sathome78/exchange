package me.exrates.controller.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.service.EDCService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import static java.util.regex.Pattern.compile;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
@ClientEndpoint
public class EDCClientWebSocketHandler {

    private volatile boolean access = false;
    private volatile RemoteEndpoint.Basic endpoint = null;

    private final String BLOCK_APPLIED_CALL_ID = "5"; // just a number to determine `block_applied` callback
    private final String BLOCK_BY_ID = "7"; // just a number to determine `get_block_by_id` callback

    private final Predicate<String> blockAppliedPattern = compile("(\\{\"method\"\\s*:\\s*\"notice\",\"params\"\\s*:\\s*\\[" + BLOCK_APPLIED_CALL_ID + ",\\[\"\\S+\"\\]\\]\\})").asPredicate();
    private final Predicate<String> blockInfoPattern = compile("(\\{\"id\"\\s*:\\s*" + BLOCK_BY_ID + ",\"result\"\\s*:\\S+)").asPredicate();

    private final String METHOD_GET_BLOCK_BY_ID = "{\"method\": \"call\", \"params\": [2, \"get_block_by_id\", [\"%s\"]], \"id\": %s}";
    private final String METHOD_GET_BLOCK = "{\"method\": \"call\", \"params\": [2, \"get_block\", [\"%s\"]], \"id\": %s}";
    private final Logger LOG = LogManager.getLogger("merchant");

    private final EDCService edcService;

    private volatile Session session;

    @Autowired
    public EDCClientWebSocketHandler(EDCService edcService) {
        this.edcService = edcService;
        subscribeForBlockchainUpdates();
    }

    private String extractBlockId(final String message) {
        return message.substring(message.lastIndexOf('[') + 2, message.indexOf(']') - 1);
    }

    private void getBlockInfo(final String message) throws IOException {
        endpoint.sendText(String.format(METHOD_GET_BLOCK_BY_ID, extractBlockId(message), BLOCK_BY_ID));
    }

    private void subscribeForBlockchainUpdates() {
        try {
            session = ContainerProvider.getWebSocketContainer()
                    .connectToServer(this, URI.create("ws://127.0.0.1:8089"));
            session.setMaxBinaryMessageBufferSize(5012000);
            session.setMaxTextMessageBufferSize(5012000);
            session.setMaxIdleTimeout(Long.MAX_VALUE);
            endpoint = session.getBasicRemote();
            access = true;
            //Auth to Full Node
            endpoint.sendText("{\"method\": \"call\", \"params\": [1, \"login\", [\"\", \"\"]], \"id\": 1}"); // 1 step - Login to the Full Node
            endpoint.sendText("{\"method\": \"call\", \"params\": [1, \"database\", []], \"id\": 1}");        // 2 step - Request access to an API
            endpoint.sendText(String.format("{\"id\": %s ,\"method\":\"call\",\"params\":[2,\"set_block_applied_callback\",[%s]]}", BLOCK_APPLIED_CALL_ID, BLOCK_APPLIED_CALL_ID)); // step 3 - subscribe for new block notifications
        } catch (DeploymentException | IOException e) {
            LOG.error(e);
        }
    }


    @Scheduled(fixedDelay = 500L)
    public void sessionMonitor() {
        if (access && !session.isOpen()) {
            LOG.debug("Disconnected! Reconnecting");
            subscribeForBlockchainUpdates();
        }
    }

    public void rescanBlockchain(final int from, final int to) {
        for (int index = from; index <= to; ) {
            try {
                if (session.isOpen()) {
                    endpoint.sendText(String.format(METHOD_GET_BLOCK, index, BLOCK_BY_ID));
                }
                index++;
            } catch (final Exception e) {
                LOG.error(e);
            }
        }
    }

    @OnMessage
    public void onMessage(final String message) throws IOException {
        if (!access) {
            LOG.debug(message);
            final ObjectMapper mapper = new ObjectMapper();
            final Map<String, String> result = mapper.readValue(message, new TypeReference<Map<String, String>>() {
            });
            if (result.get("id").equals(BLOCK_APPLIED_CALL_ID)) {
                access = true; // Done, we subscribe to updates
            }
        } else {
            if (blockAppliedPattern.test(message)) {
                LOG.info(message);
                getBlockInfo(message);
            } else if (blockInfoPattern.test(message)) {
                LOG.info(message);
                edcService.submitTransactionsForProcessing(message);
            } else {
                LOG.info("EDC Blockchain info\n" + message);
            }
        }
    }

    @OnClose
    public void close(final Session session, final CloseReason reason) {
        LOG.error("Connection lost. Session closed : {}. Reason : {}", session, reason);
    }
}
