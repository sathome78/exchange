package me.exrates.api.service;

import me.exrates.api.dao.UserDao;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Yuriy Berezin on 14.09.2018.
 */
@Service
public class ApiRateLimitService {

    private static final Logger log = LogManager.getLogger(ApiRateLimitService.class);

    private static final int TIME_LIMIT_SECONDS = 3600;

    private static final Integer DEFAULT_ATTEMPS = 5;

    private final Map<String, CopyOnWriteArrayList<LocalDateTime>> userTimes = new ConcurrentHashMap<>();

    private final Map<String, Integer> userLimits = new ConcurrentHashMap<>();

    @Autowired
    private UserDao userApiDao;

    @Scheduled(initialDelay = 30 * 60 * 1000, fixedDelay = 30 * 60 * 1000)
    public void clearExpiredRequests() {
        if(log.isDebugEnabled()) {
            log.debug(">> clearExpiredRequests");
        }
        new HashMap<>(userTimes).forEach((k, v) -> {
            if (v.stream().filter(p -> p.isAfter(LocalDateTime.now().minusSeconds(TIME_LIMIT_SECONDS))).count() == 0) {
                userTimes.remove(k);
                if(log.isDebugEnabled()){
                    log.debug("Removed from cache: "+ k);
                }
            }
        });
    }

    public void registerRequest() {

        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        userTimes.putIfAbsent(userEmail, new CopyOnWriteArrayList<>());
        userTimes.get(userEmail).add(LocalDateTime.now());
    }

    public boolean isLimitExceed() {

        LocalDateTime beginTime = LocalDateTime.now().minusSeconds(TIME_LIMIT_SECONDS);
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Integer limit = getRequestLimit(userEmail);

        List<LocalDateTime> list = userTimes.get(userEmail);
        if (list == null) {
            return false;
        } else {
            long counter = list.stream().filter(p -> p.isAfter(beginTime)).count();
            return counter > limit;
        }
    }

    @Transactional
    public void setRequestLimit(String userEmail, Integer limit) {

        userApiDao.updateRequestsLimit(userEmail, limit);
        userLimits.put(userEmail, limit);
        userTimes.remove(userEmail);
    }

    @Transactional
    public Integer getRequestLimit(String userEmail) {

        if (userLimits.containsKey(userEmail)) {
            return userLimits.get(userEmail);
        } else {
            //get from database
            Integer limit = userApiDao.getRequestsLimit(userEmail);
            if (limit == 0) {
                //no record. set default
                userApiDao.setRequestsDefaultLimit(userEmail, DEFAULT_ATTEMPS);
                limit = DEFAULT_ATTEMPS;
            }
            userLimits.put(userEmail, limit);
            return limit;
        }
    }

    Map<String, Integer> getUserLimits() {
        return Collections.unmodifiableMap(userLimits);
    }

    public static Integer getDefaultAttemps() {
        return DEFAULT_ATTEMPS;
    }
}
