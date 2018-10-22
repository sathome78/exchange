package me.exrates.ngcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.exrates.controller.handler.ChatWebSocketHandler;
import me.exrates.model.ChatMessage;
import me.exrates.model.User;
import me.exrates.model.enums.ChatLang;
import me.exrates.security.ipsecurity.IpBlockingService;
import me.exrates.security.ipsecurity.IpTypesOfChecking;
import me.exrates.service.ChatService;
import me.exrates.service.UserService;
import me.exrates.service.exception.IllegalChatMessageException;
import me.exrates.service.notifications.NotificationsSettingsService;
import me.exrates.service.util.IpUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.TextMessage;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.function.Supplier;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(value = "/info/public/v2/",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
)
public class NgPublicController {

    private static final Logger logger = LogManager.getLogger(NgPublicController.class);

    private final ChatService chatService;
    private final EnumMap<ChatLang, ChatWebSocketHandler> handlers;
    private final IpBlockingService ipBlockingService;
    private final UserService userService;
    private final NotificationsSettingsService notificationsSettingsService;

    @Autowired
    public NgPublicController(ChatService chatService,
                              EnumMap<ChatLang, ChatWebSocketHandler> handlers,
                              IpBlockingService ipBlockingService,
                              UserService userService,
                              NotificationsSettingsService notificationsSettingsService) {
        this.chatService = chatService;
        this.handlers = handlers;
        this.ipBlockingService = ipBlockingService;
        this.userService = userService;
        this.notificationsSettingsService = notificationsSettingsService;
    }

    @GetMapping(value = "/if_email_exists")
    public ResponseEntity<Boolean> checkIfNewUserEmailExists(@RequestParam("email") String email, HttpServletRequest request) {
        Boolean unique = processIpBlocking(request, "email", email,
                () -> userService.ifEmailIsUnique(email));
        // we may use this elsewhere, so exists is opposite to unique
        return new ResponseEntity<>(!unique, HttpStatus.OK);
    }

    @GetMapping("/is_google_2fa_enabled")
    @ResponseBody
    public Boolean isGoogleTwoFAEnabled(@RequestParam("email") String email) {
        User user = userService.findByEmail(email);
        return false;
        // todo
//        return notificationsSettingsService.isGoogleTwoFALoginEnabled(user);
    }

    @GetMapping(value = "/if_username_exists")
    public ResponseEntity<Boolean> checkIfNewUserUsernameExists(@RequestParam("username") String username, HttpServletRequest request) {
        Boolean unique = processIpBlocking(request, "username", username,
                () -> userService.ifNicknameIsUnique(username));
        // we may use this elsewhere, so exists is opposite to unique
        return new ResponseEntity<>(!unique, HttpStatus.OK);
    }

    @GetMapping(value = "/chat/history")
    @ResponseBody
    public Set<ChatMessage> chatMessages(final @RequestParam("lang") String lang) {
        try {
            SortedSet<ChatMessage> lastMessages = chatService.getLastMessages(ChatLang.toInstance(lang));
            return lastMessages;
        } catch (Exception e) {
            return Collections.emptyNavigableSet();
        }
    }

    @PostMapping(value = "/chat")
    public ResponseEntity<Void> sendChatMessage(@RequestBody Map<String, String> body) {
        final ChatLang chatLang = ChatLang.toInstance(body.getOrDefault("LANG", "EN"));
        String simpleMessage = body.get("MESSAGE");
        String email = body.getOrDefault("EMAIL", "");
        if (isEmpty(simpleMessage)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        final ChatMessage message;
        try {
            message = chatService.persistPublicMessage(simpleMessage, email, chatLang);
        } catch (IllegalChatMessageException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        handlers.get(chatLang).getSessions().forEach(webSocketSession -> {
            try {
                webSocketSession.sendMessage(new TextMessage(new ObjectMapper().writeValueAsString(message)));
            } catch (IOException e) {
                logger.info("Failed to send message: {} via websocket", message.getBody());
            }
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Boolean processIpBlocking(HttpServletRequest request, String logMessageValue,
                                      String value, Supplier<Boolean> operation) {
        String clientIpAddress = IpUtils.getClientIpAddress(request);
        ipBlockingService.checkIp(clientIpAddress, IpTypesOfChecking.OPEN_API);
        Boolean result = operation.get();
        if (!result) {
            ipBlockingService.failureProcessing(clientIpAddress, IpTypesOfChecking.OPEN_API);
            logger.debug("New user's %s %s is already stored!", logMessageValue, value);
        } else {
            ipBlockingService.successfulProcessing(clientIpAddress, IpTypesOfChecking.OPEN_API);
            logger.debug("New user's %s %s is not stored yet!", logMessageValue, value);
        }
        return result;
    }

}
