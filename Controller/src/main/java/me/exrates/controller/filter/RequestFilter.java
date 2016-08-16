package me.exrates.controller.filter;

import me.exrates.controller.OnlineRestController;
import me.exrates.controller.annotation.OnlineMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Enumeration;

/**
 * Created by Valk on 18.07.2016.
 */
public class RequestFilter implements Filter {
    private static final Logger LOGGER = LogManager.getLogger(OnlineRestController.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String path = ((HttpServletRequest) request).getServletPath();
        boolean resetSessionLifetimeHard = false;
        /*"refreshIfNeeded" is false when the request is generated by user activity */
        if ("false".equals(request.getParameter("refreshIfNeeded"))) {
            resetSessionLifetimeHard = true;
        }
        if (!(OnlineRestController.SESSION_LIFETIME_HARD == 0 ||
                resetSessionLifetimeHard ||
                path.matches("^/client/.*") ||
                path.matches(".*/js/.*") ||
                path.matches(".*/css/.*") ||
                path.matches(".*/font/.*") ||
                path.matches(".*/fonts/.*") ||
                path.matches(".*/images/.*") ||
                path.matches(".*/img/.*"))
                ) {
            resetSessionLifetimeHard = true;
            for (Method onlineRestControllerMethod : OnlineRestController.class.getDeclaredMethods()) {
                OnlineMethod onlineMethodAnnotation = (OnlineMethod) onlineRestControllerMethod.getDeclaredAnnotation(OnlineMethod.class);
                if (onlineMethodAnnotation != null) {
                    RequestMapping mapping = (RequestMapping) onlineRestControllerMethod.getDeclaredAnnotation(RequestMapping.class);
                    if (mapping != null) {
                        if ("false".equals(request.getParameter("refreshIfNeeded"))) {

                        }
                        for (String url : mapping.value()) {
                            url = url.split("/\\{")[0];
                            if (path.matches("^" + url + ".*")) {
                                resetSessionLifetimeHard = false;
                                break;
                            }
                        }
                    }
                }
            }

        }
        if (resetSessionLifetimeHard) {
            LOGGER.trace(" resetSessionLifetimeHard by request: " + path);
            ((HttpServletRequest) request).getSession().setAttribute("sessionEndTime", new Date().getTime() + OnlineRestController.SESSION_LIFETIME_HARD * 1000);
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
