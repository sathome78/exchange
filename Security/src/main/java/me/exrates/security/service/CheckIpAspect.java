package me.exrates.security.service;


import lombok.extern.log4j.Log4j2;
import me.exrates.security.exception.MissingHeaderException;
import me.exrates.security.ipsecurity.IpBlockingService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Optional;

@Log4j2(topic = "check_ip_aspect")
@Component
@Aspect
@PropertySource(value = {"classpath:/angular.properties"})
public class CheckIpAspect {

    @Value("${dev.mode}")
    private boolean DEV_MODE;

    @Autowired
    private IpBlockingService ipBlockingService;

    @Before("@annotation(CheckIp)")
    public void checkIp(JoinPoint jp) {
        MethodSignature signature = (MethodSignature) jp.getSignature();
        Method method = signature.getMethod();

        CheckIp myAnnotation = method.getAnnotation(CheckIp.class);

        HttpServletRequest request = getCurrentHttpRequest();
        if (request == null) {
            fail();
        }

        String ipAddress = Optional.ofNullable(request.getHeader("X-Forwarded-For"))
                .orElseThrow(() -> {
                    String message = "Missing header X-Forwarded-For in request";
                    log.error(message);
                    return new MissingHeaderException(message);
                });

        ipBlockingService.checkIp(ipAddress, myAnnotation.value());
    }


    private static HttpServletRequest getCurrentHttpRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        log.debug("Not called in the context of an HTTP request");
        return null;
    }

    private void fail() {
        String errorMessage = "Failed to find X-Forwarded-For header among request headers";
        log.warn(errorMessage);
        throw new RuntimeException(errorMessage);
    }

}
