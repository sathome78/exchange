package me.exrates.dao.impl;

import me.exrates.dao.UserSessionsDao;
import me.exrates.model.dto.UserLoginSessionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class UserSessionsDaoImpl implements UserSessionsDao {

    private final NamedParameterJdbcTemplate masterJdbcTemplate;
    private final NamedParameterJdbcTemplate slaveJdbcTemplate;

    @Autowired
    public UserSessionsDaoImpl(@Qualifier(value = "masterTemplate") NamedParameterJdbcTemplate masterJdbcTemplate,
                               @Qualifier(value = "slaveTemplate") NamedParameterJdbcTemplate slaveJdbcTemplate) {
        this.masterJdbcTemplate = masterJdbcTemplate;
        this.slaveJdbcTemplate = slaveJdbcTemplate;
    }


    @Override
    public void insertSessionDto(UserLoginSessionDto userLoginSessionDto, String email) {
        final String sql = "INSERT INTO USER_SESSIONS (user_id, device, user_agent, os, ip, country, region, city, token, started, modified) " +
                           "VALUES ((SELECT id from USER WHERE email =:email), :device, :user_agent, :os, :ip, :country, :region, :city, :token, :started, :modified)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("email", email);
        params.addValue("device", userLoginSessionDto.getDevice());
        params.addValue("user_agent", userLoginSessionDto.getUserAgent());
        params.addValue("os", userLoginSessionDto.getOs());
        params.addValue("ip", userLoginSessionDto.getIp());
        params.addValue("country", userLoginSessionDto.getCountry());
        params.addValue("region", userLoginSessionDto.getRegion());
        params.addValue("city", userLoginSessionDto.getCity());
        params.addValue("token", userLoginSessionDto.getToken());
        params.addValue("started", userLoginSessionDto.getStarted());
        params.addValue("modified", userLoginSessionDto.getModified());
        masterJdbcTemplate.update(sql, params);
    }

    @Override
    public boolean updateModified(String userAgent, String token, LocalDateTime modified) {
        final String sql = "UPDATE USER_SESSIONS SET modified = :modified " +
                           "WHERE token = :token AND user_agent = :userAgent";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("modified", modified);
        params.addValue("token", token);
        params.addValue("userAgent", userAgent);
        return masterJdbcTemplate.update(sql, params) > 0;
    }

    @Override
    public List<UserLoginSessionDto> getPage(String email, int limit, int offset) {
        final String sql = "SELECT * FROM USER_SESSIONS " +
                "WHERE user_id = (SELECT id FROM USER WHERE email = :email)" +
                "ORDER BY modified DESC " +
                "LIMIT :size OFFSET :from";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("email", email);
        params.addValue("size", limit);
        params.addValue("from", offset);
        return slaveJdbcTemplate.query(sql, params, new BeanPropertyRowMapper(UserLoginSessionDto.class));
    }

    @Override
    public int countAll(String email) {
        final String sql = "SELECT COUNT(*) FROM USER_SESSIONS " +
                           "WHERE user_id = (SELECT id FROM USER WHERE email = :email)";
        return slaveJdbcTemplate.queryForObject(sql, new MapSqlParameterSource("email", email), Integer.class);
    }
}
