package me.exrates.service.achain;

import org.json.JSONArray;
import org.json.JSONObject;

public interface NodeService {

    String getMainAccountAddress();

    String getAccountName();

    long getBlockCount();

    JSONArray getBlock(long blockNum);

    boolean getSyncState();

    JSONArray getBlockTransactions(long blockNum);

    JSONObject getPrettyContractTransaction(String innerHash);
}
