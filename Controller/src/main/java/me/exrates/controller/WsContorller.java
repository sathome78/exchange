package me.exrates.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.CurrencyPair;
import me.exrates.model.chart.ChartTimeFrame;
import me.exrates.model.dto.AlertDto;
import me.exrates.model.dto.OrdersListWrapper;
import me.exrates.model.enums.ChartPeriodsEnum;
import me.exrates.model.enums.ChartTimeFramesEnum;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.model.vo.BackDealInterval2;
import me.exrates.service.*;
import me.exrates.service.CurrencyService;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.cache.ChartsCache;
import me.exrates.service.cache.ChartsCacheManager;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import javax.websocket.EncodeException;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Maks on 24.08.2017.
 */
@Log4j2(topic = "ws_stomp_log")
@Controller
public class WsContorller {

    @Autowired
    private OrderService orderService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private UsersAlertsService usersAlertsService;
    @Autowired
    private ChartsCacheManager chartsCacheManager;
    @Autowired
    private ChartsCache chartsCache;


    @SubscribeMapping("/users_alerts/{loc}")
    public String usersAlerts(@DestinationVariable String loc) throws JsonProcessingException {
        log.debug("locale {}", loc);
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

    @SubscribeMapping("/statistics2")
    public String subscribeStatistic2() {
        return orderService.getAllCurrenciesStatForRefreshForAllPairs();
    }

    @SubscribeMapping("/statistics")
    public String subscribeStatistic() {
        return orderService.getAllCurrenciesStatForRefreshForAllPairs();
    }

    @SubscribeMapping("/queue/trade_orders/f/{currencyId}")
    public String subscribeOrdersFiltered(@DestinationVariable Integer currencyId, Principal principal) throws IOException, EncodeException {
        UserRole role = userService.getUserRoleFromDB(principal.getName());
        return initOrders(currencyId, role);
    }


    @SubscribeMapping("/trades/{currencyPairId}")
    public String subscribeTrades(@DestinationVariable Integer currencyPairId, SimpMessageHeaderAccessor headerAccessor) throws Exception {
        Principal principal = headerAccessor.getUser();

        return orderService.getAllAndMyTradesForInit(currencyPairId, principal);
    }

    @SubscribeMapping("/charts/{currencyPairId}/{resolution}")
    public String subscribeChart(@DestinationVariable Integer currencyPairId, @DestinationVariable String resolution) throws Exception {
        ChartTimeFrame timeFrame = ChartTimeFramesEnum.ofResolution(resolution).getTimeFrame();
//        Map<String, String> data = chartsCache.getData(currencyPairId);
        String preparedData = chartsCacheManager.getPreparedData(currencyPairId, timeFrame, false);
        return preparedData;
    }

    @SubscribeMapping("/charts2/{currencyPairId}/{resolution}")
    public String subscribeChart2(@DestinationVariable Integer currencyPairId, @DestinationVariable String resolution) throws Exception {
        ChartTimeFrame timeFrame = ChartTimeFramesEnum.ofResolution(resolution).getTimeFrame();
//        Map<String, String> data = chartsCache.getData(currencyPairId);
        String preparedData = chartsCacheManager.getPreparedData(currencyPairId, timeFrame, false);
        return preparedData;
    }

    @SubscribeMapping("/trade_orders/{currencyPairId}")
    public String subscribeTradeOrders(@DestinationVariable Integer currencyPairId) throws Exception {
        return initOrders(currencyPairId, null);
    }


    private String initOrders(Integer currencyPair, UserRole userRole) throws IOException, EncodeException {
        CurrencyPair cp = currencyService.findCurrencyPairById(currencyPair);
        if (cp == null) {
            return null;
        }
        JSONArray objectsArray = new JSONArray();
        objectsArray.put(objectMapper.writeValueAsString(new OrdersListWrapper(orderService.getAllSellOrdersEx
                (cp, Locale.ENGLISH, userRole), OperationType.SELL.name(), currencyPair)));
        objectsArray.put(objectMapper.writeValueAsString(new OrdersListWrapper(orderService.getAllBuyOrdersEx
                (cp, Locale.ENGLISH, userRole), OperationType.BUY.name(), currencyPair)));
        return objectsArray.toString();
    }

}
