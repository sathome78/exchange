package me.exrates.service.coinpay;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;
import me.exrates.service.exception.CoinpayException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

import static java.util.Objects.nonNull;

@Log4j2
@PropertySource("classpath:/merchants/coinpay.properties")
@Service
public class CoinpayApiImpl implements CoinpayApi {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER_TOKEN = "Bearer %s";

    private final String url;

    private final RestTemplate restTemplate;

    @Autowired
    public CoinpayApiImpl(@Value("${coinpay.url}") String url) {
        this.url = url;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public boolean createUser(String email, String password, String username, String referralId) {
        CreateUserRequest.Builder builder = CreateUserRequest.builder()
                .email(email)
                .password(password)
                .username(username);

        if (nonNull(referralId)) {
            builder.referralId(referralId);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<CreateUserRequest> requestEntity = new HttpEntity<>(builder.build(), headers);

        ResponseEntity<CreateUserResponse> responseEntity;
        try {
            responseEntity = restTemplate.postForEntity(url + "/user/create", requestEntity, CreateUserResponse.class);
            if (responseEntity.getStatusCodeValue() != 201) {
                throw new CoinpayException("COINPAY - User creation issue");
            }
        } catch (Exception ex) {
            log.warn("COINPAY - User creation issue");
            return false;
        }
        final CreateUserResponse response = responseEntity.getBody();

        return response.email.equals(email) && response.username.equals(username);
    }

    @Override
    public String authorizeUser(String email, String password) {
        AuthorizeUserRequest.Builder builder = AuthorizeUserRequest.builder()
                .email(email)
                .password(password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<AuthorizeUserRequest> requestEntity = new HttpEntity<>(builder.build(), headers);

        ResponseEntity<AuthorizeUserResponse> responseEntity;
        try {
            responseEntity = restTemplate.postForEntity(url + "/user/obtain_token", requestEntity, AuthorizeUserResponse.class);
            if (responseEntity.getStatusCodeValue() != 200) {
                throw new CoinpayException("COINPAY - User authorization issue");
            }
        } catch (Exception ex) {
            log.warn("COINPAY - User authorization issue");
            return null;
        }
        final AuthorizeUserResponse response = responseEntity.getBody();

        log.debug("User: {} successfully authorized", response.username);

        return response.token;
    }

    @Override
    public String refreshToken(String oldToken) {
        UpdateTokenRequest.Builder builder = UpdateTokenRequest.builder()
                .token(oldToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<UpdateTokenRequest> requestEntity = new HttpEntity<>(builder.build(), headers);

        ResponseEntity<UpdateTokenResponse> responseEntity;
        try {
            responseEntity = restTemplate.postForEntity(url + "/user/refresh_token", requestEntity, UpdateTokenResponse.class);
            if (responseEntity.getStatusCodeValue() != 200) {
                throw new CoinpayException("COINPAY - Update token issue");
            }
        } catch (Exception ex) {
            log.warn("COINPAY - Update token issue");
            return null;
        }
        return responseEntity.getBody().token;
    }

    @Override
    public BalanceResponse getBalancesAndWallets(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set(AUTHORIZATION, String.format(BEARER_TOKEN, token));

        HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<BalanceResponse> responseEntity;
        try {
            responseEntity = restTemplate.exchange(url + "/user/balance", HttpMethod.GET, requestEntity, BalanceResponse.class);
            if (responseEntity.getStatusCodeValue() != 200) {
                throw new CoinpayException("COINPAY - Get balance and wallets issue");
            }
        } catch (Exception ex) {
            log.warn("COINPAY - Get balance and wallets issue");
            return null;
        }
        return responseEntity.getBody();
    }

    @Builder(builderClassName = "Builder")
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class CreateUserRequest {

        String email;
        String password;
        String username;
        @JsonProperty("referral_id")
        String referralId;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class CreateUserResponse {

        String email;
        String username;
    }

    @Builder(builderClassName = "Builder")
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class AuthorizeUserRequest {

        String email;
        String password;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class AuthorizeUserResponse {

        String username;
        String token;
    }

    @Builder(builderClassName = "Builder")
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class UpdateTokenRequest {

        String token;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class UpdateTokenResponse {

        String token;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class BalanceResponse {

        Map<String, Currency> balance = Maps.newTreeMap();
        Map<String, Wallet> wallets = Maps.newTreeMap();

        @JsonSetter
        void setBalance(String key, Currency value) {
            balance.put(key, value);
        }

        @JsonSetter
        void setWallets(String key, Wallet value) {
            wallets.put(key, value);
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class Currency {

        String currency;
        Map<String, Balance> currencies = Maps.newTreeMap();

        @JsonAnySetter
        void setCurrencies(String key, Balance value) {
            currencies.put(key, value);
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class Balance {

        BigDecimal total;
        BigDecimal reserved;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class Wallet {

        String address;
        String qr;
        String qr_file_data;
    }

    //test
//    public static void main(String[] args) {
//        CoinpayApiImpl coinpayApi = new CoinpayApiImpl("https://coinpay.org.ua/api/v1/");
//
//        String token = coinpayApi.authorizeUser("o.kostiukevych@gmail.com", "123qwe123QWE");
//
//        BalanceResponse balancesAndWallets = coinpayApi.getBalancesAndWallets(token);
//
//        String newToken = coinpayApi.refreshToken(token);
//    }
}