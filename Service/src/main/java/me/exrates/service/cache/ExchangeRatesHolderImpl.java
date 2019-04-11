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
import java.math.RoundingMode;
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
    private static final String BTC = "BTC";
    private static final String ETH = "ETH";
    private static final String ICO = "ICO";
    private static final String USDT = "USDT";

    private static BigDecimal BTC_USD_RATE = BigDecimal.ZERO;
    private static BigDecimal ETH_USD_RATE = BigDecimal.ZERO;
    private static BigDecimal BTC_USDT_RATE = BigDecimal.ZERO;


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
        FIAT_SCHEDULER.scheduleAtFixedRate(() -> {
            Map<String, BigDecimal> newData = getFiatCacheFromAPI();
            fiatCache = new ConcurrentHashMap<>(newData);
        }, 0, 1, TimeUnit.MINUTES);

        log.info("<<CACHE>>: Start init ExchangeRatesHolder");

        initExchangePairsCache();

        log.info("<<CACHE>>: Finish init ExchangeRatesHolder");

        EXRATES_SCHEDULER.scheduleAtFixedRate(() -> {
            List<ExOrderStatisticsShortByPairsDto> newData = getExratesCacheFromDB(null);
            exratesCache = new CopyOnWriteArrayList<>(newData);
        }, 2, 1, TimeUnit.MINUTES);
    }

    private void initExchangePairsCache() {
        List<ExOrderStatisticsShortByPairsDto> statisticList = getExratesCache(null);

        ratesRedisRepository.batchUpdate(statisticList);
    }

    private List<ExOrderStatisticsShortByPairsDto> getExratesCache(Integer currencyPairId) {
        if (isNotEmpty(exratesCache)) {
            return isNull(currencyPairId)
                    ? exratesCache
                    : exratesCache.stream()
                    .filter(cache -> Objects.equals(currencyPairId, cache.getCurrencyPairId()))
                    .collect(Collectors.toList());
        }
        return getExratesCacheFromDB(currencyPairId);
    }

    private List<ExOrderStatisticsShortByPairsDto> getExratesCacheFromDB(Integer currencyPairId) {
        log.info("<<CACHE>>: Started retrieving last and pred last rates for all currencyPairs ......");
        Map<Integer, ExOrderStatisticsShortByPairsDto> ratesDataForCache = orderService.getRatesDataForCache(currencyPairId)
                .stream()
                .collect(Collectors.toMap(
                        ExOrderStatisticsShortByPairsDto::getCurrencyPairId,
                        Function.identity()
                ));
        log.info("<<CACHE>>: Finished retrieving last and pred last rates for all currencyPairs ......");
        log.info("<<CACHE>>: Started retrieving volumes and last 24 hours data for all currencyPairs ......");
        List<ExOrderStatisticsShortByPairsDto> dtos = orderService.getAllDataForCache(currencyPairId)
                .stream()
                .filter(statistic -> !statistic.isHidden())
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

                    BigDecimal high24hr = new BigDecimal(data.getHigh24hr());
                    if (isZero(high24hr)) {
                        data.setHigh24hr(lastOrderRate);
                    }
                    BigDecimal low24hr = new BigDecimal(data.getLow24hr());
                    if (isZero(low24hr)) {
                        data.setLow24hr(lastOrderRate);
                    }
                    BigDecimal lastOrderRate24hr = new BigDecimal(data.getLastOrderRate24hr());
                    if (isZero(lastOrderRate24hr)) {
                        data.setLastOrderRate24hr(lastOrderRate);
                    }

                    data.setLastOrderRate(lastOrderRate);
                    data.setPredLastOrderRate(predLastOrderRate);
                    data.setLastUpdateCache(DATE_TIME_FORMATTER.format(LocalDateTime.now()));

                    data.setPercentChange(calculatePercentChange(data));
                    setUSDRates(data);
                })
                .collect(Collectors.toList());
        log.info("<<CACHE>>: Finished retrieving volumes and last 24 hours data for all currencyPairs ......");
        log.info("<<CACHE>>: Started calculating price in USD for all currencyPairs ......");
        List<ExOrderStatisticsShortByPairsDto> finishedItems = dtos
                .stream()
                .peek(this::calculatePriceInUsd)
                .collect(Collectors.toList());
        log.info("<<CACHE>>: Finished calculating price in USD for all currencyPairs ......");
        return finishedItems;
    }

    private Map<String, BigDecimal> getFiatCache() {
        if (nonNull(fiatCache) && !fiatCache.isEmpty()) {
            return fiatCache;
        }
        return getFiatCacheFromAPI();
    }

    private Map<String, BigDecimal> getFiatCacheFromAPI() {
        return exchangeApi.getRatesByCurrencyType(FIAT).entrySet().stream()
                .filter(entry -> !USD.equals(entry.getKey()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().getLeft()
                ));
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
            statistic.setPriceInUSD(calculatePriceInUsd(statistic));
            statistic.setVolume(volume.add(amountBase).toPlainString());
            statistic.setCurrencyVolume(currencyVolume.add(amountConvert).toPlainString());
            statistic.setHigh24hr(high24hr.compareTo(lastOrderRate) > 0
                    ? statistic.getHigh24hr()
                    : lastOrderRate.toPlainString());
            statistic.setLow24hr(low24hr.compareTo(lastOrderRate) > 0
                    ? lastOrderRate.toPlainString()
                    : statistic.getLow24hr());
        } else {
            statistic = new ExOrderStatisticsShortByPairsDto();
            statistic.setCurrencyPairId(currencyPairId);
            statistic.setCurrencyPairName(currencyPairName);
            statistic.setMarket(market);
            statistic.setLastOrderRate(lastOrderRate.toPlainString());
            statistic.setPredLastOrderRate(lastOrderRate.toPlainString());
            statistic.setPercentChange(calculatePercentChange(statistic));
            statistic.setPriceInUSD(calculatePriceInUsd(statistic));
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

    @Override
    public void addCurrencyPairToCache(int currencyPairId) {
        final ExOrderStatisticsShortByPairsDto statistic = getExratesCacheFromDB(currencyPairId).get(0);

        ratesRedisRepository.put(statistic);
    }

    @Override
    public void deleteCurrencyPairFromCache(int currencyPairId) {
        final String currencyPairName = getCurrencyPairNameById(currencyPairId);

        ratesRedisRepository.delete(currencyPairName);
    }

    private String calculatePercentChange(ExOrderStatisticsShortByPairsDto statistic) {
        BigDecimal lastOrderRate = new BigDecimal(statistic.getLastOrderRate());
        BigDecimal lastOrderRate24hr = nonNull(statistic.getLastOrderRate24hr())
                ? new BigDecimal(statistic.getLastOrderRate24hr())
                : BigDecimal.ZERO;

        BigDecimal percentChange = BigDecimal.ZERO;
        if (BigDecimalProcessing.moreThanZero(lastOrderRate) && BigDecimalProcessing.moreThanZero(lastOrderRate24hr)) {
            percentChange = BigDecimalProcessing.doAction(lastOrderRate24hr, lastOrderRate, ActionType.PERCENT_GROWTH);
        }
        return percentChange.toPlainString();
    }

    private String getCurrencyPairNameById(Integer currencyPairId) {
        return currencyService.findCurrencyPairById(currencyPairId).getName();
    }

    private String calculatePriceInUsd(ExOrderStatisticsShortByPairsDto item) {
        if (isZero(item.getLastOrderRate())) {
            item.setPriceInUSD("0");
        } else if (item.getMarket().equalsIgnoreCase(USD)
                || item.getCurrencyPairName().endsWith(USD)) {
            item.setPriceInUSD(item.getLastOrderRate());
        } else if (item.getMarket().equalsIgnoreCase(USDT)
                || item.getCurrencyPairName().endsWith(USDT)) {
            BigDecimal usdtToUsd = BigDecimal.ZERO;
            if (!isZero(BTC_USDT_RATE) && !isZero(BTC_USD_RATE))  {
                usdtToUsd = new BigDecimal(item.getLastOrderRate()).divide(BTC_USDT_RATE, RoundingMode.HALF_UP).multiply(BTC_USD_RATE);
            }
            item.setPriceInUSD(usdtToUsd.toPlainString());
        } else if (item.getMarket().equalsIgnoreCase(BTC)
                || item.getCurrencyPairName().endsWith(BTC)) {
            BigDecimal btcToUsd = BigDecimal.ZERO;
            if (!isZero(BTC_USD_RATE)) {
                btcToUsd = new BigDecimal(item.getLastOrderRate()).multiply(BTC_USD_RATE);
            }
            item.setPriceInUSD(btcToUsd.toPlainString());
        } else if (item.getMarket().equalsIgnoreCase(ETH)
                || item.getCurrencyPairName().endsWith(ETH)) {
            BigDecimal btcToEth = BigDecimal.ZERO;
            if (!isZero(BTC_USD_RATE)) {
                btcToEth = new BigDecimal(item.getLastOrderRate()).multiply(ETH_USD_RATE);
            }
            item.setPriceInUSD(btcToEth.toPlainString());
        } else {
            String currencyName = item.getCurrencyPairName().substring(item.getCurrencyPairName().indexOf("/") + 1);
            BigDecimal result = BigDecimal.ZERO;
            BigDecimal rateInUsd = getFiatCache().getOrDefault(currencyName, BigDecimal.ZERO);
            if (!isZero(rateInUsd)) {
                result = rateInUsd;
            }
            item.setPriceInUSD(result.toPlainString());
        }
        return item.getPriceInUSD();
    }

    private void setUSDRates(ExOrderStatisticsShortByPairsDto dto) {
        if (dto.getCurrencyPairName().equalsIgnoreCase("BTC/USD")) {
            BTC_USD_RATE = new BigDecimal(dto.getLastOrderRate());
        } else if (dto.getCurrencyPairName().equalsIgnoreCase("ETH/USD")) {
            ETH_USD_RATE = new BigDecimal(dto.getLastOrderRate());
        } else if (dto.getCurrencyPairName().equalsIgnoreCase("BTC/USDT")) {
            BTC_USDT_RATE = new BigDecimal(dto.getLastOrderRate());
        }
    }

    private boolean isZero(BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) == 0;
    }

    private boolean isZero(String value) {
        return new BigDecimal(value).compareTo(BigDecimal.ZERO) == 0;
    }
}
