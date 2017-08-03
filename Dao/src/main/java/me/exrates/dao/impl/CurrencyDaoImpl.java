package me.exrates.dao.impl;

import me.exrates.dao.CurrencyDao;
import me.exrates.model.Currency;
import me.exrates.model.CurrencyLimit;
import me.exrates.model.CurrencyPair;
import me.exrates.model.dto.CurrencyPairLimitDto;
import me.exrates.model.dto.MerchantCurrencyScaleDto;
import me.exrates.model.dto.UserCurrencyOperationPermissionDto;
import me.exrates.model.dto.mobileApiDto.TransferLimitDto;
import me.exrates.model.dto.mobileApiDto.dashboard.CurrencyPairWithLimitsDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserCommentTopicEnum;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.invoice.InvoiceOperationDirection;
import me.exrates.model.enums.invoice.InvoiceOperationPermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CurrencyDaoImpl implements CurrencyDao {

  @Autowired
  private NamedParameterJdbcTemplate jdbcTemplate;

  protected static RowMapper<CurrencyPair> currencyPairRowMapper = (rs, row) -> {
    CurrencyPair currencyPair = new CurrencyPair();
    currencyPair.setId(rs.getInt("id"));
    currencyPair.setName(rs.getString("name"));
      /**/
    Currency currency1 = new Currency();
    currency1.setId(rs.getInt("currency1_id"));
    currency1.setName(rs.getString("currency1_name"));
    currencyPair.setCurrency1(currency1);
      /**/
    Currency currency2 = new Currency();
    currency2.setId(rs.getInt("currency2_id"));
    currency2.setName(rs.getString("currency2_name"));
    currencyPair.setCurrency2(currency2);
      /**/
    return currencyPair;

  };

  public List<Currency> getCurrList() {
    String sql = "SELECT id, name FROM CURRENCY WHERE hidden IS NOT TRUE ";
    List<Currency> currList;
    currList = jdbcTemplate.query(sql, (rs, row) -> {
      Currency currency = new Currency();
      currency.setId(rs.getInt("id"));
      currency.setName(rs.getString("name"));
      return currency;

    });
    return currList;
  }

  @Override
  public int getCurrencyId(int walletId) {
    String sql = "SELECT currency_id FROM WALLET WHERE id = :walletId ";
    Map<String, String> namedParameters = new HashMap<>();
    namedParameters.put("walletId", String.valueOf(walletId));
    return jdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
  }

  @Override
  public String getCurrencyName(int currencyId) {
    String sql = "SELECT name FROM CURRENCY WHERE  id = :id ";
    Map<String, String> namedParameters = new HashMap<>();
    namedParameters.put("id", String.valueOf(currencyId));
    return jdbcTemplate.queryForObject(sql, namedParameters, String.class);
  }

  @Override
  public Currency findByName(String name) {
    final String sql = "SELECT * FROM CURRENCY WHERE name = :name";
    final Map<String, String> params = new HashMap<String, String>() {
      {
        put("name", name);
      }
    };
    return jdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(Currency.class));
  }

  @Override
  public Currency findById(int id) {
    final String sql = "SELECT * FROM CURRENCY WHERE id = :id";
    final Map<String, Integer> params = new HashMap<String, Integer>() {
      {
        put("id", id);
      }
    };
    return jdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(Currency.class));
  }

  @Override
  public List<Currency> findAllCurrencies() {
    final String sql = "SELECT * FROM CURRENCY WHERE hidden IS NOT TRUE ";
    return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Currency.class));
  }

  @Override
  public List<CurrencyLimit> retrieveCurrencyLimitsForRoles(List<Integer> roleIds, OperationType operationType) {
    String sql = "SELECT DISTINCT CURRENCY_LIMIT.currency_id, CURRENCY.name, " +
        "CURRENCY_LIMIT.min_sum, CURRENCY_LIMIT.max_sum, CURRENCY_LIMIT.max_daily_request " +
        "FROM CURRENCY_LIMIT " +
        "JOIN CURRENCY ON CURRENCY_LIMIT.currency_id = CURRENCY.id " +
        "WHERE user_role_id IN(:role_ids) AND CURRENCY_LIMIT.operation_type_id = :operation_type_id";
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("role_ids", roleIds);
      put("operation_type_id", operationType.getType());
    }};

    return jdbcTemplate.query(sql, params, (rs, row) -> {
      CurrencyLimit currencyLimit = new CurrencyLimit();
      Currency currency = new Currency();
      currency.setId(rs.getInt("currency_id"));
      currency.setName(rs.getString("name"));
      currencyLimit.setCurrency(currency);
      currencyLimit.setMinSum(rs.getBigDecimal("min_sum"));
      currencyLimit.setMaxSum(rs.getBigDecimal("max_sum"));
      currencyLimit.setMaxDailyRequest(rs.getInt("max_daily_request"));
      return currencyLimit;
    });
  }

  @Override
  public List<TransferLimitDto> retrieveMinTransferLimits(List<Integer> currencyIds, Integer roleId) {
    String currencyClause = currencyIds.isEmpty() ? "" : " AND currency_id IN (:currency_ids) ";
    String sql = "SELECT currency_id, min_sum FROM CURRENCY_LIMIT WHERE operation_type_id = 9 AND user_role_id = :user_role_id " + currencyClause;
    Map<String, Object> params = new HashMap<>();
    params.put("user_role_id", roleId);
    params.put("currency_ids", currencyIds);

    return jdbcTemplate.query(sql, params, (rs, rowNum) -> {
      TransferLimitDto dto = new TransferLimitDto();
      dto.setCurrencyId(rs.getInt("currency_id"));
      dto.setTransferMinLimit(rs.getBigDecimal("min_sum"));
      return dto;
    });
  }

  @Override
  public BigDecimal retrieveMinLimitForRoleAndCurrency(UserRole userRole, OperationType operationType, Integer currencyId) {
    String sql = "SELECT min_sum FROM CURRENCY_LIMIT " +
        "WHERE user_role_id = :role_id AND operation_type_id = :operation_type_id AND currency_id = :currency_id";
    Map<String, Integer> params = new HashMap<String, Integer>() {{
      put("role_id", userRole.getRole());
      put("operation_type_id", operationType.getType());
      put("currency_id", currencyId);
    }};
    return jdbcTemplate.queryForObject(sql, params, BigDecimal.class);
  }

  @Override
  public void updateCurrencyLimit(int currencyId, OperationType operationType, List<Integer> roleIds, BigDecimal minAmount, Integer maxDailyRequest) {
    String sql = "UPDATE CURRENCY_LIMIT SET min_sum = :min_sum, max_daily_request = :max_daily_request  WHERE currency_id = :currency_id " +
        "AND operation_type_id = :operation_type_id AND user_role_id IN (:role_ids)";
    final Map<String, Object> params = new HashMap<String, Object>() {
      {
        put("min_sum", minAmount);
        put("currency_id", currencyId);
        put("operation_type_id", operationType.getType());
        put("role_ids", roleIds);
        put("max_daily_request", maxDailyRequest);
      }
    };

    jdbcTemplate.update(sql, params);
  }

  @Override
  public List<CurrencyPair> getAllCurrencyPairs() {
    String sql = "SELECT id, currency1_id, currency2_id, name, \n" +
        "(select name from CURRENCY where id = currency1_id) as currency1_name,\n" +
        "(select name from CURRENCY where id = currency2_id) as currency2_name\n" +
        " FROM CURRENCY_PAIR " +
        " WHERE hidden IS NOT TRUE " +
        " ORDER BY -pair_order DESC";

    List<CurrencyPair> currencyPairList = jdbcTemplate.query(sql, currencyPairRowMapper);

    return currencyPairList;
  }

  @Override
  public CurrencyPair getCurrencyPairById(int currency1Id, int currency2Id) {
    String sql = "SELECT id, currency1_id, currency2_id, name, \n" +
        "(select name from CURRENCY where id = currency1_id) as currency1_name,\n" +
        "(select name from CURRENCY where id = currency2_id) as currency2_name\n" +
        " FROM CURRENCY_PAIR WHERE currency1_id = :currency1Id AND currency2_id = :currency2Id";
    Map<String, String> namedParameters = new HashMap<>();
    namedParameters.put("currency1Id", String.valueOf(currency1Id));
    namedParameters.put("currency2Id", String.valueOf(currency2Id));
    return jdbcTemplate.queryForObject(sql, namedParameters, currencyPairRowMapper);
  }

  @Override
  public CurrencyPair findCurrencyPairById(int currencyPairId) {
    String sql = "SELECT id, currency1_id, currency2_id, name, " +
        "(select name from CURRENCY where id = currency1_id) as currency1_name, " +
        "(select name from CURRENCY where id = currency2_id) as currency2_name " +
        " FROM CURRENCY_PAIR WHERE id = :currencyPairId";
    Map<String, String> namedParameters = new HashMap<>();
    namedParameters.put("currencyPairId", String.valueOf(currencyPairId));
    return jdbcTemplate.queryForObject(sql, namedParameters, currencyPairRowMapper);
  }

  @Override
  public CurrencyPair findCurrencyPairByName(String pairName) {
    String sql = "SELECT id, currency1_id, currency2_id, name, " +
            "(select name from CURRENCY where id = currency1_id) as currency1_name, " +
            "(select name from CURRENCY where id = currency2_id) as currency2_name " +
            " FROM CURRENCY_PAIR WHERE name = :currencyPairName";
    Map<String, String> namedParameters = new HashMap<>();
    namedParameters.put("currencyPairName", pairName);
    return jdbcTemplate.queryForObject(sql, namedParameters, currencyPairRowMapper);
  }

  @Override
  public List<UserCurrencyOperationPermissionDto> findCurrencyOperationPermittedByUserAndDirection(Integer userId, String operationDirection) {
    String sql = "SELECT CUR.id, CUR.name, IOP.invoice_operation_permission_id" +
        " FROM CURRENCY CUR " +
        " LEFT JOIN USER_CURRENCY_INVOICE_OPERATION_PERMISSION IOP ON " +
        "				(IOP.currency_id=CUR.id) " +
        "			 	AND (IOP.operation_direction=:operation_direction) " +
        "				AND (IOP.user_id=:user_id) " +
        " WHERE CUR.hidden IS NOT TRUE " +
        " ORDER BY CUR.id ";
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("user_id", userId);
      put("operation_direction", operationDirection);
    }};
    return jdbcTemplate.query(sql, params, (rs, row) -> {
      UserCurrencyOperationPermissionDto dto = new UserCurrencyOperationPermissionDto();
      dto.setUserId(userId);
      dto.setCurrencyId(rs.getInt("id"));
      dto.setCurrencyName(rs.getString("name"));
      Integer permissionCode = rs.getObject("invoice_operation_permission_id") == null ? 0 : (Integer) rs.getObject("invoice_operation_permission_id");
      dto.setInvoiceOperationPermission(InvoiceOperationPermission.convert(permissionCode));
      return dto;
    });
  }

  @Override
  public List<UserCurrencyOperationPermissionDto> findCurrencyOperationPermittedByUserList(Integer userId) {
    String sql = "SELECT CUR.id, CUR.name, IOP.invoice_operation_permission_id, IOP.operation_direction " +
        " FROM CURRENCY CUR " +
        " JOIN USER_CURRENCY_INVOICE_OPERATION_PERMISSION IOP ON " +
        "				(IOP.currency_id=CUR.id) " +
        "				AND (IOP.user_id=:user_id) " +
        " WHERE CUR.hidden IS NOT TRUE " +
        " ORDER BY CUR.id ";
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("user_id", userId);
    }};
    return jdbcTemplate.query(sql, params, (rs, row) -> {
      UserCurrencyOperationPermissionDto dto = new UserCurrencyOperationPermissionDto();
      dto.setUserId(userId);
      dto.setCurrencyId(rs.getInt("id"));
      dto.setCurrencyName(rs.getString("name"));
      dto.setInvoiceOperationDirection(InvoiceOperationDirection.valueOf(rs.getString("operation_direction")));
      Integer permissionCode = rs.getObject("invoice_operation_permission_id") == null ? 0 : (Integer) rs.getObject("invoice_operation_permission_id");
      dto.setInvoiceOperationPermission(InvoiceOperationPermission.convert(permissionCode));
      return dto;
    });
  }

  @Override
  public List<String> getWarningForCurrency(Integer currencyId, UserCommentTopicEnum currencyWarningTopicEnum) {
    String sql = "SELECT PHT.template " +
        " FROM PHRASE_TEMPLATE PHT " +
        " JOIN USER_COMMENT_TOPIC UCT ON (UCT.id = PHT.topic_id) AND (UCT.topic = :topic)  " +
        " JOIN CURRENCY CUR ON (CUR.id = :currency_id)" +
        " WHERE PHT.template LIKE CONCAT('%.', CUR.name) ";
    Map<String, Object> params = new HashMap<>();
    params.put("currency_id", currencyId);
    params.put("topic", currencyWarningTopicEnum.name());
    return jdbcTemplate.queryForList(sql, params, String.class);
  }

  @Override
  public CurrencyPair findCurrencyPairByOrderId(int orderId) {
    String sql = "SELECT CURRENCY_PAIR.id, CURRENCY_PAIR.currency1_id, CURRENCY_PAIR.currency2_id, name, " +
        "(select name from CURRENCY where id = currency1_id) as currency1_name, " +
        "(select name from CURRENCY where id = currency2_id) as currency2_name " +
        " FROM EXORDERS " +
        " JOIN CURRENCY_PAIR ON (CURRENCY_PAIR.id = EXORDERS.currency_pair_id) " +
        " WHERE EXORDERS.id = :order_id";
    Map<String, String> namedParameters = new HashMap<>();
    namedParameters.put("order_id", String.valueOf(orderId));
    return jdbcTemplate.queryForObject(sql, namedParameters, currencyPairRowMapper);
  }

  @Override
  public CurrencyPairLimitDto findCurrencyPairLimitForRoleByPairAndType(Integer currencyPairId, Integer roleId, Integer orderTypeId) {
    String sql = "SELECT CURRENCY_PAIR.id AS currency_pair_id, CURRENCY_PAIR.name AS currency_pair_name, lim.min_rate, lim.max_rate " +
        " FROM CURRENCY_PAIR_LIMIT lim " +
        " JOIN CURRENCY_PAIR ON lim.currency_pair_id = CURRENCY_PAIR.id AND CURRENCY_PAIR.hidden != 1 " +
        " WHERE lim.currency_pair_id = :currency_pair_id AND lim.user_role_id = :user_role_id AND lim.order_type_id = :order_type_id";
    Map<String, Integer> namedParameters = new HashMap<>();
    namedParameters.put("currency_pair_id", currencyPairId);
    namedParameters.put("user_role_id", roleId);
    namedParameters.put("order_type_id", orderTypeId);
    return jdbcTemplate.queryForObject(sql, namedParameters, (rs, rowNum) -> {
      CurrencyPairLimitDto dto = new CurrencyPairLimitDto();
      dto.setCurrencyPairId(rs.getInt("currency_pair_id"));
      dto.setCurrencyPairName(rs.getString("currency_pair_name"));
      dto.setMinRate(rs.getBigDecimal("min_rate"));
      dto.setMaxRate(rs.getBigDecimal("max_rate"));
      return dto;
    });
  }

  @Override
  public List<CurrencyPairLimitDto> findLimitsForRolesByType(List<Integer> roleIds, Integer orderTypeId) {
    String sql = "SELECT DISTINCT CURRENCY_PAIR.id AS currency_pair_id, CURRENCY_PAIR.name AS currency_pair_name, " +
        " lim.min_rate, lim.max_rate " +
        " FROM CURRENCY_PAIR_LIMIT lim " +
        " JOIN CURRENCY_PAIR ON lim.currency_pair_id = CURRENCY_PAIR.id " +
        " WHERE lim.user_role_id IN(:user_role_ids) AND lim.order_type_id = :order_type_id AND CURRENCY_PAIR.hidden != 1";
    Map<String, Object> namedParameters = new HashMap<>();
    namedParameters.put("user_role_ids", roleIds);
    namedParameters.put("order_type_id", orderTypeId);
    return jdbcTemplate.query(sql, namedParameters, (rs, rowNum) -> {
      CurrencyPairLimitDto dto = new CurrencyPairLimitDto();
      dto.setCurrencyPairId(rs.getInt("currency_pair_id"));
      dto.setCurrencyPairName(rs.getString("currency_pair_name"));
      dto.setMinRate(rs.getBigDecimal("min_rate"));
      dto.setMaxRate(rs.getBigDecimal("max_rate"));
      return dto;
    });
  }

  @Override
  public void setCurrencyPairLimit(Integer currencyPairId, List<Integer> roleIds, Integer orderTypeId,
                                   BigDecimal minRate, BigDecimal maxRate) {
    String sql = "UPDATE CURRENCY_PAIR_LIMIT SET max_rate = :max_rate, min_rate = :min_rate " +
        "WHERE currency_pair_id = :currency_pair_id AND user_role_id IN(:user_role_ids) AND order_type_id = :order_type_id";
    Map<String, Object> namedParameters = new HashMap<>();
    namedParameters.put("currency_pair_id", currencyPairId);
    namedParameters.put("user_role_ids", roleIds);
    namedParameters.put("order_type_id", orderTypeId);
    namedParameters.put("min_rate", minRate);
    namedParameters.put("max_rate", maxRate);
    jdbcTemplate.update(sql, namedParameters);
  }

  @Override
  public List<CurrencyPairWithLimitsDto> findAllCurrencyPairsWithLimits(Integer roleId) {
    String sql = "SELECT CP.id, CP.currency1_id, CP.currency2_id, CP.name, " +
        "     (select name from CURRENCY where id = currency1_id) as currency1_name, " +
        "			(select name from CURRENCY where id = currency2_id) as currency2_name, " +
        "			(SELECT min_rate FROM CURRENCY_PAIR_LIMIT lim " +
        "					WHERE lim.currency_pair_id = CP.id AND lim.user_role_id = :role_id AND lim.order_type_id = 1) AS min_rate_sell, " +
        "			(SELECT min_rate FROM CURRENCY_PAIR_LIMIT lim " +
        "					WHERE lim.currency_pair_id = CP.id AND lim.user_role_id = :role_id AND lim.order_type_id = 2) AS min_rate_buy, " +
        "			(SELECT max_rate FROM CURRENCY_PAIR_LIMIT lim " +
        "					WHERE lim.currency_pair_id = CP.id AND lim.user_role_id = :role_id AND lim.order_type_id = 1) AS max_rate_sell, " +
        "			(SELECT max_rate FROM CURRENCY_PAIR_LIMIT lim " +
        "					WHERE lim.currency_pair_id = CP.id AND lim.user_role_id = :role_id AND lim.order_type_id = 2) AS max_rate_buy " +
        "			FROM CURRENCY_PAIR CP " +
        "			WHERE CP.hidden != 1 ";
    return jdbcTemplate.query(sql, Collections.singletonMap("role_id", roleId), (rs, row) -> {
      CurrencyPair currencyPair = currencyPairRowMapper.mapRow(rs, row);
      return new CurrencyPairWithLimitsDto(currencyPair,
          rs.getBigDecimal("min_rate_sell"),
          rs.getBigDecimal("max_rate_sell"),
          rs.getBigDecimal("min_rate_buy"),
          rs.getBigDecimal("max_rate_buy"));
    });

  }

  @Override
  public List<Currency> findAllCurrenciesWithHidden() {
    final String sql = "SELECT * FROM CURRENCY";
    return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Currency.class));
  }

  @Override
  public MerchantCurrencyScaleDto findCurrencyScaleByCurrencyId(Integer currencyId) {
    String sql = "SELECT id, " +
        "  max_scale_for_refill, max_scale_for_withdraw " +
        "  FROM CURRENCY " +
        "  WHERE id = :currency_id";
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("currency_id", currencyId);
    }};
    return jdbcTemplate.queryForObject(sql, params, (rs, i) -> {
      MerchantCurrencyScaleDto result = new MerchantCurrencyScaleDto();
      result.setCurrencyId(rs.getInt("id"));
      result.setMerchantId(null);
      result.setScaleForRefill((Integer) rs.getObject("max_scale_for_refill"));
      result.setScaleForWithdraw((Integer) rs.getObject("max_scale_for_withdraw"));
      return result;
    });
  }

}