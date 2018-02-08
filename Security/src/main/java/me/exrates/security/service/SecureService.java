package me.exrates.security.service;

import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.security.filter.CapchaAuthorizationFilter;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * Created by Maks on 10.10.2017.
 */
public interface SecureService {

    void checkLoginAuthNg(String email, HttpServletRequest request, Locale locale);

    void checkLoginAuth(HttpServletRequest request, Authentication authentication,
                        CapchaAuthorizationFilter filter);

    String reSendLoginMessage(HttpServletRequest request, String userEmail, Locale locale);

    /*Method used For withdraw or transfer*/
    void checkEventAdditionalPin(Locale locale, String email, NotificationMessageEventEnum event, String amountCurrency);

    String resendEventPin(Locale locale, String email, NotificationMessageEventEnum event, String amountCurrency);
}
