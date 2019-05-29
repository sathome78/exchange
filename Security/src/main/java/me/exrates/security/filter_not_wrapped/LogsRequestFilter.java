package me.exrates.security.filter_not_wrapped;

import co.elastic.apm.api.ElasticApm;
import co.elastic.apm.api.Span;
import co.elastic.apm.api.Transaction;
import lombok.extern.log4j.Log4j2;
import lombok.val;
import me.exrates.model.dto.logging.ControllerLog;
import me.exrates.model.loggingTxContext.QuerriesCountThreadLocal;
import me.exrates.security.HttpLoggingFilter;
import me.exrates.service.util.IpUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import processIdManager.ProcessIDManager;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.exrates.service.logs.LoggingUtils.*;

@Log4j2(topic = "Controller_layer_log")
public class LogsRequestFilter extends GenericFilterBean {

    private static final long SLOW_REQUEST_THREESHOLD_MS = 5000;

    private static final List<MediaType> VISIBLE_TYPES = Arrays.asList(
            MediaType.valueOf("text/*"),
            MediaType.APPLICATION_FORM_URLENCODED,
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_XML,
            MediaType.valueOf("application/*+json"),
            MediaType.valueOf("application/*+xml"),
            MediaType.MULTIPART_FORM_DATA
    );

    private static final String[] URLS_NOT_TO_LOG = new String[]{"/client", "/WEB-INF"};

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp  = (HttpServletResponse) response;
        doFilterWrapped(wrapRequest(req), wrapResponse(resp), chain);
    }

    private void doFilterWrapped(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, FilterChain filterChain) throws ServletException, IOException {
        long start = System.currentTimeMillis();
        Transaction transaction = ElasticApm.currentTransaction();
        String result;
        try {
            QuerriesCountThreadLocal.init();
            transaction.addLabel("process_id" , ProcessIDManager.getCurrentOrRegisterNewProcess(getClass()));
            filterChain.doFilter(request, response);
        }
        finally {
            ProcessIDManager.unregisterProcessId(getClass());
            if (needToLog(request)) {
                result = getResponse(response);
                transaction.setResult(result);
                transaction.addLabel("result", result);
                Integer txCount = QuerriesCountThreadLocal.getCountAndUnsetVarialbe();
                transaction.addLabel("querries_count", txCount);
                long execTime = getExecutionTime(start);
                log.debug(new ControllerLog(
                        getFullUrl(request),
                        getHttpMethod(request),
                        getAuthenticatedUser(),
                        execTime,
                        response.getStatusCode(),
                        getUserAgent(request),
                        getClientIP(request),
                        StringUtils.EMPTY,
                        StringUtils.EMPTY,
                        getRequestBody(request),
                        result,
                        getArgs(request),
                        txCount)
                );
                logSlowRequest(transaction, execTime);
            }
            response.copyBodyToResponse();
        }
    }

    private void logSlowRequest(Transaction transaction, long execTime) {
        if (execTime > SLOW_REQUEST_THREESHOLD_MS) {
            transaction.addLabel("slow_request", execTime);
        }
    }

    private boolean needToLog(HttpServletRequest request) {
        return !StringUtils.startsWithAny(request.getServletPath(), URLS_NOT_TO_LOG);
    }


    private String getRequestBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();
        final StringBuilder sb = new StringBuilder();
        if (content.length > 0) {
            sb.append(logContent(content, getContentType(request), getEncoding(request)));
        }
        return sb.toString();
    }


    private static String getResponse(ContentCachingResponseWrapper response) {
        byte[] content = response.getContentAsByteArray();
        if (content.length > 0) {
            return logContent(content, response.getContentType(), response.getCharacterEncoding());
        }
        return StringUtils.EMPTY;
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

    private static ContentCachingResponseWrapper wrapResponse(HttpServletResponse response) {
        if (response instanceof ContentCachingResponseWrapper) {
            return (ContentCachingResponseWrapper) response;
        } else {
            return new ContentCachingResponseWrapper(response);
        }
    }

    private String getUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName()
                + ":" + request.getServerPort() + request.getContextPath() + request.getRequestURI();
    }

    private String getQueryString(HttpServletRequest request) {
        return StringUtils.trimToEmpty(request.getQueryString());
    }

    private String getFullUrl(HttpServletRequest request) {
        String query = getQueryString(request);
        return getUrl(request) + (StringUtils.isBlank(query) ? "" : "?" + query);
    }

    private String getClientIP(HttpServletRequest request) {
            return IpUtils.getClientIpAddress(request);
    }

    private String getUserAgent(HttpServletRequest request) {
        return StringUtils.trimToEmpty(request.getHeader(HttpHeaders.USER_AGENT));
    }

    private String getHttpMethod(HttpServletRequest request) {
        return StringUtils.trimToEmpty(request.getMethod());
    }

    private String getContentType(HttpServletRequest request) {
        return StringUtils.trimToEmpty(request.getContentType());
    }

    private String getEncoding(HttpServletRequest request) {
        return StringUtils.trimToEmpty(request.getCharacterEncoding());
    }

    private String getArgs(HttpServletRequest request) {
        return request.getParameterMap().entrySet()
                .stream()
                .map(entry -> entry.getKey() + " : " + Arrays.stream(entry.getValue())
                        .filter(Objects::nonNull)
                        .map(Object::toString)
                        .collect(Collectors.joining(", ")))
                .collect(Collectors.joining(", "));
    }

    private static long getExecutionTime(long start) {
        return System.currentTimeMillis() - start;
    }
}
