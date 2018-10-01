package me.exrates.dao.impl;

import me.exrates.dao.TransferRequestDao;
import me.exrates.model.PagingData;
import me.exrates.model.dto.TransferRequestCreateDto;
import me.exrates.model.dto.TransferRequestFlatDto;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.VoucherFilterData;
import me.exrates.model.enums.invoice.InvoiceOperationPermission;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.enums.invoice.TransferStatusEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.*;

import static java.util.Collections.singletonMap;
import static java.util.Optional.of;


/**
 * created by ValkSam
 */

@Repository
public class TransferRequestDaoImpl implements TransferRequestDao {

  private static final Logger log = LogManager.getLogger("withdraw");

  protected static RowMapper<TransferRequestFlatDto> transferRequestFlatDtoRowMapper = (rs, idx) -> {
    TransferRequestFlatDto transferRequestFlatDto = new TransferRequestFlatDto();
    transferRequestFlatDto.setId(rs.getInt("id"));
    transferRequestFlatDto.setAmount(rs.getBigDecimal("amount"));
    transferRequestFlatDto.setDateCreation(rs.getTimestamp("date_creation").toLocalDateTime());
    transferRequestFlatDto.setStatus(TransferStatusEnum.convert(rs.getInt("status_id")));
    transferRequestFlatDto.setStatusModificationDate(rs.getTimestamp("status_modification_date").toLocalDateTime());
    transferRequestFlatDto.setCurrencyId(rs.getInt("currency_id"));
    transferRequestFlatDto.setMerchantId(rs.getInt("merchant_id"));
    transferRequestFlatDto.setUserId(rs.getInt("user_id"));
    transferRequestFlatDto.setRecipientId(rs.getInt("recipient_user_id"));
    transferRequestFlatDto.setCommissionId(rs.getInt("commission_id"));
    transferRequestFlatDto.setCommissionAmount(rs.getBigDecimal("commission"));
    transferRequestFlatDto.setHash(/*rs.getString("hash")*/"");
    return transferRequestFlatDto;
  };

  protected static RowMapper<TransferRequestFlatDto> extendedTransferRequestFlatDtoRowMapper = (rs, idx) -> {
    TransferRequestFlatDto transferRequestFlatDto = transferRequestFlatDtoRowMapper.mapRow(rs, idx);
    transferRequestFlatDto.setCreatorEmail(rs.getString("email"));
    transferRequestFlatDto.setRecipientEmail(rs.getString("recipient_email"));
    transferRequestFlatDto.setCurrencyName(rs.getString("currency"));
    transferRequestFlatDto.setMerchantName(rs.getString("merchant_name"));
    return transferRequestFlatDto;
  };

  @Autowired
  @Qualifier(value = "masterTemplate")
  private NamedParameterJdbcTemplate jdbcTemplate;

  private Optional<Integer> blockById(int id) {
    String sql = "SELECT COUNT(*) " +
        "FROM TRANSFER_REQUEST " +
        "WHERE TRANSFER_REQUEST.id = :id " +
        "FOR UPDATE ";
    return of(jdbcTemplate.queryForObject(sql, singletonMap("id", id), Integer.class));
  }

  @Override
  public int create(TransferRequestCreateDto transferRequest) {
    final String sql = "INSERT INTO TRANSFER_REQUEST " +
        "(amount, commission, status_id," +
        " date_creation, status_modification_date, currency_id, merchant_id, user_id, recipient_user_id, commission_id, hash) " +
        " VALUES (:amount, :commission, :status_id, " +
        " NOW(), NOW(), :currency_id, :merchant_id, :user_id, :recipient_user_id, :commission_id, :hash)";
    KeyHolder keyHolder = new GeneratedKeyHolder();
    MapSqlParameterSource params = new MapSqlParameterSource()
        .addValue("amount", transferRequest.getAmount())
        .addValue("commission", transferRequest.getCommission())
        .addValue("status_id", transferRequest.getStatusId())
        .addValue("currency_id", transferRequest.getCurrencyId())
        .addValue("merchant_id", transferRequest.getMerchantId())
        .addValue("user_id", transferRequest.getUserId())
        .addValue("recipient_user_id", transferRequest.getRecipientId())
        .addValue("commission_id", transferRequest.getCommissionId())
        .addValue("hash", transferRequest.getHash());
    jdbcTemplate.update(sql, params, keyHolder);
    return (int) keyHolder.getKey().longValue();
  }


