package me.exrates.service.tron;

import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.TronReceivedTransactionDto;
import me.exrates.service.merchantStrategy.IRefillable;
import me.exrates.service.merchantStrategy.IWithdrawable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

public interface TronService extends IRefillable, IWithdrawable {

    @Override
    default Boolean createdRefillRequestRecordNeeded() {
        return false;
    }

    @Override
    default Boolean needToCreateRefillRequestRecord() {
        return false;
    }

    @Override
    default Boolean toMainAccountTransferringConfirmNeeded() {
        return false;
    }

    @Override
    default Boolean generatingAdditionalRefillAddressAvailable() {
        return false;
    }

    @Override
    default Boolean additionalTagForWithdrawAddressIsUsed() {
        return false;
    }

    @Override
    default Boolean additionalFieldForRefillIsUsed() {
        return false;
    };

    @Override
    default Boolean withdrawTransferringConfirmNeeded() {
        return false;
    }

    @Override
    default boolean specificWithdrawMerchantCommissionCountNeeded() {
        return true;
    }

    @Override
    default BigDecimal countSpecCommission(java.math.BigDecimal amount, String destinationTag, Integer merchantId) {
        return new BigDecimal(0.1).setScale(3, RoundingMode.HALF_UP);
    }

    Set<String> getAddressesHEX();

    RefillRequestAcceptDto createRequest(TronReceivedTransactionDto dto);

    void putOnBchExam(RefillRequestAcceptDto requestAcceptDto);

    int getMerchantId();

    int getCurrencyId();
}
