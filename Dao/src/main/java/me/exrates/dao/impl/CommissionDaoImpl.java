package me.exrates.dao.impl;

import me.exrates.dao.CommissionDao;
import me.exrates.model.Commission;
import me.exrates.model.enums.OperationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Repository
public class CommissionDaoImpl implements CommissionDao{

	@Autowired  
	NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	public Commission getCommission(OperationType operationType) {
		final String sql = "SELECT * FROM COMMISSION WHERE operation_type = :operationType "
				+ "order by date desc limit 1";
		final HashMap<String,Integer> params = new HashMap<>();
		params.put("operationType",operationType.type);
		return jdbcTemplate.queryForObject(sql,params,(resultSet, i) -> {
			Commission commission = new Commission();
			commission.setDateOfChange(resultSet.getDate("date"));
			commission.setId(resultSet.getInt("id"));
			commission.setOperationType(OperationType.convert(resultSet.getInt("operation_type")));
			commission.setValue(resultSet.getBigDecimal("value"));
			return commission;
		});
	}

	
}
