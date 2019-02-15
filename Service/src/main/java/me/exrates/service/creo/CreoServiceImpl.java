package me.exrates.service.creo;

import me.exrates.service.bitshares.BitsharesServiceImpl;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.PostConstruct;
import javax.websocket.ClientEndpoint;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ClientEndpoint
public class CreoServiceImpl extends BitsharesServiceImpl {

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public CreoServiceImpl(String merchantName, String currencyName, String propertySource, long SCANING_INITIAL_DELAY, int decimal) {
        super(merchantName, currencyName, propertySource, SCANING_INITIAL_DELAY, decimal);
    }

    @PostConstruct
    public void init(){
        scheduler.scheduleAtFixedRate(this::requestLastIrreversibleBlock, 0, 3L , TimeUnit.SECONDS); //var
    }

    private void requestLastIrreversibleBlock() {
        try {
            JSONObject getLastIrreversibleBlock = new JSONObject();
            getLastIrreversibleBlock.put("id", 0);
            getLastIrreversibleBlock.put("jsonrpc", "2.0");
            getLastIrreversibleBlock.put("method", "condenser_api.get_dynamic_global_properties");
            getLastIrreversibleBlock.put("params", new JSONArray());

            endpoint.sendText(getLastIrreversibleBlock.toString());
        } catch (Exception e){
            log.error(e);
        }
    }

    @Override
    protected boolean isContainsLastIrreversibleBlockInfo(String jsonRpc){
        return jsonRpc.contains("last_irreversible_block_num");
    }

    @Override
    public int getLastIrreversableBlock(String msg){
        return new JSONObject(msg).getJSONObject("result").getInt("last_irreversible_block_num");
    }

    @Override
    protected void getBlock(int blockNum) throws IOException {
        JSONObject block = new JSONObject();
        block.put("id", 10);
        block.put("jsonrpc", "2.0");
        block.put("method", "block_api.get_block");
        block.put("params", new JSONObject().put("block_num", blockNum));

        endpoint.sendText(block.toString());
    }

    @Override
    protected JSONArray extractTransactionsFromBlock(JSONObject block) {
        return block.getJSONObject("result").getJSONObject("block").getJSONArray("transactions");
    }

}
