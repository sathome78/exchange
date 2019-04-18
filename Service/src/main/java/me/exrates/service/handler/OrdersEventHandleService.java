package me.exrates.service.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.ExOrder;
import me.exrates.model.OrderWsDetailDto;
import me.exrates.model.dto.CallBackLogDto;
import me.exrates.model.dto.ExOrderWrapperDTO;
import me.exrates.model.dto.InputCreateOrderDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderEventEnum;
import me.exrates.model.enums.OrderStatus;
import me.exrates.model.enums.UserRole;
import me.exrates.service.CurrencyService;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.cache.ExchangeRatesHolder;
import me.exrates.service.cache.currencyPairsInfo.CpStatisticsHolder;
import me.exrates.service.events.AcceptOrderEvent;
import me.exrates.service.events.CancelOrderEvent;
import me.exrates.service.events.CreateOrderEvent;
import me.exrates.service.events.EventsForDetailed.DetailOrderEvent;
import me.exrates.service.events.OrderEvent;
import me.exrates.service.events.PartiallyAcceptedOrder;
import me.exrates.service.stomp.StompMessenger;
import me.exrates.service.vo.ChartRefreshHandler;
import me.exrates.service.vo.CurrencyStatisticsHandler;
import me.exrates.service.vo.MyTradesHandler;
import me.exrates.service.vo.OrdersEventsHandler;
import me.exrates.service.vo.TradesEventsHandler;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.messaging.DefaultSimpUserRegistry;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

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
    private DefaultSimpUserRegistry registry;
    @Autowired
    private CurrencyService currencyService;

    private final Object handlerSync = new Object();

    private Map<Integer, OrdersEventsHandler> mapSell = new ConcurrentHashMap<>();
    private Map<Integer, OrdersEventsHandler> mapBuy = new ConcurrentHashMap<>();

    private Map<Integer, TradesEventsHandler> mapTrades = new ConcurrentHashMap<>();
    private Map<Integer, MyTradesHandler> mapMyTrades = new ConcurrentHashMap<>();
    private Map<Integer, ChartRefreshHandler> mapChart = new ConcurrentHashMap<>();
    private Map<Integer, OrdersReFreshHandler> mapOrders = new ConcurrentHashMap<>();

    private Map<Integer, UserPersonalOrdersHandler> personalOrdersHandlerMap = new ConcurrentHashMap<>();

    public void handleOrderEventOnMessage(InputCreateOrderDto orderDto) {
//        ExOrder order = orderDto.toExorder();
//        onOrdersEvent(order.getCurrencyPairId(), order.getOperationType());
//        handleAllTrades(order);
//        handleMyTrades(order);
//        handleChart(order);
//        ratesHolder.onRatesChange(order);
//        currencyStatisticsHandler.onEvent(order.getCurrencyPairId());
    }


    @Async
    @TransactionalEventListener
    public void handleOrderEventAsync(CreateOrderEvent event) {
        ExOrder exOrder = (ExOrder) event.getSource();
        onOrdersEvent(exOrder.getCurrencyPairId(), exOrder.getOperationType());
    }

    @Async
    @TransactionalEventListener
    public void handleOrderEventAsync(CancelOrderEvent event) throws JsonProcessingException {
        ExOrder exOrder = (ExOrder) event.getSource();
        onOrdersEvent(exOrder.getCurrencyPairId(), exOrder.getOperationType());
    }


    @Async
    @TransactionalEventListener
    public void handleOrderEventAsync(OrderEvent event) throws JsonProcessingException {
        ExOrder exOrder = (ExOrder) event.getSource();
        if (!(event instanceof PartiallyAcceptedOrder)) {
            handleOrdersDetailed(exOrder, event.getOrderEventEnum());
        }
        if (!DEV_MODE) {
            handleCallBack(event);
            if (exOrder.getUserAcceptorId() != 0) {
                handleAcceptorUserId(exOrder);
            }
        }
    }

    @Async
    @TransactionalEventListener
    public void handleOrderPersonalEventAsync(DetailOrderEvent event){
        try {
            List<ExOrder> orderList;
            if (event.getOrderEventEnum() == OrderEventEnum.AUTO_ACCEPT) {
                orderList = (List<ExOrder>) event.getSource();
            } else {
                orderList = Collections.singletonList(((ExOrder)event.getSource()));
            }
            handlePersonalOrders(orderList, event.getPairId());
        } catch (Exception e) {
            ExceptionUtils.printRootCauseStackTrace(e);
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
        handleAllTrades(order);
        handleMyTrades(order);
        ratesHolder.onRatesChange(order);
        currencyStatisticsHandler.onEvent(order.getCurrencyPairId());
        onOrdersEvent(order.getCurrencyPairId(), order.getOperationType());
        handleChart(order);
    }

    private void handleCallBack(OrderEvent event) throws JsonProcessingException {
        //TODO check if user have TRADER authority, use userHasAuthority method in this case
        ExOrder source = (ExOrder) event.getSource();
        int userId = source.getUserId();
        String url = userService.getCallBackUrlById(userId, source.getCurrencyPairId());

        processCallBackUrl(event, userId, url);
    }

    private void processCallBackUrl(OrderEvent event, int userId, String url) throws JsonProcessingException {
        if (url != null) {
            CallBackLogDto callBackLogDto = makeCallBack((ExOrder) event.getSource(), url, userId);
            orderService.logCallBackData(callBackLogDto);
            log.debug("*** Callback. User userId:" + userId + " | Callback:" + callBackLogDto);
        } else {
            log.debug("*** Callback url wasn't set. User userId:" + userId);
        }
    }

    private boolean userHasAuthority(UserRole authority) {
        List<GrantedAuthority> authorities = (List<GrantedAuthority>) SecurityContextHolder.getContext().getAuthentication().getAuthorities();

        for (GrantedAuthority grantedAuthority : authorities) {
            if (authority.toString().equals(grantedAuthority.getAuthority())) {
                return true;
            }
        }

        return false;
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

    @Async
    void handleOrdersDetailed(ExOrder exOrder, OrderEventEnum orderEvent) {
        try {
            String pairName = ratesHolder.getOne(exOrder.getCurrencyPairId()).getCurrencyPairName().replace("/", "_").toLowerCase();
            OrdersReFreshHandler handler = mapOrders
                    .computeIfAbsent(exOrder.getCurrencyPairId(), k -> new OrdersReFreshHandler(stompMessenger, objectMapper, pairName));
            handler.addOrderToQueue(new OrderWsDetailDto(exOrder, orderEvent));
        } catch (Exception e) {
            log.error("error handleOrdersDetailed() {}", e);
        }
    }


    private void handlePersonalOrders(List<ExOrder> orders, int pairId) {
        try {
            Map<Integer, List<OrderWsDetailDto>> byUserMap = new HashMap<>();
            orders.forEach(p-> {
                byUserMap.computeIfAbsent(p.getUserId(),  y -> new ArrayList<>()).add(new OrderWsDetailDto(p));
                if (p.getStatus() == OrderStatus.CLOSED && p.getUserId() != p.getUserAcceptorId()) {
                    byUserMap.computeIfAbsent(p.getUserAcceptorId(), y -> new ArrayList<>()).add(new OrderWsDetailDto(p));
                }
            });
            String pairName = currencyService.findCurrencyPairById(pairId).getName().replace("/", "_").toLowerCase();
            UserPersonalOrdersHandler handler = getHandlerSafe(pairId, pairName, stompMessenger);
            byUserMap.forEach((k,v)-> handler.sendInstant(v, k));
        } catch (Exception e) {
            ExceptionUtils.printRootCauseStackTrace(e);
        }
    }

    private UserPersonalOrdersHandler getHandlerSafe(int pairId, String pairName, StompMessenger stompMessenger) {
        if (!personalOrdersHandlerMap.containsKey(pairId)) {
            synchronized (handlerSync) {
                return personalOrdersHandlerMap
                        .computeIfAbsent(pairId, k -> new UserPersonalOrdersHandler(stompMessenger, objectMapper, pairName));
            }
        } else {
            return personalOrdersHandlerMap.get(pairId);
        }
    }

    @Async
    void handleAllTrades(ExOrder exOrder) {
        TradesEventsHandler handler = mapTrades
                .computeIfAbsent(exOrder.getCurrencyPairId(), k -> TradesEventsHandler.init(exOrder.getCurrencyPairId()));
        handler.onAcceptOrderEvent();
    }

    @Async
    void handleMyTrades(ExOrder exOrder) {
        MyTradesHandler handler = mapMyTrades
                .computeIfAbsent(exOrder.getCurrencyPairId(), k -> MyTradesHandler.init(exOrder.getCurrencyPairId()));
        handler.onAcceptOrderEvent(exOrder.getUserId());
        handler.onAcceptOrderEvent(exOrder.getUserAcceptorId());
    }


    private void handleChart(ExOrder exOrder) {
        try {
            ChartRefreshHandler handler = mapChart
                    .computeIfAbsent(exOrder.getCurrencyPairId(), k -> ChartRefreshHandler.init(exOrder.getCurrencyPairId()));
            handler.onAcceptOrderEvent();
        } catch (Exception e) {
            log.error(e);
        }
    }

}
