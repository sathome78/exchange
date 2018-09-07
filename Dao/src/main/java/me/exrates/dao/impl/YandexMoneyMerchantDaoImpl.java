package me.exrates.dao.impl;


import me.exrates.dao.YandexMoneyMerchantDao;
import me.exrates.model.Payment;
import me.exrates.model.enums.OperationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

import static me.exrates.jdbc.TokenRowMapper.tokenRowMapper;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Repository
public final class YandexMoneyMerchantDaoImpl implements YandexMoneyMerchantDao {

    @Autowired
    @Qualifier("masterHikariDataSource")
    private DataSource dataSource;

    private static final String YMONEY_TABLE = "YANDEX_MONEY_MERCHANT";

    @Override
    public List<String> getAllTokens() {
        final String sql = "SELECT * FROM " + YMONEY_TABLE;
        return new NamedParameterJdbcTemplate(dataSource).query(sql, tokenRowMapper);
    }

    @Override
    public boolean createToken(String token, int userId) {
        final String sql = "INSERT "+ YMONEY_TABLE +" (user_id, access_token, expiration_date) VALUES (:userId,:accessToken,:expDate)";
        final Map<String,String> params = new HashMap<>();
        params.put("userId",String.valueOf(userId));
        params.put("accessToken",token);
        params.put("expDate",Date.valueOf(LocalDate.now().plusYears(3L)).toString());
        return new NamedParameterJdbcTemplate(dataSource)
                .update(sql, params) > 0;
    }

    @Override
    public String getTokenByUserEmail(String userEmail) {
        final String sql = "SELECT access_token FROM " + YMONEY_TABLE + " WHERE user_id IN " +
                "(SELECT id FROM USER WHERE email=:userEmail)";
        final Map<String, String> params = new HashMap<>();
        params.put("userEmail", userEmail);
        try {
            return new NamedParameterJdbcTemplate(dataSource)
                    .queryForObject(sql, params, tokenRowMapper);

        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public boolean deleteTokenByUserEmail(String userEmail) {
        final String sql = "DELETE FROM "+ YMONEY_TABLE +" WHERE user_id IN " +
                "(SELECT id FROM USER WHERE email=:userEmail)";
        final Map<String,String> params = new HashMap<>();
        params.put("userEmail",userEmail);
        return new NamedParameterJdbcTemplate(dataSource)
                .update(sql,params) > 0;
    }

    @Override
    public boolean updateTokenByUserEmail(String userEmail, String newToken) {
        final String sql = "UPDATE "+ YMONEY_TABLE +" SET access_token=:accessToken where user_id IN " +
                "(SELECT id FROM USER WHERE email=:userEmail)";
        final Map<String,String> params = new HashMap<>();
        params.put("accessToken",newToken);
        params.put("userEmail",userEmail);
        return new NamedParameterJdbcTemplate(dataSource)
                .update(sql,params) > 0;
    }

    @Override
    public int savePayment(Integer currencyId, BigDecimal amount) {
        String sql = "INSERT INTO YANDEX_MONEY_PAYMENT(currency_id, amount) " +
                "VALUES(:currency_id, :amount)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("currency_id", currencyId)
                .addValue("amount", amount);
        int result = new NamedParameterJdbcTemplate(dataSource)
                .update(sql, params, keyHolder);
        int id = (int) keyHolder.getKey().longValue();
        if (result <= 0) {
            id = 0;
        }
        return id;
    }

    @Override
    public Optional<Payment> getPaymentById(Integer id) {
        String sql = "SELECT * FROM YANDEX_MONEY_PAYMENT WHERE id = :id";
        Map<String, Integer> params = Collections.singletonMap("id", id);
        try {
            return Optional.of(new NamedParameterJdbcTemplate(dataSource).queryForObject(sql, params,
                    (rs, row) -> {
                        Payment payment = new Payment();
                        payment.setSum(rs.getBigDecimal("amount").doubleValue());
                        payment.setMerchant(6);
                        payment.setOperationType(OperationType.INPUT);
                        payment.setCurrency(rs.getInt("currency_id"));
                        return payment;
                    }));
        } catch (EmptyResultDataAccessException ex) {
            return Optional.empty();
        }
    }

    @Override
    public void deletePayment(Integer id) {
        String sql = "DELETE FROM YANDEX_MONEY_PAYMENT WHERE id = :id";
        Map<String, Integer> params = Collections.singletonMap("id", id);
        new NamedParameterJdbcTemplate(dataSource)
                .update(sql, params);
    }


}