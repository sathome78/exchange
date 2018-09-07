package me.exrates.dao.impl;

import me.exrates.dao.EDCAccountDao;
import me.exrates.model.EDCAccount;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonMap;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Repository
public class EDCAccountDaoImpl implements EDCAccountDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final Logger LOG = LogManager.getLogger("merchant");

    public EDCAccountDaoImpl(@Qualifier(value = "masterTemplate")final NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public EDCAccount findByTransactionId(int id) {
        final String sql = "SELECT * FROM EDC_TEMP_ACCOUNT WHERE transaction_id = :transactionId";
        final Map<String, Integer> params = singletonMap("transactionId", id);
        return jdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(EDCAccount.class));
    }

    @Override
    public void deleteByTransactionId(int id) {
        final String sql = "DELETE FROM EDC_TEMP_ACCOUNT WHERE transaction_id = :transactionId";
        final Map<String, Integer> params = singletonMap("transactionId", id);
        try {
            jdbcTemplate.update(sql, params);
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    @Override
    public void create(EDCAccount edcAccount) {
        final String sql = "INSERT INTO EDC_TEMP_ACCOUNT (transaction_id, wif_priv_key, pub_key, brain_priv_key, account_name)" +
                " VALUES (:transactionId, :wifPrivKey, :pubKey, :brainPrivKey, :accountName)";
        final Map<String, Object> params = new HashMap<>();
        params.put("transactionId", edcAccount.getTransactionId());
        params.put("wifPrivKey", edcAccount.getWifPrivKey());
        params.put("pubKey", edcAccount.getPubKey());
        params.put("brainPrivKey", edcAccount.getBrainPrivKey());
        params.put("accountName", edcAccount.getAccountName());
        jdbcTemplate.update(sql, params);
    }

    @Override
    public void setAccountIdByTransactionId(int transactionId, String accountId) {
        final String sql = "UPDATE EDC_TEMP_ACCOUNT set account_id = :accountId WHERE transaction_id = :transactionId";
        final Map<String, Object> params = new HashMap<>();
        params.put("transactionId", transactionId);
        params.put("perfectmoney.accountId", accountId);

        try {
            jdbcTemplate.update(sql, params);
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    @Override
    public List<EDCAccount> getAccountsWithoutId() {
        final String sql = "SELECT * FROM EDC_TEMP_ACCOUNT WHERE account_name <> '' AND account_id is null";

        return jdbcTemplate.query(sql, new BeanPropertyRowMapper(EDCAccount.class));
    }

    @Override
    public List<EDCAccount> getUnusedAccounts() {
        final String sql = "SELECT * FROM EDC_TEMP_ACCOUNT WHERE used = 0" +
                " AND account_id is not null AND account_id <> '' order by transaction_id desc limit 200";

        return jdbcTemplate.query(sql, new BeanPropertyRowMapper(EDCAccount.class));
    }

    @Override
    public void setAccountUsed(int transactionId) {
        final String sql = "UPDATE EDC_TEMP_ACCOUNT set used = 1 " +
                "where transaction_id = :transactionId";

        final Map<String, Object> params = new HashMap<>();
        params.put("transactionId", transactionId);
         try {
            jdbcTemplate.update(sql, params);
        } catch (Exception e) {
            LOG.error(e);
        }
    }
}
