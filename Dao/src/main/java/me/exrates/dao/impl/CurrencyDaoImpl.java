package me.exrates.dao.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.CurrencyDao;
import me.exrates.dao.exception.notfound.CurrencyPairLimitNotFoundException;
import me.exrates.dao.exception.notfound.CurrencyPairNotFoundException;
import me.exrates.model.Currency;
import me.exrates.model.CurrencyLimit;
import me.exrates.model.CurrencyPair;
import me.exrates.model.CurrencyPairRestrictionsEnum;
import me.exrates.model.CurrencyPairWithRestriction;
import me.exrates.model.MarketVolume;
import me.exrates.model.dto.CurrencyPairLimitDto;
import me.exrates.model.dto.CurrencyReportInfoDto;
import me.exrates.model.dto.MerchantCurrencyScaleDto;
import me.exrates.model.dto.UserCurrencyOperationPermissionDto;
import me.exrates.model.dto.api.BalanceDto;
import me.exrates.model.dto.api.RateDto;
import me.exrates.model.dto.mobileApiDto.TransferLimitDto;
import me.exrates.model.dto.mobileApiDto.dashboard.CurrencyPairWithLimitsDto;
import me.exrates.model.dto.openAPI.CurrencyPairInfoItem;
import me.exrates.model.enums.RestrictedOperation;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.Market;
import me.exrates.model.enums.MerchantProcessType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserCommentTopicEnum;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.invoice.InvoiceOperationDirection;
import me.exrates.model.enums.invoice.InvoiceOperationPermission;
import me.exrates.model.util.BigDecimalProcessing;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Repository
public class CurrencyDaoImpl implements CurrencyDao {

    public static RowMapper<CurrencyPair> currencyPairRowMapper = (rs, row) -> {
        CurrencyPair currencyPair = new CurrencyPair();
        currencyPair.setId(rs.getInt("id"));
        currencyPair.setName(rs.getString("name"));
        currencyPair.setPairType(CurrencyPairType.valueOf(rs.getString("type")));
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
        currencyPair.setMarket(rs.getString("market"));
        currencyPair.setIsTopMarket(rs.getBoolean("top_market"));
        currencyPair.setTopMarketVolume(rs.getObject("top_market_volume") == null ? null :
                rs.getBigDecimal("top_market_volume"));

        return currencyPair;

    };
    public static RowMapper<CurrencyPair> currencyPairRowMapperWithDescrption = (rs, row) -> {
        CurrencyPair currencyPair = new CurrencyPair();
        currencyPair.setId(rs.getInt("id"));
        currencyPair.setName(rs.getString("name"));
        currencyPair.setPairType(CurrencyPairType.valueOf(rs.getString("type")));
        /**/
        Currency currency1 = new Currency();
        currency1.setId(rs.getInt("currency1_id"));
        currency1.setName(rs.getString("currency1_name"));
        currency1.setDescription(rs.getString("currency1_description"));
        currencyPair.setCurrency1(currency1);
        /**/
        Currency currency2 = new Currency();
        currency2.setId(rs.getInt("currency2_id"));
        currency2.setName(rs.getString("currency2_name"));
        currency2.setDescription(rs.getString("currency2_description"));
        currencyPair.setCurrency2(currency2);
        /**/
        currencyPair.setMarket(rs.getString("market"));

        return currencyPair;

    };
    protected static RowMapper<CurrencyPair> currencyPairRowShort = (rs, row) -> {
        CurrencyPair currencyPair = new CurrencyPair();
        currencyPair.setId(rs.getInt("id"));
        currencyPair.setName(rs.getString("name"));
        currencyPair.setPairType(CurrencyPairType.valueOf(rs.getString("type")));
        currencyPair.setMarket(rs.getString("market"));
        return currencyPair;
    };
    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate masterJdbcTemplate;
    @Autowired
    @Qualifier(value = "slaveTemplate")
    private NamedParameterJdbcTemplate slaveJdbcTemplate;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Currency> getAllActiveCurrencies() {
        String sql = "SELECT id, name FROM CURRENCY WHERE hidden IS NOT TRUE ";

        return masterJdbcTemplate.query(sql, (rs, row) -> Currency.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .build());
    }

    public List<Currency> getAllCurrencies() {
        String sql = "SELECT id, name FROM CURRENCY";

        return masterJdbcTemplate.query(sql, (rs, row) -> Currency.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .build());
    }

    @Override
    public int getCurrencyId(int walletId) {
        String sql = "SELECT currency_id FROM WALLET WHERE id = :walletId ";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("walletId", String.valueOf(walletId));
        return masterJdbcTemplate.queryForObject(sql, namedParameters, Integer.class);
    }

    @Override
    public String getCurrencyName(int currencyId) {
        String sql = "SELECT name FROM CURRENCY WHERE  id = :id ";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("id", String.valueOf(currencyId));
        return masterJdbcTemplate.queryForObject(sql, namedParameters, String.class);
    }

