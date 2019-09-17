package me.exrates.ngService.impl;

import me.exrates.ngDao.RedisWsSessionDao;
import me.exrates.ngService.RedisWsSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class RedisWsSessionServiceImpl implements RedisWsSessionService {

    private final RedisWsSessionDao redisWsSessionDao;

    @Autowired
    public RedisWsSessionServiceImpl(RedisWsSessionDao redisWsSessionDao) {
        this.redisWsSessionDao = redisWsSessionDao;
    }

    @Override
    public void addSession(String email, String sessionId) {
        redisWsSessionDao.addSession(email, sessionId);
    }

    @Override
    public void removeSession(String email) {
        redisWsSessionDao.removeSession(email);
    }

    @Override
    public Optional<String> getSessionId(String email) {
        return redisWsSessionDao.getSessionId(email);
    }

    @Override
    public Map<String, String> getSessions() {
        return redisWsSessionDao.getSessions();
    }
}
