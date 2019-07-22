package me.exrates.dao.impl;

import com.google.common.collect.Maps;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.QuberaDao;
import me.exrates.dao.exception.notfound.UserNotFoundException;
import me.exrates.model.QuberaUserData;
import me.exrates.model.dto.qubera.QuberaLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Map;

@Repository
@Log4j2
public class QuberaDaoImpl implements QuberaDao {

    private static final String columns = "user_id, currency_id, account_number, iban, address, city, first_name, last_name, country_code, bank_verification_status, reference, birth_day";

    private final NamedParameterJdbcTemplate masterJdbcTemplate;
    private final NamedParameterJdbcTemplate slaveJdbcTemplate;

    private final RowMapper<QuberaUserData> quberaUserDataRowMapper = (rs, row) -> {
        QuberaUserData quberaUserData = new QuberaUserData();
        quberaUserData.setUserId(rs.getInt("user_id"));
        quberaUserData.setAccountNumber(rs.getString("account_number"));
        quberaUserData.setIban(rs.getString("iban"));
        quberaUserData.setCurrencyId(rs.getInt("currency_id"));
        quberaUserData.setFirsName(rs.getObject("first_name") == null ? null : rs.getString("first_name"));
        quberaUserData.setLastName(rs.getObject("last_name") == null ? null : rs.getString("last_name"));
        quberaUserData.setAddress(rs.getObject("address") == null ? null : rs.getString("address"));
        quberaUserData.setCity(rs.getObject("city") == null ? null : rs.getString("city"));
        quberaUserData.setCountryCode(rs.getObject("country_code") == null ? null : rs.getString("country_code"));
        quberaUserData.setReference(rs.getObject("reference") == null ? null : rs.getString("reference"));
        quberaUserData.setBankVerificationStatus(rs.getString("bank_verification_status"));
        quberaUserData.setBirthDay(rs.getTimestamp("birth_day"));
        return quberaUserData;
    };

    @Autowired
    public QuberaDaoImpl(@Qualifier("masterTemplate") NamedParameterJdbcTemplate masterJdbcTemplate,
                         @Qualifier("slaveTemplate") NamedParameterJdbcTemplate slaveJdbcTemplate) {
        this.masterJdbcTemplate = masterJdbcTemplate;
        this.slaveJdbcTemplate = slaveJdbcTemplate;
    }

    @Override
    public boolean saveUserDetails(QuberaUserData userData) {
        String sql = "INSERT INTO QUBERA_USER_DETAILS (user_id, currency_id, address, city, first_name, last_name, country_code, birth_day, reference) "
                + " VALUE (:user_id, :currency_id, :address, :city, :first_name, :last_name, :country_code, :birth_day, :reference) ";
        return masterJdbcTemplate.update(sql, getQuberaUserData(userData)) > 0;
    }

