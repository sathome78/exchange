package me.exrates.security.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CORSFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String path = ((HttpServletRequest) request).getRequestURI();
        if (path.startsWith("/info")) {
            httpResponse.setHeader("Access-Control-Allow-Origin", "http://localhost:4200");
            httpResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
            httpResponse.setHeader("Access-Control-Allow-Headers", "X-Auth-Token, Exrates-Rest-Token, Content-Type");
            httpResponse.setHeader("Access-Control-Allow-Credentials", "false");
            httpResponse.setHeader("Access-Control-Max-Age", "4800");
        }
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
