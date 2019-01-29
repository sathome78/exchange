package me.exrates.security.filter;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExratesCorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) {
        HttpServletResponse response = (HttpServletResponse) resp;
        HttpServletRequest request = (HttpServletRequest) req;

        // todo uncomment if current branch is angular-master-local, otherwise should be commented
//        response.setHeader("Access-Control-Allow-Origin", "http://localhost:4200");

        // todo uncomment if current branch is angular-master-dev, otherwise should be commented
//        response.setHeader("Access-Control-Allow-Origin", "http://dev6.exapp");

        // todo uncomment if current branch is angular-master, otherwise should be commented
        response.setHeader("Access-Control-Allow-Origin", "https://demo.exrates.me");


        response.setHeader("Access-control-Allow-Methods", "POST, PUT, PATCH, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with, X-Forwarded-For, x-auth-token, Exrates-Rest-Token, GACookies, client_ip");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        if (!(request.getMethod().equalsIgnoreCase("OPTIONS"))) {
            try {
                chain.doFilter(req, resp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Pre-flight for uri: " + (request).getRequestURI());
            response.setHeader("Access-Control-Allowed-Methods", "POST, GET, DELETE");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers", "authorization, content-type, X-Forwarded-For, x-auth-token, Exrates-Rest-Token, GACookies, " +
                    "client_ip, access-control-request-headers,access-control-request-method,accept,origin,authorization,x-requested-with");
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void destroy() {}
}
