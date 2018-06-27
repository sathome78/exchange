package me.exrates.service.stockExratesRetrieval;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.CurrencyPair;
import me.exrates.model.StockExchange;
import me.exrates.model.StockExchangeStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@Log4j2(topic = "tracker")
@Service
public class ExchangeResponseProcessingServiceImpl implements ExchangeResponseProcessingService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public String sendGetRequest(String url, Map<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(url);
        params.forEach(builder::queryParam);
        return restTemplate.getForObject(builder.toUriString(), String.class);
    }

    @Override
    public String sendGetRequest(String url) {
        return sendGetRequest(url, Collections.emptyMap());
    }


    @Override
    public List<StockExchangeStats> extractAllStatsFromMapNode(StockExchange stockExchange, String jsonResponse, BiFunction<String, String, String> currencyPairTransformer) {
        List<StockExchangeStats> stockExchangeStatsList = new ArrayList<>();
        JsonNode root = extractNode(jsonResponse);
        Map<String, CurrencyPair> currencyPairs = stockExchange.getAliasedCurrencyPairs(currencyPairTransformer);
        currencyPairs.keySet().forEach(currencyPairName -> {
            JsonNode currencyPairNode = root.get(currencyPairName);
            if (currencyPairNode != null) {
                StockExchangeStats stockExchangeStats = stockExchange.extractStatsFromNode(currencyPairNode,
                        currencyPairs.get(currencyPairName).getId());
                stockExchangeStatsList.add(stockExchangeStats);
            }
        });
        return stockExchangeStatsList;
    }

    @Override
    public JsonNode extractNode(String source, String... targetNodes) {
        JsonNode node = null;
        try {
            node = objectMapper.readTree(source);
            for (String nodeName : targetNodes) {
                node = node.get(nodeName);
            }
        } catch (IOException e) {
            log.error(e);
        }
        return node;
    }


    @Override
    public List<StockExchangeStats> extractAllStatsFromArrayNode(StockExchange stockExchange, JsonNode targetNode, String currencyPairNameField,
                                                                 BiFunction<String, String, String> currencyPairTransformer) {
        List<StockExchangeStats> stockExchangeStatsList = new ArrayList<>();
        Map<String, CurrencyPair> currencyPairs = stockExchange.getAliasedCurrencyPairs(currencyPairTransformer);
        targetNode.elements().forEachRemaining(elem -> {
            CurrencyPair currencyPair = currencyPairs.get(elem.get(currencyPairNameField).asText());
            if (currencyPair != null) {
                StockExchangeStats stockExchangeStats = stockExchange.extractStatsFromNode(elem,
                        currencyPair.getId());
                stockExchangeStatsList.add(stockExchangeStats);
            }
        });
        return stockExchangeStatsList;
    }

    @Override
    public StockExchangeStats extractStatsFromSingleNode(String jsonResponse, StockExchange stockExchange, int currencyPairId) {
        JsonNode root = extractNode(jsonResponse);
        return stockExchange.extractStatsFromNode(root, currencyPairId);
    }



}
