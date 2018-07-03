package me.exrates.service.waves;

import me.exrates.model.dto.merchants.waves.WavesPayment;
import me.exrates.model.dto.merchants.waves.WavesTransaction;

import java.util.List;
import java.util.Optional;
import java.util.Properties;

public interface WavesRestClient {
    void init(Properties props);

    String generateNewAddress();

    Integer getCurrentBlockHeight();

    String transferCosts(WavesPayment payment);

    List<WavesTransaction> getTransactionsForAddress(String address);

    Optional<WavesTransaction> getTransactionById(String id);

    Long getAccountWavesBalance(String account);
}
