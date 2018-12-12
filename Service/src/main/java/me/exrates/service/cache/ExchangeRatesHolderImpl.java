package me.exrates.service.cache;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import me.exrates.dao.OrderDao;
import me.exrates.model.CurrencyPair;
import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.TradeMarket;
import me.exrates.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sound.midi.Track;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Component
public class ExchangeRatesHolderImpl implements ExchangeRatesHolder {

    private Map<Integer, ExOrderStatisticsShortByPairsDto> ratesMap = new ConcurrentHashMap<>();

    private List<CurrencyPair> pairs = new CopyOnWriteArrayList<>();


    private static Integer ETH_USD_ID = 0;
    private static Integer BTC_USD_ID = 0;

    private final OrderDao orderDao;
    private final CurrencyService currencyService;

    @Autowired
    public ExchangeRatesHolderImpl(OrderDao orderDao, CurrencyService currencyService) {
        this.orderDao = orderDao;
        this.currencyService = currencyService;
    }

    @PostConstruct
    private void init() {
        List<ExOrderStatisticsShortByPairsDto> list = orderDao.getOrderStatisticByPairs();
        list.forEach(p-> {
            ratesMap.put(p.getCurrencyPairId(), p);
            if (p.getCurrencyPairName().equalsIgnoreCase("BTC/USD")) {
                BTC_USD_ID = p.getCurrencyPairId();
            } else if (p.getCurrencyPairName().equalsIgnoreCase("ETH/USD")) {
                 ETH_USD_ID = p.getCurrencyPairId();
            }
        });
        pairs = currencyService.getAllCurrencyPairs(CurrencyPairType.ALL);
    }

    @Override
    public void onRatesChange(Integer pairId, BigDecimal rate) {
        setRates(pairId, rate);
    }

    private synchronized void setRates(Integer pairId, BigDecimal rate) {
        if (ratesMap.containsKey(pairId)) {
            ExOrderStatisticsShortByPairsDto dto = ratesMap.get(pairId);
            dto.setPredLastOrderRate(dto.getLastOrderRate());
            dto.setLastOrderRate(rate.toPlainString());
        } else {
            ratesMap.put(pairId, orderDao.getOrderStatisticForSomePairs(Collections.singletonList(pairId)).get(0));
            CurrencyPair newPair = currencyService.findCurrencyPairById(pairId);
            if (!pairs.contains(newPair)) {
                pairs.add(currencyService.findCurrencyPairById(pairId));
            }
        }
    }

    @Override
    public List<ExOrderStatisticsShortByPairsDto> getAllRates() {
        List<ExOrderStatisticsShortByPairsDto> pairs = new ArrayList<>(ratesMap.values());
        pairs.forEach(this::calculatePriceInUSD);
        return pairs;
    }

    @Override
    public List<ExOrderStatisticsShortByPairsDto> getCurrenciesRates(List<Integer> id) {
        if (id == null || id.isEmpty()) {
            return Collections.emptyList();
        }
        List<ExOrderStatisticsShortByPairsDto> result = new ArrayList<>();
        id.forEach(p-> result.add(ratesMap.get(p)));
        return result;
    }

    @Override
    public List<CurrencyPair> getPairs() {
        return pairs;
    }

    @Override
    public Map<Integer, String> getRatesForMarket(TradeMarket market) {
        return getAllRates().stream()
                .filter(p->p.getMarket().equals(market.name()))
                .collect(Collectors.toMap(ExOrderStatisticsShortByPairsDto::getCurrency1Id, ExOrderStatisticsShortByPairsDto::getLastOrderRate, (oldValue, newValue) -> oldValue));
    }

    @Override
    public BigDecimal getBtcUsdRate() {
        ExOrderStatisticsShortByPairsDto dto = ratesMap.getOrDefault(BTC_USD_ID, null);
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
            return "7.77";
        } else  if (ratesMap.containsKey(pairId)) {
            BigDecimal lastRateForBtcUsd = new BigDecimal(ratesMap.get(pairId).getLastOrderRate());
            return lastRateForBtcUsd.multiply(dealPrice).toPlainString();
        }
        return "417";
    }
}
