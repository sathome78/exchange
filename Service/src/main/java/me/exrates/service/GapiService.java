package me.exrates.service;

import me.exrates.service.impl.GapiCurrencyServiceImpl.Transaction;
import me.exrates.service.merchantStrategy.IRefillable;
import me.exrates.service.merchantStrategy.IWithdrawable;

import java.util.List;

public interface GapiService extends IRefillable, IWithdrawable {
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
        return true;
    }

    @Override
    default Boolean additionalFieldForRefillIsUsed() {
        return false;
    }

    @Override
    default Boolean additionalTagForWithdrawAddressIsUsed() {
        return false;
    }

    @Override
    default Boolean withdrawTransferringConfirmNeeded() {
        return false;
    }

    void checkAddressForTransactionReceive(List<String> listOfAddress, Transaction transaction);

    void onTransactionReceive(Transaction transaction);
}
