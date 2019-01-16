package me.exrates.controller.interceptor;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

public class MDCInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        ThreadContext.put("process.id", String.valueOf(UUID.randomUUID()));
        return true;
    }
}
