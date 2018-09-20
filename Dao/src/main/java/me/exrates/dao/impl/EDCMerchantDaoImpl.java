package me.exrates.dao.impl;

import me.exrates.dao.EDCMerchantDao;
import me.exrates.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class EDCMerchantDaoImpl implements EDCMerchantDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final Logger LOG = LogManager.getLogger("merchant");

    public EDCMerchantDaoImpl(@Qualifier(value = "masterTemplate")final NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void createAddress(String address, User user) {
        final String sql = "INSERT INTO EDC_MERCHANT_ACCOUNT (address, user_id)" +
                " VALUES (:address, :user_id)";
        final Map<String, Object> params = new HashMap<>();
        params.put("address", address);
        params.put("user_id", user.getId());
        jdbcTemplate.update(sql, params);
    }

    @Override
    public boolean checkMerchantTransactionIdIsEmpty(String merchantTransactionId) {
        final String sql = "select count(transaction_id) from  EDC_MERCHANT_TRANSACTION WHERE merchant_transaction_id = :merchantTransactionId";
        final Map<String, Object> params = new HashMap<>();
        params.put("merchantTransactionId", merchantTransactionId);

        try {
            return jdbcTemplate.queryForObject(sql, params, Integer.class) == 0;
        } catch (Exception e) {
            LOG.error(e);
        return false;
        }

    }

    @Override
    public String findUserEmailByAddress(String address){
        final String sql = "select USER.email from  EDC_MERCHANT_ACCOUNT join USER ON(EDC_MERCHANT_ACCOUNT.user_id = USER.id) " +
                "WHERE EDC_MERCHANT_ACCOUNT.address = :address limit 1";
        final Map<String, String> params = new HashMap<>();
        params.put("address", address);

        return jdbcTemplate.queryForObject(sql, params, String.class);
    }

    @Override
    public void createMerchantTransaction(String address, String merchantTransactionId, Integer transactionId) {
        final String sql = "INSERT INTO EDC_MERCHANT_TRANSACTION (merchant_transaction_id, transaction_id, address)" +
                " VALUES (:merchantTransactionId, :transactionId, :address)";
        final Map<String, Object> params = new HashMap<>();
        params.put("address", address);
        params.put("merchantTransactionId", merchantTransactionId);
        params.put("transactionId", transactionId);
        jdbcTemplate.update(sql, params);
    }
    
    @Override
    public Optional<String> getAddressForUser(Integer userId) {
        String sql = "SELECT address FROM EDC_MERCHANT_ACCOUNT WHERE user_id = :user_id LIMIT 1";
        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, Collections.singletonMap("user_id", userId), String.class));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
