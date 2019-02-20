package me.exrates.security.service;


import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.ApiAuthTokenDao;
import me.exrates.model.ApiAuthToken;
import me.exrates.model.SessionParams;
import me.exrates.model.dto.mobileApiDto.AuthTokenDto;
import me.exrates.model.dto.mobileApiDto.UserAuthenticationDto;
import me.exrates.security.exception.IncorrectPasswordException;
import me.exrates.security.exception.MissingCredentialException;
import me.exrates.security.exception.TokenException;
import me.exrates.service.SessionParamsService;
import me.exrates.service.exception.api.ErrorCode;
import me.exrates.service.util.IpUtils;
import me.exrates.service.util.RestApiUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by OLEG on 23.08.2016.
 */
@Log4j2
@Service
@PropertySource(value = {"classpath:/mobile.properties", "classpath:/angular.properties"})
public class AuthTokenServiceImpl implements AuthTokenService {
    private static final Logger logger = LogManager.getLogger("mobileAPI");
    private static final int PIN_WAIT_MINUTES = 20;
    private Map<String, LocalDateTime> usersForPincheck = new ConcurrentHashMap<>();

    @Value("${token.key}")
    private String TOKEN_KEY;

    @Value("${token.duration}")
    private long TOKEN_DURATION_TIME;

    @Value("${token.max.duration}")
    private long TOKEN_MAX_DURATION_TIME;

    @Value("${pass.encode.key}")
    private String PASS_ENCODE_KEY;

    @Value("${dev.mode}")
    private boolean DEV_MODE;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ApiAuthTokenDao apiAuthTokenDao;

    @Autowired
    @Qualifier("userDetailsService")
    private UserDetailsService userDetailsService;

    @Autowired
    private SessionParamsService sessionParamsService;


    @Override
    public Optional<AuthTokenDto> retrieveToken(String username, String encodedPassword) {
        if (username == null || encodedPassword == null) {
            throw new MissingCredentialException("Credentials missing");
        }
        String password = RestApiUtils.decodePassword(encodedPassword, PASS_ENCODE_KEY);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (passwordEncoder.matches(password, userDetails.getPassword())) {
            ApiAuthToken token = createAuthToken(userDetails.getUsername());
            Map<String, Object> tokenData = new HashMap<>();
            tokenData.put("token_id", token.getId());
            tokenData.put("username", token.getUsername());
            tokenData.put("value", token.getValue());
            JwtBuilder jwtBuilder = Jwts.builder();
            Date expiration = Date.from(LocalDateTime.now().plusSeconds(TOKEN_MAX_DURATION_TIME).atZone(ZoneId.systemDefault()).toInstant());
            tokenData.put("expiration", expiration.getTime());
            jwtBuilder.setClaims(tokenData);
            AuthTokenDto authTokenDto = new AuthTokenDto(jwtBuilder.signWith(SignatureAlgorithm.HS512, TOKEN_KEY).compact());
            return Optional.of(authTokenDto);
        } else {
            throw new IncorrectPasswordException("Incorrect password");
        }

    }

    private ApiAuthToken createAuthToken(String username) {
        ApiAuthToken token = new ApiAuthToken();
        token.setUsername(username);
        token.setValue(UUID.randomUUID().toString());
        Long id = apiAuthTokenDao.createToken(token);
        token.setId(id);
        return token;

    }

    @Override
    @Transactional(rollbackFor = Exception.class, noRollbackFor = TokenException.class)
    public UserDetails getUserByToken(String token) {
        if (token == null) {
            throw new TokenException("No authentication token header found", ErrorCode.MISSING_AUTHENTICATION_TOKEN);
        }
        DefaultClaims claims;
        try {
            claims = (DefaultClaims) Jwts.parser().setSigningKey(TOKEN_KEY).parseClaimsJws(token).getBody();
        } catch (Exception ex) {
            throw new TokenException("Token corrupted", ErrorCode.INVALID_AUTHENTICATION_TOKEN);
        }

        if (!(claims.containsKey("token_id") && claims.containsKey("username") && claims.containsKey("value"))) {
            throw new TokenException("Invalid token", ErrorCode.INVALID_AUTHENTICATION_TOKEN);
        }
        Long tokenId = Long.parseLong(String.valueOf(claims.get("token_id")));
        String username = claims.get("username", String.class);
        String value = claims.get("value", String.class);
        Optional<ApiAuthToken> tokenSearchResult = apiAuthTokenDao.retrieveTokenById(tokenId);
        if (tokenSearchResult.isPresent()) {
            ApiAuthToken savedToken = tokenSearchResult.get();
            if (!(username.equals(savedToken.getUsername()) && value.equals(savedToken.getValue()))) {
                throw new TokenException("Invalid token", ErrorCode.INVALID_AUTHENTICATION_TOKEN);
            }
            LocalDateTime expiration = savedToken.getLastRequest().plusSeconds(TOKEN_DURATION_TIME);
            LocalDateTime finalExpiration = new Date(claims.get("expiration", Long.class)).toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDateTime();
            if (expiration.isAfter(LocalDateTime.now()) && finalExpiration.isAfter(LocalDateTime.now())) {
                UserDetails user = userDetailsService.loadUserByUsername(username);
                apiAuthTokenDao.prolongToken(tokenId);
                return user;
            } else {
                apiAuthTokenDao.deleteExpiredToken(tokenId);
                throw new TokenException("Token expired", ErrorCode.EXPIRED_AUTHENTICATION_TOKEN);
            }
        } else {
            throw new TokenException("Token not found", ErrorCode.TOKEN_NOT_FOUND);
        }
    }

