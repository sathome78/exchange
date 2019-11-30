package me.exrates.dao.impl;

import me.exrates.dao.WithdrawRequestDao;
import me.exrates.model.ClientBank;
import me.exrates.model.PagingData;
import me.exrates.model.dto.WithdrawRequestCreateDto;
import me.exrates.model.dto.WithdrawRequestFlatAdditionalDataDto;
import me.exrates.model.dto.WithdrawRequestFlatDto;
import me.exrates.model.dto.WithdrawRequestFlatForReportDto;
import me.exrates.model.dto.WithdrawRequestInfoDto;
import me.exrates.model.dto.WithdrawRequestPostDto;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.WithdrawFilterData;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.invoice.InvoiceOperationPermission;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.singletonMap;
import static java.util.Objects.isNull;
import static java.util.Optional.of;
import static java.util.stream.Collectors.toList;
import static me.exrates.model.enums.TransactionSourceType.WITHDRAW;


/**
 * created by ValkSam
 */

@Repository
public class WithdrawRequestDaoImpl implements WithdrawRequestDao {

    private static final Logger log = LogManager.getLogger("withdraw");

    protected static RowMapper<WithdrawRequestFlatDto> withdrawRequestFlatDtoRowMapper = (rs, idx) -> {
        WithdrawRequestFlatDto withdrawRequestFlatDto = new WithdrawRequestFlatDto();
        withdrawRequestFlatDto.setId(rs.getInt("id"));
        withdrawRequestFlatDto.setWallet(rs.getString("wallet"));
        withdrawRequestFlatDto.setDestinationTag(rs.getString("destination_tag"));
        withdrawRequestFlatDto.setUserId(rs.getInt("user_id"));
        withdrawRequestFlatDto.setRecipientBankName(rs.getString("recipient_bank_name"));
        withdrawRequestFlatDto.setRecipientBankCode(rs.getString("recipient_bank_code"));
        withdrawRequestFlatDto.setUserFullName(rs.getString("user_full_name"));
        withdrawRequestFlatDto.setRemark(rs.getString("remark"));
        withdrawRequestFlatDto.setAmount(rs.getBigDecimal("amount"));
        withdrawRequestFlatDto.setCommissionAmount(rs.getBigDecimal("commission"));
        withdrawRequestFlatDto.setMerchantCommissionAmount(rs.getBigDecimal("merchant_commission"));
        withdrawRequestFlatDto.setCommissionId(rs.getInt("commission_id"));
        withdrawRequestFlatDto.setStatus(WithdrawStatusEnum.convert(rs.getInt("status_id")));
        withdrawRequestFlatDto.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
        withdrawRequestFlatDto.setStatusModificationDate(rs.getTimestamp("status_modification_date").toLocalDateTime());
        withdrawRequestFlatDto.setCurrencyId(rs.getInt("currency_id"));
        withdrawRequestFlatDto.setMerchantId(rs.getInt("merchant_id"));
        withdrawRequestFlatDto.setAdminHolderId(rs.getInt("admin_holder_id"));
        withdrawRequestFlatDto.setTransactionHash(rs.getString("transaction_hash"));
        withdrawRequestFlatDto.setAdditionalParams(rs.getString("additional_params"));
        withdrawRequestFlatDto.setMerchantCommissionCurrencyId(rs.getInt("merchant_commission_currency"));
        return withdrawRequestFlatDto;
    };

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Autowired
    @Qualifier(value = "slaveTemplate")
    private NamedParameterJdbcTemplate slaveJdbcTemplate;

    @Autowired
    @Qualifier(value = "slaveForReportsTemplate")
    private NamedParameterJdbcTemplate slaveForReportsTemplate;

    private Optional<Integer> blockById(int id) {
        String sql = "SELECT COUNT(*) " +
                "FROM WITHDRAW_REQUEST " +
                "WHERE WITHDRAW_REQUEST.id = :id " +
                "FOR UPDATE ";
        return of(jdbcTemplate.queryForObject(sql, singletonMap("id", id), Integer.class));
    }

