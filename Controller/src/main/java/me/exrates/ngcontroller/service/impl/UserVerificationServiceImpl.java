package me.exrates.ngcontroller.service.impl;


import me.exrates.dao.UserDao;
import me.exrates.model.Email;
import me.exrates.model.TemporalToken;
import me.exrates.model.User;
import me.exrates.model.UserEmailDto;
import me.exrates.model.enums.TokenType;
import me.exrates.model.enums.UserStatus;
import me.exrates.ngcontroller.dao.UserDocVerificationDao;
import me.exrates.ngcontroller.dao.UserInfoVerificationDao;
import me.exrates.ngcontroller.mobel.UserDocVerificationDto;
import me.exrates.ngcontroller.mobel.UserInfoVerificationDto;
import me.exrates.ngcontroller.mobel.enums.VerificationDocumentType;
import me.exrates.ngcontroller.service.UserVerificationService;
import me.exrates.service.SendMailService;
import me.exrates.service.UserService;
import me.exrates.service.util.IpUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class UserVerificationServiceImpl implements UserVerificationService {

    private final UserInfoVerificationDao userVerificationDao;
    private final UserDocVerificationDao userDocVerificationDao;
    private final UserDao userDao;
    private final UserService userService;
    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;
    private final SendMailService sendMailService;

    @Autowired
    public UserVerificationServiceImpl(UserInfoVerificationDao userVerificationDao,
                                       UserDocVerificationDao userDocVerificationDao,
                                       UserDao userDao,
                                       UserService userService,
                                       MessageSource messageSource,
                                       LocaleResolver localeResolver,
                                       SendMailService sendMailService) {
        this.userVerificationDao = userVerificationDao;
        this.userDocVerificationDao = userDocVerificationDao;
        this.userDao = userDao;
        this.userService = userService;
        this.messageSource = messageSource;
        this.localeResolver = localeResolver;
        this.sendMailService = sendMailService;
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

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveUser(UserEmailDto userEmailDto, HttpServletRequest request) {

        User user = new User();
        user.setEmail(userEmailDto.getEmail());
        if (!StringUtils.isEmpty(userEmailDto.getParentEmail())) user.setParentEmail(userEmailDto.getParentEmail());
        user.setIp(IpUtils.getClientIpAddress(request));

        if (!(userDao.create(user) && userDao.insertIp(user.getEmail(), user.getIp()))) {
            return false;
        }

        int idUser = userDao.getIdByEmail(userEmailDto.getEmail());
        user.setId(idUser);

        sendEmailWithToken(user,
                TokenType.REGISTRATION,
                "emailsubmitregister.subject",
                "emailsubmitregister.text",
                localeResolver.resolveLocale(request));

        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean confirmRegistrationUser(String token) {
        User user = userDao.getUserByTemporalToken(token);
        user.setUserStatus(UserStatus.ACTIVE);
        userDao.updateUserStatus(user);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public void sendEmailWithToken(User user, TokenType tokenType, String emailSubject, String emailText, Locale locale) {
        TemporalToken token = new TemporalToken();
        token.setUserId(user.getId());
        token.setValue(UUID.randomUUID().toString());
        token.setTokenType(tokenType);
        token.setCheckIp(user.getIp());
        token.setAlreadyUsed(false);

        userService.createTemporalToken(token);

        Email email = new Email();
        StringBuilder confirmationUrl = new StringBuilder("?t=" + token.getValue());

        String rootUrl = "url";

        email.setMessage(
                messageSource.getMessage(emailText, null, locale) +
                        " <a href='" +
                        rootUrl +
                        confirmationUrl.toString() +
                        "'>" + messageSource.getMessage("admin.ref", null, locale) + "</a>"
        );

        email.setSubject(messageSource.getMessage(emailSubject, null, locale));
        email.setTo(user.getEmail());
        sendMailService.sendMailMandrill(email);
    }

}
