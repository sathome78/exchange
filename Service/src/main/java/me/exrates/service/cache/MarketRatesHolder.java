package me.exrates.service.cache;

import me.exrates.dao.OrderDao;
import me.exrates.model.dto.StatisticForMarket;
import me.exrates.model.enums.ActionType;
import me.exrates.model.util.BigDecimalProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@EnableScheduling
@Service
public class MarketRatesHolder {

    private final OrderDao orderDao;
    private Map<Integer, StatisticForMarket> ratesMarketMap = new ConcurrentHashMap<>();

    @Autowired
    public MarketRatesHolder(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    @Scheduled(cron = "0 0 * * *") //every night on 00-00
    @PostConstruct
    private void init() {
        List<StatisticForMarket> markets = orderDao.getOrderStatisticForNewMarkets();
        markets.forEach(this::processPercentChange);
        markets.forEach(p -> ratesMarketMap.put(p.getCurrencyPairId(), p));
    }

    public List<StatisticForMarket> getAllFromDb(){
        return orderDao.getOrderStatisticForNewMarkets().stream().peek(this::processPercentChange).collect(Collectors.toList());
    }

    public List<StatisticForMarket> getAll() {
        return ratesMarketMap.values().stream().peek(this::processPercentChange).collect(Collectors.toList());
    }

    private void processPercentChange(StatisticForMarket o) {
        BigDecimal lastExrate = o.getLastOrderRate();
        BigDecimal predLast = o.getPredLastOrderRate() != null ? o.getPredLastOrderRate() : BigDecimal.ZERO;
        BigDecimal percentChange = new BigDecimal(0);
        if (BigDecimalProcessing.moreThanZero(lastExrate) && BigDecimalProcessing.moreThanZero(predLast)) {
            percentChange = BigDecimalProcessing.doAction(predLast, lastExrate, ActionType.PERCENT_GROWTH);
        }
        o.setPercentChange(BigDecimalProcessing.formatLocaleFixedDecimal(percentChange, Locale.ENGLISH, 2));
    }

    public void setRateMarket(int currencyPairId, BigDecimal rate) {
        this.setRatesMarketMap(currencyPairId, rate);
    }

    private synchronized void setRatesMarketMap(int currencyPairId, BigDecimal rate) {
        if (ratesMarketMap.containsKey(currencyPairId)) {
            StatisticForMarket statisticForMarket = ratesMarketMap.get(currencyPairId);
            BigDecimal lastExrate = statisticForMarket.getLastOrderRate();
            statisticForMarket.setPredLastOrderRate(lastExrate);
            statisticForMarket.setLastOrderRate(rate);
        }
    }

}
