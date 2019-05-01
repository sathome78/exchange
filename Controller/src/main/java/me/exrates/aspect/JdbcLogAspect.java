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

import static me.exrates.service.logs.LoggingUtils.doBaseProfiling;
import static me.exrates.service.logs.LoggingUtils.formatException;
import static me.exrates.service.logs.LoggingUtils.getAuthenticatedUser;
import static me.exrates.service.logs.LoggingUtils.getExecutionTime;
import static me.exrates.service.logs.LoggingUtils.getMethodName;

@Log4j2(topic = "Jdbc_query_log")
@Aspect
@Component
public class JdbcLogAspect {

    @Pointcut("execution(* org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate..*(..)) || " +
            "(execution(* org.springframework.jdbc.core.JdbcOperations..*(..))))")
    public void jdbc() {
    }

    @Around("jdbc()")
    public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {
       return doBaseProfiling(pjp, getClass());
    }
}
