package me.exrates.service.cache;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.ExOrder;
import me.exrates.model.dto.CacheOrderStatisticDto;
import me.exrates.model.enums.ActionType;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.Market;
import me.exrates.model.enums.TradeMarket;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.service.CurrencyService;
import me.exrates.service.OrderService;
import me.exrates.service.api.ExchangeApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static me.exrates.service.util.CollectionUtil.isEmpty;
import static me.exrates.service.util.CollectionUtil.isNotEmpty;

@Log4j2
@Component
public class ExchangeRatesHolderImpl implements ExchangeRatesHolder {

    private static final String DELIMITER = "/";

    private static final String BTC_USD = "BTC/USD";
    private static final String ETH_USD = "ETH/USD";

    private static final String FIAT = "fiat";
    private static final String USD = "USD";

    private final ExchangeApi exchangeApi;
    private final OrderService orderService;
    private final CurrencyService currencyService;
    private final ExchangeRatesRedisRepository ratesRedisRepository;

    private List<CacheOrderStatisticDto> exratesCache = new CopyOnWriteArrayList<>();
    private Map<String, BigDecimal> fiatCache = new ConcurrentHashMap<>();

    private static final ScheduledExecutorService EXRATES_SCHEDULER = Executors.newSingleThreadScheduledExecutor();
    private static final ScheduledExecutorService FIAT_SCHEDULER = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    public ExchangeRatesHolderImpl(ExchangeApi exchangeApi,
                                   OrderService orderService,
                                   CurrencyService currencyService,
                                   ExchangeRatesRedisRepository ratesRedisRepository) {
        this.exchangeApi = exchangeApi;
        this.orderService = orderService;
        this.currencyService = currencyService;
        this.ratesRedisRepository = ratesRedisRepository;
    }

    @PostConstruct
    private void init() {
        EXRATES_SCHEDULER.scheduleAtFixedRate(() -> {
            List<CacheOrderStatisticDto> newData = getExratesCache(null);
            exratesCache = new CopyOnWriteArrayList<>(newData);
        }, 0, 1, TimeUnit.MINUTES);

        FIAT_SCHEDULER.scheduleAtFixedRate(() -> {
            Map<String, BigDecimal> newData = getFiatCache();
            fiatCache = new ConcurrentHashMap<>(newData);
        }, 0, 1, TimeUnit.MINUTES);

        log.info("Start init ExchangeRatesHolder");

        initExchangePairsCache();

        initFiatPairsCache();

        log.info("Finish init ExchangeRatesHolder");
    }

    private List<CacheOrderStatisticDto> getExratesCache(String currencyPairName) {
        if (isNotEmpty(exratesCache)) {
            return isNull(currencyPairName)
                    ? exratesCache
                    : exratesCache.stream()
                    .filter(cache -> Objects.equals(currencyPairName, cache.getCurrencyPairName()))
                    .collect(toList());
        } else {
            return orderService.getDailyCoinmarketDataForCache(currencyPairName);
        }
    }

    private Map<String, BigDecimal> getFiatCache() {
        if (nonNull(fiatCache) && !fiatCache.isEmpty()) {
            return fiatCache;
        } else {
            return exchangeApi.getRatesByCurrencyType(FIAT).entrySet().stream()
                    .filter(entry -> !USD.equals(entry.getKey()))
                    .collect(
                            toMap(
                                    Map.Entry::getKey,
                                    entry -> entry.getValue().getLeft()
                            ));
        }
    }

    private void initExchangePairsCache() {
        List<CacheOrderStatisticDto> statisticList = getExratesCache(null)
                .stream()
                .peek(statistic -> {
                    statistic.setPercentChange(calculatePercentChange(statistic));
                    statistic.setPriceInUSD(calculatePriceInUSD(statistic));
                }).collect(toList());
        ratesRedisRepository.batchUpdate(statisticList);
    }

    private void initFiatPairsCache() {
        List<CacheOrderStatisticDto> statisticList = currencyService.getAllFiatPairs().stream()
                .map(pair -> {
                    final int pairId = pair.getId();
                    final String pairName = pair.getName();
                    final String market = pair.getMarket();
                    final CurrencyPairType type = pair.getType();
                    final Integer currencyPairPrecision = pair.getCurrencyPairPrecision();

                    if (ratesRedisRepository.exist(pairName)) {
                        return null;
                    } else {
                        return CacheOrderStatisticDto.builder()
                                .currencyPairId(pairId)
                                .currencyPairName(pairName)
                                .market(market)
                                .currencyPairType(type)
                                .currencyPairPrecision(currencyPairPrecision)
                                .lastOrderRate(BigDecimal.ZERO)
                                .predLastOrderRate(BigDecimal.ZERO)
                                .percentChange(BigDecimal.ZERO)
                                .priceInUSD(BigDecimal.ZERO)
                                .volume(BigDecimal.ZERO)
                                .currencyVolume(BigDecimal.ZERO)
                                .high24hr(BigDecimal.ZERO)
                                .low24hr(BigDecimal.ZERO)
                                .build();
                    }
                })
                .filter(Objects::nonNull)
                .collect(toList());

        ratesRedisRepository.batchUpdate(statisticList);
    }

    @Override
    public void onRatesChange(ExOrder exOrder) {
        setRates(exOrder);
    }

