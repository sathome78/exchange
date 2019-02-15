package me.exrates.service.ppy;

import me.exrates.service.bitshares.BitsharesServiceImpl;
import org.json.JSONObject;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnMessage;


@ClientEndpoint
public class PPYServiceImpl extends BitsharesServiceImpl {

    public PPYServiceImpl(String merchantName, String currencyName, String propertySource, long SCANING_INITIAL_DELAY) {
        super(merchantName, currencyName, propertySource, SCANING_INITIAL_DELAY);
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

    @Override
    protected void setIrreversableBlock(String msg) {
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
