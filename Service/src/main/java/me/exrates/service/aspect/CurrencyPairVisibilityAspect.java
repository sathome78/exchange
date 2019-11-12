package me.exrates.service.aspect;

import me.exrates.model.CurrencyPair;
import me.exrates.service.CurrencyService;
import me.exrates.service.cache.ExchangeRatesHolder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Aspect
@Component
public class CurrencyPairVisibilityAspect {

    private final CurrencyService currencyService;
    private final ExchangeRatesHolder exchangeRatesHolder;

    @Autowired
    public CurrencyPairVisibilityAspect(CurrencyService currencyService,
                                        ExchangeRatesHolder exchangeRatesHolder) {
        this.currencyService = currencyService;
        this.exchangeRatesHolder = exchangeRatesHolder;
    }

    @After(value = "@annotation(me.exrates.service.aspect.CheckCurrencyPairVisibility)")
    public void checkCurrencyPairVisibility(JoinPoint point) {
        Object[] args = point.getArgs();
        if (Objects.isNull(args) || args.length == 0) {
            return;
        }
        final Integer currencyPairId = (Integer) args[0];

        CurrencyPair currencyPair = currencyService.findCurrencyPairById(currencyPairId);

        if (currencyPair.isHidden()) {
            exchangeRatesHolder.deleteCurrencyPairFromCache(currencyPair);
        } else {
            exchangeRatesHolder.addCurrencyPairToCache(currencyPair);
        }
    }
}