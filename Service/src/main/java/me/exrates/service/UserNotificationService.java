package me.exrates.service;

import me.exrates.model.dto.UserNotificationMessage;

import java.util.List;

public interface UserNotificationService {

    List<UserNotificationMessage> findAllUserMessages(String userPublicId, int limit);

    List<UserNotificationMessage> findAllUserMessages(String userPublicId);

    UserNotificationMessage sendUserNotificationMessage(String userEmail, UserNotificationMessage message);
}
