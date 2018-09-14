package me.exrates.security.filter;

import me.exrates.security.exception.BannedIpException;
import me.exrates.security.service.IpBlockingService;
import me.exrates.service.util.IpUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by Valk on 31.03.16.
 */
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    private static final Logger LOGGER = LogManager.getLogger(LoginFailureHandler.class);

    @Autowired
    private IpBlockingService ipBlockingService;

    private String redirectUrl;

    public LoginFailureHandler(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        //it's nessary to "save" exception that was thrown. This exception will be used in MaimController @RequestMapping(value = "/login", method = RequestMethod.GET)
        LOGGER.info("Authentication failed. Cause: " + exception.getMessage());
        if (!(exception instanceof BannedIpException)) {
            String ipAddress = IpUtils.getClientIpAddress(request);
            ipBlockingService.processLoginFailure(ipAddress);
        }
        HttpSession session = request.getSession(false);
        session.setAttribute("SPRING_SECURITY_LAST_EXCEPTION", exception);
        //
        response.sendRedirect("/login?error");

    }
}
