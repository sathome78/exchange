package me.exrates.service;

import me.exrates.model.dto.AccountCreateDto;
import me.exrates.model.dto.qubera.AccountInfoDto;
import me.exrates.model.dto.AccountQuberaResponseDto;
import me.exrates.model.dto.qubera.ExternalPaymentDto;
import me.exrates.model.dto.qubera.PaymentRequestDto;
import me.exrates.model.dto.qubera.QuberaRequestDto;
import me.exrates.model.dto.qubera.ResponsePaymentDto;
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

    ResponsePaymentDto createExternalPayment(ExternalPaymentDto externalPaymentDto, String email);

    String confirmExternalPayment(Integer paymentId);
}
