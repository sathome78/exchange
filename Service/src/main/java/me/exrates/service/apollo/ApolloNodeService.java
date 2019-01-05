package me.exrates.service.apollo;

public interface ApolloNodeService {

    String getTransactions(String address, long timestamp);

    String getTransaction(String txHash);
}
