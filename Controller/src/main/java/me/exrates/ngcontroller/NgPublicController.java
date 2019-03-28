package me.exrates.ngcontroller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.dao.chat.telegram.TelegramChatDao;
import me.exrates.dao.exception.notfound.UserNotFoundException;
import me.exrates.model.ChatMessage;
import me.exrates.model.Currency;
import me.exrates.model.CurrencyPair;
import me.exrates.model.User;
import me.exrates.model.constants.ErrorApiTitles;
import me.exrates.model.dto.ChatHistoryDateWrapperDto;
import me.exrates.model.dto.ChatHistoryDto;
import me.exrates.model.dto.OrderBookWrapperDto;
import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.model.dto.onlineTableDto.OrderAcceptedHistoryDto;
import me.exrates.model.enums.ChatLang;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.MerchantProcessType;
import me.exrates.model.enums.OrderType;
import me.exrates.model.enums.UserStatus;
import me.exrates.model.ngExceptions.NgDashboardException;
import me.exrates.model.ngExceptions.NgResponseException;
import me.exrates.model.ngModel.ResponseInfoCurrencyPairDto;
import me.exrates.model.ngModel.response.ResponseModel;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.ngService.NgOrderService;
import me.exrates.security.ipsecurity.IpBlockingService;
import me.exrates.security.ipsecurity.IpTypesOfChecking;
import me.exrates.security.service.NgUserService;
import me.exrates.service.ChatService;
import me.exrates.service.CurrencyService;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.cache.ExchangeRatesHolder;
import me.exrates.service.exception.IllegalChatMessageException;
import me.exrates.service.notifications.G2faService;
import me.exrates.service.util.IpUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@RestController
@RequestMapping(value = "/api/public/v2",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE
)
public class NgPublicController {

    private static final Logger logger = LogManager.getLogger(NgPublicController.class);

    private final ChatService chatService;
    private final CurrencyService currencyService;
    private final IpBlockingService ipBlockingService;
    private final UserService userService;
    private final NgUserService ngUserService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final OrderService orderService;
    private final G2faService g2faService;
    private final NgOrderService ngOrderService;
    private final TelegramChatDao telegramChatDao;
    private final ExchangeRatesHolder exchangeRatesHolder;

    @Autowired
    public NgPublicController(ChatService chatService,
                              CurrencyService currencyService, IpBlockingService ipBlockingService,
                              UserService userService,
                              NgUserService ngUserService,
                              SimpMessagingTemplate simpMessagingTemplate,
                              OrderService orderService,
                              G2faService g2faService,
                              NgOrderService ngOrderService,
                              TelegramChatDao telegramChatDao,
                              ExchangeRatesHolder exchangeRatesHolder) {
        this.chatService = chatService;
        this.currencyService = currencyService;
        this.ipBlockingService = ipBlockingService;
        this.userService = userService;
        this.ngUserService = ngUserService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.orderService = orderService;
        this.g2faService = g2faService;
        this.ngOrderService = ngOrderService;
        this.exchangeRatesHolder = exchangeRatesHolder;
        this.telegramChatDao = telegramChatDao;
    }

    @GetMapping(value = "/if_email_exists")
    public ResponseEntity<Boolean> checkIfNewUserEmailExists(@RequestParam("email") String email, HttpServletRequest request) {
        logger.info("Url request url {}, scheme {}, port {}", request.getRequestURI(), request.getScheme(), request.getServerPort());
        User user;
        try {
            user = userService.findByEmail(email);
        } catch (UserNotFoundException esc) {
            String message = String.format("User with email %s not found", email);
            logger.warn(message, esc);
            throw new NgResponseException(ErrorApiTitles.USER_EMAIL_NOT_FOUND, message);
        }
        if (user.getUserStatus() == UserStatus.REGISTERED) {
            ngUserService.resendEmailForFinishRegistration(user);
            String message = String.format("User with email %s registration is not complete", email);
            logger.debug(message);
            throw new NgResponseException(ErrorApiTitles.USER_REGISTRATION_NOT_COMPLETED, message);
        }
        if (user.getUserStatus() == UserStatus.DELETED) {
            String message = String.format("User with email %s is not active", email);
            logger.debug(message);
            throw new NgResponseException(ErrorApiTitles.USER_NOT_ACTIVE, message);
        }
        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
    }

