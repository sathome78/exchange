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

import static me.exrates.aspect.LoggingUtils.formatException;
import static me.exrates.aspect.LoggingUtils.getAuthenticatedUser;
import static me.exrates.aspect.LoggingUtils.getExecutionTime;
import static me.exrates.aspect.LoggingUtils.getMethodName;

@Log4j2(topic = "Jdbc_query_log")
/*@Aspect
@Component*/
public class JdbcLogAspect {

    @Pointcut("execution(* org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate..*(..)) || execution(* org.springframework.jdbc.core.JdbcTemplate..*(..))")
    public void jdbc() {
    }

    @Around("jdbc()")
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
            log.debug(new MethodsLog(method, args, StringUtils.EMPTY, user, getExecutionTime(start), formatException(ex)));
            throw ex;
        }
    }


}
