package me.exrates.service.impl;

import me.exrates.dao.StockChartDao;
import me.exrates.model.chart.ChartResolution;
import me.exrates.model.chart.ChartTimeFrame;
import me.exrates.model.dto.ChartTimeFrameDto;
import me.exrates.service.OrderService;
import me.exrates.service.StockChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class StockChartServiceImpl implements StockChartService {

    private Map<ChartResolution, ChartTimeFrame> timeFrames = new ConcurrentHashMap<>();

    @Autowired
    OrderService orderService;

    @Autowired
    private StockChartDao stockChartDao;

    @PostConstruct
    private void initTimeFramesMap() {
        List<ChartTimeFrame> availableTimeFrames = orderService.getChartTimeFrames();
        Map<ChartResolution, ChartTimeFrame> result = availableTimeFrames
                .stream()
                .collect(Collectors.toMap(
                        ChartTimeFrame::getResolution,
                        timeFrame -> timeFrame));

        timeFrames.putAll(result);
    }


    @Override
    public Optional<ChartTimeFrame> getTimeFrameByResolution(ChartResolution resolution) {
        return Optional.ofNullable(timeFrames.get(resolution));
    }

    @Override
    public List<ChartTimeFrame> getAllTimeFrames() {
        return new ArrayList<>(timeFrames.values());
    }

    @Override
    public Map<String, ChartTimeFrameDto> getTimeFramesByResolutions() {
        return timeFrames.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        entry -> entry.getKey().toString(),
                        entry -> new ChartTimeFrameDto(entry.getValue())));
    }

    @Override
    public List<ChartResolution> getAllResolutions() {
        return new ArrayList<>(timeFrames.keySet());
    }

}

