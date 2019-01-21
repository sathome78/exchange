package me.exrates.service.cache;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.MetricsDao;
import me.exrates.model.MethodMetricsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Log4j2
@Component
public class MetricsCache {

    private final MetricsDao metricsDao;

    private ConcurrentMap<String, MethodMetricsDto> methodMetricsMap;

    @Autowired
    public MetricsCache(MetricsDao metricsDao) {
        this.metricsDao = metricsDao;
        this.methodMetricsMap = new ConcurrentHashMap<>();
    }

    @Scheduled(cron = "${scheduled.method.metrics}")
    public void scheduledMetrics() {
        saveDailyMetricsToDatabase();
    }

    private void saveDailyMetricsToDatabase() {
        List<MethodMetricsDto> values = new ArrayList<>(methodMetricsMap.values());
        metricsDao.saveMethodMetrics(values);
        methodMetricsMap.clear();
    }

    public MethodMetricsDto getMethodMetrics(String key) {
        return methodMetricsMap.get(key);
    }

    public void setMethodMetrics(MethodMetricsDto methodMetrics) {
        methodMetricsMap.put(methodMetrics.getMethodKey(), methodMetrics);
    }
}