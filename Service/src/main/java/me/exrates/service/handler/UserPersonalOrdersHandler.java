package me.exrates.service.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.OrderWsDetailDto;
import me.exrates.model.SynchronizersObject;
import me.exrates.service.stomp.StompMessenger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

@Log4j2
public class UserPersonalOrdersHandler {

    private Map<Integer, List<OrderWsDetailDto>> orders = new ConcurrentHashMap<>();
    private Map<Integer, SynchronizersObject> synchronizersMap = new ConcurrentHashMap<>();


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
        checkAndCreateSynchronizers(userId);

        synchronized (synchronizersMap.get(userId).getObjectSync()) {
            List<OrderWsDetailDto> value = orders.get(userId);
            if (value == null) {
                value = new CopyOnWriteArrayList<>();
                value.add(dto);
                orders.put(userId, value);
            }
        }
        orders.get(userId).add(dto);
        send(userId);
    }

    @Synchronized
    private void checkAndCreateSynchronizers(Integer userId) {
        synchronizersMap.putIfAbsent(userId, SynchronizersObject.init());
    }




    private void send(Integer userId) {
        //достаю синхронизатор
        //запускаю первый поток и закрываю путь потокам
        //остальные потоки отсекаю
        if (synchronizersMap.get(userId).getSemaphore().tryAcquire()) {
            try {
                //sleep потока
                TimeUnit.MILLISECONDS.sleep(refreshTime);
                //блокирую дальнейшую запись в лист
                synchronizersMap.get(userId).getLocker().lock();
                //делаю send
                List<OrderWsDetailDto> data = orders.get(userId);
                sendMessage(data, userId);
                //очищаю основной лист
                data.clear();
            } catch (InterruptedException e) {
                log.error(e);
            } finally {
                //открываю доступ потокам
                synchronizersMap.get(userId).getSemaphore().release();
                //открываю запись в лист
                synchronizersMap.get(userId).getLocker().unlock();
            }
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