    @GetMapping("/is_google_2fa_enabled")
    @ResponseBody
    public Boolean isGoogleTwoFAEnabled(@RequestParam("email") String email) {
        return g2faService.isGoogleAuthenticatorEnable(email);
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
    public List<ChatHistoryDateWrapperDto> getChatMessages(final @RequestParam("lang") String lang) {
        try {
            List<ChatHistoryDto> msgs = Lists.newArrayList(telegramChatDao.getChatHistoryQuick(ChatLang.EN));
            return Lists.newArrayList(new ChatHistoryDateWrapperDto(LocalDate.now(), msgs));
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // /info/public/v2/all-pairs
    @GetMapping("/all-pairs")
    @ResponseBody
    public List<CurrencyPair> getAllPairs() {
        try {
            return currencyService.getAllCurrencyPairs(CurrencyPairType.MAIN);
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
            String message = "Chat message cannot be empty.";
            logger.warn(message);
            throw new NgResponseException(ErrorApiTitles.EMPTY_CHAT_MESSAGE, message);
        }
        final ChatMessage message;
        try {
            message = chatService.persistPublicMessage(simpleMessage, email, chatLang);
        } catch (IllegalChatMessageException e) {
            String msg = "Chat message cannot persist " + e.getMessage();
            logger.warn(msg, e);
            throw new NgResponseException(ErrorApiTitles.FAIL_TO_PERSIST_CHAT_MESSAGE, msg);
        }
        String destination = "/topic/chat/".concat(language.toLowerCase());
        simpMessagingTemplate.convertAndSend(destination, fromChatMessage(message));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // apiUrl/info/public/v2/open-orders/0/5

    @GetMapping(value = "/open-orders/{pairId}/{precision}")
    @ResponseBody
    public List<OrderBookWrapperDto> getOpenOrders(@PathVariable Integer pairId, @PathVariable Integer precision) {
        return ImmutableList.of(
                orderService.findAllOrderBookItems(OrderType.SELL, pairId, precision),
                orderService.findAllOrderBookItems(OrderType.BUY, pairId, precision));
    }

    @GetMapping("/info/{currencyPairId}")
    public ResponseEntity getCurrencyPairInfo(@PathVariable int currencyPairId) {
        try {
            ResponseInfoCurrencyPairDto currencyPairInfo = ngOrderService.getCurrencyPairInfo(currencyPairId);
            return new ResponseEntity<>(currencyPairInfo, HttpStatus.OK);
        } catch (Exception e) {
            String msg = "Cannot get to currency pair info " + e.getMessage();
            logger.error(msg, e);
            throw new NgResponseException(ErrorApiTitles.FAIL_TO_GET_CURRENCY_PAIR_INFO, msg);
        }
    }

    @GetMapping("/info/max/{name}")
    public ResponseModel getMaxCurrencyPair24h(@PathVariable("name") String name) {
        List<ExOrderStatisticsShortByPairsDto> all = exchangeRatesHolder.getAllRates();

        Optional<ExOrderStatisticsShortByPairsDto> max = all.stream()
                .filter(o -> o.getCurrencyPairName().startsWith(name.toUpperCase()))
                .max(Comparator.comparing(ExOrderStatisticsShortByPairsDto::getVolume));

        ExOrderStatisticsShortByPairsDto result = max.orElseGet(() -> all.stream()
                .filter(o -> o.getCurrencyPairName().startsWith(name.toUpperCase()))
                .findFirst()
                .orElseThrow(() -> new NgDashboardException("No results")));

        return new ResponseModel<>(result);
    }

    @GetMapping("/currencies/fast")
    @ResponseBody
    public List<ExOrderStatisticsShortByPairsDto> getCurrencyPairInfoAll() {
        return orderService.getAllCurrenciesMarkersForAllPairsModel();
    }

    // /info/public/v2//accepted-orders/fast?pairId=1
    @GetMapping("/accepted-orders/fast")
    @ResponseBody
    public List<OrderAcceptedHistoryDto> getLastAcceptedOrders(@RequestParam(value = "pairId") Integer pairId) {
        CurrencyPair cp = currencyService.findCurrencyPairById(pairId);
        return orderService.getOrderAcceptedForPeriodEx(null, new BackDealInterval("24 HOUR"),
                25, cp, Locale.ENGLISH);
    }

//    @GetMapping("/currencies/fromdb")
//    @ResponseBody
//    public List<StatisticForMarket> getCurrencyPairInfoAllFromDb() {
//        return marketRatesHolder.getAllFromDb();
//    }

    @GetMapping("/pair/{part}/{name}")
    public ResponseEntity getPairsByPartName(@PathVariable String name,
                                             @PathVariable String part) {
        List<CurrencyPair> result;

        if (part.equalsIgnoreCase("first")) {
            result = ngOrderService.getAllPairsByFirstPartName(name);
        } else {
            result = ngOrderService.getAllPairsBySecondPartName(name);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
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

    @GetMapping("/crypto-currencies")
    @ResponseBody
    public List<Currency> getCryptoCurrencies() {
        try {
            List<Currency> currencies = currencyService.getCurrencies(MerchantProcessType.CRYPTO);
            return currencies
                    .stream()
                    .filter(cur -> cur.getName().equalsIgnoreCase("RUB"))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Failed to get all hashed currency names");
            return Collections.emptyList();
        }
    }

    // /info/private/v2/balances/refill/fiat-currencies

    /**
     * @return set of unique currencies names which market is FIAT
     */
    @GetMapping("/fiat-currencies")
    @ResponseBody
    public List<Currency> getFiatCurrencies() {
        try {
            return currencyService.getCurrencies(MerchantProcessType.MERCHANT, MerchantProcessType.INVOICE);
        } catch (Exception e) {
            logger.error("Failed to get all fiat names");
            return Collections.emptyList();
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({NgDashboardException.class, IllegalArgumentException.class})
    @ResponseBody
    public ErrorInfo OtherErrorsHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

}
