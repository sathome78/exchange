package me.exrates.service.util;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by maks on 20.06.2017.
 */
@Service
public class RateLimitService  {


    private static final int TIME_LIMIT_SECONDS = 3600;

    private static final int ATTEMPS = 5;

    private Map<String, CopyOnWriteArrayList<LocalDateTime>> map = new ConcurrentHashMap<>();

    @Scheduled(cron = "* 5 * * * *")
    public void clearExpiredRequests() {
        new HashMap<>(map).forEach((k, v)-> {
            if (v.stream().filter(p->p.isAfter(LocalDateTime.now().minusSeconds(TIME_LIMIT_SECONDS))).count() == 0) {
                map.remove(k);
            }
        });
    }


    public void registerRequest(String userEmail) {
        map.putIfAbsent(userEmail, new CopyOnWriteArrayList<>());
        map.get(userEmail).add(LocalDateTime.now());
    }


    public boolean checkLimitsExceed(String email) {
        LocalDateTime beginTime = LocalDateTime.now().minusSeconds(TIME_LIMIT_SECONDS);
        List<LocalDateTime> list = map.get(email);
        if (list == null) {
            return true;
        }
        long counter = list.stream().filter(p->p.isAfter(beginTime)).count();
        return counter < ATTEMPS;
    }

}