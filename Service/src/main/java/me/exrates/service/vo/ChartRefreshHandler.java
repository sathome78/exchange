package me.exrates.service.vo;

import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;
import me.exrates.service.cache.ChartsCache;
import me.exrates.service.cache.ChartsCacheManager;
import me.exrates.service.stomp.StompMessenger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.util.concurrent.Semaphore;

/**
 * Created by Maks on 04.09.2017.
 */
@EqualsAndHashCode
@Log4j2
public class ChartRefreshHandler {
    @Autowired
    private ChartsCacheManager chartsCacheManager;
    @Autowired
    private StompMessenger stompMessenger;
    @Autowired
    private ChartsCache chartsCache;

    private int currencyPairId;

    private final Semaphore SEMAPHORE = new Semaphore(1, true);

    private static final int LATENCY = 2000;


    private ChartRefreshHandler(int currencyPairId) {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        this.currencyPairId = currencyPairId;
    }

    public static ChartRefreshHandler init(int currencyPairId) {
        return new ChartRefreshHandler(currencyPairId);
    }

    public void onAcceptOrderEvent() {
        if (SEMAPHORE.tryAcquire()) {
            try {
                Thread.sleep(LATENCY);
            } catch (InterruptedException e) {
                log.error("interrupted ", e);
            }
            chartsCacheManager.onUpdateEvent(currencyPairId);
            SEMAPHORE.release();
        }

    }
}
