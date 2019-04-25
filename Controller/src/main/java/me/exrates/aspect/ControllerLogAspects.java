package me.exrates.aspect;

import lombok.extern.log4j.Log4j2;
import lombok.val;
import me.exrates.model.dto.logging.ControllerLog;
import me.exrates.service.util.IpUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.exrates.aspect.LoggingUtils.getAuthenticatedUser;
import static me.exrates.aspect.LoggingUtils.getExecutionTime;
import static me.exrates.aspect.LoggingUtils.getMethodName;

@Log4j2(topic = "Controller_layer_log")
@Aspect
@Component
public class ControllerLogAspects {


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


    @Around("execution(* *(..)) && @annotation(org.springframework.web.bind.annotation.ExceptionHandler)")
    public Object doBasicProfilingHandlers(ProceedingJoinPoint thisJoinPoint) throws Throwable {
        Object result = thisJoinPoint.proceed();
        log.error(result.toString());
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
            ex = e.getMessage();
            throw e;
        } finally {
            log.debug(new ControllerLog(
                    getMethodName(pjp),
                    url,
                    httpMethod,
                    getAuthenticatedUser(),
                    getExecutionTime(start),
                    code,
                    userAgent,
                    clientIP,
                    requestBody,
                    responseBody,
                    rcp.getArgs(),
                    ex));
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