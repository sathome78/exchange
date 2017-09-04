package me.exrates.service.vo;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.ExOrder;
import me.exrates.service.stomp.StompMessenger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.util.concurrent.Semaphore;

/**
 * Created by Maks on 04.09.2017.
 */
@Log4j2
public class TradesEventsHandler {

    @Autowired
    private StompMessenger stompMessenger;

    private int currencyPairId;

    private static final Semaphore SEMAPHORE = new Semaphore(1, true);

    private static final int LATENCY = 1200;


    private TradesEventsHandler(int currencyPairId) {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        this.currencyPairId = currencyPairId;
    }

    public static TradesEventsHandler init(int currencyPairId) {
        return null;
    }

    public void onAcceptOrderEvent() {
        if (SEMAPHORE.tryAcquire()) {
            try {
                Thread.sleep(LATENCY);
            } catch (InterruptedException e) {
                log.error("interrupted ", e);
            }
            SEMAPHORE.release();
            stompMessenger.sendAllTradesToUser(currencyPairId);
        }

    }
}
