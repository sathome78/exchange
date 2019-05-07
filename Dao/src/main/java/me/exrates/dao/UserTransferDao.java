package me.exrates.dao;

import me.exrates.model.UserTransfer;
import me.exrates.model.dto.UserTransferInfoDto;

public interface UserTransferDao {

    UserTransfer save(UserTransfer userTransfer);

    UserTransferInfoDto getById(int transactionId);


}
