package me.exrates.service.omni;

import org.json.JSONObject;

public interface OmniNodeService {
    boolean unlockWallet(String pass, int seconds);

    String generateNewAddress();

    String getBalance(String address, int propertyId);

    String getBtcInfo();

    String getOmniBalances();

    String getTransaction(String txId);

    String listAllTransactions();

    String listTransactions(String address, int count, int offset, int startBlock, int endblock);

    JSONObject sendFunded(String from, String to, int propertyId, String amount, String feeAdddr);
}
