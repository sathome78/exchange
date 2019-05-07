package me.exrates.service;

import me.exrates.model.UserTransfer;
import me.exrates.model.dto.UserTransferInfoDto;

import java.math.BigDecimal;

public interface UserTransferService {

    UserTransfer createUserTransfer(int fromUserId,
                                    int toUserId,
                                    int currencyId,
                                    BigDecimal amount,
                                    BigDecimal commissionAmount);

    UserTransferInfoDto getTransferInfoBySourceId(int id);
}
