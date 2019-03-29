package me.exrates.controller.filter;


import lombok.extern.log4j.Log4j2;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Log4j2(topic = "alterdice_api_logger")
public class RestAlterDiceFilter extends OncePerRequestFilter {

    private static final List<MediaType> VISIBLE_TYPES = Arrays.asList(
            MediaType.valueOf("text/*"),
            MediaType.APPLICATION_FORM_URLENCODED,
            MediaType.APPLICATION_JSON,
            MediaType.APPLICATION_XML,
            MediaType.valueOf("application/*+json"),
            MediaType.valueOf("application/*+xml"),
            MediaType.MULTIPART_FORM_DATA
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        request.getUserPrincipal();
        if (isAsyncDispatch(request) || !isAlterdiceUser()) {
            filterChain.doFilter(request, response);
        } else {
            doFilterWrapped(wrapRequest(request), wrapResponse(response), filterChain);
        }
    }

    private void doFilterWrapped(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response, FilterChain filterChain) throws ServletException, IOException {
        final StringBuilder sb = new StringBuilder();
        try {
            sb.append(beforeRequest(request, response));
            filterChain.doFilter(request, response);
        }
        finally {
            sb.append(afterRequest(request, response));
            log.debug(sb.toString());
            response.copyBodyToResponse();
        }
    }

    private String beforeRequest(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response) {
        return logRequestHeader(request, request.getRemoteAddr() + "|>");
    }

    private String afterRequest(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response) {
        StringBuilder sb = logRequestBody(request, request.getRemoteAddr() + "|>");
        StringBuilder sbr = logResponse(response, request.getRemoteAddr() + "|<");
        return sb.toString().concat(sbr.toString());
    }

    private static String logRequestHeader(ContentCachingRequestWrapper request, String prefix) {
        String queryString = request.getQueryString();
        final StringBuilder sb = new StringBuilder();
        if (queryString == null) {
            sb.append(String.format("\n %s %s %s", prefix, request.getMethod(), request.getRequestURI()));
        } else {
            sb.append(String.format("\n %s %s %s?%s", prefix, request.getMethod(), request.getRequestURI(), queryString));
        }
        Collections.list(request.getHeaderNames()).forEach(headerName ->
                Collections.list(request.getHeaders(headerName)).forEach(headerValue ->
                        sb.append(String.format("\n %s %s: %s", prefix, headerName, headerValue))));
        sb.append(prefix);
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
        response.getHeaderNames().forEach(headerName ->
                response.getHeaders(headerName).forEach(headerValue ->
                        sb.append(String.format("\n %s %s: %s  ", prefix, headerName, headerValue))));
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            auth.getAuthorities().forEach(p-> System.out.println(p));
        }
        return true;
    }

}