    @Override
    public Map<String, String> getUserDetailsForCurrency(int userId, int currencyId) {
        String sql = "SELECT account_number, iban FROM QUBERA_USER_DETAILS "
                + " WHERE user_id = :user_id AND currency_id = :currency_id;";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", userId);
        params.addValue("currency_id", currencyId);
        try {
            return slaveJdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> {
                Map<String, String> values = Maps.newHashMap();
                values.put("accountNumber", rs.getString("account_number"));
                values.put("iban", rs.getString("iban"));
                return values;
            });
        } catch (EmptyResultDataAccessException e) {
            log.info("Neither iban nor accountNumber for userId: {} and currencyId: {}", userId, currencyId);
            return Collections.emptyMap();
        }
    }

    @Override
    public boolean existAccountByUserEmailAndCurrencyName(String email, String currency) {
        String sql = "SELECT CASE WHEN count(*) > 0" +
                " THEN TRUE ELSE FALSE END" +
                " FROM QUBERA_USER_DETAILS qud" +
                " INNER JOIN USER u on qud.user_id = u.id" +
                " INNER JOIN CURRENCY c on qud.currency_id = c.id" +
                " WHERE u.email = :email AND c.name = :currency";

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("email", email);
        params.addValue("currency", currency);

        return slaveJdbcTemplate.queryForObject(sql, params, Boolean.class);
    }

    @Override
    public String getAccountByUserEmail(String email) {
        String sql = "SELECT account_number" +
                " FROM QUBERA_USER_DETAILS" +
                " INNER JOIN USER u on QUBERA_USER_DETAILS.user_id = u.id" +
                " WHERE u.email = :email";

        return slaveJdbcTemplate.queryForObject(sql, Collections.singletonMap("email", email), String.class);
    }

    @Override
    public QuberaUserData getUserDataByUserIdAndCurrencyId(int userId, int currency_id) {
        String sql = "SELECT " + columns + " FROM QUBERA_USER_DETAILS WHERE user_id = :user_id AND currency_id = :currency_id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", userId);
        params.addValue("currency_id", currency_id);
        return slaveJdbcTemplate.queryForObject(sql, params, quberaUserDataRowMapper);
    }

    @Override
    public QuberaUserData getUserDataByUserEmail(String email) {
        String sql = "SELECT " + columns + " FROM QUBERA_USER_DETAILS " +
                "INNER JOIN USER ON USER.id = QUBERA_USER_DETAILS.user_id " +
                "WHERE USER.email = :email";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("email", email);
        try {
            return slaveJdbcTemplate.queryForObject(sql, params, quberaUserDataRowMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public boolean updateUserData(QuberaUserData quberaUserData) {
        String sql = "UPDATE QUBERA_USER_DETAILS SET currency_id = :currency_id, ";
        if (quberaUserData.getAccountNumber() != null) {
            sql += "account_number = :account_number, ";
        }

        if (quberaUserData.getIban() != null) {
            sql += "iban = :iban, ";
        }

        sql += "address = :address, " +
                "city = :city, " +
                "first_name = :first_name, " +
                "last_name = :last_name, " +
                "country_code = :country_code, ";
        if (quberaUserData.getBankVerificationStatus() != null) {
            sql += "bank_verification_status = :bank_verification_status, ";
        }
        sql += "reference = :reference, " +
                "birth_day = :birth_day " +
                "WHERE user_id = :user_id";
        return masterJdbcTemplate.update(sql, getQuberaUserData(quberaUserData)) > 0;
    }

    @Override
    public QuberaUserData getUserDataByReference(String reference) {
        String sql = "SELECT " + columns + " FROM QUBERA_USER_DETAILS WHERE reference = :reference";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("reference", reference);
        return slaveJdbcTemplate.queryForObject(sql, params, quberaUserDataRowMapper);
    }

    @Override
    public Integer findUserIdByAccountNumber(String accountNumber) {
        String sql = "SELECT qud.user_id FROM QUBERA_USER_DETAILS qud WHERE qud.account_number = :account_number;";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("account_number", accountNumber);
        try {
            return slaveJdbcTemplate.queryForObject(sql, params, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            String message = String.format("User not found for accountNumber: %s", accountNumber);
            log.info(message);
            throw new UserNotFoundException(message);
        }
    }

    @Override
    public boolean logResponse(QuberaLog requestDto) {
        String sql = "INSERT INTO QUBERA_RESPONSE_LOG (paymentId , messageId, accountIBAN, accountNumber, processingTime,"
                + " state, currency, paymentAmount, transferType, rejectionReason) "
                + " VALUES (:paymentId, :messageId, :accountIBAN, :accountNumber,"
                + " :processingTime, :state, :currency, :paymentAmount,"
                + " :transferType, :rejectionReason)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("paymentId", requestDto.getPaymentId());
        params.addValue("messageId", requestDto.getMessageId());
        params.addValue("accountIBAN", requestDto.getAccountIBAN());
        params.addValue("accountNumber", requestDto.getAccountNumber());
        params.addValue("processingTime", Timestamp.valueOf(requestDto.getProcessingTime()));
        params.addValue("state", requestDto.getState());
        params.addValue("currency", requestDto.getCurrency());
        params.addValue("paymentAmount", requestDto.getPaymentAmount());
        params.addValue("transferType", requestDto.getTransferType());
        params.addValue("rejectionReason", requestDto.getRejectionReason());
        return masterJdbcTemplate.update(sql, params) > 0;
    }

    @Override
    public boolean createExternalPaymentLog(QuberaLog requestDto) {
        String sql = "INSERT INTO QUBERA_RESPONSE_LOG(paymentId, accountIBAN, accountNumber, currency, paymentAmount, " +
                "transferType, state) VALUES (:paymentId, :accountIBAN, :accountNumber, :currency, :paymentAmount," +
                ":transferType, :state)";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("paymentId", requestDto.getPaymentId());
        params.addValue("accountIBAN", requestDto.getAccountIBAN());
        params.addValue("accountNumber", requestDto.getAccountNumber());
        params.addValue("currency", requestDto.getCurrency());
        params.addValue("paymentAmount", requestDto.getPaymentAmount());
        params.addValue("transferType", requestDto.getTransferType());
        params.addValue("state", requestDto.getState());
        return masterJdbcTemplate.update(sql, params) > 0;
    }

    @Override
    public boolean updateExternalPaymentLog(int paymentId, QuberaLog.ExternalPaymentState state) {
        String sql = "UPDATE QUBERA_RESPONSE_LOG SET state = :state WEHRE paymentId = :payment_id";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("state", state);
        params.addValue("payment_id", paymentId);
        return masterJdbcTemplate.update(sql, params) > 0;
    }

    private MapSqlParameterSource getQuberaUserData(QuberaUserData userData) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("user_id", userData.getUserId());
        params.addValue("currency_id", userData.getCurrencyId());
        if (userData.getAccountNumber() != null) {
            params.addValue("account_number", userData.getAccountNumber());
        }

        if (userData.getIban() != null) {
            params.addValue("iban", userData.getIban());
        }
        params.addValue("address", userData.getAddress());
        params.addValue("city", userData.getCity());
        params.addValue("first_name", userData.getFirsName());
        params.addValue("last_name", userData.getLastName());
        params.addValue("country_code", userData.getCountryCode());
        params.addValue("birth_day", userData.getBirthDay());
        params.addValue("reference", userData.getReference());
        if (userData.getBankVerificationStatus() != null) {
            params.addValue("bank_verification_status", userData.getBankVerificationStatus());
        }
        return params;
    }
}