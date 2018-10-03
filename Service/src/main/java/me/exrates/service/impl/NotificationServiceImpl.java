package me.exrates.service.impl;

import me.exrates.dao.NotificationDao;
import me.exrates.dao.NotificationUserSettingsDao;
import me.exrates.model.Email;
import me.exrates.model.Notification;
import me.exrates.model.NotificationOption;
import me.exrates.model.User;
import me.exrates.model.dto.onlineTableDto.NotificationDto;
import me.exrates.model.enums.NotificationEvent;
import me.exrates.model.vo.CacheData;
import me.exrates.service.NotificationService;
import me.exrates.service.SendMailService;
import me.exrates.service.UserService;
import me.exrates.service.util.Cache;
import org.apache.commons.lang3.StringUtils;
import org.jboss.aerogear.security.otp.Totp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by OLEG on 10.11.2016.
 */
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    public static String QR_PREFIX = "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=";
    public static String APP_NAME = "Exrates";

    @Autowired
    private NotificationDao notificationDao;

    @Autowired
    private UserService userService;

    @Autowired
    private SendMailService sendMailService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private NotificationUserSettingsDao notificationUserSettingsDao;

    // TODO manage notifications in admin page


    /*private long createNotification(Integer userId, String title, String message, NotificationEvent cause) {
        Notification notification = new Notification();
        notification.setReceiverUserId(userId);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setCause(cause);
        return notificationDao.createNotification(notification);
    }*/

    @Override
    public long createLocalizedNotification(Integer userId, NotificationEvent cause, String titleCode, String messageCode,
                                            Object[] messageArgs) {
         Locale locale = new Locale(userService.getPreferedLang(userId));
        return 0L /*createNotification(userId, messageSource.getMessage(titleCode, null, locale),
                messageSource.getMessage(messageCode, normalizeArgs(messageArgs), locale), cause)*/;

    }

    @Override
    public long createLocalizedNotification(String userEmail, NotificationEvent cause, String titleCode, String messageCode,
                                            Object[] messageArgs) {
        Integer userId = userService.getIdByEmail(userEmail);
        Locale locale = new Locale(userService.getPreferedLang(userId));
        return 0L /*createNotification(userId, messageSource.getMessage(titleCode, null, locale),
                messageSource.getMessage(messageCode, normalizeArgs(messageArgs), locale), cause)*/;

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void notifyUser(String email, NotificationEvent cause, String titleCode, String messageCode,
                           Object[] messageArgs) {
        notifyUser(userService.getIdByEmail(email), cause, titleCode, messageCode, normalizeArgs(messageArgs));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void notifyUser(String email, NotificationEvent cause, String titleCode, String messageCode,
                           Object[] messageArgs, Locale locale) {
        notifyUser(userService.getIdByEmail(email), cause, titleCode, messageCode, normalizeArgs(messageArgs), locale);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void notifyUser(Integer userId, NotificationEvent cause, String titleCode, String messageCode,
                           Object[] messageArgs) {
        String lang = userService.getPreferedLang(userId);
        Locale locale = new Locale(StringUtils.isEmpty(lang) ? "EN" : lang);
        notifyUser(userId, cause, titleCode, messageCode, normalizeArgs(messageArgs), locale);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void notifyUser(Integer userId, NotificationEvent cause, String titleCode, String messageCode,
                           Object[] messageArgs, Locale locale) {
        String titleMessage = messageSource.getMessage(titleCode, null, locale);
        String message = messageSource.getMessage(messageCode, normalizeArgs(messageArgs), locale);
        notifyUser(userId, cause, titleMessage, message);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void notifyUser(Integer userId, NotificationEvent cause, String titleMessage, String message) {
      User user = userService.getUserById(userId);
      NotificationOption option = notificationDao.findUserOptionForEvent(userId, cause);
      /*if (option.isSendNotification()) {
        createNotification(
            userId,
            titleMessage,
            message,
            cause);
      }*/
      if (option.isSendEmail()) {
        Email email = new Email();
        email.setSubject(titleMessage);
        email.setMessage(message);
        email.setTo(user.getEmail());
        sendMailService.sendInfoMail(email);
      }
    }


    @Override
    @Transactional(readOnly = true)
    public List<Notification> findAllByUser(String email) {
        return notificationDao.findAllByUser(userService.getIdByEmail(email));
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> findByUser(String email, CacheData cacheData, Integer offset, Integer limit) {
        List<NotificationDto> result = notificationDao.findByUser(userService.getIdByEmail(email), offset, limit);
        if (Cache.checkCache(cacheData, result)) {
            result = new ArrayList<NotificationDto>() {{
                add(new NotificationDto(false));
            }};
        }
        return result;
    }

    @Override
    public boolean setRead(Long notificationId) {
        return notificationDao.setRead(notificationId);
    }

    @Override
    public boolean remove(Long notificationId) {
        return notificationDao.remove(notificationId);
    }

    @Override
    public int setReadAllByUser(String email) {
        return notificationDao.setReadAllByUser(userService.getIdByEmail(email));
    }

    @Override
    public int removeAllByUser(String email) {
        return notificationDao.removeAllByUser(userService.getIdByEmail(email));

    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationOption> getNotificationOptionsByUser(Integer userId) {
        return notificationDao.getNotificationOptionsByUser(userId);
    }

    @Override
    public void updateUserNotifications(List<NotificationOption> options) {
        notificationDao.updateNotificationOptions(options);
    }

    @Override
    public void updateNotificationOptionsForUser(int userId, List<NotificationOption> options) {
        options.forEach(item -> item.setUserId(userId));
        notificationDao.updateNotificationOptions(options);
    }

    @Override
    public String getGoogleAuthenticatorCode(Integer userId) {
        String secret2faCode = notificationDao.getGoogleAuthSecretCodeByUser(userId);
        if (secret2faCode == null || secret2faCode.isEmpty()){
            notificationDao.set2faGoogleAuthenticator(userId);
            secret2faCode = notificationDao.getGoogleAuthSecretCodeByUser(userId);
        }
        return secret2faCode;
    }

    @Override
    public void updateGoogleAuthenticatorSecretCodeForUser(Integer userId) {
        notificationDao.setGoogleAuthSecretCode(userId);
    }

    @Override
    public boolean isGoogleAuthenticatorEnable(Integer userId) {
        return notificationDao.isGoogleAuthenticatorEnable(userId);
    }

    @Override
    public boolean checkGoogle2faVerifyCode(String verificationCode, Integer userId) {
        String google2faSecret = notificationDao.getGoogleAuthSecretCodeByUser(userId);
        final Totp totp = new Totp(google2faSecret);
        if (!isValidLong(verificationCode) || !totp.verify(verificationCode)) {
            return false;
        }
        return true;
    }

    @Override
    public void setEnable2faGoogleAuth(Integer userId, Boolean connection) {
        notificationDao.setEnable2faGoogleAuth(userId, connection);
        if (!connection) {
            notificationUserSettingsDao.delete(userId);
        }
    }

    @Override
    public String generateQRUrl(String userEmail) throws UnsupportedEncodingException {
        User user = userService.findByEmail(userEmail);
        String secret2faCode = notificationDao.getGoogleAuthSecretCodeByUser(user.getId());
        return QR_PREFIX + URLEncoder.encode(String.format("otpauth://totp/%s:%s?secret=%s&issuer=%s", APP_NAME, userEmail, secret2faCode, APP_NAME), "UTF-8");
    }

    private String[] normalizeArgs(Object... args) {
       return Arrays.toString(args).replaceAll("[\\[\\]]", "").split("\\s*,\\s*");
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
