package me.exrates.service.cache;

import com.antkorwin.xsync.XSync;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.CurrencyPair;
import me.exrates.model.ExOrder;
import me.exrates.model.chart.ExchangeRatesDto;
import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.model.enums.ActionType;
import me.exrates.model.enums.TradeMarket;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.service.api.ChartApi;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import static me.exrates.service.util.CollectionUtil.isNotEmpty;

@Log4j2
@Component
public class ExchangeRatesHolderImpl implements ExchangeRatesHolder {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String DELIMITER = "/";

    private static final String BTC_USD = "BTC/USD";

    private static final String FIAT = "fiat";
    private static final String USD = "USD";
    private static final String BTC = "BTC";
    private static final String ETH = "ETH";
    private static final String USDT = "USDT";

    private static BigDecimal BTC_USD_RATE = BigDecimal.ZERO;
    private static BigDecimal ETH_USD_RATE = BigDecimal.ZERO;
    private static BigDecimal BTC_USDT_RATE = BigDecimal.ZERO;

    private final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    private final XSync<Integer> CURRENCY_PAIR_SYNC = new XSync<>();

    private final ExchangeApi exchangeApi;
    private final ChartApi chartApi;

    private Map<String, BigDecimal> fiatCache = new ConcurrentHashMap<>();
    private Map<String, ExOrderStatisticsShortByPairsDto> ratesCache = new ConcurrentHashMap<>();

    private LoadingCache<String, ExOrderStatisticsShortByPairsDto> loadingCache = CacheBuilder.newBuilder()
            .refreshAfterWrite(5, TimeUnit.MINUTES)
            .build(createCacheLoader());

    private static final ScheduledExecutorService FIAT_SCHEDULER = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    public ExchangeRatesHolderImpl(ExchangeApi exchangeApi,
                                   ChartApi chartApi) {
        this.exchangeApi = exchangeApi;
        this.chartApi = chartApi;
    }

    @PostConstruct
    private void init() {
        FIAT_SCHEDULER.scheduleAtFixedRate(() -> fiatCache.putAll(getFiatCacheFromAPI()), 0, 1, TimeUnit.MINUTES);

        StopWatch stopWatch = StopWatch.createStarted();
        log.info("<<CACHE>>: Start init ExchangeRatesHolder");

        initExchangePairsCache();

        log.info("<<CACHE>>: Finish init ExchangeRatesHolder, Time: {} ms", stopWatch.getTime(TimeUnit.MILLISECONDS));
    }

    @Override
    public void onRatesChange(ExOrder order) {
        CURRENCY_PAIR_SYNC.execute(order.getCurrencyPairId(), () -> setRatesSync(order));
    }

    @Override
    public ExOrderStatisticsShortByPairsDto getOne(String currencyPairName) {
        return loadingCache.asMap().get(currencyPairName);
    }

    @Override
    public List<ExOrderStatisticsShortByPairsDto> getAllRates() {
        return new ArrayList<>(loadingCache.asMap().values());
    }

    @Override
    public List<ExOrderStatisticsShortByPairsDto> getCurrenciesRates(Set<String> names) {
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
        ExOrderStatisticsShortByPairsDto statistic = loadingCache.getUnchecked(BTC_USD);

        return isNull(statistic) ? BTC_USD_RATE : new BigDecimal(statistic.getLastOrderRate());
    }

    @Override
    public void addCurrencyPairToCache(CurrencyPair currencyPair) {
        try {
            ExOrderStatisticsShortByPairsDto statistic = loadingCache.get(currencyPair.getName());
            ratesCache.put(currencyPair.getName(), statistic);
        } catch (ExecutionException ex) {
            log.error("Failed to load currency pair: {} from loading cache as", currencyPair.getId(), ex);
        }
    }

    @Override
    public void deleteCurrencyPairFromCache(CurrencyPair currencyPair) {
        ratesCache.remove(currencyPair.getName());
        loadingCache.invalidate(currencyPair.getId());
    }

    private void initExchangePairsCache() {
        Map<String, ExOrderStatisticsShortByPairsDto> statisticMap = chartApi.getExchangeRatesData(null, "D")
                .stream()
                .map(ExchangeRatesDto::transform)
                .peek(this::calculatePriceInUsd)
                .collect(Collectors.toMap(
                        ExOrderStatisticsShortByPairsDto::getCurrencyPairName,
                        Function.identity()));

        statisticMap.values().forEach(this::setUSDRates);

        ratesCache.putAll(statisticMap);
        loadingCache.putAll(statisticMap);
    }

