package me.exrates.dao.impl;

import me.exrates.dao.AdGroupDao;
import me.exrates.model.merchants.AdGroupTx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class AdGroupDaoImpl implements AdGroupDao {

    RowMapper<AdGroupTx> adGroupTxRowMapper = (rs, rowNum) -> {
        AdGroupTx adGroupTx = new AdGroupTx();
        adGroupTx.setId(rs.getInt("id"));
        adGroupTx.setRefillRequestId(rs.getInt("refill_request_id"));
        adGroupTx.setUserId(rs.getInt("user_id"));
        adGroupTx.setStatus(rs.getString("status"));
        adGroupTx.setTx(rs.getString("tx"));
        adGroupTx.setTime(rs.getTimestamp("date"));
        return adGroupTx;
    };

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate masterTemplate;

    @Autowired
    @Qualifier(value = "slaveTemplate")
    private NamedParameterJdbcTemplate slaveTemplate;

    @Override
    public AdGroupTx save(AdGroupTx adGroupTx) {
        String sql = "INSERT INTO MERCHANT_ADGROUP_TX (refill_request_id, user_id, tx, status) VALUES (:refill_request_id, :user_id, :tx, :status)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("refill_request_id", adGroupTx.getRefillRequestId());
        params.addValue("user_id", adGroupTx.getUserId());
        params.addValue("tx", adGroupTx.getTx());
        params.addValue("status", adGroupTx.getStatus());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        masterTemplate.update(sql, params, keyHolder);
        int id = keyHolder.getKey().intValue();
        adGroupTx.setTime(new Date());
        adGroupTx.setId(id);
        return adGroupTx;
    }

    @Override
    public List<AdGroupTx> findByStatus(String status) {
        String sql = "SELECT * FROM MERCHANT_ADGROUP_TX WHERE status = :status";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("status", status);
        return slaveTemplate.query(sql, params, adGroupTxRowMapper);
    }

    @Override
    public boolean deleteTxById(int id) {
        String sql = "DELETE FROM MERCHANT_ADGROUP_TX WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        return masterTemplate.update(sql, params) > 0;
    }

    @Override
    public boolean deleteTxByTx(String tx) {
        String sql = "DELETE FROM MERCHANT_ADGROUP_TX WHERE tx = :tx";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("tx", tx);
        return masterTemplate.update(sql, params) > 0;
    }
}
