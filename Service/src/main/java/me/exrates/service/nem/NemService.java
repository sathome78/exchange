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

/**
 * Created by maks on 18.07.2017.
 */
public interface NemService extends IRefillable, IWithdrawable {

   /* *//*method for admin manual check transaction by hash*//*
    void manualCheckNotReceivedTransaction(String hash);

    *//*return: true if tx validated; false if not validated but validationin process,
        throws Exception if declined*//*
    boolean checkSendedTransaction(String hash, String additionalParams);*/


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
