package me.exrates.service;

import me.exrates.model.chart.ChartResolution;
import me.exrates.model.chart.ChartTimeFrame;
import me.exrates.model.dto.ChartTimeFrameDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface StockChartService {
    Optional<ChartTimeFrame> getTimeFrameByResolution(ChartResolution resolution);

    List<ChartTimeFrame> getAllTimeFrames();

    Map<String, ChartTimeFrameDto> getTimeFramesByResolutions();

    List<ChartResolution> getAllResolutions();
}
