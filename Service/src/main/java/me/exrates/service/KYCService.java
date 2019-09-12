package me.exrates.service;

import me.exrates.model.dto.kyc.EventStatus;
import org.apache.commons.lang3.tuple.Pair;

public interface KYCService {

    String getVerificationUrl(String languageCode, String countryCode);

    String getQuberaKycStatus(String email);

    String getShuftiProKycStatus(String email);

    Pair<String, EventStatus> checkResponseAndUpdateVerificationStatus(String response, String s);
}
