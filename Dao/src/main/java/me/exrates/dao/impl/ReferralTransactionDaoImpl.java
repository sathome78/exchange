package me.exrates.dao.impl;

import me.exrates.dao.ReferralTransactionDao;
import me.exrates.model.referral.ReferralTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ReferralTransactionDaoImpl implements ReferralTransactionDao {

    private final NamedParameterJdbcTemplate masterJdbcTemplate;
    private final NamedParameterJdbcTemplate slaveJdbcTemplate;

    private final RowMapper<ReferralTransaction> referralTransactionRowMapper = (rs, row) -> {
        ReferralTransaction referralTransaction = new ReferralTransaction();
        referralTransaction.setId(rs.getInt("id"));
        referralTransaction.setUserId(rs.getInt("user_id"));
        referralTransaction.setAmount(rs.getBigDecimal("amount"));
        referralTransaction.setCurrencyId(rs.getInt("currency_id"));
        referralTransaction.setCurrencyName(rs.getString("currency_name"));
        referralTransaction.setLink(rs.getString("link"));
        return referralTransaction;
    };

    @Autowired
    public ReferralTransactionDaoImpl(NamedParameterJdbcTemplate masterJdbcTemplate,
                                      NamedParameterJdbcTemplate slaveJdbcTemplate) {
        this.masterJdbcTemplate = masterJdbcTemplate;
        this.slaveJdbcTemplate = slaveJdbcTemplate;
    }

    @Override
    public boolean createReferralTransaction(ReferralTransaction referralTransaction) {
        final String sql = "INSERT INTO REFERRAL_TRANSACTION(currency_id, currency_name, user_id, amount, link) " +
                "VALUES (:currency_id, :currency_name, :user_id, :amount, :link)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("currency_id", referralTransaction.getCurrencyId())
                .addValue("currency_name", referralTransaction.getCurrencyName())
                .addValue("user_id", referralTransaction.getUserId())
                .addValue("amount", referralTransaction.getAmount())
                .addValue("link", referralTransaction.getLink());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        masterJdbcTemplate.update(sql, params, keyHolder);
        referralTransaction.setId((int) keyHolder.getKey().longValue());
        return referralTransaction.getId() != null;
    }

    @Override
    public List<ReferralTransaction> findTransactionByUserIdAndLink(int userId, String link) {
        final String sql = "SELECT * FROM REFERRAL_TRANSACTION WHERE user_id = :user_id AND link = :link";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("user_id", userId);
            put("link", link);
        }};
        return slaveJdbcTemplate.query(sql, params, referralTransactionRowMapper);
    }
}
