package me.exrates.service.stockExratesRetrieval;

import me.exrates.model.StockExchange;
import me.exrates.model.StockExchangeStats;

import java.util.List;

public interface StockExrateRetrievalService {
    List<StockExchangeStats> retrieveStats(StockExchange stockExchange);
}
