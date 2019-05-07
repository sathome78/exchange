package me.exrates.service.notifications;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.NotificationUserSettingsDao;
import me.exrates.model.dto.NotificationsUserSetting;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.NotificationTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Log4j2(topic = "message_notify")
@Component
public class NotificationsSettingsServiceImpl implements NotificationsSettingsService {

    @Autowired
    private NotificationUserSettingsDao settingsDao;
    @Autowired
    private NotificatorsService notificatorsService;
    @Autowired
    private G2faService g2faService;


    @Override
    public NotificationsUserSetting getByUserAndEvent(int userId, NotificationMessageEventEnum event) {
        if (g2faService.isGoogleAuthenticatorEnable(userId)) {
            return NotificationsUserSetting.builder()
                    .notificationMessageEventEnum(event)
                    .notificatorId(NotificationTypeEnum.GOOGLE2FA.getCode())
                    .build();
        }
        return  NotificationsUserSetting.builder()
                .notificatorId(NotificationTypeEnum.EMAIL.getCode())
                .userId(userId)
                .notificationMessageEventEnum(event)
                .build();
    }

}
