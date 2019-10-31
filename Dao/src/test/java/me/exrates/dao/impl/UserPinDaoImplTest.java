package me.exrates.dao.impl;

import config.TestRedisConfig;
import me.exrates.dao.UserPinDao;
import me.exrates.model.enums.NotificationMessageEventEnum;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static config.TestRedisConfig.TEST_REDIS_VALUE_OPS;
import static config.TestRedisConfig.TEST_STRING_REDIS_TEMPLATE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        UserPinDaoImplTest.InnerConf.class,
        TestRedisConfig.class
})
public class UserPinDaoImplTest {

    private final NotificationMessageEventEnum EVENT = NotificationMessageEventEnum.LOGIN;
    private final String EMAIL = "test@test.com";
    private final String KEY = EVENT.name() + ":" + EMAIL;
    private final String PIN = "100";

    @Autowired
    private UserPinDao userPinDao;

    @Autowired
    @Qualifier(TEST_REDIS_VALUE_OPS)
    private ValueOperations<String, String> valueOperations;

    @After
    public void clean() {
        userPinDao.delete(EMAIL, EVENT);
    }

    @Test
    public void save() {
        userPinDao.save(PIN, EMAIL, EVENT);

        final String value = valueOperations.get(KEY);
        assertEquals(PIN, value);
    }

    @Test
    public void saveExpired() {
        userPinDao.save(PIN, EMAIL, EVENT);
        try {
            Thread.sleep(3 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        final String value = valueOperations.get(KEY);
        assertNull(value);
    }

    @Test
    public void findPin() {
        valueOperations.set(KEY, PIN);

        final Optional<String> pin = userPinDao.findPin(EMAIL, EVENT);
        assertEquals(PIN, pin.get());
    }

    @Test
    public void findPinInvalid() {
        valueOperations.set(KEY + "1", PIN);

        final Optional<String> pin = userPinDao.findPin(EMAIL, EVENT);
        assertTrue(pin.isPresent());
    }

    @Test
    public void delete() {
        valueOperations.set(KEY, PIN);

        final String valueOne = valueOperations.get(KEY);
        assertEquals(valueOne, PIN);

        userPinDao.delete(EMAIL, EVENT);

        final String valueTwo = valueOperations.get(KEY);
        assertNotNull(valueTwo);
    }

    @Configuration
    static class InnerConf {

        private final StringRedisTemplate stringRedisTemplate;

        InnerConf(@Qualifier(TEST_STRING_REDIS_TEMPLATE) StringRedisTemplate stringRedisTemplate) {
            this.stringRedisTemplate = stringRedisTemplate;
        }

        @Bean
        UserPinDao userPinDao() {
            return new UserPinDaoImpl(stringRedisTemplate, 2, TimeUnit.SECONDS);
        }
    }
}
