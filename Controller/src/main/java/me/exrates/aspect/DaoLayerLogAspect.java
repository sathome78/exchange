package me.exrates.aspect;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.logging.MethodsLog;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Log4j2(topic = "Dao_layer_log")
@Aspect
@Component
public class DaoLayerLogAspect {

    @Pointcut("within(@org.springframework.stereotype.Repository *)")
    public void service() {
    }

    @Around("service() && !@annotation(me.exrates.model.annotation.NoIdLog)")
    public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {
        String method = getMethodName(pjp);
        String args = Arrays.toString(pjp.getArgs());
        long start = System.currentTimeMillis();
        String user = getAuthenticatedUser();
        try {
            Object result = pjp.proceed();
            log.debug(new MethodsLog(method, args, result, user, getExecutionTime(start), StringUtils.EMPTY));
            return result;
        } catch (Throwable ex) {
            log.debug(new MethodsLog(method, args, StringUtils.EMPTY, user, getExecutionTime(start), ex.getMessage()));
            throw ex;
        }

    }


    private long getExecutionTime(long start) {
        return System.currentTimeMillis() - start;
    }

    private String getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null ? null : authentication.getName();
    }

    private String getMethodName(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        return String.join("#", signature.getDeclaringTypeName(), signature.getMethod().getName());
    }
}
