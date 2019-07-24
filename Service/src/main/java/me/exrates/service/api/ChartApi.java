package me.exrates.service.api;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import me.exrates.model.dto.CandleDto;
import me.exrates.model.enums.IntervalType;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.service.exception.ChartApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

@PropertySource(value = {"classpath:/external-apis.properties"})
@Slf4j
@Component
public class ChartApi {

    private static final LocalDateTime DEFAULT_TO_DATE;

    static {
        DEFAULT_TO_DATE = LocalDateTime.now();
    }

    private static final LocalDateTime DEFAULT_FROM_DATE = DEFAULT_TO_DATE.minus(5 * 300, ChronoUnit.MINUTES);

    public static final String DEFAULT_CURRENCY_PAIR = "BTC/USD";
    public static final BackDealInterval DEFAULT_INTERVAL = new BackDealInterval(5, IntervalType.MINUTE);

    private final String url;

    private final RestTemplate restTemplate;

    @Autowired
    public ChartApi(@Value("${api.chart.url}") String url) {
        this.url = url;
        this.restTemplate = new RestTemplate();
    }

    public List<CandleDto> getCandlesDataByRange(String currencyPair,
                                                 LocalDateTime from,
                                                 LocalDateTime to,
                                                 BackDealInterval interval) {
        final CandleDataRequest request = CandleDataRequest.builder()
                .currencyPair(currencyPair)
                .from(from)
                .to(to)
                .intervalType(interval.getIntervalType())
                .intervalValue(interval.getIntervalValue())
                .build();
        HttpEntity<CandleDataRequest> requestEntity = new HttpEntity<>(request);

        ResponseEntity<List<CandleDto>> responseEntity;
        try {
            responseEntity = restTemplate.exchange(url + "/range", HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<CandleDto>>() {
            });
            if (responseEntity.getStatusCodeValue() != 200) {
                throw new ChartApiException("Chart server is not available");
            }
        } catch (Exception ex) {
            log.warn("Chart service did not return valid data: server not available");
            return Collections.emptyList();
        }
        return responseEntity.getBody();
    }

    public List<CandleDto> getDefaultCandlesDataByRange(String currencyPair) {
        return this.getCandlesDataByRange(currencyPair, DEFAULT_FROM_DATE, DEFAULT_TO_DATE, DEFAULT_INTERVAL);
    }

    public CandleDto getLastCandleData(String currencyPair, BackDealInterval interval) {
        final CandleDataRequest request = CandleDataRequest.builder()
                .currencyPair(currencyPair)
                .intervalType(interval.getIntervalType())
                .intervalValue(interval.getIntervalValue())
                .build();
        HttpEntity<CandleDataRequest> requestEntity = new HttpEntity<>(request);

        ResponseEntity<CandleDto> responseEntity;
        try {
            responseEntity = restTemplate.exchange(url + "/last", HttpMethod.GET, requestEntity, CandleDto.class);
            if (responseEntity.getStatusCodeValue() != 200) {
                throw new ChartApiException("Chart server is not available");
            }
        } catch (Exception ex) {
            log.warn("Chart service did not return valid data: server not available");
            return null;
        }
        return responseEntity.getBody();
    }

    @Builder(builderClassName = "Builder")
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class CandleDataRequest {

        String currencyPair;
        LocalDateTime from;
        LocalDateTime to;
        IntervalType intervalType;
        int intervalValue;
    }
}