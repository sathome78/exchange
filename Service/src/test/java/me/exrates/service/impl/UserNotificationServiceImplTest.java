package me.exrates.service.impl;

import me.exrates.model.dto.UserNotificationMessage;
import me.exrates.model.enums.UserNotificationType;
import me.exrates.model.enums.WsSourceTypeEnum;
import me.exrates.service.UserService;
import me.exrates.service.stomp.StompMessenger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {UserNotificationServiceImplTest.InnerConfig.class})
public class UserNotificationServiceImplTest {

    private final String USER_PUBLIC_ID = "JHSFGKJHSFGD";

    private static final UserNotificationRepository userNotificationRepository = Mockito.mock(UserNotificationRepository.class);
    private static final StompMessenger stompMessenger = Mockito.mock(StompMessenger.class);
    private static final UserService userService = Mockito.mock(UserService.class);

    @Autowired
    private UserNotificationService userNotificationService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void sendUserNotificationMessage() {
        String email = "test@email.com";
        when(userService.getPubIdByEmail(Matchers.anyString())).thenReturn(USER_PUBLIC_ID);
        when(userService.getUserEmailFromSecurityContext()).thenReturn(email);

        UserNotificationMessage message = new UserNotificationMessage(WsSourceTypeEnum.SUBSCRIBE, UserNotificationType.SUCCESS, "message");
        UserNotificationMessage result = userNotificationService.sendUserNotificationMessage(email, message);


        assertTrue(result.isViewed());

        verify(stompMessenger, times(1)).sendPersonalMessageToUser(anyString(), anyObject());
        reset(userService, stompMessenger);
    }

    @Test
    public void sendUserNotificationMessage_Unauthenticated() {
        when(userService.getPubIdByEmail(Matchers.anyString())).thenReturn(USER_PUBLIC_ID);
        doNothing().when(stompMessenger).sendPersonalMessageToUser(anyString(), anyObject());

        UserNotificationMessage message = new UserNotificationMessage(WsSourceTypeEnum.SUBSCRIBE, UserNotificationType.SUCCESS, "message");
        UserNotificationMessage result = userNotificationService.sendUserNotificationMessage("test@email.com", message);

        assertFalse(result.isViewed());

        verify(stompMessenger, times(1)).sendPersonalMessageToUser(anyString(), anyObject());
    }


    @Configuration
    static class InnerConfig {

        @Bean
        public UserNotificationService userNotificationService() {
            return new UserNotificationServiceImpl(userNotificationRepository, userService, stompMessenger);
        }

    }
}
