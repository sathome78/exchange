package me.exrates.security.service.impl;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.ApiAuthTokenDao;
import me.exrates.model.ApiAuthToken;
import me.exrates.model.User;
import me.exrates.model.dto.mobileApiDto.AuthTokenDto;
import me.exrates.model.dto.mobileApiDto.UserAuthenticationDto;
import me.exrates.model.enums.UserRole;
import me.exrates.security.exception.IncorrectPasswordException;
import me.exrates.security.exception.MissingCredentialException;
import me.exrates.security.exception.TokenException;
import me.exrates.security.service.AuthTokenService;
import me.exrates.service.SessionParamsService;
import me.exrates.service.UserService;
import me.exrates.service.exception.api.ErrorCode;
import me.exrates.service.util.RestApiUtilComponent;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Log4j2
@Service
@PropertySource(value = {"classpath:/angular.properties"})
public class AuthTokenServiceImpl implements AuthTokenService {

    @Value("${token.key}")
    private String tokenKey;

    @Value("${token.max-duration: 120}")
    private int tokenMaxDuration;

    private Map<String, Integer> localSessionParamsMap;

    private final PasswordEncoder passwordEncoder;
    private final ApiAuthTokenDao apiAuthTokenDao;
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final RestApiUtilComponent restApiUtilComponent;

    @Autowired
    public AuthTokenServiceImpl(PasswordEncoder passwordEncoder,
                                ApiAuthTokenDao apiAuthTokenDao,
                                @Qualifier("userDetailsService") UserDetailsService userDetailsService,
                                SessionParamsService sessionParamsService,
                                UserService userService,
                                RestApiUtilComponent restApiUtilComponent) {
        this.passwordEncoder = passwordEncoder;
        this.apiAuthTokenDao = apiAuthTokenDao;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.restApiUtilComponent = restApiUtilComponent;

        this.localSessionParamsMap = sessionParamsService.getAll()
                .stream()
                .collect(Collectors.toMap(
                        Pair::getLeft,
                        Pair::getRight,
                        (v1, v2) -> v2
                ));
    }

    //todo: delete when schedule service will be deployed
    @Override
    public void deleteExpiredTokens() {
        int deletedQuantity = apiAuthTokenDao.deleteAllExpired();
        log.info(String.format("%d expired tokens deleted", deletedQuantity));
    }

