package me.exrates.dao.impl;

import me.exrates.dao.MetricsDao;
import me.exrates.model.MethodMetricsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Repository
public class MetricsDaoImpl implements MetricsDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void saveMethodMetrics(List<MethodMetricsDto> methodMetricsDtoList) {
        final String sql = "INSERT INTO METHOD_METRICS (method_key, invocation_counter, error_counter, average_execution_time, created_at)" +
                " VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                MethodMetricsDto methodMetrics = methodMetricsDtoList.get(i);
                preparedStatement.setString(1, methodMetrics.getMethodKey());
                preparedStatement.setInt(2, methodMetrics.getInvocationCounter().get());
                preparedStatement.setInt(3, methodMetrics.getErrorCounter().get());
                preparedStatement.setDouble(4, methodMetrics.getExecutionTimes().stream().mapToLong(Long::longValue).summaryStatistics().getAverage());
                preparedStatement.setDate(5, Date.valueOf(LocalDate.now()));
            }

            @Override
            public int getBatchSize() {
                return methodMetricsDtoList.size();
            }
        });
    }
}