package me.exrates.service.cache;

import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ExchangeRatesRedisRepository {

    private static final String key = "exchange_rate_holder";

    private final HashOperations<String, Object, Object> ops;

    @Autowired
    public ExchangeRatesRedisRepository(RedisTemplate<String, Object> redisTemplate) {
        redisTemplate.setHashKeySerializer(new GenericToStringSerializer<>(Integer.class));
        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(ExOrderStatisticsShortByPairsDto.class));
        ops = redisTemplate.opsForHash();
    }

    public void put(ExOrderStatisticsShortByPairsDto exOrderStatisticsShortByPairsDto) {
        ops.put(key, exOrderStatisticsShortByPairsDto.getCurrencyPairId(), exOrderStatisticsShortByPairsDto);
    }

    public ExOrderStatisticsShortByPairsDto get(Integer currencyPairId) {
        return (ExOrderStatisticsShortByPairsDto) ops.get(key, currencyPairId);
    }

    public boolean exist(Integer currencyPairId) {
        return ops.hasKey(key, currencyPairId);
    }

    public List<ExOrderStatisticsShortByPairsDto> getAll() {
        return ops.values(key).stream().map(o -> (ExOrderStatisticsShortByPairsDto) o).collect(Collectors.toList());
    }

    public List<ExOrderStatisticsShortByPairsDto> getByListId(List<Integer> ids) {
        return ops.multiGet(key, Collections.unmodifiableCollection(ids))
                .stream()
                .map(o -> (ExOrderStatisticsShortByPairsDto) o)
                .collect(Collectors.toList());
    }

    public void delete(Integer pairId) {
        ops.delete(key, pairId);
    }

    @Transactional
    public void update(ExOrderStatisticsShortByPairsDto exOrderStatisticsShortByPairsDto) {
        delete(exOrderStatisticsShortByPairsDto.getCurrencyPairId());
        put(exOrderStatisticsShortByPairsDto);
    }

    @Transactional
    public void batchUpdate(List<ExOrderStatisticsShortByPairsDto> pairsDtoList) {
        pairsDtoList.forEach(this::update);
    }

}
