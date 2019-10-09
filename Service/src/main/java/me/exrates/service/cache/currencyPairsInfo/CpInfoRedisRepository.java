package me.exrates.service.cache.currencyPairsInfo;

import me.exrates.model.ngModel.ResponseInfoCurrencyPairDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class CpInfoRedisRepository {

    private static final String NEW_KEY = "currency_pair_info1";

    private final HashOperations<String, Object, Object> ops;

    @Autowired
    public CpInfoRedisRepository(@Qualifier("exratesRedisTemplate") RedisTemplate<String, Object> redisTemplate) {
        redisTemplate.setHashKeySerializer(new GenericToStringSerializer<>(Integer.class));
        redisTemplate.delete(NEW_KEY);
        ops = redisTemplate.opsForHash();
    }

    public void put(ResponseInfoCurrencyPairDto dto, Integer pairId) {
        ops.put(NEW_KEY, pairId, dto);
    }

    public ResponseInfoCurrencyPairDto get(Integer currencyPairId) {
        return (ResponseInfoCurrencyPairDto) ops.get(NEW_KEY, currencyPairId);
    }

    public boolean exist(Integer currencyPairId) {
        return ops.hasKey(NEW_KEY, currencyPairId);
    }

    public List<ResponseInfoCurrencyPairDto> getAll() {
        return ops.values(NEW_KEY)
                .stream()
                .map(o -> (ResponseInfoCurrencyPairDto) o)
                .collect(Collectors.toList());
    }

    public List<ResponseInfoCurrencyPairDto> getByListId(List<Integer> ids) {
        return ops.multiGet(NEW_KEY, Collections.unmodifiableCollection(ids))
                .stream()
                .map(o -> (ResponseInfoCurrencyPairDto) o)
                .collect(Collectors.toList());
    }

    public void delete(Integer pairId) {
        ops.delete(NEW_KEY, pairId);
    }

    @Transactional
    public void update(ResponseInfoCurrencyPairDto dto, Integer pairId) {
        delete(pairId);
        put(dto, pairId);
    }

    @Transactional
    public void batchUpdate(Map<Integer, ResponseInfoCurrencyPairDto> pairsDtoMap) {
        pairsDtoMap.forEach((k, v) -> update(v, k));
    }


    public boolean isEmpty() {
        return ops.size(NEW_KEY) == 0;
    }
}
