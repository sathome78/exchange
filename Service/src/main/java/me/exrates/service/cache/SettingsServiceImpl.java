package me.exrates.service.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import me.exrates.dao.SettingsEmailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class SettingsServiceImpl implements SettingsService {

    private final SettingsEmailRepository emailRepository;

    private LoadingCache<String, String> EMAIL_CONFIG_CACHE;

    @Autowired
    public SettingsServiceImpl(SettingsEmailRepository emailRepository) {
        this.emailRepository = emailRepository;
        EMAIL_CONFIG_CACHE = CacheBuilder.newBuilder()
                .refreshAfterWrite(30, TimeUnit.MINUTES)
                .maximumSize(50L)
                .build(createEmailConfigCacheLoader());
    }

    private CacheLoader<String, String> createEmailConfigCacheLoader() {
        return new CacheLoader<String, String>() {
            @Override
            public String load(String key) {
                return emailRepository.getEmailSenderByHost(key);
            }
        };
    }

    @Override
    public String getEmailsSenderFromCache(String host) {
        try {
            return EMAIL_CONFIG_CACHE.get(host);
        } catch (ExecutionException e) {
            return null;
        }
    }
}
