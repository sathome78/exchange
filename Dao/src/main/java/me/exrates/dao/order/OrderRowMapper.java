package me.exrates.dao.order;

import me.exrates.model.ExOrder;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderRowMapper implements RowMapper<ExOrder> {
    public ExOrder mapRow(ResultSet rs, int rowNum) throws SQLException {
        OrderExtractor orderExtractor = new OrderExtractor();
        return orderExtractor.extractData(rs);
    }
}

