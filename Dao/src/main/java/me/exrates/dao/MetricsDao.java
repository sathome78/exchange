package me.exrates.dao;

import me.exrates.model.MethodMetricsDto;

public interface MetricsDao {

    void saveMethodMetrics(MethodMetricsDto methodMetrics);
}