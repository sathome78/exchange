package me.exrates.security.service;

import me.exrates.model.User;
import me.exrates.model.dto.NotificationResultDto;
import me.exrates.model.dto.PinDto;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.security.filter.CapchaAuthorizationFilter;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * Created by Maks on 10.10.2017.
 */
public interface SecureService {

    void checkLoginAuth(HttpServletRequest request, Authentication authentication,
                        CapchaAuthorizationFilter filter);

    PinDto reSendLoginMessage(HttpServletRequest request, String userEmail, boolean forceSend);

    PinDto reSendLoginMessage(HttpServletRequest request, String userEmail,  Locale locale);

    /*Method used For withdraw or transfer*/
    void checkEventAdditionalPin(HttpServletRequest request, String email, NotificationMessageEventEnum event, String amountCurrency);

    PinDto resendEventPin(HttpServletRequest request, String email, NotificationMessageEventEnum event, String amountCurrency);

    NotificationResultDto sendWithdrawPincode(User user);

    NotificationResultDto sendLoginPincode(User user, HttpServletRequest request, String ipAddress);

    NotificationResultDto sendApiTokenPincode(User user, HttpServletRequest request);

    void checkLoginAuthNg(String email, HttpServletRequest request, Locale locale);

    NotificationResultDto sendWithdrawPinCode(User user, String amount, String currencyName);

    NotificationResultDto sendTransferPinCode(User user, String amount, String currencyName);

}
