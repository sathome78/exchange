package me.exrates.dao.order;

import org.springframework.jdbc.core.RowMapper;

public final class TokenRowMapper {

    public static final RowMapper<String> tokenRowMapper = (resultSet, i) ->
            resultSet.getString("access_token");
}