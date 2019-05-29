package me.exrates.aspect;


import co.elastic.apm.api.ElasticApm;
import co.elastic.apm.api.Span;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.logging.LogsTypeEnum;
import me.exrates.model.dto.logging.LogsWrapper;
import me.exrates.model.dto.logging.MethodsLog;
import me.exrates.model.loggingTxContext.QuerriesCountThreadLocal;
import me.exrates.service.logs.UserLogsHandler;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import processIdManager.ProcessIDManager;


import java.util.Arrays;
import java.util.Map;

import static me.exrates.service.logs.LoggingUtils.*;

@Log4j2(topic = "Jdbc_query_log")
@Aspect
@Component
public class JdbcLogAspect {

    private static final long SLOW_QUERRY_THREESHOLD_MS = 1000;

    @Pointcut("execution(* org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate..*(..)) || " +
            "(execution(* org.springframework.jdbc.core.JdbcOperations..*(..))))")
    public void jdbc() {
    }


    @Autowired
    private UserLogsHandler userLogsHandler;

    @Around("jdbc()")
    public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {
        String method = getMethodName(pjp);
        String args = formatQuerry(pjp.getArgs());
        long start = System.currentTimeMillis();
        String user = getAuthenticatedUser();
        Object result = null;
        String exStr = StringUtils.EMPTY;
        String currentProcessId = ProcessIDManager.getProcessIdFromCurrentThread().orElse(StringUtils.EMPTY);
        Span span = ElasticApm.currentSpan().startSpan().setName(method);
        MethodsLog mLog;
        try {
            result = pjp.proceed();
            span.addLabel("process_id", currentProcessId);
            span.addLabel("fullSql", args);
            return result;
        } catch (Throwable ex) {
            span.captureException(ex);
            exStr = formatException(ex);
            throw ex;
        } finally {
            QuerriesCountThreadLocal.inc();
            long exTime = getExecutionTime(start);
            mLog = new MethodsLog(method, args, result, user, exTime, exStr);
            log.debug(mLog);
            logSlowQuerry(span, exTime);
            span.end();
        }
    }

    private String formatQuerry(Object[] args) {
        try {
            String sql = (String) args[0];
            Map<String, ?> querryArgs = (Map<String, ?>) args[1];
            return completeSql(sql, querryArgs);
        } catch (Exception e) {
            return Arrays.toString(args);
        }
    }

    private void logSlowQuerry(Span span, long execTime) {
        if (execTime > SLOW_QUERRY_THREESHOLD_MS) {
            span.addLabel("slow_querry", execTime);
        }
    }


}
