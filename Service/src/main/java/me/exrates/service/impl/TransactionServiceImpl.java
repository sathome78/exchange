package me.exrates.service.impl;

import me.exrates.dao.TransactionDao;
import me.exrates.model.Transaction;
import me.exrates.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionDao transactionDao;

    @Override
    public boolean create(Transaction transaction) {
        return transactionDao.create(transaction);
    }
}