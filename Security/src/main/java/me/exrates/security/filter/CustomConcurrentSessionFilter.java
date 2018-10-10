package me.exrates.security.filter;

import me.exrates.model.enums.SessionLifeTypeEnum;
import me.exrates.service.SessionParamsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.util.UrlUtils;
import org.springframework.util.Assert;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.apache.commons.lang.time.DateUtils.MILLIS_PER_MINUTE;

/**
 * Created from ConcurrentSessionFilter by maks on 31.03.2017.
 */

//@PropertySources({
//        @PropertySource("classpath:session.properties"),
//        @PropertySource("classpath:angular.properties")
//})
@PropertySource(value = {
        "classpath:session.properties"
})
public class CustomConcurrentSessionFilter extends GenericFilterBean {

    @Autowired
    private SessionParamsService sessionParamsService;

    /*@Autowired
    private Map<String, String> angularProperties;*/

    private SessionRegistry sessionRegistry;
    private String expiredUrl;
    private LogoutHandler[] handlers = new LogoutHandler[] { new SecurityContextLogoutHandler() };
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();


    private Set<String> onlineMethods = new HashSet<>();
    private @Value("${session.lifeTypeParamName}") String sessionLifeTypeParamName;
    private @Value("${session.timeParamName}") String sessionTimeMinutesParamName;
    private @Value("${session.lastRequestParamName}") String sessionLastRequestParamName;



    // ~ Methods
    // ========================================================================================================

    public CustomConcurrentSessionFilter(SessionRegistry sessionRegistry) {
        Assert.notNull(sessionRegistry, "SessionRegistry required");
        this.expiredUrl = "/dashboard";
        this.sessionRegistry = sessionRegistry;
    }

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(sessionRegistry, "SessionRegistry required");
        Assert.isTrue(expiredUrl == null || UrlUtils.isValidRedirectUrl(expiredUrl),
                expiredUrl + " isn't a valid redirect URL");
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;


        HttpSession session = request.getSession(false);
        if (session != null) {
            SessionInformation info = sessionRegistry.getSessionInformation(session
                    .getId());
            if (info != null) {
                if (info.isExpired()) {
                    // Expired - abort processing
                    doLogout(request, response);
                    String targetUrl = determineExpiredUrl(request, info);
                    if (targetUrl != null) {
                        logger.error("do logout, sending redirect");
                        redirectStrategy.sendRedirect(request, response, targetUrl);
                        return;
                    }
                    else {
                        response.getWriter().print(
                                "This session has been expired (possibly due to multiple concurrent "
                                        + "logins being attempted as the same user).");
                        response.flushBuffer();
                    }
                    return;
                }
                else {
                    if(isSessionExpired(session)) {
                        /*JsonObject object = sessionParamsService.getSessionEndString(request);*/
                        if (isAjax(request)) {
                            response.setStatus(419);
                            /*PrintWriter writer = response.getWriter();
                            writer.print(object.toString());
                            writer.close();*/
                        } else {
                            response.sendRedirect("/dashboard?sessionEnd");
                        }
                        doLogout(request, response);
                        return;
                    } else if (isRefreshNeeded(request)) {
                        session.setAttribute(sessionLastRequestParamName, System.currentTimeMillis());
                    }
                    // Non-expired - update last request date/time
                    sessionRegistry.refreshLastRequest(info.getSessionId());
                }
            }
        }
        chain.doFilter(request, response);
    }

    private boolean isRefreshNeeded(HttpServletRequest request) {
        HttpSession session = request.getSession();
        SessionLifeTypeEnum sessionLifeTypeEnum = SessionLifeTypeEnum
                .convert((int)session.getAttribute(sessionLifeTypeParamName));
        if (sessionLifeTypeEnum.isRefreshOnUserRequests() && isPathForSessionRefresh(request)) {
            return true;
        }
        return false;
    }

    private boolean isSessionExpired(HttpSession session) {
        Integer sessionLifeTime = (int)session.getAttribute(sessionTimeMinutesParamName);
        long lastReq = (long)session.getAttribute(sessionLastRequestParamName);
        return lastReq + sessionLifeTime * MILLIS_PER_MINUTE <= System.currentTimeMillis();
    }

    private boolean isPathForSessionRefresh(HttpServletRequest request) {
        String path = request.getServletPath();
        boolean needRefresh;
        needRefresh = !onlineMethods
                .stream()
                .filter(e -> (e.matches(".*/$") && path.matches("^" + e + ".*")) || (e.matches(".*[^/]$") && path.equals(e)))
                .findFirst().isPresent()
                && !path.matches("^/rest/.*")
                && !path.matches("^/api/.*")
                && !path.matches("^/client/.*")
                && !path.matches("^/public/.*");
        return needRefresh;
    }

    protected String determineExpiredUrl(HttpServletRequest request,
                                         SessionInformation info) {
        return expiredUrl;
    }

    private boolean isAjax(HttpServletRequest request) {
        String requestedWithHeader = request.getHeader("X-Requested-With");
        return "XMLHttpRequest".equals(requestedWithHeader);
    }

    private void doLogout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        for (LogoutHandler handler : handlers) {
            handler.logout(request, response, auth);
        }
    }

    public Set<String> getOnlineMethods() {
        return onlineMethods;
    }

    public void setOnlineMethods(Set<String> onlineMethods) {
        this.onlineMethods = onlineMethods;
    }

    public void setLogoutHandlers(LogoutHandler[] handlers) {
        Assert.notNull(handlers);
        this.handlers = handlers;
    }

    public void setRedirectStrategy(RedirectStrategy redirectStrategy) {
        this.redirectStrategy = redirectStrategy;
    }

}
