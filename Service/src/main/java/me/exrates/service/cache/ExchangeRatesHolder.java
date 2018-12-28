package me.exrates.service.cache;

import me.exrates.model.ExOrder;
import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;

import java.math.BigDecimal;
import java.util.List;

public interface ExchangeRatesHolder {

    void onRatesChange(ExOrder exOrder);

    List<ExOrderStatisticsShortByPairsDto> getAllRates();

    List<ExOrderStatisticsShortByPairsDto> getCurrenciesRates(List<Integer> id);
}
