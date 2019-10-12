package me.exrates.service.syndex;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import java.util.List;

import static me.exrates.configurations.CacheConfiguration.*;

@Log4j2(topic = "syndex")
@Component
public class SyndexFrontDataServiceImpl implements SyndexFrontDataService {

    @Autowired
    @Qualifier(SYBEX_COUNTRY_CACHE)
    private Cache countriesCache;
    @Autowired
    @Qualifier(SYBEX_CURRENCY_CACHE)
    private Cache currencyCache;
    @Autowired
    @Qualifier(SYBEX_PAYMENT_SYSTEM_BY_COUNTRY_CACHE)
    private Cache paymentSystemCache;
    @Autowired
    private SyndexClient syndexClient;


    @Override
    public List<SyndexClient.Country> getCountryList() {
        try {
            return countriesCache.get("", () -> syndexClient.getCountryList());
        } catch (EmptyResultDataAccessException | Cache.ValueRetrievalException ex) {
            throw new SyndexCallException("countries list not found", ex);
        }
    }

    @Override
    public List<SyndexClient.Currency> getCurrencyList() {
        try {
            return currencyCache.get("", () -> syndexClient.getCurrencyList());
        } catch (EmptyResultDataAccessException | Cache.ValueRetrievalException ex) {
            throw new SyndexCallException("currencies not found", ex);
        }
    }

    @Override
    public List<SyndexClient.PaymentSystemWrapper> getPaymentSystemList(String countryCode) {
        try {
            return paymentSystemCache.get(countryCode, () -> syndexClient.getPaymentSystems(countryCode));
        } catch (EmptyResultDataAccessException | Cache.ValueRetrievalException ex) {
            throw new SyndexCallException("payment systems not found", ex);
        }
    }

}
