package me.exrates.service.cache;

import me.exrates.model.ExOrder;
import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.model.enums.TradeMarket;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ExchangeRatesHolder {

    void onRatesChange(ExOrder exOrder);

    ExOrderStatisticsShortByPairsDto getOne(Integer currencyPairId);

    List<ExOrderStatisticsShortByPairsDto> getAllRates();

    List<ExOrderStatisticsShortByPairsDto> getCurrenciesRates(Set<Integer> id);

    Map<String, BigDecimal> getRatesForMarket(TradeMarket market);

    BigDecimal getBtcUsdRate();

    void addCurrencyPairToCache(int currencyPairId);

    void deleteCurrencyPairFromCache(int currencyPairId);
}
