package me.exrates.service.impl;

import me.exrates.dao.UserVerificationDao;
import me.exrates.model.User;
import me.exrates.model.dto.UserVerificationDto;
import me.exrates.model.enums.VerificationDocumentType;
import me.exrates.service.UserVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserVerificationServiceImpl implements UserVerificationService {

    private final UserVerificationDao userVerificationDao;

    @Autowired
    public UserVerificationServiceImpl(UserVerificationDao userVerificationDao) {
        this.userVerificationDao = userVerificationDao;
    }

    @Override
    public UserVerificationDto save(UserVerificationDto verificationDto) {
        return userVerificationDao.save(verificationDto);
    }

    @Override
    public boolean delete(UserVerificationDto verificationDto) {
        return userVerificationDao.delete(verificationDto);
    }

    @Override
    public List<UserVerificationDto> findAllForUser(User user) {
        return userVerificationDao.findAllByUserId(user.getId());
    }

    @Override
    public UserVerificationDto findByUserAndDocType(User user, VerificationDocumentType type) {
        List<UserVerificationDto> dtoes = findAllForUser(user);
        return dtoes
                .stream()
                .filter(verificationDto -> verificationDto.getDocumentType() == type)
                .findAny()
                .orElse(null);
    }

}
