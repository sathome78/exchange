package me.exrates.service.stockExratesRetrieval;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.StockExchange;
import me.exrates.model.StockExchangeStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by OLEG on 20.12.2016.
 */
@Log4j2(topic = "tracker")
@Service(value = "HitBTC")
public class HitBtcRetrievalService implements StockExrateRetrievalService {

    @Autowired
    private ExchangeResponseProcessingService exchangeResponseProcessingService;


    @Override
    public List<StockExchangeStats> retrieveStats(StockExchange stockExchange) {
        String jsonResponse = exchangeResponseProcessingService.sendGetRequest("http://api.hitbtc.com/api/1/public/ticker");
        return exchangeResponseProcessingService.extractAllStatsFromMapNode(stockExchange, jsonResponse, String::concat);
    }


}
