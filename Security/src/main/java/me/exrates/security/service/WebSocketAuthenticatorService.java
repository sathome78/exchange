package me.exrates.security.service;

import me.exrates.security.exception.TokenException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class WebSocketAuthenticatorService {

    private static final Logger logger = LogManager.getLogger(WebSocketAuthenticatorService.class);

    private final AuthTokenService authTokenService;

    @Autowired
    public WebSocketAuthenticatorService(AuthTokenService authTokenService) {
        this.authTokenService = authTokenService;
    }

    public UsernamePasswordAuthenticationToken getAuthenticatedOrFail(final String token, final String ip){

        if(null == token || token.isEmpty()){
            logger.info("Received token was null or empty.");
            throw new TokenException("Received token was null or empty.");
        }
        UserDetails user;
        try {
            user = authTokenService.getUserByToken(token, ip);
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
}
