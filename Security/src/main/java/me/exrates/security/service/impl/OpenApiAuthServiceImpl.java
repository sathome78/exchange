package me.exrates.security.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.OpenApiTokenDao;
import me.exrates.model.HmacSignature;
import me.exrates.model.OpenApiToken;
import me.exrates.security.exception.InvalidHmacSignatureException;
import me.exrates.security.exception.InvalidTimestampException;
import me.exrates.security.exception.TokenException;
import me.exrates.security.service.OpenApiAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Log4j2(topic = "open_api")
@Service
@PropertySource("classpath:/api.properties")
public class OpenApiAuthServiceImpl implements OpenApiAuthService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String HMAC_SIGNATURE_DELIMITER = "|";

    private final OpenApiTokenDao openApiTokenDao;
    private final UserDetailsService userDetailsService;

    @Value("${request.lifetime}")
    private Long requestLifetime;

    @Autowired
    public OpenApiAuthServiceImpl(OpenApiTokenDao openApiTokenDao,
                                  UserDetailsService userDetailsService) {
        this.openApiTokenDao = openApiTokenDao;
        this.userDetailsService = userDetailsService;
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails getUserByPublicKey(String method, String endpoint, Long timestamp, String publicKey, String signatureHex) {
        validateTimestamp(timestamp);
        OpenApiToken token = openApiTokenDao.getByPublicKey(publicKey).orElseThrow(() -> new TokenException("Public key not found: " + publicKey));
        validateSignature(token, method, endpoint, timestamp, signatureHex);
        UserDetails user = userDetailsService.loadUserByUsername(token.getUserEmail());
        Collection<GrantedAuthority> tokenPermissions = token.getPermissions()
                .stream()
                .map(perm -> new SimpleGrantedAuthority(perm.name()))
                .collect(Collectors.toList());
        tokenPermissions.addAll(user.getAuthorities());
        return new User(user.getUsername(), user.getPassword(), user.isEnabled(), user.isAccountNonExpired(), user.isCredentialsNonExpired(),
                user.isAccountNonLocked(), tokenPermissions);
    }


    private void validateSignature(OpenApiToken token, String method, String endpoint, Long timestamp, String signatureHex) {
        HmacSignature signature = new HmacSignature.Builder()
                .algorithm(HMAC_ALGORITHM)
                .delimiter(HMAC_SIGNATURE_DELIMITER)
                .apiSecret(token.getPrivateKey())
                .endpoint(endpoint)
                .requestMethod(method)
                .timestamp(timestamp)
                .publicKey(token.getPublicKey()).build();
        log.debug("Signature " + signature.getSignatureHexString());

        if (!signature.isSignatureEqual(signatureHex)) {
            throw new InvalidHmacSignatureException("Invalid signature: " + signatureHex);
        }
    }

    private void validateTimestamp(Long timestamp) {
        if (Math.abs(new Date().getTime() - timestamp) > requestLifetime) {
            throw new InvalidTimestampException("Invalid timestamp: " + String.valueOf(timestamp));
        }
    }
}
