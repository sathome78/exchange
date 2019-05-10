package me.exrates.aspect;


import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.logging.LogsTypeEnum;
import me.exrates.model.dto.logging.LogsWrapper;
import me.exrates.model.dto.logging.MethodsLog;
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
import java.util.Optional;

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

    @Autowired
    private UserLogsHandler userLogsHandler;

    @Around("jdbc()")
    public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {
        String method = getMethodName(pjp);
        String args = Arrays.toString(pjp.getArgs());
        long start = System.currentTimeMillis();
        String user = getAuthenticatedUser();
        Object result = null;
        MethodsLog mLog;
        String exMessage = StringUtils.EMPTY;
        try {
            result = pjp.proceed();
        } catch (Throwable ex) {
            exMessage = formatException(ex);
            throw ex;
        } finally {
            mLog = new MethodsLog(method, args, result, user, getExecutionTime(start), exMessage);
            log.debug(mLog);
            ProcessIDManager.getProcessIdFromCurrentThread().ifPresent(p-> userLogsHandler.onUserLogEvent(new LogsWrapper(mLog, p, LogsTypeEnum.SQL_QUERY)));
        }
        return result;
    }
}
