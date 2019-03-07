package me.exrates.service.cache;

import me.exrates.model.dto.CacheOrderStatisticDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Repository
public class ExchangeRatesRedisRepository {

    private static final String key = "exchange_rates_holder";

    private final HashOperations<String, Object, Object> ops;

    @Autowired
    public ExchangeRatesRedisRepository(RedisTemplate<String, Object> redisTemplate) {
        redisTemplate.setHashKeySerializer(new GenericToStringSerializer<>(Integer.class));
        redisTemplate.delete(key);
        ops = redisTemplate.opsForHash();
    }

    public void put(CacheOrderStatisticDto statistic) {
        ops.put(key, statistic.getCurrencyPairName(), statistic);
    }

    public CacheOrderStatisticDto get(String currencyPairName) {
        return (CacheOrderStatisticDto) ops.get(key, currencyPairName);
    }

    public boolean exist(String currencyPairName) {
        return ops.hasKey(key, currencyPairName);
    }

    public List<CacheOrderStatisticDto> getAll() {
        return ops.values(key).stream()
                .map(o -> (CacheOrderStatisticDto) o)
                .collect(toList());
    }

    public List<CacheOrderStatisticDto> getByNames(List<String> names) {
        return ops.multiGet(key, Collections.unmodifiableCollection(names))
                .stream()
                .map(o -> (CacheOrderStatisticDto) o)
                .collect(toList());
    }

    public void delete(String currencyPairName) {
        ops.delete(key, currencyPairName);
    }

    @Transactional
    public void batchUpdate(List<CacheOrderStatisticDto> statisticList) {
        statisticList.forEach(this::update);
    }

    @Transactional
    public void update(CacheOrderStatisticDto statistic) {
        delete(statistic.getCurrencyPairName());
        put(statistic);
    }

    public boolean isEmpty() {
        return ops.size(key) == 0;
    }
}
