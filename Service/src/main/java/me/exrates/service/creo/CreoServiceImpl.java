package me.exrates.service.creo;

import lombok.Data;
import me.exrates.service.bitshares.BitsharesServiceImpl;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.PostConstruct;
import javax.websocket.ClientEndpoint;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Data
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
    public void onMessage(String msg){
        try {
            if (msg.contains("last_irreversible_block_num")) setIrreversableBlock(msg);
            else if (msg.contains("previous")) processIrreversebleBlock(msg);
            else log.info("unrecogrinzed msg from aunit \n" + msg);
        } catch (Exception e) {
            log.error("Web socket error" + merchantName + "  : \n" + e.getMessage());
        }
    }

}
