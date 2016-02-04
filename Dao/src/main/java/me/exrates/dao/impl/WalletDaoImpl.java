package me.exrates.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import me.exrates.dao.WalletDao;
import me.exrates.model.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class WalletDaoImpl implements WalletDao {

	//private static final Logger logger=Logger.getLogger(WalletDaoImpl.class); 
	@Autowired  
	DataSource dataSource;
	
	public double getWalletABalance(int walletId) {
		String sql = "SELECT active_balance FROM wallet WHERE id = :walletId";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		Map<String, String> namedParameters = new HashMap<>();
		namedParameters.put("walletId", String.valueOf(walletId));
		return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Double.class);
	}
	
	public double getWalletRBalance(int walletId) {
		String sql = "SELECT reserved_balance FROM wallet WHERE id = :walletId";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		Map<String, String> namedParameters = new HashMap<>();
		namedParameters.put("walletId", String.valueOf(walletId));
		return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Double.class);
	}

	@Override
	public boolean setWalletABalance(int walletId, double newBalance) {
		final String sql = "UPDATE WALLET SET active_balance =:newBalance WHERE id =:walletId";
		final Map<String,String> params = new HashMap<String,String>() {
			{
				put("newBalance",String.valueOf(newBalance));
				put("walletId",String.valueOf(walletId));
			}
		};
		return new NamedParameterJdbcTemplate(dataSource).update(sql,params) > 0;
	}

	@Override
	public boolean setWalletRBalance(int walletId,double newBalance) {
		final String sql = "UPDATE WALLET SET reserved_balance =:newBalance WHERE id =:walletId";
		final Map<String,String> params = new HashMap<String,String>() {
			{
				put("newBalance",String.valueOf(newBalance));
				put("walletId",String.valueOf(walletId));
			}
		};
		return new NamedParameterJdbcTemplate(dataSource).update(sql,params) > 0;
	}

	public int getWalletId(int userId, int currencyId) {
		String sql = "SELECT id FROM wallet WHERE user_id = :userId AND currency_id = :currencyId";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		Map<String, String> namedParameters = new HashMap<>();
		namedParameters.put("userId", String.valueOf(userId));
		namedParameters.put("currencyId", String.valueOf(currencyId));
		return namedParameterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
	}
	
	
	public boolean createNewWallet(Wallet wallet) {
		String sql = "INSERT wallet(currency_id,users_id,active_balance) VALUES(:currId,:userId,:activeBalance)";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);		
		Map<String, String> namedParameters = new HashMap<>();
		namedParameters.put("currId", String.valueOf(wallet.getCurrId()));
		namedParameters.put("userId", String.valueOf(wallet.getUserId()));
		namedParameters.put("activeBalance", String.valueOf(wallet.getActiveBalance()));
		return namedParameterJdbcTemplate.update(sql, namedParameters) > 0;
	}

	@Override
	public List<Wallet> getAllWallets(int userId) {
		String sql = "SELECT WALLET.id,WALLET.currency_id,WALLET.user_id,WALLET.active_balance,WALLET.reserved_balance, CURRENCY.name as wallet_name FROM WALLET" +
				"  INNER JOIN CURRENCY On WALLET.currency_id = CURRENCY.id and WALLET.user_id = :userId";
		NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);		
		final Map<String, String> namedParameters = new HashMap<>();
		namedParameters.put("userId", String.valueOf(userId));
		return namedParameterJdbcTemplate.query(sql, namedParameters, (rs, row) -> {
			Wallet wallet = new Wallet();
			wallet.setId(rs.getInt("id"));
			wallet.setCurrId(rs.getInt("currency_id"));
			wallet.setUserId(rs.getInt("user_id"));
			wallet.setActiveBalance(rs.getDouble("active_balance"));
			wallet.setReservedBalance(rs.getDouble("reserved_balance"));
			wallet.setName(rs.getString("wallet_name"));
			return wallet;
        });
	}
}
