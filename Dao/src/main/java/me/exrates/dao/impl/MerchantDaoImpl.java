package me.exrates.dao.impl;

import lombok.extern.log4j.Log4j;
import me.exrates.dao.MerchantDao;
import me.exrates.model.Merchant;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.MerchantImage;
import me.exrates.model.dto.MerchantCurrencyAutoParamDto;
import me.exrates.model.dto.MerchantCurrencyBasicInfoDto;
import me.exrates.model.dto.MerchantCurrencyLifetimeDto;
import me.exrates.model.dto.MerchantCurrencyOptionsDto;
import me.exrates.model.dto.MerchantCurrencyScaleDto;
import me.exrates.model.dto.merchants.btc.CoreWalletDto;
import me.exrates.model.dto.mobileApiDto.MerchantCurrencyApiDto;
import me.exrates.model.dto.mobileApiDto.MerchantImageShortenedDto;
import me.exrates.model.dto.mobileApiDto.TransferMerchantApiDto;
import me.exrates.model.enums.MerchantProcessType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.TransferTypeVoucher;
import me.exrates.model.enums.UserRole;
import me.exrates.model.exceptions.UnsupportedTransferProcessTypeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Log4j
@Repository
public class MerchantDaoImpl implements MerchantDao {

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate masterJdbcTemplate;

    @Autowired
    @Qualifier(value = "slaveTemplate")
    private NamedParameterJdbcTemplate slaveJdbcTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RowMapper<CoreWalletDto> coreWalletRowMapper = (rs, row) -> {
        CoreWalletDto dto = new CoreWalletDto();
        dto.setId(rs.getInt("id"));
        dto.setCurrencyId(rs.getInt("currency_id"));
        dto.setMerchantId(rs.getInt("merchant_id"));
        dto.setCurrencyName(rs.getString("currency_name"));
        dto.setCurrencyDescription(rs.getString("currency_description"));
        dto.setMerchantName(rs.getString("merchant_name"));
        dto.setTitleCode(rs.getString("title"));

        return dto;
    };

    @Override
    public Merchant create(Merchant merchant) {
        final String sql = "INSERT INTO MERCHANT (description, name) VALUES (:description,:name)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("description", merchant.getDescription())
                .addValue("name", merchant.getName());
        if (masterJdbcTemplate.update(sql, params, keyHolder) > 0) {
            merchant.setId(keyHolder.getKey().intValue());
            return merchant;
        }
        return null;
    }

