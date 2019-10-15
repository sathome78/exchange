package me.exrates.ngDao;

import me.exrates.model.dto.UserNotificationMessage;

import java.util.Collection;

public interface RedisUserNotificationDao {

    Collection<UserNotificationMessage> findAllByUser(String email);

    boolean saveUserNotification(String email, UserNotificationMessage userNotification);

    void deleteUserNotification(String key);
}
