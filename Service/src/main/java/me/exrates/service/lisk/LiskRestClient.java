package me.exrates.service.lisk;

import me.exrates.model.dto.merchants.lisk.LiskAccount;
import me.exrates.model.dto.merchants.lisk.LiskSendTxDto;
import me.exrates.model.dto.merchants.lisk.LiskTransaction;

import java.util.List;

public interface LiskRestClient {
    void initClient(String propertySource);

    LiskTransaction getTransactionById(String txId);

    List<LiskTransaction> getTransactionsByRecipient(String recipientAddress);

    List<LiskTransaction> getAllTransactionsByRecipient(String recipientAddress, int offset);

    Long getFee();

    String sendTransaction(LiskSendTxDto dto);

    LiskAccount createAccount(String secret);

    LiskAccount getAccountByAddress(String address);
}
