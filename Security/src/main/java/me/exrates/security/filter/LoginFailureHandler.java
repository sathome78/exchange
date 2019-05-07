package me.exrates.security.filter;

import me.exrates.security.exception.BannedIpException;
import me.exrates.security.ipsecurity.IpBlockingService;
import me.exrates.security.ipsecurity.IpTypesOfChecking;
import me.exrates.service.util.IpUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private static final Logger LOGGER = LogManager.getLogger(LoginFailureHandler.class);

    @Autowired
    private IpBlockingService ipBlockingService;

    private String redirectUrl;

    public LoginFailureHandler(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        System.out.println("Authentication failed. Cause: " + exception.getMessage());
        LOGGER.info("Authentication failed. Cause: " + exception.getMessage());
        HttpSession session = request.getSession();
        session.setAttribute("SPRING_SECURITY_LAST_EXCEPTION", exception);
        response.sendRedirect("/login?error");
        try {
            if (!(exception instanceof BannedIpException)) {
               String ipAddress = IpUtils.getClientIpAddress(request);
               ipBlockingService.failureProcessing(ipAddress, IpTypesOfChecking.LOGIN);
           }
        } catch (Exception e) {
            LOGGER.error(ExceptionUtils.getFullStackTrace(e));
        }
        LOGGER.info("send redirect, sessionId " + session.getId());
    }
}
