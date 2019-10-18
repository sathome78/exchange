package me.exrates.service.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.AlertDto;
import me.exrates.service.UserService;
import me.exrates.service.UsersAlertsService;
import me.exrates.service.events.QRLoginEvent;
import me.exrates.service.stomp.StompMessenger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Maks on 07.09.2017.
 */
@Log4j2
@Component
public class SystemEventsHandler {

    @Autowired
    private StompMessenger stompMessenger;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private LocaleResolver localeResolver;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UsersAlertsService usersAlertsService;
    @Autowired
    private UserService userService;

//    @Async
//    @TransactionalEventListener
//    public void handleAlertUsers(AlertDto alertDto) {
//        userService.getLocalesList().forEach(p->{
//            try {
//                Locale locale = Locale.forLanguageTag(p);
//                stompMessenger.sendAlerts(objectMapper
//                        .writeValueAsString(usersAlertsService.getAllAlerts(locale)), p.toUpperCase());
//            } catch (Exception e) {
//                log.error(e);
//            }
//        });
//
//    }

//    @EventListener
//    public void handleQRLogin(QRLoginEvent qrLoginEvent) {
//        log.debug("login via qr");
//        HttpServletRequest request = (HttpServletRequest)qrLoginEvent.getSource();
//        HashMap hashMap = new HashMap<String, HashMap<String, String>>() {{
//            put("redirect", new HashMap<String, String>() {{
//                put("url", "/dashboard?qrLogin");
//              /*  put("successQR", messageSource
//                        .getMessage("dashboard.qrLogin.successful", null,
//                                localeResolver.resolveLocale(request)));*/
//            }});
//        }};
//        try {
//            stompMessenger.sendEventMessage(request.getSession().getId(), objectMapper.writeValueAsString(hashMap));
//        } catch (JsonProcessingException e) {
//            log.error("error qr login {}", e);
//        }
//    }
}
