package me.exrates.configurations;

import com.google.common.cache.CacheBuilder;
import org.springframework.cache.Cache;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfiguration {

    public static final String CURRENCY_BY_NAME_CACHE = "cache.currencyByName";
    public static final String MERCHANT_BY_NAME_CACHE = "cache.merchantByName";
    public static final String CURRENCY_PAIR_BY_NAME_CACHE = "cache.currencyPairByName";
    public static final String CURRENCY_PAIR_BY_ID_CACHE = "cache.currencyPairById";
    public static final String CURRENCY_PAIRS_LIST_BY_TYPE_CACHE = "cache.currencyPairListByType";
    public static final String COINMARKETCAP_DATA_CACHE = "cache.coinmarketcapData";
    public static final String SYNDEX_COUNTRY_CACHE = "cache.syndex.country";
    public static final String SYNDEX_CURRENCY_CACHE = "cache.syndex.currency";
    public static final String SYNDEX_PAYMENT_SYSTEM_BY_COUNTRY_CACHE = "cache.syndex.currencyByName";
    public static final String SYNDEX_ORDER_CACHE = "cache.syndex.orders";

    @Bean(CURRENCY_BY_NAME_CACHE)
    public Cache cacheCurrencyByName() {
        return new GuavaCache(CURRENCY_BY_NAME_CACHE, CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build());
    }

    @Bean(MERCHANT_BY_NAME_CACHE)
    public Cache cacheMerchantByName() {
        return new GuavaCache(MERCHANT_BY_NAME_CACHE, CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build());
    }

    @Bean(CURRENCY_PAIR_BY_NAME_CACHE)
    public Cache cacheCurrencyPairByName() {
        return new GuavaCache(CURRENCY_PAIR_BY_NAME_CACHE, CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build());
    }

    @Bean(CURRENCY_PAIR_BY_ID_CACHE)
    public Cache cacheCurrencyPairById() {
        return new GuavaCache(CURRENCY_PAIR_BY_ID_CACHE, CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build());
    }

    @Bean(CURRENCY_PAIRS_LIST_BY_TYPE_CACHE)
    public Cache cacheCurrencyPairListByType() {
        return new GuavaCache(CURRENCY_PAIRS_LIST_BY_TYPE_CACHE, CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build());
    }

    @Bean(COINMARKETCAP_DATA_CACHE)
    public Cache cacheCoinmarketcapData() {
        return new GuavaCache(COINMARKETCAP_DATA_CACHE, CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build());
    }

    @Bean(SYNDEX_COUNTRY_CACHE)
    public Cache cacheSybexCountry() {
        return new GuavaCache(CURRENCY_PAIRS_LIST_BY_TYPE_CACHE, CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build());
    }

    @Bean(SYNDEX_CURRENCY_CACHE)
    public Cache cacheSybexCurrency() {
        return new GuavaCache(CURRENCY_PAIRS_LIST_BY_TYPE_CACHE, CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build());
    }

    @Bean(SYNDEX_PAYMENT_SYSTEM_BY_COUNTRY_CACHE)
    public Cache cachePaymentSystemByCountry() {
        return new GuavaCache(CURRENCY_PAIRS_LIST_BY_TYPE_CACHE, CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build());
    }

    @Bean(SYNDEX_ORDER_CACHE)
    public Cache cacheSyndexOrder() {
        return new GuavaCache(SYNDEX_ORDER_CACHE, CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.SECONDS)
                .build());
    }
}
