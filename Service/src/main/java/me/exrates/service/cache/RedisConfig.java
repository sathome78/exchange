package me.exrates.service.cache;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.service.impl.RedisMessageSubscriber;
import me.exrates.service.stomp.StompMessenger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

@PropertySource("classpath:redis.properties")
@Configuration
public class RedisConfig {

    private @Value("${redis.host}") String host;
    private @Value("${redis.port}") Integer port;

    private static final String SUBSCRIBE_PATTERN = "candles.*";

    private final StompMessenger stompMessenger;

    public RedisConfig(StompMessenger stompMessenger) {
        this.stompMessenger = stompMessenger;
    }

    @Bean
    public JedisPoolConfig poolConfig() {
        final JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setTestOnBorrow(true);
        jedisPoolConfig.setMaxTotal(30);
        return jedisPoolConfig;
    }

    @Bean
    Topic topic() {
        return new PatternTopic(SUBSCRIBE_PATTERN);
    }

    @Bean
    MessageListenerAdapter messageListener() {
        return new MessageListenerAdapter(new RedisMessageSubscriber(stompMessenger));
    }

    @Bean
    RedisMessageListenerContainer redisContainer() {
        RedisMessageListenerContainer container
                = new RedisMessageListenerContainer();
        container.setConnectionFactory(jedisConnFactory());
        container.addMessageListener(messageListener(), topic());
        return container;
    }

    @Bean
    public JedisConnectionFactory jedisConnFactory() {
        JedisConnectionFactory jedisConnFactory = new JedisConnectionFactory(poolConfig());
        jedisConnFactory.setUsePool(true);
        jedisConnFactory.setHostName(host);
        jedisConnFactory.setDatabase(Protocol.DEFAULT_DATABASE);
        jedisConnFactory.setTimeout(3000);
        jedisConnFactory.setPort(port);
        return jedisConnFactory;
    }

    @Bean
    RedisTemplate<String, Object> redisTemplate() {
        final RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnFactory());
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
}
