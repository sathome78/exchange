package me.exrates.service;


import me.exrates.service.impl.AisiCurrencyServiceImpl.Transaction;

import java.util.List;

public interface AisiCurrencyService {

    String generateNewAddress();

    String getBalanceByAddress(String address);

    List<Transaction> getAccountTransactions();

}
