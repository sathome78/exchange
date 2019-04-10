package me.exrates.dao.impl;

import lombok.extern.log4j.Log4j;
import me.exrates.dao.IEOClaimRepository;
import me.exrates.model.IEOClaim;
import me.exrates.model.IEOResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Log4j
public class IEOClaimRepositoryImpl implements IEOClaimRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public IEOClaimRepositoryImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public IEOClaim save(IEOClaim ieoClaim) {
        final String sql = "INSERT INTO IEO_CLAIM (currency_name, ieo_id, maker_id, user_id, amount, rate, price_in_btc) " +
                "VALUES (:currency_name, :ieo_id, :maker_id, :user_id, :amount, :rate, :price_in_btc)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("currency_name", ieoClaim.getCurrencyName())
                .addValue("ieo_id", ieoClaim.getIeoId())
                .addValue("maker_id", ieoClaim.getMakerId())
                .addValue("user_id", ieoClaim.getUserId())
                .addValue("rate", ieoClaim.getRate())
                .addValue("price_in_btc", ieoClaim.getPriceInBtc())
                .addValue("amount", ieoClaim.getAmount());
        if (jdbcTemplate.update(sql, params, keyHolder) > 0) {
            ieoClaim.setId(keyHolder.getKey().intValue());
            return ieoClaim;
        }
        return null;
    }

    @Override
    public Collection<IEOClaim> findUnprocessedIeoClaims() {
        final String sql = "SElECT * FROM IEO_CLAIM WHERE status = :status";
        MapSqlParameterSource params = new MapSqlParameterSource("status", IEOResult.IEOResultStatus.NONE.name());
        return jdbcTemplate.query(sql, params, ieoClaimRowMapper());
    }

    @Override
    public boolean updateStatusIEOClaim(int claimId, IEOResult.IEOResultStatus status) {
        String sql = "UPDATE IEO_CLAIM SET status = :status WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("status", status.name());
        params.put("id", claimId);
        return jdbcTemplate.update(sql, params) > 0;
    }

    @Override
    public Collection<Integer> getAllSuccessClaimIdsByIeoId(int ieoId) {
        String sql = "SElECT id FROM IEO_CLAIM WHERE status = :status AND ieo_id = :ieoId FOR UPDATE";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("status", IEOResult.IEOResultStatus.SUCCESS.name());
        params.addValue("ieoId", ieoId);
        return jdbcTemplate.queryForList(sql, params, Integer.class);
    }

    @Override
    public List<IEOClaim> getClaimsByIds(List<Integer> ids) {
        String sql = "SElECT * FROM IEO_CLAIM WHERE id in (:ids)";
        MapSqlParameterSource params = new MapSqlParameterSource("ids", ids);
        return jdbcTemplate.query(sql, params, ieoClaimRowMapper());
    }

    private RowMapper<IEOClaim> ieoClaimRowMapper() {
        return (rs, row) -> {
            IEOClaim ieoClaim = new IEOClaim();
            ieoClaim.setId(rs.getInt("id"));
            ieoClaim.setIeoId(rs.getInt("ieo_id"));
            ieoClaim.setCurrencyName(rs.getString("currency_name"));
            ieoClaim.setMakerId(rs.getInt("maker_id"));
            ieoClaim.setUserId(rs.getInt("user_id"));
            ieoClaim.setAmount(rs.getBigDecimal("amount"));
            ieoClaim.setRate(rs.getBigDecimal("rate"));
            ieoClaim.setPriceInBtc(rs.getBigDecimal("price_in_btc"));
            ieoClaim.setCreated(rs.getDate("created"));
            ieoClaim.setStatus(IEOResult.IEOResultStatus.valueOf(rs.getString("status")));
            return ieoClaim;
        };
    }
}
