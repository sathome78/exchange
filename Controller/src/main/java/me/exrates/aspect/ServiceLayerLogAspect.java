package me.exrates.aspect;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.logging.MethodsLog;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static me.exrates.aspect.LoggingUtils.getAuthenticatedUser;
import static me.exrates.aspect.LoggingUtils.getExecutionTime;
import static me.exrates.aspect.LoggingUtils.getMethodName;

@Log4j2(topic = "Service_layer_log")
@Aspect
@Component
public class ServiceLayerLogAspect {

    @Pointcut("within(@org.springframework.stereotype.Service *) || within(@org.springframework.stereotype.Component *) *)")
    public void service() {
    }

    @Pointcut("execution(* me.exrates.service.impl..*(..))  " +
            "|| execution(* me.exrates.ngService..*(..))" +
            "|| execution(* me.exrates.service.session..*(..))" +
            "|| execution(* me.exrates.service.stopOrder..*(..))" +
            "|| execution(* me.exrates.service.userOperation..*(..))")
    protected void allMethods() {
    }

    @Around("service() && allMethods() " +
            "&& !@annotation(me.exrates.model.annotation.NoIdLog) " +
            "&& !@annotation(javax.annotation.PostConstruct) " +
            "&& !@annotation(org.springframework.scheduling.annotation.Scheduled)")
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
            log.debug(new MethodsLog(method, args, StringUtils.EMPTY, user, getExecutionTime(start), ex.getCause() + " " + ex.getMessage()));
            throw ex;
        }

    }

}
