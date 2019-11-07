package me.exrates.ngService.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.GeoLocation;
import me.exrates.ngDao.GeoLocationRepository;
import me.exrates.ngService.GeoLocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
public class GeoLocationServiceImpl implements GeoLocationService {

    private final LoadingCache<String, GeoLocation> loadingCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build(createCacheLoader());
    private final GeoLocationRepository geoLocationRepository;

    @Autowired
    public GeoLocationServiceImpl(GeoLocationRepository geoLocationRepository) {
        this.geoLocationRepository = geoLocationRepository;
    }

    @Override
    public GeoLocation findById(String ip) {
        try {
            return loadingCache.get(ip);
        } catch (ExecutionException e) {
            log.warn("Failed to find geo location for ip: " + ip, e);
            return GeoLocation.empty();
        }
    }

    private CacheLoader<String, GeoLocation> createCacheLoader() {
        return new CacheLoader<String, GeoLocation>() {
            @Override
            public GeoLocation load(String ip) {
                return geoLocationRepository.findByIP(ip).orElse(GeoLocation.empty());
            }
        };
    }
}
