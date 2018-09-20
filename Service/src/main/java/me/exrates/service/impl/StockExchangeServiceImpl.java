package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.StockExchangeDao;
import me.exrates.model.StockExchange;
import me.exrates.model.StockExchangeStats;
import me.exrates.service.StockExchangeService;
import me.exrates.service.stockExratesRetrieval.StockExrateRetrievalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by OLEG on 14.12.2016.
 */
@Log4j2(topic = "tracker")
@Service
public class StockExchangeServiceImpl implements StockExchangeService {


    @Autowired
    private Map<String, StockExrateRetrievalService> stockExrateRetrievalServices;

    @Autowired
    private StockExchangeDao stockExchangeDao;

    private ExecutorService executorService = Executors.newCachedThreadPool();
    private CompletionService<List<StockExchangeStats>> completionService = new ExecutorCompletionService<>(executorService);


    @Override
    @Scheduled(cron = "0 0 * * * *")
    public void retrieveCurrencies() {
        log.debug("Start retrieving stock exchange statistics at: " + LocalDateTime.now());
        List<StockExchangeStats> stockExchangeStatsList = new ArrayList<>();
        List<StockExchange> activeExchanges = stockExchangeDao.findAllActive();
        int tasksSubmitted = 0;
        for (StockExchange stockExchange : activeExchanges) {
            try {
                StockExrateRetrievalService retrievalService = stockExrateRetrievalServices.get(stockExchange.getName());
                if (retrievalService != null) {
                    completionService.submit(() -> retrievalService.retrieveStats(stockExchange));
                    tasksSubmitted++;
                }
            } catch (Exception e) {
                log.warn(e.getMessage());
            }
        }

        for (int i = 0; i < tasksSubmitted; i++) {
            try {
                Future<List<StockExchangeStats>> nextResult = completionService.take();
                stockExchangeStatsList.addAll(nextResult.get());

            } catch (InterruptedException | ExecutionException e) {
                log.warn(e.getMessage());
            }
        }
        stockExchangeDao.saveStockExchangeStatsList(stockExchangeStatsList);
    }

    @Override
    public List<StockExchangeStats> getStockExchangeStatistics(Integer currencyPairId) {
        return stockExchangeDao.getStockExchangeStatistics(currencyPairId);
    }

    @PreDestroy
    public void destroy() {
        executorService.shutdown();
    }
}
