package me.exrates.service.chart.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.chart.CandleDto;
import me.exrates.service.api.ChartApi;
import me.exrates.service.chart.CandleDataProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public List<CandleDto> getData(String pairName, Long from, Long to, String resolution) {
        return chartApi.getCandlesDataByRange(pairName, from, to, resolution);
    }
}