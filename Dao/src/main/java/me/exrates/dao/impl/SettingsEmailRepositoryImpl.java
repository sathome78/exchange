package me.exrates.dao.impl;

import me.exrates.dao.SettingsEmailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

@Repository
public class SettingsEmailRepositoryImpl implements SettingsEmailRepository {
    private static final String DEFAULT_SENDER = "default";

    @Autowired
    @Qualifier(value = "slaveTemplate")
    private NamedParameterJdbcTemplate slaveParameterJdbcTemplate;

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate masterParameterJdbcTemplate;

    @Override
    public Map<String, String> getAllEmailSenders() {
        String sql = "SELECT * FROM EMAIL_SETTING";
        return slaveParameterJdbcTemplate.query(sql, (ResultSet rs) -> {
            HashMap<String, String> results = new HashMap<>();
            while (rs.next()) {
                results.put(rs.getString("host"), rs.getString("email_sender"));
            }
            return results;
        });
    }

    @Override
    public boolean addNewHost(String host, String email) {
        String sql = "INSERT INTO EMAIL_SETTING(host, email_sender) VALUES (:host, :email_sender)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("email_sender", email)
                .addValue("host", host);
        return masterParameterJdbcTemplate.update(sql, params) > 0;
    }

    @Override
    public String getEmailSenderByHost(String host) {
        String sql = "SELECT email_sender FROM EMAIL_SETTING WHERE host = :host";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("host", host);
        try {
            return slaveParameterJdbcTemplate.queryForObject(sql, params, String.class);
        } catch (EmptyResultDataAccessException e) {
            return DEFAULT_SENDER;
        }
    }
}
