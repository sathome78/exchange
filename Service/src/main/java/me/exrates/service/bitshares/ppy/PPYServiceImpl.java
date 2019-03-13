package me.exrates.service.bitshares.ppy;

import me.exrates.model.condition.MonolitConditional;
import me.exrates.service.bitshares.BitsharesServiceImpl;
import org.json.JSONObject;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import javax.websocket.*;
import java.io.IOException;


@ClientEndpoint
@Service("ppyServiceImpl")
@Conditional(MonolitConditional.class)
public class PPYServiceImpl extends BitsharesServiceImpl {

    private static final String name = "PPY";
    private static final int DECIMAL = 5;

    public PPYServiceImpl() {
        super(name, name, "merchants/ppy.properties", 6, DECIMAL);
    }

    @OnMessage
    @Override
    public void onMessage(String msg) {
        log.info(msg);
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
        int blockNumber = message.getJSONArray("params").getJSONArray(1).getJSONArray(0).getJSONObject(3).getInt(lastIrreversebleBlockParam);
        synchronized (this) {
            if (blockNumber > lastIrreversibleBlockValue) {
                for (; lastIrreversibleBlockValue <= blockNumber; lastIrreversibleBlockValue++) {
                    getBlock(lastIrreversibleBlockValue);
                }
                merchantSpecParamsDao.updateParam(merchant.getName(), lastIrreversebleBlockParam, String.valueOf(lastIrreversibleBlockValue));
            }
        }
    }
}
