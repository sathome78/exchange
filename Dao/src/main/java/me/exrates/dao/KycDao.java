package me.exrates.dao;

import me.exrates.model.kyc.KYC;
import me.exrates.model.kyc.KycInfo;
import me.exrates.model.kyc.KycStatus;

public interface KycDao {
    int saveIndividual(int userId, KYC kyc);
    int saveLegalEntity(int userId, KYC kyc);
    int updateIndividual(int userId, KYC kyc);
    int updateLegalEntity(int userId, KYC kyc);
    KycInfo getInfo(int userId);
    KYC getDetailed(int userId);
    void setStatus(int userId, KycStatus status, String admin);
    boolean inProgress(int userId);
    KycStatus getKycStatus(int userId);
}
