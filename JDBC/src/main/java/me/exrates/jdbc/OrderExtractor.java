package me.exrates.jdbc;

import me.exrates.model.ExOrder;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderBaseType;
import me.exrates.model.enums.OrderStatus;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;


public class OrderExtractor implements ResultSetExtractor<ExOrder> {
    public ExOrder extractData(ResultSet rs) throws SQLException, DataAccessException {
        ExOrder exOrder = new ExOrder();
        exOrder.setId(rs.getInt("id"));
        exOrder.setUserId(rs.getInt("user_id"));
        exOrder.setCurrencyPairId(rs.getInt("currency_pair_id"));
        exOrder.setOperationType(OperationType.convert(rs.getInt("operation_type_id")));
        exOrder.setExRate(rs.getBigDecimal("exrate"));
        exOrder.setAmountBase(rs.getBigDecimal("amount_base"));
        exOrder.setComissionId(rs.getInt("commission_id"));
        exOrder.setAmountConvert(rs.getBigDecimal("amount_convert"));
        exOrder.setCommissionFixedAmount(rs.getBigDecimal("commission_fixed_amount"));
        exOrder.setUserAcceptorId(rs.getInt("user_acceptor_id"));
        exOrder.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
        exOrder.setOrderBaseType(OrderBaseType.valueOf(rs.getString("base_type")));
        LocalDateTime dateAcception = LocalDateTime.MIN;
        if (rs.getTimestamp("date_acception") != null) {
            dateAcception = rs.getTimestamp("date_acception").toLocalDateTime();
        }
        exOrder.setDateAcception(dateAcception);
        exOrder.setStatus(OrderStatus.convert(rs.getInt("status_id")));
        exOrder.setSourceId(rs.getInt("order_source_id"));
        return exOrder;
    }
}
