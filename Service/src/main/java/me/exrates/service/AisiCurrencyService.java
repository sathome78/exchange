package me.exrates.service;

import me.exrates.model.dto.aisi.AccauntTransaction;
import me.exrates.model.dto.aisi.Transaction;

import java.util.List;

public interface AisiCurrencyService {

    String generateNewAddress();

    String getBalanceByAddress(String address);

    List<AccauntTransaction> getAccountTransactions();

    Transaction getTransactionInformation(String transaction_id);
}
