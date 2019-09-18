package me.exrates.service.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SettingsRedisRepositoryImpl implements SettingsRedisRepository {

    private static final String EMAIL_CONFIG = "email_config";

    private final HashOperations<String, Object, Object> ops;
    private final RedisTemplate redisTemplate;

    @Autowired
    public SettingsRedisRepositoryImpl(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.ops = redisTemplate.opsForHash();
    }

    @Override
    public Map<String, String> getEmailConfigs() {
        Map<String, String> result = new HashMap<>();
        Map<Object, Object> entries = ops.entries(EMAIL_CONFIG);
        entries.forEach((key, value) -> result.put((String) key, (String) value));
        return result;
    }
}
