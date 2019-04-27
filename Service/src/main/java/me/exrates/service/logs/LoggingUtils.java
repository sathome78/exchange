package me.exrates.service.logs;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class LoggingUtils {

    private LoggingUtils() {
    }

    public static long getExecutionTime(long start) {
        return System.currentTimeMillis() - start;
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


}
