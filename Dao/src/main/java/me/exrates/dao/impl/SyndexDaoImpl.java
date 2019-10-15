package me.exrates.dao.impl;


import me.exrates.dao.SyndexDao;
import me.exrates.model.dto.SyndexOrderDto;
import me.exrates.model.enums.SyndexOrderStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.isNull;

@Repository
public class SyndexDaoImpl implements SyndexDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final NamedParameterJdbcTemplate slaveNamedParameterJdbcTemplate;

    @Autowired
    public SyndexDaoImpl(@Qualifier(value = "masterTemplate") NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                         @Qualifier(value = "slaveTemplate") NamedParameterJdbcTemplate slaveNamedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.slaveNamedParameterJdbcTemplate = slaveNamedParameterJdbcTemplate;
    }

    @Override
    public void saveOrder(SyndexOrderDto orderDto) {
        final String sql = "INSERT INTO SYNDEX_ORDER " +
                "(refill_request_id, user_id, syndex_id, amount, status_id, commission, payment_system_id, currency, country_id, payment_details) " +
                "values (:refill_request_id, :user_id, :syndex_id, :amount, :status_id, :commission, :payment_system_id, :currency, :country_id, :payment_details)";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("user_id", orderDto.getUserId())
                .addValue("refill_request_id", orderDto.getId())
                .addValue("amount", orderDto.getAmount())
                .addValue("commission", orderDto.getCommission())
                .addValue("country_id", orderDto.getCountryId())
                .addValue("currency", orderDto.getCurrency())
                .addValue("payment_details", orderDto.getPaymentDetails())
                .addValue("status_id", orderDto.getStatus().getStatusId())
                .addValue("syndex_id", orderDto.getSyndexId())
                .addValue("payment_system_id", orderDto.getPaymentSystemId());

        if (namedParameterJdbcTemplate.update(sql, parameters, new GeneratedKeyHolder()) < 1) {
            throw new RuntimeException("Order not saved");
        }
    }

    @Override
    public void updateStatus(int refillRequestId, int newStatus) {
        final String sql = "UPDATE SYNDEX_ORDER " +
                "SET status_id = :status, modification_date = NOW() " +
                "WHERE refill_request_id = :refill_request_id";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("refill_request_id", refillRequestId)
                .addValue("status", newStatus);

        if (namedParameterJdbcTemplate.update(sql, parameters) < 1) {
            throw new RuntimeException("Order not updated");
        }
    }

    @Override
    public void updatePaymentDetails(int refillRequestId, String details) {
        final String sql = "UPDATE SYNDEX_ORDER " +
                "SET payment_details = :details, modification_date = NOW() " +
                "WHERE refill_request_id = :refill_request_id";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("refill_request_id", refillRequestId)
                .addValue("details", details);

        if (namedParameterJdbcTemplate.update(sql, parameters) < 1) {
            throw new RuntimeException("Order not updated");
        }
    }

    @Override
    public void updateSyndexId(int refillRequestId, long syndexId) {
        final String sql = "UPDATE SYNDEX_ORDER " +
                "SET syndex_id = :syndex_id " +
                "WHERE refill_request_id = :refill_request_id";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("refill_request_id", refillRequestId)
                .addValue("syndex_id", syndexId);

        if (namedParameterJdbcTemplate.update(sql, parameters) < 1) {
            throw new RuntimeException("Order not updated");
        }
    }

    @Override
    public void setConfirmed(int refillRequestId) {
        final String sql = "UPDATE SYNDEX_ORDER " +
                "SET confirmed = TRUE " +
                "WHERE refill_request_id = :refill_request_id ";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("refill_request_id", refillRequestId);

        if (namedParameterJdbcTemplate.update(sql, parameters) < 1) {
            throw new RuntimeException("Order not confirmed");
        }
    }

    @Override
    public List<SyndexOrderDto> getAllorders(@Nullable List<Integer> statuses, @Nullable Integer userId) {
        final String sql = "SELECT * FROM SYNDEX_ORDER " +
                           " WHERE 1 " +
                            (isNull(statuses) ? "" : " and status_id IN (:statuses)") +
                            (isNull(userId) ? "" : " AND user_id = :user_id");

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("statuses", statuses)
                .addValue("user_id", userId);

        try {
            return slaveNamedParameterJdbcTemplate.query(sql, parameters, rowMapper);
        } catch (DataAccessException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public SyndexOrderDto getById(int id, @Nullable Integer userId) {
        final String sql = "SELECT * FROM SYNDEX_ORDER" +
                           " WHERE refill_request_id = :id " +
                           (isNull(userId) ? "" : " AND user_id = :user_id");

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("user_id", userId);

        try {
            return namedParameterJdbcTemplate.queryForObject(sql, parameters, rowMapper);
        } catch (DataAccessException e) {
            throw new RuntimeException("Syndex order not found");
        }
    }

    @Override
    public SyndexOrderDto getByIdForUpdate(int id, int userId) {
        final String sql = "SELECT * FROM SYNDEX_ORDER" +
                " WHERE refill_request_id = :id " +
                " AND user_id = :user_id " +
                " FOR UPDATE ";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("user_id", userId);

        try {
            return namedParameterJdbcTemplate.queryForObject(sql, parameters, rowMapper);
        } catch (DataAccessException e) {
            throw new RuntimeException("Syndex order not found");
        }
    }

    @Override
    public SyndexOrderDto getBySyndexId(long id) {
        final String sql = "SELECT * FROM SYNDEX_ORDER" +
                " WHERE syndex_id = :syndex_id ";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("syndex_id", id);

        try {
            return namedParameterJdbcTemplate.queryForObject(sql, parameters, rowMapper);
        } catch (DataAccessException e) {
            throw new RuntimeException("Syndex order not found");
        }
    }

    @Override
    public SyndexOrderDto getBySyndexIdForUpdate(long id) {
        final String sql = "SELECT * FROM SYNDEX_ORDER" +
                " WHERE syndex_id = :syndex_id " +
                " FOR UPDATE ";

        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("syndex_id", id);

        try {
            return namedParameterJdbcTemplate.queryForObject(sql, parameters, rowMapper);
        } catch (DataAccessException e) {
            throw new RuntimeException("Syndex order not found");
        }
    }

    private RowMapper<SyndexOrderDto> rowMapper = (rs, rowNum) ->
            SyndexOrderDto.builder()
            .id(rs.getInt("refill_request_id"))
            .amount(rs.getBigDecimal("amount"))
            .commission(rs.getBigDecimal("commission"))
            .status(SyndexOrderStatusEnum.convert(rs.getInt("status_id")))
            .syndexId(rs.getLong("syndex_id"))
            .currency(rs.getString("currency"))
            .countryId(rs.getString("country_id"))
            .paymentSystemId(rs.getString("payment_system_id"))
            .paymentDetails(rs.getString("payment_details"))
            .isConfirmed(rs.getBoolean("confirmed"))
            .userId(rs.getInt("user_id"))
            .lastModifDate(rs.getTimestamp("modification_date").toLocalDateTime())
            .build();

}
