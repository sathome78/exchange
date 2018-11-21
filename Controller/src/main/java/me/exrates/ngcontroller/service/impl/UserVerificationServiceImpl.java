package me.exrates.ngcontroller.service.impl;


import me.exrates.model.User;
import me.exrates.ngcontroller.dao.UserDocVerificationDao;
import me.exrates.ngcontroller.dao.UserInfoVerificationDao;
import me.exrates.ngcontroller.model.UserDocVerificationDto;
import me.exrates.ngcontroller.model.UserInfoVerificationDto;
import me.exrates.ngcontroller.model.enums.VerificationDocumentType;
import me.exrates.ngcontroller.service.UserVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserVerificationServiceImpl implements UserVerificationService {

    private final UserInfoVerificationDao userVerificationDao;
    private final UserDocVerificationDao userDocVerificationDao;

    @Autowired
    public UserVerificationServiceImpl(UserInfoVerificationDao userVerificationDao,
                                       UserDocVerificationDao userDocVerificationDao) {
        this.userVerificationDao = userVerificationDao;
        this.userDocVerificationDao = userDocVerificationDao;
    }

    @Override
    public UserInfoVerificationDto save(UserInfoVerificationDto verificationDto) {
        return userVerificationDao.save(verificationDto);
    }

    @Override
    public UserDocVerificationDto save(UserDocVerificationDto verificationDto) {
        return userDocVerificationDao.save(verificationDto);
    }

    @Override
    public boolean delete(UserInfoVerificationDto verificationDto) {
        return userVerificationDao.delete(verificationDto);
    }

    @Override
    public boolean delete(UserDocVerificationDto verificationDto) {
        return userDocVerificationDao.delete(verificationDto);
    }

    @Override
    public UserInfoVerificationDto findByUser(User user) {
        return userVerificationDao.findByUserId(user.getId());
    }

    @Override
    public UserDocVerificationDto findByUserAndDocumentType(User user, VerificationDocumentType type) {
        return userDocVerificationDao.findByUserIdAndDocumentType(user.getId(), type);
    }

    @Override
    public List<UserDocVerificationDto> findDocsByUser(User user) {
        return userDocVerificationDao.findAllByUser(user);
    }

}
