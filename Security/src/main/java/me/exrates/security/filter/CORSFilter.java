package me.exrates.security.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;

@Order(Integer.MIN_VALUE)
public class CORSFilter extends GenericFilterBean {

	private static final Logger LOGGER = LogManager.getLogger(LoginFailureHandler.class);

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
		HttpServletResponse response = (HttpServletResponse) resp;
		HttpServletRequest request = (HttpServletRequest) req;

		String domain = new URL(request.getRequestURL().toString()).getHost();
		int port = new URL(request.getRequestURL().toString()).getPort();
		/*if (domain.equals("localhost")) {
			response.setHeader("Access-Control-Allow-Origin", "http://localhost:9000");
		} else {
			response.setHeader("Access-Control-Allow-Origin", "http://dev2.exrates.tech");
		}*/
		System.out.println("domain " + domain);
		System.out.println("req_port " + port);
		response.setHeader("Access-Control-Allow-Origin", "http://localhost:9001");
		response.setHeader("Access-control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
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
			System.out.println("Pre-flight for uri: " + ((HttpServletRequest) request).getRequestURI());
			response.setHeader("Access-Control-Allowed-Methods", "POST, GET, DELETE");
			response.setHeader("Access-Control-Max-Age", "3600");
			response.setHeader("Access-Control-Allow-Headers", "authorization, content-type, X-Forwarded-For, x-auth-token, Exrates-Rest-Token, " +
					"access-control-request-headers,access-control-request-method,accept,origin,authorization,x-requested-with");
			response.setStatus(HttpServletResponse.SC_OK);
		}
	}
}
