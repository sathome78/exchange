package me.exrates.service.nem;

import me.exrates.model.dto.MosaicIdDto;
import me.exrates.model.dto.NemMosaicTransferDto;
import me.exrates.model.dto.RefillRequestFlatDto;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.merchantStrategy.IRefillable;
import me.exrates.service.merchantStrategy.IWithdrawable;
import org.nem.core.model.Account;

import java.util.List;
import java.util.Map;

public interface NemService extends IRefillable, IWithdrawable {

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
    };

    @Override
    default Boolean withdrawTransferringConfirmNeeded() {
        return false;
    }

    Account getAccount();

    @Override
    default String additionalRefillFieldName() {
        return "Message";
    }

    @Override
    default String additionalWithdrawFieldName() {
        return "Message";
    }


    void processMosaicPayment(List<NemMosaicTransferDto> mosaics, Map<String, String> params);

    void checkRecievedTransaction(RefillRequestFlatDto dto) throws RefillRequestAppropriateNotFoundException;

    boolean checkSendedTransaction(String hash, String additionalParams) throws RefillRequestAppropriateNotFoundException;

    @Override
    default boolean comissionDependsOnDestinationTag() {
        return true;
    }

    @Override
    default boolean specificWithdrawMerchantCommissionCountNeeded() {
        return true;
    }

    List<MosaicIdDto> getDeniedMosaicList();
}
