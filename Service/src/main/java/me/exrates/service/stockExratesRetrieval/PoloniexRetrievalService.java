package me.exrates.service.stockExratesRetrieval;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.StockExchange;
import me.exrates.model.StockExchangeStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * Created by OLEG on 20.12.2016.
 */
@Log4j2(topic = "tracker")
@Service(value = "Poloniex")
public class PoloniexRetrievalService implements StockExrateRetrievalService {

    @Autowired
    private ExchangeResponseProcessingService exchangeResponseProcessingService;

    @Override
    public List<StockExchangeStats> retrieveStats(StockExchange stockExchange) {
        String jsonResponse = exchangeResponseProcessingService.sendGetRequest("https://poloniex.com/public", Collections.singletonMap("command", "returnTicker"));
        return exchangeResponseProcessingService.extractAllStatsFromMapNode(stockExchange, jsonResponse,
                (name1, name2) -> name2.concat("_").concat(name1));// IMPORTANT! In POLONIEX API currencies in pairs are inverted - i.e. DASH/BTC looks like BTC_DASH
    }


}
