package me.exrates.controller.interceptor;

import lombok.extern.log4j.Log4j2;
import me.exrates.service.session.UserSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Log4j2
public class SecurityInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private UserSessionService userSessionService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        SessionInformation information = userSessionService.getSessionInfo(request.getSession(true).getId());
        if (information != null && information.getPrincipal() != null) {
            String securityContextUserEmail = auth.getName();
            String sessionRegistryUserEmail = ((UserDetails)information.getPrincipal()).getUsername();
            if (!securityContextUserEmail.equalsIgnoreCase(sessionRegistryUserEmail)) {
                request.getSession().invalidate();
                auth.setAuthenticated(false);
            }
        }
        return true;
    }
}
