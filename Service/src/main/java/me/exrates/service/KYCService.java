package me.exrates.service;

import me.exrates.model.User;
import me.exrates.model.dto.kyc.EventStatus;
import me.exrates.model.dto.kyc.IdentityDataRequest;
import me.exrates.model.dto.kyc.responces.KycStatusResponseDto;
import me.exrates.model.dto.kyc.responces.OnboardingResponseDto;
import org.apache.commons.lang3.tuple.Pair;

public interface KYCService {

    String getVerificationUrl(int stepNumber, String languageCode, String countryCode);

    Pair<String, EventStatus> getVerificationStatus();

    Pair<String, EventStatus> checkResponseAndUpdateVerificationStep(String response, String s);

    OnboardingResponseDto startKyCProcessing(IdentityDataRequest identityDataRequest, String email);

    boolean updateUserVerificationInfo(User user, KycStatusResponseDto kycStatusResponseDto);

    void processingCallBack(String referenceId, KycStatusResponseDto kycStatusResponseDto);
}
