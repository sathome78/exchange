package me.exrates.service.casinocoin;

import me.exrates.model.dto.RippleAccount;
import me.exrates.model.dto.RippleTransaction;
import org.json.JSONObject;

/**
 * Created by maks on 05.05.2017.
 */
public interface RippledNodeService {

    void signTransaction(RippleTransaction transaction);

    void submitTransaction(RippleTransaction transaction);

    JSONObject getTransaction(String txHash);

    JSONObject getAccountInfo(String accountName);

    RippleAccount porposeAccount();

    JSONObject getServerState();
}
