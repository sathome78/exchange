package me.exrates.ngService.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.RestrictedCountry;
import me.exrates.model.dto.GeoLocation;
import me.exrates.model.enums.RestrictedOperation;
import me.exrates.ngDao.GeoLocationRepository;
import me.exrates.ngService.GeoLocationService;
import me.exrates.ngService.RestrictedCountryService;
import me.exrates.service.util.IpUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
public class GeoLocationServiceImpl implements GeoLocationService {

    private final LoadingCache<String, GeoLocation> loadingCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build(createCacheLoader());
    private final GeoLocationRepository geoLocationRepository;
    private final RestrictedCountryService restrictedCountryService;

    @Autowired
    public GeoLocationServiceImpl(GeoLocationRepository geoLocationRepository,
                                  RestrictedCountryService restrictedCountryService) {
        this.geoLocationRepository = geoLocationRepository;
        this.restrictedCountryService = restrictedCountryService;
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

    @Override
    public boolean isCountryRestrictedByIp(HttpServletRequest request, RestrictedOperation ... restrictions) {
        String ipAddress = IpUtils.getIpForDbLog(request);
        final GeoLocation geoLocation = findById(ipAddress);
        String country = geoLocation.getCountry();
        final Set<RestrictedCountry> restrictedCountries = restrictedCountryService.findAllByOperation(restrictions);
        return StringUtils.isNoneBlank(country)
                && restrictedCountries.stream().anyMatch(c -> c.getCountryCode().equalsIgnoreCase(country)
                || c.getCountryName().equalsIgnoreCase(country));
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
