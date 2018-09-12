package me.exrates.dao;

import me.exrates.model.BTCTransaction;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Deprecated
public interface BTCTransactionDao {

    BTCTransaction create(BTCTransaction btcTransaction);

    BTCTransaction findByTransactionId(int transactionId);

    boolean delete(int transactionId);
}