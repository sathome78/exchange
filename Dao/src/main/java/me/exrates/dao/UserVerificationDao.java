package me.exrates.dao;

import me.exrates.model.dto.UserVerificationDto;

import java.util.List;

public interface UserVerificationDao {

    UserVerificationDto save(UserVerificationDto verificationDto);

    boolean delete(UserVerificationDto verificationDto);

    List<UserVerificationDto> findAllByUserId(Integer userId);
}
