package me.exrates.service.cache;

import me.exrates.model.ExOrder;
import me.exrates.model.dto.CacheOrderStatisticDto;
import me.exrates.model.enums.TradeMarket;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ExchangeRatesHolder {

    void onRatesChange(ExOrder exOrder);

    CacheOrderStatisticDto getOne(Integer currencyPairId);

    List<CacheOrderStatisticDto> getAllRates();

    List<CacheOrderStatisticDto> getCurrenciesRates(List<Integer> id);

    Map<String, BigDecimal> getRatesForMarket(TradeMarket market);

    BigDecimal getBtcUsdRate();
}
