package me.exrates.security.service;

import me.exrates.model.User;
import me.exrates.model.dto.mobileApiDto.AuthTokenDto;
import me.exrates.model.dto.mobileApiDto.UserAuthenticationDto;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Created by OLEG on 23.08.2016.
 */
public interface AuthTokenService {

    Optional<AuthTokenDto> retrieveToken(String username, String password);

    UserDetails getUserByToken(String token);

    @Scheduled(fixedDelay = 24L * 60L * 60L * 1000L, initialDelay = 60000L)
    void deleteExpiredTokens();

    Optional<AuthTokenDto> retrieveTokenNg(UserAuthenticationDto dto);

    Optional<AuthTokenDto> retrieveTokenNg(String email);

    AuthTokenDto refreshTokenNg(String email, HttpServletRequest request);

    boolean isValid(HttpServletRequest request);

    UserDetails getUserByToken(String token, String ip);

    boolean sessionExpiredProcessing(String token, User user);
}