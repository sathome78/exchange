package me.exrates.dao.impl;

import me.exrates.dao.MetricsDao;
import me.exrates.model.MethodMetricsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Repository
public class MetricsDaoImpl implements MetricsDao {

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void saveMethodMetrics(MethodMetricsDto methodMetrics) {
        final String methodKey = methodMetrics.getMethodKey();
        final int invocationCounter = methodMetrics.getInvocationCounter().get();
        final int errorCounter = methodMetrics.getErrorCounter().get();
        final double averageExecutionTime = methodMetrics.getExecutionTimes().stream().mapToLong(Long::longValue).summaryStatistics().getAverage();

        final String sql = "INSERT INTO METHOD_METRICS (method_key, invocation_counter, error_counter, average_execution_time, created_at)" +
                " VALUES (:method_key, :invocation_counter, :error_counter, :average_execution_time, :created_at)";

        Map<String, Object> params = new HashMap<>();
        params.put("method_key", methodKey);
        params.put("invocation_counter", invocationCounter);
        params.put("error_counter", errorCounter);
        params.put("average_execution_time", averageExecutionTime);
        params.put("created_at", Date.valueOf(LocalDate.now()));

        jdbcTemplate.update(sql, params);
    }
}