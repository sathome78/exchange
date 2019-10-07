package me.exrates.service.handler;

import com.antkorwin.xsync.XSync;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.ExOrder;
import me.exrates.model.OrderWsDetailDto;
import me.exrates.model.dto.CallBackLogDto;
import me.exrates.model.dto.ExOrderWrapperDTO;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderEventEnum;
import me.exrates.service.CurrencyService;
import me.exrates.service.OrderService;
import me.exrates.service.RabbitMqService;
import me.exrates.service.UserService;
import me.exrates.service.cache.ExchangeRatesHolder;
import me.exrates.service.events.AcceptOrderEvent;
import me.exrates.service.events.CancelOrderEvent;
import me.exrates.service.events.CreateOrderEvent;
import me.exrates.service.events.OrderEvent;
import me.exrates.service.events.PartiallyAcceptedOrder;
import me.exrates.service.stomp.StompMessenger;
import me.exrates.service.vo.CurrencyStatisticsHandler;
import me.exrates.service.vo.MyTradesHandler;
import me.exrates.service.vo.OrdersEventsHandler;
import me.exrates.service.vo.PersonalOrderRefreshDelayHandler;
import me.exrates.service.vo.TradesEventsHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Maks on 28.08.2017.
 */
@Log4j2
@Component
@PropertySource(value = {"classpath:/job.properties", "classpath:/angular.properties"})
public class OrdersEventHandleService {

    @Value("${dev.mode}")
    private boolean DEV_MODE;

    @Autowired
    private ExchangeRatesHolder ratesHolder;
    @Autowired
    private CurrencyStatisticsHandler currencyStatisticsHandler;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private StompMessenger stompMessenger;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private RabbitMqService rabbitMqService;
    @Autowired
    private PersonalOrderRefreshDelayHandler openOrdersRefreshHandler;

    private final Object handlerSync = new Object();

    private Map<Integer, OrdersEventsHandler> mapSell = new ConcurrentHashMap<>();
    private Map<Integer, OrdersEventsHandler> mapBuy = new ConcurrentHashMap<>();

    private Map<Integer, TradesEventsHandler> mapTrades = new ConcurrentHashMap<>();
    private Map<Integer, MyTradesHandler> mapMyTrades = new ConcurrentHashMap<>();
    private Map<Integer, OrdersReFreshHandler> mapOrders = new ConcurrentHashMap<>();

    private Map<Integer, UserPersonalOrdersHandler> personalOrdersHandlerMap = new ConcurrentHashMap<>();

    private final static ExecutorService handlersExecutors = Executors.newCachedThreadPool();
    private XSync<Integer> dealsSync = new XSync<>();


    @Async
    @TransactionalEventListener
    public void handleOrderEventAsync(CreateOrderEvent event) {
        ExOrder exOrder = (ExOrder) event.getSource();
        onOrdersEvent(exOrder.getCurrencyPairId(), exOrder.getOperationType());
    }

    @Async
    @TransactionalEventListener
    public void handleOrderEventAsync(CancelOrderEvent event) {
        ExOrder exOrder = (ExOrder) event.getSource();
        onOrdersEvent(exOrder.getCurrencyPairId(), exOrder.getOperationType());
    }

    @Async
    @TransactionalEventListener
    public void handleOrderEventAsync(OrderEvent event) throws JsonProcessingException {
        ExOrder exOrder = (ExOrder) event.getSource();

        if (!(event instanceof PartiallyAcceptedOrder)) {
            CompletableFuture.runAsync(() -> handleOrdersDetailed(exOrder, event.getOrderEventEnum()), handlersExecutors);
        }

        CompletableFuture.runAsync(() -> {
            openOrdersRefreshHandler.onEvent(exOrder.getUserId(), exOrder.getCurrencyPair().getName());
            sendOrderEventNotification(exOrder);
        }, handlersExecutors);

        if (!DEV_MODE) {
            handleCallBack(event);
            if (exOrder.getUserAcceptorId() != 0) {
                handleAcceptorUserId(exOrder);
            }
        }
    }

    private void handleAcceptorUserId(ExOrder exOrder) {
        String url = userService.getCallBackUrlByUserAcceptorId(exOrder.getUserAcceptorId(), exOrder.getCurrencyPairId());
        try {
            CallBackLogDto callBackLogDto = makeCallBackForAcceptor(exOrder, url, exOrder.getUserAcceptorId());
            orderService.logCallBackData(callBackLogDto);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Async
    @TransactionalEventListener
    public void handleOrderEventAsync(AcceptOrderEvent event) {
        ExOrder order = (ExOrder) event.getSource();
        dealsSync.execute(order.getCurrencyPairId(), () -> {
            List<CompletableFuture<Void>> completableFutures = new ArrayList<>();
            log.info("order accepted " + order.getCurrencyPair().getName());
            completableFutures.add(CompletableFuture.runAsync(() -> rabbitMqService.sendTradeInfo(order), handlersExecutors));
            completableFutures.add(CompletableFuture.runAsync(() -> handleAllTrades(order), handlersExecutors));
            completableFutures.add(CompletableFuture.runAsync(() -> handleMyTrades(order), handlersExecutors));
            completableFutures.add(CompletableFuture.runAsync(() -> onOrdersEvent(order.getCurrencyPairId(), order.getOperationType()), handlersExecutors));
            completableFutures.add(CompletableFuture.runAsync(() -> {
                ratesHolder.onRatesChange(order);
                currencyStatisticsHandler.onEvent(order.getCurrencyPairId());
            }, handlersExecutors));
            try {
                CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[0]))
                        .exceptionally(ex -> null)
                        .get(5, TimeUnit.SECONDS);
            } catch (Exception e) {
                ExceptionUtils.printRootCauseStackTrace(e);
            }
        });
    }

