package me.exrates.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.CurrencyPair;
import me.exrates.model.IEODetails;
import me.exrates.model.User;
import me.exrates.model.dto.AlertDto;
import me.exrates.model.dto.OrderBookWrapperDto;
import me.exrates.model.dto.OrdersListWrapper;
import me.exrates.model.dto.WsMessageObject;
import me.exrates.model.dto.onlineTableDto.OrderAcceptedHistoryDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderType;
import me.exrates.model.enums.PrecissionsEnum;
import me.exrates.model.enums.RefreshObjectsEnum;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.WsMessageTypeEnum;
import me.exrates.model.ngModel.ResponseInfoCurrencyPairDto;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.service.CurrencyService;
import me.exrates.service.IEOService;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.UsersAlertsService;
import me.exrates.service.bitshares.memo.Preconditions;
import me.exrates.service.cache.ChartsCacheManager;
import me.exrates.service.cache.currencyPairsInfo.CpStatisticsHolder;
import me.exrates.service.impl.IEOServiceImpl;
import me.exrates.service.util.OpenApiUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import javax.websocket.EncodeException;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;


@Log4j2(topic = "ws_stomp_log")
@Controller
public class WsController {

    private final OrderService orderService;
    private final CurrencyService currencyService;
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final UsersAlertsService usersAlertsService;
    private final IEOService ieoService;

    @Autowired
    public WsController(OrderService orderService, CurrencyService currencyService, ObjectMapper objectMapper, UserService userService, UsersAlertsService usersAlertsService, IEOService ieoService) {
        this.orderService = orderService;
        this.currencyService = currencyService;
        this.objectMapper = objectMapper;
        this.userService = userService;
        this.usersAlertsService = usersAlertsService;
        this.ieoService = ieoService;
    }


    @SubscribeMapping("/users_alerts/{loc}")
    public String usersAlerts(@DestinationVariable String loc) throws JsonProcessingException {
        if (!userService.getLocalesList().contains(loc)) {
            throw new RuntimeException("unsupported locale");
        }
        Locale locale = Locale.forLanguageTag(loc);
        List<AlertDto> list = usersAlertsService.getAllAlerts(locale);
        return objectMapper.writeValueAsString(list);
    }

    @SubscribeMapping("/ev/{sessionId}")
    public String subscribeEvents(@DestinationVariable String sessionId) {
        return "ok";
    }

    @SubscribeMapping("/statisticsNew")
    public String subscribeStatisticNew() {
        return orderService.getAllCurrenciesStatForRefreshForAllPairs();
    }

    @SubscribeMapping("/statistics/{type}")
    public String subscribeStatistic(@DestinationVariable String type) {
        RefreshObjectsEnum refreshObjectsEnum = RefreshObjectsEnum.valueOf(type);
        return orderService.getAllCurrenciesStatForRefresh(refreshObjectsEnum);
    }

    @SubscribeMapping("/statistics/pairInfo/{pairName}")
    public ResponseInfoCurrencyPairDto subscribePairInfo(@DestinationVariable String pairName) {
        return orderService.getStatForPair(OpenApiUtils.transformCurrencyPair(pairName));
    }

    @SubscribeMapping("/queue/trade_orders/f/{currencyId}")
    public List<OrdersListWrapper> subscribeOrdersFiltered(@DestinationVariable Integer currencyId, Principal principal) throws IOException, EncodeException {
        UserRole role = userService.getUserRoleFromDB(principal.getName());
        return initOrders(currencyId, role);
    }

    @SubscribeMapping("/trades/{currencyPairId}")
    public String subscribeTrades(@DestinationVariable Integer currencyPairId, SimpMessageHeaderAccessor headerAccessor) throws Exception {
        Principal principal = headerAccessor.getUser();
        return orderService.getAllAndMyTradesForInit(currencyPairId, principal);
    }

    @SubscribeMapping("/all_trades/{pairName}")
    public List<OrderAcceptedHistoryDto> subscribeAllTrades(@DestinationVariable String pairName) {
        CurrencyPair cp = currencyService.getCurrencyPairByName(OpenApiUtils.transformCurrencyPair(pairName));
        Preconditions.checkNotNull(cp);
        return orderService.getOrderAcceptedForPeriodEx(null, new BackDealInterval("24 HOUR"),
                25, cp, Locale.ENGLISH);
    }

    @SubscribeMapping("/trade_orders/{currencyPairId}")
    public List<OrdersListWrapper> subscribeTradeOrders(@DestinationVariable Integer currencyPairId) throws Exception {
        return initOrders(currencyPairId, null);
    }

    @SubscribeMapping("/orders/sfwfrf442fewdf/{currencyPairId}")
    public List<OrdersListWrapper> subscribeTradeOrdersHidden(@DestinationVariable Integer currencyPairId) throws Exception {
        return initOrders(currencyPairId, null);
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

    /*alterdice use it*/
    @SubscribeMapping("/queue/my_orders/{currencyPairName}")
    public List<OrdersListWrapper> subscribeMyTradeOrdersDetailed(@DestinationVariable String currencyPairName, Principal principal) {
        return orderService.getMyOpenOrdersForWs(OpenApiUtils.transformCurrencyPair(currencyPairName), principal.getName());
    }

    @SubscribeMapping("/queue/personal_message")
    public WsMessageObject subscribePersonalMessages(Principal principal) {
        return new WsMessageObject(WsMessageTypeEnum.SUBSCRIBE, "ok");
    }

    @SubscribeMapping("/queue/ieo_details")
    public Collection<IEODetails> subscribeIeoDetailsPersonal(Principal principal) {
        User user = userService.findByEmail(principal.getName());
        return ieoService.findAll(user);
    }

    @SubscribeMapping("/ieo/ieo_details/{detailId}")
    public IEODetails subscribeIeoDetails(@DestinationVariable Integer detailId) {
        return ieoService.findOne(Preconditions.checkNotNull(detailId));
    }

    private List<OrdersListWrapper> initOrders(Integer currencyPair, UserRole userRole) throws IOException {
        CurrencyPair cp = currencyService.findCurrencyPairById(currencyPair);
        if (cp == null) {
            return null;
        }
        List<OrdersListWrapper> list = new ArrayList<>();
        list.add(new OrdersListWrapper(orderService.getAllSellOrdersEx
                (cp, Locale.ENGLISH, userRole), OperationType.SELL.name(), currencyPair));
        list.add(new OrdersListWrapper(orderService.getAllBuyOrdersEx
                (cp, Locale.ENGLISH, userRole), OperationType.BUY.name(), currencyPair));
        return list;
    }
}
