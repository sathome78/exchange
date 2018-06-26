package me.exrates.security.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.ObjectArrays;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.NotificationResultDto;
import me.exrates.model.dto.NotificationsUserSetting;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.NotificationTypeEnum;
import me.exrates.security.exception.PinCodeCheckNeedException;
import me.exrates.security.filter.CapchaAuthorizationFilter;
import me.exrates.service.UserService;
import me.exrates.service.notifications.NotificationMessageService;
import me.exrates.service.notifications.NotificationsSettingsService;
import me.exrates.service.util.IpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;


/**
 * Created by Maks on 28.09.2017.
 */
@Log4j2
@Service("secureServiceImpl")
@PropertySource("classpath:session.properties")
public class SecureServiceImpl implements SecureService {

    private @Value("${session.checkPinParam}") String checkPinParam;
    private @Value("${session.authenticationParamName}") String authenticationParamName;
    private @Value("${session.passwordParam}") String passwordParam;

    @Autowired
    private NotificationMessageService notificationService;
    @Autowired
    private UserService userService;
    @Autowired
    private LocaleResolver localeResolver;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private NotificationsSettingsService settingsService;


    @Override
    public void checkLoginAuth(HttpServletRequest request, Authentication authentication,
                               CapchaAuthorizationFilter filter) {
        String result = reSendLoginMessage(request, authentication.getName());
        if (result != null) {
            request.getSession().setAttribute(checkPinParam, "");
            request.getSession().setAttribute(authenticationParamName, authentication);
            request.getSession().setAttribute(passwordParam, request.getParameter(filter.getPasswordParameter()));
            authentication.setAuthenticated(false);
            throw new PinCodeCheckNeedException(result);
        }
    }

    @Override
    public String reSendLoginMessage(HttpServletRequest request, String userEmail) {
        int userId = userService.getIdByEmail(userEmail);
        NotificationMessageEventEnum event = NotificationMessageEventEnum.LOGIN;
        NotificationsUserSetting setting = settingsService.getByUserAndEvent(userId, event);
        if (userService.isGlobal2FaActive() || (setting != null && setting.getNotificatorId() != null) ) {
            if (setting == null) {
                setting = NotificationsUserSetting.builder()
                        .notificatorId(NotificationTypeEnum.EMAIL.getCode())
                        .userId(userId)
                        .notificationMessageEventEnum(event)
                        .build();
            }
            if (setting.getNotificatorId() == null) {
                setting.setNotificatorId(NotificationTypeEnum.EMAIL.getCode());
            }
            log.debug("noty_setting {}", setting.toString());
            return sendPinMessage(userEmail, setting, request, new String[]{IpUtils.getClientIpAddress(request, 18)});
        }
        return null;
    }


    /*Method used For withdraw or transfer*/
    @Override
    public void checkEventAdditionalPin(HttpServletRequest request, String email,
                                        NotificationMessageEventEnum event, String amountCurrency) {
        String result = resendEventPin(request, email, event, amountCurrency);
        if (result != null) {
            throw new PinCodeCheckNeedException(result);
        }
    }

    @Override
    public String resendEventPin(HttpServletRequest request, String email, NotificationMessageEventEnum event, String amountCurrency) {
        Preconditions.checkArgument(event.equals(NotificationMessageEventEnum.TRANSFER) || event.equals(NotificationMessageEventEnum.WITHDRAW));
        int userId = userService.getIdByEmail(email);
        NotificationsUserSetting setting = settingsService.getByUserAndEvent(userId, event);
        if ((setting != null && setting.getNotificatorId() != null) || !event.isCanBeDisabled()) {
            setting = NotificationsUserSetting.builder()
                    .notificatorId(NotificationTypeEnum.EMAIL.getCode())
                    .userId(userId)
                    .notificationMessageEventEnum(event)
                    .build();
            return sendPinMessage(email, setting, request, new String[]{amountCurrency});
        }
        return null;
    }

    private String sendPinMessage(String email, NotificationsUserSetting setting, HttpServletRequest request, String[] args) {
        Locale locale = localeResolver.resolveLocale(request);
        String subject = messageSource.getMessage(setting.getNotificationMessageEventEnum().getSbjCode(), null, locale);
        String[] pin = new String[]{userService.updatePinForUserForEvent(email, setting.getNotificationMessageEventEnum())};
        String messageText = messageSource.getMessage(setting.getNotificationMessageEventEnum().getMessageCode(),
                ObjectArrays.concat(pin, args, String.class), locale);
        NotificationResultDto notificationResultDto = notificationService.notifyUser(email, messageText, subject, setting);
        return messageSource.getMessage(notificationResultDto.getMessageSource(), notificationResultDto.getArguments(), locale);
    }

}
