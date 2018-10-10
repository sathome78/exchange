package me.exrates.service.notifications;

import me.exrates.model.User;
import me.exrates.model.dto.NotificationsUserSetting;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.NotificationTypeEnum;

import java.util.List;
import java.util.Map;

/**
 * Created by Maks on 08.10.2017.
 */
public interface NotificationsSettingsService {

    List<NotificationsUserSetting> getByUserAndEvents(int userId, NotificationMessageEventEnum... events);

    NotificationsUserSetting getByUserAndEvent(int userId, NotificationMessageEventEnum event);

    void createOrUpdate(NotificationsUserSetting setting);

    Map<String, Object> get2faOptionsForUser(int id);

    Map<Integer, NotificationsUserSetting> getSettingsMap(int userId);

    Map<NotificationMessageEventEnum, NotificationTypeEnum> getUser2FactorSettings(int userId);

    Map<NotificationMessageEventEnum, Boolean> getUserTwoFASettings(User user);

    boolean isGoogleTwoFALoginEnabled(User user);

    void updateUser2FactorSettings(int userId, Map<String, String> body);
}
