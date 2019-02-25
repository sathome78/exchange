package me.exrates.service.bitshares;

import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.TransactionsInfo;
import me.exrates.model.dto.BTSBlockInfo;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.service.merchantStrategy.IRefillable;
import me.exrates.service.merchantStrategy.IWithdrawable;

import java.io.IOException;
import java.math.BigDecimal;

public interface BitsharesService extends IRefillable , IWithdrawable {


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

    @Override
    default boolean isValidDestinationAddress(String address) {
        return false;
    }

    Merchant getMerchant();

    Currency getCurrency();

    RefillRequestAcceptDto createRequest(String hash, String address, BigDecimal amount);

    void putOnBchExam(RefillRequestAcceptDto requestAcceptDto);

    void requestBlockTransactionsInfo(BTSBlockInfo BTSBlockInfo) throws IOException;

    TransactionsInfo getRequestedBlocksInfo(int blockNum);
}
