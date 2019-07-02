package me.exrates.service.vo;


import com.google.common.collect.Sets;
import lombok.extern.log4j.Log4j2;
import me.exrates.service.messaging.StompMessenger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.Semaphore;

/**
 * Created by Maks on 07.09.2017.
 */
@Log4j2
@Component
public class CurrencyStatisticsHandler {

    @Autowired
    private StompMessenger stompMessenger;

    private Set<Integer> currenciesSet = Sets.newConcurrentHashSet();

    private final Semaphore semaphoreMain = new Semaphore(1, true);
    private static final long DELAY = 1000;
    private final Object syncObj = new Object();

    @Async
    public void onEvent(int pairId) {
        synchronized (syncObj) {
            currenciesSet.add(pairId);
        }
        if (semaphoreMain.tryAcquire()) {
            try {
                Thread.sleep(DELAY);
                Set<Integer> forUpdate;
                synchronized (syncObj) {
                    forUpdate = Sets.newHashSet(currenciesSet);
                    currenciesSet.clear();
                }
                if (!forUpdate.isEmpty()) {
                    stompMessenger.sendStatisticMessage(forUpdate);
                }
            } catch (Exception e) {
                log.error(e);
            } finally {
                semaphoreMain.release();
            }
        }
    }

}
