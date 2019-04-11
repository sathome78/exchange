package me.exrates.security.service;

import me.exrates.model.userOperation.enums.UserOperationAuthority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckUserAuthority {

    UserOperationAuthority authority();

}
