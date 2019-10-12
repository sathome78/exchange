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
    public static final String SYBEX_COUNTRY_CACHE = "cache.sybex.country";
    public static final String SYBEX_CURRENCY_CACHE = "cache.sybex.currency";
    public static final String SYBEX_PAYMENT_SYSTEM_BY_COUNTRY_CACHE = "cache.sybex.currencyByName";

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

    @Bean(SYBEX_COUNTRY_CACHE)
    public Cache cacheSybexCountry() {
        return new GuavaCache(CURRENCY_PAIRS_LIST_BY_TYPE_CACHE, CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build());
    }

    @Bean(SYBEX_CURRENCY_CACHE)
    public Cache cacheSybexCurrency() {
        return new GuavaCache(CURRENCY_PAIRS_LIST_BY_TYPE_CACHE, CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build());
    }

    @Bean(SYBEX_PAYMENT_SYSTEM_BY_COUNTRY_CACHE)
    public Cache cachePaymentSystemByCountry() {
        return new GuavaCache(CURRENCY_PAIRS_LIST_BY_TYPE_CACHE, CacheBuilder.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .build());
    }
}
