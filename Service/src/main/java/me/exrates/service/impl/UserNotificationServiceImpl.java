package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.UserNotificationRepository;
import me.exrates.model.dto.UserNotificationMessage;
import me.exrates.service.UserNotificationService;
import me.exrates.service.UserService;
import me.exrates.service.exception.AuthenticationNotAvailableException;
import me.exrates.service.stomp.StompMessenger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Log4j2
@Service
public class UserNotificationServiceImpl implements UserNotificationService {

    private final UserNotificationRepository userNotificationRepository;
    private final UserService userService;
    private final StompMessenger stompMessenger;

    @Autowired
    public UserNotificationServiceImpl(UserNotificationRepository userNotificationRepository,
                                       UserService userService,
                                       StompMessenger stompMessenger) {
        this.userNotificationRepository = userNotificationRepository;
        this.userService = userService;
        this.stompMessenger = stompMessenger;
    }

    @Override
    public List<UserNotificationMessage> findAllUserMessages(String userPublicId, int limit) {
        return userNotificationRepository.findAll(userPublicId, limit);
    }

    @Override
    public List<UserNotificationMessage> findAllUserMessages(String userPublicId) {
        return findAllUserMessages(userPublicId, 5);
    }

    @Override
    public UserNotificationMessage sendUserNotificationMessage(String userEmail, UserNotificationMessage message) {
        String userPublicId = userService.getPubIdByEmail(userEmail);
        try {
            String emailFromSecurityContext = userService.getUserEmailFromSecurityContext();
            boolean sameUser = userEmail.equalsIgnoreCase(emailFromSecurityContext);
            message.setViewed(sameUser);
        } catch (AuthenticationNotAvailableException exc) {
            log.debug("It seems - there is no authenticated user now");
        }
        stompMessenger.sendPersonalMessageToUser(userEmail, message);
        userNotificationRepository.save(userPublicId, message);
        return message;
    }


}
