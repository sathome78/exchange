package me.exrates.service.notifications.scheduled;

import me.exrates.model.User;
import me.exrates.service.SendMailService;
import me.exrates.service.UserService;
import me.exrates.service.notifications.Google2faNotificatorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@EnableScheduling
@Component
public class NotificatorSchedule {

    private final UserService userService;
    private final Google2faNotificatorServiceImpl notificatorService;
    private final SendMailService sendMailService;

    @Autowired
    public NotificatorSchedule(UserService userService,
                               Google2faNotificatorServiceImpl notificatorService,
                               SendMailService sendMailService) {
        this.userService = userService;
        this.notificatorService = notificatorService;
        this.sendMailService = sendMailService;
    }

    @Scheduled(cron = )
    public void sendNotificationScheduler() {
        sendUserNotificationMail();
    }

    private void sendUserNotificationMail() {
        Set<Integer> usersId = notificatorService.getUsersWithout2faGoogleAuth();

        for (Integer userId: usersId) {
            User user = userService.getUserById(userId);
        }
    }
}
