package me.exrates.service.ppy;

import me.exrates.service.bitshares.BitsharesServiceImpl;
import org.json.JSONObject;

import javax.websocket.ClientEndpoint;


@ClientEndpoint
public class PPYServiceImpl extends BitsharesServiceImpl {

    public PPYServiceImpl(String merchantName, String currencyName, String propertySource, long SCANING_INITIAL_DELAY, int decimal) {
        super(merchantName, currencyName, propertySource, SCANING_INITIAL_DELAY, decimal);
    }

    @Override
    protected boolean isContainsLastIrreversibleBlockInfo(String jsonRpc) {
        return jsonRpc.contains("last_irreversible_block_num");
    }

    @Override
    protected int getLastIrreversableBlock(String msg) {
        return new JSONObject(msg).getJSONArray("params").getJSONArray(1).getJSONArray(0).getJSONObject(3).getInt(lastIrreversebleBlockParam);
    }
}
