package me.exrates.service.tron;

import lombok.SneakyThrows;
import me.exrates.model.dto.TronNewAddressDto;
import me.exrates.model.dto.TronTransferDto;
import me.exrates.model.dto.TronTransferDtoTRC20;
import org.json.JSONObject;

public interface TronNodeService {

    TronNewAddressDto getNewAddress();

    JSONObject transferFunds(TronTransferDto tronTransferDto);

    JSONObject transferFundsTRC20(TronTransferDtoTRC20 tronTransferDto);

    JSONObject signTransferFundsTRC20(JSONObject jsonObject);

    JSONObject broadcastTransferFundsTRC20(JSONObject jsonObject);

    @SneakyThrows
    JSONObject transferAsset(TronTransferDto tronTransferDto);

    JSONObject getTransactions(long blockNum);

    JSONObject getTransaction(String hash);

    JSONObject getLastBlock();

    JSONObject getAccount(String addressBase58);
}
