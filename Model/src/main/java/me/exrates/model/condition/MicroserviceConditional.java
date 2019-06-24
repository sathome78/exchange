package me.exrates.model.condition;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.Properties;

@Log4j2(topic = "inout")
public class MicroserviceConditional implements Condition {

    private static final String INOUT_IS_ENABLED_JEDIS_KEY = "inout.isEnabled";
    private final boolean isEnabled;


    private Jedis getJedis() {
        Properties properties = null;
        try {
            properties = getJedisProperties();
        } catch (IOException e) {
            log.error("jedis properties not found ");
            throw new RuntimeException(e);
        }
        log.info("Redis props: " + properties.toString());
        return new Jedis(properties.getProperty("redis.host"), Integer.parseInt(properties.getProperty("redis.port")));
    }

    private Properties getJedisProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(this.getClass().getClassLoader().getResourceAsStream("redis.properties"));
        return properties;
    }

    public MicroserviceConditional(){
        isEnabled = Boolean.parseBoolean(getJedis().get(INOUT_IS_ENABLED_JEDIS_KEY));
        log.info("isEnabled = " + isEnabled);
    }

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        return isEnabled;
    }
}
