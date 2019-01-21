package me.exrates.service;

import me.exrates.model.dto.kyc.EventStatus;

import java.util.Locale;

public interface KYCService {

    String getVerificationUrl(int userId, Locale locale);

    EventStatus getVerificationStatus(int userId);

    void checkResponseAndUpdateStatus(String response, String s);
}
