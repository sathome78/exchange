package me.exrates.ngcontroller.service;

import me.exrates.model.User;
import me.exrates.ngcontroller.model.UserDocVerificationDto;
import me.exrates.ngcontroller.model.UserInfoVerificationDto;
import me.exrates.ngcontroller.model.enums.VerificationDocumentType;

import java.util.List;

public interface UserVerificationService {

    UserInfoVerificationDto save(UserInfoVerificationDto verificationDto);

    UserDocVerificationDto save(UserDocVerificationDto verificationDto);

    boolean delete(UserInfoVerificationDto verificationDto);

    boolean delete(UserDocVerificationDto verificationDto);

    UserInfoVerificationDto findByUser(User user);

    UserDocVerificationDto findByUserAndDocumentType(User user, VerificationDocumentType type);

    List<UserDocVerificationDto> findDocsByUser(User user);
}
