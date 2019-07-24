package me.exrates.service.api;

import lombok.extern.slf4j.Slf4j;
import me.exrates.model.dto.CandleDto;
import me.exrates.model.enums.IntervalType;
import me.exrates.model.vo.BackDealInterval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
        return null;
    }

    public List<CandleDto> getDefaultCandlesDataByRange(String currencyPair) {
        return this.getCandlesDataByRange(currencyPair, DEFAULT_FROM_DATE, DEFAULT_TO_DATE, DEFAULT_INTERVAL);
    }

    public CandleDto getLastCandleData(String currencyPair,
                                       BackDealInterval interval) {
        return null;
    }
}