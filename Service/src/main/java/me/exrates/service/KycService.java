package me.exrates.service;

import me.exrates.model.kyc.KYC;

public interface KycService {
    void save(int userId, KYC kyc);
    void update(int userId, KYC kyc);
    void updateByAdmin(int userId, KYC kyc);
    KYC getKyc(int userId);
    void sendKycForApprove(int userId);
    void approveKyc(int userId, String admin);
    void rejectKyc(int userId, String admin);
}
