package me.exrates.service.stockExratesRetrieval;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.StockExchange;
import me.exrates.model.StockExchangeStats;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by OLEG on 09.02.2017.
 */
@Log4j2(topic = "tracker")
@Service(value = "Exrates")
public class ExratesRetrievalService implements StockExrateRetrievalService {

    @Autowired
    private OrderService orderService;

    @Override
    public List<StockExchangeStats> retrieveStats(StockExchange stockExchange) {
        return orderService.getCoinmarketDataForActivePairs("", new BackDealInterval("24 HOUR"))
                .stream()
                .map(dto -> new StockExchangeStats(dto, stockExchange))
                .collect(Collectors.toList());
    }

}
