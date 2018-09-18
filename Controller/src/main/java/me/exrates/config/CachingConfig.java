package me.exrates.config;

import net.sf.ehcache.config.CacheConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by ValkSam
 */
@Configuration
@EnableCaching
@PropertySource(value = {"classpath:/cache.properties"})
public class CachingConfig extends CachingConfigurerSupport {

    @Value("${currencyPairStatistics.timeToLiveSeconds}")
    Integer currencyPairStatisticsTimeToLiveSeconds;

    @Value("${orderAccepted.timeToLiveSeconds}")
    Integer orderAcceptedTimeToLiveSeconds;

    @Value("${orderBuy.timeToLiveSeconds}")
    Integer orderBuyTimeToLiveSeconds;

    @Value("${orderSell.timeToLiveSeconds}")
    Integer orderSellTimeToLiveSeconds;

    @Value("${newsBrief.timeToLiveSeconds}")
    Integer newsBriefTimeToLiveSeconds;

    @Value("${singlePairStatistics.timeToLiveSeconds}")
    Integer singlePairStatisticsTimeToLiveSeconds;

    @Value("${candleChart.timeToLiveSeconds}")
    Integer candleChartTimeToLiveSeconds;

    @Value("${twitterTimeline.timeToLiveSeconds}")
    Integer twitterTimeLineTimeToLiveSeconds;

    @Bean
    public net.sf.ehcache.CacheManager ehCacheManager() {
        net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
        /**/
        CacheConfiguration cacheConfiguration = new CacheConfiguration();
        cacheConfiguration.setName("currencyPairStatistics");
        cacheConfiguration.setTimeToLiveSeconds(currencyPairStatisticsTimeToLiveSeconds);
        cacheConfiguration.setMemoryStoreEvictionPolicy("LRU");
        cacheConfiguration.setMaxEntriesLocalHeap(1000);
        config.addCache(cacheConfiguration);
        /**/
        cacheConfiguration = new CacheConfiguration();
        cacheConfiguration.setName("orderAccepted");
        cacheConfiguration.setTimeToLiveSeconds(orderAcceptedTimeToLiveSeconds);
        cacheConfiguration.setMemoryStoreEvictionPolicy("LRU");
        cacheConfiguration.setMaxEntriesLocalHeap(1000);
        config.addCache(cacheConfiguration);
        /**/
        cacheConfiguration = new CacheConfiguration();
        cacheConfiguration.setName("orderBuy");
        cacheConfiguration.setTimeToLiveSeconds(orderBuyTimeToLiveSeconds);
        cacheConfiguration.setMemoryStoreEvictionPolicy("LRU");
        cacheConfiguration.setMaxEntriesLocalHeap(1000);
        config.addCache(cacheConfiguration);
        /**/
        cacheConfiguration = new CacheConfiguration();
        cacheConfiguration.setName("orderSell");
        cacheConfiguration.setTimeToLiveSeconds(orderSellTimeToLiveSeconds);
        cacheConfiguration.setMemoryStoreEvictionPolicy("LRU");
        cacheConfiguration.setMaxEntriesLocalHeap(1000);
        config.addCache(cacheConfiguration);
        /**/
        cacheConfiguration = new CacheConfiguration();
        cacheConfiguration.setName("newsBrief");
        cacheConfiguration.setTimeToLiveSeconds(newsBriefTimeToLiveSeconds);
        cacheConfiguration.setMemoryStoreEvictionPolicy("LRU");
        cacheConfiguration.setMaxEntriesLocalHeap(1000);
        config.addCache(cacheConfiguration);
        /**/
        cacheConfiguration = new CacheConfiguration();
        cacheConfiguration.setName("singlePairStatistics");
        cacheConfiguration.setTimeToLiveSeconds(singlePairStatisticsTimeToLiveSeconds);
        cacheConfiguration.setMemoryStoreEvictionPolicy("LRU");
        cacheConfiguration.setMaxEntriesLocalHeap(1000);
        config.addCache(cacheConfiguration);
        /**/
        cacheConfiguration = new CacheConfiguration();
        cacheConfiguration.setName("candleChart");
        cacheConfiguration.setTimeToLiveSeconds(candleChartTimeToLiveSeconds);
        cacheConfiguration.setMemoryStoreEvictionPolicy("LRU");
        cacheConfiguration.setMaxEntriesLocalHeap(1000);
        config.addCache(cacheConfiguration);
        /**/
        cacheConfiguration = new CacheConfiguration();
        cacheConfiguration.setName("twitter");
        cacheConfiguration.setTimeToLiveSeconds(twitterTimeLineTimeToLiveSeconds);
        cacheConfiguration.setMemoryStoreEvictionPolicy("LRU");
        cacheConfiguration.setMaxEntriesLocalHeap(1000);
        config.addCache(cacheConfiguration);
        /**/
        return net.sf.ehcache.CacheManager.newInstance(config);
    }

    @Bean
    @Override
    public CacheManager cacheManager() {
        return new EhCacheCacheManager(ehCacheManager());
    }

}
