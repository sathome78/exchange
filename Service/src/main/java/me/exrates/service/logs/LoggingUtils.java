package me.exrates.service.logs;


import co.elastic.apm.api.ElasticApm;
import co.elastic.apm.api.Span;
import co.elastic.apm.api.Transaction;
import me.exrates.model.dto.logging.MethodsLog;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import processIdManager.ProcessIDManager;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;


public class LoggingUtils {

    private LoggingUtils() {
    }

    public static long getExecutionTime(long start) {
        return System.currentTimeMillis() - start;
    }


    public static Object doBaseProfiling(ProceedingJoinPoint pjp, org.apache.logging.log4j.Logger log) throws Throwable {
        String method = getMethodName(pjp);
        String args = Arrays.toString(pjp.getArgs());
        long start = System.currentTimeMillis();
        String user = getAuthenticatedUser();
        Transaction transaction = ElasticApm.currentTransaction();
        if (StringUtils.isEmpty(transaction.getTraceId())) {
            transaction = ElasticApm.startTransaction();
            transaction.setName(method);
            transaction.setType("SEVICE_LAYER");
        }
        transaction.addLabel("process_id" , ProcessIDManager.getProcessIdFromCurrentThread().orElse(StringUtils.EMPTY));
        Span span = transaction.startSpan();
        try {
            span.setName(method);
            Object result = pjp.proceed();
            span.addLabel("process_id" , ProcessIDManager.getProcessIdFromCurrentThread().orElse(StringUtils.EMPTY));
            log.debug(new MethodsLog(method, args, result, user, getExecutionTime(start), StringUtils.EMPTY));
            return result;
        } catch (Throwable ex) {
            span.captureException(ex);
            log.debug(new MethodsLog(method, args, StringUtils.EMPTY, user, getExecutionTime(start), formatException(ex)));
            throw ex;
        } finally {
            span.end();
        }
    }


    public static Object doBaseProfilingWithRegisterAndUnregister(ProceedingJoinPoint pjp, Class clazz, org.apache.logging.log4j.Logger log) throws Throwable {
        String method = getMethodName(pjp);
        String args = Arrays.toString(pjp.getArgs());
        long start = System.currentTimeMillis();
        String user = getAuthenticatedUser();
        ProcessIDManager.registerNewThreadForParentProcessId(clazz, Optional.empty());
        try {
            Transaction transaction = ElasticApm.currentTransaction();
            transaction.addLabel("process_id" , ProcessIDManager.getProcessIdFromCurrentThread().orElse(StringUtils.EMPTY));
            Object result = pjp.proceed();
            log.debug(new MethodsLog(method, args, result, user, getExecutionTime(start), StringUtils.EMPTY));
            return result;
        } catch (Throwable ex) {
            log.debug(new MethodsLog(method, args, StringUtils.EMPTY, user, getExecutionTime(start), ex.getCause() + " " + ex.getMessage()));
            throw ex;
        } finally {
            ProcessIDManager.unregisterProcessId(clazz);
        }
    }


    public static String getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null ? null : authentication.getName();
    }

    public static String getMethodName(ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String fullClassName = signature.getDeclaringTypeName();
        return getMethodName(fullClassName, signature.getMethod().getName());
    }

    public static String getMethodName(String fullClassName, String methodName) {
        return String.join("#", fullClassName.substring(fullClassName.lastIndexOf(".") + 1), methodName);
    }

    public static String formatException(Throwable throwable) {
        return String.join(" ", throwable.getClass().getName(), throwable.getMessage());
    }

    public static String completeSql(String sql, Map<String, ?> paramMap) {
        for (Map.Entry<String, ?> entry : paramMap.entrySet()) {
            sql = sql.replace(":" + entry.getKey(), String.valueOf(entry.getValue()));
        }
        return sql;
    }


}
