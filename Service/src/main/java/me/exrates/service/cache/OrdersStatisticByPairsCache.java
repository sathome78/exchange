package me.exrates.service.cache;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.OrderDao;
import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Maks on 23.01.2018.
 */
@Log4j2(topic = "cache")
@Component
public class OrdersStatisticByPairsCache {

    @Autowired
    private OrderDao orderDao;

    private List<ExOrderStatisticsShortByPairsDto> cachedList = new CopyOnWriteArrayList<>();

    private Semaphore semaphore = new Semaphore(1, true);

    private AtomicBoolean needUpdate = new AtomicBoolean(true);

    @PostConstruct
    private void init() {
        update();
        needUpdate.set(false);
        log.info("initialized, {}", cachedList.size());
    }


    public void update() {
        log.info("try update cache");
        if (semaphore.tryAcquire()) {
            try {
                log.info("update cache");
                this.cachedList = orderDao.getOrderStatisticByPairs();
                log.info("end update cache");
                needUpdate.set(false);
            } finally {
                semaphore.release();
            }
        }
    }

    public List<ExOrderStatisticsShortByPairsDto> getCachedList() {
        log.info("get cache");
        if (needUpdate.get()) {
            if (semaphore.availablePermits() > 0) {
                update();
            } else {
                LocalTime startTime = LocalTime.now();
                while (needUpdate.get()) {
                    if (startTime.until(LocalDateTime.now(),  ChronoUnit.SECONDS) > 10) {
                        break;
                    }
                }
            }
        }
        log.info("return cache");
        return cachedList;
    }

    public void setNeedUpdate(boolean newNeedUpdate) {
        needUpdate.set(newNeedUpdate);
    }
}
