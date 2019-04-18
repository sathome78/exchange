package me.exrates.dao.impl;

import lombok.extern.log4j.Log4j;
import me.exrates.dao.IEOResultRepository;
import me.exrates.model.IEOClaim;
import me.exrates.model.IEOResult;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Repository
@Log4j
public class IEOResultRepositoryImpl implements IEOResultRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public IEOResultRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public IEOResult save(IEOResult ieoResult) {
        final String sql = "INSERT INTO IEO_RESULT (claim_id, ieo_id, status, available_amount) " +
                "VALUES (:claim_id, :ieo_id, :status, :availableAmount)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("claim_id", ieoResult.getClaimId())
                .addValue("ieo_id", ieoResult.getIeoId())
                .addValue("status", ieoResult.getStatus().name())
                .addValue("availableAmount", ieoResult.getAvailableAmount());
        if (jdbcTemplate.update(sql, params) > 0) {
            return ieoResult;
        }
        return null;
    }

    @Override
    public boolean isAlreadyStarted(IEOClaim ieoClaim) {
        String sql = "SELECT COUNT(ieo_id) FROM IEO_CLAIM WHERE ieo_id = :ieoId";
        MapSqlParameterSource params = new MapSqlParameterSource("ieoId", ieoClaim.getIeoId());
        Integer result = jdbcTemplate.queryForObject(sql, params, Integer.class);
        return result != null && result > 0;
    }

    private RowMapper<IEOResult> ieoResultRawMapper() {
        return (rs, i) -> IEOResult.builder()
                .claimId(rs.getInt("claim_id"))
                .ieoId(rs.getInt("ieo_id"))
                .availableAmount(rs.getBigDecimal("available_amount"))
                .status(IEOResult.IEOResultStatus.valueOf(rs.getString("status")))
                .build();
    }
}
