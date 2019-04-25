package me.exrates.aspect;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.logging.ServiceLog;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Log4j2(topic = "Service_layer_log")
@Aspect
@Component
public class ServiceLayerAspect {

    @Pointcut("within(@org.springframework.stereotype.Service *) || within(@org.springframework.stereotype.Component *) *)")
    public void service() {
    }

    @Pointcut("execution(* *.*(..)))")
    protected void allMethods() {
    }

    @Around("service() && allMethods() && !@annotation(me.exrates.controller.annotation.NoLog)")
    public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String method = signature.getMethod().getName();
        String args = "";
        long start = System.currentTimeMillis();
        String user = getAuthenticatedUser();
        try {
            Object result = pjp.proceed();
            log.debug(new ServiceLog(method, args, result.toString(), user, getExecutionTime(start), StringUtils.EMPTY));
            return result;
        } catch (Throwable ex) {
            log.debug(new ServiceLog(method, args, StringUtils.EMPTY, user, getExecutionTime(start), ex.getMessage()));
            throw ex;
        }

    }


    private long getExecutionTime(long start) {
        return System.currentTimeMillis() - start;
    }

    private String getAuthenticatedUser() {
       /* Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null ? null : authentication.getName();*/
       return null;
    }
}
