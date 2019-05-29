package me.exrates.security;


import org.apache.commons.lang3.StringUtils;
import processIdManager.ProcessIDManager;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(filterName="logging_filter")
public class HttpLoggingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        // empty
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ProcessIDManager
                .getProcessIdFromCurrentThread()
                .orElseGet(() -> {
                    ProcessIDManager.registerNewProcessForRequest(HttpLoggingFilter.class, (HttpServletRequest) request);
                    return StringUtils.EMPTY;
                });
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
