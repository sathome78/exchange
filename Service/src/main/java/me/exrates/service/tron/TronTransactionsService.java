package me.exrates.service.tron;

import me.exrates.model.dto.TronReceivedTransactionDto;

public interface TronTransactionsService {
    boolean checkIsTransactionConfirmed(String txHash);

    void processTransaction(TronReceivedTransactionDto p);

    void processTransaction(int id, String address, String hash, String amount, Integer merchantId, Integer currencyId);
}
