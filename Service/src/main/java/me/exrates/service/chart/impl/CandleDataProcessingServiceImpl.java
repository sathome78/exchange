package me.exrates.service.chart.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.CandleDto;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.service.api.ChartApi;
import me.exrates.service.chart.CandleDataProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@Service
public class CandleDataProcessingServiceImpl implements CandleDataProcessingService {

    private final ChartApi chartApi;

    @Autowired
    public CandleDataProcessingServiceImpl(ChartApi chartApi) {
        this.chartApi = chartApi;
    }

    @Override
    public List<CandleDto> getData(String pairName, LocalDateTime fromDate, LocalDateTime toDate, String resolution) {
        return chartApi.getCandlesDataByRange(pairName, fromDate, toDate, resolution);
    }

    @Override
    public LocalDateTime getLastCandleTimeBeforeDate(String pairName, LocalDateTime date, String resolution) {
        return chartApi.getLastCandleTimeBeforeDate(pairName, date, resolution);
    }
}