package me.exrates.dao;

import me.exrates.model.StockExchange;
import me.exrates.model.StockExchangeStats;

import java.util.List;
import java.util.Optional;

/**
 * Created by OLEG on 14.12.2016.
 */
public interface StockExchangeDao {

    Optional<StockExchange> findStockExchangeByName(String name);

    List<StockExchange> findAll();

    List<StockExchange> findAllActive();

    List<StockExchangeStats> getStockExchangeStatistics(Integer currencyPairId);
}
