package me.exrates.service.impl;

import me.exrates.dao.StockExchangeDao;
import me.exrates.model.CurrencyPair;
import me.exrates.model.StockExchange;
import me.exrates.model.dto.StockExchangeRateDto;
import me.exrates.service.CurrencyService;
import me.exrates.service.StockExchangeService;
import me.exrates.service.stockExratesRetrieval.StockExrateRetrievalService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by OLEG on 14.12.2016.
 */
@Service
public class StockExchangeServiceImpl implements StockExchangeService {

    private static final Logger LOGGER = LogManager.getLogger(StockExchangeServiceImpl.class);

    @Autowired
    private List<StockExrateRetrievalService> stockExrateRetrievalServices;

    @Autowired
    private StockExchangeDao stockExchangeDao;


    @Override
//    @Scheduled(initialDelay = 5 * 1000L, fixedDelay = 1000 * 10000000000L)
    public void retrieveCurrencies() {
        Map<String, StockExchange> stockExchanges = stockExchangeDao.findAll().stream()
                .collect(Collectors.toMap(StockExchange::getName, stockExchange -> stockExchange));
        LOGGER.debug(stockExchanges);

        stockExrateRetrievalServices.forEach(service -> {
            service.retrieveAndSave(stockExchanges.get(service.getStockExchangeName()));
        });
    }

    @Override
    public List<StockExchangeRateDto> getStockExchangeStatistics() {
        return stockExchangeDao.getStockExchangeStatistics();
    }
}
