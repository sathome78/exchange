package me.exrates.dao.impl;

import me.exrates.dao.CommissionDao;
import me.exrates.dao.exception.notfound.CommissionsNotFoundException;
import me.exrates.model.Commission;
import me.exrates.model.dto.ComissionCountDto;
import me.exrates.model.dto.CommissionShortEditDto;
import me.exrates.model.dto.EditMerchantCommissionDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static me.exrates.model.enums.OrderStatus.CLOSED;

@Repository
public class CommissionDaoImpl implements CommissionDao {

    @Autowired
    private MessageSource messageSource;

    private static final RowMapper<Commission> commissionRowMapper = (resultSet, i) -> {
        Commission commission = new Commission();
        commission.setDateOfChange(resultSet.getDate("date"));
        commission.setId(resultSet.getInt("id"));
        commission.setOperationType(OperationType.convert(resultSet.getInt("operation_type")));
        commission.setValue(resultSet.getBigDecimal("value"));
        return commission;
    };


    @Autowired
    @Qualifier(value = "masterTemplate")
    NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Commission getCommission(OperationType operationType, UserRole userRole) {
        final String sql = "SELECT COMMISSION.id, COMMISSION.operation_type, COMMISSION.date, COMMISSION.value " +
                "FROM COMMISSION " +
                "WHERE operation_type = :operation_type AND user_role = :role_id";

        final HashMap<String, Integer> params = new HashMap<>();
        params.put("operation_type", operationType.type);
        params.put("role_id", userRole.getRole());

        try {
            return jdbcTemplate.queryForObject(sql, params, commissionRowMapper);
        } catch (Exception ex) {
            throw new CommissionsNotFoundException("Commission not found");
        }
    }

    @Override
    public Commission getCommission(OperationType operationType, Integer userId) {
        final String sql = "SELECT COMMISSION.id, COMMISSION.operation_type, COMMISSION.date, COMMISSION.value " +
                "FROM COMMISSION " +
                "JOIN USER ON USER.id = :user_id " +
                "WHERE operation_type = :operation_type AND user_role = USER.roleid";

        final HashMap<String, Integer> params = new HashMap<>();
        params.put("operation_type", operationType.type);
        params.put("user_id", userId);

        return jdbcTemplate.queryForObject(sql, params, commissionRowMapper);
    }

    @Override
    public Commission getDefaultCommission(OperationType operationType) {
        final String sql = "SELECT id, operation_type, date, value " +
                "FROM COMMISSION " +
                "WHERE operation_type = :operation_type AND user_role = 4";

        final HashMap<String, Integer> params = new HashMap<>();
        params.put("operation_type", operationType.type);

        try {
            return jdbcTemplate.queryForObject(sql, params, commissionRowMapper);
        } catch (Exception ex) {
            throw new CommissionsNotFoundException("Commission not found");
        }
    }

    @Override
    public BigDecimal getCommissionMerchant(String merchant, String currency, OperationType operationType) {
        String selectedField = resolveCommissionDataField(operationType);
        final String sql = "SELECT " + selectedField + " FROM birzha.MERCHANT_CURRENCY " +
                "where merchant_id = (select id from MERCHANT where name = :merchant) \n" +
                "and currency_id = (select id from CURRENCY where name = :currency)";
        final HashMap<String, String> params = new HashMap<>();
        params.put("currency", currency);
        params.put("merchant", merchant);
        return BigDecimal.valueOf(jdbcTemplate.queryForObject(sql, params, Double.class));
    }

    @Override
    public BigDecimal getCommissionMerchant(Integer merchantId, Integer currencyId, OperationType operationType) {
        String selectedField = resolveCommissionDataField(operationType);
        final String sql = "SELECT " + selectedField + " FROM MERCHANT_CURRENCY " +
                "where merchant_id = :merchant_id " +
                "and currency_id = :currency_id ";
        final HashMap<String, Object> params = new HashMap<>();
        params.put("currency_id", currencyId);
        params.put("merchant_id", merchantId);
        return BigDecimal.valueOf(jdbcTemplate.queryForObject(sql, params, Double.class));
    }

    private String resolveCommissionDataField(OperationType operationType) {
        String selectedField;
        switch (operationType) {
            case INPUT: {
                selectedField = "merchant_input_commission";
                break;
            }
            case OUTPUT: {
                selectedField = "merchant_output_commission";
                break;
            }
            case USER_TRANSFER: {
                selectedField = "merchant_transfer_commission";
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid operation type: " + operationType);
            }
        }
        return selectedField;
    }

    @Override
    public List<Commission> getEditableCommissions() {
        final String sql = "SELECT COMMISSION.id, COMMISSION.operation_type, COMMISSION.value, " +
                "COMMISSION.date, USER_ROLE.name AS user_role_name " +
                "FROM COMMISSION " +
                "JOIN USER_ROLE ON COMMISSION.user_role = USER_ROLE.id " +
                "WHERE COMMISSION.operation_type NOT IN (5, 6, 7, 8) " +
                "ORDER BY COMMISSION.id";
        return jdbcTemplate.query(sql, (resultSet, i) -> {
            Commission commission = new Commission();
            commission.setOperationType(OperationType.convert(resultSet.getInt("operation_type")));
            commission.setValue(resultSet.getBigDecimal("value"));
            return commission;
        });
    }

