package me.exrates.service.coinpay;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.merchants.coinpay.CoinPayCreateWithdrawDto;
import me.exrates.model.dto.merchants.coinpay.CoinPayResponseDepositDto;
import me.exrates.model.dto.merchants.coinpay.CoinPayResponseOrderDetailDto;
import me.exrates.model.dto.merchants.coinpay.CoinPayWithdrawRequestDto;
import me.exrates.service.exception.CoinpayException;
import org.apache.commons.lang.StringUtils;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Collections;
import java.util.Map;

@Log4j2(topic = "coin_pay_log")
@PropertySource("classpath:/merchants/coinpay.properties")
@Service
public class CoinpayApiImpl implements CoinpayApi {

    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER_TOKEN = "Bearer %s";

    private final String url;
    private final String email;
    private final String password;

    private final RestTemplate restTemplate;

    @Autowired
    public CoinpayApiImpl(@Value("${coinpay.url}") String url,
                          @Value("${coinpay.email}") String email,
                          @Value("${coinpay.password}") String password) {
        this.url = url;
        this.email = email;
        this.password = password;
        this.restTemplate = new RestTemplate();
    }

    @Override
    public String authorizeUser() {
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

    @Override
    public CoinPayWithdrawRequestDto createWithdrawRequest(String token, CoinPayCreateWithdrawDto request) {
        log.info("Create request withdraw to coin pay \n{}", toJson(request));
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(AUTHORIZATION, String.format(BEARER_TOKEN, token));

        HttpEntity<Object> requestEntity = new HttpEntity<>(request, headers);

        ResponseEntity<CoinPayWithdrawRequestDto> responseEntity;
        try {
            responseEntity = restTemplate.exchange(url + "/api/v1/withdrawal", HttpMethod.POST, requestEntity,
                    CoinPayWithdrawRequestDto.class);
            if (responseEntity.getStatusCodeValue() != 200) {
                throw new CoinpayException("COINPAY - Error while creating withdraw request");
            }
        } catch (Exception ex) {
            log.error("COINPAY - Error response while create withdraw request");
            throw new CoinpayException("COINPAY - Error while creating withdraw request");
        }

        if (!responseEntity.getBody().getStatus().equalsIgnoreCase("success")) {
            log.error("COINPAY - Response from merchant {}", responseEntity.getBody().getStatus());
            throw new CoinpayException("COINPAY - Error while creating withdraw request");
        }
        log.info("Response from withdraw \n{} ", toJson(responseEntity.getBody()));
        return responseEntity.getBody();
    }

    @Override
    public String checkOrderById(String token, String orderId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set(AUTHORIZATION, String.format(BEARER_TOKEN, token));

        HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

        String finalUrl = url + "api/v1/orders/details";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(finalUrl);
        builder.queryParam("order_id", orderId);
        URI uri = builder.build(true).toUri();
        log.info("Send request check order to coin pay {}", uri);
        ResponseEntity<CoinPayResponseOrderDetailDto> responseEntity;
        try {
            responseEntity = restTemplate.exchange(uri, HttpMethod.GET, requestEntity,
                    CoinPayResponseOrderDetailDto.class);
            if (responseEntity.getStatusCodeValue() != 200) {
                throw new CoinpayException("COINPAY - Error while creating withdraw request");
            }
        } catch (Exception ex) {
            log.error("COINPAY - Error response while create withdraw request");
            throw new CoinpayException("COINPAY - Error while creating withdraw request");
        }
        log.info("Response from check order \n {}", toJson(requestEntity.getBody()));
        return responseEntity.getBody().getStatus();
    }

    @Override
    public CoinPayResponseDepositDto createDeposit(String token, String amount, String currency, String callbackUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set(AUTHORIZATION, String.format(BEARER_TOKEN, token));

        HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

        String finalUrl = url + "api/v1/deposit/address";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(finalUrl);
        builder.queryParam("currency", currency);
        builder.queryParam("amount", amount);
        builder.queryParam("callback_url", callbackUrl);
        URI uri = builder.build(true).toUri();
        log.info("Send request deposit to coin pay {}", uri);
        ResponseEntity<CoinPayResponseDepositDto> responseEntity;
        try {
            responseEntity = restTemplate.exchange(uri, HttpMethod.GET, requestEntity,
                    CoinPayResponseDepositDto.class);
            if (responseEntity.getStatusCodeValue() != 200) {
                throw new CoinpayException("COINPAY - Error while creating withdraw request");
            }
        } catch (Exception ex) {
            log.error("COINPAY - Error response while create withdraw request");
            throw new CoinpayException("COINPAY - Error while creating withdraw request");
        }
        if (!responseEntity.getBody().getStatus().equalsIgnoreCase("success")) {
            log.error("COINPAY - Error status while create deposit request");
            throw new CoinpayException("COINPAY - Error status while create deposit request");
        }
        log.info("Response from deposit: \n{}", toJson(responseEntity.getBody()));
        return responseEntity.getBody();
    }

    @SuppressWarnings("Duplicated")
    private String toJson(Object input) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(input);
        } catch (JsonProcessingException e) {
            log.error("Error create json from object");
            return StringUtils.EMPTY;
        }
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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class Wallet {

        String address;
        String qr;
        String qr_file_data;
    }
}