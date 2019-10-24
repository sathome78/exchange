package me.exrates.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.CurrencyPair;
import me.exrates.model.IEODetails;
import me.exrates.model.User;
import me.exrates.model.dto.OrderBookWrapperDto;
import me.exrates.model.dto.OrdersListWrapper;
import me.exrates.model.dto.onlineTableDto.OrderAcceptedHistoryDto;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.enums.OrderType;
import me.exrates.model.enums.PrecissionsEnum;
import me.exrates.model.ngModel.ResponseInfoCurrencyPairDto;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.ngService.RedisUserNotificationService;
import me.exrates.service.CurrencyService;
import me.exrates.service.IEOService;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.UsersAlertsService;
import me.exrates.service.bitshares.memo.Preconditions;
import me.exrates.service.util.OpenApiUtils;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Locale;


@Log4j2(topic = "ws_stomp_log")
@Controller
public class WsController {

    private final CurrencyService currencyService;
    private final IEOService ieoService;
    private final ObjectMapper objectMapper;
    private final OrderService orderService;
    private final RedisUserNotificationService redisUserNotificationService;
    private final UserService userService;
    private final UsersAlertsService usersAlertsService;

    public WsController(CurrencyService currencyService,
                        IEOService ieoService,
                        ObjectMapper objectMapper,
                        OrderService orderService,
                        RedisUserNotificationService redisUserNotificationService,
                        UserService userService,
                        UsersAlertsService usersAlertsService) {
        this.currencyService = currencyService;
        this.ieoService = ieoService;
        this.objectMapper = objectMapper;
        this.orderService = orderService;
        this.redisUserNotificationService = redisUserNotificationService;
        this.userService = userService;
        this.usersAlertsService = usersAlertsService;
    }

//    @SubscribeMapping("/users_alerts/{loc}")
//    public String usersAlerts(@DestinationVariable String loc) throws JsonProcessingException {
//        if (!userService.getLocalesList().contains(loc)) {
//            throw new RuntimeException("unsupported locale");
//        }
//        Locale locale = Locale.forLanguageTag(loc);
//        List<AlertDto> list = usersAlertsService.getAllAlerts(locale);
//        return objectMapper.writeValueAsString(list);
//    }

    @SubscribeMapping("/statisticsNew")
    public String subscribeStatisticNew() {
        return orderService.getAllCurrenciesStatForRefreshForAllPairs();
    }

    @SubscribeMapping("/statistics/pairInfo/{pairName}")
    public ResponseInfoCurrencyPairDto subscribePairInfo(@DestinationVariable String pairName) {
        return orderService.getStatForPair(OpenApiUtils.transformCurrencyPair(pairName));
    }

    @SubscribeMapping("/all_trades/{pairName}")
    public List<OrderAcceptedHistoryDto> subscribeAllTrades(@DestinationVariable String pairName) {
        CurrencyPair cp = currencyService.getCurrencyPairByName(OpenApiUtils.transformCurrencyPair(pairName));
        Preconditions.checkNotNull(cp);
        return orderService.getOrderAcceptedForPeriodEx(null, new BackDealInterval("24 HOUR"),
                25, cp, Locale.ENGLISH);
    }

    @SubscribeMapping("/order_book/{pairName}/{precision}")
    public List<OrderBookWrapperDto> subscribeOrdersBook(@DestinationVariable String pairName, @DestinationVariable Integer precision) {
        PrecissionsEnum precissionsEnum = PrecissionsEnum.convert(precision);
        CurrencyPair currencyPair = currencyService.getCurrencyPairByName(OpenApiUtils.transformCurrencyPair(pairName));
        Preconditions.checkNotNull(currencyPair);
        return ImmutableList.of(
                orderService.findAllOrderBookItems(OrderType.SELL, currencyPair.getId(), precissionsEnum.getValue()),
                orderService.findAllOrderBookItems(OrderType.BUY, currencyPair.getId(), precissionsEnum.getValue()));
    }

    /*alterdice use it*/
    @SubscribeMapping("/orders/sfwfrf442fewdf/detailed/{currencyPairName}")
    public List<OrdersListWrapper> subscribeTradeOrdersDetailed(@DestinationVariable String currencyPairName) {
        return orderService.getOpenOrdersForWs(OpenApiUtils.transformCurrencyPair(currencyPairName));
    }

//    @MessageMapping("/register")
//    public void processMessage(@Payload Map<String, String> message, SimpMessageHeaderAccessor simpMessageHeaderAccessor) throws Exception {
//        redisWsSessionService.addSession(message.get("email"), message.get("sessionId"));
//    }
//
//    @MessageMapping("/unregister")
//    public void processOffMessage(@Payload Map<String, String> message, SimpMessageHeaderAccessor simpMessageHeaderAccessor) throws Exception {
//        redisWsSessionService.removeSession(message.get("email"));
//    }

//    @SubscribeMapping("/message/private/{pubId}")
//    public WsMessageObject subscribePersonalMessages(Principal principal, @DestinationVariable String pubId) {
//        Preconditions.checkArgument(userService.getEmailByPubId(pubId).equals(principal.getName()));
//        final Collection<UserNotificationMessage> messages = redisUserNotificationService.findAllByUser(principal.getName());
//        return new WsMessageObject(WsSourceTypeEnum.SUBSCRIBE, messages);
//    }
    @SubscribeMapping("/orders/open/{pairName}/{pubId}")
    public String getUserOpenOrdersByPairName(Principal principal,
                                              @DestinationVariable String pairName,
                                              @DestinationVariable String pubId) {
        Preconditions.checkArgument(userService.getEmailByPubId(pubId).equals(principal.getName()));
        String currencyPairName = currencyService.getCurrencyPairByName(OpenApiUtils.transformCurrencyPair(pairName)).getName();
        final List<OrderWideListDto> userOrders = orderService.getMyOpenOrdersWithState(currencyPairName, principal.getName());
        try {
            return objectMapper.writeValueAsString(userOrders);
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    @SubscribeMapping("/ieo_details/private/{pubId}")
    public Collection<IEODetails> subscribeIeoDetailsPersonal(Principal principal, @DestinationVariable String pubId) {
        Preconditions.checkArgument(userService.getEmailByPubId(pubId).equals(principal.getName()));
        User user = userService.findByEmail(principal.getName());
        return ieoService.findAll(user);
    }

    @SubscribeMapping("/ieo/ieo_details")
    public Collection<IEODetails> subscribeIeoDetailsPublic() {
        return ieoService.findAll(null);
    }

    @SubscribeMapping("/ieo/ieo_details/{detailId}")
    public IEODetails subscribeIeoDetails(@DestinationVariable Integer detailId) {
        IEODetails ieoDetails = ieoService.findOne(Preconditions.checkNotNull(detailId));
        if (ieoDetails == null) {
            throw new RuntimeException("Ieo details cannot be null");
        }
        return ieoDetails;
    }
}
