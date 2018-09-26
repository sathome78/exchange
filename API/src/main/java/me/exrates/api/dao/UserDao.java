package me.exrates.api.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Yuriy Berezin on 14.09.2018.
 */
@Repository
public class UserDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String SELECT_ATTEMPTS = "SELECT attempts FROM USER_API WHERE user_id = " +
            "(SELECT id FROM USER WHERE email = :email)";

    private static final String UPDATE_ATTEMPTS = "UPDATE USER_API SET attempts = :attempts " +
            "WHERE user_id = (SELECT id FROM USER WHERE email = :email)";

    private static final String INSERT_DEF_ATTEMPTS = "INSERT INTO USER_API (user_id, attempts) " +
            "VALUES ((SELECT id FROM USER WHERE email = :email), :attempts)";

    public Integer getRequestsLimit(String email) {
        try {
            return namedParameterJdbcTemplate.queryForObject(SELECT_ATTEMPTS,
                    new MapSqlParameterSource("email", email), Integer.class);
        } catch (EmptyResultDataAccessException e) {
            return 0;
        }
    }

    public void updateRequestsLimit(String email, Integer limit) {

        MapSqlParameterSource parameterSource =  new MapSqlParameterSource();
        parameterSource.addValue("email", email);
        parameterSource.addValue("attempts", limit);

        namedParameterJdbcTemplate.update(UPDATE_ATTEMPTS, parameterSource);
    }

    public void setRequestsDefaultLimit(String email, Integer limit) {

        MapSqlParameterSource parameterSource =  new MapSqlParameterSource();
        parameterSource.addValue("email", email);
        parameterSource.addValue("attempts", limit);

        namedParameterJdbcTemplate.update(INSERT_DEF_ATTEMPTS, parameterSource);
    }

}
