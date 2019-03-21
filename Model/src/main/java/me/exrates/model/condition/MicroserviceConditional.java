package me.exrates.model.condition;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.io.IOException;
import java.util.Properties;

@Log4j2
public class MicroserviceConditional implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Properties properties = new Properties();
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("inout.properties"));
        } catch (Exception e) { //TODO test does not resolves property file, in case of exception return false
            log.warn("Failed to load inout.properties", e);
            return false;
        }
        return Boolean.valueOf(properties.getProperty("inout.isMicroserviceEnabled"));
    }
}
