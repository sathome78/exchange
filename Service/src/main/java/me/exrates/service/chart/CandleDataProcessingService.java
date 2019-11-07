package me.exrates.service.chart;

import me.exrates.model.chart.CandleDto;

import java.util.List;

public interface CandleDataProcessingService {

    List<CandleDto> getData(String pairName, Long from, Long to, String resolution);
}