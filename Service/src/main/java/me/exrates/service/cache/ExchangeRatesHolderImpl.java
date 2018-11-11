package me.exrates.service.cache;

import me.exrates.dao.OrderDao;
import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ExchangeRatesHolderImpl implements ExchangeRatesHolder {

    private Map<Integer, ExOrderStatisticsShortByPairsDto> ratesMap = new ConcurrentHashMap<>();

    private final OrderDao orderDao;
    private static Integer ETH_USD_ID = 0;
    private static Integer BTC_USD_ID = 0;

    @Autowired
    public ExchangeRatesHolderImpl(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    @PostConstruct
    private void init() {
        List<ExOrderStatisticsShortByPairsDto> list = orderDao.getOrderStatisticByPairs();
        list.forEach(p-> {
            ratesMap.put(p.getCurrencyPairId(), p);
            if (p.getCurrencyPairName().equalsIgnoreCase("BTC/USD")) {
                BTC_USD_ID = p.getCurrencyPairId();
            } else if (p.getCurrencyPairName().equalsIgnoreCase("ETH/USD")) {
                 ETH_USD_ID = p.getCurrencyPairId();
            }
        });
    }

    @Override
    public void onRatesChange(Integer pairId, BigDecimal rate) {
        setRates(pairId, rate);
    }

    private synchronized void setRates(Integer pairId, BigDecimal rate) {
        if (ratesMap.containsKey(pairId)) {
            ExOrderStatisticsShortByPairsDto dto = ratesMap.get(pairId);
            dto.setPredLastOrderRate(dto.getLastOrderRate());
            dto.setLastOrderRate(rate.toPlainString());
        } else {
            ratesMap.put(pairId, orderDao.getOrderStatisticForSomePairs(Collections.singletonList(pairId)).get(0));
        }
    }

    @Override
    public List<ExOrderStatisticsShortByPairsDto> getAllRates() {
        List<ExOrderStatisticsShortByPairsDto> pairs = new ArrayList<>(ratesMap.values());
        pairs.forEach(this::calculatePriceInUSD);
        return pairs;
    }

    @Override
    public List<ExOrderStatisticsShortByPairsDto> getCurrenciesRates(List<Integer> id) {
        if (id == null || id.isEmpty()) {
            return Collections.emptyList();
        }
        List<ExOrderStatisticsShortByPairsDto> result = new ArrayList<>();
        id.forEach(p-> result.add(ratesMap.get(p)));
        return result;
    }

    private void calculatePriceInUSD(ExOrderStatisticsShortByPairsDto pair) {
        if (pair.getMarket().equalsIgnoreCase("USD")) {
            pair.setPriceInUSD(pair.getLastOrderRate());
            return;
        }
        BigDecimal dealPrice = new BigDecimal(pair.getLastOrderRate());
        if (pair.getMarket().equalsIgnoreCase("BTC")) {
            pair.setPriceInUSD(getPriceInUsd(dealPrice, BTC_USD_ID));
        } else if (pair.getMarket().equalsIgnoreCase("ETH")) {
            pair.setPriceInUSD(getPriceInUsd(dealPrice, ETH_USD_ID));
        }
    }

    private String getPriceInUsd(BigDecimal dealPrice, int pairId) {
        if (pairId == 0) {
            return "7.77";
        } else  if (ratesMap.containsKey(pairId)) {
            BigDecimal lastRateForBtcUsd = new BigDecimal(ratesMap.get(pairId).getLastOrderRate());
            return lastRateForBtcUsd.multiply(dealPrice).toPlainString();
        }
        return "417";
    }
}
