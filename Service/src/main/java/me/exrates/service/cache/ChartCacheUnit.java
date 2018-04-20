package me.exrates.service.cache;

import lombok.Data;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.chart.ChartTimeFrame;
import me.exrates.model.dto.CandleChartItemDto;
import me.exrates.model.vo.BackDealInterval;
import me.exrates.service.OrderService;
import me.exrates.service.events.ChartCacheUpdateEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Maks on 25.01.2018.
 */
@Log4j2(topic = "cache")
@Data
public class ChartCacheUnit implements ChartsCacheInterface {

    private final int DEFAULT_LAST_ITEMS_NUMBER = 5;

    /*fields*/
    private List<CandleChartItemDto> cachedData;
    private final Integer currencyPairId;
    private final ChartTimeFrame timeFrame;
    private final long minUpdateIntervalSeconds;
    private AtomicBoolean needToUpdate = new AtomicBoolean(true);
    private LocalDateTime lastUpdateDate;
    private OrderService orderService;
    private ApplicationEventPublisher eventPublisher;
    /*provide update only when user try to get data, or update imediately, when time for update remaining*/
    private final boolean lazyUpdate;

    /*synchronizers*/

    private ReentrantLock lock = new ReentrantLock();

    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private CyclicBarrier barrier = new CyclicBarrier(99999);

    private ReentrantLock timerLock = new ReentrantLock();

    /*constructor*/
    public ChartCacheUnit(Integer currencyPairId,
                          ChartTimeFrame timeFrame,
                          OrderService orderService,
                          ApplicationEventPublisher eventPublisher) {
        this.currencyPairId = currencyPairId;
        this.timeFrame = timeFrame;
        this.minUpdateIntervalSeconds = timeFrame.getTimeUnit().getChartRefreshInterval();
        this.lazyUpdate = timeFrame.getTimeUnit().isChartLazyUpdate();
        this.eventPublisher = eventPublisher;
        this.orderService = orderService;
        cachedData = null;
    }


    /*methods*/

    @Override
    public List<CandleChartItemDto> getData() {
        log.debug("getting data {} {}", currencyPairId, timeFrame);
        if (cachedData == null || isUpdateCasheRequired()) {
            log.debug("update data {} {}", currencyPairId, timeFrame);
            updateCache(cachedData != null );
        }
        log.debug("return data {} {}", currencyPairId, timeFrame);
        return cachedData;
    }

    private boolean isUpdateCasheRequired() {
        return needToUpdate.get() && isTimeForUpdate() && lazyUpdate;
    }

    @Override
    public List<CandleChartItemDto> getLastData() {
        List<CandleChartItemDto> data = getData();
        if (data.size() <= DEFAULT_LAST_ITEMS_NUMBER) {
            return data;
        }
        return data.subList(data.size() - DEFAULT_LAST_ITEMS_NUMBER, data.size());
    }

    private boolean isTimeForUpdate() {
        return lastUpdateDate == null || lastUpdateDate.plusSeconds(minUpdateIntervalSeconds).compareTo(LocalDateTime.now()) <= 0;
    }

    @Override
    public void setNeedToUpdate() {
        log.debug("setting update data {} {}", currencyPairId, timeFrame);
        if (!lazyUpdate) {
            log.debug("not lazy update data {} {}", currencyPairId, timeFrame);
            if (timerLock.tryLock()) {
                timerLock.lock();
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                            log.debug("execute task update data {} {}", currencyPairId, timeFrame);
                            updateCache(true);
                            eventPublisher.publishEvent(new ChartCacheUpdateEvent(getLastData(), timeFrame, currencyPairId));
                        if (timerLock.isLocked()) {
                            timerLock = new ReentrantLock();
                        }
                    }
                }, getMinUpdateIntervalSeconds() * 1000);

            }
        } else {
            needToUpdate.set(true);
        }
    }

    private void updateCache(boolean appendLastEntriesOnly) {
        log.debug("try update cahce {} {}", currencyPairId, timeFrame);
        if (!lock.isLocked()) {
            log.debug("lock was unlocked {} {}", currencyPairId, timeFrame);
            lock.lock();
            if (appendLastEntriesOnly && cachedData != null && !cachedData.isEmpty() ) {
                CandleChartItemDto lastBar = cachedData.remove(cachedData.size() - 1);
                LocalDateTime lastBarStartTime = lastBar.getBeginPeriod();
                List<CandleChartItemDto> newData = orderService.getLastDataForCandleChart(currencyPairId, lastBarStartTime, timeFrame.getResolution());
                cachedData.addAll(newData);
            } else {
                setCachedData(orderService.getDataForCandleChart(currencyPairId, timeFrame));
            }
            lastUpdateDate = LocalDateTime.now();
            needToUpdate.set(false);
            lock.unlock();
            /*countDownLatch.countDown();
            countDownLatch = new CountDownLatch(1);*/
            barrier.reset();
            log.debug("end update cache {} {}", currencyPairId, timeFrame);
        } else {
            log.debug("wait update data {} {}", currencyPairId, timeFrame);
            try {
                barrier.await(20, TimeUnit.SECONDS);
            } catch (Exception e) {
                log.warn(e);
            }
        }

    }

}
