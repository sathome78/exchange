package me.exrates.service.api;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import me.exrates.model.dto.api.BalanceDto;
import me.exrates.service.CurrencyService;
import me.exrates.service.exception.WalletsApiException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toMap;

@EnableScheduling
@PropertySource(value = {"classpath:/external-apis.properties", "classpath:/ethereum_contracts.properties"})
@Slf4j
@Component
public class WalletsApi {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

    private static final String ETHEREUM_CONTRACTS_PROPERTY_FILE = "ethereum_contracts.properties";

    private static final String ALL_BALANCES_CACHE = "all-balances-cache";

    private final String url;
    private final Map<String, String> ethereumContractsData;

    private final CurrencyService currencyService;
    private final RestTemplate restTemplate;

    private final Cache<String, List<BalanceDto>> balancesCache;

    @Autowired
    public WalletsApi(@Value("${api.wallets.url}") String url,
                      @Value("${api.wallets.username}") String username,
                      @Value("${api.wallets.password}") String password,
                      CurrencyService currencyService) {
        this.url = url;
        this.currencyService = currencyService;
        this.restTemplate = new RestTemplate();
        this.restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(username, password));
        this.balancesCache = CacheBuilder.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build();

        Properties properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream(ETHEREUM_CONTRACTS_PROPERTY_FILE));
        } catch (Exception ex) {
            log.error("Ethereum contracts data not loaded from [{}]", ETHEREUM_CONTRACTS_PROPERTY_FILE);
        }
        this.ethereumContractsData = new HashMap<>();
        this.ethereumContractsData.putAll(properties.entrySet()
                .stream()
                .collect(toMap(
                        e -> e.getKey().toString(),
                        e -> e.getValue().toString())));
    }

    @Scheduled(initialDelay = 0, fixedDelay = 30 * 60 * 1_000)
    public void updateCurrencyBalances() {
        try {
            currencyService.updateCurrencyBalances(getBalancesFromApi());
        } catch (Exception ex) {
            log.error("--> In processing 'WalletsApi' occurred error", ex);
        }
    }

    public Map<String, BalanceDto> getBalances() {
        try {
            return balancesCache.get(ALL_BALANCES_CACHE, currencyService::getCurrencyBalances).stream()
                    .collect(toMap(BalanceDto::getCurrencyName, Function.identity()));
        } catch (ExecutionException ex) {
            return Collections.emptyMap();
        }
    }

    private List<BalanceDto> getBalancesFromApi() {
        ResponseEntity<WalletsData[]> responseEntity;
        try {
            responseEntity = restTemplate.getForEntity(url, WalletsData[].class);
            if (responseEntity.getStatusCodeValue() != 200) {
                throw new WalletsApiException("Wallets server is not available");
            }
        } catch (Exception ex) {
            log.warn("Wallet service did not return valid data: server not available");
            return Collections.emptyList();
        }
        WalletsData[] body = responseEntity.getBody();
        return nonNull(body) && body.length != 0
                ? Arrays.stream(body)
                .map(wallet -> BalanceDto.builder()
                        .currencyName(wallet.name)
                        .balance(new BigDecimal(wallet.currentAmount.replace(" ", "")))
                        .lastUpdatedAt(StringUtils.isNotEmpty(wallet.date) ? LocalDateTime.parse(wallet.date, FORMATTER) : null)
                        .build())
                .collect(Collectors.toList())
                : Collections.emptyList();
    }

    public Map<String, BigDecimal> getReservedBalances() {
        ResponseEntity<ReservedWalletsData> responseEntity;
        try {
            responseEntity = restTemplate.getForEntity(url + "/balance/reserved", ReservedWalletsData.class);
            if (responseEntity.getStatusCodeValue() != 200) {
                throw new WalletsApiException("Wallets server is not available");
            }
        } catch (Exception ex) {
            log.warn("Wallet service did not return valid data: server not available");
            return Collections.emptyMap();
        }
        ReservedWalletsData body = responseEntity.getBody();
        return nonNull(body) && nonNull(body.balances) && body.balances.size() != 0
                ? body.balances
                : Collections.emptyMap();
    }

    public BigDecimal getBalanceByCurrencyAndWallet(String currencySymbol, String walletAddress) {
        MultiValueMap<String, String> requestParameters = new LinkedMultiValueMap<>();
        requestParameters.add("wallet", walletAddress);

        final String ethereumContract = ethereumContractsData.get(currencySymbol);
        if (nonNull(ethereumContract)) {
            requestParameters.add("eth_contract", ethereumContract);
        }

        UriComponents builder = UriComponentsBuilder
                .fromHttpUrl(String.format("%s/%s/balance", url, currencySymbol))
                .queryParams(requestParameters)
                .build();

        ResponseEntity<WalletBalanceData> responseEntity;
        try {
            responseEntity = restTemplate.getForEntity(builder.toUriString(), WalletBalanceData.class);
            if (responseEntity.getStatusCodeValue() != 200) {
                throw new WalletsApiException("Wallets server is not available");
            }
        } catch (Exception ex) {
            log.warn("Wallet service did not return valid data: wallet address not exist or server not available");
            return null;
        }
        WalletBalanceData body = responseEntity.getBody();

        return nonNull(body) ? BigDecimal.valueOf(body.balance) : null;
    }

    public boolean addReservedWallet(String currencySymbol, String walletAddress) {
        ReservedWalletRequest.Builder builder = ReservedWalletRequest.builder()
                .ticker(currencySymbol)
                .address(walletAddress);

        final String ethereumContract = ethereumContractsData.get(currencySymbol);
        if (nonNull(ethereumContract)) {
            builder.ethContract(ethereumContract);
        }
        HttpEntity<ReservedWalletRequest> requestEntity = new HttpEntity<>(builder.build());

        ResponseEntity<Boolean> responseEntity;
        try {
            responseEntity = restTemplate.postForEntity(url + "/add", requestEntity, Boolean.class);
            if (responseEntity.getStatusCodeValue() != 200) {
                throw new WalletsApiException("Wallets server is not available");
            }
        } catch (Exception ex) {
            log.warn("Wallet service did not return valid data: wallet address not exist or server not available");
            return false;
        }
        return responseEntity.getBody();
    }

    public boolean deleteReservedWallet(String currencySymbol, String walletAddress) {
        ReservedWalletRequest.Builder builder = ReservedWalletRequest.builder()
                .ticker(currencySymbol)
                .address(walletAddress);

        final String ethereumContract = ethereumContractsData.get(currencySymbol);
        if (nonNull(ethereumContract)) {
            builder.ethContract(ethereumContract);
        }
        HttpEntity<ReservedWalletRequest> requestEntity = new HttpEntity<>(builder.build());

        ResponseEntity<Boolean> responseEntity;
        try {
            responseEntity = restTemplate.exchange(url + "/delete", HttpMethod.DELETE, requestEntity, Boolean.class);
            if (responseEntity.getStatusCodeValue() != 200) {
                throw new WalletsApiException("Wallets server is not available");
            }
        } catch (Exception ex) {
            log.warn("Wallet service did not return valid data: wallet address not exist or server not available");
            return false;
        }
        return responseEntity.getBody();
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class WalletsData {

        String name;
        String currentAmount;
        String date;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class ReservedWalletsData {

        Map<String, BigDecimal> balances = Maps.newTreeMap();

        @JsonAnySetter
        void setRates(String key, BigDecimal value) {
            balances.put(key, value);
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class WalletBalanceData {

        double balance;
    }

    @Builder(builderClassName = "Builder")
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class ReservedWalletRequest {

        String ticker;
        String address;
        @JsonProperty("eth_contract")
        String ethContract;
    }
}
