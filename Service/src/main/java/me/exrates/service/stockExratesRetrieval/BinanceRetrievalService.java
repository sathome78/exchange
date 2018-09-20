package me.exrates.service.stockExratesRetrieval;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.StockExchange;
import me.exrates.model.StockExchangeStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by OLEG on 20.12.2016.
 */
@Log4j2(topic = "tracker")
@Service(value = "Binance")
public class BinanceRetrievalService implements StockExrateRetrievalService {
    @Autowired
    private ExchangeResponseProcessingService exchangeResponseProcessingService;


    @Override
    public List<StockExchangeStats> retrieveStats(StockExchange stockExchange) {
        List<StockExchangeStats> stockExchangeStatsList = new ArrayList<>();
        stockExchange.getAliasedCurrencyPairs(String::concat)
                .forEach((currencyPairName, currencyPair) -> {
                    String jsonResponse = exchangeResponseProcessingService.sendGetRequest("https://www.binance.com/api/v1/ticker/24hr",
                            Collections.singletonMap("symbol", currencyPairName));
                    stockExchangeStatsList.add(exchangeResponseProcessingService.extractStatsFromSingleNode(jsonResponse,
                            stockExchange, currencyPair.getId())) ;
                });
        return stockExchangeStatsList;


    }

}
