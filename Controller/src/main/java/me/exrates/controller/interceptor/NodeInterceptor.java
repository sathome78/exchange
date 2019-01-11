package me.exrates.controller.interceptor;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class NodeInterceptor extends HandlerInterceptorAdapter {
    private final String NODE_TOKEN_VALUE;
    private final String NODE_TOKEN = "NODE_TOKEN";

    public NodeInterceptor(String secret) {
        this.NODE_TOKEN_VALUE = secret;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String token = request.getHeader(NODE_TOKEN);

        if(token == null || !token.equals(NODE_TOKEN_VALUE)) {
            response.setStatus(403);
            response.getWriter().write("Incorrect token");
            return false;
        }
        else return true;
    }
}
