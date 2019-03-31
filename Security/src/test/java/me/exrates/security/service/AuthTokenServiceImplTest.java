package me.exrates.security.service;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import me.exrates.dao.ApiAuthTokenDao;
import me.exrates.model.ApiAuthToken;
import me.exrates.model.dto.mobileApiDto.AuthTokenDto;
import me.exrates.security.exception.TokenException;
import me.exrates.security.service.impl.AuthTokenServiceImpl;
import me.exrates.service.ReferralService;
import me.exrates.service.SessionParamsService;
import me.exrates.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AuthTokenServiceImplTest.InnerConfig.class})
public class AuthTokenServiceImplTest {

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    private ApiAuthTokenDao apiAuthTokenDao;

    @Test
    public void getUserByToken_whenTokenIsOk() {
        LocalDateTime future = LocalDateTime.now().plusMinutes(10);
        when(apiAuthTokenDao.retrieveTokenById(anyLong())).thenReturn(Optional.of(createToken(future)));
        UserDetails user = authTokenService.getUserByToken(getToken(future).orElseThrow(RuntimeException::new).getToken());
        assertNull(user);
    }

    @Test
    public void getUserByToken_whenTokenExpired() {
        LocalDateTime past = LocalDateTime.now().minusMinutes(10);
        when(apiAuthTokenDao.retrieveTokenById(anyLong())).thenReturn(Optional.of(createToken(past)));
        try {
            authTokenService.getUserByToken(getToken(past).orElseThrow(RuntimeException::new).getToken());
            fail();
        } catch (TokenException e) {
            e.printStackTrace();
        }
    }

    private ApiAuthToken createToken(LocalDateTime when) {
        return ApiAuthToken
                .builder()
                .id(123L)
                .username("test@email.com")
                .value("1613089c-3698-11e9-b210-d663bd873d93")
                .lastRequest(when)
                .build();
    }

    private Optional<AuthTokenDto> getToken(LocalDateTime when) {
        ApiAuthToken token = createToken(when);
        Map<String, Object> tokenData = new HashMap<>();
        tokenData.put("token_id", token.getId());
        tokenData.put("username", token.getUsername());
        tokenData.put("value", token.getValue());
        JwtBuilder jwtBuilder = Jwts.builder();
        tokenData.put("expiration", when.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        jwtBuilder.setClaims(tokenData);
        AuthTokenDto authTokenDto = new AuthTokenDto(jwtBuilder.signWith(SignatureAlgorithm.HS512, "${token.key}").compact());
        return Optional.of(authTokenDto);
    }

    @Configuration
    static class InnerConfig {

        @Bean
        public UserService userService() {
            return Mockito.mock(UserService.class);
        }

        @Bean
        public ApiAuthTokenDao apiAuthTokenDao() {
            return Mockito.mock(ApiAuthTokenDao.class);
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return Mockito.mock(PasswordEncoder.class);
        }

        @Bean("userDetailsService")
        public UserDetailsService userDetailsService() {
            return Mockito.mock(UserDetailsService.class);
        }

        @Bean
        public SessionParamsService sessionParamsService() {
            return Mockito.mock(SessionParamsService.class);
        }

        @Bean
        public ReferralService referralService() {
            return Mockito.mock(ReferralService.class);
        }

        @Bean
        public AuthTokenService authTokenService() {
            return new AuthTokenServiceImpl(passwordEncoder(), apiAuthTokenDao(), userDetailsService(),
                    sessionParamsService(), userService(), referralService());
        }
    }
}