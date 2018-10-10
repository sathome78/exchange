package me.exrates.service.stockExratesRetrieval;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.StockExchange;
import me.exrates.model.StockExchangeStats;
import me.exrates.model.util.BigDecimalProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Kraken API Response Syntax:
 *      a = ask array(<price>, <whole lot volume>, <lot volume>),
 *      b = bid array(<price>, <whole lot volume>, <lot volume>),
 *      c = last trade closed array(<price>, <lot volume>),
 *      v = volume array(<today>, <last 24 hours>),
 *      p = volume weighted average price array(<today>, <last 24 hours>),
 *      t = number of trades array(<today>, <last 24 hours>),
 *      l = low array(<today>, <last 24 hours>),
 *      h = high array(<today>, <last 24 hours>),
 *      o = today's opening price
 *
 * Created by OLEG on 14.12.2016.
 */
@Log4j2(topic = "tracker")
@Service(value = "Kraken")
public class KrakenRetrievalService implements StockExrateRetrievalService {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ExchangeResponseProcessingService exchangeResponseProcessingService;

    private final String LAST_ARRAY = "a";
    private final String ASK_ARRAY = "a";
    private final String BID_ARRAY = "b";
    private final String LOW_ARRAY = "l";
    private final String HIGH_ARRAY = "h";
    private final String VOLUME_ARRAY = "v";
    private final int LAST_PRICE_ITEM = 0;
    private final int ASK_PRICE_ITEM = 0;
    private final int BID_PRICE_ITEM = 0;
    private final int LOW_PRICE_ITEM = 0;
    private final int HIGH_PRICE_ITEM = 0;
    private final int VOLUME_ITEM = 0;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<StockExchangeStats> retrieveStats(StockExchange stockExchange) {
        List<StockExchangeStats> stockExchangeStatsList = new ArrayList<>();


        stockExchange.getAliasedCurrencyPairs(String::concat)
                .forEach((name, currencyPair ) -> {
            String url = "https://api.kraken.com/0/public/Ticker";
            Map<String, String> params = Collections.singletonMap("pair", name);
            String jsonResponse = exchangeResponseProcessingService.sendGetRequest(url, params);
            log.debug(jsonResponse);
            try {
                JsonNode root = objectMapper.readTree(jsonResponse);
                StockExchangeStats stockExchangeStats= new StockExchangeStats();
                stockExchangeStats.setCurrencyPairId(currencyPair.getId());
                root.get("result").elements().forEachRemaining(currencyPairNode -> {
                    BigDecimal priceLast = BigDecimalProcessing.parseNonePoint(currencyPairNode.get(LAST_ARRAY)
                            .get(LAST_PRICE_ITEM).asText());
                    BigDecimal priceBuy = BigDecimalProcessing.parseNonePoint(currencyPairNode.get(BID_ARRAY)
                            .get(BID_PRICE_ITEM).asText());
                    BigDecimal priceSell = BigDecimalProcessing.parseNonePoint(currencyPairNode.get(ASK_ARRAY)
                            .get(ASK_PRICE_ITEM).asText());
                    BigDecimal priceLow = BigDecimalProcessing.parseNonePoint(currencyPairNode.get(LOW_ARRAY)
                            .get(LOW_PRICE_ITEM).asText());
                    BigDecimal priceHigh = BigDecimalProcessing.parseNonePoint(currencyPairNode.get(HIGH_ARRAY)
                            .get(HIGH_PRICE_ITEM).asText());
                    BigDecimal volume = BigDecimalProcessing.parseNonePoint(currencyPairNode.get(VOLUME_ARRAY)
                            .get(VOLUME_ITEM).asText());

                    stockExchangeStats.setDate(LocalDateTime.now());
                    stockExchangeStats.setStockExchange(stockExchange);
                    stockExchangeStats.setPriceLast(priceLast);
                    stockExchangeStats.setPriceBuy(priceBuy);
                    stockExchangeStats.setPriceSell(priceSell);
                    stockExchangeStats.setPriceLow(priceLow);
                    stockExchangeStats.setPriceHigh(priceHigh);
                    stockExchangeStats.setVolume(volume);
                    stockExchangeStatsList.add(stockExchangeStats);
                });

            } catch (IOException e) {
                log.error(e);
            }

        });
        return stockExchangeStatsList;

    }


}
