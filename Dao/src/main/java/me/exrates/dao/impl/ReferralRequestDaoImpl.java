package me.exrates.dao.impl;

import me.exrates.dao.ReferralRequestDao;
import me.exrates.model.referral.ReferralRequest;
import me.exrates.model.referral.enums.ReferralProcessStatus;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ReferralRequestDaoImpl implements ReferralRequestDao {

    private static final RowMapper<ReferralRequest> requestRowMapper = (rs, row) -> {
        ReferralRequest request = new ReferralRequest();
        request.setId(rs.getInt("id"));
        request.setUserId(rs.getInt("user_id"));
        request.setAmount(rs.getBigDecimal("amount"));
        request.setCurrencyId(rs.getInt("currency_id"));
        request.setOrderId(rs.getInt("order_id"));
        request.setProcessStatus(ReferralProcessStatus.valueOf(rs.getString("process_status")));
        return request;
    };

    private final NamedParameterJdbcTemplate masterJdbcTemplate;
    private final NamedParameterJdbcTemplate slaveJdbcTemplate;

    public ReferralRequestDaoImpl(NamedParameterJdbcTemplate masterJdbcTemplate,
                                  NamedParameterJdbcTemplate slaveJdbcTemplate) {
        this.masterJdbcTemplate = masterJdbcTemplate;
        this.slaveJdbcTemplate = slaveJdbcTemplate;
    }

    @Override
    public void saveReferralRequestsBatch(final List<ReferralRequest> requests) {
        List<MapSqlParameterSource> batchArgs = new ArrayList<>();
        for (ReferralRequest request : requests) {
            MapSqlParameterSource parameters = new MapSqlParameterSource();
            parameters.addValue("currency_id", request.getCurrencyId());
            parameters.addValue("user_id", request.getUserId());
            parameters.addValue("amount", request.getAmount());
            parameters.addValue("order_id", request.getOrderId());
            parameters.addValue("process_status", request.getProcessStatus());
            batchArgs.add(parameters);
        }

        final String sql = "INSERT INTO REFERRAL_REQUESTS(currency_id, user_id, amount, order_id, process_status) " +
                "VALUES (:currency_id, :user_id, :amount, :order_id, :process_status)";
        masterJdbcTemplate.batchUpdate(sql, batchArgs.toArray(new MapSqlParameterSource[requests.size()]));
    }

    @Override
    public List<ReferralRequest> getReferralRequestsByStatus(int chunk, ReferralProcessStatus status) {
        final String sql = "SELECT * " +
                "FROM REFERRAL_REQUESTS WHERE process_status = :status ORDER BY created_at ASC LIMIT " + chunk;
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("status", status);
        }};
        return slaveJdbcTemplate.query(sql, params, requestRowMapper);
    }

    @Override
    public boolean updateStatusReferralRequest(int id, ReferralProcessStatus status) {
        final String sql = "UPDATE REFERRAL_REQUESTS set process_status = :status WHERE id = :id ";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("status", status);
            put("id", id);
        }};
        return masterJdbcTemplate.update(sql, params) > 0;
    }
}
