package me.exrates.dao;

import me.exrates.model.MethodMetricsDto;

import java.util.List;

public interface MetricsDao {

    void saveMethodMetrics(List<MethodMetricsDto> methodMetricsDtoList);
}