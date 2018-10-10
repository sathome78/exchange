package me.exrates.service;

import me.exrates.model.dto.merchants.btc.BtcAdminPreparedTxDto;
import me.exrates.model.dto.merchants.btc.BtcBlockDto;
import me.exrates.model.dto.merchants.btc.BtcPaymentResultDetailedDto;
import me.exrates.model.dto.merchants.btc.BtcPreparedTransactionDto;
import me.exrates.model.dto.merchants.btc.BtcTransactionDto;
import me.exrates.model.dto.merchants.btc.BtcWalletPaymentItemDto;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.List;

public interface BitcoinLikeCurrency extends BitcoinService{

    @Override
    default boolean isRawTxEnabled() {
        return false;
    }

    @Override
    default void onPayment(BtcTransactionDto transactionDto) {
        throw new RuntimeException("Not Supported");
    }

    @Override
    default void onIncomingBlock(BtcBlockDto blockDto) {
        throw new RuntimeException("Not Supported");
    }

    @Override
    default void backupWallet() {
        throw new RuntimeException("Not Supported");
    }


    @Override
    default BigDecimal estimateFee() {
        throw new RuntimeException("Not Supported");
    }

    @Override
    default String getEstimatedFeeString() {
        throw new RuntimeException("Not Supported");
    }

    @Override
    default BigDecimal getActualFee() {
        throw new RuntimeException("Not Supported");
    }

    @Override
    default void setTxFee(BigDecimal fee) {
        throw new RuntimeException("Not Supported");
    }

    @Override
    default BtcAdminPreparedTxDto prepareRawTransactions(List<BtcWalletPaymentItemDto> payments) {
        throw new RuntimeException("Not Supported");
    }

    @Override
    default BtcAdminPreparedTxDto updateRawTransactions(List<BtcPreparedTransactionDto> preparedTransactions) {
        throw new RuntimeException("Not Supported");
    }

    @Override
    default List<BtcPaymentResultDetailedDto> sendRawTransactions(List<BtcPreparedTransactionDto> preparedTransactions) {
        throw new RuntimeException("Not Supported");
    }

    @Override
    default void scanForUnprocessedTransactions(@Nullable String blockHash) {
        throw new RuntimeException("Not Supported");
    }

    @Override
    default void setSubtractFeeFromAmount(boolean subtractFeeFromAmount) {
        throw new RuntimeException("Not Supported");
    }

    @Override
    default boolean getSubtractFeeFromAmount() {
        throw new RuntimeException("Not Supported");
    }
}
