package me.exrates.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.GsonBuilder;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.CurrencyPair;
import me.exrates.model.chart.ChartTimeFrame;
import me.exrates.model.dto.AlertDto;
import me.exrates.model.dto.OrdersListWrapper;
import me.exrates.model.enums.*;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.service.CurrencyService;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.UsersAlertsService;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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
    @Autowired
    private WalletService walletService;


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

	@SubscribeMapping("/info/statistics")
	public String subscribeStatisticNg() {
    	return orderService.getAllCurrenciesStatForRefreshNg();
	}

    @SubscribeMapping("/queue/balance/{currencyId1}/{currencyId2}")
    public String subscribeCurrencyPairBalances(@DestinationVariable Integer currencyId1,
                                                @DestinationVariable Integer currencyId2,
                                                SimpMessageHeaderAccessor headerAccessor) {
        Principal principal = headerAccessor.getUser();
        if(principal == null) {
            return "";
        }
        return walletService.getActiveBalanceForCurrencies(Arrays.asList(currencyId1, currencyId2), principal.getName());
    }

    @SubscribeMapping("/queue/trade_orders/f/{currencyId}")
    public String subscribeOrdersFiltered(@DestinationVariable Integer currencyId, Principal principal) throws IOException, EncodeException {
        UserRole role = userService.getUserRoleFromDB(principal.getName());
        return initOrders(currencyId, role);
    }


    @SubscribeMapping("/trades/{currencyPairId}")
    public String subscribeTradesNg(@DestinationVariable Integer currencyPairId) throws Exception {
        return orderService.getAllAndMyTradesForInitNg(currencyPairId, RefreshObjectsEnum.ALL_TRADES, null);
    }

    @SubscribeMapping("/queue/personal/{currencyPairId}")
    public String subscribeTradesNg(@DestinationVariable Integer currencyPairId,  SimpMessageHeaderAccessor headerAccessor) throws Exception {
        Principal principal = headerAccessor.getUser();
        log.info("principal {}", principal);
        if (principal == null) {
            return "";
        }
        return orderService.getAllAndMyTradesForInitNg(currencyPairId, RefreshObjectsEnum.MY_TRADES, principal);
    }

    @SubscribeMapping("/charts/{currencyPairId}/{period}")
    public String subscribeChart(@DestinationVariable Integer currencyPairId, @DestinationVariable String period) throws Exception {
        BackDealInterval backDealInterval = ChartPeriodsEnum.convert(period).getBackDealInterval();
        return chartsCache.getDataForPeriod(currencyPairId, backDealInterval.getInterval());
    }

    @SubscribeMapping("/charts2/{currencyPairId}/{resolution}")
    public String subscribeChart2(@DestinationVariable Integer currencyPairId, @DestinationVariable String resolution) throws Exception {
        ChartTimeFrame timeFrame = ChartTimeFramesEnum.ofResolution(resolution).getTimeFrame();
        String preparedData = chartsCacheManager.getPreparedData(currencyPairId, timeFrame, false);
        return preparedData;
    }

    @SubscribeMapping("/trade_orders/{currencyPairId}")
    public String subscribeTradeOrders(@DestinationVariable Integer currencyPairId) throws Exception {
        return initOrders(currencyPairId, null);
    }

    /*@SubscribeMapping("/info/trade_orders/{currencyPairId}")
    public String subscribeToTradeOrders(@DestinationVariable Integer currencyPairId) throws Exception {
        return getSimpleTradeOrders(currencyPairId);
    }

    private String getSimpleTradeOrders(Integer currencyPairId) throws JsonProcessingException {
        CurrencyPair cp = currencyService.findCurrencyPairById(currencyPairId);
        if (cp == null) {
            return null;
        }
        List<OrderListDto> orders = new ArrayList<>();
        orders.addAll(orderService.getAllSellOrdersEx(cp, Locale.ENGLISH, null));
        orders.addAll(orderService.getAllBuyOrdersEx(cp, Locale.ENGLISH, null));
        return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(orders);
    }*/

    private String initOrders(Integer currencyPair, UserRole userRole) throws IOException, EncodeException {
        CurrencyPair cp = currencyService.findCurrencyPairById(currencyPair);
        if (cp == null) {
            return null;
        }
        List<OrdersListWrapper> wrappers = new ArrayList<>();
        wrappers.add(orderService.getOrdersForRefresh(currencyPair, OperationType.SELL, null));
        wrappers.add(orderService.getOrdersForRefresh(currencyPair, OperationType.BUY, null));
        return new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(wrappers);
    }

}
