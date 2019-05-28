package me.exrates.dao.impl;



import lombok.extern.log4j.Log4j2;
import me.exrates.dao.GtagRefillRequests;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
@Log4j2
public class GtagRefillRequestsImpl implements GtagRefillRequests {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public void updateUserRequestsCount(Integer userId) {
        SqlParameterSource namedParameters = new MapSqlParameterSource("userId", userId);
        namedParameterJdbcTemplate.update("UPDATE GTAG_REFILL_REQUESTS SET GTAG_REFILL_REQUESTS.COUNT=GTAG_REFILL_REQUESTS.COUNT+1 WHERE GTAG_REFILL_REQUESTS.USER_ID=:userId", namedParameters);
    }

    public Integer getUserRequestsCount(Integer userId) {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("userId", userId);
        }};
        try {
            return namedParameterJdbcTemplate.queryForObject("SELECT COUNT FROM GTAG_REFILL_REQUESTS WHERE USER_ID=:userId ", params, Integer.class);
        } catch (DataAccessException e) {
            return 0;
        }
    }

    public void resetCount(Integer userId) {
        SqlParameterSource namedParameters = new MapSqlParameterSource("userId", userId);
        namedParameterJdbcTemplate.update("UPDATE GTAG_REFILL_REQUESTS SET GTAG_REFILL_REQUESTS.COUNT = 0 WHERE GTAG_REFILL_REQUESTS.USER_ID=:userId", namedParameters);
    }

    @Override
    public Integer getUserIdOfGtagRequests(Integer userId) {
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("userId", userId);
        }};
        try {
            return namedParameterJdbcTemplate.queryForObject("SELECT USER_ID FROM GTAG_REFILL_REQUESTS WHERE USER_ID=:userId", params, Integer.class);
        } catch (Exception ex) {
            return null;
        }

    }

    @Override
    public void addFirstCount(Integer userId) {
        String sql = "INSERT INTO GTAG_REFILL_REQUESTS" +
                "  (USER_ID, COUNT)" +
                "  VALUES " +
                "  (:user_id, :currency_pair_id)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("user_id", userId)
                .addValue("currency_pair_id", 1);
        namedParameterJdbcTemplate.update(sql, parameters, keyHolder);
    }


}
