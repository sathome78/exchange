package me.exrates.service;

import me.exrates.model.dto.AccountCreateDto;
import me.exrates.model.dto.AccountInfoDto;
import me.exrates.model.dto.AccountQuberaResponseDto;
import me.exrates.model.dto.PaymentRequestDto;
import me.exrates.model.dto.QuberaRequestDto;
import me.exrates.model.dto.ResponsePaymentDto;
import me.exrates.service.merchantStrategy.IRefillable;
import me.exrates.service.merchantStrategy.IWithdrawable;

public interface QuberaService extends IRefillable, IWithdrawable {

    @Override
    default Boolean createdRefillRequestRecordNeeded() {
        return false;
    }

    @Override
    default Boolean needToCreateRefillRequestRecord() {
        return true;
    }

    @Override
    default Boolean toMainAccountTransferringConfirmNeeded() {
        return false;
    }

    @Override
    default Boolean generatingAdditionalRefillAddressAvailable() {
        return null;
    }

    @Override
    default Boolean additionalTagForWithdrawAddressIsUsed() {
        return false;
    }

    @Override
    default Boolean withdrawTransferringConfirmNeeded() {
        return false;
    }

    @Override
    default Boolean additionalFieldForRefillIsUsed() {
        return false;
    }

    boolean logResponse(QuberaRequestDto requestDto);

    AccountQuberaResponseDto createAccount(AccountCreateDto accountCreateDto);

    boolean checkAccountExist(String email, String currency);

    AccountInfoDto getInfoAccount(String principalEmail);

    ResponsePaymentDto createPaymentToMaster(String email, PaymentRequestDto paymentRequestDto);

    ResponsePaymentDto createPaymentFromMater(String email, PaymentRequestDto paymentRequestDto);

    String confirmPaymentToMaster(Integer paymentId);

    String confirmPaymentFRomMaster(Integer paymentId);
}
