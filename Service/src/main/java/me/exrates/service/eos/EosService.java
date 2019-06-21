package me.exrates.service.eos;

import me.exrates.service.merchantStrategy.IRefillable;
import me.exrates.service.merchantStrategy.IWithdrawable;

import java.util.Map;


public interface EosService extends IRefillable, IWithdrawable {


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
        return true;
    }

    @Override
    default Boolean additionalFieldForRefillIsUsed() {
        return true;
    }

    @Override
    default Boolean withdrawTransferringConfirmNeeded() {
        return false;
    }

    @Override
    default boolean specificWithdrawMerchantCommissionCountNeeded() {
        return true;
    }

    @Override
    default String additionalWithdrawFieldName() {
        return "MEMO";
    }

    @Override
    default String additionalRefillFieldName() {
        return "MEMO";
    }

    void processPayment(Map<String, String> params);
}
