package me.exrates.security.filter;

import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Log4j2(topic = "alterdice_api_logger")
public class RestAlterdiceFilterExp extends GenericFilterBean {

    private static final List<MediaType> VISIBLE_TYPES = Arrays.asList(
            MediaType.valueOf("text/*"),
            MediaType.APPLICATION_FORM_URLENCODED,
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_XML,
            MediaType.valueOf("application/*+json"),
            MediaType.valueOf("application/*+xml"),
            MediaType.MULTIPART_FORM_DATA
    );

    private static final List<String> rolesToFilter = Arrays.asList("OUTER_MARKET_BOT", "BOT_TRADER");

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp  = (HttpServletResponse) response;
        doFilterWrapped(wrapRequest(req), wrapResponse(resp), chain);
    }

    private void doFilterWrapped(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, FilterChain filterChain) throws ServletException, IOException {
        final StringBuilder sb = new StringBuilder();
        try {
            sb.append(beforeRequest(request));
            filterChain.doFilter(request, response);
        }
        finally {
            if(isAlterdiceUser()) {
                sb.append(afterRequest(request, response));
                log.debug(sb.toString());
            }
            response.copyBodyToResponse();
        }
    }

    private String beforeRequest(ContentCachingRequestWrapper request) {
        try {
            return logRequestHeader(request, "       |>/ ");
        } catch (Exception e) {
            return StringUtils.EMPTY;
        }
    }

    private String afterRequest(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response) {
        try {
            StringBuilder sb = logRequestBody(request, "       |>");
            StringBuilder sbr = logResponse(response, "       |<");
            return sb.toString().concat(sbr.toString());
        } catch (Exception e) {
            return StringUtils.EMPTY;
        }
    }

    private static String logRequestHeader(ContentCachingRequestWrapper request, String prefix) {
        String queryString = request.getQueryString();
        final StringBuilder sb = new StringBuilder();
        if (queryString == null) {
            sb.append(String.format("\n %s %s %s", prefix, request.getMethod(), request.getRequestURI()));
        } else {
            sb.append(String.format("\n %s %s %s?%s", prefix, request.getMethod(), request.getRequestURI(), queryString));
        }
        Collections.list(request.getHeaderNames()).stream().filter(p->p.contains("api") || p.contains("API")).forEach(headerName ->
                Collections.list(request.getHeaders(headerName)).forEach(headerValue ->
                        sb.append(String.format("\n %s %s: %s", prefix, headerName, headerValue))));
        return sb.toString();
    }

    private static StringBuilder logRequestBody(ContentCachingRequestWrapper request, String prefix) {
        byte[] content = request.getContentAsByteArray();
        final StringBuilder sb = new StringBuilder();
        if (content.length > 0) {
            sb.append(logContent(content, request.getContentType(), request.getCharacterEncoding(), prefix));
        }
        return sb;
    }

    private static StringBuilder logResponse(ContentCachingResponseWrapper response, String prefix) {
        int status = response.getStatus();
        final StringBuilder sb = new StringBuilder();
        sb.append(String.format("\n %s %s %s", prefix, status, HttpStatus.valueOf(status).getReasonPhrase()));
        sb.append(prefix);
        byte[] content = response.getContentAsByteArray();
        if (content.length > 0) {
            sb.append(logContent(content, response.getContentType(), response.getCharacterEncoding(), prefix));
        }
        return sb;
    }

    private static String logContent(byte[] content, String contentType, String contentEncoding, String prefix) {
        val mediaType = MediaType.valueOf(contentType);
        val visible = VISIBLE_TYPES.stream().anyMatch(visibleType -> visibleType.includes(mediaType));
        final StringBuilder sb = new StringBuilder();
        if (visible) {
            try {
                val contentString = new String(content, contentEncoding);
                Stream.of(contentString.split("\r\n|\r|\n")).forEach(line -> sb.append(String.format("\n %s %s", prefix, line)));
            } catch (UnsupportedEncodingException e) {
                sb.append(String.format("\n %s [%d bytes content]", prefix, content.length));
            }
        } else {
            sb.append(String.format("\n %s [%d bytes content]", prefix, content.length));
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

    private boolean isAlterdiceUser() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            return auth != null && auth.getAuthorities().stream().anyMatch(p-> rolesToFilter.contains(p.getAuthority()));
        } catch (Exception e) {
            return false;
        }
    }
}
