package me.exrates.ngcontroller.service;

import me.exrates.model.UserEmailDto;
import me.exrates.model.dto.mobileApiDto.AuthTokenDto;
import me.exrates.ngcontroller.model.PasswordCreateDto;

import javax.servlet.http.HttpServletRequest;

public interface NgUserService {

    boolean registerUser(UserEmailDto userEmailDto, HttpServletRequest request);

    AuthTokenDto createPassword(PasswordCreateDto passwordCreateDto, HttpServletRequest request);

    boolean recoveryPassword(UserEmailDto userEmailDto, HttpServletRequest request);

}
