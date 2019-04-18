package me.exrates.service.util;

import lombok.RequiredArgsConstructor;
import me.exrates.dao.UserDao;
import me.exrates.model.User;
import me.exrates.service.properties.InOutProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class RequestUtil {

    private final InOutProperties properties;
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

    public HttpHeaders prepareHeaders() {
        HttpHeaders headers = new HttpHeaders();
//        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
//        headers.add(properties.getTokenName(), properties.getTokenValue());
        return headers;
    }

    public HttpHeaders prepareHeaders(Locale locale) {
        HttpHeaders httpHeaders = prepareHeaders();
        httpHeaders.add("locale", locale.toLanguageTag());
        return httpHeaders;
    }
}
