package me.exrates.service.lisk;

import me.exrates.model.dto.merchants.lisk.ArkSendTxDto;
import me.exrates.model.dto.merchants.lisk.LiskAccount;

public interface ArkRpcClient {
    void initClient(String propertySource);

    LiskAccount createAccount(String secret);

    String createTransaction(ArkSendTxDto dto);

    void broadcastTransaction(String txId);
}
