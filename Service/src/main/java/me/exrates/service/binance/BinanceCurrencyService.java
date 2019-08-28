package me.exrates.service.binance;

import com.binance.dex.api.client.domain.broadcast.Transaction;

import java.util.List;

public interface BinanceCurrencyService {

    List<Transaction> getBlockTransactions(long num);

    String getReceiverAddress(Transaction transaction);

    String getToken(Transaction transaction);

    String getHash(Transaction transaction);

    String getAmount(Transaction transaction);

    String getMemo(Transaction transaction);

    long getBlockchainHeigh();
}
