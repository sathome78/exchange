package me.exrates.service.api;

import lombok.extern.slf4j.Slf4j;
import me.exrates.model.dto.CandleDto;
import me.exrates.model.dto.CoinmarketcapApiDto;
import me.exrates.service.exception.ChartApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.nonNull;

@PropertySource(value = {"classpath:/external-apis.properties"})
@Slf4j
@Component
public class ChartApi {

    private final String url;
    private final String coinmarketcapUrl;

    private final RestTemplate restTemplate;

    @Autowired
    public ChartApi(@Value("${api.chart.url}") String url,
                    @Value("${api.chart.coinmarketcap-url}") String coinmarketcapUrl) {
        this.url = url;
        this.coinmarketcapUrl = coinmarketcapUrl;
        this.restTemplate = new RestTemplate(getClientHttpRequestFactory());
    }

    public List<CandleDto> getCandlesDataByRange(String pairName,
                                                 Long from,
                                                 Long to,
                                                 String resolution) {
        final String queryParams = buildQueryParams(pairName, from, to, resolution);

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

    public CandleDto getLastCandleData(String pairName, String resolution) {
        final String queryParams = buildQueryParams(pairName, null, null, resolution);

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
                                                     Long date,
                                                     String resolution) {
        final String queryParams = buildQueryParams(pairName, null, date, resolution);

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

    public List<CoinmarketcapApiDto> getCoinmarketcapData(String pairName, String resolution) {
        final String queryParams = buildQueryParams(pairName, null, null, resolution);

        ResponseEntity<CoinmarketcapApiDto[]> responseEntity;
        try {
            responseEntity = restTemplate.getForEntity(String.format("%s?%s", coinmarketcapUrl, queryParams), CoinmarketcapApiDto[].class);
            if (responseEntity.getStatusCodeValue() != 200) {
                throw new ChartApiException("Chart server is not available");
            }
        } catch (Exception ex) {
            log.warn("Chart service did not return valid data: server not available");
            return Collections.emptyList();
        }
        return Arrays.asList(responseEntity.getBody());
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectTimeout(10000);
        httpRequestFactory.setConnectionRequestTimeout(10000);
        httpRequestFactory.setReadTimeout(10000);

        return httpRequestFactory;
    }

    private String buildQueryParams(String pairName, Long from, Long to, String resolution) {
        List<String> params = new ArrayList<>();

        if (nonNull(pairName)) {
            params.add(String.format("currencyPair=%s", pairName));
        }
        if (nonNull(from)) {
            params.add(String.format("from=%s", String.valueOf(from)));
        }
        if (nonNull(to)) {
            params.add(String.format("to=%s", String.valueOf(to)));
        }
        if (nonNull(resolution)) {
            params.add(String.format("resolution=%s", resolution));
        }
        return String.join("&", params);
    }
}