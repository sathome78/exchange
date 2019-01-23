package me.exrates.service;

import me.exrates.model.dto.kyc.EventStatus;
import org.apache.commons.lang3.tuple.Pair;

public interface KYCService {

    String getVerificationUrl(int stepNumber, String languageCode, String countryCode);

    Pair<String, EventStatus> getVerificationStatus();

    Pair<String, EventStatus> checkResponseAndUpdateVerificationStep(String response, String s);
}
