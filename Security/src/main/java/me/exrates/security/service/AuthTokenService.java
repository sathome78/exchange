package me.exrates.security.service;

import me.exrates.model.dto.mobileApiDto.AuthTokenDto;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Created by OLEG on 23.08.2016.
 */
public interface AuthTokenService {

    Optional<AuthTokenDto> retrieveTokenNg(String username, String encodedPassword,
                                           HttpServletRequest request, String pin, boolean checkpin);

    Optional<AuthTokenDto> retrieveToken(String username, String password);

    UserDetails getUserByToken(String token, String ip);

    @Scheduled(fixedDelay = 24L * 60L * 60L * 1000L, initialDelay = 60000L)
    void deleteExpiredTokens();
}
