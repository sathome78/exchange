package me.exrates.dao.impl;

import me.exrates.dao.FreecoinsSettingsRepository;
import me.exrates.model.dto.freecoins.FreecoinsSettingsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class FreecoinsSettingsRepositoryImpl implements FreecoinsSettingsRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    public FreecoinsSettingsRepositoryImpl(@Qualifier(value = "masterTemplate") NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public FreecoinsSettingsDto get(int currencyId) {
        final String sql = "SELECT cur.id AS currency_id, cur.name AS currency_name, fcs.min_amount, fcs.min_partial_amount " +
                "FROM FREE_COINS_SETTINGS fcs " +
                "JOIN CURRENCY cur ON cur.id = fcs.currency_id " +
                "WHERE fcs.currency_id = :currency_id";

        Map<String, Object> params = new HashMap<>();
        params.put("currency_id", currencyId);

        try {
            return jdbcTemplate.queryForObject(sql, params, getSettingsRowMapper());
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public List<FreecoinsSettingsDto> getAll() {
        final String sql = "SELECT cur.id AS currency_id, cur.name AS currency_name, fcs.min_amount, fcs.min_partial_amount " +
                "FROM FREE_COINS_SETTINGS fcs " +
                "JOIN CURRENCY cur ON cur.id = fcs.currency_id";

        return jdbcTemplate.query(sql, Collections.emptyMap(), getSettingsRowMapper());
    }

    @Override
    public boolean set(int currencyId, BigDecimal minAmount, BigDecimal minPartialAmount) {
        final String sql = "UPDATE FREE_COINS_SETTINGS fcs " +
                "SET fcs.min_amount = :min_amount, fcs.min_partial_amount = :min_partial_amount " +
                "WHERE fcs.currency_id = :currency_id";

        Map<String, Object> params = new HashMap<>();
        params.put("currency_id", currencyId);
        params.put("min_amount", minAmount);
        params.put("min_partial_amount", minPartialAmount);

        return jdbcTemplate.update(sql, params) > 0;
    }

    private RowMapper<FreecoinsSettingsDto> getSettingsRowMapper() {
        return (rs, i) -> FreecoinsSettingsDto.builder()
                .currencyId(rs.getInt("currency_id"))
                .currencyName(rs.getString("currency_name"))
                .minAmount(rs.getBigDecimal("min_amount"))
                .minPartialAmount(rs.getBigDecimal("min_partial_amount"))
                .build();
    }
}
