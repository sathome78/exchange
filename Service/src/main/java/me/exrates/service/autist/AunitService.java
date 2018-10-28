package me.exrates.service.autist;

import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.service.merchantStrategy.IRefillable;
import me.exrates.service.merchantStrategy.IWithdrawable;

import java.math.BigDecimal;

public interface AunitService extends IRefillable , IWithdrawable {


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
    default Boolean additionalFieldForRefillIsUsed() {
        return true;
    }

    @Override
    default String additionalRefillFieldName() {
        return "MEMO";
    }

    @Override
    default Boolean additionalTagForWithdrawAddressIsUsed() {
        return true;
    }

    @Override
    default Boolean withdrawTransferringConfirmNeeded() {
        return false;
    }

    Merchant getMerchant();

    Currency getCurrency();

    RefillRequestAcceptDto createRequest(String hash, String address, BigDecimal amount);

    void putOnBchExam(RefillRequestAcceptDto requestAcceptDto);
}