    @Override
    public Integer findStatusIdByRequestId(Integer withdrawRequestId) {
        String sql = "SELECT status FROM WITHDRAW_REQUEST WHERE transaction_id = :request_id";
        return jdbcTemplate.queryForObject(sql, Collections.singletonMap("request_id", withdrawRequestId), Integer.class);
    }

    @Override
    public int create(WithdrawRequestCreateDto withdrawRequest) {
        final String sql = "INSERT INTO WITHDRAW_REQUEST " +
                "(wallet, recipient_bank_name, recipient_bank_code, user_full_name, remark, amount, commission, merchant_commission, status_id," +
                " date_creation, status_modification_date, currency_id, merchant_id, merchant_image_id, user_id, commission_id, destination_tag, merchant_commission_currency) " +
                "VALUES (:wallet, :payer_bank_name, :payer_bank_code, :user_full_name, :remark, :amount, :commission, :merchant_commission, :status_id," +
                " NOW(), NOW(), :currency_id, :merchant_id, :merchant_image_id, :user_id, :commission_id, :destination_tag, :merchant_commission_currency)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("wallet", withdrawRequest.getDestinationWallet())
                .addValue("destination_tag", withdrawRequest.getDestinationTag())
                .addValue("payer_bank_name", withdrawRequest.getRecipientBankName())
                .addValue("payer_bank_code", withdrawRequest.getRecipientBankCode())
                .addValue("user_full_name", withdrawRequest.getUserFullName())
                .addValue("remark", withdrawRequest.getRemark())
                .addValue("amount", withdrawRequest.getAmount())
                .addValue("commission", withdrawRequest.getCommission())
                .addValue("merchant_commission", withdrawRequest.getMerchantCommissionAmount())
                .addValue("status_id", withdrawRequest.getStatusId())
                .addValue("currency_id", withdrawRequest.getCurrencyId())
                .addValue("merchant_id", withdrawRequest.getMerchantId())
                .addValue("merchant_image_id", withdrawRequest.getMerchantImageId())
                .addValue("user_id", withdrawRequest.getUserId())
                .addValue("commission_id", withdrawRequest.getCommissionId())
                .addValue("merchant_commission_currency", withdrawRequest.getMerchantCommissionCurrencyId());
        jdbcTemplate.update(sql, params, keyHolder);
        return (int) keyHolder.getKey().longValue();
    }

    @Override
    public void setStatusById(Integer id, InvoiceStatus newStatus) {
        final String sql = "UPDATE WITHDRAW_REQUEST " +
                "  SET status_id = :new_status_id, " +
                "      status_modification_date = NOW() " +
                "  WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("new_status_id", newStatus.getCode());
        jdbcTemplate.update(sql, params);
    }

    @Override
    public void setHashAndParamsById(Integer id, Map<String, String> params) {
        if (params.isEmpty()) {
            return;
        }
        final String sql = "UPDATE WITHDRAW_REQUEST " +
                "  SET transaction_hash = :hash, " +
                "      status_modification_date = NOW(), additional_params = :additional_params " +
                "  WHERE id = :id";
        Map<String, Object> sqlParams = new HashMap<>();
        sqlParams.put("id", id);
        sqlParams.put("hash", params.get("hash"));
        sqlParams.put("additional_params", params.get("params"));
        jdbcTemplate.update(sql, sqlParams);
    }

    @Override
    public Optional<WithdrawRequestFlatDto> getFlatByIdAndBlock(int id) {
        blockById(id);
        return getFlatById(id);
    }

    @Override
    public Optional<WithdrawRequestFlatDto> getFlatById(int id) {
        String sql = "SELECT * " +
                " FROM WITHDRAW_REQUEST " +
                " WHERE id = :id";
        return of(jdbcTemplate.queryForObject(sql, singletonMap("id", id), withdrawRequestFlatDtoRowMapper));
    }


