package me.exrates.service;

import me.exrates.model.User;
import me.exrates.model.dto.UserVerificationDto;
import me.exrates.model.enums.VerificationDocumentType;

import java.util.List;

public interface UserVerificationService {

    UserVerificationDto save(UserVerificationDto verificationDto);

    boolean delete(UserVerificationDto verificationDto);

    List<UserVerificationDto> findAllForUser(User user);

    UserVerificationDto findByUserAndDocType(User user, VerificationDocumentType type);
}
