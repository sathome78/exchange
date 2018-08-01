package me.exrates.service.cache;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.CurrencyDao;
import me.exrates.dao.OrderDao;
import me.exrates.model.CurrencyPair;
import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
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
    @Autowired
    private CurrencyDao currencyDao;

    private List<CurrencyPair> allPairs = new ArrayList<>();

    private List<ExOrderStatisticsShortByPairsDto> allStatsCachedList = new CopyOnWriteArrayList<>();

    private List<ExOrderStatisticsShortByPairsDto> cachedList = new CopyOnWriteArrayList<>();

    private Semaphore semaphore = new Semaphore(1, true);

    private AtomicBoolean needUpdate = new AtomicBoolean(true);

    @PostConstruct
    private void init() {
        allPairs = currencyDao.getAllCurrencyPairs();
        update();
        needUpdate.set(false);
        log.info("initialized, {}", cachedList.size());
    }


    public void update() {
        log.info("try update cache");
        if (semaphore.tryAcquire()) {
            log.info("update cache");
            this.cachedList = orderDao.getOrderStatisticByPairs();
            this.updateAllPairs();
            needUpdate.set(false);
            semaphore.release();
        }
    }

    public List<ExOrderStatisticsShortByPairsDto> getAllPairsCahedList() {
        log.info("get cache");
        checkCache();
        return allStatsCachedList;
    }

    public List<ExOrderStatisticsShortByPairsDto> getCachedList() {
        log.info("get cache");
        checkCache();
        return cachedList;
    }

    public void setNeedUpdate(boolean newNeedUpdate) {
        needUpdate.set(newNeedUpdate);
    }

    private void checkCache() {
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
    }

    private void updateAllPairs() {
        allStatsCachedList.clear();
        allStatsCachedList.addAll(this.cachedList);
        allPairs.forEach(pair -> {
            boolean available = false;
            for (ExOrderStatisticsShortByPairsDto order : this.cachedList) {
                if (order.getCurrencyPairName().equals(pair.getName())) {
                    available = true;
                    break;
                }
            }
            if (!available) {
                ExOrderStatisticsShortByPairsDto dto = new ExOrderStatisticsShortByPairsDto();
                dto.setCurrencyPairName(pair.getName());
                dto.setNeedRefresh(true);
                dto.setLastOrderRate(BigDecimal.ZERO.toPlainString());
                dto.setPredLastOrderRate(BigDecimal.ZERO.toPlainString());
                dto.setPercentChange(BigDecimal.ZERO.toPlainString());
                allStatsCachedList.add(dto);
            }
        });
    }
}
