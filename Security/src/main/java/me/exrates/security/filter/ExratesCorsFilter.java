package me.exrates.security.filter;

import org.springframework.core.annotation.Order;

import java.io.IOException;
import java.net.URL;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Order(Integer.MIN_VALUE)
public class ExratesCorsFilter implements Filter {

    private final String angularOrigins;

    public ExratesCorsFilter(String angularOrigins) {
        this.angularOrigins = angularOrigins;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) resp;
        HttpServletRequest request = (HttpServletRequest) req;

        URL reqUrl =   new URL(request.getRequestURL().toString());
//        System.out.println(reqUrl.toString());
		/*if (domain.equals("localhost")) {
			response.setHeader("Access-Control-Allow-Origin", "http://localhost:9000");
		} else {
			response.setHeader("Access-Control-Allow-Origin", "http://dev2.exrates.tech");
		}*/
        ;
//        System.out.println("protocol " + reqUrl.getProtocol());
//        System.out.println("domain " + reqUrl.getHost());
//        System.out.println("req_port " + reqUrl.getPort());

//        String path = String.join("", reqUrl.getProtocol(), "://", reqUrl.getHost());
//        if (reqUrl.getHost().equals("localhost")) {
//            path = String.join("", path, ":", String.valueOf(reqUrl.getPort()));
//        }
//        System.out.println("header path " + path);
        response.setHeader("Access-Control-Allow-Origin", angularOrigins);
        response.setHeader("Access-control-Allow-Methods", "POST, PUT, PATCH, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Allow-Headers", "x-requested-with, X-Forwarded-For, x-auth-token, Exrates-Rest-Token");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        if(!(request.getMethod().equalsIgnoreCase("OPTIONS"))) {
            try {
                chain.doFilter(req, resp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Pre-flight for uri: " + (request).getRequestURI());
            response.setHeader("Access-Control-Allowed-Methods", "POST, GET, DELETE");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.setHeader("Access-Control-Allow-Headers", "authorization, content-type, X-Forwarded-For, x-auth-token, Exrates-Rest-Token, " +
                    "access-control-request-headers,access-control-request-method,accept,origin,authorization,x-requested-with");
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
