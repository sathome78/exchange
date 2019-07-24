package me.exrates.chart.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.log4j.Log4j2;
import me.exrates.chart.CandleDataProcessingService;
import me.exrates.model.dto.CandleDto;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.service.api.ChartApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
public class CandleDataProcessingServiceImpl implements CandleDataProcessingService {

    private final ChartApi chartApi;

    private LoadingCache<String, List<CandleDto>> requestCache;

    @Autowired
    public CandleDataProcessingServiceImpl(ChartApi chartApi) {
        this.chartApi = chartApi;
        this.requestCache = CacheBuilder.newBuilder()
                .refreshAfterWrite(20, TimeUnit.SECONDS)
                .build(new CacheLoader<String, List<CandleDto>>() {
                    @Override
                    public List<CandleDto> load(String key) {
                        return chartApi.getDefaultCandlesDataByRange(key);
                    }
                });
    }

    @Override
    public List<CandleDto> getData(String pairName, LocalDateTime fromDate, LocalDateTime toDate, BackDealInterval interval) {
        if (Objects.equals(ChartApi.DEFAULT_CURRENCY_PAIR, pairName)
                && Objects.equals(ChartApi.DEFAULT_INTERVAL, interval)) {
            try {
                return requestCache.get(pairName);
            } catch (ExecutionException ex) {
                log.error("Failed to load candles data by key: {} from cache", pairName, ex);
            }
        }
        return chartApi.getCandlesDataByRange(pairName, fromDate, toDate, interval);
    }
}