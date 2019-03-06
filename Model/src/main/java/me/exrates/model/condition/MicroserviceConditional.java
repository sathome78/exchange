package me.exrates.model.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.io.IOException;
import java.util.Properties;

public class MicroserviceConditional implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Properties properties = new Properties();
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("inout.properties"));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return Boolean.valueOf(properties.getProperty("inout.isMicroserviceEnabled"));
    }
}
