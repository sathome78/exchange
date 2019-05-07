package me.exrates.dao;

import me.exrates.model.BTCTransaction;

public interface BTCTransactionDao {

    BTCTransaction create(BTCTransaction btcTransaction);

    BTCTransaction findByTransactionId(int transactionId);

    boolean delete(int transactionId);
}