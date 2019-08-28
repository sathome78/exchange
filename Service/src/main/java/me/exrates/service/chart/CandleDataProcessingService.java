package me.exrates.service.chart;

import me.exrates.model.dto.CandleDto;
import me.exrates.model.vo.BackDealInterval;

import java.time.LocalDateTime;
import java.util.List;

public interface CandleDataProcessingService {

    List<CandleDto> getData(String pairName, LocalDateTime fromDate, LocalDateTime toDate, BackDealInterval interval);

    LocalDateTime getLastCandleTimeBeforeDate(String pairName, LocalDateTime date, BackDealInterval interval);
}