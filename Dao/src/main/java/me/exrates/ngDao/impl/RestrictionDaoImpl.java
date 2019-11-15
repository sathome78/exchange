package me.exrates.ngDao.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Restriction;
import me.exrates.ngDao.RestrictionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
@Log4j2
public class RestrictionDaoImpl implements RestrictionDao {

    private final NamedParameterJdbcOperations masterOperations;
    private final NamedParameterJdbcOperations slaveOperations;

    @Autowired
    public RestrictionDaoImpl(@Qualifier("masterTemplate") NamedParameterJdbcOperations masterOperations,
                              @Qualifier("slaveTemplate") NamedParameterJdbcOperations slaveOperations) {
        this.masterOperations = masterOperations;
        this.slaveOperations = slaveOperations;
    }

    @Override
    public Restriction save(Restriction restriction) {
        String sql = "INSERT INTO " + TABLE_NAME + " ( + " + String.join(", ", COLUMN_PAIR_NAME, COLUMN_NAME,
                COLUMN_DESCRIPTION, COLUMN_CONDITION, COLUMN_ERROR_CODE, COLUMN_ERROR_MESSAGE) + ") VALUE "
                + "(:pairName, :rName, :rDescription, :rCondition, :errorCode, :errorMessage) "
                + "ON DUPLICATE KEY UPDATE "
                + COLUMN_PAIR_NAME + ":pairName, "
                + COLUMN_NAME + ":rName, "
                + COLUMN_DESCRIPTION + ":rDescription, "
                + COLUMN_CONDITION + ":rCondition, "
                + COLUMN_ERROR_CODE + ":errorCode, "
                + COLUMN_ERROR_MESSAGE + ":errorMessage";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("pairName", restriction.getCurrencyPairName())
                .addValue("rName", restriction.getName())
                .addValue("rDescription", restriction.getDescription())
                .addValue("rCondition", restriction.getCondition())
                .addValue("errorCode", restriction.getErrorCode())
                .addValue("errorMessage", restriction.getErrorMessage());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        masterOperations.update(sql, params, keyHolder);
        if (restriction.getId() != 0) {
            restriction.setId(keyHolder.getKey().intValue());
        }
        return restriction;
    }

    @Override
    public boolean delete(int restrictionId) {
        String sql = "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = :id";
        MapSqlParameterSource params = new MapSqlParameterSource("id", restrictionId);
        return masterOperations.update(sql, params) > 0;
    }

    @Override
    public Optional<Restriction> findById(int restrictionId) {
        try {
            String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = :id";
            MapSqlParameterSource params = new MapSqlParameterSource("id", restrictionId);
            return Optional.ofNullable(slaveOperations.queryForObject(sql, params, getRowMapper()));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Restriction> findAll() {
        String sql = "SELECT * FROM " + TABLE_NAME;
        return slaveOperations.query(sql, Collections.emptyMap(), getRowMapper());
    }

    @Override
    public List<Restriction> findAllByPairName(String pairName) {
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_PAIR_NAME + " = :pairName";
        MapSqlParameterSource params = new MapSqlParameterSource("pairName", pairName);
        return slaveOperations.query(sql, params, getRowMapper());
    }

    @Override
    public boolean matches(String sql) {
        try {
            return slaveOperations.queryForObject(sql, Collections.emptyMap(), Boolean.class);
        } catch (DataAccessException e) {
            log.error("Failed to match condition: " + sql, e);
            return false;
        }
    }

    private RowMapper<Restriction> getRowMapper() {
        return (rs, i) -> Restriction.builder()
                .id(rs.getInt(COLUMN_ID))
                .currencyPairName(rs.getString(COLUMN_PAIR_NAME))
                .name(rs.getString(COLUMN_NAME))
                .description(rs.getString(COLUMN_DESCRIPTION))
                .condition(rs.getString(COLUMN_CONDITION))
                .errorCode(rs.getString(COLUMN_ERROR_CODE))
                .errorMessage(rs.getString(COLUMN_ERROR_MESSAGE))
                .build();
    }

}
