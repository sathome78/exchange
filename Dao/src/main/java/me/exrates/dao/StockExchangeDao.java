package me.exrates.dao;

import me.exrates.model.StockExchange;
import me.exrates.model.StockExchangeStats;

import java.util.List;
import java.util.Optional;

public interface StockExchangeDao {
    void saveStockExchangeStats(StockExchangeStats stockExchangeRate);

    void saveStockExchangeStatsList(List<StockExchangeStats> stockExchangeRates);

    Optional<StockExchange> findStockExchangeByName(String name);

    List<StockExchange> findAll();

    List<StockExchange> findAllActive();

    List<StockExchangeStats> getStockExchangeStatistics(Integer currencyPairId);
}
