package me.exrates.security.filter;

import me.exrates.service.SessionParamsService;
import me.exrates.service.events.QRLoginEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by OLEG on 11.10.2016.
 */
public class QRAuthorizationFilter extends GenericFilterBean {

    @Autowired
    private SessionParamsService sessionParamsService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private SessionAuthenticationStrategy authenticationStrategy = new NullAuthenticatedSessionStrategy();


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest httpServletRequest = ((HttpServletRequest) request);
            HttpSession session = httpServletRequest.getSession();
            UserDetails userDetails = (UserDetails) session.getAttribute("USER_DETAIL_TOKEN");
            if (userDetails != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                Object mutex = WebUtils.getSessionMutex(session);
                synchronized (mutex) {
                    session.removeAttribute("USER_DETAIL_TOKEN");
                  /*  session.setAttribute("QR_LOGGED_IN", true);*/
                }
                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails,
                        null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
                SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
                sessionParamsService.setSessionLifeParams((HttpServletRequest) request);
                authenticationStrategy.onAuthentication(auth, httpServletRequest, ((HttpServletResponse)response));
                eventPublisher.publishEvent(new QRLoginEvent(httpServletRequest));
            }

        }
        chain.doFilter(request, response);
    }

    public SessionAuthenticationStrategy getAuthenticationStrategy() {
        return authenticationStrategy;
    }

    public void setAuthenticationStrategy(SessionAuthenticationStrategy authenticationStrategy) {
        this.authenticationStrategy = authenticationStrategy;
    }

}
