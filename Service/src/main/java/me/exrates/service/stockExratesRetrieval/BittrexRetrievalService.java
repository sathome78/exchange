package me.exrates.service.stockExratesRetrieval;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.CurrencyPair;
import me.exrates.model.StockExchange;
import me.exrates.model.StockExchangeStats;
import me.exrates.service.util.OkHttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by OLEG on 20.12.2016.
 */
@Log4j2(topic = "tracker")
@Service(value = "Bittrex")
public class BittrexRetrievalService implements StockExrateRetrievalService {

    @Autowired
    private ExchangeResponseProcessingService exchangeResponseProcessingService;


    @Override
    public List<StockExchangeStats> retrieveStats(StockExchange stockExchange) {
        String jsonResponse = exchangeResponseProcessingService.sendGetRequest("https://bittrex.com/api/v1.1/public/getmarketsummaries");
        JsonNode root = exchangeResponseProcessingService.extractNode(jsonResponse, "result");
        return exchangeResponseProcessingService.extractAllStatsFromArrayNode(stockExchange, root, "MarketName",
                (name1, name2) -> name2.concat("-").concat(name1));

    }


}