    @Override
    public Optional<AuthTokenDto> retrieveToken(String username, String encodedPassword) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(encodedPassword)) {
            throw new MissingCredentialException("Credentials missing");
        }
        String password = restApiUtilComponent.decodePassword(encodedPassword);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (passwordEncoder.matches(password, userDetails.getPassword())) {
            Integer duration = localSessionParamsMap.getOrDefault(username, tokenMaxDuration);
            Date expiredAt = getExpirationTime(duration);

            ApiAuthToken apiAuthToken = createAuthToken(userDetails.getUsername(), expiredAt);

            Map<String, Object> tokenData = new HashMap<>();
            tokenData.put("token_id", apiAuthToken.getId());
            tokenData.put("username", apiAuthToken.getUsername());
            tokenData.put("value", apiAuthToken.getValue());

            final String token = Jwts.builder()
                    .setClaims(tokenData)
                    .signWith(SignatureAlgorithm.HS512, tokenKey)
                    .compact();
            return Optional.of(new AuthTokenDto(token));
        } else {
            throw new IncorrectPasswordException("Incorrect password");
        }
    }

    private ApiAuthToken createAuthToken(String username, Date expiredAt) {
        ApiAuthToken token = new ApiAuthToken();
        token.setUsername(username);
        token.setValue(UUID.randomUUID().toString());
        token.setExpiredAt(expiredAt);
        Long id = apiAuthTokenDao.createToken(token);
        token.setId(id);
        return token;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, noRollbackFor = TokenException.class)
    public UserDetails getUserByToken(String token) {
        if (StringUtils.isEmpty(token)) {
            throw new TokenException("No authentication token header found", ErrorCode.MISSING_AUTHENTICATION_TOKEN);
        }
        DefaultClaims claims = getClaims(token);

        Long tokenId = Long.parseLong(String.valueOf(claims.get("token_id")));
        String username = claims.get("username", String.class);
        String value = claims.get("value", String.class);

        Optional<ApiAuthToken> tokenSearchResult = apiAuthTokenDao.retrieveTokenById(tokenId);
        if (tokenSearchResult.isPresent()) {
            ApiAuthToken savedToken = tokenSearchResult.get();
            if (!(username.equals(savedToken.getUsername()) && value.equals(savedToken.getValue()))) {
                throw new TokenException("Invalid token", ErrorCode.INVALID_AUTHENTICATION_TOKEN);
            }
            if (savedToken.getExpiredAt().before(new Date())) {
                apiAuthTokenDao.deleteExpiredToken(tokenId);
                throw new TokenException("Token expired", ErrorCode.EXPIRED_AUTHENTICATION_TOKEN);
            }
            Integer duration = localSessionParamsMap.getOrDefault(username, tokenMaxDuration);
            Date expiredAt = getExpirationTime(duration);

            boolean updated = apiAuthTokenDao.updateExpiration(tokenId, expiredAt);
            log.debug("Expiration period for session: {} {}.", token, updated ? "have been updated" : "have not been updated");

            return userDetailsService.loadUserByUsername(username);
        } else {
            throw new TokenException("Token not found", ErrorCode.TOKEN_NOT_FOUND);
        }
    }

    @Override
    public Optional<AuthTokenDto> retrieveTokenNg(UserAuthenticationDto dto) {
        return prepareAuthTokenNg(dto.getEmail());
    }

    @Override
    public Optional<AuthTokenDto> retrieveTokenNg(String email) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        return prepareAuthTokenNg(userDetails.getUsername());
    }

    @Override
    public boolean isValid(HttpServletRequest request) {
        String token = request.getHeader("Exrates-Rest-Token");
        if (StringUtils.isEmpty(token)) {
            throw new TokenException("No authentication token header found", ErrorCode.MISSING_AUTHENTICATION_TOKEN);
        }
        DefaultClaims claims = getClaims(token);

        Long tokenId = Long.parseLong(String.valueOf(claims.get("token_id")));
        String username = claims.get("username", String.class);
        String value = claims.get("value", String.class);

        Optional<ApiAuthToken> tokenSearchResult = apiAuthTokenDao.retrieveTokenById(tokenId);
        if (tokenSearchResult.isPresent()) {
            ApiAuthToken savedToken = tokenSearchResult.get();
            if (!(username.equals(savedToken.getUsername()) && value.equals(savedToken.getValue()))) {
                throw new TokenException("Invalid token", ErrorCode.INVALID_AUTHENTICATION_TOKEN);
            }
            return savedToken.getExpiredAt().after(new Date());
        } else {
            throw new TokenException("Token not found", ErrorCode.TOKEN_NOT_FOUND);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, noRollbackFor = TokenException.class)
    public UserDetails getUserByToken(String token, String currentIp) {
        if (StringUtils.isEmpty(token)) {
            throw new TokenException("No authentication token header found", ErrorCode.MISSING_AUTHENTICATION_TOKEN);
        }
        DefaultClaims claims = getClaims(token);

        Long tokenId = Long.parseLong(String.valueOf(claims.get("token_id")));
        String username = claims.get("username", String.class);
        String value = claims.get("value", String.class);
        String ip = claims.get("client_ip", String.class);

        Optional<ApiAuthToken> tokenSearchResult = apiAuthTokenDao.retrieveTokenById(tokenId);
        if (tokenSearchResult.isPresent()) {
            ApiAuthToken savedToken = tokenSearchResult.get();
            if (!(username.equals(savedToken.getUsername()) && value.equals(savedToken.getValue()))) {
                throw new TokenException("Invalid token", ErrorCode.INVALID_AUTHENTICATION_TOKEN);
            }
            log.debug("request ip {}", ip);
            if (StringUtils.isNotEmpty(ip) && !ip.equals(currentIp)) {
                throw new TokenException("Invalid token", ErrorCode.INVALID_AUTHENTICATION_TOKEN);
            }
            if (savedToken.getExpiredAt().before(new Date())) {
                apiAuthTokenDao.deleteExpiredToken(tokenId);
                throw new TokenException("Token expired", ErrorCode.EXPIRED_AUTHENTICATION_TOKEN);
            }
            Integer duration = localSessionParamsMap.getOrDefault(username, tokenMaxDuration);
            Date expiredAt = getExpirationTime(duration);

            boolean updated = apiAuthTokenDao.updateExpiration(tokenId, expiredAt);
            log.debug("Expiration period for session: {} {}.", token, updated ? "have been updated" : "have not been updated");

            return userDetailsService.loadUserByUsername(username);
        } else {
            throw new TokenException("Token not found", ErrorCode.TOKEN_NOT_FOUND);
        }
    }

    private Optional<AuthTokenDto> prepareAuthTokenNg(String username) {
        User user = userService.findByEmail(username);

        Integer duration = localSessionParamsMap.getOrDefault(username, tokenMaxDuration);
        Date expiredAt = getExpirationTime(duration);

        ApiAuthToken apiAuthToken = createAuthToken(username, expiredAt);

        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("token_id", apiAuthToken.getId());
        tokenData.put("username", apiAuthToken.getUsername());
        tokenData.put("value", apiAuthToken.getValue());
        tokenData.put("publicId", user.getPublicId());
        tokenData.put("userRole", Objects.isNull(user.getRole()) ? UserRole.USER.name() : user.getRole().name());

        final String token = Jwts.builder()
                .setClaims(tokenData)
                .signWith(SignatureAlgorithm.HS512, tokenKey)
                .compact();
        return Optional.of(new AuthTokenDto(token));
    }

    @Override
    @Transactional(rollbackFor = Exception.class, noRollbackFor = TokenException.class)
    public boolean sessionExpiredProcessing(String token, User user) {
        if (StringUtils.isEmpty(token)) {
            throw new TokenException("No authentication token header found", ErrorCode.MISSING_AUTHENTICATION_TOKEN);
        }
        DefaultClaims claims = getClaims(token);

        Long tokenId = Long.parseLong(String.valueOf(claims.get("token_id")));
        String username = claims.get("username", String.class);
        String value = claims.get("value", String.class);

        Optional<ApiAuthToken> tokenSearchResult = apiAuthTokenDao.retrieveTokenById(tokenId);
        if (tokenSearchResult.isPresent()) {
            ApiAuthToken savedToken = tokenSearchResult.get();
            if (!(username.equals(savedToken.getUsername()) && value.equals(savedToken.getValue()))) {
                throw new TokenException("Invalid token", ErrorCode.INVALID_AUTHENTICATION_TOKEN);
            }

            if (savedToken.getExpiredAt().before(new Date())) {
                return apiAuthTokenDao.deleteAllByUsername(username);
            }
            return apiAuthTokenDao.deleteAllExceptCurrent(tokenId, username);
        } else {
            throw new TokenException("Token not found", ErrorCode.TOKEN_NOT_FOUND);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, noRollbackFor = TokenException.class)
    public void updateSessionLifetime(String token, int intervalInMinutes) {
        if (StringUtils.isEmpty(token)) {
            throw new TokenException("No authentication token header found", ErrorCode.MISSING_AUTHENTICATION_TOKEN);
        }
        DefaultClaims claims = getClaims(token);

        Long tokenId = Long.parseLong(String.valueOf(claims.get("token_id")));
        String username = claims.get("username", String.class);
        String value = claims.get("value", String.class);

        Optional<ApiAuthToken> tokenSearchResult = apiAuthTokenDao.retrieveTokenById(tokenId);
        if (tokenSearchResult.isPresent()) {
            ApiAuthToken savedToken = tokenSearchResult.get();
            if (!(username.equals(savedToken.getUsername()) && value.equals(savedToken.getValue()))) {
                throw new TokenException("Invalid token", ErrorCode.INVALID_AUTHENTICATION_TOKEN);
            }

            if (savedToken.getExpiredAt().before(new Date())) {
                apiAuthTokenDao.deleteExpiredToken(tokenId);
                throw new TokenException("Token expired", ErrorCode.EXPIRED_AUTHENTICATION_TOKEN);
            }
            if (localSessionParamsMap.containsKey(username)) {
                localSessionParamsMap.replace(username, intervalInMinutes);
            } else {
                localSessionParamsMap.putIfAbsent(username, intervalInMinutes);
            }

            Date expiredAt = getExpirationTime(intervalInMinutes);

            boolean updated = apiAuthTokenDao.updateExpiration(tokenId, expiredAt);
            log.debug("Expiration period for session: {} {}.", token, updated ? "have been changed" : "have not been changed");
        } else {
            throw new TokenException("Token not found", ErrorCode.TOKEN_NOT_FOUND);
        }
    }

    private DefaultClaims getClaims(String token) {
        DefaultClaims claims;
        try {
            claims = (DefaultClaims) Jwts.parser()
                    .setSigningKey(tokenKey)
                    .parseClaimsJws(token)
                    .getBody();

            claims.forEach((key, value) -> log.info(key + " :: " + value + " :: " + value.getClass()));
        } catch (Exception ex) {
            throw new TokenException("Token corrupted", ErrorCode.INVALID_AUTHENTICATION_TOKEN);
        }
        if (!(claims.containsKey("token_id") && claims.containsKey("username") && claims.containsKey("value"))) {
            throw new TokenException("Invalid token", ErrorCode.INVALID_AUTHENTICATION_TOKEN);
        }
        return claims;
    }

    private Date getExpirationTime(long minutes) {
        return Date.from(LocalDateTime.now().plusMinutes(minutes).atZone(ZoneId.systemDefault()).toInstant());
    }

    private String getAvatarPathPrefix(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() +
                ":" + request.getServerPort() + "/rest";
    }
}