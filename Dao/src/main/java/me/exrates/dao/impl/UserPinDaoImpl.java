package me.exrates.dao.impl;

import me.exrates.dao.UserPinDao;
import me.exrates.model.enums.NotificationMessageEventEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Repository
@PropertySource(value = {"classpath:/angular.properties"})
public class UserPinDaoImpl implements UserPinDao {

    private final ValueOperations<String, String> valueOperations;
    private final long EXPIRATION_PERIOD;
    private final TimeUnit TIME_UNIT;

    @Autowired
    public UserPinDaoImpl(@Qualifier("stringRedisTemplate") StringRedisTemplate stringRedisTemplate,
                          @Value("${login.expiration.time: 5}") long expirationPeriod) {
        this.valueOperations = stringRedisTemplate.opsForValue();
        this.EXPIRATION_PERIOD = expirationPeriod;
        this.TIME_UNIT = TimeUnit.MINUTES;
    }

    public UserPinDaoImpl(@Qualifier("stringRedisTemplate") StringRedisTemplate stringRedisTemplate,
                          @Value("${login.expiration.time: 5}") long expirationPeriod,
                          TimeUnit timeUnit) {
        this.valueOperations = stringRedisTemplate.opsForValue();
        this.EXPIRATION_PERIOD = expirationPeriod;
        this.TIME_UNIT = timeUnit;
    }

    @Override
    public String save(String pin, String useEmail, NotificationMessageEventEnum eventEnum) {
        String key = getKey(useEmail, eventEnum);
        valueOperations.set(key, pin, EXPIRATION_PERIOD, TIME_UNIT);
        return pin;
    }

    @Override
    public Optional<String> findPin(String useEmail, NotificationMessageEventEnum event) {
        String key = getKey(useEmail, event);
        return Optional.ofNullable(valueOperations.get(key));
    }

    @Override
    public void delete(String useEmail, NotificationMessageEventEnum event) {
        String key = getKey(useEmail, event);
        valueOperations.set(key, "", 1L, TimeUnit.SECONDS);
    }

    private String getKey(String email, NotificationMessageEventEnum event) {
        return event.name() + ":" + email;
    }
}
