package me.exrates.service;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Log4j2(topic = "jdbc_logger")
public class NamedParameterJdbcTemplateWrapper extends NamedParameterJdbcTemplate {

    private static final long EXECUTION_TIME_THRESHOLD = TimeUnit.SECONDS.toMillis(15);

    public NamedParameterJdbcTemplateWrapper(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public <T> T execute(String sql, Map<String, ?> paramMap, PreparedStatementCallback<T> action) throws DataAccessException {
        logSql(sql, paramMap);
        return withTimeMeasure(sql, paramMap, () -> super.execute(sql, paramMap, action));
    }

    @Override
    public <T> T query(String sql, Map<String, ?> paramMap, ResultSetExtractor<T> rse) throws DataAccessException {
        logSql(sql, paramMap);
        return withTimeMeasure(sql, paramMap, () -> super.query(sql, paramMap, rse));
    }

    @Override
    public void query(String sql, Map<String, ?> paramMap, RowCallbackHandler rch) throws DataAccessException {
        logSql(sql, paramMap);
        super.query(sql, paramMap, rch);
    }

    @Override
    public <T> List<T> query(String sql, Map<String, ?> paramMap, RowMapper<T> rowMapper) throws DataAccessException {
        logSql(sql, paramMap);
        return withTimeMeasure(sql, paramMap, () -> super.query(sql, paramMap, rowMapper));
    }

    @Override
    public <T> T queryForObject(String sql, Map<String, ?> paramMap, RowMapper<T> rowMapper) throws DataAccessException {
        logSql(sql, paramMap);
        return withTimeMeasure(sql, paramMap, () -> super.queryForObject(sql, paramMap, rowMapper));
    }

    @Override
    public <T> T queryForObject(String sql, Map<String, ?> paramMap, Class<T> requiredType) throws DataAccessException {
        logSql(sql, paramMap);
        return withTimeMeasure(sql, paramMap, () -> super.queryForObject(sql, paramMap, requiredType));
    }

    @Override
    public Map<String, Object> queryForMap(String sql, Map<String, ?> paramMap) throws DataAccessException {
        logSql(sql, paramMap);
        return withTimeMeasure(sql, paramMap, () -> super.queryForMap(sql, paramMap));
    }

    @Override
    public <T> List<T> queryForList(String sql, Map<String, ?> paramMap, Class<T> elementType) throws DataAccessException {
        logSql(sql, paramMap);
        return withTimeMeasure(sql, paramMap, () -> super.queryForList(sql, paramMap, elementType));
    }

    @Override
    public List<Map<String, Object>> queryForList(String sql, Map<String, ?> paramMap) throws DataAccessException {
        logSql(sql, paramMap);
        return withTimeMeasure(sql, paramMap, () -> super.queryForList(sql, paramMap));
    }

    @Override
    public SqlRowSet queryForRowSet(String sql, Map<String, ?> paramMap) throws DataAccessException {
        logSql(sql, paramMap);
        return withTimeMeasure(sql, paramMap, () -> super.queryForRowSet(sql, paramMap));
    }

    @Override
    public int update(String sql, Map<String, ?> paramMap) throws DataAccessException {
        logSql(sql, paramMap);
        return withTimeMeasure(sql, paramMap, () -> super.update(sql, paramMap));
    }

    private void logSql(String sql, Map<String, ?> paramMap) {
        log.debug(completeSql(sql, paramMap));
    }

    private String completeSql(String sql, Map<String, ?> paramMap) {
        for (Map.Entry<String, ?> entry : paramMap.entrySet()) {
            sql = sql.replace(":" + entry.getKey(), String.valueOf(entry.getValue()));
        }
        return sql;
    }

    private <T> T withTimeMeasure(String sql, Map<String, ?> paramMap, Supplier<T> supplier) {
        StopWatch stopWatch = StopWatch.createStarted();
        try {
            return supplier.get();
        } catch (Exception e) {
            log.error("SQL EXCEPTION: {} \n Performed with exception", completeSql(sql, paramMap), e);
            throw e;
        } finally {
            long millis = stopWatch.getTime(TimeUnit.MILLISECONDS);
            if (millis > EXECUTION_TIME_THRESHOLD) {
                String result = completeSql(sql, paramMap);
                log.info(String.format("SLOW SQL EXECUTION TIME: [%d], SQL: [%s]", millis, result));
            }
        }
    }

}
