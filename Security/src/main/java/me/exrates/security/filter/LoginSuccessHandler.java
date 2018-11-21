package me.exrates.security.filter;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.UserIpDto;
import me.exrates.model.enums.UserIpState;
import me.exrates.security.ipsecurity.IpBlockingService;
import me.exrates.security.ipsecurity.IpTypesOfChecking;
import me.exrates.service.SessionParamsService;
import me.exrates.service.UserService;
import me.exrates.service.util.IpUtils;
import org.json.JSONObject;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import java.util.Locale;

/**
 * Created by Valk on 28.04.2016.
 */
@Log4j2
@PropertySource("classpath:/auth_server.properties")
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

    @Value("${auth.server.host}")
    private String authServiceHost;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        super.setAlwaysUseDefaultTargetUrl(false);
        try {
            User principal = (User) authentication.getPrincipal();
            setAuthTokens(request, principal);

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

    private void setAuthTokens(HttpServletRequest request, User principal) {
        try {
            String rawPassword = (String) request.getSession().getAttribute("raw_password");
            request.getSession().removeAttribute("raw_password");

            OkHttpClient cl = new OkHttpClient();

            Request req = new Request.Builder()
                    .url("http://" + authServiceHost + "/oauth/token?grant_type=password&username=" + principal.getUsername() + "&password=" + rawPassword)
                    .post(RequestBody.create(com.squareup.okhttp.MediaType.parse(MediaType.APPLICATION_FORM_URLENCODED), ""))
                    .addHeader("authorization", "Basic Y3VybF9jbGllbnQxOnVzZXI=")
                    .addHeader("cache-control", "no-cache")
                    .addHeader("Content-Type", MediaType.APPLICATION_FORM_URLENCODED)
                    .build();

            Response response = cl.newCall(req).execute();
            JSONObject tokensJson = new JSONObject(response.body().string());

            System.out.println(tokensJson);
            log.info("User " + principal.getUsername() + " getted tokens");

            request.getSession().setAttribute("access_token", tokensJson.getString("access_token"));
            request.getSession().setAttribute("refresh_token", tokensJson.getString("refresh_token"));
        } catch (Throwable e) {
            log.error(e);
        }
    }

}