    private void handleCallBack(OrderEvent event) throws JsonProcessingException {
        //TODO check if user have TRADER authority, use userHasAuthority method in this case
        ExOrder source = (ExOrder) event.getSource();
        int userId = source.getUserId();
        String url = userService.getCallBackUrlById(userId, source.getCurrencyPairId());

        processCallBackUrl(event, userId, url);
    }

    private void processCallBackUrl(OrderEvent event, int userId, String url) throws JsonProcessingException {
        if (!StringUtils.isEmpty(url)) {
            CallBackLogDto callBackLogDto = makeCallBack((ExOrder) event.getSource(), url, userId);
            orderService.logCallBackData(callBackLogDto);
            log.debug("*** Callback. User userId:" + userId + " | Callback:" + callBackLogDto);
        } else {
            log.debug("*** Callback url wasn't set. User userId:" + userId);
        }
    }


    private CallBackLogDto makeCallBack(ExOrder order, String url, int userId) throws JsonProcessingException {
        CallBackLogDto callbackLog = new CallBackLogDto();
        callbackLog.setRequestJson(new ObjectMapper().writeValueAsString(order));
        callbackLog.setRequestDate(LocalDateTime.now());
        callbackLog.setUserId(userId);

        ResponseEntity<String> responseEntity;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(callbackLog.getRequestJson(), headers);

            responseEntity = restTemplate.postForEntity(url, entity, String.class);
        } catch (Exception e) {
            e.printStackTrace();
            callbackLog.setResponseCode(999);
            callbackLog.setResponseJson(e.getMessage());
            callbackLog.setResponseDate(LocalDateTime.now());
            return callbackLog;
        }
        callbackLog.setResponseCode(responseEntity.getStatusCodeValue());
        callbackLog.setResponseJson(responseEntity.getBody());
        callbackLog.setResponseDate(LocalDateTime.now());
        return callbackLog;
    }

    private CallBackLogDto makeCallBackForAcceptor(ExOrder order, String url, int id) throws JsonProcessingException {
        CallBackLogDto callbackLog = new CallBackLogDto();

        callbackLog.setRequestJson(new ObjectMapper().
                writeValueAsString(
                        ExOrderWrapperDTO.
                                builder()
                                .exOrder(order)
                                .message("Your order has been processed")
                                .userId(id)
                                .build()))
        ;

        callbackLog.setRequestDate(LocalDateTime.now());
        callbackLog.setUserId(id);

        ResponseEntity<String> responseEntity;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(callbackLog.getRequestJson(), headers);

            responseEntity = restTemplate.postForEntity(url, entity, String.class);
        } catch (Exception e) {
            e.printStackTrace();
            callbackLog.setResponseCode(999);
            callbackLog.setResponseJson(e.getMessage());
            callbackLog.setResponseDate(LocalDateTime.now());
            return callbackLog;
        }
        callbackLog.setResponseCode(responseEntity.getStatusCodeValue());
        callbackLog.setResponseJson(responseEntity.getBody());
        callbackLog.setResponseDate(LocalDateTime.now());
        return callbackLog;
    }

    private void onOrdersEvent(Integer pairId, OperationType operationType) {
        Map<Integer, OrdersEventsHandler> mapForWork;
        if (operationType.equals(OperationType.BUY)) {
            mapForWork = mapBuy;
        } else if (operationType.equals(OperationType.SELL)) {
            mapForWork = mapSell;
        } else {
            log.error("no such map");
            return;
        }
        OrdersEventsHandler handler = mapForWork
                .computeIfAbsent(pairId, k -> OrdersEventsHandler.init(pairId, operationType));
        handler.onOrderEvent();
    }

    void handleOrdersDetailed(ExOrder exOrder, OrderEventEnum orderEvent) {
        try {
            String pairName = currencyService.findCurrencyPairById(exOrder.getCurrencyPairId()).getName().replace("/", "_").toLowerCase();
            OrdersReFreshHandler handler = mapOrders
                    .computeIfAbsent(exOrder.getCurrencyPairId(), k -> new OrdersReFreshHandler(stompMessenger, objectMapper, pairName));
            handler.addOrderToQueue(new OrderWsDetailDto(exOrder, orderEvent));
        } catch (Exception e) {
            log.error("error handleOrdersDetailed() {}", e);
        }
    }

    private Runnable sendOrderEventNotification(ExOrder exOrder) {
        return () -> {
            String pairName = exOrder.getCurrencyPair().getName();
            String creatorEmail = userService.findEmailById(exOrder.getUserId());
            stompMessenger.updateUserOpenOrders(pairName, creatorEmail);

            if (exOrder.getUserAcceptorId() > 0
                    && exOrder.getUserAcceptorId() != exOrder.getUserId()) {
                String acceptorEmail = userService.findEmailById(exOrder.getUserAcceptorId());
                stompMessenger.updateUserOpenOrders(pairName, acceptorEmail);
            }
        };
    }

    private void handleAllTrades(ExOrder exOrder) {
        TradesEventsHandler handler = mapTrades
                .computeIfAbsent(exOrder.getCurrencyPairId(), k -> TradesEventsHandler.init(exOrder.getCurrencyPairId()));
        handler.onAcceptOrderEvent();
    }


    private void handleMyTrades(ExOrder exOrder) {
        MyTradesHandler handler = mapMyTrades
                .computeIfAbsent(exOrder.getCurrencyPairId(), k -> MyTradesHandler.init(exOrder.getCurrencyPairId()));
        handler.onAcceptOrderEvent(exOrder.getUserId());
        handler.onAcceptOrderEvent(exOrder.getUserAcceptorId());
    }
}
