package me.exrates.service.tron;

import me.exrates.service.merchantStrategy.IRefillable;
import me.exrates.service.merchantStrategy.IWithdrawable;

import java.util.Set;

public interface Trc20TokenService extends IRefillable, IWithdrawable {

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

    Set<String> getAddressesHEX();

    String getMerchantName();

    String getCurrencyName();
}
