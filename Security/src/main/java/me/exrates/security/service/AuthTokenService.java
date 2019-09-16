package me.exrates.security.service;

import me.exrates.model.User;
import me.exrates.model.dto.mobileApiDto.AuthTokenDto;
import me.exrates.model.dto.mobileApiDto.UserAuthenticationDto;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Created by OLEG on 23.08.2016.
 */
public interface AuthTokenService {

    void deleteExpiredTokens();

    Optional<AuthTokenDto> retrieveToken(String username, String password);

    UserDetails getUserByToken(String token);

    Optional<AuthTokenDto> retrieveTokenNg(UserAuthenticationDto dto);

    Optional<AuthTokenDto> retrieveTokenNg(String email);

    boolean isValid(HttpServletRequest request);

    UserDetails getUserByToken(String token, String ip);

    boolean sessionExpiredProcessing(String token, User user);

    void updateSessionLifetime(String token, int intervalInMinutes);
}