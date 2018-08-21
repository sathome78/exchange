package me.exrates.service.achain;

import me.exrates.model.dto.achain.TransactionDTO;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Maks on 14.06.2018.
 */
public interface NodeService {

    String getMainAccountAddress();

    String getAccountName();

    long getBlockCount();

    JSONArray getBlock(long blockNum);

    boolean getSyncState();

    JSONArray getBlockTransactions(long blockNum);

    JSONObject getPrettyContractTransaction(String innerHash);
}
