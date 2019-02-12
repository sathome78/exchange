package me.exrates.service.casinocoin;

import me.exrates.model.dto.WithdrawMerchantOperationDto;

import java.math.BigDecimal;
import java.util.Map;

public interface CasinoCoinTransactionService {
    Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto);

    BigDecimal normalizeAmountToDecimal(String amount);

    BigDecimal getAccountBalance(String accountName);

    boolean checkSendedTransactionConsensus(String txHash, String additionalParams);
}