    @Override
    public PagingData<List<WithdrawRequestFlatDto>> getPermittedFlatByStatus(
            List<Integer> statusIdList,
            Integer requesterUserId,
            DataTableParams dataTableParams,
            WithdrawFilterData withdrawFilterData) {
        final String JOINS_FOR_USER =
                " JOIN USER ON USER.id = WITHDRAW_REQUEST.user_id ";
        final String JOINS_CURRENCY_AND_MERCHANT = " JOIN CURRENCY CUR ON (CUR.id=WITHDRAW_REQUEST.currency_id) " +
                " JOIN MERCHANT MER ON (MER.id=WITHDRAW_REQUEST.merchant_id) ";
        String filter = withdrawFilterData.getSQLFilterClause();
        String searchClause = dataTableParams.getSearchByEmailAndNickClause();
        String sqlBase =
                " FROM WITHDRAW_REQUEST " +
                        getPermissionClause(requesterUserId) +
                        JOINS_FOR_USER + JOINS_CURRENCY_AND_MERCHANT +
                        (statusIdList.isEmpty() ? "" : " WHERE status_id IN (:status_id_list) ");
        String whereClauseFilter = StringUtils.isEmpty(filter) ? "" : " AND ".concat(filter);
        String whereClauseSearch = StringUtils.isEmpty(searchClause) || !StringUtils.isEmpty(whereClauseFilter)
                ? "" : " AND ".concat(searchClause);
        String orderClause = dataTableParams.getOrderByClause();
        String offsetAndLimit = dataTableParams.getLimitAndOffsetClause();
        String sqlMain = String.join(" ", "SELECT WITHDRAW_REQUEST.*, IOP.invoice_operation_permission_id ",
                sqlBase, whereClauseFilter, whereClauseSearch, orderClause, offsetAndLimit);
        String sqlCount = String.join(" ", "SELECT COUNT(*) ", sqlBase, whereClauseFilter, whereClauseSearch);
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("status_id_list", statusIdList);
            put("requester_user_id", requesterUserId);
            put("operation_direction", "WITHDRAW");
            put("offset", dataTableParams.getStart());
            put("limit", dataTableParams.getLength());
        }};
        params.putAll(withdrawFilterData.getNamedParams());
        params.putAll(dataTableParams.getSearchNamedParams());
        log.debug("sql main {}", sqlMain);
        List<WithdrawRequestFlatDto> requests = jdbcTemplate.query(sqlMain, params, (rs, i) -> {
            WithdrawRequestFlatDto withdrawRequestFlatDto = withdrawRequestFlatDtoRowMapper.mapRow(rs, i);
            withdrawRequestFlatDto.setInvoiceOperationPermission(InvoiceOperationPermission.convert(rs.getInt("invoice_operation_permission_id")));

            int merchantImageId = rs.getInt("merchant_image_id");
            if (merchantImageId != 0) {
                final String imageName = getImageName(merchantImageId);
                withdrawRequestFlatDto.setMerchantImageName(imageName);
            }
            return withdrawRequestFlatDto;
        });
        Integer totalQuantity = jdbcTemplate.queryForObject(sqlCount, params, Integer.class);
        PagingData<List<WithdrawRequestFlatDto>> result = new PagingData<>();
        result.setData(requests);
        result.setFiltered(totalQuantity);
        result.setTotal(totalQuantity);
        return result;
    }

    private String getImageName(int merchantImageId) {
        String sql = "SELECT mi.image_name FROM MERCHANT_IMAGE mi WHERE mi.id = :image_id";
        return jdbcTemplate.queryForObject(sql, Collections.singletonMap("image_id", merchantImageId), String.class);
    }

    @Override
    public WithdrawRequestFlatDto getPermittedFlatById(
            Integer id,
            Integer requesterUserId) {
        String sql = "SELECT WITHDRAW_REQUEST.*, IOP.invoice_operation_permission_id " +
                " FROM WITHDRAW_REQUEST " +
                getPermissionClause(requesterUserId) +
                " WHERE WITHDRAW_REQUEST.id=:id ";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("id", id);
            put("requester_user_id", requesterUserId);
            put("operation_direction", "WITHDRAW");
        }};
        return jdbcTemplate.queryForObject(sql, params, (rs, i) -> {
            WithdrawRequestFlatDto withdrawRequestFlatDto = withdrawRequestFlatDtoRowMapper.mapRow(rs, i);
            withdrawRequestFlatDto.setInvoiceOperationPermission(InvoiceOperationPermission.convert(rs.getInt("invoice_operation_permission_id")));
            return withdrawRequestFlatDto;
        });
    }

    @Override
    public List<WithdrawRequestPostDto> getForPostByStatusList(Integer statusId) {
        String sql = " SELECT WR.*, " +
                " CUR.name AS currency_name, " +
                " M.name AS merchant_name, M.service_bean_name " +
                " FROM WITHDRAW_REQUEST WR " +
                " JOIN CURRENCY CUR ON (CUR.id = WR.currency_id) " +
                " JOIN MERCHANT M ON (M.id = WR.merchant_id) " +
                " WHERE status_id = :status_id ";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("status_id", statusId);
        }};
        return jdbcTemplate.query(sql, params, (rs, idx) -> {
            WithdrawRequestPostDto result = new WithdrawRequestPostDto();
            result.setId(rs.getInt("id"));
            result.setWallet(rs.getString("wallet"));
            result.setDestinationTag(rs.getString("destination_tag"));
            result.setRecipientBankName(rs.getString("recipient_bank_name"));
            result.setRecipientBankCode(rs.getString("recipient_bank_code"));
            result.setUserFullName(rs.getString("user_full_name"));
            result.setRemark(rs.getString("remark"));
            result.setAmount(rs.getBigDecimal("amount"));
            result.setCommissionAmount(rs.getBigDecimal("commission"));
            result.setStatus(WithdrawStatusEnum.convert(rs.getInt("status_id")));
            result.setCurrencyName(rs.getString("currency_name"));
            result.setMerchantName(rs.getString("merchant_name"));
            result.setMerchantServiceBeanName(rs.getString("service_bean_name"));
            result.setUserId(rs.getInt("user_id"));
            result.setMerchantId(rs.getInt("merchant_id"));
            result.setCurrencyId(rs.getInt("currency_id"));
            return result;
        });
    }

    @Override
    public WithdrawRequestFlatAdditionalDataDto getAdditionalDataForId(int id) {
        String sql = "SELECT " +
                "   CUR.name AS currency_name, " +
                "   USER.email AS user_email, " +
                "   ADMIN.email AS admin_email, " +
                "   M.name AS merchant_name, " +
                "   MC.subtract_merchant_commission_for_withdraw " +
                " FROM WITHDRAW_REQUEST WR " +
                " JOIN CURRENCY CUR ON (CUR.id = WR.currency_id) " +
                " JOIN USER USER ON (USER.id = WR.user_id) " +
                " LEFT JOIN USER ADMIN ON (ADMIN.id = WR.admin_holder_id) " +
                " JOIN MERCHANT M ON (M.id = WR.merchant_id)" +
                " JOIN MERCHANT_CURRENCY MC ON CUR.id = MC.currency_id AND M.id = MC.merchant_id " +
                " WHERE WR.id = :id";
        return jdbcTemplate.queryForObject(sql, singletonMap("id", id), (rs, idx) -> {
                    WithdrawRequestFlatAdditionalDataDto withdrawRequestFlatAdditionalDataDto = new WithdrawRequestFlatAdditionalDataDto();
                    withdrawRequestFlatAdditionalDataDto.setUserEmail(rs.getString("user_email"));
                    withdrawRequestFlatAdditionalDataDto.setAdminHolderEmail(rs.getString("admin_email"));
                    withdrawRequestFlatAdditionalDataDto.setCurrencyName(rs.getString("currency_name"));
                    withdrawRequestFlatAdditionalDataDto.setMerchantName(rs.getString("merchant_name"));
                    withdrawRequestFlatAdditionalDataDto.setIsMerchantCommissionSubtractedForWithdraw(
                            rs.getBoolean("subtract_merchant_commission_for_withdraw"));
                    return withdrawRequestFlatAdditionalDataDto;
                }
        );
    }

    @Override
    public void setHolderById(Integer id, Integer holderId) {
        final String sql = "UPDATE WITHDRAW_REQUEST " +
                "  SET admin_holder_id = :admin_holder_id " +
                "  WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("admin_holder_id", holderId);
        jdbcTemplate.update(sql, params);
    }

    @Override
    public void setInPostingStatusByStatus(Integer inPostingStatusId, List<Integer> statusIdList) {
        final String sql =
                "  UPDATE " +
                        "    WITHDRAW_REQUEST WR " +
                        "    JOIN MERCHANT_CURRENCY MC ON (MC.merchant_id = WR.merchant_id) AND " +
                        "     (MC.currency_id = WR.currency_id) AND " +
                        "     (MC.withdraw_auto_enabled = 1) AND " +
                        "     (WR.status_modification_date <= NOW() - INTERVAL MC.withdraw_auto_delay_seconds SECOND) " +
                        "  SET status_id = :new_status_id " +
                        "  WHERE WR.status_id IN (:status_id_list)  ";
        Map<String, Object> params = new HashMap<>();
        params.put("status_id_list", statusIdList);
        params.put("new_status_id", inPostingStatusId);
        jdbcTemplate.update(sql, params);
    }

    @Override
    public List<ClientBank> findClientBanksForCurrency(Integer currencyId) {
        final String sql = "SELECT id, currency_id, name, code " +
                " FROM CLIENT_BANK " +
                " WHERE currency_id = :currency_id";
        final Map<String, Integer> params = Collections.singletonMap("currency_id", currencyId);
        return jdbcTemplate.query(sql, params, (rs, rowNum) -> {
            ClientBank bank = new ClientBank();
            bank.setId(rs.getInt("id"));
            bank.setName(rs.getString("name"));
            bank.setCurrencyId(rs.getInt("currency_id"));
            bank.setCode(rs.getString("code"));
            return bank;
        });
    }

    @Override
    public boolean checkOutputRequests(int currencyId, String email) {
        final String sql = "SELECT " +
                "(SELECT COUNT(*)" +
                " FROM WITHDRAW_REQUEST wr" +
                " JOIN USER u ON u.id = wr.user_id" +
                " WHERE u.email = :email AND wr.currency_id = :currency_id AND wr.status_id NOT IN (:statuses) AND wr.date_creation > CURDATE())" +
                " <= " +
                "(SELECT cl.max_daily_request" +
                " FROM CURRENCY_LIMIT cl" +
                " JOIN USER u ON u.roleid = cl.user_role_id" +
                " WHERE u.email = :email AND cl.operation_type_id = 2 AND cl.currency_id = :currency_id)";

        Map<String, Object> params = new HashMap<>();
        params.put("currency_id", currencyId);
        params.put("email", email);
        params.put("statuses", Arrays.asList(7, 8, 12));

        return jdbcTemplate.queryForObject(sql, params, Integer.class) == 1;
    }

    @Override
    public boolean checkOutputMaxSum(int currencyId, String email, BigDecimal newSum) {
        final String sql = "SELECT " +
                "((SELECT IFNULL(SUM(wr.amount), 0)" +
                " FROM WITHDRAW_REQUEST wr" +
                " JOIN USER u ON u.id = wr.user_id" +
                " WHERE u.email = :email AND wr.currency_id = :currency_id AND wr.status_id NOT IN (:statuses) AND wr.date_creation > CURDATE()) + :newSum)" +
                " <= " +
                "(SELECT IFNULL(cl.max_sum, 999999999999)" +
                " FROM CURRENCY_LIMIT cl" +
                " JOIN USER u ON u.roleid = cl.user_role_id" +
                " WHERE u.email = :email AND cl.operation_type_id = 2 AND cl.currency_id = :currency_id) ";

        Map<String, Object> params = new HashMap<>();
        params.put("currency_id", currencyId);
        params.put("email", email);
        params.put("statuses", Arrays.asList(7, 8, 12));
        params.put("newSum", newSum);

        return jdbcTemplate.queryForObject(sql, params, Integer.class) == 1;
    }

    @Override
    public Optional<Integer> findUserIdById(Integer requestId) {
        String sql = "SELECT WR.user_id " +
                " FROM WITHDRAW_REQUEST WR " +
                " WHERE WR.id = :id ";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("id", requestId);
        }};
        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, params, Integer.class));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Integer> getIdByHashAndMerchantId(String hash, Integer merchantId) {
        String sql = "SELECT WR.id " +
                " FROM WITHDRAW_REQUEST WR " +
                " WHERE WR.transaction_hash = :hash AND WR.merchant_id = :merchant_id";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("hash", hash);
            put("merchant_id", merchantId);
        }};
        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, params, Integer.class));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<WithdrawRequestFlatDto> findRequestsByStatusAndMerchant(Integer merchantId, List<Integer> statusId) {
        String sql = "SELECT WITHDRAW_REQUEST.* " +
                " FROM WITHDRAW_REQUEST " +
                " WHERE WITHDRAW_REQUEST.merchant_id = :merchant_id  AND WITHDRAW_REQUEST.status_id IN (:statuses)";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("merchant_id", merchantId);
            put("statuses", statusId);
        }};
        return jdbcTemplate.query(sql, params, (rs, i) -> {
            return withdrawRequestFlatDtoRowMapper.mapRow(rs, i);
        });
    }

    @Override
    public List<Integer> getWithdrawalStatistic(String startDate, String endDate) {
        final String sql = "SELECT (SELECT COUNT(*) FROM WITHDRAW_REQUEST WHERE status_modification_date \n" +
                "BETWEEN STR_TO_DATE(:start_date, '%Y-%m-%d %H:%i:%s') AND STR_TO_DATE(:end_date, '%Y-%m-%d %H:%i:%s')" +
                " AND status_id IN (8,9)) as manual, \n" +
                "(SELECT COUNT(*) FROM WITHDRAW_REQUEST WHERE status_modification_date " +
                "BETWEEN STR_TO_DATE(:start_date, '%Y-%m-%d %H:%i:%s') AND STR_TO_DATE(:end_date, '%Y-%m-%d %H:%i:%s')  \n" +
                " AND status_id IN (10,12)) as auto;";

        Map<String, Object> params = new HashMap<String, Object>() {{
            put("start_date", startDate);
            put("end_date", endDate);
        }};

        return jdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> {
            List<Integer> list = new LinkedList<>();
            list.add(rs.getInt("manual"));
            list.add(rs.getInt("auto"));

            return list;
        });

    }

    @Override
    public WithdrawRequestInfoDto findWithdrawInfo(Integer id) {
        String sql = "SELECT WR.*, U.email, M.description, M.name AS merchant_name, C.name, MC.withdraw_auto_delay_seconds " +
                " FROM WITHDRAW_REQUEST WR " +
                " JOIN USER U on U.id = WR.user_id " +
                " JOIN MERCHANT_CURRENCY MC ON (MC.currency_id = WR.currency_id " +
                "  AND MC.merchant_id = WR.merchant_id) " +
                " JOIN MERCHANT M ON M.id = WR.merchant_id " +
                " JOIN CURRENCY C ON C.id = WR.currency_id " +
                " WHERE WR.id = :id  ";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("id", id);
        }};
        return jdbcTemplate.queryForObject(sql, params, (rs, i) -> {
            WithdrawRequestInfoDto withdrawRequestInfoDto = new WithdrawRequestInfoDto();
            withdrawRequestInfoDto.setRequestId(id);
            withdrawRequestInfoDto.setAmount(rs.getBigDecimal("amount"));
            withdrawRequestInfoDto.setComissionAmount(rs.getBigDecimal("commission"));
            withdrawRequestInfoDto.setMerchantComissionAmount(rs.getBigDecimal("merchant_commission"));
            withdrawRequestInfoDto.setUserEmail(rs.getString("email"));
            withdrawRequestInfoDto.setStatusEnum(WithdrawStatusEnum.convert(rs.getInt("status_id")));
            withdrawRequestInfoDto.setMerchantDescription(rs.getString("description"));
            withdrawRequestInfoDto.setDelaySeconds(rs.getInt("withdraw_auto_delay_seconds"));
            withdrawRequestInfoDto.setCurrencyName(rs.getString("name"));
            withdrawRequestInfoDto.setMerchantName(rs.getString("merchant_name"));
            return withdrawRequestInfoDto;
        });
    }

    @Override
    public List<WithdrawRequestFlatForReportDto> findAllByPeriodAndRoles(LocalDateTime startTime,
                                                                         LocalDateTime endTime,
                                                                         List<UserRole> userRoles,
                                                                         int requesterId) {
        String sql = "SELECT wr.id AS invoice_id, " +
                "wr.wallet, " +
                "wr.recipient_bank_name, " +
                "wr.status_modification_date, " +
                "wr.status_id, " +
                "wr.user_full_name, " +
                "wr.amount, " +
                "wr.commission, " +
                "wr.date_creation, " +
                "userr.email AS user_email, " +
                "userr.nickname, " +
                "admin.email AS admin_email, " +
                "m.name AS merchant_name, " +
                "cur.name AS currency_name" +
                " FROM WITHDRAW_REQUEST wr" +
                " JOIN CURRENCY cur ON cur.id = wr.currency_id" +
                " JOIN MERCHANT m ON m.id = wr.merchant_id" +
                " JOIN USER userr ON userr.id = wr.user_id AND userr.roleid IN (:user_roles)" +
                " LEFT JOIN USER admin ON admin.id = wr.admin_holder_id" +
                " WHERE wr.date_creation BETWEEN :start_time AND :end_time" +
                " AND EXISTS (SELECT * FROM USER_CURRENCY_INVOICE_OPERATION_PERMISSION iop WHERE iop.currency_id = cur.id AND iop.user_id = :requester_user_id)";

        Map<String, Object> namedParameters = new HashMap<String, Object>() {{
            put("start_time", Timestamp.valueOf(startTime));
            put("end_time", Timestamp.valueOf(endTime));
            put("user_roles", userRoles.stream().map(UserRole::getRole).collect(toList()));
            put("requester_user_id", requesterId);
        }};

        try {
            return slaveForReportsTemplate.query(sql, namedParameters, (rs, i) -> WithdrawRequestFlatForReportDto.builder()
                    .invoiceId(rs.getInt("invoice_id"))
                    .wallet(rs.getString("wallet"))
                    .recipientBank(rs.getString("recipient_bank_name"))
                    .adminEmail(rs.getString("admin_email"))
                    .acceptanceTime(isNull(rs.getTimestamp("status_modification_date")) ? null : rs.getTimestamp("status_modification_date").toLocalDateTime())
                    .status(WithdrawStatusEnum.convert(rs.getInt("status_id")))
                    .userFullName(rs.getString("user_full_name"))
                    .userNickname(rs.getString("nickname"))
                    .userEmail(rs.getString("user_email"))
                    .amount(rs.getBigDecimal("amount"))
                    .commissionAmount(rs.getBigDecimal("commission"))
                    .datetime(isNull(rs.getTimestamp("date_creation")) ? null : rs.getTimestamp("date_creation").toLocalDateTime())
                    .currency(rs.getString("currency_name"))
                    .sourceType(WITHDRAW)
                    .merchant(rs.getString("merchant_name"))
                    .build());
        } catch (EmptyResultDataAccessException ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public BigDecimal getLeftOutputRequestsCount(int currencyId, String email) {
        final String sql = "SELECT " +
                "(SELECT cl.max_daily_request FROM CURRENCY_LIMIT cl" +
                " JOIN USER u ON u.roleid = cl.user_role_id" +
                " WHERE u.email = :email AND cl.operation_type_id = 2 AND cl.currency_id = :currency_id)" +
                " - " +
                "(SELECT COUNT(*) FROM WITHDRAW_REQUEST wr" +
                " JOIN USER u ON u.id = wr.user_id" +
                " WHERE u.email = :email AND wr.currency_id = :currency_id AND wr.status_id NOT IN (:statuses) AND wr.date_creation > CURDATE())";

        Map<String, Object> params = new HashMap<>();
        params.put("currency_id", currencyId);
        params.put("email", email);
        params.put("statuses", Arrays.asList(7, 8, 12));

        return jdbcTemplate.queryForObject(sql, params, BigDecimal.class);
    }

    @Override
    public BigDecimal getDailyWithdrawalSumByCurrency(String email, Integer currencyId) {
        final String sql = "SELECT IFNULL(SUM(wr.amount), 0)" +
                " FROM WITHDRAW_REQUEST wr " +
                " JOIN USER u ON u.id = wr.user_id " +
                " WHERE u.email = :email AND wr.currency_id = :currency_id AND wr.status_id NOT IN (:statuses) AND wr.date_creation > CURDATE()";

        Map<String, Object> params = new HashMap<>();
        params.put("currency_id", currencyId);
        params.put("email", email);
        params.put("statuses", Arrays.asList(7, 8, 12));

        return jdbcTemplate.queryForObject(sql, params, BigDecimal.class);
    }

    @Override
    public List<WithdrawRequestFlatDto> findListByMerchantIdAndAdditionParam(int merchantId, String additionalParam) {
        String sql = "SELECT WITHDRAW_REQUEST.* " +
                " FROM WITHDRAW_REQUEST " +
                " WHERE WITHDRAW_REQUEST.merchant_id = :merchant_id  AND WITHDRAW_REQUEST.additional_params = :param";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("merchant_id", merchantId);
            put("param", additionalParam);
        }};
        return jdbcTemplate.query(sql, params, (rs, i) -> {
            return withdrawRequestFlatDtoRowMapper.mapRow(rs, i);
        });
    }

    @Override
    public WithdrawRequestFlatDto findByMerchantIdAndAdditionParam(int merchantId, String additionalParam) {
        String sql = "SELECT WITHDRAW_REQUEST.* " +
                " FROM WITHDRAW_REQUEST " +
                " WHERE WITHDRAW_REQUEST.merchant_id = :merchant_id  AND WITHDRAW_REQUEST.additional_params = :param";
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("merchant_id", merchantId);
            put("param", additionalParam);
        }};
        return jdbcTemplate.queryForObject(sql, params, (rs, i) -> {
            return withdrawRequestFlatDtoRowMapper.mapRow(rs, i);
        });
    }

    @Override
    public boolean updateAdditionalParamById(int requestId, String additionalParam) {
        final String sql = "UPDATE WITHDRAW_REQUEST " +
                "  SET additional_params = :param " +
                "  WHERE id = :id";
        Map<String, Object> params = new HashMap<>();
        params.put("id", requestId);
        params.put("param", additionalParam);
        return jdbcTemplate.update(sql, params) > 0;
    }

    private String getPermissionClause(Integer requesterUserId) {
        if (requesterUserId == null) {
            return " LEFT JOIN USER_CURRENCY_INVOICE_OPERATION_PERMISSION IOP ON (IOP.user_id = -1) ";
        }
        return " JOIN USER_CURRENCY_INVOICE_OPERATION_PERMISSION IOP ON " +
                "	  			(IOP.currency_id=WITHDRAW_REQUEST.currency_id) " +
                "	  			AND (IOP.user_id=:requester_user_id) " +
                "	  			AND (IOP.operation_direction=:operation_direction) ";
    }
}

