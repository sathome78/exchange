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
import org.springframework.beans.factory.annotation.Value;
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
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by Valk on 28.04.2016.
 */
@Log4j2
@PropertySource({"classpath:session.properties", "classpath:microservices.properties"})
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
    @Autowired
    Client client;

//    @Value("${auth.server.url}")
    private String authServiceUrl = "172.50.10.115";

    public LoginSuccessHandler() {

    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        super.setAlwaysUseDefaultTargetUrl(false);
        try {
            User principal = (User) authentication.getPrincipal();

            String cleanPassword = (String) request.getSession().getAttribute("clean_password");
            request.getSession().removeAttribute("clean_password");
            Form form = new Form();
            form.param("username", principal.getUsername());
            form.param("password", cleanPassword);
            form.param("grant_type", "password");
            Response resp = null;
            try {
                resp = client.target(authServiceUrl + "/oauth/token").
                        request().header(HttpHeaders.AUTHORIZATION, "Y3VybF9jbGllbnQxOnVzZXI=").post(Entity.entity(form, MediaType.MULTIPART_FORM_DATA
                ));
            } catch (Throwable e){
                System.out.println(e);
            } finally {
               if(resp != null) System.out.println(resp.readEntity(String.class));
            }
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

    public static void main(String[] args) {
        Client client = ClientBuilder.newClient();
        Response resp = client.target("172.50.10.115" + "/oauth/token").queryParam("grant_type", "password")
                .queryParam("username", "mikita.malykov@upholding.biz").queryParam("password", "123").
                        request().header(HttpHeaders.AUTHORIZATION, "Y3VybF9jbGllbnQxOnVzZXI=").post(Entity.entity("", MediaType.APPLICATION_JSON_TYPE
                        ));
        System.out.println(resp.readEntity(String.class));
    }

}
