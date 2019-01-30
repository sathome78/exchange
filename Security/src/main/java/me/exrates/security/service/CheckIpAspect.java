package me.exrates.security.service;


import me.exrates.security.ipsecurity.IpBlockingService;
import me.exrates.security.ipsecurity.IpTypesOfChecking;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;


@Component
@Aspect
@PropertySource(value = {"classpath:/angular.properties"})
public class CheckIpAspect {

    @Value("${dev.mode}")
    private boolean DEV_MODE;

    private static final Logger logger = LoggerFactory.getLogger(CheckIpAspect.class);

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
        String ipAddress = request.getHeader("client_ip");
        if (!DEV_MODE) {
            ipBlockingService.checkIp(ipAddress, myAnnotation.value());
        }
    }


    private static HttpServletRequest getCurrentHttpRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        logger.debug("Not called in the context of an HTTP request");
        return null;
    }

    private void fail() {
        String errorMessage = "Failed to find client_ip header among request headers";
        logger.warn(errorMessage);
        throw new RuntimeException(errorMessage);
    }

}
