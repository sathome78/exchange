package me.exrates.dao;

import me.exrates.model.dto.kyc.responces.KycStatusResponseDto;

public interface KycDao {

    boolean updateUserVerification(int userId, KycStatusResponseDto kycResponseDto);
}
