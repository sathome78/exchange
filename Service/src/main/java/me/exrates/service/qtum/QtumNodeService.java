package me.exrates.service.qtum;

import me.exrates.model.dto.merchants.qtum.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface QtumNodeService {
    String getNewAddress();

    String getBlockHash(Integer height);

    Block getBlock(String hash);

    Optional<QtumListTransactions> listSinceBlock(String blockHash);

    void setWalletPassphrase();

    BigDecimal getBalance();

    void transfer(String address, BigDecimal amount);

    void backupWallet();

    List<QtumTokenTransaction> getTokenHistory(Integer blockStart, List<String> tokenAddressList);

    String fromHexAddress(String address);

    String getHexAddress(String address);

    QtumTransaction getTransaction(String hash);

    QtumTokenContract getTokenBalance(String tokenAddress, String data);

    void sendToContract(String tokenAddress, String data, String addressFrom);
}
