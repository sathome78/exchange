package me.exrates.dao.impl;

import me.exrates.dao.SettingsEmailRepository;
import me.exrates.model.EmailRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    public List<EmailRule> getAllEmailSenders() {
        String sql = "SELECT * FROM EMAIL_SETTING";
        return slaveParameterJdbcTemplate.query(sql, (rs, row) -> {
            EmailRule emailRule = new EmailRule();
            emailRule.setHost(rs.getString("host"));
            emailRule.setSender(rs.getString("email_sender"));
            return emailRule;
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

    @Override
    public boolean deleteEmailRule(String host) {
        String sql = "DELETE FROM EMAIL_SETTING WHERE host = :host";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("host", host);
        return masterParameterJdbcTemplate.update(sql, params) > 0;
    }

    @Override
    public boolean updateEmailRule(String host, String emailSender) {
        String sql = "UPDATE EMAIL_SETTING SET email_sender = :email_sender WHERE host = :host";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("host", host)
                .addValue("email_sender", emailSender);
        return masterParameterJdbcTemplate.update(sql, params) > 0;
    }


}
