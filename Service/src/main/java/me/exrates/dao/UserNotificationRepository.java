package me.exrates.dao;

import me.exrates.model.dto.UserNotificationMessage;

import java.util.List;

public interface UserNotificationRepository {

    UserNotificationMessage save(String userPublicId, UserNotificationMessage notificationMessage);

    UserNotificationMessage update(UserNotificationMessage notificationMessage);

    void delete(UserNotificationMessage notificationMessage);

    boolean exists(String key);

    List<UserNotificationMessage> findAll(String userPublicId, int limit);
}