    @Override
    public List<CommissionShortEditDto> getEditableCommissionsByRoles(List<Integer> roleIds, Locale locale) {
        final String sql = "SELECT DISTINCT COMMISSION.operation_type, COMMISSION.value " +
                "FROM COMMISSION " +
                "JOIN USER_ROLE ON COMMISSION.user_role = USER_ROLE.id " +
                "WHERE COMMISSION.user_role IN(:roles) AND COMMISSION.operation_type NOT IN (5, 6, 7, 8) " +
                "ORDER BY COMMISSION.operation_type";
        Map<String, List<Integer>> params = Collections.singletonMap("roles", roleIds);
        return jdbcTemplate.query(sql, params, (resultSet, i) -> {
            CommissionShortEditDto commission = new CommissionShortEditDto();
            OperationType operationType = OperationType.convert(resultSet.getInt("operation_type"));
            commission.setOperationType(operationType);
            commission.setOperationTypeLocalized(operationType.toString(messageSource, locale));
            commission.setValue(resultSet.getBigDecimal("value"));
            return commission;
        });
    }


    @Override
    public void updateCommission(Integer id, BigDecimal value) {
        final String sql = "UPDATE COMMISSION SET value = :value, date = NOW() where id = :id";
        Map<String, Number> params = new HashMap<String, Number>() {{
            put("id", id);
            put("value", value);
        }};
        jdbcTemplate.update(sql, params);
    }

    @Override
    public void updateCommission(OperationType operationType, List<Integer> roleIds, BigDecimal value) {
        final String sql = "UPDATE COMMISSION SET value = :value, date = NOW() " +
                "where operation_type = :operation_type AND user_role IN (:user_roles)";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("operation_type", operationType.getType());
            put("user_roles", roleIds);
            put("value", value);
        }};
        jdbcTemplate.update(sql, params);
    }

    @Override
    public void updateMerchantCurrencyCommission(EditMerchantCommissionDto editMerchantCommissionDto) {
        final String sql = "UPDATE MERCHANT_CURRENCY " +
                "SET merchant_input_commission = :input_value," +
                " merchant_output_commission = :output_value," +
                " merchant_transfer_commission = :transfer_value," +
                " merchant_fixed_commission = :fixed_commission," +
                " merchant_fixed_commission_usd = :fixed_commission_usd " +
                "WHERE merchant_id = (SELECT id FROM MERCHANT WHERE name = :merchant_name) " +
                "AND currency_id = (SELECT id FROM CURRENCY WHERE name = :currency_name)";

        Map<String, Object> params = new HashMap<String, Object>() {{
            put("merchant_name", editMerchantCommissionDto.getMerchantName());
            put("currency_name", editMerchantCommissionDto.getCurrencyName());
            put("input_value", editMerchantCommissionDto.getInputValue());
            put("output_value", editMerchantCommissionDto.getOutputValue());
            put("transfer_value", editMerchantCommissionDto.getTransferValue());
            put("fixed_commission", editMerchantCommissionDto.getMinFixedAmount());
            put("fixed_commission_usd", editMerchantCommissionDto.getMinFixedAmountUSD());
        }};

        jdbcTemplate.update(sql, params);
    }

    @Override
    public BigDecimal getMinFixedCommission(Integer currencyId, Integer merchantId) {
        final String sql = "SELECT merchant_fixed_commission FROM MERCHANT_CURRENCY " +
                "where merchant_id = :merchant " +
                "and currency_id = :currency ";
        final HashMap<String, Object> params = new HashMap<>();
        params.put("currency", currencyId);
        params.put("merchant", merchantId);
        return BigDecimal.valueOf(jdbcTemplate.queryForObject(sql, params, Double.class));
    }

    @Override
    public Commission getCommissionById(Integer commissionId) {
        final String sql = "SELECT COMMISSION.id, COMMISSION.operation_type, COMMISSION.date, COMMISSION.value " +
                " FROM COMMISSION " +
                " WHERE id = :id";
        final HashMap<String, Integer> params = new HashMap<>();
        params.put("id", commissionId);
        return jdbcTemplate.queryForObject(sql, params, commissionRowMapper);
    }

    @Override
    public List<ComissionCountDto> countComissinsByPeriod(LocalDateTime from, LocalDateTime to) {
        final String sql = "SELECT C.name, " +
                "       SUM(IF(TX.source_type='WITHDRAW', TX.commission_amount, 0)) as withdraw, " +
                "       SUM(IF(TX.source_type='REFILL', TX.commission_amount, 0)) as refill, " +
                "       SUM(IF(TX.source_type='USER_TRANSFER', TX.commission_amount, 0)) as transfer, " +
                "       SUM(IF(TX.source_type='ORDER', TX.commission_amount, 0)) as trade, " +
                "       SUM(IF(TX.source_type='REFERRAL', TX.amount, 0)) as referral, " +
                "       SUM(IF(TX.source_type='ACCRUAL', TX.amount, 0)) as total_withdrawed, " +
                "       (SELECT commission_balance FROM COMPANY_WALLET WHERE currency_id = TX.currency_id) as commission_balance " +
                "FROM TRANSACTION TX " +
                "JOIN CURRENCY C ON C.id = TX.currency_id " +
                "WHERE TX.datetime between :start_date and :end_date " +
                "GROUP BY TX.currency_id";

        Map<String, Object> params = new HashMap<>();
        params.put("start_date", from);
        params.put("end_date", to);
        return jdbcTemplate.query(sql, params, (resultSet, i) -> {
            ComissionCountDto dto = new ComissionCountDto();
            dto.setWithdrawComission(resultSet.getBigDecimal("withdraw"));
            dto.setTradeComission(resultSet.getBigDecimal("trade"));
            dto.setTransferComission(resultSet.getBigDecimal("transfer"));
            dto.setRefillComission(resultSet.getBigDecimal("refill"));
            dto.setReferralPayments(resultSet.getBigDecimal("referral"));
            dto.setName(resultSet.getString("name"));
            dto.setCommissionBalance(resultSet.getBigDecimal("commission_balance"));
            dto.setTotalWithdrawed(resultSet.getBigDecimal("total_withdrawed"));
            dto.countTotal();
            return dto;
        });
    }
}
