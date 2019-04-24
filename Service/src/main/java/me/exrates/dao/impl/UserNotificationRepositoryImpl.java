package me.exrates.dao.impl;

import me.exrates.dao.UserNotificationRepository;
import me.exrates.model.dto.UserNotificationMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class UserNotificationRepositoryImpl implements UserNotificationRepository {

    @Value("${redis.notification.expiration.period : 7}")
    private Long expiresIn;

    private final RedisTemplate redisTemplate;

    @Autowired
    public UserNotificationRepositoryImpl(@Qualifier("notificationsRedisTemplate") RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        if (Objects.nonNull(this.redisTemplate)) {
            this.redisTemplate.setHashKeySerializer(new GenericToStringSerializer<>(Integer.class));
        }
    }

    @Override
    public UserNotificationMessage save(String userPublicId, UserNotificationMessage notificationMessage) {
        notificationMessage.setId(userPublicId + ":" + System.nanoTime());
        return update(notificationMessage);
    }

    @Override
    public UserNotificationMessage update(UserNotificationMessage notificationMessage) {
        redisTemplate.opsForValue().set(notificationMessage.getId(), notificationMessage, expiresIn, TimeUnit.DAYS);
        return notificationMessage;
    }

    @Override
    public void delete(UserNotificationMessage notificationMessage) {
        redisTemplate.delete(notificationMessage.getId());
    }

    @Override
    public boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public List<UserNotificationMessage> findAll(String userPublicId, int limit) {
        Set<String> keys = redisTemplate.keys(userPublicId + ":*");
        List<UserNotificationMessage> result = redisTemplate.opsForValue().multiGet(keys);
        return result.stream()
                .sorted((o1, o2) -> {
                    if (Objects.isNull(o1) || Objects.isNull(o2)) {
                        return 0;
                    }
                    String value1 = o1.getId().substring(o1.getId().indexOf(":") + 1);
                    Long time1 = Long.parseLong(value1);
                    String value2 = o2.getId().substring(o2.getId().indexOf(":") + 1);
                    Long time2 = Long.parseLong(value2);
                    return time2.compareTo(time1);
                })
                .limit(limit)
                .collect(Collectors.toList());
    }


}
