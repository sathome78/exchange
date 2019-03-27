package me.exrates.service.cache;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.ExOrder;
import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.model.enums.ActionType;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static me.exrates.service.util.CollectionUtil.isEmpty;
import static me.exrates.service.util.CollectionUtil.isNotEmpty;

@Log4j2
@Component
public class ExchangeRatesHolderImpl implements ExchangeRatesHolder {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String DELIMITER = "/";

    private static final String BTC_USD = "BTC/USD";
    private static final String ETH_USD = "ETH/USD";

    private static final String FIAT = "fiat";
    private static final String USD = "USD";

    private Integer BTC_USD_ID;
    private Integer ETH_USD_ID;

    private final ExchangeApi exchangeApi;
    private final OrderService orderService;
    private final CurrencyService currencyService;
    private final ExchangeRatesRedisRepository ratesRedisRepository;

    private List<ExOrderStatisticsShortByPairsDto> exratesCache = new CopyOnWriteArrayList<>();
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
            List<ExOrderStatisticsShortByPairsDto> newData = getExratesCache(null);
            exratesCache = new CopyOnWriteArrayList<>(newData);
        }, 0, 1, TimeUnit.MINUTES);

        FIAT_SCHEDULER.scheduleAtFixedRate(() -> {
            Map<String, BigDecimal> newData = getFiatCache();
            fiatCache = new ConcurrentHashMap<>(newData);
        }, 0, 1, TimeUnit.MINUTES);

        BTC_USD_ID = currencyService.findCurrencyPairIdByName(BTC_USD);
        ETH_USD_ID = currencyService.findCurrencyPairIdByName(ETH_USD);

        log.info("Start init ExchangeRatesHolder");

        initExchangePairsCache();

        log.info("Finish init ExchangeRatesHolder");
    }

    private List<ExOrderStatisticsShortByPairsDto> getExratesCache(Integer currencyPairId) {
        if (isNotEmpty(exratesCache)) {
            return isNull(currencyPairId)
                    ? exratesCache
                    : exratesCache.stream()
                    .filter(cache -> Objects.equals(currencyPairId, cache.getCurrencyPairId()))
                    .collect(Collectors.toList());
        }
        Map<Integer, ExOrderStatisticsShortByPairsDto> ratesDataForCache = orderService.getRatesDataForCache(currencyPairId)
                .stream()
                .collect(Collectors.toMap(
                        ExOrderStatisticsShortByPairsDto::getCurrencyPairId,
                        Function.identity()
                ));
        return orderService.getAllDataForCache(currencyPairId)
                .stream()
                .peek(data -> {
                    final Integer id = data.getCurrencyPairId();
                    ExOrderStatisticsShortByPairsDto rate = ratesDataForCache.get(id);

                    String lastOrderRate;
                    String predLastOrderRate;
                    if (Objects.nonNull(rate)) {
                        lastOrderRate = rate.getLastOrderRate();
                        predLastOrderRate = rate.getPredLastOrderRate();
                    } else {
                        lastOrderRate = BigDecimal.ZERO.toPlainString();
                        predLastOrderRate = BigDecimal.ZERO.toPlainString();
                    }
                    data.setLastOrderRate(lastOrderRate);
                    data.setPredLastOrderRate(predLastOrderRate);
                    data.setLastUpdateCache(DATE_TIME_FORMATTER.format(LocalDateTime.now()));
                })
                .collect(Collectors.toList());
    }

    private Map<String, BigDecimal> getFiatCache() {
        if (nonNull(fiatCache) && !fiatCache.isEmpty()) {
            return fiatCache;
        } else {
            return exchangeApi.getRatesByCurrencyType(FIAT).entrySet().stream()
                    .filter(entry -> !USD.equals(entry.getKey()))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> entry.getValue().getLeft()
                    ));
        }
    }

    private void initExchangePairsCache() {
        List<ExOrderStatisticsShortByPairsDto> statisticList = getExratesCache(null)
                .stream()
                .peek(statistic -> {
                    statistic.setPercentChange(calculatePercentChange(statistic));
                    statistic.setPriceInUSD(calculatePriceInUSD(statistic));
                    statistic.setLastUpdateCache(DATE_TIME_FORMATTER.format(LocalDateTime.now()));
                })
                .collect(Collectors.toList());
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
        final LocalDateTime dateAcception = nonNull(order.getDateAcception()) ? order.getDateAcception() : LocalDateTime.now();

        List<ExOrderStatisticsShortByPairsDto> statisticList = getExratesCache(currencyPairId);

        ExOrderStatisticsShortByPairsDto statistic;
        if (isNotEmpty(statisticList)) {
            statistic = statisticList.get(0);
            final String predLastOrderRate = statistic.getLastOrderRate();
            final BigDecimal volume = new BigDecimal(statistic.getVolume());
            final BigDecimal currencyVolume = new BigDecimal(statistic.getCurrencyVolume());
            final BigDecimal high24hr = new BigDecimal(statistic.getHigh24hr());
            final BigDecimal low24hr = new BigDecimal(statistic.getLow24hr());
            final LocalDateTime lastUpdateCache = LocalDateTime.parse(statistic.getLastUpdateCache(), DATE_TIME_FORMATTER);

            if (dateAcception.isAfter(lastUpdateCache)) {
                statistic.setLastOrderRate(lastOrderRate.toPlainString());
                statistic.setPredLastOrderRate(predLastOrderRate);
            }
            statistic.setPercentChange(calculatePercentChange(statistic));
            statistic.setPriceInUSD(calculatePriceInUSD(statistic));
            statistic.setVolume(volume.add(amountBase).toPlainString());
            statistic.setCurrencyVolume(currencyVolume.add(amountConvert).toPlainString());
            statistic.setHigh24hr(high24hr.compareTo(lastOrderRate) > 0
                    ? statistic.getHigh24hr()
                    : lastOrderRate.toPlainString());
            statistic.setLow24hr(low24hr.compareTo(lastOrderRate) > 0
                    ? lastOrderRate.toPlainString()
                    : statistic.getLow24hr());
            statistic.setLastUpdateCache(DATE_TIME_FORMATTER.format(LocalDateTime.now()));
        } else {
            statistic = new ExOrderStatisticsShortByPairsDto();
            statistic.setCurrencyPairId(currencyPairId);
            statistic.setCurrencyPairName(currencyPairName);
            statistic.setMarket(market);
            statistic.setLastOrderRate(lastOrderRate.toPlainString());
            statistic.setPredLastOrderRate(lastOrderRate.toPlainString());
            statistic.setPercentChange(calculatePercentChange(statistic));
            statistic.setPriceInUSD(calculatePriceInUSD(statistic));
            statistic.setVolume(amountBase.toPlainString());
            statistic.setCurrencyVolume(amountConvert.toPlainString());
            statistic.setHigh24hr(lastOrderRate.toPlainString());
            statistic.setLow24hr(lastOrderRate.toPlainString());
            statistic.setLastUpdateCache(DATE_TIME_FORMATTER.format(LocalDateTime.now()));
        }

        if (ratesRedisRepository.exist(currencyPairName)) {
            ratesRedisRepository.update(statistic);
        } else {
            ratesRedisRepository.put(statistic);
        }
    }

    @Override
    public ExOrderStatisticsShortByPairsDto getOne(Integer currencyPairId) {
        String currencyPairName = getCurrencyPairNameById(currencyPairId);
        return ratesRedisRepository.get(currencyPairName);
    }

    @Override
    public List<ExOrderStatisticsShortByPairsDto> getAllRates() {
        return ratesRedisRepository.getAll();
    }

    @Override
    public List<ExOrderStatisticsShortByPairsDto> getCurrenciesRates(Set<Integer> ids) {
        if (isEmpty(ids)) {
            return Collections.emptyList();
        }
        List<String> names = ids.stream()
                .map(this::getCurrencyPairNameById)
                .collect(Collectors.toList());

        return ratesRedisRepository.getByNames(names);
    }

    @Override
    public Map<String, BigDecimal> getRatesForMarket(TradeMarket market) {
        return getAllRates().stream()
                .filter(statistic -> statistic.getMarket().equals(market.name()))
                .collect(Collectors.toMap(
                        statistic -> statistic.getCurrencyPairName().split(DELIMITER)[0],
                        statistic -> new BigDecimal(statistic.getLastOrderRate()),
                        (oldValue, newValue) -> oldValue));
    }

    @Override
    public BigDecimal getBtcUsdRate() {
        ExOrderStatisticsShortByPairsDto dto = ratesRedisRepository.get(BTC_USD);
        return isNull(dto) ? BigDecimal.ZERO : new BigDecimal(dto.getLastOrderRate());
    }

    private String calculatePriceInUSD(ExOrderStatisticsShortByPairsDto statistic) {
        final String market = statistic.getMarket();
        final BigDecimal lastOrderRate = new BigDecimal(statistic.getLastOrderRate());
        if (lastOrderRate.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.toPlainString();
        }

        switch (Market.of(market)) {
            case USD:
            case USDT:
                return lastOrderRate.toPlainString();
            case BTC:
                ExOrderStatisticsShortByPairsDto btcStatistic = getExratesCache(BTC_USD_ID).get(0);
                BigDecimal btcLastOrderRate = nonNull(btcStatistic)
                        ? new BigDecimal(btcStatistic.getLastOrderRate())
                        : BigDecimal.ZERO;
                return btcLastOrderRate.multiply(lastOrderRate).toPlainString();
            case ETH:
                ExOrderStatisticsShortByPairsDto ethStatistic = getExratesCache(ETH_USD_ID).get(0);
                BigDecimal ethLastOrderRate = nonNull(ethStatistic)
                        ? new BigDecimal(ethStatistic.getLastOrderRate())
                        : BigDecimal.ZERO;
                return ethLastOrderRate.multiply(lastOrderRate).toPlainString();
            case FIAT:
                final String currencyName = statistic.getCurrencyPairName().split(DELIMITER)[1];
                BigDecimal usdRate = getFiatCache().get(currencyName);
                BigDecimal newLastOrderRate = nonNull(usdRate) ? usdRate : BigDecimal.ZERO;
                return newLastOrderRate.multiply(lastOrderRate).toPlainString();
            case UNDEFINED:
                return BigDecimal.ZERO.toPlainString();
        }
        return BigDecimal.ZERO.toPlainString();
    }

    private String calculatePercentChange(ExOrderStatisticsShortByPairsDto statistic) {
        BigDecimal lastOrderRate = new BigDecimal(statistic.getLastOrderRate());
        BigDecimal predLastOrderRate = nonNull(statistic.getPredLastOrderRate())
                ? new BigDecimal(statistic.getPredLastOrderRate())
                : BigDecimal.ZERO;

        BigDecimal percentChange = BigDecimal.ZERO;
        if (BigDecimalProcessing.moreThanZero(lastOrderRate) && BigDecimalProcessing.moreThanZero(predLastOrderRate)) {
            percentChange = BigDecimalProcessing.doAction(predLastOrderRate, lastOrderRate, ActionType.PERCENT_GROWTH);
        }
        return percentChange.toPlainString();
    }

    private String getCurrencyPairNameById(Integer currencyPairId) {
        return currencyService.findCurrencyPairById(currencyPairId).getName();
    }
}
