package me.exrates.service;

import me.exrates.service.impl.GapiCurrencyServiceImpl.Transaction;

import java.util.List;

public interface GapiCurrencyService {

    List<String> generateNewAddress();

    List<Transaction> getAccountTransactions();

    String createNewTransaction(String privKey, String amount);
}
