package me.exrates.dao;

import me.exrates.model.Wallet;

import java.math.BigDecimal;
import java.util.List;

public interface WalletDao {

	BigDecimal getWalletABalance(int walletId);

	BigDecimal getWalletRBalance(int walletId);

	boolean setWalletABalance(int walletId, BigDecimal newBalance);

	boolean setWalletRBalance(int walletId, BigDecimal newBalance);

	int getWalletId(int userId, int currencyId);

	int createNewWallet(Wallet wallet);

	int getUserIdFromWallet(int walletId);

	List<Wallet> findAllByUser(int userId);

	Wallet findByUserAndCurrency(int userId,int currencyId);

	Wallet createWallet(int userId,int currencyId);

	boolean update(Wallet wallet);
}