package me.exrates.model.referral.enums;

public enum ReferralRequestStatus {
    CREATED_USER,
    WAITING_MANUAL_POSTING,
    WAITING_AUTO_POSTING,
    POSTED_MANUAL,
    POSTED_AUTO,
    IN_POSTING,
    DECLINED_ANALYTICS_MANUAL,
    DECLINED_ERROR,
}
