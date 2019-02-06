package me.exrates.service.tron;

import lombok.SneakyThrows;
import me.exrates.model.dto.TronNewAddressDto;
import me.exrates.model.dto.TronTransferDto;
import org.json.JSONObject;

public interface TronNodeService {

    TronNewAddressDto getNewAddress();

    JSONObject transferFunds(TronTransferDto tronTransferDto);

    @SneakyThrows
    JSONObject transferAsset(TronTransferDto tronTransferDto);

    JSONObject getTransactions(long blockNum);

    JSONObject getTransaction(String hash);

    JSONObject getLastBlock();

    JSONObject getAccount(String addressBase58);
}
