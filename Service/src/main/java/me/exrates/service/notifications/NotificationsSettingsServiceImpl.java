package me.exrates.service.notifications;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.NotificationUserSettingsDao;
import me.exrates.model.dto.NotificationsUserSetting;
import me.exrates.model.dto.Notificator;
import me.exrates.model.dto.ngDto.User2FactorAuthItemDto;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.NotificationTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Maks on 08.10.2017.
 */
@Log4j2(topic = "message_notify")
@Component
public class NotificationsSettingsServiceImpl implements NotificationsSettingsService {

    @Autowired
    private NotificationUserSettingsDao settingsDao;
    @Autowired
    private cService notificatorsService;


    @Override
    public NotificationsUserSetting getByUserAndEvent(int userId, NotificationMessageEventEnum event) {
        return settingsDao.getByUserAndEvent(userId, event);
    }

    @Override
    public void createOrUpdate(NotificationsUserSetting setting) {
        if (getByUserAndEvent(setting.getUserId(), setting.getNotificationMessageEventEnum()) == null) {
            settingsDao.create(setting);
        } else {
            settingsDao.update(setting);
        }
    }

    @Override
    public Map<String, Object> get2faOptionsForUser(int userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("notificators", notificatorsService.getAllNotificators());
        map.put("events", Arrays.asList(NotificationMessageEventEnum.values()));
        map.put("settings", getSettingsMap(userId));
        map.put("subscriptions", notificatorsService.getSubscriptions(userId));
        return map;
    }

    @Override
    public Map<Integer, NotificationsUserSetting> getSettingsMap(int userId) {
        HashMap<Integer, NotificationsUserSetting> settingsMap = new HashMap<>();
        Arrays.asList(NotificationMessageEventEnum.values()).forEach(p -> {
                    settingsMap.put(p.getCode(), getByUserAndEvent(userId, p));
                }
        );
        return settingsMap;
    }

    public Map<NotificationMessageEventEnum, Map<NotificationTypeEnum, Boolean>> getUser2FactorSettings(int userId){
        Map<NotificationMessageEventEnum, Map<NotificationTypeEnum, Boolean>> settings =
                                new HashMap<>(NotificationMessageEventEnum.values().length);
        Arrays.asList(NotificationMessageEventEnum.values()).forEach(value -> {
            if (null != getByUserAndEvent(userId, value)) {
                Map<NotificationTypeEnum, Boolean> notifications = new HashMap<>();
                Arrays.asList(NotificationTypeEnum.values()).forEach(type -> {
                    notifications.put(type, )
                });
                settings.put(value, notifications);
            } else {
                Map<NotificationTypeEnum, Boolean> notifications = new HashMap<>();
                Arrays.asList(NotificationTypeEnum.values()).forEach(type -> {
                    notifications.put(type, false);
                });
                settings.put(value, notifications);
            }
        });
        return settings;
    }



}
