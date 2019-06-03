package me.exrates.aspect;

import lombok.extern.log4j.Log4j2;
import lombok.val;
import me.exrates.model.dto.logging.ControllerLog;
import me.exrates.model.dto.logging.LogsTypeEnum;
import me.exrates.model.dto.logging.LogsWrapper;
import me.exrates.model.dto.logging.MethodsLog;
import me.exrates.service.logs.UserLogsHandler;
import me.exrates.service.util.IpUtils;
import me.exrates.service.util.RequestUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.WebUtils;
import processIdManager.ProcessIDManager;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.exrates.service.logs.LoggingUtils.*;

@Log4j2(topic = "Controller_layer_log")
@Aspect
@Component
public class ControllerLogAspects {


    @Pointcut("within(@org.springframework.stereotype.Controller *) || within(@org.springframework.web.bind.annotation.RestController *)")
    public void controller() {
    }


    @Around("execution(* *(..)) && @annotation(org.springframework.messaging.simp.annotation.SubscribeMapping)")
    public Object doBasicProfilingHandlersOnWsSubscribe(ProceedingJoinPoint pjp) throws Throwable {
        String method = getMethodName(pjp);
        String args = Arrays.toString(pjp.getArgs());
        long start = System.currentTimeMillis();
        String user = getAuthenticatedUser();
        ProcessIDManager.registerNewThreadForParentProcessId(getClass(), Optional.empty());
        String errorMsg = StringUtils.EMPTY;
        Object result = null;
        try {
            result = pjp.proceed();
        } catch (Throwable ex) {
            errorMsg = formatException(ex);
            throw ex;
        } finally {
            MethodsLog mLog = new MethodsLog(method, args, result, user, getExecutionTime(start), errorMsg);
            log.debug(mLog);
            ProcessIDManager.unregisterProcessId(getClass());
        }
        return result;
    }

}