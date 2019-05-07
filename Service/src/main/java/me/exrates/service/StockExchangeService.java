package me.exrates.service;

import me.exrates.model.StockExchangeStats;

import java.util.List;

/**
 * Created by OLEG on 14.12.2016.
 */
public interface StockExchangeService {

    List<StockExchangeStats> getStockExchangeStatistics(Integer currencyPairId);
}
