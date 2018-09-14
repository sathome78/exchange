package me.exrates.api.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Created by Yuriy Berezin on 14.09.2018.
 */
@Repository
public class UserDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String SELECT_ATTEMPTS = "SELECT attempts FROM user_api WHERE user_id = " +
            "(SELECT id FROM user WHERE email = :email)";

    private static final String UPDATE_ATTEMPTS = "UPDATE user_api SET = :attempts WHERE user_id = " +
            "(SELECT id FROM user WHERE email = :email)";

    public Integer getUserLimit(String email) {
        try {
            return namedParameterJdbcTemplate.queryForObject(SELECT_ATTEMPTS,
                    new MapSqlParameterSource("email", email), Integer.class);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    public void setUserLimit(String email, Integer limit) {

        MapSqlParameterSource parameterSource =  new MapSqlParameterSource();
        parameterSource.addValue("email", email);
        parameterSource.addValue("limit", limit);

        namedParameterJdbcTemplate.update(UPDATE_ATTEMPTS, parameterSource);
    }

}
