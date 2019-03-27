package me.exrates.service.cache.currencyPairsInfo;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.CurrencyPair;
import me.exrates.model.ngModel.ResponseInfoCurrencyPairDto;
import me.exrates.ngService.NgOrderService;
import me.exrates.service.bitshares.memo.Preconditions;
import me.exrates.service.cache.CurrencyPairsCache;
import me.exrates.service.stomp.StompMessenger;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;



public class CpStatisticsHolderImpl  {


    /*private Map<Integer, Semaphore> synchronizersMap = new ConcurrentHashMap<>();
    private static final int SLEEP_TIME_MS = 2000;

    private final NgOrderService orderService;
    private final CpInfoRedisRepository redisRepository;
    private final StompMessenger stompMessenger;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CurrencyPairsCache pairsCache;

    @Autowired
    public CpStatisticsHolderImpl(NgOrderService orderService, CpInfoRedisRepository redisRepository, StompMessenger stompMessenger, CurrencyPairsCache pairsCache) {
        this.orderService = orderService;
        this.redisRepository = redisRepository;
        this.stompMessenger = stompMessenger;
        this.pairsCache = pairsCache;
    }*/


    public void onOrderAccept(Integer pairId) {
      /*  if (!synchronizersMap.containsKey(pairId)) {
            putSynchronizer(pairId);
        }
        Semaphore semaphore = synchronizersMap.get(pairId);
        if (semaphore.tryAcquire()) {
            try {
                TimeUnit.MILLISECONDS.sleep(SLEEP_TIME_MS);
                ResponseInfoCurrencyPairDto dto = orderService.getCurrencyPairInfo(pairId);
                redisRepository.put(dto, pairId);
                stompMessenger.sendCpInfoMessage(pairsCache.getPairById(pairId).getName(), objectMapper.writeValueAsString(dto));
            } catch (Exception e) {
                log.error(e);
            } finally {
                semaphore.release();
            }
        }*/
    }
/*
    @Synchronized
    private void putSynchronizer(Integer pairId) {
        synchronizersMap.putIfAbsent(pairId, new Semaphore(1));
    }

    @Override
    public ResponseInfoCurrencyPairDto get(String pairName) {
        CurrencyPair cp = pairsCache.getPairByName(pairName);
        Preconditions.checkNotNull(cp);
        return get(cp.getId());
    }

    @Override
    public ResponseInfoCurrencyPairDto get(Integer pairId) {
        if (redisRepository.exist(pairId)) {
            return redisRepository.get(pairId);
        } else {
            ResponseInfoCurrencyPairDto dto = orderService.getCurrencyPairInfo(pairId);
            redisRepository.put(dto, pairId);
            return dto;
        }
    }*/




}
