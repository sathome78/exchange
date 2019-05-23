package me.exrates.dao.impl;

import lombok.RequiredArgsConstructor;
import me.exrates.dao.ApiAuthTokenDao;
import me.exrates.dao.rowmappers.ApiAuthTokenRowMapper;
import me.exrates.model.ApiAuthToken;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.singletonMap;

@Repository
@RequiredArgsConstructor
public class ApiAuthTokenDaoImpl implements ApiAuthTokenDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private RowMapper<ApiAuthToken> ROW_MAPPER =
            (rs, i) -> new ApiAuthToken(rs.getLong(1), rs.getString(2), rs.getString(3), LocalDateTime.now());

    @Override
    public long createToken(ApiAuthToken token) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("username", token.getUsername())
                .addValue("value", token.getValue());
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

    public boolean prolongToken(Long id) {
        String sql = "UPDATE API_AUTH_TOKEN SET last_request = CURRENT_TIMESTAMP WHERE id = :id";
        Map<String, Object> params = singletonMap("id", id);
        return namedParameterJdbcTemplate.update(sql, params) == 1;
    }

    @Override
    public boolean deleteExpiredToken(Long id) {
        String sql = "DELETE FROM API_AUTH_TOKEN WHERE id = :id";
        Map<String, Object> params = singletonMap("id", id);
        return namedParameterJdbcTemplate.update(sql, params) == 1;
    }

    @Override
    public int deleteAllExpired(long tokenDuration) {
        String sql = "DELETE FROM API_AUTH_TOKEN WHERE DATE_ADD(last_request,INTERVAL :duration SECOND) < now()";
        return namedParameterJdbcTemplate.update(sql, singletonMap("duration", tokenDuration));
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
}
