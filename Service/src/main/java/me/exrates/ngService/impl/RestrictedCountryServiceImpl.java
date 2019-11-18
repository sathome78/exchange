package me.exrates.ngService.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.RestrictedCountry;
import me.exrates.model.enums.RestrictedOperation;
import me.exrates.ngDao.RestrictedCountryDao;
import me.exrates.ngService.RestrictedCountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Log4j2
public class RestrictedCountryServiceImpl implements RestrictedCountryService {

    private final RestrictedCountryDao restrictedCountryDao;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final LoadingCache<RestrictedOperation, Set<RestrictedCountry>> restrictedCountriesCache = CacheBuilder.newBuilder()
            .refreshAfterWrite(1, TimeUnit.HOURS)
            .build(createCacheLoader());

    @Autowired
    public RestrictedCountryServiceImpl(RestrictedCountryDao restrictedCountryDao) {
        this.restrictedCountryDao = restrictedCountryDao;
    }

    @PostConstruct
    public void loadCache() {
        loadCacheData();
    }

    @Override
    public RestrictedCountry save(RestrictedCountry restrictedCountry) {
        final RestrictedCountry save = restrictedCountryDao.save(restrictedCountry);
        restrictedCountriesCache.refresh(restrictedCountry.getOperation());
        return save;
    }

    @Override
    public Set<RestrictedCountry> findAllByOperation(RestrictedOperation ... operations) {
        Set<RestrictedOperation> ops = ImmutableSet.copyOf(operations);
        return restrictedCountriesCache.asMap()
                .entrySet()
                .stream()
                .filter(entry -> ops.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean delete(RestrictedCountry restrictedCountry) {
        final boolean result = restrictedCountryDao.delete(restrictedCountry.getId());
        if (result) {
            restrictedCountriesCache.refresh(restrictedCountry.getOperation());
        }
        return result;
    }

    @Override
    public boolean delete(RestrictedOperation operation, String countryName) {
        return restrictedCountryDao.delete(operation, countryName);
    }

    private CacheLoader<RestrictedOperation, Set<RestrictedCountry>> createCacheLoader() {
        return new CacheLoader<RestrictedOperation, Set<RestrictedCountry>>() {
            @Override
            public Set<RestrictedCountry> load(RestrictedOperation operation) {
                return restrictedCountryDao.findAll(operation);
            }

            @Override
            public ListenableFuture<Set<RestrictedCountry>> reload(RestrictedOperation key, Set<RestrictedCountry> oldValue) {
                ListenableFutureTask<Set<RestrictedCountry>> command =
                        ListenableFutureTask.create(() -> restrictedCountryDao.findAll(key));
                executorService.execute(command);
                return command;
            }
        };
    }

    private void loadCacheData() {
        final Set<RestrictedCountry> countries = restrictedCountryDao.findAll();
        Map<RestrictedOperation, Set<RestrictedCountry>> cache = new HashMap<>();
        countries.forEach(restrictedCountry -> {
            final RestrictedOperation operation = restrictedCountry.getOperation();
            cache.computeIfPresent(operation, (operation1, restrictedCountries) -> {
                restrictedCountries.add(restrictedCountry);
                return restrictedCountries;
            });
            cache.putIfAbsent(operation, new HashSet<>(Arrays.asList(restrictedCountry)));
        });
        restrictedCountriesCache.putAll(cache);
    }
}
