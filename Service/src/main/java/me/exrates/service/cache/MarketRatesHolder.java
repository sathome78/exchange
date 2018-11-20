package me.exrates.service.cache;

import lombok.extern.log4j.Log4j2;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Log4j2
@EnableScheduling
@Service
public class MarketRatesHolder {

    private final OrderDao orderDao;
    private Map<Integer, StatisticForMarket> ratesMarketMap = new ConcurrentHashMap<>();

    @Autowired
    public MarketRatesHolder(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    @Scheduled(cron = "0 0 * * * ?") //every night on 00-00
    @PostConstruct
    private void init() {
        log.info("Start fill ratesMarketMap, time = {}", new Date());
        if (!ratesMarketMap.isEmpty()) ratesMarketMap.clear();
        List<StatisticForMarket> markets = orderDao.getOrderStatisticForNewMarkets();
        markets.forEach(o -> {
            processPercentChange(o);
            ratesMarketMap.put(o.getCurrencyPairId(), o);
        });
        log.info("Finish fill ratesMarketMap, time = {}", new Date());
    }

    public List<StatisticForMarket> getAllFromDb() {
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

    public void setRateMarket(int currencyPairId, BigDecimal rate, BigDecimal amount) {
        this.setRatesMarketMap(currencyPairId, rate, amount);
    }

    private synchronized void setRatesMarketMap(int currencyPairId, BigDecimal rate, BigDecimal amount) {
        if (ratesMarketMap.containsKey(currencyPairId)) {
            StatisticForMarket statisticForMarket = ratesMarketMap.get(currencyPairId);
            statisticForMarket.setLastOrderRate(rate);
            BigDecimal volume = BigDecimalProcessing.doAction(statisticForMarket.getVolume(), amount, ActionType.ADD);
            statisticForMarket.setVolume(volume);
            this.processPercentChange(statisticForMarket);
        }
    }
}
