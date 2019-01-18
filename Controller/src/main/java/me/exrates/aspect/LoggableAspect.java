package me.exrates.aspect;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.MethodMetricsDto;
import me.exrates.service.cache.MetricsCache;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.isNull;

@Log4j2
@Aspect
@Component
public class LoggableAspect {

    private static final String SEPARATOR = "::";

    @Autowired
    private MetricsCache metricsCache;

    @Around("execution(* me.exrates.dao..*(..)) && " +
            "(within(me.exrates.dao.aspects..*) " +
            "|| within(me.exrates.dao.exception..*) " +
            "|| within(me.exrates.dao.impl..*)" +
            "|| within(me.exrates.dao.newsExt.impl..*)" +
            "|| within(me.exrates.dao.rowmappers..*)" +
            "|| within(me.exrates.dao.userOperation.UserOperationDaoImpl))")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        final MethodSignature signature = (MethodSignature) point.getSignature();
        final String methodName = signature.getName();
        final String className = signature.getMethod().getDeclaringClass().getName();

        final String key = StringUtils.join(className, SEPARATOR, methodName);

        MethodMetricsDto methodMetrics = metricsCache.getMethodMetrics(key);
        if (isNull(methodMetrics)) {
            methodMetrics = MethodMetricsDto.builder()
                    .methodKey(key)
                    .invocationCounter(new AtomicInteger())
                    .errorCounter(new AtomicInteger())
                    .executionTimes(new CopyOnWriteArrayList<>())
                    .build();
            metricsCache.setMethodMetrics(key, methodMetrics);
        }

        log.debug("Starting [{}], called {} time(s)",
                key,
                methodMetrics.getInvocationCounter().incrementAndGet());

        final StopWatch stopWatch = StopWatch.createStarted();

        Object result;
        try {
            result = point.proceed();
        } catch (Throwable throwable) {
            methodMetrics.getErrorCounter().incrementAndGet();

            log.debug("An error has occurred [{}]: {}",
                    key,
                    throwable.getMessage());

            throw new Throwable(throwable);
        }

        stopWatch.stop();

        final long time = stopWatch.getTime(TimeUnit.MILLISECONDS);

        methodMetrics.getExecutionTimes().add(time);

        log.debug("Finished [{}] in {} millisecond(s)",
                key,
                time);

        return result;
    }
}