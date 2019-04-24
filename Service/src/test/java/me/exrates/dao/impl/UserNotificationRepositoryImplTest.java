package me.exrates.dao.impl;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.dao.UserNotificationRepository;
import me.exrates.model.dto.UserNotificationMessage;
import me.exrates.model.enums.UserNotificationType;
import me.exrates.model.enums.WsSourceTypeEnum;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {UserNotificationRepositoryImplTest.TestReddisConfig.class})
public class UserNotificationRepositoryImplTest {

    private final String USER_PUBLIC_ID = "01";
    private final String OTHER_USER_PUBLIC_ID = "02";
    private static int counter = 0;

    @Autowired
    private UserNotificationRepository userNotificationRepository;

    @Autowired
    @Qualifier("testRedisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    @Before
    public void setUp() {
        counter = 0;
    }

    @Test
    public void save() {
        userNotificationRepository.save(USER_PUBLIC_ID, getSimpleTestMessage());
        List<UserNotificationMessage> found = getAll(USER_PUBLIC_ID);
        assertEquals(1, found.size());
    }

    @Test
    public void update() {
        String key = USER_PUBLIC_ID + ":03765327645";
        UserNotificationMessage message = getSimpleTestMessage();
        message.setId(key);
        assertFalse(message.isViewed());
        redisTemplate.opsForValue().set(key, message);
        message.setViewed(true);
        userNotificationRepository.update(message);
        List<UserNotificationMessage> found = getAll(USER_PUBLIC_ID);
        assertEquals(1, found.size());
        assertTrue(found.get(0).isViewed());
    }

    @Test
    public void delete() {
        String key = USER_PUBLIC_ID + ":03765327645";
        UserNotificationMessage message = getSimpleTestMessage();
        message.setId(key);
        redisTemplate.opsForValue().set(key, message);
        userNotificationRepository.delete(message);
        List<UserNotificationMessage> found = getAll(USER_PUBLIC_ID);
        assertEquals(0, found.size());
    }

    @Test
    public void findAll() {
        String key1 = USER_PUBLIC_ID + ":03765327645";
        String key2 = USER_PUBLIC_ID + ":03765327324";
        String key3 = OTHER_USER_PUBLIC_ID + ":03765327324";

        redisTemplate.opsForValue().set(key1, getSimpleTestMessage(key1));
        redisTemplate.opsForValue().set(key2, getSimpleTestMessage(key2));
        redisTemplate.opsForValue().set(key3, getSimpleTestMessage(key3));

        List<UserNotificationMessage> found = userNotificationRepository.findAll(USER_PUBLIC_ID, 2);
        assertEquals(2, found.size());
    }

    @Test
    public void findAll_sorted() {
        String key1 = USER_PUBLIC_ID + ":03765327645";
        String key2 = USER_PUBLIC_ID + ":03765327994";

        redisTemplate.opsForValue().set(key1, getSimpleTestMessage(key1));
        redisTemplate.opsForValue().set(key2, getSimpleTestMessage(key2));

        List<UserNotificationMessage> found = userNotificationRepository.findAll(USER_PUBLIC_ID, 2);
        assertEquals(2, found.size());
        assertEquals(key2, found.get(0).getId());
    }

    @After
    public void cleanUp() {
        getAll(USER_PUBLIC_ID)
                .forEach(notificationMessage -> redisTemplate.delete(notificationMessage.getId()));
        getAll(OTHER_USER_PUBLIC_ID)
                .forEach(notificationMessage -> redisTemplate.delete(notificationMessage.getId()));
    }

    private UserNotificationMessage getSimpleTestMessage() {
        String text = "test message " + (++counter);
        return new UserNotificationMessage(WsSourceTypeEnum.SUBSCRIBE, UserNotificationType.SUCCESS, text);
    }

    private UserNotificationMessage getSimpleTestMessage(String key) {
        UserNotificationMessage message = getSimpleTestMessage();
        message.setId(key);
        return message;
    }

    private List<UserNotificationMessage> getAll(String prefix) {
        Set<String> keys = redisTemplate.keys(prefix + ":*");
        return redisTemplate.opsForValue()
                .multiGet(keys)
                .stream()
                .map(o -> (UserNotificationMessage)o)
                .collect(Collectors.toList());
    }

    @Configuration
    @PropertySource("classpath:redis.properties")
    static class TestReddisConfig {

        private @Value("${redis.host}")
        String host;
        private @Value("${redis.port}")
        Integer port;

        @Bean
        public JedisPoolConfig poolConfig() {
            final JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setTestOnBorrow(true);
            jedisPoolConfig.setMaxTotal(30);
            return jedisPoolConfig;
        }

        @Bean
        @Qualifier("testJedisConnectionFactory")
        public JedisConnectionFactory notificationsJedisConnectionFactory() {
            JedisConnectionFactory jedisConnFactory = new JedisConnectionFactory(poolConfig());
            jedisConnFactory.setUsePool(true);
            jedisConnFactory.setHostName(host);
            jedisConnFactory.setDatabase(Protocol.DEFAULT_DATABASE);
            jedisConnFactory.setTimeout(3000);
            jedisConnFactory.setPort(port);
            return jedisConnFactory;
        }

        @Bean
        @Qualifier("testRedisTemplate")
        public RedisTemplate<String, Object> testRedisTemplate() {
            final RedisTemplate<String, Object> template = new RedisTemplate<>();
            template.setConnectionFactory(notificationsJedisConnectionFactory());
            template.setKeySerializer(new StringRedisSerializer());
            template.setHashKeySerializer(new StringRedisSerializer());

            Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
            ObjectMapper om = new ObjectMapper();
            om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
            om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
            jackson2JsonRedisSerializer.setObjectMapper(om);

            template.setHashValueSerializer(jackson2JsonRedisSerializer);
            template.setValueSerializer(jackson2JsonRedisSerializer);
            template.afterPropertiesSet();
            return template;
        }

        @Bean
        public UserNotificationRepository userNotificationRepository() {
            return new UserNotificationRepositoryImpl(testRedisTemplate());
        }
    }
}
