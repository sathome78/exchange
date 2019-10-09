package me.exrates.ngService;

import me.exrates.model.dto.UserNotificationMessage;

import java.util.Collection;

public interface RedisUserNotificationService {

    Collection<UserNotificationMessage> findAllByUser(String email);

    boolean saveUserNotification(String email, UserNotificationMessage userNotification);

    void deleteUserNotification(String key);
}
