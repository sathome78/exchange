package me.exrates.dao.impl;

import me.exrates.dao.BTCTransactionDao;
import me.exrates.dao.UserDao;
import me.exrates.model.BTCTransaction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Repository
public class BTCTransactionDaoImpl implements BTCTransactionDao {

    private final Logger LOG = LogManager.getLogger("merchant");

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    private UserDao userDao;

    private final RowMapper<BTCTransaction> btcTransactionRowMapper = (resultSet, i) -> {
        BTCTransaction btcTransaction = new BTCTransaction();
        btcTransaction.setHash(resultSet.getString("hash"));
        btcTransaction.setAmount(resultSet.getBigDecimal("amount"));
        btcTransaction.setTransactionId(resultSet.getInt("transaction_id"));
        Timestamp acceptanceTimeResult = resultSet.getTimestamp("acceptance_time");
        LocalDateTime acceptanceTime = acceptanceTimeResult == null ? null : acceptanceTimeResult.toLocalDateTime();
        btcTransaction.setAcceptance_time(acceptanceTime);
        int acceptance_user_id = resultSet.getInt("acceptance_user_id");
        if (acceptance_user_id != 0){
            btcTransaction.setAcceptanceUser(userDao.getUserById(resultSet.getInt("acceptance_user_id")));
        }

        return btcTransaction;
    };

    @Override
    public final BTCTransaction create(BTCTransaction btcTransaction) {
        final String sql = "INSERT INTO BTC_TRANSACTION (hash, amount, transaction_id, acceptance_user_id) VALUES (:hash, :amount, :transactionId, :acceptance_user_id)";
        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("hash", btcTransaction.getHash());
                put("amount", btcTransaction.getAmount());
                put("transactionId", btcTransaction.getTransactionId());
                if (btcTransaction.getAcceptanceUser() != null){
                    put("acceptance_user_id", btcTransaction.getAcceptanceUser().getId());
                }else {
                    put("acceptance_user_id", null);
                }
            }
        };
        return jdbcTemplate.update(sql, params) > 0 ? btcTransaction : null;

    }

    @Override
    public final BTCTransaction findByTransactionId(int transactionId) {
        final String sql = "SELECT * FROM BTC_TRANSACTION WHERE transaction_id = :transactionId";
        final Map<String, Integer> params = Collections.singletonMap("transactionId", transactionId);
        try {
            return jdbcTemplate.queryForObject(sql, params, btcTransactionRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public final boolean delete(int transactionId) {
        final String sql = "DELETE FROM BTC_TRANSACTION WHERE transaction_id = :transactionId";
        final Map<String, Integer> params = Collections.singletonMap("transactionId", transactionId);
        return jdbcTemplate.update(sql,params) > 0;
    }
}