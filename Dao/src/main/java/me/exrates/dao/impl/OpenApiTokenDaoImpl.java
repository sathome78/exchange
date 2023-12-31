package me.exrates.dao.impl;

import me.exrates.dao.OpenApiTokenDao;
import me.exrates.model.OpenApiToken;
import me.exrates.model.dto.openAPI.OpenApiTokenPublicDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class OpenApiTokenDaoImpl implements OpenApiTokenDao {

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier(value = "slaveTemplate")
    private NamedParameterJdbcTemplate slaveJdbcTemplate;

    private final RowMapper<OpenApiToken> tokenRowMapper = (rs, rowNum) -> {
        OpenApiToken token = new OpenApiToken();
        token.setId(rs.getLong("token_id"));
        token.setUserId(rs.getInt("user_id"));
        token.setUserEmail(rs.getString("email"));
        token.setAlias(rs.getString("alias"));
        token.setPublicKey(rs.getString("public_key"));
        token.setPrivateKey(rs.getString("private_key"));
        token.setAllowTrade(rs.getBoolean("allow_trade"));
        token.setAllowWithdraw(rs.getBoolean("allow_withdraw"));
        token.setGenerationDate(rs.getTimestamp("date_generation").toLocalDateTime());
        token.setAllowAcceptById(rs.getBoolean("allow_accept_by_id"));
        return token;
    };

    @Override
    public Long saveToken(OpenApiToken token) {
        String sql = "INSERT INTO OPEN_API_USER_TOKEN (user_id, alias, public_key, private_key, allow_trade, allow_withdraw, allow_accept_by_id) " +
                " VALUES (:user_id, :alias, :public_key, :private_key, :allow_trade, :allow_withdraw, :allow_accept_by_id) ";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("user_id", token.getUserId())
                .addValue("alias", token.getAlias())
                .addValue("public_key", token.getPublicKey())
                .addValue("private_key", token.getPrivateKey())
                .addValue("allow_trade", token.getAllowTrade())
                .addValue("allow_withdraw", token.getAllowWithdraw())
                .addValue("allow_accept_by_id", token.getAllowAcceptById());
        jdbcTemplate.update(sql, params, keyHolder);
        return keyHolder.getKey().longValue();
    }

    @Override
    public Optional<OpenApiToken> getByPublicKey(String publicKey) {
        String sql = "SELECT OT.id AS token_id, OT.user_id, U.email, OT.alias, OT.public_key, OT.private_key, OT.date_generation, " +
                "OT.allow_trade, OT.allow_withdraw, OT.allow_accept_by_id as allow_accept_by_id " +
                " FROM OPEN_API_USER_TOKEN OT" +
                " JOIN USER U ON OT.user_id = U.id " +
                " WHERE OT.public_key = :public_key AND is_active = 1 ";

        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, Collections.singletonMap("public_key", publicKey), tokenRowMapper));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<OpenApiToken> getById(Long id) {
        String sql = "SELECT OT.id AS token_id, OT.user_id, U.email, OT.alias, OT.public_key, OT.private_key, OT.date_generation, " +
                "OT.allow_trade, OT.allow_withdraw, OT.allow_accept_by_id as allow_accept_by_id " +
                " FROM OPEN_API_USER_TOKEN OT" +
                " JOIN USER U ON OT.user_id = U.id " +
                " WHERE OT.id = :id ";
        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, Collections.singletonMap("id", id), tokenRowMapper));
        } catch (DataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<OpenApiTokenPublicDto> getActiveTokensForUser(String userEmail) {
        String sql = "SELECT OT.id AS token_id, OT.user_id, OT.alias, OT.public_key, OT.allow_trade, OT.allow_withdraw," +
                " OT.private_key, OT.date_generation, OT.allow_accept_by_id as allow_accept_by_id " +
                " FROM OPEN_API_USER_TOKEN OT" +
                " WHERE OT.is_active = 1 AND OT.user_id = (SELECT id FROM USER where email = :user_email) ";

        return jdbcTemplate.query(sql, Collections.singletonMap("user_email", userEmail), (rs, rowNum) -> {
            OpenApiTokenPublicDto token = new OpenApiTokenPublicDto();
            token.setId(rs.getLong("token_id"));
            token.setUserId(rs.getInt("user_id"));
            token.setAlias(rs.getString("alias"));
            token.setPublicKey(rs.getString("public_key"));
            token.setAllowTrade(rs.getBoolean("allow_trade"));
            token.setAllowWithdraw(rs.getBoolean("allow_withdraw"));
            token.setGenerationDate(rs.getTimestamp("date_generation").toLocalDateTime());
            token.setAllowAcceptById(rs.getBoolean("allow_accept_by_id"));
            return token;
        });
    }

    @Override
    public void updateToken(Long tokenId, String alias, Boolean allowTrade, Boolean allowWithdraw, Boolean allowAcceptById) {
        String sql = "UPDATE OPEN_API_USER_TOKEN SET alias = :alias, allow_trade = :allow_trade, allow_withdraw = :allow_withdraw, " +
                " allow_accept_by_id = :allow_accept_by_id WHERE id = :token_id ";
        Map<String, Object> params = new HashMap<>();
        params.put("token_id", tokenId);
        params.put("alias", alias);
        params.put("allow_trade", allowTrade);
        params.put("allow_withdraw", allowWithdraw);
        params.put("allow_accept_by_id", allowAcceptById);
        jdbcTemplate.update(sql, params);
    }

    @Override
    public void deactivateToken(Long tokenId) {
        String sql = "UPDATE OPEN_API_USER_TOKEN SET is_active = 0 WHERE id = :token_id ";
        jdbcTemplate.update(sql, Collections.singletonMap("token_id", tokenId));
    }


}
