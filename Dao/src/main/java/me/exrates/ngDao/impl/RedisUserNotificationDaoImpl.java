package me.exrates.ngDao.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.model.dto.UserNotificationMessage;
import me.exrates.ngDao.RedisUserNotificationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Repository
public class RedisUserNotificationDaoImpl implements RedisUserNotificationDao {

    @Value("${redis.notification.expiration.period: 7}")
    private int expirationPeriod;

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public RedisUserNotificationDaoImpl(StringRedisTemplate redisTemplate,
                                        ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public Collection<UserNotificationMessage> findAllByUser(String email) {
        final Set<String> keys = Optional.ofNullable(redisTemplate.keys(email + ":*"))
                .orElse(Collections.emptySet());
        if (!keys.isEmpty()) {
            List<UserNotificationMessage> messages = new ArrayList<>(keys.size());
            keys.forEach(key -> {
                final String value = redisTemplate.opsForValue().get(key);
                try {
                    final UserNotificationMessage message = objectMapper.readValue(value, UserNotificationMessage.class);
                    message.setMessageId(key);
                    messages.add(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            return messages;
        }
        return Collections.emptyList();
    }

    @Override
    public boolean saveUserNotification(String email, UserNotificationMessage userNotification) {
        try {
            String key = email.concat(":" + System.currentTimeMillis());
            final String notification = objectMapper.writeValueAsString(userNotification);
            redisTemplate.opsForValue().set(key, notification);
            redisTemplate.expire(key, expirationPeriod, TimeUnit.DAYS);
            return true;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse user notification: " + e);
        }
    }

    @Override
    public void deleteUserNotification(String key) {
        redisTemplate.delete(key);
    }
}
