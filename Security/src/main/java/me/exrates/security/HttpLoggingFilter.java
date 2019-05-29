package me.exrates.security;


import processIdManager.ProcessIDManager;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(filterName="logging_filter")
public class HttpLoggingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        System.out.println("init loggigng filter");
        // empty
    }

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
