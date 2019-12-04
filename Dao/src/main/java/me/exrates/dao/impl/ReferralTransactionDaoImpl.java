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

import java.math.BigDecimal;
import java.sql.ResultSet;
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
        referralTransaction.setUserIdFrom(rs.getInt("user_from"));
        referralTransaction.setUserIdTo(rs.getInt("user_to"));
        referralTransaction.setAmount(rs.getBigDecimal("amount"));
        referralTransaction.setCurrencyId(rs.getInt("currency_id"));
        referralTransaction.setCurrencyName(rs.getString("currency_name"));
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
        final String sql = "INSERT INTO REFERRAL_TRANSACTION(currency_id, currency_name, user_id, amount) " +
                "VALUES (:currency_id, :currency_name, :user_id, :amount, :link)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("currency_id", referralTransaction.getCurrencyId())
                .addValue("currency_name", referralTransaction.getCurrencyName())
                .addValue("user_to", referralTransaction.getUserIdTo())
                .addValue("user_from", referralTransaction.getUserIdFrom())
                .addValue("amount", referralTransaction.getAmount());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        masterJdbcTemplate.update(sql, params, keyHolder);
        referralTransaction.setId((int) keyHolder.getKey().longValue());
        return referralTransaction.getId() != null;
    }

    @Override
    public Map<String, BigDecimal> getEarnedByUsersFromAndUserToAndCurrencies(List<Integer> usersFrom, int userTo, List<String> currencies) {
        final String sql = "SELECT sum(referral_transaction.amount) AS sum, referral_transaction.currency_name FROM REFERRAL_TRANSACTION " +
                "WHERE user_to = :user_to AND user_from in (:users_from) AND currency_name in (:currencies) " +
                "GROUP BY referral_transaction.currency_name";

        Map<String, Object> params = new HashMap<String, Object>() {{
            put("user_to", userTo);
            put("users_from", usersFrom);
            put("currencies", currencies);
        }};

        return slaveJdbcTemplate.query(sql, params, (ResultSet rs) -> {
            HashMap<String, BigDecimal> results = new HashMap<>(currencies.size());
            while (rs.next()) {
                results.put(rs.getString("currency_name"), rs.getBigDecimal("sum"));
            }
            return results;
        });
    }
}
