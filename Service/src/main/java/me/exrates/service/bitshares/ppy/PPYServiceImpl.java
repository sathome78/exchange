package me.exrates.service.bitshares.ppy;

import lombok.SneakyThrows;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.service.bitshares.BitsharesServiceImpl;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import java.io.IOException;


@ClientEndpoint
@Service("ppyServiceImpl")
@Conditional(MonolitConditional.class)
public class PPYServiceImpl extends BitsharesServiceImpl {

    private static final String name = "PPY";
    private static final int DECIMAL = 5;

    public PPYServiceImpl() {
        super(name, name, "merchants/ppy.properties", 7, DECIMAL);
    }

    public void subscribeToTransactions() throws IOException {
        JSONObject login = new JSONObject();
        login.put("id", 0);
        login.put("method", "call");
        login.put("params", new JSONArray().put("database").put("login").put(new JSONArray().put("").put("")));

        JSONObject db = new JSONObject();
        db.put("id", 1);
        db.put("method", "call");
        db.put("params", new JSONArray().put("database").put("database").put(new JSONArray()));

        JSONObject netw = new JSONObject();
        netw.put("id", 2);
        netw.put("method", "call");
        netw.put("params", new JSONArray().put("database").put("network_broadcast").put(new JSONArray()));

        JSONObject history = new JSONObject();
        history.put("id", 3);
        history.put("method", "call");
        history.put("params", new JSONArray().put("database").put("history").put(new JSONArray()));

        JSONObject chainId = new JSONObject();
        chainId.put("id", 5);
        chainId.put("method", "call");
        chainId.put("params", new JSONArray().put("database").put("get_chain_id").put(new JSONArray().put(new JSONArray())));

        JSONObject get_object = new JSONObject();
        get_object.put("id", 6);
        get_object.put("method", "call");
        get_object.put("params", new JSONArray().put("database").put("get_objects").put(new JSONArray().put(new JSONArray().put("2.1.0"))));


        JSONObject subscribe = new JSONObject();
        subscribe.put("id", 7);
        subscribe.put("method", "call");
        subscribe.put("params", new JSONArray().put("database").put("set_subscribe_callback").put(new JSONArray().put(7).put(false)));

        endpoint.sendText(login.toString());

        endpoint.sendText(db.toString());

        endpoint.sendText(netw.toString());

        endpoint.sendText(history.toString());

        endpoint.sendText(chainId.toString());

        endpoint.sendText(subscribe.toString());

        endpoint.sendText(get_object.toString());

    }


    @OnMessage
    @Override
    public void onMessage(String msg) {
        try {
            if (msg.contains("last_irreversible_block_num")) setIrreversableBlock(msg);
            else if (msg.contains("previous")) processIrreversebleBlock(msg);
            else log.info("unrecogrinzed msg from " + merchantName + "\n" + msg);
        } catch (Exception e) {
            log.error("Web socket error" + merchantName + "  : \n" + e.getMessage());
        }

    }

    private void setIrreversableBlock(String msg) throws IOException {
        JSONObject message = new JSONObject(msg);
        int blockNumber = message.getJSONArray("params").getJSONArray(1).getJSONArray(0).getJSONObject(0).getInt(lastIrreversebleBlockParam);
        synchronized (this) {
            if (blockNumber > lastIrreversibleBlockValue) {
                for (; lastIrreversibleBlockValue <= blockNumber; lastIrreversibleBlockValue++) {
                    getBlock(lastIrreversibleBlockValue);
                }
                merchantSpecParamsDao.updateParam(merchant.getName(), lastIrreversebleBlockParam, String.valueOf(lastIrreversibleBlockValue));
            }
        }
    }

    @SneakyThrows
    protected void getBlock(int blockNum) {
        JSONObject block = new JSONObject();
        block.put("id", 10);
        block.put("method", "call");
        block.put("params", new JSONArray().put("database").put("get_block").put(new JSONArray().put(blockNum)));
        endpoint.sendText(block.toString());
    }

    @OnError
    public void onError(Throwable t) throws Throwable {
        log.error(ExceptionUtils.getStackTrace(t));
    }

}
