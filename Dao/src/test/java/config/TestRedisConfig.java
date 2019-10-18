package config;

import me.exrates.dao.UserPinDao;
import me.exrates.dao.impl.UserPinDaoImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class TestRedisConfig {

    public static final String TEST_STRING_REDIS_TEMPLATE = "testStringRedisTemplate";
    public static final String TEST_REDIS_VALUE_OPS = "TEST_REDIS_VALUE_OPS";

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(5);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);

        JedisConnectionFactory connectionFactory = new JedisConnectionFactory(poolConfig);
        connectionFactory.setUsePool(true);
        connectionFactory.setHostName("172.50.50.100");
        connectionFactory.setPort(6379);

        return connectionFactory;
    }

    @Bean
    @Qualifier(TEST_STRING_REDIS_TEMPLATE)
    public StringRedisTemplate stringRedisTemplate() {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate(redisConnectionFactory());
        stringRedisTemplate.setEnableTransactionSupport(true);
        return stringRedisTemplate;
    }

    @Bean
    @Qualifier(TEST_REDIS_VALUE_OPS)
    public ValueOperations<String, String> valueOperations() {
        return stringRedisTemplate().opsForValue();
    }
}
