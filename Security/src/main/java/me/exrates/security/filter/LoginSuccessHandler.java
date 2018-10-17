package me.exrates.security.filter;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.UserIpDto;
import me.exrates.model.enums.UserIpState;
import me.exrates.security.ipsecurity.IpTypesOfChecking;
import me.exrates.security.ipsecurity.IpBlockingService;
import me.exrates.service.SessionParamsService;
import me.exrates.service.SurveyService;
import me.exrates.service.UserService;
import me.exrates.service.util.IpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by Valk on 28.04.2016.
 */
@Log4j2
@PropertySource("classpath:session.properties")
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {


    @Autowired
    private SessionParamsService sessionParamsService;
    @Autowired
    MessageSource messageSource;
    @Autowired
    LocaleResolver localeResolver;
    @Autowired
    private UserService userService;
    @Autowired
    private IpBlockingService ipBlockingService;


    public LoginSuccessHandler() {

    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        super.setAlwaysUseDefaultTargetUrl(false);
        try {
            User principal = (User) authentication.getPrincipal();
            log.info("Authentication succeeded for user: " + principal.getUsername());
            request.getSession().setMaxInactiveInterval(0);
            sessionParamsService.setSessionLifeParams(request);
            Locale locale = new Locale(userService.getPreferedLang(userService.getIdByEmail(principal.getUsername())));
            localeResolver.setLocale(request, response, locale);
        /**/
            request.getSession().removeAttribute("errorNoty");
            request.getSession().removeAttribute("successNoty");
        /**/
            String email = authentication.getName();
            String ip = request.getHeader("X-FORWARDED-FOR");
            if (ip == null) {
                ip = IpUtils.getClientIpAddress(request, 100);
            }
            UserIpDto userIpDto = userService.getUserIpState(email, ip);
            if (userIpDto.getUserIpState() == UserIpState.NEW) {
                userService.insertIp(email, ip);
                me.exrates.model.User u = new me.exrates.model.User();
                u.setId(userIpDto.getUserId());
                u.setEmail(email);
                u.setIp(ip);
                userService.sendUnfamiliarIpNotificationEmail(u, "emailsubmitnewip.subject", "emailsubmitnewip.text", locale);
            }
            userService.setLastRegistrationDate(userIpDto.getUserId(), ip);
            ipBlockingService.successfulProcessing(ip, IpTypesOfChecking.LOGIN);
            String lastPage = (String) request.getSession().getAttribute("lastPageBeforeLogin");
            request.getSession().removeAttribute("lastPageBeforeLogin");
            if (!StringUtils.isEmpty(lastPage)) {
                super.setDefaultTargetUrl(lastPage);
            }
            WebUtils.setSessionAttribute(request,"first_entry_after_login", true);
            super.onAuthenticationSuccess(request, response, authentication);
        } catch (Exception e) {
            log.error(e);
            authentication.setAuthenticated(false);
        }
    }


}
