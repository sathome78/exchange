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
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private final HashOperations<String, String, String> hashOperations;
    private LoadingCache<String, String> redisCache = CacheBuilder.newBuilder()
            .refreshAfterWrite(5, TimeUnit.MINUTES)
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
            return Optional.ofNullable(redisCache.get(email));
        } catch (Exception e) {
            log.info("Failed to get sessionId from email: " + email, e);
            return Optional.empty();
        }
    }

    @Override
    public Map<String, String> getSessions() {
        return hashOperations.entries(REDIS_SESSION_MAP);
    }

    private CacheLoader<String, String> createCacheLoader() {
        return new CacheLoader<String, String>() {
            @Override
            public String load(@NonNull String email) {
                return hashOperations.get(REDIS_SESSION_MAP, email);
            }

            @Override
            public ListenableFuture<String> reload(final String email, String sessionId) {
                ListenableFutureTask<String> command =
                        ListenableFutureTask.create(() -> hashOperations.get(REDIS_SESSION_MAP, email));
                executorService.execute(command);
                return command;
            }
        };
    }
}
