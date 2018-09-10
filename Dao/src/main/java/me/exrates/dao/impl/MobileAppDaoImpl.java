package me.exrates.dao.impl;

import me.exrates.dao.MobileAppDao;
import me.exrates.model.enums.UserAgent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

/**
 * Created by OLEG on 06.10.2016.
 */
@Repository
public class MobileAppDaoImpl implements MobileAppDao {

    private static final Logger LOGGER = LogManager.getLogger(MobileAppDaoImpl.class);

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate parameterJdbcTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Override
    public Optional<String> getAppKey(UserAgent userAgent) {
        if (userAgent == UserAgent.DESKTOP) {
            return Optional.empty();
        }
        String sql = "SELECT param_value FROM API_PARAMS WHERE param_name = :paramName";
        Map<String, String> params = Collections.singletonMap("paramName", userAgent.name() + "_APP_VERSION_KEY");
        return Optional.ofNullable(parameterJdbcTemplate.queryForObject(sql, params, (resultSet, row) -> resultSet.getString("param_value")));
    }

    @Override
    public boolean appKeyCheckEnabled() {
        String sql = "SELECT param_value FROM API_PARAMS WHERE param_name = 'KEY_CHECK_ENABLED'";
        String result = jdbcTemplate.queryForObject(sql, (resultSet, row) -> resultSet.getString("param_value"));
        return "ON".equals(result);
    }
}
