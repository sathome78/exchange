package me.exrates.ngcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import me.exrates.controller.handler.ChatWebSocketHandler;
import me.exrates.model.ChatMessage;
import me.exrates.model.CurrencyPair;
import me.exrates.model.User;
import me.exrates.model.dto.ChatHistoryDateWrapperDto;
import me.exrates.model.dto.ChatHistoryDto;
import me.exrates.model.dto.ChatHistoryDateWrapperDto;
import me.exrates.model.dto.onlineTableDto.OrderListDto;
import me.exrates.model.enums.ChatLang;
import me.exrates.ngcontroller.service.NgOrderService;
import me.exrates.security.ipsecurity.IpBlockingService;
import me.exrates.security.ipsecurity.IpTypesOfChecking;
import me.exrates.service.ChatService;
import me.exrates.service.CurrencyService;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.exception.IllegalChatMessageException;
import me.exrates.service.notifications.NotificationsSettingsService;
import me.exrates.service.util.IpUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@RestController
@RequestMapping(value = "/info/public/v2/",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
)
@PropertySource("classpath:angular.properties")
public class NgPublicController {

    private static final Logger logger = LogManager.getLogger(NgPublicController.class);

    private final ChatService chatService;
    private final EnumMap<ChatLang, ChatWebSocketHandler> handlers;
    private final IpBlockingService ipBlockingService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationsSettingsService notificationsSettingsService;
    private final CurrencyService currencyService;
    private final NgOrderService ngOrderService;
    private final OrderService orderService;

    @Value("${angular.write.mode}")
    private boolean WRITE_MODE;

    @Autowired
    public NgPublicController(ChatService chatService,
                              EnumMap<ChatLang, ChatWebSocketHandler> handlers,
                              IpBlockingService ipBlockingService,
                              UserService userService,
                              SimpMessagingTemplate messagingTemplate,
                              NotificationsSettingsService notificationsSettingsService,
                              CurrencyService currencyService,
                              NgOrderService ngOrderService,
                              OrderService orderService) {
        this.chatService = chatService;
        this.handlers = handlers;
        this.ipBlockingService = ipBlockingService;
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
        this.notificationsSettingsService = notificationsSettingsService;
        this.currencyService = currencyService;
        this.ngOrderService = ngOrderService;
        this.orderService = orderService;
    }

    @PostConstruct
    private void initCheckVersion(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        logger.error ("Build at: " +  LocalDateTime.now().format(formatter));
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
    public  List<ChatHistoryDateWrapperDto> getChatMessages(final @RequestParam("lang") String lang) {
        try {
            if (WRITE_MODE){
                return chatService.getPublicChatHistoryByDate(ChatLang.toInstance(lang));
            } else {
                return chatService.getChatHistoryByDate(ChatLang.toInstance(lang));
            }
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @PostMapping(value = "/chat")
    public ResponseEntity<Void> sendChatMessage(@RequestBody Map<String, String> body) {
        String language = body.getOrDefault("LANG", "EN");
        ChatLang chatLang = ChatLang.toInstance(language);
        String simpleMessage = body.get("MESSAGE");
        String email = body.getOrDefault("EMAIL", "");
        if (isEmpty(simpleMessage)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        final ChatMessage message;
        try {
            if (WRITE_MODE) {
                message = chatService.persistPublicMessage(simpleMessage, email, chatLang);
            } else {
                message = new ChatMessage();
                message.setNickname("anonymous");
                message.setBody(simpleMessage);
                message.setId(Long.parseLong(RandomStringUtils.randomNumeric(5)));
                message.setTime(LocalDateTime.now());
            }
        } catch (IllegalChatMessageException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String destination = "/topic/chat/".concat(language.toLowerCase());
        messagingTemplate.convertAndSend(destination, fromChatMessage(message));
        return new ResponseEntity<>(HttpStatus.OK);
    }

//    @MessageMapping("/topic/chat/{lang}")
//    public void onReceivedNewMessage(@DestinationVariable String lang, String message){
//        this.template.convertAndSend("/topic/chat/" + lang, message);
//    }

    // /info/public/v2/currencies/min-max/{currencyPairId}
    @GetMapping("/currencies/min-max/{currencyPairId}")
    public ResponseEntity<Map<String, OrderListDto>> getMinAndMaxOrdersSell(@PathVariable int currencyPairId) {
        try {
            CurrencyPair currencyPair = currencyService.findCurrencyPairById(currencyPairId);
            Map<String, OrderListDto> values = orderService.getLastMinAndMaxOrderFor(currencyPair);
            return ResponseEntity.ok(values);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private String fromChatMessage(ChatMessage message) {
        String send = "";
        ChatHistoryDto dto = new ChatHistoryDto();
        dto.setMessageTime(message.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        dto.setEmail(message.getNickname());
        dto.setBody(message.getBody());

        try {
            ObjectMapper mapper = new ObjectMapper();
            send = mapper.writeValueAsString(dto);
        } catch (Exception e) {
            logger.info("Failed to convert to json {}", dto.getBody());
        }
        return send;
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
