package me.exrates.dao.impl;



import lombok.extern.log4j.Log4j2;
import me.exrates.dao.GtagRefillRequests;
import me.exrates.model.enums.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Repository
@Log4j2
public class GtagRefillRequestsImpl implements GtagRefillRequests {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void updateUserRequestsCount(Integer userId) {

        SqlParameterSource namedParameters = new MapSqlParameterSource("userId", userId);
        try {
            int update = namedParameterJdbcTemplate.update("UPDATE GTAG_REFILL_REQUESTS SET GTAG_REFILL_REQUESTS.COUNT=GTAG_REFILL_REQUESTS.COUNT+1 WHERE GTAG_REFILL_REQUESTS.USER_ID=:userId", namedParameters);
            if (update != 0) {
                log.info("Update");
            }
        } catch (Exception ex) {
            log.warn("Unable to update GTAG_REFILL_REQUESTS count for user");
        }
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Integer getUserRequestsCount(Integer userId) {

        Map<String, Object> params = new HashMap<String, Object>() {{
            put("userId", userId);
        }};
        try {
            return namedParameterJdbcTemplate.queryForObject("SELECT COUNT FROM GTAG_REFILL_REQUESTS WHERE USER_ID=:userId FOR UPDATE ", params, Integer.class);
        } catch (RuntimeException ex) {
            return 0;
        }
    }

    public void resetCount(Integer userId) {
        SqlParameterSource namedParameters = new MapSqlParameterSource("userId", userId);
        try {
            int update = namedParameterJdbcTemplate.update("UPDATE GTAG_REFILL_REQUESTS SET GTAG_REFILL_REQUESTS.COUNT = 0 WHERE GTAG_REFILL_REQUESTS.USER_ID=:userId", namedParameters);
            if (update != 0) {
                log.info("Update");
            }
        } catch (Exception ex) {
            log.warn("Unable to reset GTAG_REFILL_REQUESTS count for user");
        }
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
        int result = namedParameterJdbcTemplate.update(sql, parameters, keyHolder);
        if (result <= 0) {
            log.warn("Unable to insert row");
        }
    }


}
