package me.exrates.model.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class MonolitConditional implements Condition {

    private final MicroserviceConditional microserviceConditional = new MicroserviceConditional();

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        return !microserviceConditional.matches(conditionContext, annotatedTypeMetadata);
    }
}
