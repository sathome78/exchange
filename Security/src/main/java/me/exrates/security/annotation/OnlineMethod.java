package me.exrates.security.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation means that the method, to which it is applied, is an online method
 * ("online method" is the handler of the online request that update data on browser page in online mode)
 * and corresponding request should not reset session param "sessionEndTime"
 * Details see in description of class OnlineRestController
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OnlineMethod {
}
