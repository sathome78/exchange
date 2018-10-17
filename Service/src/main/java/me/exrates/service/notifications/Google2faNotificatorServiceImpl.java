package me.exrates.service.notifications;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.G2faDao;
import me.exrates.dao.NotificationUserSettingsDao;
import me.exrates.model.User;
import me.exrates.model.dto.NotificationsUserSetting;
import me.exrates.model.enums.NotificationEvent;
import me.exrates.model.enums.NotificationTypeEnum;
import me.exrates.service.NotificationService;
import me.exrates.service.UserService;
import me.exrates.service.exception.MessageUndeliweredException;
import org.jboss.aerogear.security.otp.Totp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

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

    private static String QR_PREFIX = "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=";
    private static String APP_NAME = "Exrates";


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
    public String generateQRUrl(String userEmail) throws UnsupportedEncodingException {
        User user = userService.findByEmail(userEmail);
        String secret2faCode = getGoogleAuthenticatorCode(user.getId());
        return QR_PREFIX + URLEncoder.encode(String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s", APP_NAME, userEmail, secret2faCode, APP_NAME), "UTF-8");
    }

    @Override
    public String getGoogleAuthenticatorCode(Integer userId) {
        String secret2faCode = g2faDao.getGoogleAuthSecretCodeByUser(userId);
        if (secret2faCode == null || secret2faCode.isEmpty()){
            g2faDao.set2faGoogleAuthenticator(userId);
            secret2faCode = g2faDao.getGoogleAuthSecretCodeByUser(userId);
        }
        return secret2faCode;
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
    public boolean checkGoogle2faVerifyCode(String verificationCode, Integer userId) {

        String google2faSecret = g2faDao.getGoogleAuthSecretCodeByUser(userId);
        final Totp totp = new Totp(google2faSecret);
        if (!isValidLong(verificationCode) || !totp.verify(verificationCode)) {
            return false;
        }
        return true;
    }

    @Override
    public void setEnable2faGoogleAuth(Integer userId, Boolean connection){
        g2faDao.setEnable2faGoogleAuth(userId, connection);
        if (!connection) {
            notificationUserSettingsDao.delete(userId);
            notificationService.notifyUser(userId, NotificationEvent.ACCOUNT, "ga.2fa_disable_title", "message.g2fa.successDisable", null);
        } else {
            notificationService.notifyUser(userId, NotificationEvent.ACCOUNT, "ga.2fa_enable_title", "message.g2fa.successEnable", null);
        }
    }

    private boolean isValidLong(String code) {
        try {
            Long.parseLong(code);
        } catch (final NumberFormatException e) {
            return false;
        }
        return true;
    }
}
