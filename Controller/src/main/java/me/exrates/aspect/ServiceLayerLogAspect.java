package me.exrates.aspect;

import co.elastic.apm.api.ElasticApm;
import co.elastic.apm.api.Transaction;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.logging.MethodsLog;
import me.exrates.service.events.ApplicationEventWithProcessId;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import processIdManager.ProcessIDManager;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static me.exrates.service.logs.LoggingUtils.doBaseProfiling;
import static me.exrates.service.logs.LoggingUtils.doBaseProfilingWithRegisterAndUnregister;
import static me.exrates.service.logs.LoggingUtils.getAuthenticatedUser;
import static me.exrates.service.logs.LoggingUtils.getExecutionTime;
import static me.exrates.service.logs.LoggingUtils.getMethodName;

@Log4j2(topic = "Service_layer_log")
@Aspect
@Component
public class ServiceLayerLogAspect {


    @Around(" execution(* me.exrates.service..*(..)) " +
            "&& (@annotation(org.springframework.transaction.event.TransactionalEventListener) " +
            "|| @annotation(org.springframework.context.event.EventListener)) " +
            "&& @annotation(org.springframework.scheduling.annotation.Async) ")
    public Object doBasicProfilingOfHandlers(ProceedingJoinPoint pjp) throws Throwable {
        String method = getMethodName(pjp);
        String args = Arrays.toString(pjp.getArgs());
        long start = System.currentTimeMillis();
        String user = getAuthenticatedUser();
        AtomicReference<Optional<String>> id = new AtomicReference<>();
        Arrays.stream(pjp.getArgs()).forEach(p-> {
            if (p instanceof ApplicationEventWithProcessId) {
                ApplicationEventWithProcessId event = (ApplicationEventWithProcessId) p;
                id.set(event.getProcessId());
            }
        });
        ProcessIDManager.registerNewThreadForParentProcessId(getClass(), id.get());
        try {
            Object result = pjp.proceed();
            log.debug(new MethodsLog(method, args, result, user, getExecutionTime(start), StringUtils.EMPTY));
            return result;
        } catch (Throwable ex) {
            log.debug(new MethodsLog(method, args, StringUtils.EMPTY, user, getExecutionTime(start), ex.getCause() + " " + ex.getMessage()));
            throw ex;
        } finally {
            ProcessIDManager.unregisterProcessId(getClass());
        }
    }

    @Around(" (execution(* me.exrates.service..*(..)) " +
            "  || execution(* me.exrates.ngService..*(..)) " +
            "  || execution(* me.exrates.security.service..*(..)) " +
            "  || execution(* me.exrates.security.ipsecurity..*(..)) " +
            "  || execution(* me.exrates.security.filter..*(..))) " +
            "&& !@annotation(me.exrates.model.annotation.NoIdLog) " +
            "&& !@annotation(org.springframework.scheduling.annotation.Async) " +
            "&& !@annotation(org.springframework.scheduling.annotation.Scheduled) ")
    public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {
        return doBaseProfiling(pjp, log);
    }

    @Around(" execution(* me.exrates.service..*(..)) " +
            "&& !(@annotation(org.springframework.transaction.event.TransactionalEventListener) " +
            "       || @annotation(org.springframework.context.event.EventListener)) " +
            "&& @annotation(org.springframework.scheduling.annotation.Async) ")
    public Object doBasicProfilingOfAsync(ProceedingJoinPoint pjp) throws Throwable {
        return doBaseProfilingWithRegisterAndUnregister(pjp, getClass(), log);
    }

}