    @Override
    public Merchant findById(int id) {
        final String sql = "SELECT * FROM MERCHANT WHERE id = :id";
        final Map<String, Integer> params = new HashMap<String, Integer>() {
            {
                put("id", id);
            }
        };
        return masterJdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(Merchant.class));
    }

    @Override
    public Merchant findByName(String name) {
        final String sql = "SELECT * FROM MERCHANT WHERE name = :name";
        final Map<String, String> params = Collections.singletonMap("name", name);
        try {
            return masterJdbcTemplate.queryForObject(sql, params, new BeanPropertyRowMapper<>(Merchant.class));
        } catch (EmptyResultDataAccessException ex) {
            log.debug("Method 'MerchantDaoImpl::findByName' did not return any result");
            return null;
        }
    }

    @Override
    public List<Merchant> findAll() {
        final String sql = "SELECT * FROM MERCHANT";
        return masterJdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Merchant.class));
    }


    @Override
    public List<Merchant> findAllByCurrency(int currencyId) {
        final String sql = "SELECT * FROM MERCHANT WHERE id in (SELECT merchant_id FROM MERCHANT_CURRENCY WHERE currency_id = :currencyId)";
        Map<String, Integer> params = new HashMap<String, Integer>() {
            {
                put("currencyId", currencyId);
            }
        };
        try {
            return masterJdbcTemplate.query(sql, params, (resultSet, i) -> {
                Merchant merchant = new Merchant();
                merchant.setDescription(resultSet.getString("description"));
                merchant.setId(resultSet.getInt("id"));
                merchant.setName(resultSet.getString("name"));
                merchant.setProcessType(MerchantProcessType.convert(resultSet.getString("process_type")));
                merchant.setTokensParrentId(resultSet.getInt("tokens_parrent_id"));
                return merchant;
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public BigDecimal getMinSum(int merchant, int currency) {
        final String sql = "SELECT min_sum FROM MERCHANT_CURRENCY WHERE merchant_id = :merchant AND currency_id = :currency";
        final Map<String, Integer> params = new HashMap<String, Integer>() {
            {
                put("merchant", merchant);
                put("currency", currency);
            }
        };
        return masterJdbcTemplate.queryForObject(sql, params, BigDecimal.class);
    }

    @Override
    public Optional<MerchantCurrency> findByMerchantAndCurrency(int merchantId, int currencyId) {
        final String sql = "SELECT MERCHANT.id as merchant_id,MERCHANT.name,MERCHANT.description, MERCHANT.process_type, " +
                " MERCHANT_CURRENCY.min_sum, " +
                " MERCHANT_CURRENCY.currency_id, MERCHANT_CURRENCY.merchant_input_commission, MERCHANT_CURRENCY.merchant_output_commission, " +
                " MERCHANT_CURRENCY.merchant_fixed_commission " +
                " FROM MERCHANT JOIN MERCHANT_CURRENCY " +
                " ON MERCHANT.id = MERCHANT_CURRENCY.merchant_id " +
                " WHERE MERCHANT_CURRENCY.merchant_id = :merchant_id AND MERCHANT_CURRENCY.currency_id = :currency_id ";
        Map<String, Integer> params = new HashMap<>();
        params.put("merchant_id", merchantId);
        params.put("currency_id", currencyId);

        try {
            return Optional.of(masterJdbcTemplate.queryForObject(sql, params, (resultSet, row) -> {
                MerchantCurrency merchantCurrency = new MerchantCurrency();
                merchantCurrency.setMerchantId(resultSet.getInt("merchant_id"));
                merchantCurrency.setName(resultSet.getString("name"));
                merchantCurrency.setDescription(resultSet.getString("description"));
                merchantCurrency.setMinSum(resultSet.getBigDecimal("min_sum"));
                merchantCurrency.setCurrencyId(resultSet.getInt("currency_id"));
                merchantCurrency.setInputCommission(resultSet.getBigDecimal("merchant_input_commission"));
                merchantCurrency.setOutputCommission(resultSet.getBigDecimal("merchant_output_commission"));
                merchantCurrency.setFixedMinCommission(resultSet.getBigDecimal("merchant_fixed_commission"));
                merchantCurrency.setProcessType(resultSet.getString("process_type"));
                final String sqlInner = "SELECT * FROM MERCHANT_IMAGE where merchant_id = :merchant_id" +
                        " AND currency_id = :currency_id;";
                Map<String, Integer> innerParams = new HashMap<String, Integer>();
                innerParams.put("merchant_id", resultSet.getInt("merchant_id"));
                innerParams.put("currency_id", resultSet.getInt("currency_id"));
                merchantCurrency.setListMerchantImage(masterJdbcTemplate.query(sqlInner, innerParams, new BeanPropertyRowMapper<>(MerchantImage.class)));
                return merchantCurrency;
            }));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }


    @Override
    public List<MerchantCurrency> findAllUnblockedForOperationTypeByCurrencies(List<Integer> currenciesId, OperationType operationType) {
        String blockClause = "";
        if (operationType == OperationType.INPUT) {
            blockClause = " AND MERCHANT_CURRENCY.refill_block = 0";
        } else if (operationType == OperationType.OUTPUT) {
            blockClause = " AND MERCHANT_CURRENCY.withdraw_block = 0";
        } else if (operationType == OperationType.USER_TRANSFER) {
            blockClause = " AND MERCHANT_CURRENCY.transfer_block = 0";
        }

        final String sql = "SELECT MERCHANT.id as merchant_id,MERCHANT.name,MERCHANT.description, MERCHANT.process_type, MERCHANT_CURRENCY.refill_block, MERCHANT.needVerification AS needVerification," +
                " MERCHANT_CURRENCY.min_sum, " +
                " MERCHANT_CURRENCY.currency_id, MERCHANT_CURRENCY.merchant_input_commission, MERCHANT_CURRENCY.merchant_output_commission, " +
                " MERCHANT_CURRENCY.merchant_fixed_commission " +
                " FROM MERCHANT JOIN MERCHANT_CURRENCY " +
                " ON MERCHANT.id = MERCHANT_CURRENCY.merchant_id WHERE MERCHANT_CURRENCY.currency_id in (:currenciesId)" +
                blockClause + " ORDER BY MERCHANT.merchant_order";

        try {
            return masterJdbcTemplate.query(sql, Collections.singletonMap("currenciesId", currenciesId), (resultSet, i) -> {
                MerchantCurrency merchantCurrency = new MerchantCurrency();
                merchantCurrency.setMerchantId(resultSet.getInt("merchant_id"));
                merchantCurrency.setName(resultSet.getString("name"));
                merchantCurrency.setDescription(resultSet.getString("description"));
                merchantCurrency.setMinSum(resultSet.getBigDecimal("min_sum"));
                merchantCurrency.setCurrencyId(resultSet.getInt("currency_id"));
                merchantCurrency.setInputCommission(resultSet.getBigDecimal("merchant_input_commission"));
                merchantCurrency.setOutputCommission(resultSet.getBigDecimal("merchant_output_commission"));
                merchantCurrency.setFixedMinCommission(resultSet.getBigDecimal("merchant_fixed_commission"));
                merchantCurrency.setProcessType(resultSet.getString("process_type"));
                final String sqlInner = "SELECT * FROM MERCHANT_IMAGE where merchant_id = :merchant_id" +
                        " AND currency_id = :currency_id;";
                Map<String, Integer> params = new HashMap<String, Integer>();
                params.put("merchant_id", resultSet.getInt("merchant_id"));
                params.put("currency_id", resultSet.getInt("currency_id"));
                merchantCurrency.setListMerchantImage(masterJdbcTemplate.query(sqlInner, params, new BeanPropertyRowMapper<>(MerchantImage.class)));
                merchantCurrency.setNeedVerification(resultSet.getBoolean("needVerification"));
                merchantCurrency.setAvailableForRefill(resultSet.getInt("refill_block") == 0);
                return merchantCurrency;
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<MerchantCurrencyApiDto> findAllMerchantCurrencies(Integer currencyId, UserRole userRole, List<String> merchantProcessTypes) {

        String whereClause = currencyId == null ? "" : " AND MERCHANT_CURRENCY.currency_id = :currency_id";

        final String sql = "SELECT MERCHANT.id as merchant_id, MERCHANT.name, MERCHANT.service_bean_name, MERCHANT.process_type, " +
                "                 MERCHANT_CURRENCY.currency_id, MERCHANT_CURRENCY.merchant_input_commission, MERCHANT_CURRENCY.merchant_output_commission, MERCHANT_CURRENCY.merchant_transfer_commission,  " +
                "                 MERCHANT_CURRENCY.withdraw_block, MERCHANT_CURRENCY.refill_block, MERCHANT_CURRENCY.transfer_block, LIMIT_WITHDRAW.min_sum AS min_withdraw_sum, " +
                "                 LIMIT_REFILL.min_sum AS min_refill_sum, LIMIT_TRANSFER.min_sum AS min_transfer_sum, MERCHANT_CURRENCY.merchant_fixed_commission " +
                "                FROM MERCHANT " +
                "                JOIN MERCHANT_CURRENCY ON MERCHANT.id = MERCHANT_CURRENCY.merchant_id " +
                "                JOIN CURRENCY_LIMIT AS LIMIT_WITHDRAW ON MERCHANT_CURRENCY.currency_id = LIMIT_WITHDRAW.currency_id " +
                "                                  AND LIMIT_WITHDRAW.operation_type_id = 2 AND LIMIT_WITHDRAW.user_role_id = :user_role_id " +
                "                JOIN CURRENCY_LIMIT AS LIMIT_REFILL ON MERCHANT_CURRENCY.currency_id = LIMIT_REFILL.currency_id " +
                "                                  AND LIMIT_REFILL.operation_type_id = 1 AND LIMIT_REFILL.user_role_id = :user_role_id " +
                "             JOIN CURRENCY_LIMIT AS LIMIT_TRANSFER ON MERCHANT_CURRENCY.currency_id = LIMIT_TRANSFER.currency_id " +
                "                                  AND LIMIT_TRANSFER.operation_type_id = 9 AND LIMIT_TRANSFER.user_role_id = :user_role_id " +
                "             WHERE process_type IN (:process_types)" + whereClause;
        Map<String, Object> paramMap = new HashMap<String, Object>() {{
            put("currency_id", currencyId);
            put("user_role_id", userRole.getRole());
            put("process_types", merchantProcessTypes);
        }};

        try {
            return masterJdbcTemplate.query(sql, paramMap, (resultSet, i) -> {
                MerchantCurrencyApiDto merchantCurrencyApiDto = new MerchantCurrencyApiDto();
                merchantCurrencyApiDto.setMerchantId(resultSet.getInt("merchant_id"));
                merchantCurrencyApiDto.setCurrencyId(resultSet.getInt("currency_id"));
                merchantCurrencyApiDto.setName(resultSet.getString("name"));
                merchantCurrencyApiDto.setMinInputSum(resultSet.getBigDecimal("min_refill_sum"));
                merchantCurrencyApiDto.setMinOutputSum(resultSet.getBigDecimal("min_withdraw_sum"));
                merchantCurrencyApiDto.setMinTransferSum(resultSet.getBigDecimal("min_transfer_sum"));
                merchantCurrencyApiDto.setInputCommission(resultSet.getBigDecimal("merchant_input_commission"));
                merchantCurrencyApiDto.setOutputCommission(resultSet.getBigDecimal("merchant_output_commission"));
                merchantCurrencyApiDto.setTransferCommission(resultSet.getBigDecimal("merchant_transfer_commission"));
                merchantCurrencyApiDto.setIsWithdrawBlocked(resultSet.getBoolean("withdraw_block"));
                merchantCurrencyApiDto.setIsRefillBlocked(resultSet.getBoolean("refill_block"));
                merchantCurrencyApiDto.setIsTransferBlocked(resultSet.getBoolean("transfer_block"));
                merchantCurrencyApiDto.setMinFixedCommission(resultSet.getBigDecimal("merchant_fixed_commission"));
                final String sqlInner = "SELECT id, image_path FROM birzha.MERCHANT_IMAGE where merchant_id = :merchant_id" +
                        " AND currency_id = :currency_id;";
                Map<String, Integer> params = new HashMap<String, Integer>();
                params.put("merchant_id", resultSet.getInt("merchant_id"));
                params.put("currency_id", resultSet.getInt("currency_id"));
                merchantCurrencyApiDto.setListMerchantImage(masterJdbcTemplate.query(sqlInner, params, new BeanPropertyRowMapper<>(MerchantImageShortenedDto.class)));
                merchantCurrencyApiDto.setServiceBeanName(resultSet.getString("service_bean_name"));
                merchantCurrencyApiDto.setProcessType(resultSet.getString("process_type"));
                return merchantCurrencyApiDto;
            });
        } catch (EmptyResultDataAccessException e) {
            return Collections.EMPTY_LIST;
        }
    }


    @Override
    public List<TransferMerchantApiDto> findTransferMerchants() {
        String sql = "SELECT id, name, service_bean_name FROM MERCHANT WHERE process_type = 'TRANSFER'";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            TransferMerchantApiDto dto = new TransferMerchantApiDto();
            dto.setMerchantId(rs.getInt("id"));
            dto.setName(rs.getString("name"));
            dto.setServiceBeanName(rs.getString("service_bean_name"));
            String sqlInner = "SELECT DISTINCT currency_id FROM MERCHANT_CURRENCY where merchant_id = :merchant_id" +
                    " AND transfer_block = 1;";
            List<Integer> blockedForCurrencies = masterJdbcTemplate.queryForList(sqlInner, Collections.singletonMap("merchant_id", rs.getInt("id")), Integer.class);
            dto.setBlockedForCurrencies(blockedForCurrencies);
            return dto;
        });
    }

    @Override
    public List<Integer> findCurrenciesIdsByType(List<String> processTypes) {
        final String sql = "SELECT MC.currency_id FROM MERCHANT_CURRENCY MC " +
                " JOIN MERCHANT M ON MC.merchant_id = M.id " +
                " WHERE M.process_type IN (:process_type) ";
        return masterJdbcTemplate.queryForList(sql,
                Collections.singletonMap("process_type", processTypes), Integer.class);
    }

    @Override
    public List<MerchantCurrencyOptionsDto> findMerchantCurrencyOptions(List<String> processTypes) {
        final String sql = "SELECT" +
                " MERCHANT.id as merchant_id," +
                " MERCHANT.name AS merchant_name," +
                " CURRENCY.id AS currency_id," +
                " CURRENCY.name AS currency_name," +
                " MERCHANT_CURRENCY.merchant_input_commission," +
                " MERCHANT_CURRENCY.merchant_output_commission," +
                " MERCHANT_CURRENCY.merchant_transfer_commission," +
                " MERCHANT_CURRENCY.withdraw_block," +
                " MERCHANT_CURRENCY.refill_block," +
                " MERCHANT_CURRENCY.transfer_block," +
                " MERCHANT_CURRENCY.merchant_fixed_commission," +
                " MERCHANT_CURRENCY.merchant_fixed_commission_usd," +
                " MERCHANT_CURRENCY.usd_rate," +
                " MERCHANT_CURRENCY.withdraw_auto_enabled," +
                " MERCHANT_CURRENCY.withdraw_auto_delay_seconds," +
                " MERCHANT_CURRENCY.withdraw_auto_threshold_amount," +
                " MERCHANT_CURRENCY.subtract_merchant_commission_for_withdraw, " +
                " MERCHANT_CURRENCY.recalculate_to_usd " +
                "FROM MERCHANT " +
                "JOIN MERCHANT_CURRENCY ON MERCHANT.id = MERCHANT_CURRENCY.merchant_id " +
                "JOIN CURRENCY ON MERCHANT_CURRENCY.currency_id = CURRENCY.id AND CURRENCY.hidden != 1 " +
                (processTypes.isEmpty() ? "" : "WHERE MERCHANT.process_type IN (:process_types) ") +
                "ORDER BY merchant_id, currency_id";

        Map<String, List<String>> params = new HashMap<String, List<String>>();
        params.put("process_types", processTypes);

        return masterJdbcTemplate.query(sql, params, (rs, rowNum) -> MerchantCurrencyOptionsDto.builder()
                .merchantId(rs.getInt("merchant_id"))
                .currencyId(rs.getInt("currency_id"))
                .merchantName(rs.getString("merchant_name"))
                .currencyName(rs.getString("currency_name"))
                .inputCommission(rs.getBigDecimal("merchant_input_commission"))
                .outputCommission(rs.getBigDecimal("merchant_output_commission"))
                .transferCommission(rs.getBigDecimal("merchant_transfer_commission"))
                .isRefillBlocked(rs.getBoolean("refill_block"))
                .isWithdrawBlocked(rs.getBoolean("withdraw_block"))
                .isTransferBlocked(rs.getBoolean("transfer_block"))
                .minFixedCommission(rs.getBigDecimal("merchant_fixed_commission"))
                .minFixedCommissionUsdRate(rs.getBigDecimal("merchant_fixed_commission_usd"))
                .currencyUsdRate(rs.getBigDecimal("usd_rate"))
                .withdrawAutoEnabled(rs.getBoolean("withdraw_auto_enabled"))
                .withdrawAutoDelaySeconds(rs.getInt("withdraw_auto_delay_seconds"))
                .withdrawAutoThresholdAmount(rs.getBigDecimal("withdraw_auto_threshold_amount"))
                .isMerchantCommissionSubtractedForWithdraw(rs.getBoolean("subtract_merchant_commission_for_withdraw"))
                .recalculateToUsd(rs.getBoolean("recalculate_to_usd"))
                .build());
    }

    @Override
    public void toggleSubtractMerchantCommissionForWithdraw(String merchantName, String currencyName, boolean subtractMerchantCommissionForWithdraw) {
        String sql = "UPDATE MERCHANT_CURRENCY " +
                "SET subtract_merchant_commission_for_withdraw = :subtract_merchant_commission " +
                "WHERE merchant_id = (SELECT id FROM MERCHANT WHERE name = :merchant_name) " +
                "AND currency_id = (SELECT id FROM CURRENCY WHERE name = :currency_name)";

        Map<String, Object> params = new HashMap<>();
        params.put("subtract_merchant_commission", subtractMerchantCommissionForWithdraw);
        params.put("merchant_name", merchantName);
        params.put("currency_name", currencyName);

        masterJdbcTemplate.update(sql, params);
    }

    @Override
    public void toggleMerchantBlock(Integer merchantId, Integer currencyId, OperationType operationType) {
        String fieldToToggle = resolveBlockFieldByOperationType(operationType);
        String sql = "UPDATE MERCHANT_CURRENCY SET " + fieldToToggle + " = !" + fieldToToggle +
                " WHERE merchant_id = :merchant_id AND currency_id = :currency_id ";
        Map<String, Integer> params = new HashMap<>();
        params.put("merchant_id", merchantId);
        params.put("currency_id", currencyId);
        masterJdbcTemplate.update(sql, params);
    }

    @Override
    public void setBlockForAllNonTransfer(OperationType operationType) {

        String blockField = resolveBlockFieldByOperationType(operationType);
        String sql = "UPDATE MERCHANT_CURRENCY MC " +
                "JOIN MERCHANT M ON MC.merchant_id = M.id " +
                "SET MC." + blockField + " = 1 WHERE M.process_type != 'TRANSFER'";
        jdbcTemplate.update(sql);
    }

    @Override
    public void backupBlockState(OperationType operationType) {

        String blockField = resolveBlockFieldByOperationType(operationType);
        String sql = "UPDATE MERCHANT_CURRENCY MC " +
                "JOIN MERCHANT M ON MC.merchant_id = M.id " +
                "SET MC." + blockField + "_backup" + " = MC." + blockField + " WHERE M.process_type != 'TRANSFER'";
        jdbcTemplate.update(sql);
    }

    @Override
    public void restoreBlockState(OperationType operationType) {

        String blockField = resolveBlockFieldByOperationType(operationType);
        String sql = "UPDATE MERCHANT_CURRENCY MC " +
                "JOIN MERCHANT M ON MC.merchant_id = M.id " +
                "SET MC." + blockField + " = MC." + blockField + "_backup" + " WHERE M.process_type != 'TRANSFER'";
        jdbcTemplate.update(sql);
    }

    @Override
    public boolean isBlockStateBackupValid(OperationType operationType) {

        String blockField = resolveBlockFieldByOperationType(operationType);
        String sql = "select distinct MC." + blockField + "_backup from MERCHANT_CURRENCY MC " +
                "JOIN MERCHANT M ON MC.merchant_id = M.id " +
                "WHERE M.process_type != 'TRANSFER'";
        return jdbcTemplate.queryForList(sql).size() > 1;
    }

    @Override
    public boolean isBlockStateValid(OperationType operationType) {

        String blockField = resolveBlockFieldByOperationType(operationType);
        String sql = "select distinct MC." + blockField + " from MERCHANT_CURRENCY MC " +
                "JOIN MERCHANT M ON MC.merchant_id = M.id " +
                "WHERE M.process_type != 'TRANSFER'";
        return jdbcTemplate.queryForList(sql).size() > 1;
    }


    @Override
    public void setBlockForMerchant(Integer merchantId, Integer currencyId, OperationType operationType, boolean blockStatus) {
        String blockField = resolveBlockFieldByOperationType(operationType);
        String sql = "UPDATE MERCHANT_CURRENCY SET " + blockField + " = :block" +
                " WHERE merchant_id = :merchant_id AND currency_id = :currency_id ";
        Map<String, Integer> params = new HashMap<>();
        params.put("block", blockStatus ? 1 : 0);
        params.put("merchant_id", merchantId);
        params.put("currency_id", currencyId);
        masterJdbcTemplate.update(sql, params);
    }

    @Override
    public boolean checkMerchantBlock(Integer merchantId, Integer currencyId, OperationType operationType) {
        String blockField = resolveBlockFieldByOperationType(operationType);
        String sql = "SELECT " + blockField + " FROM MERCHANT_CURRENCY " +
                " WHERE merchant_id = :merchant_id AND currency_id = :currency_id ";
        Map<String, Integer> params = new HashMap<>();
        params.put("merchant_id", merchantId);
        params.put("currency_id", currencyId);
        return masterJdbcTemplate.queryForObject(sql, params, Boolean.class);
    }

    private String resolveBlockFieldByOperationType(OperationType operationType) {
        String blockField;
        switch (operationType) {
            case INPUT:
                blockField = "refill_block";
                break;
            case OUTPUT:
                blockField = "withdraw_block";
                break;
            case USER_TRANSFER:
                blockField = "transfer_block";
                break;
            default:
                throw new IllegalArgumentException("Incorrect operation type: " + operationType);
        }
        return blockField;
    }

    @Override
    public void setAutoWithdrawParamsByMerchantAndCurrency(
            Integer merchantId,
            Integer currencyId,
            Boolean withdrawAutoEnabled,
            Integer withdrawAutoDelaySeconds,
            BigDecimal withdrawAutoThresholdAmount
    ) {
        String sql = "UPDATE MERCHANT_CURRENCY SET " +
                " withdraw_auto_enabled = :withdraw_auto_enabled, " +
                " withdraw_auto_delay_seconds = :withdraw_auto_delay_seconds, " +
                " withdraw_auto_threshold_amount = :withdraw_auto_threshold_amount " +
                " WHERE merchant_id = :merchant_id AND currency_id = :currency_id ";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("withdraw_auto_enabled", withdrawAutoEnabled);
            put("withdraw_auto_delay_seconds", withdrawAutoDelaySeconds);
            put("withdraw_auto_threshold_amount", withdrawAutoThresholdAmount);
            put("merchant_id", merchantId);
            put("currency_id", currencyId);
        }};
        masterJdbcTemplate.update(sql, params);
    }

    @Override
    public MerchantCurrencyAutoParamDto findAutoWithdrawParamsByMerchantAndCurrency(
            Integer merchantId,
            Integer currencyId
    ) {
        String sql = "SELECT withdraw_auto_enabled, withdraw_auto_threshold_amount, withdraw_auto_delay_seconds " +
                " FROM MERCHANT_CURRENCY " +
                " WHERE merchant_id = :merchant_id AND currency_id = :currency_id ";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("merchant_id", merchantId);
            put("currency_id", currencyId);
        }};
        return masterJdbcTemplate.queryForObject(sql, params, (resultSet, i) -> {
            MerchantCurrencyAutoParamDto dto = new MerchantCurrencyAutoParamDto();
            dto.setWithdrawAutoEnabled(resultSet.getBoolean("withdraw_auto_enabled"));
            dto.setWithdrawAutoThresholdAmount(resultSet.getBigDecimal("withdraw_auto_threshold_amount"));
            dto.setWithdrawAutoDelaySeconds(resultSet.getInt("withdraw_auto_delay_seconds"));
            return dto;
        });
    }

    @Override
    public List<String> retrieveBtcCoreBasedMerchantNames() {
        String sql = "SELECT name FROM MERCHANT JOIN CRYPTO_CORE_WALLET core ON MERCHANT.id = core.merchant_id";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    @Override
    public Optional<CoreWalletDto> retrieveCoreWalletByMerchantName(String merchantName) {
        String sql = "SELECT ccw.id, ccw.merchant_id, ccw.currency_id, m.name AS merchant_name, " +
                "c.name AS currency_name, c.description AS currency_description, ccw.title_code AS title " +
                "FROM CRYPTO_CORE_WALLET ccw " +
                "  JOIN MERCHANT m ON ccw.merchant_id = m.id " +
                "  JOIN CURRENCY c ON ccw.currency_id = c.id " +
                "WHERE m.name = :merchant_name";
        try {
            return Optional.of(masterJdbcTemplate.queryForObject(sql, Collections.singletonMap("merchant_name", merchantName), coreWalletRowMapper));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<CoreWalletDto> retrieveCoreWallets() {
        String sql = "SELECT ccw.id, ccw.merchant_id, ccw.currency_id, m.name AS merchant_name, " +
                " c.name AS currency_name, c.description AS currency_description, ccw.title_code AS title " +
                "FROM CRYPTO_CORE_WALLET ccw " +
                "  JOIN MERCHANT m ON ccw.merchant_id = m.id " +
                "  JOIN CURRENCY c ON ccw.currency_id = c.id " +
                "ORDER BY currency_name ASC;";

        return jdbcTemplate.query(sql, coreWalletRowMapper);

    }

    @Override
    public List<MerchantCurrencyLifetimeDto> findMerchantCurrencyWithRefillLifetime() {
        String sql = "SELECT currency_id, merchant_id, refill_lifetime_hours " +
                " FROM MERCHANT_CURRENCY " +
                " WHERE refill_lifetime_hours > 0 ";
        return jdbcTemplate.query(sql, (rs, i) -> {
            MerchantCurrencyLifetimeDto result = new MerchantCurrencyLifetimeDto();
            result.setCurrencyId(rs.getInt("currency_id"));
            result.setMerchantId(rs.getInt("merchant_id"));
            result.setRefillLifetimeHours(rs.getInt("refill_lifetime_hours"));
            return result;
        });
    }

    @Override
    public MerchantCurrencyLifetimeDto findMerchantCurrencyLifetimeByMerchantIdAndCurrencyId(Integer merchantId, Integer currencyId) {
        String sql = "SELECT currency_id, merchant_id, refill_lifetime_hours " +
                " FROM MERCHANT_CURRENCY " +
                " WHERE " +
                "   merchant_id = :merchant_id " +
                "   AND currency_id = :currency_id";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("merchant_id", merchantId);
            put("currency_id", currencyId);
        }};
        return masterJdbcTemplate.queryForObject(sql, params, (rs, i) -> {
            MerchantCurrencyLifetimeDto result = new MerchantCurrencyLifetimeDto();
            result.setCurrencyId(rs.getInt("currency_id"));
            result.setMerchantId(rs.getInt("merchant_id"));
            result.setRefillLifetimeHours(rs.getInt("refill_lifetime_hours"));
            return result;
        });
    }

    @Override
    public MerchantCurrencyScaleDto findMerchantCurrencyScaleByMerchantIdAndCurrencyId(Integer merchantId, Integer currencyId) {
        String sql = "SELECT currency_id, merchant_id, " +
                "  IF(MERCHANT_CURRENCY.max_scale_for_refill IS NOT NULL, MERCHANT_CURRENCY.max_scale_for_refill, CURRENCY.max_scale_for_refill) AS max_scale_for_refill, " +
                "  IF(MERCHANT_CURRENCY.max_scale_for_withdraw IS NOT NULL, MERCHANT_CURRENCY.max_scale_for_withdraw, CURRENCY.max_scale_for_withdraw) AS max_scale_for_withdraw, " +
                "  IF(MERCHANT_CURRENCY.max_scale_for_transfer IS NOT NULL, MERCHANT_CURRENCY.max_scale_for_transfer, CURRENCY.max_scale_for_transfer) AS max_scale_for_transfer" +
                "  FROM MERCHANT_CURRENCY " +
                "  JOIN CURRENCY ON CURRENCY.id = MERCHANT_CURRENCY.currency_id " +
                "  WHERE merchant_id = :merchant_id " +
                "        AND currency_id = :currency_id";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("merchant_id", merchantId);
            put("currency_id", currencyId);
        }};
        return masterJdbcTemplate.queryForObject(sql, params, (rs, i) -> {
            MerchantCurrencyScaleDto result = new MerchantCurrencyScaleDto();
            result.setCurrencyId(rs.getInt("currency_id"));
            result.setMerchantId(rs.getInt("merchant_id"));
            result.setScaleForRefill((Integer) rs.getObject("max_scale_for_refill"));
            result.setScaleForWithdraw((Integer) rs.getObject("max_scale_for_withdraw"));
            result.setScaleForTransfer((Integer) rs.getObject("max_scale_for_transfer"));
            return result;
        });
    }

    @Override
    public boolean getSubtractFeeFromAmount(Integer merchantId, Integer currencyId) {
        String sql = "SELECT subtract_fee_from_amount FROM CRYPTO_CORE_WALLET WHERE merchant_id = :merchant_id " +
                "AND currency_id = :currency_id ";
        Map<String, Integer> params = new HashMap<>();
        params.put("merchant_id", merchantId);
        params.put("currency_id", currencyId);
        return masterJdbcTemplate.queryForObject(sql, params, Boolean.class);
    }

    @Override
    public void setSubtractFeeFromAmount(Integer merchantId, Integer currencyId, boolean subtractFeeFromAmount) {
        String sql = "UPDATE CRYPTO_CORE_WALLET SET  subtract_fee_from_amount = :subtract_fee WHERE merchant_id = :merchant_id " +
                "AND currency_id = :currency_id ";
        Map<String, Object> params = new HashMap<>();
        params.put("merchant_id", merchantId);
        params.put("currency_id", currencyId);
        params.put("subtract_fee", subtractFeeFromAmount);
        masterJdbcTemplate.update(sql, params);
    }

    @Override
    public Optional<String> getCoreWalletPassword(String merchantName, String currencyName) {
        String sql = "SELECT passphrase FROM CRYPTO_CORE_WALLET WHERE merchant_id = (SELECT id FROM MERCHANT WHERE name = :merchant_name) " +
                "AND currency_id = (SELECT id FROM CURRENCY WHERE name = :currency_name)";
        Map<String, String> params = new HashMap<>();
        params.put("merchant_name", merchantName);
        params.put("currency_name", currencyName);
        try {
            return Optional.ofNullable(masterJdbcTemplate.queryForObject(sql, params, String.class));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<MerchantCurrencyBasicInfoDto> findTokenMerchantsByParentId(Integer parentId) {
        final String sql = "SELECT M.name AS merchant_name, M.id AS merchant_id, CUR.name AS currency_name, CUR.id AS currency_id," +
                "CUR.max_scale_for_refill, CUR.max_scale_for_withdraw, CUR.max_scale_for_transfer " +
                " FROM MERCHANT M " +
                " JOIN MERCHANT_CURRENCY MC ON M.id = MC.merchant_id" +
                " JOIN CURRENCY CUR ON MC.currency_id = CUR.id" +
                " WHERE M.tokens_parrent_id = :parent_id";
        final Map<String, Integer> params = Collections.singletonMap("parent_id", parentId);
        return masterJdbcTemplate.query(sql, params, (rs, row) -> {
            MerchantCurrencyBasicInfoDto dto = new MerchantCurrencyBasicInfoDto();
            dto.setCurrencyId(rs.getInt("currency_id"));
            dto.setCurrencyName(rs.getString("currency_name"));
            dto.setMerchantId(rs.getInt("merchant_id"));
            dto.setMerchantName(rs.getString("merchant_name"));
            dto.setRefillScale(rs.getInt("max_scale_for_refill"));
            dto.setWithdrawScale(rs.getInt("max_scale_for_withdraw"));
            dto.setTransferScale(rs.getInt("max_scale_for_transfer"));
            return dto;
        });
    }

    @Override
    public BigDecimal getMerchantInputCommission(int merchantId, int currencyId, String childMerchant) {
        final String sql = "SELECT mi.input_commission" +
                " FROM MERCHANT_IMAGE mi" +
                " WHERE mi.merchant_id = :merchant_id AND mi.currency_id = :currency_id AND mi.child_merchant = :child_merchant";

        final Map<String, Object> params = new HashMap<String, Object>() {{
            put("merchant_id", merchantId);
            put("currency_id", currencyId);
            put("child_merchant", childMerchant);
        }};

        return slaveJdbcTemplate.queryForObject(sql, params, BigDecimal.class);
    }

    @Transactional
    @Override
    public boolean setPropertyRecalculateCommissionLimitToUsd(String merchantName, String currencyName, Boolean recalculateToUsd) {
        String sql = "UPDATE MERCHANT_CURRENCY " +
                "SET recalculate_to_usd = :recalculate_to_usd " +
                "WHERE merchant_id = (SELECT id FROM MERCHANT WHERE name = :merchant_name) " +
                "AND currency_id = (SELECT id FROM CURRENCY WHERE name = :currency_name)";

        Map<String, Object> params = new HashMap<>();
        params.put("merchant_name", merchantName);
        params.put("currency_name", currencyName);
        params.put("recalculate_to_usd", recalculateToUsd);

        return masterJdbcTemplate.update(sql, params) > 0;
    }

    @Transactional(readOnly = true)
    @Override
    public List<MerchantCurrencyOptionsDto> getAllMerchantCommissionsLimits() {
        String sql = "SELECT " +
                "MERCHANT_CURRENCY.merchant_id, " +
                "MERCHANT_CURRENCY.currency_id, " +
                "CURRENCY.name AS currency_name, " +
                "MERCHANT_CURRENCY.merchant_fixed_commission, " +
                "MERCHANT_CURRENCY.merchant_fixed_commission_usd, " +
                "MERCHANT_CURRENCY.usd_rate, " +
                "MERCHANT_CURRENCY.recalculate_to_usd " +
                "FROM MERCHANT_CURRENCY " +
                "JOIN CURRENCY ON MERCHANT_CURRENCY.currency_id = CURRENCY.id";

        return masterJdbcTemplate.query(sql, (rs, row) -> MerchantCurrencyOptionsDto.builder()
                .merchantId(rs.getInt("merchant_id"))
                .currencyId(rs.getInt("currency_id"))
                .currencyName(rs.getString("currency_name"))
                .minFixedCommission(rs.getBigDecimal("merchant_fixed_commission"))
                .minFixedCommissionUsdRate(rs.getBigDecimal("merchant_fixed_commission_usd"))
                .currencyUsdRate(rs.getBigDecimal("usd_rate"))
                .recalculateToUsd(rs.getBoolean("recalculate_to_usd"))
                .build());
    }

    @Override
    public boolean checkAvailable(Integer currencyId, Integer merchantId) {
        String sql = "SELECT refill_block FROM MERCHANT_CURRENCY WHERE currency_id = :currency_id AND merchant_id = :merchant_id";
        Map<String, Integer> params = new HashMap<>();
        params.put("merchant_id", merchantId);
        params.put("currency_id", currencyId);
        return masterJdbcTemplate.queryForObject(sql, params, Integer.class) == 0;
    }

    @Override
    public MerchantCurrency getMerchantByCurrencyForVoucher(Integer currencyId, TransferTypeVoucher transferType) {
        String blockClause;

        switch (transferType) {
            case INNER_VOUCHER:
                blockClause = "AND MERCHANT.name = 'VoucherTransfer'";
                break;
            case VOUCHER:
                blockClause = "AND MERCHANT.name = 'VoucherFreeTransfer'";
                break;
            case TRANSFER:
                blockClause = "AND MERCHANT.name = 'SimpleTransfer'";
                break;
            default:
                throw new UnsupportedTransferProcessTypeException("Error transfer type - " + transferType);
        }

        String sql = "SELECT" +
                "  MERCHANT.id as merchant_id," +
                "  MERCHANT.name," +
                "  MERCHANT.description," +
                "  MERCHANT.process_type," +
                "  MERCHANT_CURRENCY.min_sum," +
                "  MERCHANT_CURRENCY.currency_id," +
                "  MERCHANT_CURRENCY.merchant_input_commission," +
                "  MERCHANT_CURRENCY.merchant_output_commission," +
                "  MERCHANT_CURRENCY.merchant_fixed_commission" +
                " FROM MERCHANT" +
                "  JOIN MERCHANT_CURRENCY" +
                "    ON MERCHANT.id = MERCHANT_CURRENCY.merchant_id" +
                " WHERE MERCHANT_CURRENCY.currency_id in (:currency_id)" +
                "      AND MERCHANT_CURRENCY.transfer_block = 0 "
                + blockClause;

        return masterJdbcTemplate.queryForObject(sql, Collections.singletonMap("currency_id", currencyId), (resultSet, i) -> {
            MerchantCurrency merchantCurrency = new MerchantCurrency();
            merchantCurrency.setMerchantId(resultSet.getInt("merchant_id"));
            merchantCurrency.setName(resultSet.getString("name"));
            merchantCurrency.setDescription(resultSet.getString("description"));
            merchantCurrency.setMinSum(resultSet.getBigDecimal("min_sum"));
            merchantCurrency.setCurrencyId(resultSet.getInt("currency_id"));
            merchantCurrency.setInputCommission(resultSet.getBigDecimal("merchant_input_commission"));
            merchantCurrency.setOutputCommission(resultSet.getBigDecimal("merchant_output_commission"));
            merchantCurrency.setFixedMinCommission(resultSet.getBigDecimal("merchant_fixed_commission"));
            merchantCurrency.setProcessType(resultSet.getString("process_type"));
            return merchantCurrency;
        });
    }

    @Transactional
    @Override
    public void updateMerchantCommissionsLimits(List<MerchantCurrencyOptionsDto> merchantCommissionsLimits) {
        String sql = "UPDATE MERCHANT_CURRENCY " +
                "SET merchant_fixed_commission = ?, merchant_fixed_commission_usd = ?, usd_rate = ? " +
                "WHERE merchant_id = ? AND currency_id = ?";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                MerchantCurrencyOptionsDto dto = merchantCommissionsLimits.get(i);
                ps.setBigDecimal(1, dto.getMinFixedCommission());
                ps.setBigDecimal(2, dto.getMinFixedCommissionUsdRate());
                ps.setBigDecimal(3, dto.getCurrencyUsdRate());
                ps.setInt(4, dto.getMerchantId());
                ps.setInt(5, dto.getCurrencyId());
            }

            @Override
            public int getBatchSize() {
                return merchantCommissionsLimits.size();
            }
        });
    }
}

