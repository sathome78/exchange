package me.exrates.controller.interceptor;

import com.google.common.net.HttpHeaders;
import com.sun.jersey.api.client.Client;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.util.concurrent.CompletableFuture.*;
import static java.util.stream.Collectors.*;

@Component
@Log4j2
//@PropertySource("classpath:/db.properties")
public class LoggerInterceptor extends HandlerInterceptorAdapter {

    //    @Value("${db.elasticsearch.url}")
    private static final String url = "http://vpc-ex-app-s34knhiardlmpsiswpqhbu5d6e.us-east-2.es.amazonaws.com/test/_doc";

    public static final String USER_ID = "USER_ID";

    public LoggerInterceptor() {
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        runAsync(() -> logRequest(request, response, handler));
        return true;
    }

    private void logRequest(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

        try (CloseableHttpClient build = httpClientBuilder.build()) {
            boolean newUser = isNewUser(request);
            Cookie cookie = getUserId(request);
            Map<String, Object> headers = getHeaders(request);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("method", request.getMethod());
            jsonObject.put("email", getPrincipalUser(request));
            jsonObject.put("UUID", UUID.randomUUID().toString().replace("-", ""));
            jsonObject.put("headers", headers);
            jsonObject.put("referer", headers.getOrDefault(HttpHeaders.REFERER, "none"));
            jsonObject.put("URL", request.getRequestURL().toString());
            jsonObject.put("user-agent", headers.getOrDefault(HttpHeaders.USER_AGENT, "none"));
            jsonObject.put("new-user", newUser);
            jsonObject.put("user-id", cookie.getValue());
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("message", jsonObject.toString());
            jsonObject1.put("post_date", new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss").format(new Date()));
            response.addCookie(cookie);

            HttpPost httpPost = new HttpPost();
            httpPost.setHeader(new BasicHeader("Content-type", "application/json"));
            httpPost.setEntity(new StringEntity(jsonObject1.toString()));
            httpPost.setURI(new URI(url));

            log.info("Post statistic to elasticsearch with id " + jsonObject.getString("UUID"));
            CloseableHttpResponse execute = build.execute(httpPost);
            log.info("Elastic search response with id" + jsonObject.getString("UUID") + " is " + IOUtils.toString(execute.getEntity().getContent()) + " . Status code is " + execute.getStatusLine().getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean isNewUser(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return true;
        }
        List<Cookie> cookies1 = Arrays.stream(request.getCookies()).filter(cookie -> USER_ID.equals(cookie.getName())).collect(toList());
        return cookies1.size() == 0;
    }

    private Cookie getUserId(HttpServletRequest request) {
        Cookie generatedCookie = new Cookie(USER_ID, UUID.randomUUID().toString().replace("-", ""));
        if (request.getCookies() == null) {
            return generatedCookie;
        }
        List<Cookie> collect = Arrays.stream(request.getCookies()).filter(cookie -> USER_ID.equals(cookie.getName())).collect(toList());
        if (request.getCookies() == null || collect.size() == 0) {
            return generatedCookie;
        }
        return collect.get(0);
    }

    private Map<String, Object> getHeaders(HttpServletRequest request) {
        Map<String, Object> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames == null) return headers;
        try {
            headers = Collections.list(request.getHeaderNames())
                    .stream()
                    .collect(Collectors.toMap(h -> h, request::getHeader));
        } catch (Exception e) {

        }

        return headers;
    }


    private String getPrincipalUser(HttpServletRequest request) {
        Principal principal = request.getUserPrincipal();
        return principal == null ? "none" : principal.getName();
    }

}