    private synchronized void setRates(ExOrder order) {
        final int currencyPairId = order.getCurrencyPairId();
        final String currencyPairName = order.getCurrencyPair().getName();
        final String market = order.getCurrencyPair().getMarket();
        final BigDecimal lastOrderRate = order.getExRate();
        final BigDecimal amountBase = order.getAmountBase();
        final BigDecimal amountConvert = order.getAmountConvert();

        List<CacheOrderStatisticDto> statisticList = getExratesCache(currencyPairName);

        CacheOrderStatisticDto statistic;
        if (isNotEmpty(statisticList)) {
            statistic = statisticList.get(0);
            statistic.setLastOrderRate(lastOrderRate);
            statistic.setPredLastOrderRate(statistic.getLastOrderRate());
            statistic.setPercentChange(calculatePercentChange(statistic));
            statistic.setPriceInUSD(calculatePriceInUSD(statistic));
            statistic.setVolume(statistic.getVolume().add(amountBase));
            statistic.setCurrencyVolume(statistic.getCurrencyVolume().add(amountConvert));
            statistic.setHigh24hr(statistic.getHigh24hr().compareTo(lastOrderRate) > 0
                    ? statistic.getHigh24hr()
                    : lastOrderRate);
            statistic.setLow24hr(statistic.getLow24hr().compareTo(lastOrderRate) > 0
                    ? lastOrderRate
                    : statistic.getLow24hr());
        } else {
            statistic = new CacheOrderStatisticDto();
            statistic.setCurrencyPairId(currencyPairId);
            statistic.setCurrencyPairName(currencyPairName);
            statistic.setMarket(market);
            statistic.setLastOrderRate(lastOrderRate);
            statistic.setPredLastOrderRate(statistic.getLastOrderRate());
            statistic.setPercentChange(calculatePercentChange(statistic));
            statistic.setPriceInUSD(calculatePriceInUSD(statistic));
            statistic.setVolume(statistic.getVolume().add(amountBase));
            statistic.setCurrencyVolume(statistic.getCurrencyVolume().add(amountConvert));
            statistic.setHigh24hr(statistic.getHigh24hr().compareTo(lastOrderRate) > 0
                    ? statistic.getHigh24hr()
                    : lastOrderRate);
            statistic.setLow24hr(statistic.getLow24hr().compareTo(lastOrderRate) > 0
                    ? lastOrderRate
                    : statistic.getLow24hr());
        }

        if (ratesRedisRepository.exist(currencyPairName)) {
            ratesRedisRepository.update(statistic);
        } else {
            ratesRedisRepository.put(statistic);
        }
    }

    @Override
    public CacheOrderStatisticDto getOne(Integer currencyPairId) {
        String currencyPairName = getCurrencyPairNameById(currencyPairId);
        return ratesRedisRepository.get(currencyPairName);
    }

    @Override
    public List<CacheOrderStatisticDto> getAllRates() {
        return ratesRedisRepository.getAll();
    }

    @Override
    public List<CacheOrderStatisticDto> getCurrenciesRates(List<Integer> ids) {
        if (isEmpty(ids)) {
            return Collections.emptyList();
        }
        List<String> names = ids.stream()
                .map(this::getCurrencyPairNameById)
                .collect(toList());

        return ratesRedisRepository.getByNames(names);
    }

    @Override
    public Map<String, BigDecimal> getRatesForMarket(TradeMarket market) {
        return getAllRates().stream()
                .filter(statistic -> statistic.getMarket().equals(market.name()))
                .collect(Collectors.toMap(
                        statistic -> statistic.getCurrencyPairName().split(DELIMITER)[0],
                        CacheOrderStatisticDto::getLastOrderRate, (oldValue, newValue) -> oldValue));
    }

    @Override
    public BigDecimal getBtcUsdRate() {
        CacheOrderStatisticDto dto = ratesRedisRepository.get(BTC_USD);
        return isNull(dto) ? BigDecimal.ZERO : dto.getLastOrderRate();
    }

    private BigDecimal calculatePriceInUSD(CacheOrderStatisticDto statistic) {
        final String market = statistic.getMarket();
        final BigDecimal lastOrderRate = statistic.getLastOrderRate();
        if (isNull(lastOrderRate) || lastOrderRate.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        switch (Market.of(market)) {
            case USD:
            case USDT:
                return lastOrderRate;
            case BTC:
                CacheOrderStatisticDto btcStatistic = getExratesCache(BTC_USD).get(0);
                BigDecimal btcLastOrderRate = nonNull(btcStatistic) ? btcStatistic.getLastOrderRate() : BigDecimal.ZERO;
                return btcLastOrderRate.multiply(lastOrderRate);
            case ETH:
                CacheOrderStatisticDto ethStatistic = getExratesCache(ETH_USD).get(0);
                BigDecimal ethLastOrderRate = nonNull(ethStatistic) ? ethStatistic.getLastOrderRate() : BigDecimal.ZERO;
                return ethLastOrderRate.multiply(lastOrderRate);
            case FIAT:
                final String currencyName = statistic.getCurrencyPairName().split(DELIMITER)[1];
                BigDecimal usdRate = getFiatCache().get(currencyName);
                BigDecimal newLastOrderRate = nonNull(usdRate) ? usdRate : BigDecimal.ZERO;
                return newLastOrderRate.multiply(lastOrderRate);
            case UNDEFINED:
                return BigDecimal.ZERO;
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal calculatePercentChange(CacheOrderStatisticDto statistic) {
        BigDecimal lastOrderRate = statistic.getLastOrderRate();
        BigDecimal predLastOrderRate = nonNull(statistic.getPredLastOrderRate()) ? statistic.getPredLastOrderRate() : BigDecimal.ZERO;
        BigDecimal percentChange = BigDecimal.ZERO;
        if (BigDecimalProcessing.moreThanZero(lastOrderRate) && BigDecimalProcessing.moreThanZero(predLastOrderRate)) {
            percentChange = BigDecimalProcessing.doAction(predLastOrderRate, lastOrderRate, ActionType.PERCENT_GROWTH);
        }
        return percentChange;
    }

    private String getCurrencyPairNameById(Integer currencyPairId) {
        return currencyService.findCurrencyPairById(currencyPairId).getName();
    }
}
