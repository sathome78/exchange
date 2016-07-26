package me.exrates.dao.impl;

import me.exrates.dao.MerchantDao;
import me.exrates.model.Merchant;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.MerchantImage;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.util.BigDecimalProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Repository
public class MerchantDaoImpl implements MerchantDao {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Merchant create(Merchant merchant) {
        final String sql = "INSERT INTO MERCHANT (description, name) VALUES (:description,:name)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("description",merchant.getDescription())
            .addValue("name",merchant.getName());
        if (jdbcTemplate.update(sql, params, keyHolder)>0) {
            merchant.setId(keyHolder.getKey().intValue());
            return merchant;
        }
        return null;
    }

    @Override
    public Merchant findById(int id) {
        final String sql = "SELECT * FROM MERCHANT WHERE id = :id";
        final Map<String, Integer> params = new HashMap<String,Integer>(){
            {
                put("id", id);
            }
        };
        return jdbcTemplate.queryForObject(sql,params,new BeanPropertyRowMapper<>(Merchant.class));
    }


    @Override
    public List<Merchant> findAllByCurrency(int currencyId) {
        final String sql = "SELECT * FROM MERCHANT WHERE id in (SELECT merchant_id FROM MERCHANT_CURRENCY WHERE currency_id = :currencyId)";
        Map<String, Integer> params = new HashMap<String,Integer>() {
            {
                put("currencyId", currencyId);
            }
        };
        try {
            return jdbcTemplate.query(sql, params, (resultSet, i) -> {
                Merchant merchant = new Merchant();
                merchant.setDescription(resultSet.getString("description"));
                merchant.setId(resultSet.getInt("id"));
                merchant.setName(resultSet.getString("name"));
                return merchant;
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public BigDecimal getMinSum(int merchant, int currency) {
        final String sql = "SELECT min_sum FROM MERCHANT_CURRENCY WHERE merchant_id = :merchant AND currency_id = :currency";
        final Map<String, Integer> params = new HashMap<String,Integer>(){
            {
                put("merchant", merchant);
                put("currency", currency);
            }
        };
        return jdbcTemplate.queryForObject(sql,params,BigDecimal.class);
    }

    @Override
    public List<MerchantCurrency> findAllByCurrencies(List<Integer> currenciesId) {
        final String sql = "SELECT MERCHANT.id as merchant_id,MERCHANT.name,MERCHANT.description,MERCHANT_CURRENCY.min_sum," +
                " MERCHANT_CURRENCY.currency_id FROM MERCHANT JOIN MERCHANT_CURRENCY" +
                " ON MERCHANT.id = MERCHANT_CURRENCY.merchant_id WHERE MERCHANT_CURRENCY.currency_id in (:currenciesId)";
        try {
            return jdbcTemplate.query(sql, Collections.singletonMap("currenciesId",currenciesId), (resultSet, i) -> {
                MerchantCurrency merchantCurrency = new MerchantCurrency();
                merchantCurrency.setMerchantId(resultSet.getInt("merchant_id"));
                merchantCurrency.setName(resultSet.getString("name"));
                merchantCurrency.setDescription(resultSet.getString("description"));
                merchantCurrency.setMinSum(resultSet.getBigDecimal("min_sum"));
                merchantCurrency.setCurrencyId(resultSet.getInt("currency_id"));
                final String sqlInner = "SELECT * FROM birzha.MERCHANT_IMAGE where merchant_id = :merchant_id" +
                        " AND currency_id = :currency_id;";
                Map<String, Integer> params = new HashMap<String, Integer>();
                params.put("merchant_id", resultSet.getInt("merchant_id"));
                params.put("currency_id", resultSet.getInt("currency_id"));
                merchantCurrency.setListMerchantImage(jdbcTemplate.query(sqlInner, params, new BeanPropertyRowMapper<>(MerchantImage.class)));

                return merchantCurrency;
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

    }

    @Override
    public List<MyInputOutputHistoryDto> getMyInputOutputHistory(String email, Integer offset, Integer limit, Locale locale) {
        String sql = " select TRANSACTION.datetime, CURRENCY.name as currency, TRANSACTION.amount, TRANSACTION.commission_amount, \n" +
                "case when OPERATION_TYPE.name = 'input' or WITHDRAW_REQUEST.merchant_image_id is null then\n" +
                "MERCHANT.name else\n" +
                "MERCHANT_IMAGE.image_name end as merchant,\n" +
                "OPERATION_TYPE.name as operation_type, TRANSACTION.id, TRANSACTION.provided from TRANSACTION \n" +
                "left join CURRENCY on TRANSACTION.currency_id=CURRENCY.id\n" +
                "left join WITHDRAW_REQUEST on TRANSACTION.id=WITHDRAW_REQUEST.transaction_id\n" +
                "left join MERCHANT_IMAGE on WITHDRAW_REQUEST.merchant_image_id=MERCHANT_IMAGE.id\n" +
                "left join MERCHANT on TRANSACTION.merchant_id = MERCHANT.id \n" +
                "left join OPERATION_TYPE on TRANSACTION.operation_type_id=OPERATION_TYPE.id\n" +
                "left join WALLET on TRANSACTION.user_wallet_id=WALLET.id\n" +
                "left join USER on WALLET.user_id=USER.id\n" +
                "where TRANSACTION.source_type=:source_type and USER.email=:email group by datetime DESC" +
                (limit == -1 ? "" : "  LIMIT " + limit + " OFFSET " + offset);
        final Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        params.put("source_type", TransactionSourceType.MERCHANT.toString());
        return jdbcTemplate.query(sql, params, new RowMapper<MyInputOutputHistoryDto>() {
            @Override
            public MyInputOutputHistoryDto mapRow(ResultSet rs, int i) throws SQLException {
                MyInputOutputHistoryDto myInputOutputHistoryDto = new MyInputOutputHistoryDto();
                myInputOutputHistoryDto.setDatetime(rs.getTimestamp("datetime").toLocalDateTime());
                myInputOutputHistoryDto.setCurrencyName(rs.getString("currency"));
                myInputOutputHistoryDto.setAmount(BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount"), locale, 2));
                myInputOutputHistoryDto.setCommissionAmount(BigDecimalProcessing.formatLocale(rs.getBigDecimal("commission_amount"), locale, 2));
                myInputOutputHistoryDto.setMerchantName(rs.getString("merchant"));
                myInputOutputHistoryDto.setOperationType(rs.getString("operation_type"));
                myInputOutputHistoryDto.setTransactionId(rs.getInt("id"));
                myInputOutputHistoryDto.setTransactionProvided(rs.getInt("provided") == 0 ? "false" : "true");
                return myInputOutputHistoryDto;
            }
        });
    }
}