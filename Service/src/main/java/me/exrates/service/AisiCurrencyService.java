package me.exrates.service;

import me.exrates.model.dto.aisi.AccauntTransaction;
import me.exrates.model.dto.aisi.Transaction;

public interface AisiCurrencyService {

    String generateNewAddress();

    String getBalanceByAddress(String address);

    AccauntTransaction[] getAccountTransactions();

    Transaction getTransactionInformation(String transaction_id);
}
