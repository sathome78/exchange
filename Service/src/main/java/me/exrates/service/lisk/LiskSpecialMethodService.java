package me.exrates.service.lisk;

import me.exrates.model.dto.merchants.lisk.LiskAccount;

public interface LiskSpecialMethodService {

    String sendTransaction(String secret, Long amount, String recipientId);

    LiskAccount createAccount(String secret);
}
