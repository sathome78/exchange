package me.exrates.dao;

import me.exrates.model.StockExchange;
import me.exrates.model.StockExchangeStats;
import me.exrates.model.dto.StockExchangeRateDto;

import java.util.List;
import java.util.Optional;

/**
 * Created by OLEG on 14.12.2016.
 */
public interface StockExchangeDao {
    void saveStockExchangeStats(StockExchangeStats stockExchangeRate);

    void saveStockExchangeStatsList(List<StockExchangeStats> stockExchangeRates);

    Optional<StockExchange> findStockExchangeByName(String name);

    List<StockExchange> findAll();

    List<StockExchangeRateDto> getStockExchangeStatistics(List<Integer> currencyPairIds);
}
