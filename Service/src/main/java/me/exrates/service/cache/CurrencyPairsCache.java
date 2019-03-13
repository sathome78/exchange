package me.exrates.service.cache;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.CurrencyPair;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/*
todo: caching with redis, cache update*/
@Log4j2
@Component
public class CurrencyPairsCache {

    private final CurrencyService currencyService;

    private Map<Integer, CurrencyPair> pairsMap = new ConcurrentHashMap<>();

    @Autowired
    public CurrencyPairsCache(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @PostConstruct
    private void init() {
        List<CurrencyPair> allPairs = currencyService.getAllCurrencyPairsWithHidden(CurrencyPairType.ALL);
        allPairs.forEach(p -> pairsMap.put(p.getId(), p));
    }

    public CurrencyPair getPairById(Integer id) {
        return pairsMap.get(id);
    }

    public CurrencyPair getPairByName(String pairName) {
        return pairsMap.values()
                .stream()
                .filter(p->p.getName().equalsIgnoreCase(pairName))
                .findFirst()
                .orElse(null);
    }
}
