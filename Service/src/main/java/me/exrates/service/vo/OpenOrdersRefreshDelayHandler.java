package me.exrates.service.vo;

import lombok.extern.log4j.Log4j2;
import me.exrates.service.stomp.StompMessenger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

@Log4j2
@Component
public class OpenOrdersRefreshDelayHandler implements PersonalOrderRefreshDelayHandler {

    private Map<String, Map<Integer, Semaphore>> locksMap = new ConcurrentHashMap<>();

    private final Object pairsLock = new Object();
    private final Object usersLock = new Object();
    private final StompMessenger stompMessenger;

    private static final int LATENCY = 1000;

    @Autowired
    public OpenOrdersRefreshDelayHandler(StompMessenger stompMessenger) {
        this.stompMessenger = stompMessenger;
    }

    @Async
    @Override
    public void onEvent(int userId, String currencyPairName) {
        Semaphore semaphore = safeGetSemaphore(userId, currencyPairName);
        if (semaphore.tryAcquire()) {
            try {
                Thread.sleep(LATENCY);
                stompMessenger.sendPersonalOpenOrdersToUser(userId, currencyPairName);
            } catch (InterruptedException e) {
                log.error("interrupted ", e);
            } finally {
                semaphore.release();
            }
        }
    }

    private Semaphore safeGetSemaphore(Integer userId, String currencyPairName) {
        Map<Integer, Semaphore> mapByUsers;
        if ((mapByUsers = locksMap.get(currencyPairName)) == null) {
            synchronized (pairsLock) {
                WeakHashMap<Integer,Semaphore> weakHashMap = new WeakHashMap<>();
                weakHashMap.put(userId, new Semaphore(1));
                mapByUsers = locksMap.computeIfAbsent(currencyPairName, (k) -> weakHashMap);
            }
        }
        Semaphore semaphore;
        if ((semaphore = mapByUsers.get(userId)) == null) {
            synchronized (usersLock) {
                semaphore = mapByUsers.computeIfAbsent(userId, (k) -> new Semaphore(1));
            }
        }
        return semaphore;
    }
}
