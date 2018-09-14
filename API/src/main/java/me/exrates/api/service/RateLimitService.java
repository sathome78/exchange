package me.exrates.api.service;

import me.exrates.api.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Yuriy Berezin on 14.09.2018.
 */
@Service
public class RateLimitService {

    private static final int TIME_LIMIT_SECONDS = 3600;

    private static final int ATTEMPS = 5;

    private Map<String, CopyOnWriteArrayList<LocalDateTime>> map = new ConcurrentHashMap<>();

    @Autowired
    private UserDao userApiDao;

    @Scheduled(cron = "* 5 * * * *")
    public void clearExpiredRequests() {

        new HashMap<>(map).forEach((k, v) -> {
            if (v.stream().filter(p -> p.isAfter(LocalDateTime.now().minusSeconds(TIME_LIMIT_SECONDS))).count() == 0) {
                map.remove(k);
            }
        });
    }

    public void registerRequest() {

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        map.putIfAbsent(userEmail, new CopyOnWriteArrayList<>());
        map.get(userEmail).add(LocalDateTime.now());
    }

    public void checkLimitsExceed() throws RequestsLimitExceedException {

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        LocalDateTime beginTime = LocalDateTime.now().minusSeconds(TIME_LIMIT_SECONDS);
        List<LocalDateTime> list = map.get(userEmail);
        if (list != null) {
            long counter = list.stream().filter(p -> p.isAfter(beginTime)).count();
            if (counter > ATTEMPS) {
                throw new RequestsLimitExceedException("LimitsExceed: " + userEmail);
            }
        }
    }

    public void setLimit(String userEmail, Integer limit){

        userApiDao.setUserLimit(userEmail, limit);
    }

    public void getLimit(Long userId){

    }

}
