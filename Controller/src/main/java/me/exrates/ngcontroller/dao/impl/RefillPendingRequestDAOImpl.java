package me.exrates.ngcontroller.dao.impl;

import me.exrates.ngcontroller.dao.RefillPendingRequestDAO;
import me.exrates.ngcontroller.model.RefillPendingRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class RefillPendingRequestDAOImpl implements RefillPendingRequestDAO {

    private static final String GET_PENDING_REQUESTS =
            "SELECT rr.id as id, rr.date_creation as date, C.name as currency, rr.amount as amount, stat.name as status, " +
                    "TX.commission_amount as commission, m.description as system, 'REFILL' as operation " +
            "FROM REFILL_REQUEST as rr " +
            "        LEFT JOIN TRANSACTION TX ON (TX.source_id = rr.id AND TX.source_type = 'REFILL') " +
            "        JOIN CURRENCY C on rr.currency_id = C.id " +
            "        JOIN REFILL_REQUEST_STATUS stat on rr.status_id = stat.id " +
            "        JOIN MERCHANT m ON m.id = rr.merchant_id " +
            "WHERE rr.user_id = :user_id AND rr.status_id IN (:refill_statuses) " +
            "UNION ALL " +
            "SELECT WR.id as id, WR.date_creation as date, C2.name as currency, WR.amount as amount, WRS.name as status, " +
                    "WR.commission as commission, m.description as system, 'WITHDRAW' as operation " +
            "FROM  WITHDRAW_REQUEST WR " +
            "        JOIN CURRENCY C2 on WR.currency_id = C2.id " +
            "        JOIN MERCHANT m ON m.id = WR.merchant_id " +
            "        JOIN WITHDRAW_REQUEST_STATUS WRS on WR.status_id = WRS.id " +
            "WHERE WR.user_id =:user_id AND WR.status_id IN (:withdraw_statuses) " +
            "UNION ALL " +
            "SELECT TR.id as id, TR.date_creation as date, C3.name as currency, TR.amount as amount, TRS.name as status, " +
                    "TR.commission as commission, m.description as system, 'TRANSFER' as operation " +
                    "FROM TRANSFER_REQUEST TR " +
                    "        JOIN CURRENCY C3 on TR.currency_id = C3.id " +
                    "        JOIN MERCHANT m ON m.id = TR.merchant_id " +
                    "        JOIN TRANSFER_REQUEST_STATUS TRS on TR.status_id = TRS.id " +
                    "WHERE TR.user_id =:user_id AND TR.status_id IN (:transfer_statuses) ";
    @Autowired
    @Qualifier(value = "slaveTemplate")
    private NamedParameterJdbcTemplate slaveTemplate;

    @Override
    public List<RefillPendingRequestDto> getPendingRefillRequests(long userId, List<Integer> withdrawRequestStatuses,
                                                                  List<Integer> refillRequestStatuses,
                                                                  List<Integer> transferStatuses) {
        if (withdrawRequestStatuses == null || withdrawRequestStatuses.isEmpty()) {
            withdrawRequestStatuses = Collections.singletonList(-1);
        }
        if (refillRequestStatuses == null || refillRequestStatuses.isEmpty()) {
            refillRequestStatuses = Collections.singletonList(-1);
        }
        MapSqlParameterSource sqlParameterSource = new MapSqlParameterSource();
        sqlParameterSource.addValue("user_id", userId);
        sqlParameterSource.addValue("refill_statuses", refillRequestStatuses);
        sqlParameterSource.addValue("withdraw_statuses", withdrawRequestStatuses);
        sqlParameterSource.addValue("transfer_statuses", transferStatuses);
        return slaveTemplate.query(GET_PENDING_REQUESTS, sqlParameterSource, getRefillPendingRequestDtoRowMapper());
    }

    private RowMapper<RefillPendingRequestDto> getRefillPendingRequestDtoRowMapper() {
        return (rs, rowNum) -> RefillPendingRequestDto
                .builder()
                .requestId(rs.getInt("id"))
                .date(rs.getString("date"))
                .currency(rs.getString("currency"))
                .amount(rs.getDouble("amount"))
                .status(rs.getString("status"))
                .commission(rs.getDouble("commission"))
                .system(rs.getString("system"))
                .operation(rs.getString("operation"))
                .build();
    }

}
