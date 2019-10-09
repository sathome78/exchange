package me.exrates.ngDao.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import lombok.extern.log4j.Log4j2;
import me.exrates.ngDao.RedisWsSessionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Repository;
import reactor.util.annotation.NonNull;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Repository
@Log4j2
public class RedisWsSessionDaoImpl implements RedisWsSessionDao {

    private final String REDIS_SESSION_MAP = "REDIS_WS_SESSION_MAP";

    private final HashOperations<String, String, String> hashOperations;
    private LoadingCache<String, Optional<String>> redisCache = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build(createCacheLoader());

    @Autowired
    public RedisWsSessionDaoImpl(@Qualifier("hashOperations") HashOperations<String, String, String> hashOperations) {
        this.hashOperations = hashOperations;
    }

    @Override
    public void addSession(String email, String sessionId) {
        hashOperations.put(REDIS_SESSION_MAP, email, sessionId);
    }

    @Override
    public void removeSession(String email) {
        redisCache.invalidate(email);
        hashOperations.delete(REDIS_SESSION_MAP, email);
    }

    @Override
    public Optional<String> getSessionId(String email) {
        try {
            return redisCache.get(email);
        } catch (Exception e) {
            log.debug("Failed to get sessionId from email: " + email, e);
            return Optional.empty();
        }
    }

    @Override
    public Map<String, String> getSessions() {
        return hashOperations.entries(REDIS_SESSION_MAP);
    }

    private CacheLoader<String, Optional<String>> createCacheLoader() {
        return new CacheLoader<String, Optional<String>>() {
            @Override
            public Optional<String> load(@NonNull String email) {
                return Optional.ofNullable(hashOperations.get(REDIS_SESSION_MAP, email));
            }
        };
    }
}