    private void setRatesSync(ExOrder order) {
        final String currencyPairName = order.getCurrencyPair().getName();
        final BigDecimal lastOrderRate = order.getExRate();

        BigDecimal predLastOrderRate;
        if (ratesCache.containsKey(currencyPairName)) {
            predLastOrderRate = new BigDecimal(ratesCache.get(currencyPairName).getLastOrderRate());
        } else {
            predLastOrderRate = lastOrderRate;
        }

        ExOrderStatisticsShortByPairsDto statistic = loadingCache.getUnchecked(currencyPairName);
        if (Objects.isNull(statistic)) {
            statistic = new ExOrderStatisticsShortByPairsDto();
            statistic.setCurrencyPairId(order.getCurrencyPair().getId());
            statistic.setCurrencyPairName(order.getCurrencyPair().getName());
            statistic.setMarket(order.getCurrencyPair().getMarket());
            statistic.setType(order.getCurrencyPair().getPairType());
        }
        statistic.setLastOrderRate(lastOrderRate.toPlainString());
        statistic.setPredLastOrderRate(predLastOrderRate.toPlainString());
        statistic.setPriceInUSD(calculatePriceInUsd(statistic));
        statistic.setLastUpdateCache(DATE_TIME_FORMATTER.format(LocalDateTime.now()));
        setDailyData(statistic, lastOrderRate.toPlainString());

        String volumeString = statistic.getVolume();
        BigDecimal volume = StringUtils.isEmpty(volumeString)
                ? BigDecimal.ZERO
                : new BigDecimal(volumeString);
        statistic.setVolume(volume.add(order.getAmountBase()).toPlainString());

        String currencyVolumeString = statistic.getCurrencyVolume();
        BigDecimal currencyVolume = StringUtils.isEmpty(currencyVolumeString)
                ? BigDecimal.ZERO
                : new BigDecimal(currencyVolumeString);
        statistic.setCurrencyVolume(currencyVolume.add(order.getAmountConvert()).toPlainString());

        if (ratesCache.containsKey(currencyPairName)) {
            ratesCache.replace(currencyPairName, statistic);
        } else {
            ratesCache.putIfAbsent(currencyPairName, statistic);
        }
        loadingCache.put(currencyPairName, statistic);

        log.info("<<CACHE>>: Updated exchange rate for currency pair: {} to: {}", currencyPairName, lastOrderRate);
    }

    private void setDailyData(ExOrderStatisticsShortByPairsDto data, String lastOrderRateValue) {
        BigDecimal lastOrderRate = new BigDecimal(lastOrderRateValue);
        BigDecimal high24hr = new BigDecimal(data.getHigh24hr());
        if (isZero(high24hr) || lastOrderRate.compareTo(high24hr) > 0) {
            data.setHigh24hr(lastOrderRate.toPlainString());
        }
        BigDecimal low24hr = new BigDecimal(data.getLow24hr());
        if (isZero(low24hr) || lastOrderRate.compareTo(low24hr) < 0) {
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
            item.setPriceInUSD(BigDecimal.ZERO.toPlainString());
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
            if (!isZero(ETH_USD_RATE)) {
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

    private CacheLoader<String, ExOrderStatisticsShortByPairsDto> createCacheLoader() {
        return new CacheLoader<String, ExOrderStatisticsShortByPairsDto>() {
            @Override
            public ExOrderStatisticsShortByPairsDto load(String currencyPairName) {
                return refreshItem(currencyPairName);
            }

            @Override
            public ListenableFuture<ExOrderStatisticsShortByPairsDto> reload(final String currencyPairName,
                                                                             ExOrderStatisticsShortByPairsDto statistic) {
                StopWatch timer = new StopWatch();
                log.info("<<CACHE>>: Start refreshing (async) cache item for pair: {}", currencyPairName);

                ListenableFutureTask<ExOrderStatisticsShortByPairsDto> command =
                        ListenableFutureTask.create(() -> refreshItem(currencyPairName));
                EXECUTOR.execute(command);

                String message = String.format("<<CACHE>>: Finished refreshed (async) cache item for pair: %s, Time: %d ms",
                        currencyPairName, timer.getTime(TimeUnit.SECONDS));
                log.info(message);
                return command;
            }
        };
    }

    private ExOrderStatisticsShortByPairsDto refreshItem(String currencyPairName) {
        ExOrderStatisticsShortByPairsDto statistic = chartApi.getExchangeRatesData(currencyPairName, "D")
                .stream()
                .map(ExchangeRatesDto::transform)
                .peek(this::calculatePriceInUsd)
                .findFirst()
                .orElseThrow(() -> {
                    String message = "Filed to refresh rates cache item with pair: " + currencyPairName;
                    log.warn(message);
                    return new RuntimeException(message);
                });

        setUSDRates(statistic);

        ratesCache.put(currencyPairName, statistic);
        return statistic;
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

    private boolean isZero(BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) == 0;
    }

    private boolean isZero(String value) {
        return new BigDecimal(value).compareTo(BigDecimal.ZERO) == 0;
    }
}