    @Override
    @Scheduled(fixedDelay = 12L * 60L * 60L * 1000L, initialDelay = 60000L)
    public void deleteExpiredTokens() {
        int deletedQuantity = apiAuthTokenDao.deleteAllExpired(TOKEN_DURATION_TIME);
        logger.info(String.format("%d expired tokens deleted", deletedQuantity));
        usersForPincheck.values().removeIf(v -> v.plusMinutes(PIN_WAIT_MINUTES).isBefore(LocalDateTime.now()));
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
        if (token == null) {
            return false;
        }
        DefaultClaims claims;
        try {
            claims = (DefaultClaims) Jwts.parser().setSigningKey(TOKEN_KEY).parseClaimsJws(token).getBody();
            return claims.getExpiration().before(new Date());
        } catch (Exception ex) {
            throw new TokenException("Token corrupted", ErrorCode.INVALID_AUTHENTICATION_TOKEN);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class, noRollbackFor = TokenException.class)
    public UserDetails getUserByToken(String token, String currentIp) {
        if (token == null) {
            throw new TokenException("No authentication token header found", ErrorCode.MISSING_AUTHENTICATION_TOKEN);
        }
        DefaultClaims claims;
        try {
            claims = (DefaultClaims) Jwts.parser().setSigningKey(TOKEN_KEY).parseClaimsJws(token).getBody();
            claims.forEach((key, value) -> logger.info(key + " :: " + value + " :: " + value.getClass()));
        } catch (Exception ex) {
            throw new TokenException("Token corrupted", ErrorCode.INVALID_AUTHENTICATION_TOKEN);
        }

        if (!(claims.containsKey("token_id") && claims.containsKey("username") && claims.containsKey("value"))) {
            throw new TokenException("Invalid token", ErrorCode.INVALID_AUTHENTICATION_TOKEN);
        }
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
            /*temporary disabled check for ip, need testing*/
            log.debug("request ip {}", ip);
            /*if (ip != null && !ip.equals(currentIp)) {
                throw new TokenException("Invalid token", ErrorCode.INVALID_AUTHENTICATION_TOKEN);
            }*/
            LocalDateTime expiration = savedToken.getLastRequest().plusSeconds(TOKEN_DURATION_TIME);
            LocalDateTime finalExpiration = new Date(claims.get("expiration", Long.class)).toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDateTime();
            if (finalExpiration.isAfter(LocalDateTime.now())) {
                UserDetails user = userDetailsService.loadUserByUsername(username);
                apiAuthTokenDao.prolongToken(tokenId);
                return user;
            } else {
                apiAuthTokenDao.deleteExpiredToken(tokenId);
                throw new TokenException("Token expired", ErrorCode.EXPIRED_AUTHENTICATION_TOKEN);
            }
        } else {
            throw new TokenException("Token not found", ErrorCode.TOKEN_NOT_FOUND);
        }
    }

    private Optional<AuthTokenDto> prepareAuthTokenNg(String username) {
        ApiAuthToken token = createAuthToken(username);
        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("token_id", token.getId());
        tokenData.put("username", token.getUsername());
        tokenData.put("value", token.getValue());
        JwtBuilder jwtBuilder = Jwts.builder();
        Optional<SessionParams> params = Optional.of(sessionParamsService.getByEmailOrDefault(username));
        Date expiration = params
                .map(p -> getExpirationTime(p.getSessionTimeMinutes()))
                .orElseGet(() -> getExpirationTime(TOKEN_MAX_DURATION_TIME / 60));
        tokenData.put("expiration", expiration.getTime());
        jwtBuilder.setClaims(tokenData);
        AuthTokenDto authTokenDto = new AuthTokenDto(jwtBuilder.signWith(SignatureAlgorithm.HS512, TOKEN_KEY).compact());
        usersForPincheck.remove(token.getUsername());
        return Optional.of(authTokenDto);
    }

    private Date getExpirationTime(long minutes) {
        return Date.from(LocalDateTime.now().plusMinutes(minutes).atZone(ZoneId.systemDefault()).toInstant());
    }


}
