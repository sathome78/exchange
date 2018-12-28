package me.exrates.service.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.ExOrder;
import me.exrates.model.dto.CallBackLogDto;
import me.exrates.model.dto.InputCreateOrderDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;
import me.exrates.service.OrderService;
import me.exrates.service.RabbitMqService;
import me.exrates.service.UserService;
import me.exrates.service.cache.ExchangeRatesHolder;
import me.exrates.service.events.*;
import me.exrates.service.vo.*;
import org.apache.http.entity.ContentType;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Maks on 28.08.2017.
 */
@Log4j2
@Component
@PropertySource(value = {"classpath:/job.properties"})
public class OrdersEventHandleService {

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
    private RabbitMqService rabbitMqService;


    private Map<Integer, OrdersEventsHandler> mapSell = new ConcurrentHashMap<>();
    private Map<Integer, OrdersEventsHandler> mapBuy = new ConcurrentHashMap<>();

    private Map<Integer, TradesEventsHandler> mapTrades = new ConcurrentHashMap<>();
    private Map<Integer, MyTradesHandler> mapMyTrades = new ConcurrentHashMap<>();
    private Map<Integer, ChartRefreshHandler> mapChart = new ConcurrentHashMap<>();

    public void handleOrderEventOnMessage(InputCreateOrderDto orderDto) {
        ExOrder order = orderDto.toExorder();
        onOrdersEvent(order.getCurrencyPairId(), order.getOperationType());
        handleAllTrades(order);
        handleMyTrades(order);
        handleChart(order);
        ratesHolder.onRatesChange(order);
        currencyStatisticsHandler.onEvent(order.getCurrencyPairId());
    }


    @Async
    @TransactionalEventListener
    public void handleOrderEventAsync(CreateOrderEvent event) {
        ExOrder exOrder = (ExOrder) event.getSource();
        InputCreateOrderDto inputCreateOrderDto = InputCreateOrderDto.of(exOrder);
        rabbitMqService.sendOrderInfo(inputCreateOrderDto, RabbitMqService.ANGULAR_QUEUE);
    }

    @Async
    @TransactionalEventListener
    public void handleOrderEventAsync(CancelOrderEvent event) throws JsonProcessingException {
        ExOrder exOrder = (ExOrder) event.getSource();
        InputCreateOrderDto inputCreateOrderDto = InputCreateOrderDto.of(exOrder);
        rabbitMqService.sendOrderInfo(inputCreateOrderDto, RabbitMqService.ANGULAR_QUEUE);
    }


    @Async
    @TransactionalEventListener
    public void handleOrderEventAsync(OrderEvent event) throws JsonProcessingException {
        ExOrder exOrder = (ExOrder) event.getSource();
        log.debug("order event {} ", exOrder);
        onOrdersEvent(exOrder.getCurrencyPairId(), exOrder.getOperationType());
        handleCallBack(event);
    }

    @Async
    @TransactionalEventListener
    public void handleOrderEventAsync(AcceptOrderEvent event) {
        log.debug("new thr accept {} ", Thread.currentThread().getName());
        ExOrder order = (ExOrder) event.getSource();
        handleAllTrades(order);
        handleMyTrades(order);
        handleChart(order);
        ratesHolder.onRatesChange(order);
        currencyStatisticsHandler.onEvent(order.getCurrencyPairId());
        InputCreateOrderDto inputCreateOrderDto = InputCreateOrderDto.of(order);
        rabbitMqService.sendOrderInfo(inputCreateOrderDto, RabbitMqService.ANGULAR_QUEUE);
    }

    private void handleCallBack(OrderEvent event) throws JsonProcessingException {
        //TODO check if user have TRADER authority, use userHasAuthority method in this case
        ExOrder source = (ExOrder) event.getSource();
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String url = userService.getCallBackUrlByEmail(email,source.getCurrencyPairId());

        processCallBackUrl(event, email, url);
    }

    private void processCallBackUrl(OrderEvent event, String email, String url) throws JsonProcessingException {
        if (url != null) {
            CallBackLogDto callBackLogDto = makeCallBack((ExOrder) event.getSource(), url, email);
            orderService.logCallBackData(callBackLogDto);
            log.info("*** Callback. User email:" + email + " | Callback:" + callBackLogDto);
        } else {
            log.info("*** Callback url wasn't set. User email:" + email);
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

    private CallBackLogDto makeCallBack(ExOrder order, String url, String email) throws JsonProcessingException {
        CallBackLogDto callbackLog = new CallBackLogDto();
        callbackLog.setRequestJson(new ObjectMapper().writeValueAsString(order));
        callbackLog.setRequestDate(LocalDateTime.now());
        callbackLog.setUserId(userService.getIdByEmail(email));

        ResponseEntity<String> responseEntity;
        try{
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(callbackLog.getRequestJson(), headers);

            responseEntity = restTemplate.postForEntity(url, entity, String.class);
        } catch (Exception e){
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

    @Async
    void handleChart(ExOrder exOrder) {
        ChartRefreshHandler handler = mapChart
                .computeIfAbsent(exOrder.getCurrencyPairId(), k -> ChartRefreshHandler.init(exOrder.getCurrencyPairId()));
        handler.onAcceptOrderEvent();
    }

}
