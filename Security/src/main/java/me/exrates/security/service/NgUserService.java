package me.exrates.security.service;

import me.exrates.model.User;
import me.exrates.model.UserEmailDto;
import me.exrates.model.dto.mobileApiDto.AuthTokenDto;
import me.exrates.model.ngModel.PasswordCreateDto;

import javax.servlet.http.HttpServletRequest;

public interface NgUserService {
    boolean registerUser(UserEmailDto userEmailDto, HttpServletRequest request);

    AuthTokenDto createPassword(PasswordCreateDto passwordCreateDto, HttpServletRequest request);

    boolean recoveryPassword(UserEmailDto userEmailDto, HttpServletRequest request);

    boolean createPasswordRecovery(PasswordCreateDto passwordCreateDto, HttpServletRequest request);

    boolean validateTempToken(String token);

    void sendEmailDisable2Fa(String userEmail);

    void sendEmailEnable2Fa(String userEmail);

    void resendEmailForFinishRegistration(User user);
}
