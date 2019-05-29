package me.exrates.controller.filter;


import org.springframework.web.filter.GenericFilterBean;
import processIdManager.ProcessIDManager;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class HttpLoggingFilter extends GenericFilterBean {


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("do loggigng filter");
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
