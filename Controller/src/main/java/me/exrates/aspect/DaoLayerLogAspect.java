package me.exrates.aspect;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.logging.MethodsLog;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static me.exrates.service.logs.LoggingUtils.doBaseProfiling;

@Log4j2(topic = "Dao_layer_log")
@Aspect
@Component
public class DaoLayerLogAspect {

    @Pointcut("within(@org.springframework.stereotype.Repository *)")
    public void service() {
    }

    @Around("service() && !@annotation(me.exrates.model.annotation.NoIdLog)")
    public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable {
        return doBaseProfiling(pjp, getClass());
    }
}
