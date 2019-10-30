package me.exrates.service.chart;

import me.exrates.model.dto.CandleDto;

import java.time.LocalDateTime;
import java.util.List;

public interface CandleDataProcessingService {

    List<CandleDto> getData(String pairName, Long from, Long to, String resolution);

    LocalDateTime getLastCandleTimeBeforeDate(String pairName, Long date, String resolution);
}