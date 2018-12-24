package me.exrates.service.cache;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.OrderDao;
import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.dto.ExOrderStatisticsDto;
import me.exrates.model.dto.InputCreateOrderDto;
import me.exrates.model.dto.StatisticForMarket;
import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.model.enums.ActionType;
import me.exrates.model.enums.ChartPeriodsEnum;
import me.exrates.model.enums.TradeMarket;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.service.RabbitMqService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
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

    private Map<Integer, StatisticForMarket> ratesMarketMap = new ConcurrentHashMap<>();

    private static Integer ETH_USD_ID = 0;
    private static Integer BTC_USD_ID = 0;

    private final OrderDao orderDao;
    private final RabbitMqService rabbitMqService;

    @Autowired
    public MarketRatesHolder(OrderDao orderDao, RabbitMqService rabbitMqService) {
        this.orderDao = orderDao;
        this.rabbitMqService = rabbitMqService;
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
            if (o.getCurrencyPairName().equalsIgnoreCase("BTC/USD")) {
                BTC_USD_ID = o.getCurrencyPairId();
            } else if (o.getCurrencyPairName().equalsIgnoreCase("ETH/USD")) {
                ETH_USD_ID = o.getCurrencyPairId();
            }
        });
        log.info("Finish fill ratesMarketMap, time = {}", new Date());
    }

    public List<StatisticForMarket> getAllFromDb() {
        return orderDao.getOrderStatisticForNewMarkets().stream().peek(this::calculatePriceInUSD).collect(Collectors.toList());
    }

    public List<StatisticForMarket> getAll() {
        return ratesMarketMap.values().stream().peek(this::calculatePriceInUSD).collect(Collectors.toList());
    }

    public Map<Integer, StatisticForMarket> getRatesMarketMap() {
        return ratesMarketMap;
    }

    private void processPercentChange(StatisticForMarket o) {
        BigDecimal lastExrate = o.getLastOrderRate();
        BigDecimal predLast = o.getPredLastOrderRate() != null ? o.getPredLastOrderRate() : BigDecimal.ZERO;
        BigDecimal percentChange = BigDecimal.ZERO;
        if (BigDecimalProcessing.moreThanZero(lastExrate) && BigDecimalProcessing.moreThanZero(predLast)) {
            percentChange = BigDecimalProcessing.doAction(predLast, lastExrate, ActionType.PERCENT_GROWTH);
        }
        o.setPercentChange(BigDecimalProcessing.formatLocaleFixedDecimal(percentChange, Locale.ENGLISH, 2));
    }

    public void setRateMarket(ExOrder exOrder) {
        this.setRatesMarketMap(exOrder.getCurrencyPairId(), exOrder.getExRate(), exOrder.getAmountBase());
        InputCreateOrderDto createOrderDto = InputCreateOrderDto.of(exOrder);
        rabbitMqService.sendOrderInfo(createOrderDto, RabbitMqService.JSP_QUEUE);
    }

    public void setRateMarket(int currencyPairId, BigDecimal rate, BigDecimal amount) {
        this.setRatesMarketMap(currencyPairId, rate, amount);
    }

    private synchronized void setRatesMarketMap(int currencyPairId, BigDecimal rate, BigDecimal amount) {
        if (ratesMarketMap.containsKey(currencyPairId)) {

            CurrencyPair currencyPair = new CurrencyPair();
            currencyPair.setId(currencyPairId);

            ExOrderStatisticsDto statistic = orderDao.getOrderStatistic(currencyPair, ChartPeriodsEnum.HOURS_24.getBackDealInterval());

            StatisticForMarket statisticForMarket = ratesMarketMap.get(currencyPairId);
            statisticForMarket.setLastOrderRate(rate);
            BigDecimal predLastRate = new BigDecimal(statistic.getFirstOrderRate());
            statisticForMarket.setPredLastOrderRate(BigDecimalProcessing.normalize(predLastRate));
            BigDecimal volume = BigDecimalProcessing.doAction(statisticForMarket.getVolume(), amount, ActionType.ADD);
            statisticForMarket.setVolume(volume);
            this.processPercentChange(statisticForMarket);
            this.calculatePriceInUSD(statisticForMarket);
        }
    }

    public List<StatisticForMarket> getStatisticForMarketsByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        List<StatisticForMarket> result = new ArrayList<>();
        ids.forEach(p -> {
            StatisticForMarket statistic = ratesMarketMap.get(p);
            this.calculatePriceInUSD(statistic);
            result.add(statistic);
        });
        return result;
    }

    private void calculatePriceInUSD(StatisticForMarket pair) {
        if (pair.getMarket().equalsIgnoreCase("USD")) {
            pair.setPriceInUsd(pair.getLastOrderRate());
            return;
        }
        BigDecimal dealPrice = pair.getLastOrderRate();
        if (pair.getMarket().equalsIgnoreCase("BTC")) {
            pair.setPriceInUsd(getPriceInUsd(dealPrice, BTC_USD_ID));
        } else if (pair.getMarket().equalsIgnoreCase("ETH")) {
            pair.setPriceInUsd(getPriceInUsd(dealPrice, ETH_USD_ID));
        }
    }

    private BigDecimal getPriceInUsd(BigDecimal dealPrice, int pairId) {
        if (pairId == 0) {
            return null;
        } else if (ratesMarketMap.containsKey(pairId)) {
            BigDecimal lastRateForBtcUsd = ratesMarketMap.get(pairId).getLastOrderRate();
            return lastRateForBtcUsd.multiply(dealPrice);
        }
        return null;
    }

    public List<StatisticForMarket> getRatesForMarket(TradeMarket market){
        return getAll().stream()
                .filter(p->p.getMarket().equals(market.name()))
                .collect(Collectors.toList());

    }

    public BigDecimal getBtcUsdRate() {
        StatisticForMarket dto = ratesMarketMap.getOrDefault(BTC_USD_ID, null);
        return dto == null ? BigDecimal.ZERO : dto.getLastOrderRate();
    }
}
