package me.exrates.service.cache;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.OrderDao;
import me.exrates.model.ExOrder;
import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.model.enums.ActionType;
import me.exrates.model.enums.TradeMarket;
import me.exrates.model.util.BigDecimalProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Component
public class ExchangeRatesHolderImpl implements ExchangeRatesHolder {
    private final OrderDao orderDao;
    private final ExchangeRatesRedisRepository ratesRedisRepository;

    private static Integer ETH_USD_ID = 0;
    private static Integer BTC_USD_ID = 0;

    @Autowired
    public ExchangeRatesHolderImpl(OrderDao orderDao,
                                   ExchangeRatesRedisRepository ratesRedisRepository) {
        this.orderDao = orderDao;
        this.ratesRedisRepository = ratesRedisRepository;
    }

    @PostConstruct
    private void init() {
        log.info("Start init ExchangeRatesHolder");
        List<ExOrderStatisticsShortByPairsDto> list = orderDao.getOrderStatisticByPairs()
                .stream()
                .peek(o -> {
                    processPercentChange(o);
                    calculateCurrencyVolume(o);
                    calculatePriceInUSD(o);
                    if (o.getCurrencyPairName().equalsIgnoreCase("BTC/USD")) {
                        BTC_USD_ID = o.getCurrencyPairId();
                    } else if (o.getCurrencyPairName().equalsIgnoreCase("ETH/USD")) {
                        ETH_USD_ID = o.getCurrencyPairId();
                    }
                }).collect(Collectors.toList());
        ratesRedisRepository.batchUpdate(list);
        log.info("Finish init ExchangeRatesHolder");
    }

    @Override
    public void onRatesChange(ExOrder exOrder) {
        setRates(exOrder.getCurrencyPairId(), exOrder.getExRate());
    }

    @Override
    public ExOrderStatisticsShortByPairsDto getOne(Integer id) {
        return ratesRedisRepository.get(id);
    }

    private synchronized void setRates(Integer pairId, BigDecimal rate) {
        if (ratesRedisRepository.exist(pairId)) {
            ExOrderStatisticsShortByPairsDto dto = ratesRedisRepository.get(pairId);
            dto.setPredLastOrderRate(dto.getLastOrderRate());
            dto.setLastOrderRate(rate.toPlainString());
            calculatePriceInUSD(dto);
            ratesRedisRepository.update(dto);
        } else {
            ratesRedisRepository.put(orderDao.getOrderStatisticForSomePairs(Collections.singletonList(pairId)).get(0));
        }
    }

    @Override
    public List<ExOrderStatisticsShortByPairsDto> getAllRates() {
        return ratesRedisRepository.getAll();
    }

    @Override
    public List<ExOrderStatisticsShortByPairsDto> getCurrenciesRates(List<Integer> id) {
        if (id == null || id.isEmpty()) {
            return Collections.emptyList();
        }
        return ratesRedisRepository.getByListId(id);
    }

    @Override
    public Map<Integer, String> getRatesForMarket(TradeMarket market) {
        return getAllRates().stream()
                .filter(p -> p.getMarket().equals(market.name()))
                .collect(Collectors.toMap(ExOrderStatisticsShortByPairsDto::getCurrency1Id, ExOrderStatisticsShortByPairsDto::getLastOrderRate, (oldValue, newValue) -> oldValue));
    }

    @Override
    public BigDecimal getBtcUsdRate() {
        ExOrderStatisticsShortByPairsDto dto = ratesRedisRepository.get(BTC_USD_ID);
        return dto == null ? BigDecimal.ZERO : new BigDecimal(dto.getLastOrderRate());
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
            return null;
        } else if (ratesRedisRepository.exist(pairId)) {
            String lastOrderRate = ratesRedisRepository.get(pairId).getLastOrderRate();
            BigDecimal lastRate = new BigDecimal(lastOrderRate);
            return lastRate.multiply(dealPrice).toPlainString();
        }
        return null;
    }

    private void processPercentChange(ExOrderStatisticsShortByPairsDto o) {
        BigDecimal lastExrate = new BigDecimal(o.getLastOrderRate());
        BigDecimal predLast = o.getPredLastOrderRate() != null ? new BigDecimal(o.getPredLastOrderRate()) : BigDecimal.ZERO;
        BigDecimal percentChange = BigDecimal.ZERO;
        if (BigDecimalProcessing.moreThanZero(lastExrate) && BigDecimalProcessing.moreThanZero(predLast)) {
            percentChange = BigDecimalProcessing.doAction(predLast, lastExrate, ActionType.PERCENT_GROWTH);
        }
        o.setPercentChange(BigDecimalProcessing.formatLocaleFixedDecimal(percentChange, Locale.ENGLISH, 2));
    }

    private void calculateCurrencyVolume(ExOrderStatisticsShortByPairsDto dto) {
        BigDecimal lastOrderRate = new BigDecimal(dto.getLastOrderRate());
        BigDecimal volume = new BigDecimal(dto.getVolume());
        BigDecimal currencyVolume = BigDecimalProcessing.doAction(volume, lastOrderRate, ActionType.MULTIPLY);
        dto.setCurrencyVolume(currencyVolume.toPlainString());
    }
}
