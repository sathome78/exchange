package me.exrates.openapi.service.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class UserTransferService {

    @Autowired
    private UserTransferDao userTransferDao;

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public UserTransfer createUserTransfer(int fromUserId, int toUserId, int currencyId, BigDecimal amount, BigDecimal commissionAmount) {
        UserTransfer userTransfer = UserTransfer.builder()
                .fromUserId(fromUserId)
                .toUserId(toUserId)
                .currencyId(currencyId)
                .amount(amount)
                .commissionAmount(commissionAmount)
                .build();
        return userTransferDao.save(userTransfer);
    }

    @Override
    public UserTransferInfoDto getTransferInfoBySourceId(int id) {
        return userTransferDao.getById(id);
    }
}
