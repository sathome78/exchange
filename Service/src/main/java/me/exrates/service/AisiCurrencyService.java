package me.exrates.service;

public interface AisiCurrencyService {

    String generateNewAddress();

    String getBalanceByAddress(String address);

}
