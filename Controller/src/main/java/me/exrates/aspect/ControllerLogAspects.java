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


    @Autowired
    private UserLogsHandler userLogsHandler;


    private static final List<MediaType> VISIBLE_TYPES = Arrays.asList(
            MediaType.valueOf("text/*"),
            MediaType.APPLICATION_FORM_URLENCODED,
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_XML,
            MediaType.valueOf("application/*+json"),
            MediaType.valueOf("application/*+xml"),
            MediaType.MULTIPART_FORM_DATA
    );

    @Pointcut("within(@org.springframework.stereotype.Controller *) || within(@org.springframework.web.bind.annotation.RestController *)")
    public void controller() {
    }

    @Pointcut("execution(* *.*(..)))")
    protected void allMethod() {
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
            ProcessIDManager.getProcessIdFromCurrentThread()
                    .ifPresent(p -> userLogsHandler.onUserLogEvent(new LogsWrapper(mLog, p, LogsTypeEnum.WS_SUBSCRIBE)));
            log.debug(mLog);
            ProcessIDManager.unregisterProcessId(getClass());
        }
        return result;
    }


    @Around("execution(* *(..)) && @annotation(org.springframework.web.bind.annotation.ExceptionHandler)")
    public Object doBasicProfilingHandlers(ProceedingJoinPoint thisJoinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = thisJoinPoint.proceed();
        String args = Arrays.toString(thisJoinPoint.getArgs());
        String method = getMethodName(thisJoinPoint);
        String user = getAuthenticatedUser();
        MethodsLog mLog = new MethodsLog(method, args, result.toString(), user, getExecutionTime(start), StringUtils.EMPTY);
        ProcessIDManager.getProcessIdFromCurrentThread()
                .ifPresent(p -> userLogsHandler.onUserLogEvent(new LogsWrapper(mLog, p, LogsTypeEnum.HTTP_ERROR_HANDLER_RESPONSE)));
        log.error(mLog);
        return result;
    }


    @Around("controller() && allMethod() && !@annotation(me.exrates.model.annotation.NoIdLog) && !execution(* me.exrates.controller.WsController..*(..))")
    public Object doBasicProfilingControllers(ProceedingJoinPoint pjp) throws Throwable {
        RequestContextProcessor rcp = new RequestContextProcessor();
        String clientIP = rcp.getClientIP();
        String httpMethod = rcp.getHttpMethod();
        String url = rcp.getFullUrl();
        String userAgent = rcp.getUserAgent();
        String requestBody = getRequestBody(rcp);
        long start = System.currentTimeMillis();
        int code = 0;
        String responseBody = null;
        Object result;
        String ex = null;
        try {
            result =  pjp.proceed();
            ResponseContextProcessor responseCp = new ResponseContextProcessor();
            code = responseCp.getStatus();
            Object body = null;
            if (result instanceof ResponseEntity) {
                ResponseEntity res = (ResponseEntity) result;
                body = res.getBody();
            }
            responseBody = (body == null) ? "" : body.toString();
        } catch (Exception e) {
            ex = formatException(e);
            throw e;
        } finally {
            ControllerLog controllerLog = new ControllerLog(
                    getMethodName(pjp),
                    url,
                    httpMethod,
                    getAuthenticatedUser(),
                    getExecutionTime(start),
                    code,
                    userAgent,
                    clientIP,
                    rcp.getJwtToken(),
                    rcp.getJsessionId(),
                    requestBody,
                    responseBody,
                    rcp.getArgs(),
                    ex);
            log.debug(controllerLog);
            ProcessIDManager.getProcessIdFromCurrentThread()
                    .ifPresent(p -> userLogsHandler.onUserLogEvent(new LogsWrapper(controllerLog, p, LogsTypeEnum.HTTP_REQUEST_LOG)));
        }
        return result;
    }


    private static class RequestContextProcessor {

        private HttpServletRequest request;

        private ContentCachingRequestWrapper getWrappedRequest() {
            return wrapRequest(request);
        }

        RequestContextProcessor() {
            this.request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                    .getRequest();
        }

        private String getUrl() {
            return request.getScheme() + "://" + request.getServerName()
                    + ":" + request.getServerPort() + request.getContextPath() + request.getRequestURI();
        }

        private String getQueryString() {
            return StringUtils.trimToEmpty(request.getQueryString());
        }

        private String getFullUrl() {
            String query = getQueryString();
            return getUrl() + (StringUtils.isBlank(query) ? "" : "?" + query);
        }

        private String getClientIP() {
            return IpUtils.getClientIpAddress(request);
        }

        private String getUserAgent() {
            return StringUtils.trimToEmpty(request.getHeader(HttpHeaders.USER_AGENT));
        }

        private String getHttpMethod() {
            return StringUtils.trimToEmpty(request.getMethod());
        }

        private String getContentType() {
            return StringUtils.trimToEmpty(request.getContentType());
        }

        private String getEncoding() {
            return StringUtils.trimToEmpty(request.getCharacterEncoding());
        }

        private String getArgs() {
            return request.getParameterMap().entrySet()
                    .stream()
                    .map(entry -> entry.getKey() + " : " + Arrays.stream(entry.getValue())
                            .filter(Objects::nonNull)
                            .map(Object::toString)
                            .collect(Collectors.joining(", ")))
                    .collect(Collectors.joining(", "));
        }

        private String getJwtToken() {
           return request.getHeader("Exrates-Rest-Token");
        }

        private String getJsessionId() {
            return Optional.ofNullable(WebUtils.getCookie(request, "JSESSIONID")).orElse(null).getValue();
        }
    }

    private static class ResponseContextProcessor {

        private HttpServletResponse response;

        ResponseContextProcessor() {
            this.response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                    .getResponse();
        }

        private int getStatus() {
            return response.getStatus();
        }

    }

    private String getRequestBody(RequestContextProcessor rcp) {
        byte[] content = rcp.getWrappedRequest().getContentAsByteArray();
        final StringBuilder sb = new StringBuilder();
        if (content.length > 0) {
            sb.append(logContent(content, rcp.getContentType(), rcp.getEncoding()));
        }
        return sb.toString();
    }


    private static String logContent(byte[] content, String contentType, String contentEncoding) {
        val mediaType = MediaType.valueOf(contentType);
        val visible = VISIBLE_TYPES.stream().anyMatch(visibleType -> visibleType.includes(mediaType));
        final StringBuilder sb = new StringBuilder();
        if (visible) {
            try {
                val contentString = new String(content, contentEncoding);
                Stream.of(contentString.split("\r\n|\r|\n")).forEach(line -> sb.append(String.format(" %s", line)));
            } catch (UnsupportedEncodingException e) {
                sb.append(String.format("\n [%d bytes content]", content.length));
            }
        } else {
            sb.append(String.format("\n [%d bytes content]", content.length));
        }
        return sb.toString();
    }

    private static ContentCachingRequestWrapper wrapRequest(HttpServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper) {
            return (ContentCachingRequestWrapper) request;
        } else {
            return new ContentCachingRequestWrapper(request);
        }
    }
}