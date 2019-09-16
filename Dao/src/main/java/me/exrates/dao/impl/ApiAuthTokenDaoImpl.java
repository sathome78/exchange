package me.exrates.dao.impl;

import lombok.RequiredArgsConstructor;
import me.exrates.dao.ApiAuthTokenDao;
import me.exrates.dao.rowmappers.ApiAuthTokenRowMapper;
import me.exrates.model.ApiAuthToken;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.singletonMap;

@Repository
@RequiredArgsConstructor
public class ApiAuthTokenDaoImpl implements ApiAuthTokenDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public long createToken(ApiAuthToken token) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("username", token.getUsername())
                .addValue("value", token.getValue())
                .addValue("expired_at", token.getExpiredAt());

        int result = namedParameterJdbcTemplate.update(INSERT_API_AUTH_TOKEN, params, keyHolder);
        long id = keyHolder.getKey().longValue();
        if (result <= 0) {
            id = 0;
        }
        return id;
    }

    @Override
    public Optional<ApiAuthToken> retrieveTokenById(Long id) {
        Map<String, Long> params = singletonMap("id", id);
        try {
            return Optional.of(namedParameterJdbcTemplate.queryForObject(SELECT_TOKEN_BY_ID, params, new ApiAuthTokenRowMapper()));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean deleteExpiredToken(Long id) {
        String sql = "DELETE FROM API_AUTH_TOKEN WHERE id = :id";
        Map<String, Object> params = singletonMap("id", id);
        return namedParameterJdbcTemplate.update(sql, params) == 1;
    }

    @Override
    public int deleteAllExpired() {
        String sql = "DELETE FROM API_AUTH_TOKEN WHERE expired_at < now()";
        return namedParameterJdbcTemplate.update(sql, Collections.emptyMap());
    }

    @Override
    public boolean deleteAllByUsername(String username) {
        final String sql = "DELETE FROM API_AUTH_TOKEN WHERE username = :username";

        return namedParameterJdbcTemplate.update(sql, Collections.singletonMap("username", username)) > 0;
    }

    @Override
    public boolean deleteAllExceptCurrent(Long tokenId, String username) {
        final String sql = "DELETE FROM API_AUTH_TOKEN WHERE id NOT IN (:ids) AND username = :username";

        final Map<String, Object> params = new HashMap<>();
        params.put("ids", Collections.singletonList(tokenId));
        params.put("username", username);

        return namedParameterJdbcTemplate.update(sql, params) > 0;
    }

    @Override
    public boolean updateExpiration(Long tokenId, Date expiredAt) {
        final String sql = "UPDATE API_AUTH_TOKEN SET expired_at = :expired_at WHERE id = :id";

        final Map<String, Object> params = new HashMap<>();
        params.put("id", tokenId);
        params.put("expired_at", expiredAt);

        return namedParameterJdbcTemplate.update(sql, params) > 0;
    }
}