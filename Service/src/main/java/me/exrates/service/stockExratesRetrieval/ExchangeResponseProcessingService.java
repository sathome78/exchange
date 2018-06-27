package me.exrates.service.stockExratesRetrieval;

import com.fasterxml.jackson.databind.JsonNode;
import me.exrates.model.StockExchange;
import me.exrates.model.StockExchangeStats;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public interface ExchangeResponseProcessingService {
    String sendGetRequest(String url, Map<String, String> params);

    String sendGetRequest(String url);

    List<StockExchangeStats> extractAllStatsFromMapNode(StockExchange stockExchange, String jsonResponse, BiFunction<String, String, String> currencyPairTransformer);

    JsonNode extractNode(String source, String... targetNodes);

    List<StockExchangeStats> extractAllStatsFromArrayNode(StockExchange stockExchange, JsonNode targetNode, String currencyPairNameField,
                                                          BiFunction<String, String, String> currencyPairTransformer);

    StockExchangeStats extractStatsFromSingleNode(String jsonResponse, StockExchange stockExchange, int currencyPairId);
}
