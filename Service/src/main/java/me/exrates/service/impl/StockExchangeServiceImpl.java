package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.StockExchangeDao;
import me.exrates.model.StockExchangeStats;
import me.exrates.service.StockExchangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by OLEG on 14.12.2016.
 */
@Log4j2(topic = "tracker")
@Service
public class StockExchangeServiceImpl implements StockExchangeService {

    @Autowired
    private StockExchangeDao stockExchangeDao;

    @Override
    public List<StockExchangeStats> getStockExchangeStatistics(Integer currencyPairId) {
        return stockExchangeDao.getStockExchangeStatistics(currencyPairId);
    }
}
