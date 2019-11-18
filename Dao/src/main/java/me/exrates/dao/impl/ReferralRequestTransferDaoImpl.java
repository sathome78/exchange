package me.exrates.dao.impl;

import me.exrates.dao.ReferralRequestTransferDao;
import me.exrates.model.referral.ReferralRequestTransfer;
import me.exrates.model.referral.enums.ReferralRequestStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ReferralRequestTransferDaoImpl implements ReferralRequestTransferDao {

    private final NamedParameterJdbcTemplate masterJdbcTemplate;
    private final NamedParameterJdbcTemplate slaveJdbcTemplate;

    private final RowMapper<ReferralRequestTransfer> referralRequestTransferRowMapper = (rs, row) -> {
        ReferralRequestTransfer referralTransaction = new ReferralRequestTransfer();
        referralTransaction.setId(rs.getInt("id"));
        return referralTransaction;
    };

    @Autowired
    public ReferralRequestTransferDaoImpl(NamedParameterJdbcTemplate masterJdbcTemplate,
                                          NamedParameterJdbcTemplate slaveJdbcTemplate) {
        this.masterJdbcTemplate = masterJdbcTemplate;
        this.slaveJdbcTemplate = slaveJdbcTemplate;
    }

    @Override
    public ReferralRequestTransfer createReferralRequestTransfer(ReferralRequestTransfer referralRequestTransfer) {
        final String sql = "INSERT INTO REFERRAL_REQUEST_TRANSFER(currency_id, currency_name, user_id, amount, status, status_modification_date) " +
                "VALUES (:currency_id, :currency_name, :user_id, :amount, :status, :status_modification_date)";

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("currency_id", referralRequestTransfer.getCurrencyId())
                .addValue("currency_name", referralRequestTransfer.getCurrencyName())
                .addValue("user_id", referralRequestTransfer.getUserId())
                .addValue("amount", referralRequestTransfer.getAmount())
                .addValue("status", referralRequestTransfer.getStatus().name())
                .addValue("status_modification_date", referralRequestTransfer.getStatusModificationDate());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        masterJdbcTemplate.update(sql, params, keyHolder);
        referralRequestTransfer.setId((int) keyHolder.getKey().longValue());
        return referralRequestTransfer;
    }

    @Override
    public boolean updateReferralRequestTransfer(ReferralRequestTransfer request) {

        StringBuilder sqlBuilder = new StringBuilder("UPDATE REFERRAL_REQUEST_TRANSFER set status = :status ");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("status", request.getStatus().name());

        if (request.getTransactionId() != null) {
            sqlBuilder.append(", transaction_id = :transaction_id, ");
            params.put("transaction_id", request.getTransactionId());
        }

        if (request.getRemark() != null) {
            sqlBuilder.append(", remark = :remark ");
            params.put("remark", request.getRemark());
        }

        sqlBuilder.append(" WHERE id = :id");
        params.put("id", request.getId());
        return masterJdbcTemplate.update(sqlBuilder.toString(), params) > 0;
    }

    @Override
    public List<ReferralRequestTransfer> findByStatus(List<ReferralRequestStatus> status) {
        final String sql = "SELECT * FROM REFERRAL_REQUEST_TRANSFER WHERE status in (:status) FOR UPDATE";
        return slaveJdbcTemplate.query(sql, Collections.singletonMap("status", status), referralRequestTransferRowMapper);
    }
}
