package me.exrates.service.ripple;

import me.exrates.model.dto.WithdrawMerchantOperationDto;

import java.math.BigDecimal;

/**
 * Created by maks on 11.05.2017.
 */
public interface RippleTransactionService {
    String withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto);

    BigDecimal normalizeAmountToDecimal(String amount);

    BigDecimal getAccountBalance(String accountName);

    boolean checkSendedTransactionConsensus(String txHash);
}
