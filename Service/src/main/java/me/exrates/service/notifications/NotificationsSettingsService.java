package me.exrates.service.notifications;

import me.exrates.model.dto.NotificationsUserSetting;
import me.exrates.model.enums.NotificationMessageEventEnum;

public interface NotificationsSettingsService {

    NotificationsUserSetting getByUserAndEvent(int userId, NotificationMessageEventEnum event);

}