    @Override
    public Currency findByName(String name) {
        final String sql = "SELECT * FROM CURRENCY WHERE name = :name";
        final Map<String, String> params = new HashMap<String, String>() {
            {
                put("name", name);
            }
        };
        try {
            return masterJdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(Currency.class));
        } catch (Exception ex) {
            log.warn("Failed to find currency for name " + name, ex);
            throw ex;
        }
    }

    @Override
    public Currency findById(int id) {
        final String sql = "SELECT * FROM CURRENCY WHERE id = :id";
        final Map<String, Integer> params = new HashMap<String, Integer>() {
            {
                put("id", id);
            }
        };
        return masterJdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(Currency.class));
    }

    @Override
    public List<Currency> findAllCurrencies() {
        final String sql = "SELECT * FROM CURRENCY WHERE hidden IS NOT TRUE order by name";
        return masterJdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Currency.class));
    }

    @Override
    public List<CurrencyLimit> retrieveCurrencyLimitsForRoles(List<Integer> roleIds, OperationType operationType) {
        String sql = "SELECT DISTINCT " +
                "CURRENCY_LIMIT.currency_id, " +
                "CURRENCY.name, " +
                "CURRENCY_LIMIT.min_sum, " +
                "CURRENCY_LIMIT.min_sum_usd, " +
                "CURRENCY_LIMIT.usd_rate, " +
                "CURRENCY_LIMIT.max_sum, " +
                "CURRENCY_LIMIT.max_daily_request, " +
                "CURRENCY_LIMIT.recalculate_to_usd " +
                "FROM CURRENCY_LIMIT " +
                "JOIN CURRENCY ON CURRENCY_LIMIT.currency_id = CURRENCY.id " +
                "WHERE user_role_id IN (:role_ids) AND CURRENCY_LIMIT.operation_type_id = :operation_type_id";

        final Map<String, Object> params = new HashMap<String, Object>() {{
            put("role_ids", roleIds);
            put("operation_type_id", operationType.getType());
        }};

        return masterJdbcTemplate.query(sql, params, (rs, row) -> {
            Currency currency = new Currency(rs.getInt("currency_id"));
            currency.setName(rs.getString("name"));

            return CurrencyLimit.builder()
                    .currency(currency)
                    .minSum(rs.getBigDecimal("min_sum"))
                    .minSumUsdRate(rs.getBigDecimal("min_sum_usd"))
                    .maxSum(rs.getBigDecimal("max_sum"))
                    .currencyUsdRate(rs.getBigDecimal("usd_rate"))
                    .maxDailyRequest(rs.getInt("max_daily_request"))
                    .recalculateToUsd(rs.getBoolean("recalculate_to_usd"))
                    .build();
        });
    }

    @Override
    public List<TransferLimitDto> retrieveMinTransferLimits(List<Integer> currencyIds, Integer roleId) {
        String currencyClause = currencyIds.isEmpty() ? "" : " AND currency_id IN (:currency_ids) ";
        String sql = "SELECT currency_id, min_sum FROM CURRENCY_LIMIT WHERE operation_type_id = 9 AND user_role_id = :user_role_id " + currencyClause;
        Map<String, Object> params = new HashMap<>();
        params.put("user_role_id", roleId);
        params.put("currency_ids", currencyIds);

        return masterJdbcTemplate.query(sql, params, (rs, rowNum) -> {
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

        Map<String, Object> params = new HashMap<>();
        params.put("role_id", userRole.getRole());
        params.put("operation_type_id", operationType.getType());
        params.put("currency_id", currencyId);

        return masterJdbcTemplate.queryForObject(sql, params, BigDecimal.class);
    }

    @Override
    public BigDecimal retrieveMaxDailyRequestForRoleAndCurrency(UserRole userRole, OperationType operationType, Integer currencyId) {
        String sql = "SELECT max_daily_request FROM CURRENCY_LIMIT " +
                "WHERE user_role_id = :role_id AND operation_type_id = :operation_type_id AND currency_id = :currency_id";

        Map<String, Object> params = new HashMap<>();
        params.put("role_id", userRole.getRole());
        params.put("operation_type_id", operationType.getType());
        params.put("currency_id", currencyId);

        return masterJdbcTemplate.queryForObject(sql, params, BigDecimal.class);
    }

    @Override
    public void updateCurrencyLimit(int currencyId, OperationType operationType, List<Integer> roleIds, BigDecimal minAmount, BigDecimal minAmountUSD, BigDecimal maxAmount, Integer maxDailyRequest) {
        String sql = "UPDATE CURRENCY_LIMIT " +
                "SET min_sum = :min_sum, min_sum_usd = :min_sum_usd, max_sum = :max_sum, max_daily_request = :max_daily_request " +
                "WHERE currency_id = :currency_id AND operation_type_id = :operation_type_id AND user_role_id IN (:role_ids)";

        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("min_sum", minAmount);
                put("max_sum", maxAmount);
                put("min_sum_usd", minAmountUSD);
                put("currency_id", currencyId);
                put("operation_type_id", operationType.getType());
                put("role_ids", roleIds);
                put("max_daily_request", maxDailyRequest);
            }
        };
        masterJdbcTemplate.update(sql, params);
    }

    @Override
    public void updateCurrencyLimit(int currencyId, OperationType operationType, BigDecimal minAmount, BigDecimal minAmountUSD, BigDecimal maxAmount, Integer maxDailyRequest) {
        String sql = "UPDATE CURRENCY_LIMIT " +
                "SET min_sum = :min_sum, min_sum_usd = :min_sum_usd, max_sum = :max_sum, max_daily_request = :max_daily_request " +
                "WHERE currency_id = :currency_id AND operation_type_id = :operation_type_id";

        final Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("min_sum", minAmount);
                put("max_sum", maxAmount);
                put("min_sum_usd", minAmountUSD);
                put("currency_id", currencyId);
                put("operation_type_id", operationType.getType());
                put("max_daily_request", maxDailyRequest);
            }
        };
        masterJdbcTemplate.update(sql, params);
    }

    @Override
    public List<CurrencyPair> getAllCurrencyPairs(CurrencyPairType type) {
        String typeClause = "";
        if (type != null && type != CurrencyPairType.ALL) {
            typeClause = " AND type =:pairType ";
        }
        String sql = "SELECT id, currency1_id, currency2_id, name, market, type," +
                "       (select name from CURRENCY where id = currency1_id) as currency1_name," +
                "       (select description from CURRENCY where id = currency1_id) as currency1_description," +
                "       (select name from CURRENCY where id = currency2_id) as currency2_name," +
                "       (select description from CURRENCY where id = currency2_id) as currency2_description " +
                "FROM CURRENCY_PAIR  WHERE hidden IS NOT TRUE  ORDER BY pair_order DESC";
        return masterJdbcTemplate.query(sql, Collections.singletonMap("pairType", type.name()), currencyPairRowMapperWithDescrption);
    }

    @Override
    public List<CurrencyPair> getAllCurrencyPairsWithHidden(CurrencyPairType type) {
        String typeClause = "";
        if (type != null && type != CurrencyPairType.ALL) {
            typeClause = " WHERE type =:pairType ";
        }
        String sql = "SELECT id, currency1_id, currency2_id, name, market, type, top_market, top_market_volume, " +
                "(select name from CURRENCY where id = currency1_id) as currency1_name, " +
                "(select name from CURRENCY where id = currency2_id) as currency2_name " +
                " FROM CURRENCY_PAIR " + typeClause +
                " ORDER BY -pair_order DESC";
        return masterJdbcTemplate.query(sql, Collections.singletonMap("pairType", type.name()), currencyPairRowMapper);
    }


    @Override
    public CurrencyPair getCurrencyPairById(int currency1Id, int currency2Id) {
        String sql = "SELECT id, currency1_id, currency2_id, name, market, type, top_market, top_market_volume, " +
                "(select name from CURRENCY where id = currency1_id) as currency1_name, " +
                "(select name from CURRENCY where id = currency2_id) as currency2_name " +
                " FROM CURRENCY_PAIR WHERE currency1_id = :currency1Id AND currency2_id = :currency2Id";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("currency1Id", String.valueOf(currency1Id));
        namedParameters.put("currency2Id", String.valueOf(currency2Id));
        return masterJdbcTemplate.queryForObject(sql, namedParameters, currencyPairRowMapper);
    }

    @Override
    public CurrencyPair findCurrencyPairById(int currencyPairId) {
        String sql = "SELECT id, currency1_id, currency2_id, name, market, type, top_market, top_market_volume, " +
                "(select name from CURRENCY where id = currency1_id) as currency1_name, " +
                "(select name from CURRENCY where id = currency2_id) as currency2_name " +
                " FROM CURRENCY_PAIR WHERE id = :currencyPairId";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("currencyPairId", String.valueOf(currencyPairId));
        return slaveJdbcTemplate.queryForObject(sql, namedParameters, currencyPairRowMapper);
    }

    @Override
    public CurrencyPair getNotHiddenCurrencyPairByName(String currencyPairName) {
        String sql = "SELECT id, currency1_id, currency2_id, name, market, type, top_market, top_market_volume, " +
                "(select name from CURRENCY where id = currency1_id) as currency1_name, " +
                "(select name from CURRENCY where id = currency2_id) as currency2_name " +
                " FROM CURRENCY_PAIR WHERE name = :currencyPairName AND hidden IS NOT TRUE ";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("currencyPairName", String.valueOf(currencyPairName));
        return masterJdbcTemplate.queryForObject(sql, namedParameters, currencyPairRowMapper);
    }

    @Override
    public CurrencyPair findCurrencyPairByName(String currencyPair) {
        String sql = "SELECT cp.id, " +
                "cp.currency1_id, " +
                "cp.currency2_id, " +
                "cp.name, " +
                "cp.market, " +
                "cp.type," +
                "cp.top_market, " +
                "cp.top_market_volume, " +
                "(select name from CURRENCY where id = currency1_id) as currency1_name, " +
                "(select name from CURRENCY where id = currency2_id) as currency2_name " +
                " FROM CURRENCY_PAIR cp" +
                " WHERE cp.name = :currency_pair";

        Map<String, String> params = new HashMap<>();
        params.put("currency_pair", currencyPair);

        try {
            return slaveJdbcTemplate.queryForObject(sql, params, currencyPairRowMapper);
        } catch (Exception ex) {
            throw new CurrencyPairNotFoundException(String.format("Currency pair: %s not found", currencyPair));
        }
    }

    @Override
    public List<UserCurrencyOperationPermissionDto> findCurrencyOperationPermittedByUserAndDirection(Integer userId, String operationDirection) {
        String sql = "SELECT CUR.id, CUR.name, IOP.invoice_operation_permission_id" +
                " FROM CURRENCY CUR " +
                " LEFT JOIN USER_CURRENCY_INVOICE_OPERATION_PERMISSION IOP ON " +
                "				(IOP.currency_id=CUR.id) " +
                "			 	AND (IOP.operation_direction=:operation_direction) " +
                "				AND (IOP.user_id=:user_id) " +
                " ORDER BY CUR.id ";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("user_id", userId);
            put("operation_direction", operationDirection);
        }};
        return masterJdbcTemplate.query(sql, params, (rs, row) -> {
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
    public List<UserCurrencyOperationPermissionDto> findAllCurrencyOperationPermittedByUserAndDirection(Integer userId, String operationDirection) {
        String sql = "SELECT CUR.id, CUR.name, IOP.invoice_operation_permission_id" +
                " FROM CURRENCY CUR " +
                " LEFT JOIN USER_CURRENCY_INVOICE_OPERATION_PERMISSION IOP ON " +
                "				(IOP.currency_id=CUR.id) " +
                "			 	AND (IOP.operation_direction=:operation_direction) " +
                "				AND (IOP.user_id=:user_id) " +
                " ORDER BY CUR.id ";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("user_id", userId);
            put("operation_direction", operationDirection);
        }};
        return masterJdbcTemplate.query(sql, params, (rs, row) -> {
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
        return masterJdbcTemplate.query(sql, params, (rs, row) -> {
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
        return masterJdbcTemplate.queryForList(sql, params, String.class);
    }

    @Override
    public List<String> getWarningsByTopic(UserCommentTopicEnum currencyWarningTopicEnum) {
        String sql = "SELECT PHT.template " +
                " FROM PHRASE_TEMPLATE PHT " +
                " JOIN USER_COMMENT_TOPIC UCT ON (UCT.id = PHT.topic_id) AND (UCT.topic = :topic)  ";
        Map<String, Object> params = new HashMap<>();
        params.put("topic", currencyWarningTopicEnum.name());
        return masterJdbcTemplate.queryForList(sql, params, String.class);
    }

    @Override
    public List<String> getWarningForMerchant(Integer merchantId, UserCommentTopicEnum currencyWarningTopicEnum) {
        String sql = "SELECT PHT.template " +
                " FROM PHRASE_TEMPLATE PHT " +
                " JOIN USER_COMMENT_TOPIC UCT ON (UCT.id = PHT.topic_id) AND (UCT.topic = :topic)  " +
                " JOIN MERCHANT MCH ON (MCH.id = :merchant_id)" +
                " WHERE PHT.template LIKE CONCAT('%.', REPLACE(MCH.name, ' ', '')) ";
        Map<String, Object> params = new HashMap<>();
        params.put("merchant_id", merchantId);
        params.put("topic", currencyWarningTopicEnum.name());
        return masterJdbcTemplate.queryForList(sql, params, String.class);
    }

    @Override
    public CurrencyPair findCurrencyPairByOrderId(int orderId) {
        String sql = "SELECT CURRENCY_PAIR.id, CURRENCY_PAIR.currency1_id, CURRENCY_PAIR.currency2_id, name, type, top_market, top_market_volume, " +
                "CURRENCY_PAIR.market, " +
                "(select name from CURRENCY where id = currency1_id) as currency1_name, " +
                "(select name from CURRENCY where id = currency2_id) as currency2_name " +
                " FROM EXORDERS " +
                " JOIN CURRENCY_PAIR ON (CURRENCY_PAIR.id = EXORDERS.currency_pair_id) " +
                " WHERE EXORDERS.id = :order_id";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("order_id", String.valueOf(orderId));

        try {
            return masterJdbcTemplate.queryForObject(sql, namedParameters, currencyPairRowMapper);
        } catch (Exception ex) {
            throw new CurrencyPairNotFoundException("Currency pair not found");
        }
    }

    @Override
    public CurrencyPairLimitDto findCurrencyPairLimitForRoleByPairAndType(Integer currencyPairId, Integer roleId, Integer orderTypeId) {
        String sql = "SELECT CURRENCY_PAIR.id AS currency_pair_id, CURRENCY_PAIR.name AS currency_pair_name, lim.min_rate, lim.max_rate, " +
                "lim.min_amount, lim.max_amount, lim.min_total " +
                " FROM CURRENCY_PAIR_LIMIT lim " +
                " JOIN CURRENCY_PAIR ON lim.currency_pair_id = CURRENCY_PAIR.id AND CURRENCY_PAIR.hidden != 1 " +
                " WHERE lim.currency_pair_id = :currency_pair_id AND lim.user_role_id = :user_role_id AND lim.order_type_id = :order_type_id";
        Map<String, Integer> namedParameters = new HashMap<>();
        namedParameters.put("currency_pair_id", currencyPairId);
        namedParameters.put("user_role_id", roleId);
        namedParameters.put("order_type_id", orderTypeId);

        try {
            return masterJdbcTemplate.queryForObject(sql, namedParameters, (rs, rowNum) -> {
                CurrencyPairLimitDto dto = new CurrencyPairLimitDto();
                dto.setCurrencyPairId(rs.getInt("currency_pair_id"));
                dto.setCurrencyPairName(rs.getString("currency_pair_name"));
                dto.setMinRate(rs.getBigDecimal("min_rate"));
                dto.setMaxRate(rs.getBigDecimal("max_rate"));
                dto.setMinAmount(rs.getBigDecimal("min_amount"));
                dto.setMaxAmount(rs.getBigDecimal("max_amount"));
                dto.setMinTotal(rs.getBigDecimal("min_total"));
                return dto;
            });
        } catch (Exception ex) {
            throw new CurrencyPairLimitNotFoundException(String.format("Currency pair limit for pair: %d not found", currencyPairId));
        }

    }

    @Override
    public List<CurrencyPairLimitDto> findLimitsForRolesByType(List<Integer> roleIds, Integer orderTypeId) {
        String sql = "SELECT DISTINCT CURRENCY_PAIR.id AS currency_pair_id, CURRENCY_PAIR.name AS currency_pair_name, " +
                " lim.min_rate, lim.max_rate, lim.min_amount, lim.max_amount, lim.min_total " +
                " FROM CURRENCY_PAIR_LIMIT lim " +
                " JOIN CURRENCY_PAIR ON lim.currency_pair_id = CURRENCY_PAIR.id " +
                " WHERE lim.user_role_id IN(:user_role_ids) AND lim.order_type_id = :order_type_id AND CURRENCY_PAIR.hidden != 1";
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("user_role_ids", roleIds);
        namedParameters.put("order_type_id", orderTypeId);
        return masterJdbcTemplate.query(sql, namedParameters, (rs, rowNum) -> {
            CurrencyPairLimitDto dto = new CurrencyPairLimitDto();
            dto.setCurrencyPairId(rs.getInt("currency_pair_id"));
            dto.setCurrencyPairName(rs.getString("currency_pair_name"));
            dto.setMinRate(rs.getBigDecimal("min_rate"));
            dto.setMaxRate(rs.getBigDecimal("max_rate"));
            dto.setMinAmount(rs.getBigDecimal("min_amount"));
            dto.setMaxAmount(rs.getBigDecimal("max_amount"));
            dto.setMinTotal(rs.getBigDecimal("min_total"));
            return dto;
        });
    }

    @Override
    public void setCurrencyPairLimit(Integer currencyPairId, List<Integer> roleIds, Integer orderTypeId,
                                     BigDecimal minRate, BigDecimal maxRate, BigDecimal minAmount, BigDecimal maxAmount, BigDecimal minTotal) {
        String sql = "UPDATE CURRENCY_PAIR_LIMIT SET max_rate = :max_rate, min_rate = :min_rate, min_amount = :min_amount, max_amount = :max_amount, min_total = :min_total " +
                "WHERE currency_pair_id = :currency_pair_id AND user_role_id IN(:user_role_ids) AND order_type_id = :order_type_id";
        Map<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("currency_pair_id", currencyPairId);
        namedParameters.put("user_role_ids", roleIds);
        namedParameters.put("order_type_id", orderTypeId);
        namedParameters.put("min_rate", minRate);
        namedParameters.put("max_rate", maxRate);
        namedParameters.put("min_amount", minAmount);
        namedParameters.put("max_amount", maxAmount);
        namedParameters.put("min_total", minTotal);
        masterJdbcTemplate.update(sql, namedParameters);
    }

    @Override
    public List<CurrencyPairWithLimitsDto> findAllCurrencyPairsWithLimits(Integer roleId) {
        String sql = "SELECT CP.id, CP.currency1_id, CP.currency2_id, CP.name, CP.market, CP.type, CP.top_market, CP.top_market_volume, " +
                "             (select name from CURRENCY where id = currency1_id) as currency1_name, " +
                "        (select name from CURRENCY where id = currency2_id) as currency2_name, " +
                "  LIM_SELL.min_rate AS min_rate_sell, LIM_SELL.max_rate AS max_rate_sell, LIM_SELL.min_amount AS min_amount_sell, LIM_SELL.max_amount AS max_amount_sell, " +
                "  LIM_BUY.min_rate AS min_rate_buy, LIM_BUY.max_rate AS max_rate_buy, LIM_BUY.min_amount AS min_amount_buy, LIM_BUY.max_amount AS max_amount_buy " +
                "        FROM CURRENCY_PAIR CP " +
                "                JOIN CURRENCY_PAIR_LIMIT LIM_SELL ON CP.id = LIM_SELL.currency_pair_id AND LIM_SELL.user_role_id = :role_id " +
                "                                                     AND LIM_SELL.order_type_id = 1 " +
                "                JOIN CURRENCY_PAIR_LIMIT LIM_BUY ON CP.id = LIM_BUY.currency_pair_id AND LIM_BUY.user_role_id = :role_id " +
                "                                                     AND LIM_BUY.order_type_id = 2 " +
                "        WHERE CP.hidden != 1 ";
        return masterJdbcTemplate.query(sql, Collections.singletonMap("role_id", roleId), (rs, row) -> {
            CurrencyPair currencyPair = currencyPairRowMapper.mapRow(rs, row);
            return new CurrencyPairWithLimitsDto(currencyPair,
                    rs.getBigDecimal("min_rate_sell"),
                    rs.getBigDecimal("max_rate_sell"),
                    rs.getBigDecimal("min_rate_buy"),
                    rs.getBigDecimal("max_rate_buy"),
                    rs.getBigDecimal("min_amount_sell"),
                    rs.getBigDecimal("max_amount_sell"),
                    rs.getBigDecimal("min_amount_buy"),
                    rs.getBigDecimal("max_amount_buy"));
        });

    }

    @Override
    public List<Currency> findAllCurrenciesWithHidden() {
        final String sql = "SELECT * FROM CURRENCY";
        return masterJdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Currency.class));
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
        return masterJdbcTemplate.queryForObject(sql, params, (rs, i) -> {
            MerchantCurrencyScaleDto result = new MerchantCurrencyScaleDto();
            result.setCurrencyId(rs.getInt("id"));
            result.setMerchantId(null);
            result.setScaleForRefill((Integer) rs.getObject("max_scale_for_refill"));
            result.setScaleForWithdraw((Integer) rs.getObject("max_scale_for_withdraw"));
            return result;
        });
    }

    @Override
    public List<Currency> findAllCurrenciesByProcessType(MerchantProcessType processType) {
        final String sql = "SELECT * FROM CURRENCY " +
                "JOIN MERCHANT_CURRENCY MC ON MC.currency_id = CURRENCY.id " +
                "JOIN MERCHANT M ON M.id = MC.merchant_id " +
                "WHERE M.process_type = :process_type ";
        return masterJdbcTemplate.query(sql,
                new HashMap<String, Object>() {{
                    put("process_type", processType.name());
                }},
                (rs, i) -> {
                    Currency currency = new Currency();
                    currency.setId(rs.getInt("id"));
                    currency.setName(rs.getString("name"));
                    currency.setDescription(rs.getString("description"));
                    return currency;
                });
    }

    @Override
    public List<CurrencyPair> findPermitedCurrencyPairs(CurrencyPairType currencyPairType) {
        String sql = "SELECT id, currency1_id, currency2_id, name, market, type, top_market, top_market_volume, " +
                "        (select name from CURRENCY where id = currency1_id) as currency1_name, " +
                "        (select name from CURRENCY where id = currency2_id) as currency2_name " +
                "         FROM CURRENCY_PAIR " +
                "         WHERE hidden IS NOT TRUE AND permitted_link IS TRUE ";
        if (currencyPairType != CurrencyPairType.ALL) {
            sql = sql.concat(" AND type =:type");
        }
        return masterJdbcTemplate.query(sql, Collections.singletonMap("type", currencyPairType.name()), currencyPairRowMapper);
    }


    @Override
    public boolean isCurrencyIco(Integer currencyId) {
        String sql = "SELECT id " +
                "         FROM CURRENCY_PAIR " +
                "         WHERE hidden IS NOT TRUE AND type = 'ICO' AND currency1_id =:currency_id ";
        return !masterJdbcTemplate.queryForList(sql, Collections.singletonMap("currency_id", currencyId), Integer.class).isEmpty();
    }

    @Override
    public List<CurrencyPairInfoItem> findActiveCurrencyPairs() {
        String sql = "SELECT name FROM CURRENCY_PAIR WHERE hidden != 1 ORDER BY name ASC";
        return masterJdbcTemplate.query(sql, Collections.emptyMap(),
                (rs, row) -> new CurrencyPairInfoItem(rs.getString("name")));
    }

    @Override
    public Optional<Integer> findOpenCurrencyPairIdByName(String pairName) {
        String sql = "SELECT id FROM CURRENCY_PAIR WHERE name = :pair_name AND hidden != 1";
        try {
            return Optional.of(masterJdbcTemplate.queryForObject(sql, Collections.singletonMap("pair_name", pairName), Integer.class));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Currency> findAllCurrency() {
        String sql = "SELECT id, name, description, hidden FROM CURRENCY";
        return masterJdbcTemplate.query(sql, (rs, i) -> {
            Currency result = new Currency();
            result.setId(rs.getInt("id"));
            result.setName(rs.getString("name"));
            result.setDescription(rs.getString("description"));
            result.setHidden(rs.getBoolean("hidden"));
            return result;
        });
    }

    @Override
    public boolean updateVisibilityCurrencyById(int currencyId) {
        String sql = "UPDATE CURRENCY SET hidden = !hidden WHERE id = :currency_id";
        Map<String, Object> params = new HashMap<>();
        params.put("currency_id", currencyId);
        return masterJdbcTemplate.update(sql, params) > 0;
    }

    @Override
    public List<CurrencyPair> findAllCurrencyPair() {
        String sql = "SELECT id, name, hidden, permitted_link, top_market_volume FROM CURRENCY_PAIR";
        return masterJdbcTemplate.query(sql, (rs, i) -> {
            CurrencyPair result = new CurrencyPair();
            result.setId(rs.getInt("id"));
            result.setName(rs.getString("name"));
            result.setHidden(rs.getBoolean("hidden"));
            result.setPermittedLink(rs.getBoolean("permitted_link"));
            result.setTopMarketVolume(rs.getObject("top_market_volume") == null ? null :
                    rs.getBigDecimal("top_market_volume"));
            return result;
        });
    }

    @Override
    public boolean updateVisibilityCurrencyPairById(int currencyPairId) {
        String sql = "UPDATE CURRENCY_PAIR SET hidden = !hidden WHERE id = :currency_pair_id";
        Map<String, Object> params = new HashMap<>();
        params.put("currency_pair_id", currencyPairId);
        return masterJdbcTemplate.update(sql, params) > 0;
    }


    @Override
    public boolean updateAccessToDirectLinkCurrencyPairById(int currencyPairId) {
        String sql = "UPDATE CURRENCY_PAIR SET permitted_link = !permitted_link WHERE id = :currency_pair_id";
        Map<String, Object> params = new HashMap<>();
        params.put("currency_pair_id", currencyPairId);
        return masterJdbcTemplate.update(sql, params) > 0;
    }

    @Override
    public List<CurrencyReportInfoDto> getStatsByCoin(int currencyId) {
        String sql = "SELECT us.id, us.email, wall.active_balance, wall.reserved_balance, us.regdate," +
                "(SELECT date_creation FROM REFILL_REQUEST WHERE currency_id = :currencyId AND user_id = us.id " +
                "ORDER BY date_creation DESC LIMIT 1) as date_last_refill " +
                "FROM WALLET as wall JOIN USER as us ON wall.user_id = us.id " +
                "WHERE wall.currency_id = :currencyId AND (wall.active_balance > 0 OR wall.reserved_balance > 0)";

        Map<String, Object> params = new HashMap<String, Object>() {{
            put("currencyId", currencyId);
        }};

        return masterJdbcTemplate.query(sql, params, (rs, i) -> {
            CurrencyReportInfoDto result = new CurrencyReportInfoDto();
            result.setEmail(rs.getString("email"));
            result.setActiveBalance(BigDecimalProcessing.formatLocale(rs.getBigDecimal("active_balance"), Locale.ENGLISH, 2));
            result.setReservedBalance(BigDecimalProcessing.formatLocale(rs.getBigDecimal("reserved_balance"), Locale.ENGLISH, 2));
            result.setDateUserRegistration(rs.getTimestamp("regdate").toLocalDateTime());
            result.setDateLastRefillByUser(rs.getTimestamp("date_last_refill") != null
                    ? rs.getTimestamp("date_last_refill").toLocalDateTime() : null);
            return result;
        });
    }

    @Transactional
    @Override
    public boolean setPropertyCalculateLimitToUsd(int currencyId, OperationType operationType, List<Integer> roleIds, Boolean recalculateToUsd) {
        String sql = "UPDATE CURRENCY_LIMIT " +
                "SET recalculate_to_usd = :recalculate_to_usd " +
                "WHERE currency_id = :currency_id AND operation_type_id = :operation_type_id AND user_role_id IN (:role_ids)";

        Map<String, Object> params = new HashMap<>();
        params.put("currency_id", currencyId);
        params.put("operation_type_id", operationType.getType());
        params.put("role_ids", roleIds);
        params.put("recalculate_to_usd", recalculateToUsd);

        return masterJdbcTemplate.update(sql, params) > 0;
    }

    @Transactional(readOnly = true)
    @Override
    public List<CurrencyLimit> getAllCurrencyLimits() {
        String sql = "SELECT DISTINCT " +
                "CURRENCY_LIMIT.id, " +
                "CURRENCY_LIMIT.currency_id, " +
                "CURRENCY.name, " +
                "CURRENCY_LIMIT.min_sum, " +
                "CURRENCY_LIMIT.min_sum_usd, " +
                "CURRENCY_LIMIT.usd_rate, " +
                "CURRENCY_LIMIT.recalculate_to_usd " +
                "FROM CURRENCY_LIMIT " +
                "JOIN CURRENCY ON CURRENCY_LIMIT.currency_id = CURRENCY.id";

        return masterJdbcTemplate.query(sql, (rs, row) -> {
            Currency currency = new Currency(rs.getInt("currency_id"));
            currency.setName(rs.getString("name"));

            return CurrencyLimit.builder()
                    .id(rs.getInt("id"))
                    .currency(currency)
                    .minSum(rs.getBigDecimal("min_sum"))
                    .minSumUsdRate(rs.getBigDecimal("min_sum_usd"))
                    .currencyUsdRate(rs.getBigDecimal("usd_rate"))
                    .recalculateToUsd(rs.getBoolean("recalculate_to_usd"))
                    .build();
        });
    }

    @Override
    public List<Currency> getCurrencies(MerchantProcessType... types) {
        String sql = "SELECT C.id, C.name, C.description FROM CURRENCY C " +
                "       JOIN MERCHANT_CURRENCY ON MERCHANT_CURRENCY.currency_id = C.id " +
                "       JOIN MERCHANT M on MERCHANT_CURRENCY.merchant_id = M.id " +
                "       WHERE M.process_type IN (:processTypes) and C.hidden = 0 " +
                "       GROUP BY C.id, C.name, C.description ORDER BY C.name ASC";
        List<String> processTypes = Arrays
                .stream(types)
                .map(String::valueOf)
                .collect(Collectors.toList());
        MapSqlParameterSource params = new MapSqlParameterSource("processTypes", processTypes);
        return masterJdbcTemplate.query(sql, params, getCurrencyRowMapper());
    }

    @Override
    public List<CurrencyPair> findAllCurrenciesByFirstPartName(String partName) {
        final String sql = "SELECT * FROM CURRENCY_PAIR WHERE name LIKE CONCAT(:part, '/%') AND hidden = 0 order by name";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("part", partName.toUpperCase());
        }};
        return masterJdbcTemplate.query(sql, params, currencyPairRowShort);
    }

    @Override
    public List<CurrencyPair> findAllCurrenciesBySecondPartName(String partName) {
        final String sql = "SELECT * FROM CURRENCY_PAIR WHERE name LIKE CONCAT('%/', :part) AND hidden = 0 order by name";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("part", partName.toUpperCase());
        }};
        return masterJdbcTemplate.query(sql, params, currencyPairRowShort);
    }

    @Override
    public List<Currency> findAllByNames(Collection<String> names) {
        String sql = "SELECT id, name FROM CURRENCY WHERE name IN (:names) ";
        MapSqlParameterSource params = new MapSqlParameterSource("names", names);
        return masterJdbcTemplate.query(sql, params, (rs, row) -> Currency.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("name"))
                .build());
    }

    private RowMapper<Currency> getCurrencyRowMapper() {
        return (rs, rowNum) -> Currency
                .builder()
                .id(rs.getInt("C.id"))
                .name(rs.getString("C.name"))
                .description(rs.getString("C.description"))
                .build();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void updateWithdrawLimits(List<CurrencyLimit> currencyLimits) {
        String sql = "UPDATE CURRENCY_LIMIT " +
                "SET min_sum = ?, min_sum_usd = ?, usd_rate = ? " +
                "WHERE id = ?";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                CurrencyLimit dto = currencyLimits.get(i);
                ps.setBigDecimal(1, dto.getMinSum());
                ps.setBigDecimal(2, dto.getMinSumUsdRate());
                ps.setBigDecimal(3, dto.getCurrencyUsdRate());
                ps.setInt(4, dto.getId());
            }

            @Override
            public int getBatchSize() {
                return currencyLimits.size();
            }
        });
    }

    @Override
    public boolean isCurrencyPairHidden(int currencyPairId) {
        String sql = "SELECT cp.hidden FROM CURRENCY_PAIR cp WHERE cp.id = :currency_pair_id";

        Map<String, Object> params = new HashMap<>();
        params.put("currency_pair_id", currencyPairId);

        try {
            return masterJdbcTemplate.queryForObject(sql, params, Boolean.TYPE);
        } catch (Exception ex) {
            throw new CurrencyPairNotFoundException("Currency pair not found");
        }
    }

    @Override
    public void addCurrency(String currencyName, String description, String beanName, String imgPath, boolean hidden, boolean lockInOut) {
        final String sql = "{call add_currency(:currencyName, :description, :beanName, :imgPath, :hidden, :lockInOut)}";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("currencyName", currencyName);
        params.addValue("description", description);
        params.addValue("beanName", beanName);
        params.addValue("hidden", hidden);
        params.addValue("lockInOut", lockInOut);
        params.addValue("imgPath", imgPath);
        try {
            masterJdbcTemplate.execute(sql, params, ps -> {
                ResultSet rs = ps.executeQuery();
                rs.close();
                return rs;
            });
        } catch (DataAccessException e) {
            log.error("Failed to insert currency pair ", e);
            throw new RuntimeException(String.format("Error crete new currency in db for %s to DB", currencyName));
        }


    }

    @Override
    public void addCurrencyPair(Currency currency1, Currency currency2, String newPairName, CurrencyPairType type, Market market, String tiker, boolean hidden) {
        final String insertPair = "INSERT INTO CURRENCY_PAIR (currency1_id, currency2_id, name, pair_order, hidden, market, ticker_name, type)" +
                "VALUES(:currency1_id, :currency2_id, :pairName, 160, :hidden, :market, :ticker, :type) ";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("currency1_id", currency1.getId());
        params.addValue("currency2_id", currency2.getId());
        params.addValue("pairName", newPairName);
        params.addValue("hidden", hidden);
        params.addValue("market", market.name());
        params.addValue("ticker", tiker);
        params.addValue("type", type.name());
        try {
            masterJdbcTemplate.update(insertPair, params, keyHolder);
        } catch (DataAccessException e) {
            log.error("Failed to insert currency pair ", e);
            throw new RuntimeException(String.format("Error insert new pair to db for %s to DB", newPairName));
        }
        final String insertPairLimit = "INSERT INTO CURRENCY_PAIR_LIMIT (currency_pair_id, user_role_id, order_type_id, min_rate, max_rate)" +
                "  SELECT CP.id, UR.id, OT.id, 0, 99999999999 FROM CURRENCY_PAIR CP " +
                "    JOIN USER_ROLE UR " +
                "    JOIN ORDER_TYPE OT where CP.name = :name;";
        MapSqlParameterSource paramsForLimit = new MapSqlParameterSource();
        paramsForLimit.addValue("name", newPairName);
        try {
            masterJdbcTemplate.update(insertPairLimit, paramsForLimit, keyHolder);
        } catch (DataAccessException e) {
            log.error("Failed to insert Currency pir limits ", e);
            throw new RuntimeException(String.format("Error insert pair limits for %s to DB", newPairName));
        }
    }

    @Override
    public void updateCurrencyExchangeRates(List<RateDto> rates) {
        final String sql = "UPDATE CURRENT_CURRENCY_RATES " +
                "SET usd_rate = ?, btc_rate = ? " +
                "WHERE currency_name = ?";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                RateDto rateDto = rates.get(i);
                ps.setBigDecimal(1, rateDto.getUsdRate());
                ps.setBigDecimal(2, rateDto.getBtcRate());
                ps.setString(3, rateDto.getCurrencyName());
            }

            @Override
            public int getBatchSize() {
                return rates.size();
            }
        });
    }

    @Override
    public List<RateDto> getCurrencyRates() {
        final String sql = "SELECT currency_name, usd_rate, btc_rate FROM CURRENT_CURRENCY_RATES";

        return masterJdbcTemplate.query(sql, (rs, row) -> RateDto.builder()
                .currencyName(rs.getString("currency_name"))
                .usdRate(rs.getBigDecimal("usd_rate"))
                .btcRate(rs.getBigDecimal("btc_rate"))
                .build());
    }

    @Override
    public void updateCurrencyBalances(List<BalanceDto> balances) {
        final String sql = "UPDATE CURRENT_CURRENCY_BALANCES " +
                "SET balance = ?, last_updated_at = ? " +
                "WHERE currency_name = ?";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                BalanceDto balanceDto = balances.get(i);
                ps.setBigDecimal(1, balanceDto.getBalance());
                ps.setTimestamp(2, Timestamp.valueOf(balanceDto.getLastUpdatedAt()));
                ps.setString(3, balanceDto.getCurrencyName());
            }

            @Override
            public int getBatchSize() {
                return balances.size();
            }
        });
    }

    @Override
    public List<BalanceDto> getCurrencyBalances() {
        final String sql = "SELECT currency_name, balance, last_updated_at FROM CURRENT_CURRENCY_BALANCES";

        return masterJdbcTemplate.query(sql, (rs, row) -> BalanceDto.builder()
                .currencyName(rs.getString("currency_name"))
                .balance(rs.getBigDecimal("balance"))
                .lastUpdatedAt(rs.getTimestamp("last_updated_at").toLocalDateTime())
                .build());
    }

    @Transactional
    @Override
    public boolean updateCurrencyPair(CurrencyPair currencyPair) {
        String sql = "UPDATE CURRENCY_PAIR SET currency1_id = : currency1_id, currency2_id = :currency2_id," +
                "name = :name, hidden = :hidden, market = :market, permitted_link = :permitted_link," +
                "type = :type WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("currency1_id", currencyPair.getCurrency1().getId());
        params.put("currency2_id", currencyPair.getCurrency2().getId());
        params.put("name", currencyPair.getName());
        params.put("hidden", currencyPair.isHidden());
        params.put("market", currencyPair.getMarket());
        params.put("permitted_link", currencyPair.isPermittedLink());
        params.put("type", currencyPair.getPairType().name());

        return masterJdbcTemplate.update(sql, params) > 0;
    }

    @Override
    public boolean updateCurrencyPairVolume(Integer currencyPairId, BigDecimal volume) {
        String sql = "UPDATE CURRENCY_PAIR SET top_market_volume = ";
        if (volume == null) {
            sql += "NULL ";
        } else {
            sql += ":volume";
        }

        sql += " WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", currencyPairId);
        if (volume != null) {
            params.put("volume", volume);
        }
        return masterJdbcTemplate.update(sql, params) > 0;
    }

    @Override
    public List<MarketVolume> getAllMarketVolumes() {
        String sql = "SELECT * FROM CURRENCY_PAIR_MARKET_VOLUMES";
        return masterJdbcTemplate.query(sql, (rs, row) -> MarketVolume.builder()
                .name(rs.getString("name"))
                .marketVolume(rs.getBigDecimal("market_volume"))
                .build());
    }

    @Override
    public boolean updateDefaultMarketVolume(String name, BigDecimal marketVolume) {
        String sql = "UPDATE CURRENCY_PAIR_MARKET_VOLUMES SET market_volume = :volume WHERE name = :name";
        Map<String, Object> params = new HashMap<>();
        params.put("volume", marketVolume);
        params.put("name", name);
        return masterJdbcTemplate.update(sql, params) > 0;
    }

    @Override
    public CurrencyLimit getCurrencyLimit(Integer currencyId, Integer roleId, Integer operationType) {
        String sql = "SELECT " +
                "CL.currency_id, " +
                "CL.min_sum, " +
                "CL.min_sum_usd, " +
                "CL.usd_rate, " +
                "IFNULL(CL.max_sum, 999999999999) as max_sum, " +
                "CL.max_daily_request, " +
                "CL.recalculate_to_usd " +
                "FROM CURRENCY_LIMIT CL " +
                "WHERE user_role_id = :role_id AND CL.operation_type_id = :operation_type_id AND CL.currency_id = :currency_id ";

        final Map<String, Object> params = new HashMap<String, Object>() {{
            put("role_id", roleId);
            put("currency_id", currencyId);
            put("operation_type_id", operationType);
        }};

        return masterJdbcTemplate.queryForObject(sql, params, (rs, row) -> CurrencyLimit.builder()
                .minSum(rs.getBigDecimal("min_sum"))
                .minSumUsdRate(rs.getBigDecimal("min_sum_usd"))
                .maxSum(rs.getBigDecimal("max_sum"))
                .currencyUsdRate(rs.getBigDecimal("usd_rate"))
                .maxDailyRequest(rs.getInt("max_daily_request"))
                .recalculateToUsd(rs.getBoolean("recalculate_to_usd"))
                .build());
    }

    @Override
    public CurrencyPairWithRestriction findCurrencyPairWithRestrictionRestrictions(Integer currencyPairId) {
        String sql = "SELECT id, currency1_id, currency2_id, name, market, type, top_market, top_market_volume, " +
                "(select name from CURRENCY where id = currency1_id) as currency1_name, " +
                "(select name from CURRENCY where id = currency2_id) as currency2_name, " +
                "(select group_concat(cpr.restriction_name) from CURRENCY_PAIR_RESTRICTION cpr " +
                "   where currency_pair_id = :currencyPairId ) as restrictions " +
                " FROM CURRENCY_PAIR " +
                "WHERE id = :currencyPairId ";
        Map<String, String> namedParameters = new HashMap<>();
        namedParameters.put("currencyPairId", String.valueOf(currencyPairId));
        return slaveJdbcTemplate.queryForObject(sql, namedParameters, (rs, rowNum) -> {
            CurrencyPairWithRestriction pair = new CurrencyPairWithRestriction(currencyPairRowMapper.mapRow(rs, rowNum));
            String restrictions = rs.getString("restrictions");
            if (!StringUtils.isEmpty(restrictions)) {
                pair.setTradeRestriction(Arrays.stream(restrictions.split(","))
                        .map(CurrencyPairRestrictionsEnum::valueOf)
                        .collect(Collectors.toList()));
            }
            return pair;
        });
    }

    @Override
    public void insertCurrencyPairRestriction(Integer currencyPairId, CurrencyPairRestrictionsEnum restrictionsEnum) {
        final String sql = "INSERT INTO CURRENCY_PAIR_RESTRICTION (currency_pair_id, restriction_name) values (:id, :name)";
        MapSqlParameterSource parameters = new MapSqlParameterSource()
            .addValue("id", currencyPairId)
            .addValue("name", restrictionsEnum.name());

        int result = masterJdbcTemplate.update(sql, parameters);
        if (result <= 0) {
            throw new RuntimeException("error, cant add new restriction " + currencyPairId + " " + restrictionsEnum);
        }
    }

    @Override
    public void deleteCurrencyPairRestriction(Integer currencyPairId, CurrencyPairRestrictionsEnum restrictionsEnum) {
        final String sql = "DELETE FROM CURRENCY_PAIR_RESTRICTION WHERE restriction_name = :name and currency_pair_id = :id ";
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", currencyPairId)
                .addValue("name", restrictionsEnum.name());

        int result = masterJdbcTemplate.update(sql, parameters);
        if (result <= 0) {
            throw new RuntimeException("error, cant remove restriction " + currencyPairId + " " + restrictionsEnum);
        }
    }

    @Override
    public List<CurrencyPairWithRestriction> findAllCurrencyPairWithRestrictions() {
        String sql = "SELECT id, currency1_id, currency2_id, name, market, type, top_market, top_market_volume, " +
                "(select name from CURRENCY where id = currency1_id) as currency1_name, " +
                "(select name from CURRENCY where id = currency2_id) as currency2_name, " +
                "(select group_concat(cpr.restriction_name) from CURRENCY_PAIR_RESTRICTION cpr " +
                "   where currency_pair_id = CURRENCY_PAIR.id ) as restrictions " +
                " FROM CURRENCY_PAIR ";
        return slaveJdbcTemplate.query(sql, (rs, rowNum) -> {
            CurrencyPairWithRestriction pair = new CurrencyPairWithRestriction(currencyPairRowMapper.mapRow(rs, rowNum));
            String restrictions = rs.getString("restrictions");
            if (!StringUtils.isEmpty(restrictions)) {
                pair.setTradeRestriction(Arrays.stream(restrictions.split(","))
                        .map(CurrencyPairRestrictionsEnum::valueOf)
                        .collect(Collectors.toList()));
            }
            return pair;
        });
    }


}
