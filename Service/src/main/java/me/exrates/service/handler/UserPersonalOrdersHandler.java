package me.exrates.service.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.OrderWsDetailDto;
import me.exrates.service.stomp.StompMessenger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;

@Log4j2
public class UserPersonalOrdersHandler {

    private Map<Integer, List<OrderWsDetailDto>> orders = new ConcurrentHashMap<>();
    private Map<Integer, Semaphore> synchronizersMap = new ConcurrentHashMap<>();

    private final StompMessenger stompMessenger;
    private final ObjectMapper objectMapper;
    private final Integer pairId;
    private final long refreshTime = 1000; /*in millis*/
    private Timer timer;

    public UserPersonalOrdersHandler(StompMessenger stompMessenger, ObjectMapper objectMapper, Integer pairId) {
        this.stompMessenger = stompMessenger;
        this.objectMapper = objectMapper;
        this.pairId = pairId;
        this.timer = new Timer();
    }

    void addOrderToQueue(OrderWsDetailDto dto, Integer userId) {
        synchronizersMap.putIfAbsent(userId, new Semaphore(1));
        if ((synchronizersMap.get(userId).tryAcquire())) {
            List<OrderWsDetailDto> value = orders.get(userId);
            if (value == null) {
                value = new CopyOnWriteArrayList<>();
            }
            value.add(dto);
            orders.put(userId, value);
            /*timer task here*/
            send(userId);
        }
    }

    private void send(Integer userId) {
        synchronized (synchronizersMap.get(userId)) {
            sendMessage(orders.get(userId), userId);
        }
    }

    void addOrderToQueueInstant(OrderWsDetailDto dto, Integer userId) {
        sendMessage(new ArrayList<OrderWsDetailDto>(){{add(dto);}}, userId);
    }

    private void sendMessage(List<OrderWsDetailDto> dtos, Integer userId) {
        try {
            stompMessenger.sendPersonalOpenOrdersAndDealsToUser(userId, pairId, objectMapper.writeValueAsString(dtos));
        } catch (Exception e) {
            log.error(e);
        }
    }



}
