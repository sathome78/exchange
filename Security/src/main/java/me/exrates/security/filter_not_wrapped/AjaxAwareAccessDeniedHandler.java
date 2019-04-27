package me.exrates.security.filter_not_wrapped;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by OLEG on 08.12.2016.
 */
public class AjaxAwareAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger LOGGER = LogManager.getLogger(AjaxAwareAccessDeniedHandler.class);

    @Autowired
    private LocaleResolver localeResolver;

    @Autowired
    private MessageSource messageSource;

    private String errorPage;

    public AjaxAwareAccessDeniedHandler(String errorPage) {
        this.errorPage = errorPage;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        LOGGER.debug("Access Denied Exception: " + accessDeniedException.getClass().getSimpleName() + "\n" +
                accessDeniedException.getMessage());
        LOGGER.debug("Request: " + request.getServletPath());
        LOGGER.debug("Authentication: " + SecurityContextHolder.getContext().getAuthentication());


        if (!response.isCommitted()) {
            String requestedWith = request.getHeader("X-Requested-With");
            if ("XMLHttpRequest".equals(requestedWith)) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding("UTF-8");
                Map<String, String> errorInfo = new HashMap<String, String>() {{
                    put("error", accessDeniedException.toString());
                    put("cause", accessDeniedException.getClass().getSimpleName());
                    put("detail", messageSource.getMessage("accessDenied.title", null, localeResolver.resolveLocale(request)));
                }};
                String responseString = new ObjectMapper().writeValueAsString(errorInfo);
                LOGGER.debug(responseString);
                PrintWriter writer = response.getWriter();
                writer.print(responseString);
                writer.flush();
            }
            else {
                request.setAttribute(WebAttributes.ACCESS_DENIED_403,
                        accessDeniedException);
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                RequestDispatcher dispatcher = request.getRequestDispatcher(errorPage);
                dispatcher.forward(request, response);
            }
        }
    }
}
