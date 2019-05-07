package me.exrates.service;

import me.exrates.model.StockExchangeStats;

import javax.annotation.PostConstruct;
import java.util.List;

public interface StockExchangeService {
    @PostConstruct
    void retrieveCurrencies();

    List<StockExchangeStats> getStockExchangeStatistics(Integer currencyPairId);
}
