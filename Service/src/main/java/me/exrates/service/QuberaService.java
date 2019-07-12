package me.exrates.service;

import me.exrates.model.dto.AccountQuberaResponseDto;
import me.exrates.model.dto.kyc.IdentityDataRequest;
import me.exrates.model.dto.kyc.responces.KycStatusResponseDto;
import me.exrates.model.dto.kyc.responces.OnboardingResponseDto;
import me.exrates.model.dto.qubera.AccountInfoDto;
import me.exrates.model.dto.qubera.ExternalPaymentDto;
import me.exrates.model.dto.qubera.PaymentRequestDto;
import me.exrates.model.dto.qubera.QuberaPaymentInfoDto;
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

    boolean logResponse(QuberaRequestDto requestDto);

    AccountQuberaResponseDto createAccount(String email);

    boolean checkAccountExist(String email, String currency);

    AccountInfoDto getInfoAccount(String principalEmail);

    ResponsePaymentDto createPaymentToMaster(String email, PaymentRequestDto paymentRequestDto);

    ResponsePaymentDto createPaymentFromMater(String email, PaymentRequestDto paymentRequestDto);

    boolean confirmPaymentToMaster(Integer paymentId);

    boolean confirmPaymentFRomMaster(Integer paymentId);

    ResponsePaymentDto createExternalPayment(ExternalPaymentDto externalPaymentDto, String email);

    String confirmExternalPayment(Integer paymentId);

    QuberaPaymentInfoDto getInfoForPayment(String email);

    void sendNotification(QuberaRequestDto quberaRequestDto);

    String getUserVerificationStatus(String email);

    void processingCallBack(String referenceId, KycStatusResponseDto kycStatusResponseDto);

    OnboardingResponseDto startVerificationProcessing(IdentityDataRequest identityDataRequest, String email);
}
