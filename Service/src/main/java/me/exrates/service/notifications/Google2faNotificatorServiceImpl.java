package me.exrates.service.notifications;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.G2faDao;
import me.exrates.dao.NotificationUserSettingsDao;
import me.exrates.model.User;
import me.exrates.model.dto.Generic2faResponseDto;
import me.exrates.model.dto.NotificationsUserSetting;
import me.exrates.model.enums.NotificationEvent;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.NotificationTypeEnum;
import me.exrates.service.NotificationService;
import me.exrates.service.UserService;
import me.exrates.service.exception.MessageUndeliweredException;
import me.exrates.service.util.RestApiUtils;
import org.apache.commons.lang.StringUtils;
import org.jboss.aerogear.security.otp.Totp;
import org.jboss.aerogear.security.otp.api.Base32;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Log4j2(topic = "message_notify")
@Component
public class Google2faNotificatorServiceImpl implements NotificatorService, G2faService {

    @Autowired
    private UserService userService;
    @Autowired
    private G2faDao g2faDao;
    @Autowired
    private NotificationUserSettingsDao notificationUserSettingsDao;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private LocaleResolver localeResolver;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String QR_PREFIX = "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=";
    private static final String APP_NAME = "Exrates";

    private static final Cache<Integer, String> GOOGLE_SECRETS_STORE = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(1000L)
            .build();


    @Override
    public Object getSubscriptionByUserId(int userId) {
        return null;
    }

    @Override
    public String sendMessageToUser(String userEmail, String message, String subject) throws MessageUndeliweredException {
        return "";
    }

    @Override
    public NotificationTypeEnum getNotificationType() {
        return NotificationTypeEnum.GOOGLE2FA;
    }

    @Override
    public String generateQRUrl(String userEmail, String secretCode) throws UnsupportedEncodingException {
        return QR_PREFIX
                + URLEncoder.encode(
                        String.format(
                                "otpauth://totp/%s:%s?secret=%s&issuer=%s",
                                APP_NAME,
                                userEmail,
                                secretCode,
                                APP_NAME
                        ), "UTF-8");
    }

    @Override
    public String getGoogleAuthenticatorCode(Integer userId) {
        final String newSecret2faCode = Base32.random();

        String secret2faCode = g2faDao.getGoogleAuthSecretCodeByUser(userId);
        if (Objects.nonNull(secret2faCode) && !secret2faCode.isEmpty()) {
            g2faDao.updateGoogleAuthSecretCode(userId, newSecret2faCode, false);
        } else {
            g2faDao.setGoogleAuthSecretCode(userId, newSecret2faCode);
        }
        return newSecret2faCode;
    }


    @Override
    public void updateGoogleAuthenticatorSecretCodeForUser(Integer userId) {
        g2faDao.setGoogleAuthSecretCode(userId);
    }

    @Override
    public boolean isGoogleAuthenticatorEnable(Integer userId) {
        return g2faDao.isGoogleAuthenticatorEnable(userId);
    }

    @Override
    public boolean isGoogleAuthenticatorEnable(String email) {
        try {
            int userId = userService.getIdByEmail(email);
            return g2faDao.isGoogleAuthenticatorEnable(userId);
        } catch (Exception exc) {
            return false;
        }
    }

    @Override
    public boolean checkGoogle2faVerifyCode(String verificationCode, Integer userId) {
        String google2faSecret = g2faDao.getGoogleAuthSecretCodeByUser(userId);
        final Totp totp = new Totp(google2faSecret);
        return isValidLong(verificationCode) && totp.verify(verificationCode);
    }

    @Override
    public void setEnable2faGoogleAuth(Integer userId, Boolean connection) {
        g2faDao.setEnable2faGoogleAuth(userId, connection);
        if (!connection) {
            notificationUserSettingsDao.delete(userId);
            notificationService.notifyUser(userId, NotificationEvent.ACCOUNT, "ga.2fa_disable_title", "message.g2fa.successDisable", null);
        } else {
            notificationService.notifyUser(userId, NotificationEvent.ACCOUNT, "ga.2fa_enable_title", "message.g2fa.successEnable", null);
        }
    }

    @Override
    public Generic2faResponseDto getGoogleAuthenticatorCodeNg(Integer userId) {
        String secret = getGoogleAuthenticatorCode(userId);
        GOOGLE_SECRETS_STORE.invalidate(userId);
        GOOGLE_SECRETS_STORE.put(userId, secret);

        Generic2faResponseDto result = new Generic2faResponseDto("", "");
        result.setMessage(secret);
        if (StringUtils.isEmpty(secret)) {
            result.setError("Failed to retrieve secret code for user with id:" + userId);
        }
        return result;
    }

    @Override
    public void sendGoogleAuthPinConfirm(User user, HttpServletRequest request) {
        sendGoogleAuthPincode(user, request);
    }

    @Override
    public boolean submitGoogleSecret(User user, Map<String, String> body) {
        String password = RestApiUtils.decodePassword(body.get("PASSWORD"));
        String secret = body.get("SECRET");
        String pin = body.get("PINCODE");
        String cached = GOOGLE_SECRETS_STORE.getIfPresent(user.getId());
        if (StringUtils.isEmpty(cached) || !secret.equals(cached)) {
            return false;
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            return false;
        } else if (!userService.checkPin(user.getEmail(), pin, NotificationMessageEventEnum.CHANGE_2FA_SETTING)) {
            return false;
        }
        g2faDao.updateGoogleAuthSecretCode(user.getId(), secret, true);
        return true;
    }

    private boolean isValidLong(String code) {
        try {
            Long.parseLong(code);
        } catch (final NumberFormatException e) {
            return false;
        }
        return true;
    }

    private void sendGoogleAuthPincode(User user, HttpServletRequest request) {
        NotificationsUserSetting setting = getLoginSettings(user);
        Locale locale = localeResolver.resolveLocale(request);
        String subject = messageSource.getMessage(setting.getNotificationMessageEventEnum().getSbjCode(), null, locale);
        String pin = userService.updatePinForUserForEvent(user.getEmail(), setting.getNotificationMessageEventEnum());
        String messageText = messageSource.getMessage(setting.getNotificationMessageEventEnum().getMessageCode(),
                new String[]{pin}, locale);
        System.out.println(messageText);
        notificationService.notifyUser(user.getId(), NotificationEvent.CUSTOM, subject, messageText);
    }

    private NotificationsUserSetting getLoginSettings(User user) {
        return NotificationsUserSetting
                .builder()
                .notificationMessageEventEnum(NotificationMessageEventEnum.CHANGE_2FA_SETTING)
                .notificatorId(NotificationMessageEventEnum.CHANGE_2FA_SETTING.getCode())
                .userId(user.getId())
                .build();
    }

    @Override
    public boolean disableGoogleAuth(User user, Map<String, String> body) {
        String password = RestApiUtils.decodePassword(body.get("PASSWORD"));
        String pin = body.get("PINCODE");
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            return false;
        } else if (!userService.checkPin(user.getEmail(), pin, NotificationMessageEventEnum.CHANGE_2FA_SETTING)) {
            return false;
        }
        g2faDao.setEnable2faGoogleAuth(user.getId(), false);
        return true;
    }
}
