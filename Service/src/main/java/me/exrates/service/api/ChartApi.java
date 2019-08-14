package me.exrates.service.api;

import lombok.extern.slf4j.Slf4j;
import me.exrates.model.dto.CandleDto;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.service.exception.ChartApiException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.nonNull;

@PropertySource(value = {"classpath:/external-apis.properties"})
@Slf4j
@Component
public class ChartApi {

    private final String url;

    private final RestTemplate restTemplate;

    @Autowired
    public ChartApi(@Value("${api.chart.url}") String url) {
        this.url = url;
        this.restTemplate = new RestTemplate();
    }

    public List<CandleDto> getCandlesDataByRange(String pairName,
                                                 LocalDateTime from,
                                                 LocalDateTime to,
                                                 BackDealInterval interval) {
        final String queryParams = buildQueryParams(pairName, from, to, interval);

        ResponseEntity<CandleDto[]> responseEntity;
        try {
            responseEntity = restTemplate.getForEntity(String.format("%s/range?%s", url, queryParams), CandleDto[].class);
            if (responseEntity.getStatusCodeValue() != 200) {
                throw new ChartApiException("Chart server is not available");
            }
        } catch (Exception ex) {
            log.warn("Chart service did not return valid data: server not available");
            return Collections.emptyList();
        }
        return Arrays.asList(responseEntity.getBody());
    }

    public CandleDto getLastCandleData(String pairName, BackDealInterval interval) {
        final String queryParams = buildQueryParams(pairName, null, null, interval);

        ResponseEntity<CandleDto> responseEntity;
        try {
            responseEntity = restTemplate.getForEntity(String.format("%s/last?%s", url, queryParams), CandleDto.class);
            if (responseEntity.getStatusCodeValue() != 200) {
                throw new ChartApiException("Chart server is not available");
            }
        } catch (Exception ex) {
            log.warn("Chart service did not return valid data: server not available");
            return null;
        }
        return responseEntity.getBody();
    }

    public LocalDateTime getLastCandleTimeBeforeDate(String pairName,
                                                     LocalDateTime date,
                                                     BackDealInterval interval) {
        final String queryParams = buildQueryParams(pairName, null, date, interval);

        ResponseEntity<LocalDateTime> responseEntity;
        try {
            responseEntity = restTemplate.getForEntity(String.format("%s/last-date?%s", url, queryParams), LocalDateTime.class);
            if (responseEntity.getStatusCodeValue() != 200) {
                throw new ChartApiException("Chart server is not available");
            }
        } catch (Exception ex) {
            log.warn("Chart service did not return valid data: server not available");
            return null;
        }
        return responseEntity.getBody();
    }

    private String buildQueryParams(String pairName, LocalDateTime from, LocalDateTime to, BackDealInterval interval) {
        String pairParam = String.format("currencyPair=%s", pairName);
        String fromParam = nonNull(from) ? String.format("from=%s", from.format(DateTimeFormatter.ISO_DATE_TIME)) : StringUtils.EMPTY;
        String toParam = nonNull(to) ? String.format("to=%s", to.format(DateTimeFormatter.ISO_DATE_TIME)) : StringUtils.EMPTY;
        String intervalValueParam = String.format("intervalValue=%s", interval.getIntervalValue().toString());
        String intervalTypeParam = String.format("intervalType=%s", interval.getIntervalType().name());

        return String.join("&", pairParam, fromParam, toParam, intervalValueParam, intervalTypeParam);
    }
}