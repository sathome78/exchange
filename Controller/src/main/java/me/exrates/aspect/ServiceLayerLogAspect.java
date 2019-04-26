package me.exrates.aspect;

import lombok.extern.log4j.Log4j2;
import me.exrates.ProcessIDManager;
import me.exrates.model.dto.logging.MethodsLog;
import me.exrates.service.events.ApplicationEventWithProcessId;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

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
            "|| execution(* me.exrates.service.openapi..*(..))" +
            "|| execution(* me.exrates.service.stopOrder..*(..))" +
            "|| execution(* me.exrates.service.userOperation..*(..))" +
            "|| execution(* me.exrates.service.ieo..*(..))" +
            "|| execution(* me.exrates.service.notifications..*(..))" +
            "|| execution(* me.exrates.service.newsExt..*(..))" +
            "|| execution(* me.exrates.service.kyc..*(..))" +
            "|| execution(* me.exrates.service.merchantStrategy..*(..))" +
            "|| execution(* me.exrates.service.stomp..*(..))" +
            "|| execution(* me.exrates.service.refreshHandlers..*(..))" +
            "|| execution(* me.exrates.service.handler..*(..))" /*+
            "|| execution(* me.exrates.security.service.impl..*(..))"*/)
    protected void allMethods() {
    }

    @Around("allMethods() " +
            "&& !@annotation(me.exrates.model.annotation.NoIdLog) " +
            "&& !@annotation(org.springframework.scheduling.annotation.Async) " +
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
        System.out.println("args " + Arrays.toString(pjp.getArgs()));
        Arrays.stream(pjp.getArgs()).forEach(p-> {
            if (p instanceof ApplicationEventWithProcessId) {
                ApplicationEventWithProcessId event = (ApplicationEventWithProcessId) p;
                id.set(event.getProcessId());
            }
        });
        System.out.println("process id from parent thread " + id.get().orElse("no id!!"));
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

}
