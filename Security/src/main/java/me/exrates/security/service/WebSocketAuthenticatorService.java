package me.exrates.security.service;

import me.exrates.model.exceptions.InvalidCredentialsException;
import me.exrates.security.exception.TokenException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


@Component
public class WebSocketAuthenticatorService {
    private static final Logger logger = LogManager.getLogger(WebSocketAuthenticatorService.class);

    private final AuthTokenService authTokenService;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final OpenApiAuthService openApiAuthService;

    @Autowired
    public WebSocketAuthenticatorService(AuthTokenService authTokenService, UserDetailsService userDetailsService, OpenApiAuthService openApiAuthService) {
        this.authTokenService = authTokenService;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.openApiAuthService = openApiAuthService;
    }

    public UsernamePasswordAuthenticationToken getAuthenticatedOrFailByJwt(final String token, final String ip){

        if(StringUtils.isEmpty(token)){
            logger.info("Received token was null or empty.");
            throw new TokenException("Received token was null or empty.");
        }
        UserDetails user;
        try {
            user = authTokenService.getUserByToken(token);
        } catch (TokenException e) {
            logger.info("Failed to retrieve user by token as " + e.getMessage());
            throw new TokenException(e.getMessage());
        }
//		logger.error("$$$$$$ Registered user: " + SecurityContextHolder.getContext().getAuthentication().getName());
//		if(!(user.getUsername().equals(SecurityContextHolder.getContext().getAuthentication().getName()))){
//			throw new BadCredentialsException("Current principal is not the same with user (username) " + user.getUsername());
//		}
        return new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
    }

    public UsernamePasswordAuthenticationToken getAuthenticatedOrFailByUsernamePassword(final String email, final String password){

        if (email == null || email.trim().length() < 3) {
            throw new AuthenticationCredentialsNotFoundException("Username was null or empty.");
        }
        if (password == null || password.trim().length() < 1) {
            throw new AuthenticationCredentialsNotFoundException("Password was null or empty.");
        }
        UserDetails user;
        try {
            user = userDetailsService.loadUserByUsername(email);
        } catch (Exception e) {
            System.out.println("cant get user " + e);
            logger.info("Failed to retrieve user by token as " + e.getMessage());
            throw new InvalidCredentialsException(e.getMessage());
        }
        if(!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        return new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
    }

    public UsernamePasswordAuthenticationToken getAuthenticatedOrFailByHMAC(String method, String endpoint, Long timestamp, String publicKey, String signatureHex) {
        UserDetails user;
        try {
            user = openApiAuthService.getUserByPublicKey(method, endpoint, timestamp, publicKey, signatureHex);
        } catch (Exception e) {
            throw new InvalidCredentialsException();
        }
        return new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
    }
}