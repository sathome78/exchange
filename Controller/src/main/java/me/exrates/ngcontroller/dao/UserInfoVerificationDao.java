package me.exrates.ngcontroller.dao;

import me.exrates.ngcontroller.mobel.UserInfoVerificationDto;

public interface UserInfoVerificationDao {

    UserInfoVerificationDto save(UserInfoVerificationDto verificationDto);

    boolean delete(UserInfoVerificationDto verificationDto);

    UserInfoVerificationDto findByUserId(Integer userId);
}
