package me.exrates.service;

import me.exrates.model.CreditsOperation;
import me.exrates.model.OperationView;
import me.exrates.model.Transaction;

import java.util.List;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface TransactionService {

    Transaction createTransactionRequest(CreditsOperation creditsOperation);

    Transaction findById(int id);

    void provideTransaction(Transaction transaction);

    void invalidateTransaction(Transaction transaction);

    List<Transaction> findAllByUserWallets(List<Integer> userWalletsIds);
    
    List<OperationView> showMyOperationHistory(String email);
}