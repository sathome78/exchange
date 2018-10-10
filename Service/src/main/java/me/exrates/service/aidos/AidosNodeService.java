package me.exrates.service.aidos;

import me.exrates.model.dto.merchants.btc.BtcTransactionDto;
import me.exrates.model.dto.merchants.btc.BtcWalletPaymentItemDto;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.List;

public interface AidosNodeService {
    String generateNewAddress();

    BigDecimal getBalance();

    BtcTransactionDto getTransaction(String txId);

    JSONArray getAllTransactions(Integer count, Integer from);

    JSONObject sendToAddress(String address, BigDecimal amount);


    JSONObject sendMany(List<BtcWalletPaymentItemDto> payments);

    boolean unlockWallet(String pass, int seconds);

    JSONArray getAllTransactions();
}