  @Override
  public Optional<TransferRequestFlatDto> getFlatByIdAndBlock(int id) {
    blockById(id);
    return getFlatById(id);
  }

  @Override
  public Optional<TransferRequestFlatDto> getFlatById(int id) {
    String sql = "SELECT TR.*, U1.email AS email, U2.email AS recipient_email, " +
            "CU.name AS currency, M.name AS merchant_name " +
            " FROM TRANSFER_REQUEST TR " +
            " JOIN CURRENCY CU ON CU.id = TR.currency_id " +
            " JOIN MERCHANT M ON M.id = TR.merchant_id " +
            " JOIN USER U1 ON U1.id = TR.user_id " +
            " LEFT JOIN USER U2 ON U2.id <=> TR.recipient_user_id " +
            " WHERE TR.id = :id";
    log.debug("sql {}", sql);
    return of(jdbcTemplate.queryForObject(sql, singletonMap("id", id), extendedTransferRequestFlatDtoRowMapper));
  }

  @Override
  public Optional<TransferRequestFlatDto> getFlatByHashAndStatus(String hash, Integer requiredStatus, boolean block) {
    String sql = "SELECT * " +
            " FROM TRANSFER_REQUEST " +
            " WHERE hash = :hash AND status_id = :status ";
    if (block) {
      sql = sql.concat(" FOR UPDATE");
    }
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("hash", hash);
      put("status", requiredStatus);
    }};
    Optional<TransferRequestFlatDto> dto = Optional.empty();
   try {
      dto = of(jdbcTemplate.queryForObject(sql, params, transferRequestFlatDtoRowMapper));
    } catch (DataAccessException e) {
      log.error(e);
    }
    return dto;
  }


  @Override
  public void setStatusById(Integer id, InvoiceStatus newStatus) {
    final String sql = "UPDATE TRANSFER_REQUEST " +
        "  SET status_id = :new_status_id, " +
        "      status_modification_date = NOW() " +
        "  WHERE id = :id";
    Map<String, Object> params = new HashMap<>();
    params.put("id", id);
    params.put("new_status_id", newStatus.getCode());
    jdbcTemplate.update(sql, params);
  }

  @Override
  public void setRecipientById(Integer id, Integer recipientId) {
    final String sql = "UPDATE TRANSFER_REQUEST " +
            "  SET recipient_user_id = :recipient_id, " +
            "      status_modification_date = NOW() " +
            "  WHERE id = :id";
    Map<String, Object> params = new HashMap<>();
    params.put("recipient_id", recipientId);
    params.put("id", id);
    jdbcTemplate.update(sql, params);
  }

  @Override
  public List<TransferRequestFlatDto> findRequestsByStatusAndMerchant(Integer merchantId, List<Integer> statusId) {
    String sql = "SELECT TRANSFER_REQUEST.* " +
        " FROM TRANSFER_REQUEST " +
        " WHERE TRANSFER_REQUEST.merchant_id = :merchant_id  AND TRANSFER_REQUEST.status_id IN (:statuses)";
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("merchant_id", merchantId);
      put("statuses", statusId);
    }};
    return jdbcTemplate.query(sql, params, (rs, i) -> {
      return transferRequestFlatDtoRowMapper.mapRow(rs, i);
    });
  }

  @Override
  public void setHashById(Integer id, Map<String, String> params) {
    final String sql = "UPDATE TRANSFER_REQUEST " +
        "  SET hash = :hash " +
        "  WHERE id = :id";
    Map<String, Object> sqlParams = new HashMap<>();
    sqlParams.put("id", id);
    sqlParams.put("hash", params.get("hash"));
    jdbcTemplate.update(sql, sqlParams);
  }

  @Override
  public String getCreatorEmailById(int id) {
    String sql = " SELECT U.email " +
            " FROM TRANSFER_REQUEST TR " +
            " JOIN USER U ON U.id = TR.user_id " +
            " WHERE TR.id = :id ";
    return jdbcTemplate.queryForObject(sql, Collections.singletonMap("id", id), String.class);
  }

  @Override
  public PagingData<List<TransferRequestFlatDto>> getPermittedFlat(
          Integer requesterUserId,
          DataTableParams dataTableParams,
          VoucherFilterData voucherFilterData) {
    final String BASE_JOINS =
                    " JOIN USER UC ON UC.id = TRANSFER_REQUEST.user_id " +
                            " JOIN CURRENCY CU ON CU.id = TRANSFER_REQUEST.currency_id " +
                            " JOIN MERCHANT M ON M.id = TRANSFER_REQUEST.merchant_id " +
                    " LEFT JOIN USER UR ON UR.id <=> TRANSFER_REQUEST.recipient_user_id ";
    String filter = voucherFilterData.getSQLFilterClause();
    String searchClause = dataTableParams.getSearchByEmailAndNickClauseForVouchers();
    String sqlBase =
            " FROM TRANSFER_REQUEST " +
                    getPermissionClause(requesterUserId) +
                    BASE_JOINS;
    if (!(StringUtils.isEmpty(filter) && StringUtils.isEmpty(searchClause))) {
      sqlBase = sqlBase.concat(" WHERE ");
    }
    String whereClauseFilter = StringUtils.isEmpty(filter) ? "" :
            StringUtils.isEmpty(searchClause) ? filter : filter.concat(" AND ");
    String whereClauseSearch = StringUtils.isEmpty(searchClause) ? "" : searchClause;
    String orderClause = dataTableParams.getOrderByClause();
    String offsetAndLimit = dataTableParams.getLimitAndOffsetClause();
    String sqlMain = String.join(" ", "SELECT TRANSFER_REQUEST.*, IOP.invoice_operation_permission_id, " +
                    "UC.email AS email, UR.email AS recipient_email, CU.name AS currency, M.name AS merchant_name  ",
            sqlBase, whereClauseFilter, whereClauseSearch, orderClause, offsetAndLimit);
    String sqlCount = String.join(" ", "SELECT COUNT(*) ", sqlBase, whereClauseFilter, whereClauseSearch);
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("requester_user_id", requesterUserId);
      put("operation_direction", "TRANSFER_VOUCHER");
      put("offset", dataTableParams.getStart());
      put("limit", dataTableParams.getLength());
    }};
    params.putAll(voucherFilterData.getNamedParams());
    params.putAll(dataTableParams.getSearchNamedParams());
    log.debug("sql main {}", sqlMain);
    List<TransferRequestFlatDto> requests = jdbcTemplate.query(sqlMain, params, (rs, i) -> {
      TransferRequestFlatDto withdrawRequestFlatDto = extendedTransferRequestFlatDtoRowMapper.mapRow(rs, i);
      withdrawRequestFlatDto.setInvoiceOperationPermission(InvoiceOperationPermission.convert(rs.getInt("invoice_operation_permission_id")));
      withdrawRequestFlatDto.setHash(rs.getString("hash"));
      return withdrawRequestFlatDto;
    });
    Integer totalQuantity = jdbcTemplate.queryForObject(sqlCount, params, Integer.class);
    PagingData<List<TransferRequestFlatDto>> result = new PagingData<>();
    result.setData(requests);
    result.setFiltered(totalQuantity);
    result.setTotal(totalQuantity);
    return result;
  }

  private String getPermissionClause(Integer requesterUserId) {
    if (requesterUserId == null) {
      return " LEFT JOIN USER_CURRENCY_INVOICE_OPERATION_PERMISSION IOP ON (IOP.user_id = -1) ";
    }
    return " JOIN USER_CURRENCY_INVOICE_OPERATION_PERMISSION IOP ON " +
            "	  			(IOP.currency_id=TRANSFER_REQUEST.currency_id) " +
            "	  			AND (IOP.user_id=:requester_user_id) " +
            "	  			AND (IOP.operation_direction=:operation_direction) ";
  }

  @Override
  public String getHashById(Integer id) {
    String sql = " SELECT TR.hash " +
            " FROM TRANSFER_REQUEST TR " +
            " WHERE TR.id = :id ";
    return jdbcTemplate.queryForObject(sql, Collections.singletonMap("id", id), String.class);
  }
}

