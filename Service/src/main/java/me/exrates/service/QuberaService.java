package me.exrates.service;

import me.exrates.model.dto.AccountQuberaResponseDto;
import me.exrates.model.dto.kyc.IdentityDataRequest;
import me.exrates.model.dto.kyc.responces.KycStatusResponseDto;
import me.exrates.model.dto.kyc.responces.OnboardingResponseDto;
import me.exrates.model.dto.qubera.AccountInfoDto;
import me.exrates.model.dto.qubera.ExternalPaymentShortDto;
import me.exrates.model.dto.qubera.PaymentRequestDto;
import me.exrates.model.dto.qubera.QuberaLog;
import me.exrates.model.dto.qubera.QuberaPaymentInfoDto;
import me.exrates.model.dto.qubera.ResponsePaymentDto;
import me.exrates.model.dto.qubera.responses.ExternalPaymentResponseDto;
import me.exrates.service.merchantStrategy.IRefillable;
import me.exrates.service.merchantStrategy.IWithdrawable;

public interface QuberaService extends IRefillable, IWithdrawable {

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

    boolean logResponse(QuberaLog requestDto);

    AccountQuberaResponseDto createAccount(String email);

    boolean checkAccountExist(String email, String currency);

    AccountInfoDto getInfoAccount(String principalEmail);

    ResponsePaymentDto createPaymentToMaster(String email, PaymentRequestDto paymentRequestDto);

    ResponsePaymentDto createPaymentFromMater(String email, PaymentRequestDto paymentRequestDto);

    boolean confirmPaymentToMaster(Integer paymentId);

    boolean confirmPaymentFRomMaster(Integer paymentId);

    ExternalPaymentResponseDto createExternalPayment(ExternalPaymentShortDto externalPaymentDto, String email);

    QuberaPaymentInfoDto getInfoForPayment(String email);

    void sendNotification(QuberaLog quberaRequestDto);

    String getUserVerificationStatus(String email);

    void processingCallBack(String referenceId, KycStatusResponseDto kycStatusResponseDto);

    OnboardingResponseDto startVerificationProcessing(IdentityDataRequest identityDataRequest, String email);

    boolean confirmExternalPayment(Integer paymentId);

    byte[] getPdfFileForPayment(String email);
}
