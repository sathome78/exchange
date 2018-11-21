package me.exrates.ngcontroller.dao;

import me.exrates.model.User;
import me.exrates.ngcontroller.model.UserDocVerificationDto;
import me.exrates.ngcontroller.model.enums.VerificationDocumentType;

import java.util.List;

public interface UserDocVerificationDao {

    UserDocVerificationDto save(UserDocVerificationDto verificationDto);

    boolean delete(UserDocVerificationDto verificationDto);

    UserDocVerificationDto findByUserIdAndDocumentType(Integer userId, VerificationDocumentType documentType);

    List<UserDocVerificationDto> findAllByUser(User user);

}
