package me.exrates.service.notifications;

import me.exrates.model.dto.NotificationsUserSetting;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.NotificationTypeEnum;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Maks on 08.10.2017.
 */
public interface NotificationsSettingsService {

    NotificationsUserSetting getByUserAndEvent(int userId, NotificationMessageEventEnum event);

    void createOrUpdate(NotificationsUserSetting setting);

    Map<String, Object> get2faOptionsForUser(int id);

    Map<Integer, NotificationsUserSetting> getSettingsMap(int userId);

    Map<NotificationMessageEventEnum, NotificationTypeEnum> getUser2FactorSettings(int userId);

    void updateUser2FactorSettings(int userId, Map<NotificationMessageEventEnum, NotificationTypeEnum> newSettings);
}
