package me.exrates.service;

import me.exrates.model.dto.kyc.EventStatus;
import org.apache.commons.lang3.tuple.Pair;

public interface KYCService {

    String getVerificationUrl(int userId, String language, String country);

    Pair<String, EventStatus> getVerificationStatus(int userId);

    Pair<String, EventStatus> checkResponseAndUpdateStatus(String response, String s);
}
