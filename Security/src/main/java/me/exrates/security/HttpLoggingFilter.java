package me.exrates.security;


import org.springframework.web.filter.GenericFilterBean;
import processIdManager.ProcessIDManager;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class HttpLoggingFilter extends GenericFilterBean {


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("do loggigng filter " );
        System.out.println(Thread.currentThread().getName());
        ProcessIDManager.registerNewProcessForRequest(getClass(), (HttpServletRequest) request);
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
