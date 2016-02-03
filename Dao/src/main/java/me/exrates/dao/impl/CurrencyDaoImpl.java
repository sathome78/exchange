package me.exrates.dao.impl;

<<<<<<< HEAD
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import me.exrates.dao.CurrencyDao;
import me.exrates.model.Currency;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;


public class CurrencyDaoImpl implements CurrencyDao{

	//private static final Logger logger=Logger.getLogger(CurrencyDaoImpl.class); 
	@Autowired  
	DataSource dataSource; 
	
	public List<Currency> getCurrList() {
		String sql = "SELECT id, name FROM currency";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);		
		List<Currency> currList = new ArrayList<Currency>();  
		currList = jdbcTemplate.query(sql, new RowMapper<Currency>(){
			public Currency mapRow(ResultSet rs, int row) throws SQLException {
					Currency currency = new Currency();
					currency.setId(rs.getInt("id"));
					currency.setName(rs.getString("name"));
					return currency;

			}
		});
	
=======
import me.exrates.dao.CurrencyDao;
import me.exrates.model.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CurrencyDaoImpl implements CurrencyDao{

	//private static final Logger logger=Logger.getLogger(CurrencyDaoImpl.class); 
	@Autowired
	DataSource dataSource;

	public List<Currency> getCurrList() {
		String sql = "SELECT id, name FROM currency";
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		List<Currency> currList;
		currList = jdbcTemplate.query(sql, (rs, row) -> {
            Currency currency = new Currency();
            currency.setId(rs.getInt("id"));
            currency.setName(rs.getString("name"));
            return currency;

        });
>>>>>>> 04262353b47fdd14c36825d96fcecbda53d964c1
		return currList;
	}

	@Override
	public int getCurrencyId(int walletId) {
		String sql = "SELECT currency_id FROM wallet WHERE id = :walletId ";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		Map<String, String> namedParameters = new HashMap<String, String>();
		namedParameters.put("walletId", String.valueOf(walletId));
		int value = namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
		return value;
	}

	@Override
	public String getCurrencyName(int currencyId) {
		String sql = "SELECT name FROM currency WHERE  id = :id ";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		Map<String, String> namedParameters = new HashMap<String, String>();
		namedParameters.put("id", String.valueOf(currencyId));
		String value = namedParameterJdbcTemplate.queryForObject(sql, namedParameters, String.class);
		return value;
	}

<<<<<<< HEAD
	
}
=======

}
>>>>>>> 04262353b47fdd14c36825d96fcecbda53d964c1
