package me.exrates.controller.filter;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import me.exrates.ProcessIDManager;


@Log4j2(topic = "http_process_logger")
public class HttpLoggingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        // empty
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ProcessIDManager.registerNewProcessForRequest(HttpLoggingFilter.class, (HttpServletRequest) request);
        try {
            chain.doFilter(request, response);
        } finally {
            ProcessIDManager.unregisterProcessId(getClass());
        }
    }

    @Override
    public void destroy() {
        // empty
    }
}
