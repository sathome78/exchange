package me.exrates.security.service;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public interface WebSocketAuthenticatorService {
    UsernamePasswordAuthenticationToken getAuthenticatedOrFailByJwt(String token, String ip);

    UsernamePasswordAuthenticationToken getAuthenticatedOrFailByUsernamePassword(String email, String password);

    UsernamePasswordAuthenticationToken getAuthenticatedOrFailByHMAC(String method, String endpoint, Long timestamp, String publicKey, String signatureHex);
}
