package me.exrates.dao.impl;

import me.exrates.dao.CommissionDao;
import me.exrates.model.Commission;
import me.exrates.model.dto.CommissionShortEditDto;
import me.exrates.model.dto.EditMerchantCommissionDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;

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
	NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	public Commission getCommission(OperationType operationType, UserRole userRole) {
		final String sql = "SELECT COMMISSION.id, COMMISSION.operation_type, COMMISSION.date, COMMISSION.value " +
				"FROM COMMISSION " +
				"WHERE operation_type = :operation_type AND user_role = :role_id";
		final HashMap<String,Integer> params = new HashMap<>();
		params.put("operation_type",operationType.type);
		params.put("role_id", userRole.getRole());
		return jdbcTemplate.queryForObject(sql,params, commissionRowMapper);
	}

	@Override
	public Commission getDefaultCommission(OperationType operationType) {
		final String sql = "SELECT id, operation_type, date, value " +
				"FROM COMMISSION " +
				"WHERE operation_type = :operation_type AND user_role = 4;";
		final HashMap<String,Integer> params = new HashMap<>();
		params.put("operation_type",operationType.type);
		return jdbcTemplate.queryForObject(sql,params,commissionRowMapper);
	}


	@Override
	public BigDecimal getCommissionMerchant(String merchant, String currency, OperationType operationType) {
		String selectedField = operationType == OperationType.INPUT ? "merchant_input_commission" : "merchant_output_commission";


		final String sql = "SELECT " + selectedField + " FROM birzha.MERCHANT_CURRENCY " +
				"where merchant_id = (select id from MERCHANT where name = :merchant) \n" +
				"and currency_id = (select id from CURRENCY where name = :currency)";
		final HashMap<String, String> params = new HashMap<>();
		params.put("currency", currency);
		params.put("merchant", merchant);

		return BigDecimal.valueOf(jdbcTemplate.queryForObject(sql, params, Double.class));
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
	public void updateMerchantCurrencyCommission(EditMerchantCommissionDto editMerchantCommissionDto){
		final String sql = "UPDATE MERCHANT_CURRENCY SET merchant_input_commission = :input_value, " +
				"merchant_output_commission = :output_value, merchant_fixed_commission = :fixed_commision " +
				"where merchant_id = :merchant_id AND currency_id = :currency_id";
		Map<String, Number> params = new HashMap<String, Number>() {{
			put("merchant_id", editMerchantCommissionDto.getMerchantId());
			put("currency_id", editMerchantCommissionDto.getCurrencyId());
			put("input_value", editMerchantCommissionDto.getInputValue());
			put("output_value", editMerchantCommissionDto.getOutputValue());
			put("fixed_commision", editMerchantCommissionDto.getMinFixedAmount());
		}};
		jdbcTemplate.update(sql, params);
	}

	@Override
	public BigDecimal getMinFixedCommission(String merchant, String currency) {
		final String sql = "SELECT merchant_fixed_commission FROM birzha.MERCHANT_CURRENCY " +
				"where merchant_id = (select id from MERCHANT where name = :merchant) \n" +
				"and currency_id = (select id from CURRENCY where name = :currency)";
		final HashMap<String, String> params = new HashMap<>();
		params.put("currency", currency);
		params.put("merchant", merchant);
		return BigDecimal.valueOf(jdbcTemplate.queryForObject(sql, params, Double.class));
	}


}
