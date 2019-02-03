package me.exrates.security.service;

import me.exrates.security.ipsecurity.IpTypesOfChecking;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckIp {

    IpTypesOfChecking value();
}
