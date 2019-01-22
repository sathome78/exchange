package me.exrates.ngcontroller.service;

import me.exrates.model.User;
import me.exrates.model.UserEmailDto;
import me.exrates.model.dto.mobileApiDto.AuthTokenDto;
import me.exrates.model.enums.TokenType;
import me.exrates.ngcontroller.model.PasswordCreateDto;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

public interface NgUserService {

    boolean registerUser(UserEmailDto userEmailDto, HttpServletRequest request);

    AuthTokenDto createPassword(PasswordCreateDto passwordCreateDto, HttpServletRequest request);

    boolean recoveryPassword(UserEmailDto userEmailDto, HttpServletRequest request);

    boolean createPasswordRecovery(PasswordCreateDto passwordCreateDto, HttpServletRequest request);

    boolean validateTempToken(String token);

    void sendEmailDisable2Fa(String email);

    void sendEmailEnable2Fa(String email);

    void resendEmailForFinishRegistration(User user);

}
