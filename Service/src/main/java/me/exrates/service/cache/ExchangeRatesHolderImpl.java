package me.exrates.service.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.ExOrder;
import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.model.enums.ActionType;
import me.exrates.model.enums.TradeMarket;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.service.CurrencyService;
import me.exrates.service.OrderService;
import me.exrates.service.api.ExchangeApi;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
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

    private Map<Integer, ExOrderStatisticsShortByPairsDto> ratesMap = new ConcurrentHashMap<>();
    private final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private LoadingCache<Integer, ExOrderStatisticsShortByPairsDto> loadingCache = CacheBuilder.newBuilder()
            .refreshAfterWrite(1, TimeUnit.HOURS)
            .build(createCacheLoader());
    private Map<String, BigDecimal> fiatCache = new ConcurrentHashMap<>();
    private Map<Integer, Object> locks = new ConcurrentHashMap<>();
    private final Object safeSync = new Object();

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

        StopWatch stopWatch = StopWatch.createStarted();
        log.info("<<CACHE>>: Start init ExchangeRatesHolder");

        initExchangePairsCache();

        log.info("<<CACHE>>: Finish init ExchangeRatesHolder, Time: {} ms", stopWatch.getTime(TimeUnit.MILLISECONDS));
    }

    @Override
    public void onRatesChange(ExOrder exOrder) {
        setRates(exOrder);
    }

    @Override
    public ExOrderStatisticsShortByPairsDto getOne(Integer currencyPairId) {
        return loadingCache.asMap().get(currencyPairId);
    }

    @Override
    public List<ExOrderStatisticsShortByPairsDto> getAllRates() {
        return new ArrayList<>(loadingCache.asMap().values());
    }

    @Override
    public List<ExOrderStatisticsShortByPairsDto> getCurrenciesRates(Set<Integer> ids) {
        if (isEmpty(ids)) {
            return Collections.emptyList();
        }
        Set<String> names = ids.stream()
                .map(p -> currencyService.findCurrencyPairById(p).getName())
                .collect(Collectors.toSet());

        return loadingCache.asMap().values()
                .stream()
                .filter(i -> isNotEmpty(names) && names.contains(i.getCurrencyPairName()))
                .collect(Collectors.toList());
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
        return isNull(dto) ? BTC_USD_RATE : new BigDecimal(dto.getLastOrderRate());
    }

    @Override
    public void addCurrencyPairToCache(int currencyPairId) {
        try {
            ExOrderStatisticsShortByPairsDto statistic = loadingCache.get(currencyPairId);
            ratesRedisRepository.put(statistic);
        } catch (ExecutionException e) {
            log.error("Failed to load currency pair #" + currencyPairId + " from loading cache as", e);
        }
    }

    @Override
    public void deleteCurrencyPairFromCache(int currencyPairId) {
        final String currencyPairName = currencyService.findCurrencyPairById(currencyPairId).getName();
        ratesRedisRepository.delete(currencyPairName);
        ratesMap.remove(currencyPairId);
        loadingCache.invalidate(currencyPairId);
    }

    private void initExchangePairsCache() {
        ratesMap.putAll(loadRatesFromDB());
        Map<Integer, ExOrderStatisticsShortByPairsDto> preparedRateItems = getExratesDailyCacheFromDB(null)
                .stream()
                .collect(Collectors.toMap(ExOrderStatisticsShortByPairsDto::getCurrencyPairId, Function.identity()));
        loadingCache.putAll(preparedRateItems);
        ratesRedisRepository.batchUpdate(new ArrayList<>(preparedRateItems.values()));
    }

    private void setRates(ExOrder order) {
        int currencyPairId = order.getCurrencyPairId();
        synchronized (getRatesMapSyncSynchronizerSafe(currencyPairId)) {
            final BigDecimal lastOrderRate = order.getExRate();
            BigDecimal predLastOrderRate;
            if (ratesMap.containsKey(currencyPairId)) {
                predLastOrderRate = new BigDecimal(ratesMap.get(currencyPairId).getLastOrderRate());
            } else {
                log.info("<<CACHE>>: Started retrieving SINGLE pred last rate for currencyPairId: " + currencyPairId);
                String newRate = orderService.getBeforeLastRateForCache(currencyPairId).getPredLastOrderRate();
                log.info("<<CACHE>>: Finished retrieving SINGLE pred last rate for currencyPairId: " + currencyPairId);
                predLastOrderRate = new BigDecimal(newRate);
            }

            ExOrderStatisticsShortByPairsDto cachedItem = loadingCache.getUnchecked(currencyPairId);
            if (Objects.isNull(cachedItem)) {
                cachedItem = new ExOrderStatisticsShortByPairsDto();
            }
            cachedItem.setPriceInUSD(calculatePriceInUsd(cachedItem));
            cachedItem.setLastOrderRate(lastOrderRate.toPlainString());
            cachedItem.setPredLastOrderRate(predLastOrderRate.toPlainString());
            cachedItem.setUpdated(LocalDateTime.now());
            cachedItem.setLastUpdateCache(DATE_TIME_FORMATTER.format(LocalDateTime.now()));
            setDailyData(cachedItem, lastOrderRate.toPlainString());

            String volumeString = cachedItem.getVolume();
            BigDecimal volume = StringUtils.isEmpty(volumeString)
                    ? BigDecimal.ZERO
                    : new BigDecimal(volumeString);
            cachedItem.setVolume(volume.add(order.getAmountBase()).toPlainString());

            String currencyVolumeString = cachedItem.getCurrencyVolume();
            BigDecimal currencyVolume = StringUtils.isEmpty(currencyVolumeString)
                    ? BigDecimal.ZERO
                    : new BigDecimal(currencyVolumeString);
            cachedItem.setCurrencyVolume(currencyVolume.add(order.getAmountConvert()).toPlainString());

            if (ratesMap.containsKey(currencyPairId)) {
                ratesMap.replace(currencyPairId, cachedItem);
            } else {
                ratesMap.putIfAbsent(currencyPairId, cachedItem);
            }
            loadingCache.put(currencyPairId, cachedItem);
            if (ratesRedisRepository.exist(cachedItem.getCurrencyPairName())) {
                ratesRedisRepository.update(cachedItem);
            } else {
                ratesRedisRepository.put(cachedItem);
            }
            log.info("<<CACHE>>: Updated exchange rate for currency pair " + cachedItem.getCurrencyPairName() + " to " + cachedItem.getLastOrderRate());
        }
    }

    private Object getRatesMapSyncSynchronizerSafe(Integer pairId) {
        if (!locks.containsKey(pairId)) {
            synchronized (safeSync) {
                locks.putIfAbsent(pairId, new Object());
            }
        }
        return locks.get(pairId);
    }

    private Map<Integer, ExOrderStatisticsShortByPairsDto> loadRatesFromDB() {
        StopWatch stopWatch = StopWatch.createStarted();
        log.info("<<CACHE>>: Started retrieving last and pred last rates for all currencyPairs ......");
        Map<Integer, ExOrderStatisticsShortByPairsDto> rates = orderService.getRatesDataForCache(null)
                .stream()
                .collect(Collectors.toMap(
                        ExOrderStatisticsShortByPairsDto::getCurrencyPairId,
                        Function.identity()
                ));
        log.info("<<CACHE>>: Finished retrieving last and pred last rates for all currencyPairs ......, Time: {} ms", stopWatch.getTime(TimeUnit.MILLISECONDS));
        return rates;
    }

    private List<ExOrderStatisticsShortByPairsDto> getExratesDailyCacheFromDB(Integer currencyPairId) {
        StopWatch stopWatch = StopWatch.createStarted();
        log.info("<<CACHE>>: Started retrieving volumes and last 24 hours data for all currencyPairs ......");
        List<ExOrderStatisticsShortByPairsDto> dtos = orderService.getAllDataForCache(currencyPairId)
                .stream()
                .filter(statistic -> !statistic.isHidden())
                .peek(data -> {
                    final Integer id = data.getCurrencyPairId();

                    String lastOrderRate;
                    String predLastOrderRate;

                    if (ratesMap.containsKey(id)) {
                        ExOrderStatisticsShortByPairsDto rate = ratesMap.get(id);
                        lastOrderRate = rate.getLastOrderRate();
                        predLastOrderRate = rate.getPredLastOrderRate();
                    } else {
                        lastOrderRate = BigDecimal.ZERO.toPlainString();
                        predLastOrderRate = BigDecimal.ZERO.toPlainString();
                        ExOrderStatisticsShortByPairsDto newItem = ExOrderStatisticsShortByPairsDto.builder()
                                .currencyPairId(id)
                                .lastOrderRate(lastOrderRate)
                                .predLastOrderRate(predLastOrderRate)
                                .build();
                        ratesMap.put(id, newItem);
                    }

                    setDailyData(data, lastOrderRate);

                    data.setLastOrderRate(lastOrderRate);
                    data.setPredLastOrderRate(predLastOrderRate);
                    data.setLastUpdateCache(DATE_TIME_FORMATTER.format(LocalDateTime.now()));
                    setUSDRates(data);
                })
                .collect(Collectors.toList());
        log.info("<<CACHE>>: Finished retrieving volumes and last 24 hours data for all currencyPairs ......, Time: {} ms", stopWatch.getTime(TimeUnit.MILLISECONDS));
        log.info("<<CACHE>>: Started calculating price in USD for all currencyPairs ......");
        List<ExOrderStatisticsShortByPairsDto> finishedItems = dtos
                .stream()
                .peek(this::calculatePriceInUsd)
                .collect(Collectors.toList());
        log.info("<<CACHE>>: Finished calculating price in USD for all currencyPairs ......, Time: {} ms", stopWatch.getTime(TimeUnit.MILLISECONDS));
        return finishedItems;
    }

    private void setDailyData(ExOrderStatisticsShortByPairsDto data, String lastOrderRateValue) {
        BigDecimal lastOrderRate = new BigDecimal(lastOrderRateValue);
        BigDecimal high24hr = new BigDecimal(data.getHigh24hr());
        if (isZero(high24hr) || lastOrderRate.compareTo(high24hr) > 0) {
            data.setHigh24hr(lastOrderRate.toPlainString());
        }
        BigDecimal low24hr = new BigDecimal(data.getLow24hr());
        if (isZero(low24hr) || lastOrderRate.compareTo(high24hr) < 0) {
            data.setLow24hr(lastOrderRate.toPlainString());
        }
        calculatePercentChange(data);
    }

    private void calculatePercentChange(ExOrderStatisticsShortByPairsDto statistic) {
        BigDecimal lastOrderRate = statistic.getLastOrderRate() == null ? BigDecimal.ZERO : new BigDecimal(statistic.getLastOrderRate());
        BigDecimal lastOrderRate24hr = nonNull(statistic.getLastOrderRate24hr())
                ? new BigDecimal(statistic.getLastOrderRate24hr())
                : BigDecimal.ZERO;

        BigDecimal percentChange = BigDecimal.ZERO;
        BigDecimal valueChange = BigDecimal.ZERO;
        if (BigDecimalProcessing.moreThanZero(lastOrderRate) && BigDecimalProcessing.moreThanZero(lastOrderRate24hr)) {
            percentChange = BigDecimalProcessing.doAction(lastOrderRate24hr, lastOrderRate, ActionType.PERCENT_GROWTH);
            valueChange = BigDecimalProcessing.doAction(lastOrderRate24hr, lastOrderRate, ActionType.SUBTRACT);
        }
        if (BigDecimalProcessing.moreThanZero(lastOrderRate) && lastOrderRate24hr.compareTo(BigDecimal.ZERO) == 0) {
            percentChange = new BigDecimal(100);
            valueChange = lastOrderRate;
        }
        statistic.setPercentChange(percentChange.toPlainString());
        statistic.setValueChange(valueChange.toPlainString());
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
            if (!isZero(BTC_USDT_RATE) && !isZero(BTC_USD_RATE)) {
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

    private CacheLoader<Integer, ExOrderStatisticsShortByPairsDto> createCacheLoader() {
        return new CacheLoader<Integer, ExOrderStatisticsShortByPairsDto>() {
            @Override
            public ExOrderStatisticsShortByPairsDto load(Integer currencyPairId) {
                return refreshItem(currencyPairId);
            }

            @Override
            public ListenableFuture<ExOrderStatisticsShortByPairsDto> reload(final Integer currencyPairId,
                                                                             ExOrderStatisticsShortByPairsDto dto) {
                if (dto.getUpdated() == null || dto.getUpdated().isBefore(LocalDateTime.now().minus(1, ChronoUnit.DAYS))) {
                    return Futures.immediateFuture(dto);
                }
                StopWatch timer = new StopWatch();
                log.info("<<CACHE>>: Start refreshing (async) cache item for id: " + currencyPairId);
                ListenableFutureTask<ExOrderStatisticsShortByPairsDto> command =
                        ListenableFutureTask.create(() -> refreshItem(currencyPairId));
                EXECUTOR.execute(command);
                String message = String.format("<<CACHE>>: Finished refreshed (async)  cache item for id: %d, timer: %d s",
                        currencyPairId, timer.getTime(TimeUnit.SECONDS));
                log.info(message);
                return command;
            }

            @Override
            public Map<Integer, ExOrderStatisticsShortByPairsDto> loadAll(Iterable<? extends Integer> keys) throws Exception {
                StopWatch timer = new StopWatch();
                log.info("<<CACHE>>: Start loading all cache items");
                Map<Integer, ExOrderStatisticsShortByPairsDto> result = ratesRedisRepository.getAll()
                        .stream()
                        .collect(Collectors.toMap(ExOrderStatisticsShortByPairsDto::getCurrencyPairId, Function.identity()));
                log.info("<<CACHE>>: Finished loading all cache items in " + timer.getTime(TimeUnit.SECONDS) + " s.");
                return result;
            }
        };
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
                        Map.Entry::getValue
                ));
    }

    private ExOrderStatisticsShortByPairsDto refreshItem(Integer currencyPairId) {
        if (!ratesMap.containsKey(currencyPairId)) {
            Optional<ExOrderStatisticsShortByPairsDto> found = orderService.getRatesDataForCache(currencyPairId)
                    .stream()
                    .findFirst();
            found.ifPresent(item -> ratesMap.put(item.getCurrencyPairId(), item));
        }
        ExOrderStatisticsShortByPairsDto refreshedItem = getExratesDailyCacheFromDB(currencyPairId)
                .stream()
                .findFirst()
                .orElseThrow(() -> {
                    String message = "Filed to refresh rates cache item with id: " + currencyPairId;
                    log.warn(message);
                    return new RuntimeException(message);
                });
        setUSDRates(refreshedItem);
        ratesRedisRepository.put(refreshedItem);
        return refreshedItem;
    }

    private boolean isZero(BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) == 0;
    }

    private boolean isZero(String value) {
        return new BigDecimal(value).compareTo(BigDecimal.ZERO) == 0;
    }
}
