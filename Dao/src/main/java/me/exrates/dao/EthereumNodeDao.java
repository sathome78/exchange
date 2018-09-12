package me.exrates.dao;

import me.exrates.model.EthereumAccount;

import java.util.List;
import java.util.Optional;

@Deprecated
public interface EthereumNodeDao {

    void createAddress(EthereumAccount ethereumAccount, String merchant);

    Optional<EthereumAccount> findByAddress(String address, String merchant);

    List<String> findAllAddresses(String merchant);

    String findUserEmailByAddress(String address, String merchant);

    void createMerchantTransaction(String address, String merchantTransactionId, Integer transactionId);

    List<String> findPendingTransactions(String merchant);

    Integer findTransactionId(String merchantTransactionId, String merchant);

    boolean isMerchantTransactionExists(String merchantTransactionId, String merchant);

    String findAddressByMerchantTransactionId(String merchantTransactionId, String merchant);
}
