package me.exrates.service.notifications;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Email;
import me.exrates.model.enums.NotificationTypeEnum;
import me.exrates.service.SendMailService;
import me.exrates.service.exception.MessageUndeliweredException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/*@Log4j2(topic = "message_notify")
@Component
public class Google2faNotificatorServiceImpl implements NotificatorService {

    @Override
    public Object getSubscriptionByUserId(int userId) {
        return null;
    }

    @Override
    public String sendMessageToUser(String userEmail, String message, String subject) throws MessageUndeliweredException {
        return "";
    }

    @Override
    public NotificationTypeEnum getNotificationType() {
        return NotificationTypeEnum.GOOGLE2FA;
    }
}*/
