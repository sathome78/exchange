package me.exrates.service.cache;

import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@PropertySource("classpath:redis.properties")
@Repository
public class ExchangeRatesRedisRepository {

    private final String key;

    private final HashOperations<String, Object, Object> ops;

    @Autowired
    public ExchangeRatesRedisRepository(@Value("${redis.key}") String key,
                                        RedisTemplate<String, Object> redisTemplate) {
        this.key = key;
        redisTemplate.setHashKeySerializer(new GenericToStringSerializer<>(Integer.class));
        redisTemplate.delete(key);
        this.ops = redisTemplate.opsForHash();
    }

    public void put(ExOrderStatisticsShortByPairsDto statistic) {
        ops.put(key, statistic.getCurrencyPairName(), statistic);
    }

    public ExOrderStatisticsShortByPairsDto get(String currencyPairName) {
        return (ExOrderStatisticsShortByPairsDto) ops.get(key, currencyPairName);
    }

    public boolean exist(String currencyPairName) {
        return ops.hasKey(key, currencyPairName);
    }

    public List<ExOrderStatisticsShortByPairsDto> getAll() {
        return ops.values(key)
                .stream()
                .map(o -> (ExOrderStatisticsShortByPairsDto) o)
                .collect(Collectors.toList());
    }

    public List<ExOrderStatisticsShortByPairsDto> getByNames(List<String> names) {
        return ops.multiGet(key, Collections.unmodifiableCollection(names))
                .stream()
                .map(o -> (ExOrderStatisticsShortByPairsDto) o)
                .collect(Collectors.toList());
    }

    public void delete(String currencyPairName) {
        ops.delete(key, currencyPairName);
    }

    @Transactional
    public void batchUpdate(List<ExOrderStatisticsShortByPairsDto> statisticList) {
        statisticList.forEach(this::update);
    }

    @Transactional
    public void update(ExOrderStatisticsShortByPairsDto statistic) {
        delete(statistic.getCurrencyPairName());
        put(statistic);
    }

    public boolean isEmpty() {
        return ops.size(key) == 0;
    }
}
