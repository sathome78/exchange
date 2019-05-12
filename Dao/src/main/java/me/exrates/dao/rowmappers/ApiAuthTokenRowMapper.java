package me.exrates.dao.rowmappers;

import me.exrates.model.ApiAuthToken;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class ApiAuthTokenRowMapper implements RowMapper<ApiAuthToken> {

    public ApiAuthToken mapRow(ResultSet rs, int rowNum) throws SQLException {
        ApiAuthToken token = new ApiAuthToken();
        token.setId(rs.getLong("id"));
        token.setUsername(rs.getString("username"));
        token.setValue(rs.getString("value"));
        token.setExpiredAt(new Date(rs.getTimestamp("expired_at").getTime()));
        return token;
    }
}
