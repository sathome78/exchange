package me.exrates.security.service;

import me.exrates.model.dto.PinDto;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.security.filter.CapchaAuthorizationFilter;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by Maks on 10.10.2017.
 */
public interface SecureService {

    void checkLoginAuth(HttpServletRequest request, Authentication authentication,
                        CapchaAuthorizationFilter filter);

    PinDto reSendLoginMessage(HttpServletRequest request, String userEmail, boolean forceSend);

    /*Method used For withdraw or transfer*/
    void checkEventAdditionalPin(HttpServletRequest request, String email, NotificationMessageEventEnum event, String amountCurrency);

    PinDto resendEventPin(HttpServletRequest request, String email, NotificationMessageEventEnum event, String amountCurrency);
}
