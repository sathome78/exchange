package me.exrates.service.stockExratesRetrieval;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.CurrencyPair;
import me.exrates.model.StockExchange;
import me.exrates.model.StockExchangeStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by OLEG on 15.12.2016.
 */
@Log4j2(topic = "tracker")
@Service(value = "xBTCe")
public class XBTCeRetrievalService implements StockExrateRetrievalService {

    @Autowired
    private ExchangeResponseProcessingService exchangeResponseProcessingService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<StockExchangeStats> retrieveStats(StockExchange stockExchange) {
        Map<String, CurrencyPair> currencyPairs = stockExchange.getAliasedCurrencyPairs(String::concat);
        String urlBase = "https://cryptottlivewebapi.xbtce.net:8443/api/v1/public/ticker/";
        String urlFilter = String.join(" ", currencyPairs.keySet());
        String jsonResponse = exchangeResponseProcessingService.sendGetRequest(urlBase + urlFilter);

        JsonNode root = exchangeResponseProcessingService.extractNode(jsonResponse);

        List<StockExchangeStats> stockExchangeStatsList = new ArrayList<>();
        root.elements().forEachRemaining(jsonNode -> {
            CurrencyPair currencyPair = currencyPairs.get(root.get("Symbol").asText());
            if (currencyPair != null) {
                stockExchange.setLastFieldName(extractLastPriceField(jsonNode));
                StockExchangeStats stockExchangeStats = stockExchange.extractStatsFromNode(jsonNode, currencyPair.getId());
                stockExchangeStatsList.add(stockExchangeStats);
            }

        });
        return stockExchangeStatsList;


    }

    private String extractLastPriceField(JsonNode jsonNode) {
        long lastBuyTimestamp = jsonNode.get("LastBuyTimestamp").longValue();
        long lastSellTimestamp = jsonNode.get("LastSellTimestamp").longValue();
        return lastBuyTimestamp > lastSellTimestamp ? "LastBuyPrice" : "LastSellPrice";
    }


}
