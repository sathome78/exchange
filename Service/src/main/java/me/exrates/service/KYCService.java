package me.exrates.service;

import me.exrates.model.User;
import me.exrates.model.dto.kyc.EventStatus;
import me.exrates.model.dto.kyc.IdentityDataRequest;
import me.exrates.model.dto.kyc.responces.KycStatusResponseDto;
import me.exrates.model.dto.kyc.responces.OnboardingResponseDto;
import org.apache.commons.lang3.tuple.Pair;

public interface KYCService {

    String getVerificationUrl(String languageCode, String countryCode);

    String getQuberaKycStatus(String email);

    String getShuftiProKycStatus(String email);

    Pair<String, EventStatus> checkResponseAndUpdateVerificationStatus(String response, String s);

    OnboardingResponseDto startKyCProcessing(IdentityDataRequest identityDataRequest, String email);

    boolean updateUserVerificationInfo(User user, KycStatusResponseDto kycStatusResponseDto);

    void processingCallBack(String referenceId, KycStatusResponseDto kycStatusResponseDto);
}