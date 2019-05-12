package me.exrates.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.security.exception.TokenException;
import me.exrates.security.service.AuthTokenService;
import me.exrates.service.exception.api.ApiError;
import me.exrates.service.exception.api.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by OLEG on 22.08.2016.
 */
public class AuthenticationTokenProcessingFilter extends AbstractAuthenticationProcessingFilter {

    private final String HEADER_SECURITY_TOKEN = "Exrates-Rest-Token";

    @Autowired
    private AuthTokenService authTokenService;

    public AuthenticationTokenProcessingFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
        setAuthenticationSuccessHandler((request, response, authentication) ->
        {
            String pathInfo = request.getPathInfo() == null ? "" : request.getPathInfo();
            request.getRequestDispatcher(request.getServletPath() + pathInfo).forward(request, response);
        });
        setAuthenticationFailureHandler((request, response, authenticationException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            Throwable cause = authenticationException;
            while (cause.getCause() != null) {
                cause = cause.getCause();
            }
            ErrorCode errorCode = ErrorCode.FAILED_AUTHENTICATION;
            if (cause instanceof TokenException) {
                TokenException tokenEx = (TokenException) cause;
                errorCode = tokenEx.getErrorCode();
            }
            ApiError apiError = new ApiError(errorCode, request.getRequestURL().toString(), cause);
            String responseString = new ObjectMapper().writeValueAsString(apiError);
            logger.debug(responseString);
            ServletOutputStream out = response.getOutputStream();
            out.print(responseString);
            out.flush();
        });
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        String token = request.getHeader(HEADER_SECURITY_TOKEN);

        UserDetails userDetails = authTokenService.getUserByToken(token);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return authentication;
    }

    @Override
    protected AuthenticationManager getAuthenticationManager() {
        return super.getAuthenticationManager();
    }

    @Autowired
    @Override
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
    }
}
