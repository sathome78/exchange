package me.exrates.service.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
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
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
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
            .refreshAfterWrite(2, TimeUnit.MINUTES)
            .build(createCacheLoader());
    private Map<String, BigDecimal> fiatCache = new ConcurrentHashMap<>();

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
        final ExOrderStatisticsShortByPairsDto statistic = loadingCache.getUnchecked(currencyPairId);
        ratesRedisRepository.put(statistic);
    }

    @Override
    public void deleteCurrencyPairFromCache(int currencyPairId) {
        final String currencyPairName = currencyService.findCurrencyPairById(currencyPairId).getName();
        ratesRedisRepository.delete(currencyPairName);
    }

    private void initExchangePairsCache() {
        ratesMap.putAll(loadRatesFromDB());
        Map<Integer, ExOrderStatisticsShortByPairsDto> preparedRateItems = getExratesDailyCacheFromDB(null)
                .stream()
                .collect(Collectors.toMap(ExOrderStatisticsShortByPairsDto::getCurrencyPairId, Function.identity()));
        loadingCache.putAll(preparedRateItems);
        ratesRedisRepository.batchUpdate(new ArrayList<>(preparedRateItems.values()));
    }

    private synchronized void setRates(ExOrder order) {
        final BigDecimal lastOrderRate = order.getExRate();
        BigDecimal predLastOrderRate;
        if (ratesMap.containsKey(order.getCurrencyPairId())) {
            predLastOrderRate = new BigDecimal(ratesMap.get(order.getCurrencyPairId()).getLastOrderRate());
        } else {
            String newRate = orderService.getBeforeLastRateForCache(order.getCurrencyPairId()).getPredLastOrderRate();
            predLastOrderRate = new BigDecimal(newRate);
        }
        ExOrderStatisticsShortByPairsDto newSimpleItem = ExOrderStatisticsShortByPairsDto
                .builder()
                .currencyPairId(order.getCurrencyPairId())
                .predLastOrderRate(predLastOrderRate.toPlainString())
                .lastOrderRate(lastOrderRate.toPlainString())
                .build();
        ratesMap.put(order.getCurrencyPairId(), newSimpleItem);
        ExOrderStatisticsShortByPairsDto refreshedItem = refreshItem(order.getCurrencyPairId());
        loadingCache.put(order.getCurrencyPairId(), refreshedItem);
        if (ratesRedisRepository.exist(refreshedItem.getCurrencyPairName())) {
            ratesRedisRepository.update(refreshedItem);
        } else {
            ratesRedisRepository.put(refreshedItem);
        }
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
        log.info("<<CACHE>>: Finished retrieving volumes and last 24 hours data for all currencyPairs ......, Time: {} ms", stopWatch.getTime(TimeUnit.MILLISECONDS));
        log.info("<<CACHE>>: Started calculating price in USD for all currencyPairs ......");
        List<ExOrderStatisticsShortByPairsDto> finishedItems = dtos
                .stream()
                .peek(this::calculatePriceInUsd)
                .collect(Collectors.toList());
        log.info("<<CACHE>>: Finished calculating price in USD for all currencyPairs ......, Time: {} ms", stopWatch.getTime(TimeUnit.MILLISECONDS));
        return finishedItems;
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
            public ExOrderStatisticsShortByPairsDto load(Integer currencyPairId) throws Exception {
                return refreshItem(currencyPairId);
            }

            @Override
            public ListenableFuture<ExOrderStatisticsShortByPairsDto> reload(final Integer currencyPairId,
                                                                             ExOrderStatisticsShortByPairsDto dto) {
                ListenableFutureTask<ExOrderStatisticsShortByPairsDto> command =
                        ListenableFutureTask.create(() -> refreshItem(currencyPairId));
                EXECUTOR.execute(command);
                return command;
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
