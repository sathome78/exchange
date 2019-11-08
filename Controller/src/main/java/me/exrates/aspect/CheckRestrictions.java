package me.exrates.aspect;

import me.exrates.model.enums.RestrictedOperation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckRestrictions {

    RestrictedOperation[] restrictions() default RestrictedOperation.NONE;

}
