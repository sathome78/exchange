package me.exrates.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import me.exrates.model.Currency;
import me.exrates.model.CurrencyPair;
import me.exrates.model.dto.AlertDto;
import me.exrates.model.dto.OrderBookWrapperDto;
import me.exrates.model.dto.OrdersListWrapper;
import me.exrates.model.dto.SimpleOrderBookItem;
import me.exrates.model.dto.UserNotificationMessage;
import me.exrates.model.dto.onlineTableDto.OrderAcceptedHistoryDto;
import me.exrates.model.dto.onlineTableDto.OrderListDto;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderType;
import me.exrates.model.enums.RefreshObjectsEnum;
import me.exrates.model.enums.UserNotificationType;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.WsSourceTypeEnum;
import me.exrates.model.ngModel.ResponseInfoCurrencyPairDto;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.ngService.RedisUserNotificationService;
import me.exrates.service.CurrencyService;
import me.exrates.service.IEOService;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.UsersAlertsService;
import me.exrates.service.impl.OrderServiceImpl;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.util.JsonPathExpectationsHelper;

import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class WsControllerTest {
    private OrderService orderService;
    private CurrencyService currencyService;
    private ObjectMapper objectMapper;
    private UserService userService;
    private UsersAlertsService usersAlertsService;
    private RedisUserNotificationService redisUserNotificationService;
    private IEOService ieoService;

    private TestMessageChannel clientOutboundChannel;
    private TestAnnotationMethodHandler annotationMethodHandler;

    @Before
    public void init() {
        this.currencyService = Mockito.mock(CurrencyService.class);
        this.orderService = Mockito.mock(OrderServiceImpl.class);
        this.objectMapper = Mockito.mock(ObjectMapper.class);
        this.redisUserNotificationService = Mockito.mock(RedisUserNotificationService.class);
        this.userService = Mockito.mock(UserService.class);
        this.usersAlertsService = Mockito.mock(UsersAlertsService.class);

        WsController wsController = new WsController(currencyService, ieoService,
                objectMapper, orderService, redisUserNotificationService, userService, usersAlertsService);

        this.clientOutboundChannel = new TestMessageChannel();

        this.annotationMethodHandler = new TestAnnotationMethodHandler(
                new TestMessageChannel(),
                clientOutboundChannel,
                new SimpMessagingTemplate(new TestMessageChannel())
        );

        this.annotationMethodHandler.registerHandler(wsController);
        this.annotationMethodHandler.setDestinationPrefixes(Collections.singletonList("/"));
        this.annotationMethodHandler.setMessageConverter(new MappingJackson2MessageConverter());
        this.annotationMethodHandler.setApplicationContext(new StaticApplicationContext());
        this.annotationMethodHandler.afterPropertiesSet();
    }

    @Test
    @Ignore
    public void usersAlerts_isOk() throws Exception {
        AlertDto mockAlertDto = AlertDto.builder().build();
        mockAlertDto.setText("SOME_TEXT");
        mockAlertDto.setAlertType("ALERT_TYPE");
        mockAlertDto.setEnabled(Boolean.TRUE);
        mockAlertDto.setEventStart(LocalDateTime.of(2019, 4, 2, 10, 18, 25));
        mockAlertDto.setLenghtOfWorks(5);
        mockAlertDto.setMinutes(20);
        mockAlertDto.setEventStart(LocalDateTime.of(2019, 6, 2, 10, 18, 25));
        mockAlertDto.setTimeRemainSeconds(35L);

        String result = new ObjectMapper().writeValueAsString(mockAlertDto);

        when(userService.getLocalesList()).thenReturn(getMockLocalesList());
        when(usersAlertsService.getAllAlerts(any(Locale.class))).thenReturn(Collections.singletonList(mockAlertDto));
        when(objectMapper.writeValueAsString(anyListOf(AlertDto.class))).thenReturn(result);

        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        headers.setSubscriptionId("0");
        headers.setDestination("/users_alerts/RU");
        headers.setSessionId("0");
        headers.setSessionAttributes(new HashMap<>());
        Message<byte[]> message = MessageBuilder.withPayload(new byte[0]).setHeaders(headers).build();

        this.annotationMethodHandler.handleMessage(message);

        assertEquals(1, this.clientOutboundChannel.getMessages().size());
        Message<?> reply = this.clientOutboundChannel.getMessages().get(0);

        StompHeaderAccessor replyHeaders = StompHeaderAccessor.wrap(reply);
        assertEquals("0", replyHeaders.getSessionId());
        assertEquals("0", replyHeaders.getSubscriptionId());
        assertEquals("/users_alerts/RU", replyHeaders.getDestination());

        String json = new String((byte[]) reply.getPayload(), Charset.forName("UTF-8"));
        new JsonPathExpectationsHelper("$").assertValue(json, result);
    }

    @Test
    @Ignore
    public void subscribeEvents() {
        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        headers.setSubscriptionId("0");
        headers.setDestination("/ev/0");
        headers.setSessionId("0");
        headers.setSessionAttributes(new HashMap<>());
        Message<byte[]> message = MessageBuilder.withPayload(new byte[0]).setHeaders(headers).build();

        this.annotationMethodHandler.handleMessage(message);

        assertEquals(1, this.clientOutboundChannel.getMessages().size());
        Message<?> reply = this.clientOutboundChannel.getMessages().get(0);

        StompHeaderAccessor replyHeaders = StompHeaderAccessor.wrap(reply);
        assertEquals("0", replyHeaders.getSessionId());
        assertEquals("0", replyHeaders.getSubscriptionId());
        assertEquals("/ev/0", replyHeaders.getDestination());

        String json = new String((byte[]) reply.getPayload(), Charset.forName("UTF-8"));
        new JsonPathExpectationsHelper("$").assertValue(json, "ok");
    }

    @Test
    @Ignore
    public void subscribeStatisticNew() {
        when(orderService.getAllCurrenciesStatForRefreshForAllPairs()).thenReturn("TEST_NEWS");

        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        headers.setSubscriptionId("0");
        headers.setDestination("/statisticsNew");
        headers.setSessionId("0");
        headers.setSessionAttributes(new HashMap<>());
        Message<byte[]> message = MessageBuilder.withPayload(new byte[0]).setHeaders(headers).build();

        this.annotationMethodHandler.handleMessage(message);

        assertEquals(1, this.clientOutboundChannel.getMessages().size());
        Message<?> reply = this.clientOutboundChannel.getMessages().get(0);

        StompHeaderAccessor replyHeaders = StompHeaderAccessor.wrap(reply);
        assertEquals("0", replyHeaders.getSessionId());
        assertEquals("0", replyHeaders.getSubscriptionId());
        assertEquals("/statisticsNew", replyHeaders.getDestination());

        String json = new String((byte[]) reply.getPayload(), Charset.forName("UTF-8"));
        new JsonPathExpectationsHelper("$").assertValue(json, "TEST_NEWS");
    }

    @Test
    @Ignore
    public void subscribeStatistic() {
        when(orderService.getAllCurrenciesStatForRefresh(any(RefreshObjectsEnum.class))).thenReturn("TEST_CHART_STATISTIC");

        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        headers.setSubscriptionId("0");
        headers.setDestination("/statistics/CHART");
        headers.setSessionId("0");
        headers.setSessionAttributes(new HashMap<>());
        Message<byte[]> message = MessageBuilder.withPayload(new byte[0]).setHeaders(headers).build();

        this.annotationMethodHandler.handleMessage(message);

        assertEquals(1, this.clientOutboundChannel.getMessages().size());
        Message<?> reply = this.clientOutboundChannel.getMessages().get(0);

        StompHeaderAccessor replyHeaders = StompHeaderAccessor.wrap(reply);
        assertEquals("0", replyHeaders.getSessionId());
        assertEquals("0", replyHeaders.getSubscriptionId());
        assertEquals("/statistics/CHART", replyHeaders.getDestination());

        String json = new String((byte[]) reply.getPayload(), Charset.forName("UTF-8"));
        new JsonPathExpectationsHelper("$").assertValue(json, "TEST_CHART_STATISTIC");
    }

    @Test
    @Ignore
    public void subscribePairInfo() {
        ResponseInfoCurrencyPairDto mockResponseInfoCurrencyPairDto = new ResponseInfoCurrencyPairDto();
        mockResponseInfoCurrencyPairDto.setCurrencyRate("TEST_CURRENCY_RATE");
        mockResponseInfoCurrencyPairDto.setPercentChange("TEST_PERCENT_CHANGE");
        mockResponseInfoCurrencyPairDto.setChangedValue("TEST_CHANGED_VALUE");
        mockResponseInfoCurrencyPairDto.setLastCurrencyRate("TEST_LAST_CURRENCY_RATE");
        mockResponseInfoCurrencyPairDto.setVolume24h("TEST_VOLUME_24H");
        mockResponseInfoCurrencyPairDto.setRateHigh("TEST_RATE_HIGH");
        mockResponseInfoCurrencyPairDto.setRateLow("TEST_RATE_LOWE");

        when(orderService.getStatForPair(anyString())).thenReturn(mockResponseInfoCurrencyPairDto);

        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        headers.setSubscriptionId("0");
        headers.setDestination("/statistics/pairInfo/btc_usd");
        headers.setSessionId("0");
        headers.setSessionAttributes(new HashMap<>());
        Message<byte[]> message = MessageBuilder.withPayload(new byte[0]).setHeaders(headers).build();

        this.annotationMethodHandler.handleMessage(message);

        assertEquals(1, this.clientOutboundChannel.getMessages().size());
        Message<?> reply = this.clientOutboundChannel.getMessages().get(0);

        StompHeaderAccessor replyHeaders = StompHeaderAccessor.wrap(reply);
        assertEquals("0", replyHeaders.getSessionId());
        assertEquals("0", replyHeaders.getSubscriptionId());
        assertEquals("/statistics/pairInfo/btc_usd", replyHeaders.getDestination());

        String json = new String((byte[]) reply.getPayload(), Charset.forName("UTF-8"));
        new JsonPathExpectationsHelper("$.currencyRate").assertValue(json, "TEST_CURRENCY_RATE");
        new JsonPathExpectationsHelper("$.percentChange").assertValue(json, "TEST_PERCENT_CHANGE");
        new JsonPathExpectationsHelper("$.changedValue").assertValue(json, "TEST_CHANGED_VALUE");
        new JsonPathExpectationsHelper("$.lastCurrencyRate").assertValue(json, "TEST_LAST_CURRENCY_RATE");
        new JsonPathExpectationsHelper("$.volume24h").assertValue(json, "TEST_VOLUME_24H");
        new JsonPathExpectationsHelper("$.rateHigh").assertValue(json, "TEST_RATE_HIGH");
        new JsonPathExpectationsHelper("$.rateLow").assertValue(json, "TEST_RATE_LOWE");
    }

    @Test
    @Ignore
    public void subscribeOrdersFiltered_isOk() {
        when(currencyService.findCurrencyPairById(anyInt())).thenReturn(getMockCurrencyPair());
        when(orderService.getAllSellOrdersEx(any(CurrencyPair.class), any(Locale.class), any(UserRole.class)))
                .thenReturn(Collections.singletonList(getMockOrderListDto()));
        when(orderService.getAllBuyOrdersEx(any(CurrencyPair.class), any(Locale.class), any(UserRole.class)))
                .thenReturn(Collections.singletonList(getMockOrderListDto()));

        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        headers.setSubscriptionId("0");
        headers.setDestination("/queue/trade_orders/f/1");
        headers.setSessionId("0");
        headers.setUser(new TestPrincipal("Andrew"));
        headers.setSessionAttributes(new HashMap<>());
        Message<byte[]> message = MessageBuilder.withPayload(new byte[0]).setHeaders(headers).build();

        this.annotationMethodHandler.handleMessage(message);

        assertEquals(1, this.clientOutboundChannel.getMessages().size());
        Message<?> reply = this.clientOutboundChannel.getMessages().get(0);

        StompHeaderAccessor replyHeaders = StompHeaderAccessor.wrap(reply);
        assertEquals("0", replyHeaders.getSessionId());
        assertEquals("0", replyHeaders.getSubscriptionId());
        assertEquals("/queue/trade_orders/f/1", replyHeaders.getDestination());

        String json = new String((byte[]) reply.getPayload(), Charset.forName("UTF-8"));
        new JsonPathExpectationsHelper("$.[0].data.[0].id").assertValue(json, 100);
        new JsonPathExpectationsHelper("$.[0].data.[0].userId").assertValue(json, 200);
        new JsonPathExpectationsHelper("$.[0].data.[0].orderType").assertValue(json, "BUY");
        new JsonPathExpectationsHelper("$.[0].data.[0].exrate").assertValue(json, "TEST_EXRATE");
        new JsonPathExpectationsHelper("$.[0].data.[0].amountBase").assertValue(json, "TEST_AMOUNT_BASE");
        new JsonPathExpectationsHelper("$.[0].data.[0].amountConvert").assertValue(json, "TEST_AMOUNT_CONVERT");
        new JsonPathExpectationsHelper("$.[0].data.[0].ordersIds").assertValue(json, "TEST_ORDERS_IDS");
        new JsonPathExpectationsHelper("$.[0].type").assertValue(json, "SELL");
        new JsonPathExpectationsHelper("$.[0].currencyPairId").assertValue(json, 1);
        new JsonPathExpectationsHelper("$.[1].data.[0].id").assertValue(json, 100);
        new JsonPathExpectationsHelper("$.[1].data.[0].userId").assertValue(json, 200);
        new JsonPathExpectationsHelper("$.[1].data.[0].orderType").assertValue(json, "BUY");
        new JsonPathExpectationsHelper("$.[1].data.[0].exrate").assertValue(json, "TEST_EXRATE");
        new JsonPathExpectationsHelper("$.[1].data.[0].amountBase").assertValue(json, "TEST_AMOUNT_BASE");
        new JsonPathExpectationsHelper("$.[1].data.[0].amountConvert").assertValue(json, "TEST_AMOUNT_CONVERT");
        new JsonPathExpectationsHelper("$.[1].data.[0].ordersIds").assertValue(json, "TEST_ORDERS_IDS");
        new JsonPathExpectationsHelper("$.[1].type").assertValue(json, "BUY");
        new JsonPathExpectationsHelper("$.[1].currencyPairId").assertValue(json, 1);
    }

    @Test
    @Ignore
    public void subscribeOrdersFiltered_currency_pair_equals_null() {
        when(userService.getUserRoleFromDB(anyString())).thenReturn(UserRole.USER);
        when(currencyService.findCurrencyPairById(anyInt())).thenReturn(null);

        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        headers.setSubscriptionId("0");
        headers.setDestination("/queue/trade_orders/f/1");
        headers.setSessionId("0");
        headers.setUser(new TestPrincipal("Andrew"));
        headers.setSessionAttributes(new HashMap<>());
        Message<byte[]> message = MessageBuilder.withPayload(new byte[0]).setHeaders(headers).build();

        this.annotationMethodHandler.handleMessage(message);

        assertEquals(0, this.clientOutboundChannel.getMessages().size());
    }

    @Test
    @Ignore
    public void subscribeTrades() throws Exception {
        when(orderService.getAllAndMyTradesForInit(anyInt(), any(Principal.class))).thenReturn("TEST_TRADES");

        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        headers.setSubscriptionId("0");
        headers.setDestination("/trades/1");
        headers.setSessionId("0");
        headers.setUser(new TestPrincipal("Andrew"));
        headers.setSessionAttributes(new HashMap<>());
        Message<byte[]> message = MessageBuilder.withPayload(new byte[0]).setHeaders(headers).build();

        this.annotationMethodHandler.handleMessage(message);

        assertEquals(1, this.clientOutboundChannel.getMessages().size());
        Message<?> reply = this.clientOutboundChannel.getMessages().get(0);

        StompHeaderAccessor replyHeaders = StompHeaderAccessor.wrap(reply);
        assertEquals("0", replyHeaders.getSessionId());
        assertEquals("0", replyHeaders.getSubscriptionId());
        assertEquals("/trades/1", replyHeaders.getDestination());

        String json = new String((byte[]) reply.getPayload(), Charset.forName("UTF-8"));
        new JsonPathExpectationsHelper("$").assertValue(json, "TEST_TRADES");
    }

    @Test
    @Ignore
    public void subscribeAllTrades_isOk() {
        OrderAcceptedHistoryDto mockOrderAcceptedHistoryDto = new OrderAcceptedHistoryDto();
        mockOrderAcceptedHistoryDto.setOrderId(100);
        mockOrderAcceptedHistoryDto.setDateAcceptionTime("TEST_DATE_ACCEPTION_TIME");
        mockOrderAcceptedHistoryDto.setAcceptionTime(Timestamp.from(Instant.MIN));
        mockOrderAcceptedHistoryDto.setRate("TEST_RATE");
        mockOrderAcceptedHistoryDto.setAmountBase("TEST_AMOUNT_BASE");
        mockOrderAcceptedHistoryDto.setOperationType(OperationType.BUY);

        when(currencyService.getCurrencyPairByName(anyString())).thenReturn(getMockCurrencyPair());
        when(orderService.getOrderAcceptedForPeriodEx(
                any(),
                any(BackDealInterval.class),
                anyInt(),
                any(CurrencyPair.class),
                any(Locale.class))
        ).thenReturn(Collections.singletonList(mockOrderAcceptedHistoryDto));

        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        headers.setSubscriptionId("0");
        headers.setDestination("/all_trades/btc_usd");
        headers.setSessionId("0");
        headers.setSessionAttributes(new HashMap<>());
        Message<byte[]> message = MessageBuilder.withPayload(new byte[0]).setHeaders(headers).build();

        this.annotationMethodHandler.handleMessage(message);

        assertEquals(1, this.clientOutboundChannel.getMessages().size());
        Message<?> reply = this.clientOutboundChannel.getMessages().get(0);

        StompHeaderAccessor replyHeaders = StompHeaderAccessor.wrap(reply);
        assertEquals("0", replyHeaders.getSessionId());
        assertEquals("0", replyHeaders.getSubscriptionId());
        assertEquals("/all_trades/btc_usd", replyHeaders.getDestination());

        String json = new String((byte[]) reply.getPayload(), Charset.forName("UTF-8"));
        new JsonPathExpectationsHelper("$.[0].needRefresh").assertValue(json, Boolean.TRUE);
        new JsonPathExpectationsHelper("$.[0].page").assertValue(json, 0);
        new JsonPathExpectationsHelper("$.[0].orderId").assertValue(json, 100);
        new JsonPathExpectationsHelper("$.[0].dateAcceptionTime").assertValue(json, "TEST_DATE_ACCEPTION_TIME");
        new JsonPathExpectationsHelper("$.[0].acceptionTime").assertValue(json, 5336473980199903000L);
        new JsonPathExpectationsHelper("$.[0].rate").assertValue(json, "TEST_RATE");
        new JsonPathExpectationsHelper("$.[0].amountBase").assertValue(json, "TEST_AMOUNT_BASE");
        new JsonPathExpectationsHelper("$.[0].operationType").assertValue(json, "BUY");
    }

    @Test
    @Ignore
    public void subscribeTradeOrders() {
        when(currencyService.findCurrencyPairById(anyInt())).thenReturn(getMockCurrencyPair());
        when(orderService.getAllSellOrdersEx(any(CurrencyPair.class), any(Locale.class), any(UserRole.class)))
                .thenReturn(Collections.singletonList(getMockOrderListDto()));
        when(orderService.getAllBuyOrdersEx(any(CurrencyPair.class), any(Locale.class), any(UserRole.class)))
                .thenReturn(Collections.singletonList(getMockOrderListDto()));

        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        headers.setSubscriptionId("0");
        headers.setDestination("/trade_orders/1");
        headers.setSessionId("0");
        headers.setSessionAttributes(new HashMap<>());
        Message<byte[]> message = MessageBuilder.withPayload(new byte[0]).setHeaders(headers).build();

        this.annotationMethodHandler.handleMessage(message);

        assertEquals(1, this.clientOutboundChannel.getMessages().size());
        Message<?> reply = this.clientOutboundChannel.getMessages().get(0);

        StompHeaderAccessor replyHeaders = StompHeaderAccessor.wrap(reply);
        assertEquals("0", replyHeaders.getSessionId());
        assertEquals("0", replyHeaders.getSubscriptionId());
        assertEquals("/trade_orders/1", replyHeaders.getDestination());

        String json = new String((byte[]) reply.getPayload(), Charset.forName("UTF-8"));
        new JsonPathExpectationsHelper("$.[0].data.[0].id").assertValue(json, 100);
        new JsonPathExpectationsHelper("$.[0].data.[0].userId").assertValue(json, 200);
        new JsonPathExpectationsHelper("$.[0].data.[0].orderType").assertValue(json, "BUY");
        new JsonPathExpectationsHelper("$.[0].data.[0].exrate").assertValue(json, "TEST_EXRATE");
        new JsonPathExpectationsHelper("$.[0].data.[0].amountBase").assertValue(json, "TEST_AMOUNT_BASE");
        new JsonPathExpectationsHelper("$.[0].data.[0].amountConvert").assertValue(json, "TEST_AMOUNT_CONVERT");
        new JsonPathExpectationsHelper("$.[0].data.[0].ordersIds").assertValue(json, "TEST_ORDERS_IDS");
        new JsonPathExpectationsHelper("$.[0].type").assertValue(json, "SELL");
        new JsonPathExpectationsHelper("$.[0].currencyPairId").assertValue(json, 1);
        new JsonPathExpectationsHelper("$.[1].data.[0].id").assertValue(json, 100);
        new JsonPathExpectationsHelper("$.[1].data.[0].userId").assertValue(json, 200);
        new JsonPathExpectationsHelper("$.[1].data.[0].orderType").assertValue(json, "BUY");
        new JsonPathExpectationsHelper("$.[1].data.[0].exrate").assertValue(json, "TEST_EXRATE");
        new JsonPathExpectationsHelper("$.[1].data.[0].amountBase").assertValue(json, "TEST_AMOUNT_BASE");
        new JsonPathExpectationsHelper("$.[1].data.[0].amountConvert").assertValue(json, "TEST_AMOUNT_CONVERT");
        new JsonPathExpectationsHelper("$.[1].data.[0].ordersIds").assertValue(json, "TEST_ORDERS_IDS");
        new JsonPathExpectationsHelper("$.[1].type").assertValue(json, "BUY");
        new JsonPathExpectationsHelper("$.[1].currencyPairId").assertValue(json, 1);
    }

    @Test
    @Ignore
    public void subscribeTradeOrdersHidden() {
        when(currencyService.findCurrencyPairById(anyInt())).thenReturn(getMockCurrencyPair());
        when(orderService.getAllSellOrdersEx(any(CurrencyPair.class), any(Locale.class), any(UserRole.class)))
                .thenReturn(Collections.singletonList(getMockOrderListDto()));
        when(orderService.getAllBuyOrdersEx(any(CurrencyPair.class), any(Locale.class), any(UserRole.class)))
                .thenReturn(Collections.singletonList(getMockOrderListDto()));

        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        headers.setSubscriptionId("0");
        headers.setDestination("/orders/sfwfrf442fewdf/1");
        headers.setSessionId("0");
        headers.setSessionAttributes(new HashMap<>());
        Message<byte[]> message = MessageBuilder.withPayload(new byte[0]).setHeaders(headers).build();

        this.annotationMethodHandler.handleMessage(message);

        assertEquals(1, this.clientOutboundChannel.getMessages().size());
        Message<?> reply = this.clientOutboundChannel.getMessages().get(0);

        StompHeaderAccessor replyHeaders = StompHeaderAccessor.wrap(reply);
        assertEquals("0", replyHeaders.getSessionId());
        assertEquals("0", replyHeaders.getSubscriptionId());
        assertEquals("/orders/sfwfrf442fewdf/1", replyHeaders.getDestination());

        String json = new String((byte[]) reply.getPayload(), Charset.forName("UTF-8"));
        new JsonPathExpectationsHelper("$.[0].data.[0].id").assertValue(json, 100);
        new JsonPathExpectationsHelper("$.[0].data.[0].userId").assertValue(json, 200);
        new JsonPathExpectationsHelper("$.[0].data.[0].orderType").assertValue(json, "BUY");
        new JsonPathExpectationsHelper("$.[0].data.[0].exrate").assertValue(json, "TEST_EXRATE");
        new JsonPathExpectationsHelper("$.[0].data.[0].amountBase").assertValue(json, "TEST_AMOUNT_BASE");
        new JsonPathExpectationsHelper("$.[0].data.[0].amountConvert").assertValue(json, "TEST_AMOUNT_CONVERT");
        new JsonPathExpectationsHelper("$.[0].data.[0].ordersIds").assertValue(json, "TEST_ORDERS_IDS");
        new JsonPathExpectationsHelper("$.[0].type").assertValue(json, "SELL");
        new JsonPathExpectationsHelper("$.[0].currencyPairId").assertValue(json, 1);
        new JsonPathExpectationsHelper("$.[1].data.[0].id").assertValue(json, 100);
        new JsonPathExpectationsHelper("$.[1].data.[0].userId").assertValue(json, 200);
        new JsonPathExpectationsHelper("$.[1].data.[0].orderType").assertValue(json, "BUY");
        new JsonPathExpectationsHelper("$.[1].data.[0].exrate").assertValue(json, "TEST_EXRATE");
        new JsonPathExpectationsHelper("$.[1].data.[0].amountBase").assertValue(json, "TEST_AMOUNT_BASE");
        new JsonPathExpectationsHelper("$.[1].data.[0].amountConvert").assertValue(json, "TEST_AMOUNT_CONVERT");
        new JsonPathExpectationsHelper("$.[1].data.[0].ordersIds").assertValue(json, "TEST_ORDERS_IDS");
        new JsonPathExpectationsHelper("$.[1].type").assertValue(json, "BUY");
        new JsonPathExpectationsHelper("$.[1].currencyPairId").assertValue(json, 1);
    }

    @Test
    @Ignore
    public void subscribeOrdersBook() {
        when(currencyService.getCurrencyPairByName(anyString())).thenReturn(getMockCurrencyPair());
        when(orderService.findAllOrderBookItems(any(OrderType.class), anyInt(), anyInt()))
                .thenReturn(getMockOrderBookWrapperDto(OrderType.SELL));
        when(orderService.findAllOrderBookItems(any(OrderType.class), anyInt(), anyInt()))
                .thenReturn(getMockOrderBookWrapperDto(OrderType.BUY));

        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        headers.setSubscriptionId("0");
        headers.setDestination("/order_book/btc_usd/1");
        headers.setSessionId("0");
        headers.setSessionAttributes(new HashMap<>());
        Message<byte[]> message = MessageBuilder.withPayload(new byte[0]).setHeaders(headers).build();

        this.annotationMethodHandler.handleMessage(message);

        assertEquals(1, this.clientOutboundChannel.getMessages().size());
        Message<?> reply = this.clientOutboundChannel.getMessages().get(0);

        StompHeaderAccessor replyHeaders = StompHeaderAccessor.wrap(reply);
        assertEquals("0", replyHeaders.getSessionId());
        assertEquals("0", replyHeaders.getSubscriptionId());
        assertEquals("/order_book/btc_usd/1", replyHeaders.getDestination());

        String json = new String((byte[]) reply.getPayload(), Charset.forName("UTF-8"));
        new JsonPathExpectationsHelper("$.[0].orderType").assertValue(json, "BUY");
        new JsonPathExpectationsHelper("$.[0].lastExrate").assertValue(json, "0.00000004");
        new JsonPathExpectationsHelper("$.[0].preLastExrate").assertValue(json, "0.00000001");
        new JsonPathExpectationsHelper("$.[0].positive").assertValue(json, Boolean.TRUE);
        new JsonPathExpectationsHelper("$.[0].total").assertValue(json, 10);
        new JsonPathExpectationsHelper("$.[0].orderBookItems.[0].currencyPairId").assertValue(json, 1);
        new JsonPathExpectationsHelper("$.[0].orderBookItems.[0].orderType").assertValue(json, "BUY");
        new JsonPathExpectationsHelper("$.[0].orderBookItems.[0].currencyPairName").assertValue(json, "BTC/USD");
        new JsonPathExpectationsHelper("$.[0].orderBookItems.[0].exrate").assertValue(json, 10);
        new JsonPathExpectationsHelper("$.[0].orderBookItems.[0].amount").assertValue(json, 1);
        new JsonPathExpectationsHelper("$.[0].orderBookItems.[0].total").assertValue(json, 0);
        new JsonPathExpectationsHelper("$.[0].orderBookItems.[0].sumAmount").assertValue(json, 10);
    }

    @Test
    @Ignore
    public void subscribeTradeOrdersDetailed() {
        when(orderService.getOpenOrdersForWs(anyString()))
                .thenReturn(Collections.singletonList(new OrdersListWrapper(getMockOrderListDto(), "SELL", 1)));

        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        headers.setSubscriptionId("0");
        headers.setDestination("/orders/sfwfrf442fewdf/detailed/btc_usd");
        headers.setSessionId("0");
        headers.setSessionAttributes(new HashMap<>());
        Message<byte[]> message = MessageBuilder.withPayload(new byte[0]).setHeaders(headers).build();

        this.annotationMethodHandler.handleMessage(message);

        assertEquals(1, this.clientOutboundChannel.getMessages().size());
        Message<?> reply = this.clientOutboundChannel.getMessages().get(0);

        StompHeaderAccessor replyHeaders = StompHeaderAccessor.wrap(reply);
        assertEquals("0", replyHeaders.getSessionId());
        assertEquals("0", replyHeaders.getSubscriptionId());
        assertEquals("/orders/sfwfrf442fewdf/detailed/btc_usd", replyHeaders.getDestination());

        String json = new String((byte[]) reply.getPayload(), Charset.forName("UTF-8"));
        new JsonPathExpectationsHelper("$.[0].data.id").assertValue(json, 100);
        new JsonPathExpectationsHelper("$.[0].data.userId").assertValue(json, 200);
        new JsonPathExpectationsHelper("$.[0].data.orderType").assertValue(json, "BUY");
        new JsonPathExpectationsHelper("$.[0].data.exrate").assertValue(json, "TEST_EXRATE");
        new JsonPathExpectationsHelper("$.[0].data.amountBase").assertValue(json, "TEST_AMOUNT_BASE");
        new JsonPathExpectationsHelper("$.[0].data.amountConvert").assertValue(json, "TEST_AMOUNT_CONVERT");
        new JsonPathExpectationsHelper("$.[0].data.ordersIds").assertValue(json, "TEST_ORDERS_IDS");
        new JsonPathExpectationsHelper("$.[0].type").assertValue(json, "SELL");
    }

    @Test
    @Ignore
    public void subscribeMyTradeOrdersDetailed() {
        when(orderService.getMyOpenOrdersForWs(anyString(), anyString()))
                .thenReturn(Collections.singletonList(new OrdersListWrapper(getMockOrderListDto(), "SELL", 1)));

        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        headers.setSubscriptionId("0");
        headers.setDestination("/queue/my_orders/btc_usd");
        headers.setSessionId("0");
        headers.setUser(new TestPrincipal("Andrew"));
        headers.setSessionAttributes(new HashMap<>());
        Message<byte[]> message = MessageBuilder.withPayload(new byte[0]).setHeaders(headers).build();

        this.annotationMethodHandler.handleMessage(message);

        assertEquals(1, this.clientOutboundChannel.getMessages().size());
        Message<?> reply = this.clientOutboundChannel.getMessages().get(0);

        StompHeaderAccessor replyHeaders = StompHeaderAccessor.wrap(reply);
        assertEquals("0", replyHeaders.getSessionId());
        assertEquals("0", replyHeaders.getSubscriptionId());
        assertEquals("/queue/my_orders/btc_usd", replyHeaders.getDestination());

        String json = new String((byte[]) reply.getPayload(), Charset.forName("UTF-8"));
        new JsonPathExpectationsHelper("$.[0].data.id").assertValue(json, 100);
        new JsonPathExpectationsHelper("$.[0].data.userId").assertValue(json, 200);
        new JsonPathExpectationsHelper("$.[0].data.orderType").assertValue(json, "BUY");
        new JsonPathExpectationsHelper("$.[0].data.exrate").assertValue(json, "TEST_EXRATE");
        new JsonPathExpectationsHelper("$.[0].data.amountBase").assertValue(json, "TEST_AMOUNT_BASE");
        new JsonPathExpectationsHelper("$.[0].data.amountConvert").assertValue(json, "TEST_AMOUNT_CONVERT");
        new JsonPathExpectationsHelper("$.[0].data.ordersIds").assertValue(json, "TEST_ORDERS_IDS");
        new JsonPathExpectationsHelper("$.[0].type").assertValue(json, "SELL");
    }

    @Test
    @Ignore
    public void subscribeToUserPersonalMessages() {
        when(redisUserNotificationService.findAllByUser(anyString())).thenReturn(getTestUserMessages("test@test.com"));
        when(userService.getEmailByPubId(anyString())).thenReturn("test@test.com");

        StompHeaderAccessor headers = StompHeaderAccessor.create(StompCommand.SUBSCRIBE);
        headers.setSubscriptionId("0");
        headers.setDestination("/message/private/123456");
        headers.setSessionId("0");
        headers.setUser(new TestPrincipal("test@test.com"));
        headers.setSessionAttributes(new HashMap<>());
        Message<byte[]> message = MessageBuilder.withPayload(new byte[0]).setHeaders(headers).build();

        this.annotationMethodHandler.handleMessage(message);

        assertEquals(1, this.clientOutboundChannel.getMessages().size());
        Message<?> reply = this.clientOutboundChannel.getMessages().get(0);

        String json = new String((byte[]) reply.getPayload(), Charset.forName("UTF-8"));
        new JsonPathExpectationsHelper("$.message[0].text").assertValue(json, "message #1");
        new JsonPathExpectationsHelper("$.message[1].text").assertValue(json, "message #2");
        new JsonPathExpectationsHelper("$.message[2].text").assertValue(json, "message #3");
    }

    private List<UserNotificationMessage> getTestUserMessages(String key) {
        List<UserNotificationMessage> messages = Lists.newArrayList();
        IntStream.range(1, 5).forEach(i -> messages.add(new UserNotificationMessage(key, WsSourceTypeEnum.IEO, UserNotificationType.SUCCESS, "message #" + i, false)));
        return messages;
    }

    private static class TestAnnotationMethodHandler extends SimpAnnotationMethodMessageHandler {
        TestAnnotationMethodHandler(
                SubscribableChannel inChannel,
                MessageChannel outChannel,
                SimpMessageSendingOperations brokerTemplate
        ) {
            super(inChannel, outChannel, brokerTemplate);
        }

        void registerHandler(Object handler) {
            super.detectHandlerMethods(handler);
        }
    }

    private static class TestMessageChannel extends AbstractSubscribableChannel {
        private final List<Message<?>> messages = new ArrayList<>();

        List<Message<?>> getMessages() {
            return this.messages;
        }

        @Override
        protected boolean sendInternal(Message<?> message, long timeout) {
            this.messages.add(message);
            return true;
        }
    }

    private static class TestPrincipal implements Principal {
        private final String name;

        TestPrincipal(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }
    }

    private List<String> getMockLocalesList() {
        return new ArrayList<String>() {{
            add("EN");
            add("RU");
            add("CN");
            add("ID");
            add("AR");
        }};
    }

    private CurrencyPair getMockCurrencyPair() {
        CurrencyPair currencyPair = new CurrencyPair();
        currencyPair.setId(100);
        currencyPair.setName("TEST_NAME");
        currencyPair.setCurrency1(new Currency(4, "BTC", "Currency1", Boolean.FALSE));
        currencyPair.setCurrency2(new Currency(2, "USD", "Currency2", Boolean.FALSE));
        currencyPair.setMarket("TEST_MARKET");
        currencyPair.setMarketName("TEST_MARKET_NAME");
        currencyPair.setPairType(CurrencyPairType.MAIN);
        currencyPair.setHidden(Boolean.FALSE);
        currencyPair.setPermittedLink(Boolean.TRUE);

        return currencyPair;
    }

    private OrderListDto getMockOrderListDto() {
        OrderListDto orderListDto = new OrderListDto();
        orderListDto.setId(100);
        orderListDto.setUserId(200);
        orderListDto.setOrderType(OperationType.BUY);
        orderListDto.setExrate("TEST_EXRATE");
        orderListDto.setAmountBase("TEST_AMOUNT_BASE");
        orderListDto.setAmountConvert("TEST_AMOUNT_CONVERT");
        orderListDto.setOrdersIds("TEST_ORDERS_IDS");
        orderListDto.setCreated(LocalDateTime.of(2019, 4, 2, 16, 0, 1));
        orderListDto.setAccepted(LocalDateTime.of(2019, 4, 2, 16, 20, 1));
        orderListDto.setOrderSourceId(300);
        return orderListDto;
    }

    private OrderBookWrapperDto getMockOrderBookWrapperDto(OrderType orderType) {
        OrderBookWrapperDto orderBookWrapperDto = OrderBookWrapperDto.builder().build();
        orderBookWrapperDto.setOrderType(orderType);
        orderBookWrapperDto.setLastExrate("0.00000004");
        orderBookWrapperDto.setPreLastExrate("0.00000001");
        orderBookWrapperDto.setPositive(Boolean.TRUE);
        orderBookWrapperDto.setTotal(BigDecimal.TEN);
        orderBookWrapperDto.setOrderBookItems(getMockSimpleOrderBookItem(orderType));

        return orderBookWrapperDto;
    }

    private List<SimpleOrderBookItem> getMockSimpleOrderBookItem(OrderType orderType) {
        SimpleOrderBookItem dto1 = SimpleOrderBookItem.builder().build();
        dto1.setCurrencyPairId(1);
        dto1.setOrderType(orderType);
        dto1.setCurrencyPairName("BTC/USD");
        dto1.setExrate(BigDecimal.TEN);
        dto1.setAmount(BigDecimal.ONE);
        dto1.setTotal(BigDecimal.ZERO);
        dto1.setSumAmount(BigDecimal.TEN);

        SimpleOrderBookItem dto2 = SimpleOrderBookItem.builder().build();
        dto2.setCurrencyPairId(2);
        dto2.setOrderType(orderType);
        dto2.setCurrencyPairName("BTC/USD");
        dto2.setExrate(BigDecimal.TEN);
        dto2.setAmount(BigDecimal.ONE);
        dto2.setTotal(BigDecimal.ZERO);
        dto2.setSumAmount(BigDecimal.TEN);

        return Arrays.asList(dto1, dto2);
    }
}
