package me.exrates.dao;

import me.exrates.model.dto.NotificationsUserSetting;
import me.exrates.model.enums.NotificationMessageEventEnum;

import java.util.List;

/**
 * Created by Maks on 29.09.2017.
 */
public interface NotificationUserSettingsDao {

    NotificationsUserSetting getByUserAndEvent(int userId, NotificationMessageEventEnum event);

    List<NotificationsUserSetting> getByUserAndEvents(int userId, NotificationMessageEventEnum ... events);

    int create(NotificationsUserSetting setting);

    void update(NotificationsUserSetting setting);

    void delete(Integer userId);
}
