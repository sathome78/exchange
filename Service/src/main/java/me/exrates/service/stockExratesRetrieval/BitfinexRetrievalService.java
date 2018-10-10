package me.exrates.service.stockExratesRetrieval;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.StockExchange;
import me.exrates.model.StockExchangeStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by OLEG on 14.12.2016.
 */
@Log4j2(topic = "tracker")
@Service(value = "BITFINEX")
public class BitfinexRetrievalService implements StockExrateRetrievalService {

    @Autowired
    private ExchangeResponseProcessingService exchangeResponseProcessingService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<StockExchangeStats> retrieveStats(StockExchange stockExchange) {
        List<StockExchangeStats> stockExchangeStatsList = new ArrayList<>();
        stockExchange.getAliasedCurrencyPairs((name1, name2) -> name1.toLowerCase() + name2.toLowerCase())
                .forEach((name, currencyPair)-> {
            String jsonResponse = exchangeResponseProcessingService.sendGetRequest("https://api.bitfinex.com/v1/pubticker/" + name);
            stockExchangeStatsList.add(exchangeResponseProcessingService.extractStatsFromSingleNode(jsonResponse,
                            stockExchange, currencyPair.getId())) ;

        });
        return stockExchangeStatsList;

    }

}
