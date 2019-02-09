package me.exrates.service.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.OrderWsDetailDto;
import me.exrates.service.stomp.StompMessenger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Log4j2
public class OrdersReFreshHandler {

    private final StompMessenger stompMessenger;
    private final ObjectMapper objectMapper;
    private final Integer pairId;
    private final Semaphore semaphore = new Semaphore(1);
    private final long refreshTime = 1000; /*in millis*/

    private List<OrderWsDetailDto> dtos = new CopyOnWriteArrayList<>();

    public OrdersReFreshHandler(StompMessenger stompMessenger, ObjectMapper objectMapper, Integer pairId) {
        this.stompMessenger = stompMessenger;
        this.objectMapper = objectMapper;
        this.pairId = pairId;
    }

    void addOrderToQueue(OrderWsDetailDto dto) {
        dtos.add(dto);
        check();
    }

    private void check() {
        if (semaphore.tryAcquire()) {
            try {
                TimeUnit.MILLISECONDS.sleep(refreshTime);
                sendMessage(dtos);
                dtos.clear();
            } catch (InterruptedException e) {
                log.error(e);
            } finally {
                semaphore.release();
            }
        }
    }

    @Synchronized
    private void sendMessage(List<OrderWsDetailDto> dtos) {
        try {
            stompMessenger.sendRefreshTradeOrdersDetailMessage(pairId, objectMapper.writeValueAsString(dtos));
        } catch (Exception e) {
            log.error(e);
        }
    }


}
