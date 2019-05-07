package me.exrates.service.notifications;

import me.exrates.model.enums.NotificationTypeEnum;
import me.exrates.service.exception.MessageUndeliweredException;

public interface NotificatorService {


    Object getSubscriptionByUserId(int userId);

    String sendMessageToUser(String userEmail, String message, String subject) throws MessageUndeliweredException;

    NotificationTypeEnum getNotificationType();

}
