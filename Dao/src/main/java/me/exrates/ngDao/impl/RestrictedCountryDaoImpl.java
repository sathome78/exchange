package me.exrates.ngDao.impl;

import me.exrates.model.RestrictedCountry;
import me.exrates.model.enums.RestrictedOperation;
import me.exrates.ngDao.RestrictedCountryDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Repository
public class RestrictedCountryDaoImpl implements RestrictedCountryDao {

    private final NamedParameterJdbcOperations masterOperations;
    private final NamedParameterJdbcOperations slaveOperations;

    @Autowired
    public RestrictedCountryDaoImpl(@Qualifier("masterTemplate") NamedParameterJdbcOperations masterOperations,
                                    @Qualifier("slaveTemplate") NamedParameterJdbcOperations slaveOperations) {
        this.masterOperations = masterOperations;
        this.slaveOperations = slaveOperations;
    }

    @Override
    public RestrictedCountry save(RestrictedCountry restrictedCountry) {
        String sql = "INSERT INTO " + TABLE_NAME + " ( + " + String.join(", ", COLUMN_RESTRICTED_OP_NAME,
                COLUMN_COUNTRY_NAME, COLUMN_COUNTRY_CODE) + ") VALUE "
                + "(:opName, :opId, :countryName, :countryCode) "
                + "ON DUPLICATE KEY UPDATE "
                + COLUMN_RESTRICTED_OP_NAME + ":opName, "
                + COLUMN_COUNTRY_NAME + ":countryName, "
                + COLUMN_COUNTRY_CODE + ":countryCode, ";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("opName", restrictedCountry.getOperation().name())
                .addValue("countryName", restrictedCountry.getCountryName())
                .addValue("countryCode", restrictedCountry.getCountryCode());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        masterOperations.update(sql, params, keyHolder);
        if (restrictedCountry.getId() != 0) {
            restrictedCountry.setId(keyHolder.getKey().intValue());
        }
        return restrictedCountry;
    }

    @Override
    public Optional<RestrictedCountry> findById(int restrictedCountryId) {
        try {
            String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = :id";
            MapSqlParameterSource params = new MapSqlParameterSource("id", restrictedCountryId);
            return Optional.ofNullable(slaveOperations.queryForObject(sql, params, getRowMapper()));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Set<RestrictedCountry> findAll(RestrictedOperation operation) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_RESTRICTED_OP_NAME + " = :opName";
        MapSqlParameterSource params = new MapSqlParameterSource("opName", operation.name());
        return new HashSet<>(slaveOperations.query(sql, params, getRowMapper()));
    }

    @Override
    public Set<RestrictedCountry> findAll() {
        String sql = "SELECT * FROM " + TABLE_NAME;
        return new HashSet<>(slaveOperations.query(sql, Collections.emptyMap(), getRowMapper()));
    }

    @Override
    public boolean delete(int restrictedCountryId) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = :id ";
        MapSqlParameterSource params = new MapSqlParameterSource("id", restrictedCountryId);
        return masterOperations.update(sql, params) > 0;
    }

    @Override
    public boolean delete(RestrictedOperation operation, String countryCode) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_RESTRICTED_OP_NAME + " = :opName "
                + " AND " + COLUMN_COUNTRY_CODE + " = :code";
        MapSqlParameterSource params = new MapSqlParameterSource("opName", operation.name())
                .addValue("code", countryCode);
        return masterOperations.update(sql, params) > 0;
    }

    private RowMapper<RestrictedCountry> getRowMapper() {
        return (rs, i) -> RestrictedCountry.builder()
                .id(rs.getInt(COLUMN_ID))
                .countryCode(rs.getString(COLUMN_COUNTRY_CODE))
                .countryName(rs.getString(COLUMN_COUNTRY_NAME))
                .operation(RestrictedOperation.valueOf(rs.getString(COLUMN_RESTRICTED_OP_NAME)))
                .build();
    }
}
