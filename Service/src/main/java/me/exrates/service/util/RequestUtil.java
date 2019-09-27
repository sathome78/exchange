package me.exrates.service.util;

import lombok.RequiredArgsConstructor;
import me.exrates.dao.UserDao;
import me.exrates.model.User;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;

import java.util.Locale;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@Component
@RequiredArgsConstructor
public class RequestUtil {

    private final UserDao userDao;

    public HttpHeaders prepareHeaders(String userEmail) {
        return prepareHeaders(userDao.findByEmail(userEmail));
    }

    public HttpHeaders prepareHeaders(Integer userId) {
        return prepareHeaders(userDao.getUserById(userId));
    }

    private HttpHeaders prepareHeaders(User user) {
        HttpHeaders headers = prepareHeaders();
        headers.add("user_id", String.valueOf(user.getId()));
        headers.add("user_role", String.valueOf(user.getRole()));
        return headers;
    }

    public HttpHeaders prepareHeaders(Locale locale) {
        HttpHeaders httpHeaders = prepareHeaders();
        httpHeaders.add("locale", locale.toLanguageTag());
        return httpHeaders;
    }

    private HttpHeaders prepareHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE);
        headers.add(HttpHeaders.ACCEPT, APPLICATION_JSON_UTF8_VALUE);
//        headers.add(properties.getTokenName(), properties.getTokenValue());
        return headers;
    }

    public static HttpComponentsClientHttpRequestFactory getSSlFactory() {
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLHostnameVerifier(new NoopHostnameVerifier())
                .build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        return requestFactory;
    }
}
