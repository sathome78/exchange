package me.exrates.service.api;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import me.exrates.model.dto.api.RateDto;
import me.exrates.service.CurrencyService;
import me.exrates.service.exception.ExchangeApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@EnableScheduling
@PropertySource(value = {"classpath:/external-apis.properties"})
@Slf4j
@Component
public class ExchangeApi {

    private static final String ALL_RATES_CACHE = "all-rates-cache";

    private final String url;

    private final CurrencyService currencyService;
    private final RestTemplate restTemplate;

    private final Cache<String, List<RateDto>> ratesCache;

    @Autowired
    public ExchangeApi(@Value("${api.exchange.url}") String url,
                       @Value("${api.exchange.username}") String username,
                       @Value("${api.exchange.password}") String password,
                       CurrencyService currencyService) {
        this.url = url;
        this.currencyService = currencyService;
        this.restTemplate = new RestTemplate();
        this.restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(username, password));
        this.ratesCache = CacheBuilder.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build();
    }

    @Scheduled(initialDelay = 0, fixedDelay = 30 * 60 * 1_000)
    public void updateExchangeCurrencyRates() {
        currencyService.updateCurrencyExchangeRates(getRatesFromApi());
    }

    public Map<String, RateDto> getRates() {
        try {
            return ratesCache.get(ALL_RATES_CACHE, currencyService::getCurrencyRates).stream()
                    .collect(toMap(RateDto::getCurrencyName, Function.identity()));
        } catch (ExecutionException ex) {
            return Collections.emptyMap();
        }
    }

    private List<RateDto> getRatesFromApi() {
        ResponseEntity<ExchangeData> responseEntity;
        try {
            responseEntity = restTemplate.getForEntity(url + "/all", ExchangeData.class);
            if (responseEntity.getStatusCodeValue() != 200) {
                throw new ExchangeApiException("Exchange server is not available");
            }
        } catch (Exception ex) {
            log.warn("Exchange service did not return valid data: server not available");
            return Collections.emptyList();
        }
        ExchangeData body = responseEntity.getBody();
        return nonNull(body) && nonNull(body.rates) && !body.rates.isEmpty()
                ? body.rates.entrySet().stream()
                .map(entry -> RateDto.builder()
                        .currencyName(entry.getKey())
                        .usdRate(BigDecimal.valueOf(entry.getValue().usdRate))
                        .btcRate(BigDecimal.valueOf(entry.getValue().btcRate))
                        .build())
                .collect(toList())
                : Collections.emptyList();
    }

    public Map<String, BigDecimal> getRatesByCurrencyType(String type) {
        ResponseEntity<ExchangeData> responseEntity;
        try {
            responseEntity = restTemplate.getForEntity(String.format(url + "/type/%s", type), ExchangeData.class);
            if (responseEntity.getStatusCodeValue() != 200) {
                throw new ExchangeApiException("Exchange server is not available");
            }
        } catch (Exception ex) {
            log.warn("Exchange service did not return valid data: server not available");
            return Collections.emptyMap();
        }
        ExchangeData body = responseEntity.getBody();
        return nonNull(body) && nonNull(body.rates) && !body.rates.isEmpty()
                ? body.rates.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> BigDecimal.valueOf(entry.getValue().usdRate)))
                : Collections.emptyMap();
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class ExchangeData {

        Map<String, Rates> rates = Maps.newTreeMap();

        @JsonAnySetter
        void setRates(String key, Rates value) {
            rates.put(key, value);
        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    private static class Rates {

        @JsonProperty("usd_rate")
        double usdRate;
        @JsonProperty("btc_rate")
        double btcRate;
    }
}
