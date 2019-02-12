package me.exrates.service.casinocoin;

import me.exrates.model.dto.RippleAccount;
import me.exrates.model.dto.RippleTransaction;
import org.json.JSONObject;

public interface CasinoCoinNodeService {

    void signTransaction(RippleTransaction transaction);

    void submitTransaction(RippleTransaction transaction);

    JSONObject getTransaction(String txHash);

    JSONObject getAccountInfo(String accountName);

    RippleAccount porposeAccount();

    JSONObject getServerState();
}
