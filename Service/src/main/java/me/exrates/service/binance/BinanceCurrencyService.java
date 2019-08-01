package me.exrates.service.binance;

import com.binance.dex.api.client.domain.broadcast.Transaction;

import java.util.List;

public interface BinanceCurrencyService {

    List<Transaction> getBlockTransactions(long num);

    String getReceiverAddress(Transaction transaction);

    String getTocken(Transaction transaction);

}
