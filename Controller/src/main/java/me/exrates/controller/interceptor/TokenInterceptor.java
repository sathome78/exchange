package me.exrates.controller.interceptor;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TokenInterceptor extends HandlerInterceptorAdapter {
    private final String AUTH_TOKEN_VALUE;
    private final String AUTH_TOKEN = "AUTH_TOKEN";

    public TokenInterceptor(String secret) {
        this.AUTH_TOKEN_VALUE = secret;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String token = request.getHeader(AUTH_TOKEN);
        if(token == null || !token.equals(AUTH_TOKEN_VALUE)) {
            response.setStatus(403);
            response.getWriter().write("Incorrect token");
            return false;
        }
        else return true;
    }
}
