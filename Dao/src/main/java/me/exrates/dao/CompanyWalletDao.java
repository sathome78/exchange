package me.exrates.dao;

import me.exrates.model.CompanyWallet;
import me.exrates.model.Currency;

import java.math.BigDecimal;

public interface CompanyWalletDao {

    CompanyWallet create(Currency currency);

    CompanyWallet findByCurrencyId(Currency currency);

    boolean update(CompanyWallet companyWallet);

    CompanyWallet findByWalletId(int walletId);

    boolean substarctCommissionBalanceById(Integer id, BigDecimal amount);
}