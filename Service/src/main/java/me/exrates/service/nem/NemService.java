package me.exrates.service.nem;

import me.exrates.service.merchantStrategy.IRefillable;
import me.exrates.service.merchantStrategy.IWithdrawable;
import org.nem.core.model.Account;

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
        return true;
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

    boolean checkSendedTransaction(String hash, String additionalParams);

    @Override
    default boolean comissionDependsOnDestinationTag() {
        return true;
    }

    @Override
    default boolean specificWithdrawMerchantCommissionCountNeeded() {
        return true;
    }
}
