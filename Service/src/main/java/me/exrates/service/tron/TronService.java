package me.exrates.service.tron;

import me.exrates.model.dto.TronReceivedTransactionDto;
import me.exrates.service.merchantStrategy.IRefillable;
import me.exrates.service.merchantStrategy.IWithdrawable;

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

    Set<String> getAddressesHEX();

    void processTransaction(TronReceivedTransactionDto p);

    void createRequest(TronReceivedTransactionDto dto);
}
