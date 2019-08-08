package me.exrates.dao.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.IEOSubscribeRepository;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Log4j2
public class IEOSubscribeRepositoryImpl implements IEOSubscribeRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public IEOSubscribeRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean subscribeEmail(String email) {
        String sql = "INSERT IGNORE INTO IEO_SUBSCRIBE(email, email_subscribe) VALUES (:email, :subscribe)";
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("email", email);
        namedParameters.addValue("subscribe", true);
        return jdbcTemplate.update(sql, namedParameters) > 0;
    }

    @Override
    public boolean subscribeTelegram(String email) {
        String sql = "INSERT IGNORE INTO IEO_SUBSCRIBE(email, telegram_subscribe) VALUES (:email, :subscribe)";
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("email", email);
        namedParameters.addValue("subscribe", true);
        return jdbcTemplate.update(sql, namedParameters) > 0;
    }

    @Override
    public boolean isUserSubscribeForEmail(String email) {
        String rawSql = "SELECT CASE WHEN count(*) > 0 THEN TRUE ELSE FALSE END FROM IEO_SUBSCRIBE WHERE email = :email AND email_subscribe = :subscribe";
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("email", email);
        namedParameters.addValue("subscribe", true);
        return jdbcTemplate.queryForObject(rawSql, namedParameters, Boolean.class);
    }

    @Override
    public boolean isUserSubscribeForTelegram(String email) {
        String rawSql = "SELECT CASE WHEN count(*) > 0 THEN TRUE ELSE FALSE END FROM IEO_SUBSCRIBE WHERE email = :email AND telegram_subscribe = :subscribe";
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("email", email);
        namedParameters.addValue("subscribe", true);
        return jdbcTemplate.queryForObject(rawSql, namedParameters, Boolean.class);
    }

    @Override
    public boolean isUserSubscribe(String email) {
        String rawSql = "SELECT CASE WHEN count(*) > 0 THEN TRUE ELSE FALSE END FROM IEO_SUBSCRIBE WHERE email = :email";
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("email", email);
        return jdbcTemplate.queryForObject(rawSql, namedParameters, Boolean.class);
    }

    @Override
    public boolean updateSubscribeEmail(String email) {
        String sql = "UPDATE IEO_SUBSCRIBE SET email_subscribe = :subscribe WHERE email = :email";
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("email", email);
        namedParameters.addValue("subscribe", true);
        return jdbcTemplate.update(sql, namedParameters) > 0;
    }

    @Override
    public boolean updateSubscribeTelegram(String email) {
        String sql = "UPDATE IEO_SUBSCRIBE SET telegram_subscribe = :subscribe WHERE email = :email";
        MapSqlParameterSource namedParameters = new MapSqlParameterSource();
        namedParameters.addValue("email", email);
        namedParameters.addValue("subscribe", true);
        return jdbcTemplate.update(sql, namedParameters) > 0;
    }
}
