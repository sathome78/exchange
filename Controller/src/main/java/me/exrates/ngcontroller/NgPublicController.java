package me.exrates.ngcontroller;

import me.exrates.controller.exception.ErrorInfo;
import me.exrates.controller.handler.ChatWebSocketHandler;
import me.exrates.model.ChatMessage;
import me.exrates.model.CurrencyPair;
import me.exrates.model.User;
import me.exrates.model.dto.CandleDto;
import me.exrates.model.dto.ChatHistoryDto;
import me.exrates.model.enums.ChartTimeFramesEnum;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isEmpty;

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
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationsSettingsService notificationsSettingsService;
    private final CurrencyService currencyService;
    private final NgOrderService ngOrderService;
    private final OrderService orderService;

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
    public List<ChatHistoryDto> chatMessages(final @RequestParam("lang") String lang) {
        try {
            List<ChatHistoryDto> messages = chatService.getPublicChatHistory(ChatLang.toInstance(lang));
            return messages;
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
            message = chatService.persistPublicMessage(simpleMessage, email, chatLang);
        } catch (IllegalChatMessageException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String destination = "/topic/chat/".concat(language.toLowerCase());
        messagingTemplate.convertAndSend(destination, fromChatMessage(message));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private ChatHistoryDto fromChatMessage(ChatMessage message) {
        ChatHistoryDto dto = new ChatHistoryDto();
        dto.setMessageTime(message.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        dto.setEmail(message.getNickname());
        dto.setBody(message.getBody());
        return dto;
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

    @GetMapping("/history")
    public ResponseEntity getCandleChartHistoryData(
            @QueryParam("symbol") String symbol,
            @QueryParam("to") Long to,
            @QueryParam("from") Long from,
            @QueryParam("resolution") String resolution) {

        CurrencyPair currencyPair = currencyService.getCurrencyPairByName(symbol);
        List<CandleDto> result = new ArrayList<>();
        if (currencyPair == null) {
            HashMap<String, Object> errors = new HashMap<>();
            errors.putAll(ngOrderService.filterDataPeriod(result, from, to, resolution));
            errors.put("s", "error");
            errors.put("errmsg", "can not find currencyPair");
            return new ResponseEntity(errors, HttpStatus.NOT_FOUND);
        }

        String rsolutionForChartTime = (resolution.equals("W") || resolution.equals("M")) ? "D" : resolution;
        result = orderService.getCachedDataForCandle(currencyPair,
                ChartTimeFramesEnum.ofResolution(rsolutionForChartTime).getTimeFrame())
                .stream().map(CandleDto::new).collect(Collectors.toList());
        return new ResponseEntity(ngOrderService.filterDataPeriod(result, from, to, resolution), HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ErrorInfo OtherErrorsHandlerMethodArgumentNotValidException(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EmptyResultDataAccessException.class)
    @ResponseBody
    public ErrorInfo OtherErrorsHandlerEmptyResultDataAccessException(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception, exception.getLocalizedMessage());
    }

}
