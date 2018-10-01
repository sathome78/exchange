package me.exrates.dao.impl;

import me.exrates.dao.EthereumNodeDao;
import me.exrates.model.EthereumAccount;
import me.exrates.model.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class EthereumNodeDaoImpl implements EthereumNodeDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final Logger LOG = LogManager.getLogger("node_ethereum");

    public EthereumNodeDaoImpl(@Qualifier(value = "masterTemplate")final NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void createAddress(EthereumAccount ethereumAccount, String merchant) {
        final String sql = "INSERT INTO ETHEREUM_TEMP_ACCOUNT (address, user_id, private_key, public_key, merchant_id)" +
                " VALUES (:address, :user_id, :private_key, :public_key, (select id from MERCHANT where name = :merchant))";
        final Map<String, Object> params = new HashMap<>();
        params.put("address", ethereumAccount.getAddress());
        params.put("user_id", ethereumAccount.getUser().getId());
        params.put("private_key", ethereumAccount.getPrivateKey());
        params.put("public_key", ethereumAccount.getPublicKey());
        params.put("merchant", merchant);
        jdbcTemplate.update(sql, params);
    }

    @Override
    public Optional<EthereumAccount> findByAddress(String address, String merchant){
        final String sql = "select ETHEREUM_TEMP_ACCOUNT.*, USER.id, USER.email from  ETHEREUM_TEMP_ACCOUNT join USER ON(ETHEREUM_TEMP_ACCOUNT.user_id = USER.id) " +
                "WHERE ETHEREUM_TEMP_ACCOUNT.address = :address  " +
                "AND ETHEREUM_TEMP_ACCOUNT.merchant_id = (select id from MERCHANT where name = :merchant) limit 1";
        final Map<String, String> params = new HashMap<>();
        params.put("address", address);
        params.put("merchant", merchant);

        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, params, (resultSet, row) -> {
                EthereumAccount ethereumAccount = new EthereumAccount();
                ethereumAccount.setAddress(resultSet.getString("address"));
                User user = new User();
                user.setId(resultSet.getInt("user_id"));
                user.setEmail(resultSet.getString("email"));
                ethereumAccount.setUser(user);
                ethereumAccount.setPrivateKey(new BigInteger(resultSet.getString("private_key")));
                ethereumAccount.setPublicKey(new BigInteger(resultSet.getString("public_key")));
                return ethereumAccount;
            }));

        }catch (EmptyResultDataAccessException e){
            return Optional.empty();
        }
    }

    @Override
    public List<String> findAllAddresses(String merchant) {
        final String sql = "SELECT ETHEREUM_TEMP_ACCOUNT.address FROM ETHEREUM_TEMP_ACCOUNT " +
                "where merchant_id = (select id from MERCHANT where name = :merchant)";

        final Map<String, String> params = new HashMap<>();
        params.put("merchant", merchant);

        return jdbcTemplate.query(sql, params, (rs, row) -> rs.getString("address"));
    }

    @Override
    public String findUserEmailByAddress(String address, String merchant){
        final String sql = "select USER.email from  ETHEREUM_TEMP_ACCOUNT join USER ON(ETHEREUM_TEMP_ACCOUNT.user_id = USER.id) " +
                "WHERE ETHEREUM_TEMP_ACCOUNT.address = :address " +
                "AND ETHEREUM_TEMP_ACCOUNT.merchant_id = (select id from MERCHANT where name = :merchant) limit 1";
        final Map<String, String> params = new HashMap<>();
        params.put("address", address);
        params.put("merchant", merchant);

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
    public List<String> findPendingTransactions(String merchant) {
        final String sql = "SELECT EDC_MERCHANT_TRANSACTION.merchant_transaction_id FROM EDC_MERCHANT_TRANSACTION " +
                "JOIN TRANSACTION ON (TRANSACTION.id = EDC_MERCHANT_TRANSACTION.transaction_id) " +
                "where TRANSACTION.merchant_id = (select id from MERCHANT where MERCHANT.name = :merchant) AND " +
                "TRANSACTION.provided = 0";

        final Map<String, String> params = new HashMap<>();
        params.put("merchant", merchant);

        return jdbcTemplate.query(sql, params, (rs, row) -> rs.getString("merchant_transaction_id"));
    }

    @Override
    public Integer findTransactionId(String merchantTransactionId, String merchant){
        final String sql = "select EDC_MERCHANT_TRANSACTION.transaction_id from  EDC_MERCHANT_TRANSACTION " +
                "join ETHEREUM_TEMP_ACCOUNT ON (EDC_MERCHANT_TRANSACTION.address = ETHEREUM_TEMP_ACCOUNT.address) \n" +
                "where EDC_MERCHANT_TRANSACTION.merchant_transaction_id = :merchant_transaction_id\n" +
                "AND ETHEREUM_TEMP_ACCOUNT.merchant_id = (select id from MERCHANT where name = :merchant)";
        final Map<String, String> params = new HashMap<>();
        params.put("merchant_transaction_id", merchantTransactionId);
        params.put("merchant", merchant);

        return jdbcTemplate.queryForObject(sql, params, Integer.class);
    }

    @Override
    public boolean isMerchantTransactionExists(String merchantTransactionId, String merchant){
        final String sql = "select COUNT(EDC_MERCHANT_TRANSACTION.transaction_id) from  EDC_MERCHANT_TRANSACTION\n" +
                "join ETHEREUM_TEMP_ACCOUNT ON (EDC_MERCHANT_TRANSACTION.address = ETHEREUM_TEMP_ACCOUNT.address) \n" +
                "where EDC_MERCHANT_TRANSACTION.merchant_transaction_id = :merchant_transaction_id\n" +
                "AND ETHEREUM_TEMP_ACCOUNT.merchant_id = (select id from MERCHANT where name = :merchant)";
        final Map<String, String> params = new HashMap<>();
        params.put("merchant_transaction_id", merchantTransactionId);
        params.put("merchant", merchant);

        if (jdbcTemplate.queryForObject(sql, params, Integer.class) > 0){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public String findAddressByMerchantTransactionId(String merchantTransactionId, String merchant){
        final String sql = "select EDC_MERCHANT_TRANSACTION.address from  EDC_MERCHANT_TRANSACTION " +
                "join ETHEREUM_TEMP_ACCOUNT ON (EDC_MERCHANT_TRANSACTION.address = ETHEREUM_TEMP_ACCOUNT.address) \n" +
                "where EDC_MERCHANT_TRANSACTION.merchant_transaction_id = :merchant_transaction_id\n" +
                "AND ETHEREUM_TEMP_ACCOUNT.merchant_id = (select id from MERCHANT where name = :merchant)";
        final Map<String, String> params = new HashMap<>();
        params.put("merchant_transaction_id", merchantTransactionId);
        params.put("merchant", merchant);

        return jdbcTemplate.queryForObject(sql, params, String.class);
    }

}
