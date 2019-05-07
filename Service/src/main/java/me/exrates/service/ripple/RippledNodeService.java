package me.exrates.service.ripple;

import me.exrates.model.dto.RippleAccount;
import me.exrates.model.dto.RippleTransaction;
import org.json.JSONObject;

public interface RippledNodeService {

    void signTransaction(RippleTransaction transaction);

    void submitTransaction(RippleTransaction transaction);

    JSONObject getTransaction(String txHash);

    JSONObject getAccountInfo(String accountName);

    RippleAccount porposeAccount();

    JSONObject getServerState();
}
