package me.exrates.service.bitshares.crea;

import com.google.common.hash.Hashing;
import me.exrates.service.bitshares.BitsharesServiceImpl;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.annotation.PostConstruct;
import javax.websocket.ClientEndpoint;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@ClientEndpoint
public class CreaServiceImpl extends BitsharesServiceImpl {


    private static final long IRREVERIBLE_BLOCK_PERIOD = 3L;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public CreaServiceImpl(String merchantName, String currencyName, String propertySource, long SCANING_INITIAL_DELAY, int decimal) {
        super(merchantName, currencyName, propertySource, SCANING_INITIAL_DELAY, decimal);
    }

    @PostConstruct
    public void init(){
        scheduler.scheduleAtFixedRate(this::requestLastIrreversibleBlock, 0, IRREVERIBLE_BLOCK_PERIOD, TimeUnit.SECONDS);
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

    @Override
    protected JSONObject extractTransaction(JSONArray transactions, int i) {
        return transactions.getJSONObject(i).getJSONArray("operations").getJSONObject(0).getJSONObject("value");
    }

    @Override
    protected void makeRefill(List<String> lisfOfMemo, JSONObject transaction, String hash) {
        String memoText = transaction.getString("memo");
        if (lisfOfMemo.contains(memoText)) {
            BigDecimal amount = reduceAmount(transaction.getJSONObject("amount").getBigDecimal("amount"));

            prepareAndProcessTx(Hashing.sha256()
                    .hashString(hash, StandardCharsets.UTF_8)
                    .toString(), memoText, amount);

        }
    }

    @Override
    protected void processIrreversebleBlock(String trx) {
        JSONObject block = new JSONObject(trx);

        JSONArray transactions = extractTransactionsFromBlock(block);
        if (transactions.length() == 0) return;

        List<String> lisfOfMemo = refillService.getListOfValidAddressByMerchantIdAndCurrency(merchant.getId(), currency.getId());
        try {
            for (int i = 0; i < transactions.length(); i++) {
                JSONObject transaction = extractTransaction(transactions, i);

                if (transaction.getString("to").equals(mainAddressId)) makeRefill(lisfOfMemo, transaction, transactions.getJSONObject(0).getJSONArray("signatures").getString(i));

            }

        } catch (JSONException e) {
            log.debug(e);
        }
    }
}
