package me.exrates.dao.impl;

import me.exrates.dao.TransactionDao;
import me.exrates.model.*;
import me.exrates.model.Currency;
import me.exrates.model.dto.RefillRequestFlatDto;
import me.exrates.model.dto.TransactionFlatForReportDto;
import me.exrates.model.dto.UserSummaryDto;
import me.exrates.model.dto.UserSummaryOrdersDto;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.AdminTransactionsFilterData;
import me.exrates.model.dto.onlineTableDto.AccountStatementDto;
import me.exrates.model.enums.*;
import me.exrates.model.enums.invoice.RefillStatusEnum;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;
import me.exrates.model.util.BigDecimalProcessing;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.Collections.singletonMap;

@Repository
public final class TransactionDaoImpl implements TransactionDao {

  private static final Logger LOGGER = LogManager.getLogger(TransactionDaoImpl.class);

  protected static RowMapper<Transaction> transactionRowMapper = (resultSet, i) -> {

    final OperationType operationType = OperationType.convert(resultSet.getInt("TRANSACTION.operation_type_id"));

    Currency currency = null;
    try {
      resultSet.findColumn("CURRENCY.id");
      currency = new Currency();
      currency.setId(resultSet.getInt("CURRENCY.id"));
      currency.setName(resultSet.getString("CURRENCY.name"));
      currency.setDescription(resultSet.getString("CURRENCY.description"));
    } catch (SQLException e) {
      //NOP
    }

    Merchant merchant = null;
    try {
      resultSet.findColumn("MERCHANT.id");
      if (resultSet.getObject("MERCHANT.id") != null) {
        merchant = new Merchant();
        merchant.setId(resultSet.getInt("MERCHANT.id"));
        merchant.setName(resultSet.getString("MERCHANT.name"));
        merchant.setDescription(resultSet.getString("MERCHANT.description"));
      }
    } catch (SQLException e) {
      //NOP
    }

    ExOrder order = null;
    try {
      resultSet.findColumn("EXORDERS.id");
      if (resultSet.getObject("EXORDERS.id") != null) {
        order = new ExOrder();
        order.setId(resultSet.getInt("EXORDERS.id"));
        order.setUserId(resultSet.getInt("EXORDERS.user_id"));
        order.setCurrencyPairId(resultSet.getInt("EXORDERS.currency_pair_id"));
        order.setOperationType(resultSet.getInt("EXORDERS.operation_type_id") == 0 ? null : OperationType.convert(resultSet.getInt("EXORDERS.operation_type_id")));
        order.setExRate(resultSet.getBigDecimal("EXORDERS.exrate"));
        order.setAmountBase(resultSet.getBigDecimal("EXORDERS.amount_base"));
        order.setAmountConvert(resultSet.getBigDecimal("EXORDERS.amount_convert"));
        order.setCommissionFixedAmount(resultSet.getBigDecimal("EXORDERS.commission_fixed_amount"));
        order.setDateCreation(resultSet.getTimestamp("EXORDERS.date_creation") == null ? null : resultSet.getTimestamp("EXORDERS.date_creation").toLocalDateTime());
        order.setDateAcception(resultSet.getTimestamp("EXORDERS.date_acception") == null ? null : resultSet.getTimestamp("EXORDERS.date_acception").toLocalDateTime());
      }
    } catch (SQLException e) {
      //NOP
    }

    WithdrawRequest withdraw = null;
    try {
      resultSet.findColumn("WITHDRAW_REQUEST.id");
      if (resultSet.getObject("WITHDRAW_REQUEST.id") != null) {
        withdraw = new WithdrawRequest();
        withdraw.setId(resultSet.getInt("WITHDRAW_REQUEST.id"));
        withdraw.setWallet(resultSet.getString("WITHDRAW_REQUEST.wallet"));
        withdraw.setUserId(resultSet.getInt("WITHDRAW_REQUEST.user_id"));
        withdraw.setRecipientBankName(resultSet.getString("WITHDRAW_REQUEST.recipient_bank_name"));
        withdraw.setRecipientBankCode(resultSet.getString("WITHDRAW_REQUEST.recipient_bank_code"));
        withdraw.setUserFullName(resultSet.getString("WITHDRAW_REQUEST.user_full_name"));
        withdraw.setRemark(resultSet.getString("WITHDRAW_REQUEST.remark"));
        withdraw.setAmount(resultSet.getBigDecimal("WITHDRAW_REQUEST.amount"));
        withdraw.setCommissionAmount(resultSet.getBigDecimal("WITHDRAW_REQUEST.commission"));
        withdraw.setCommissionId(resultSet.getInt("WITHDRAW_REQUEST.commission_id"));
        withdraw.setStatus(WithdrawStatusEnum.convert(resultSet.getInt("WITHDRAW_REQUEST.status_id")));
        withdraw.setDateCreation(resultSet.getTimestamp("WITHDRAW_REQUEST.date_creation").toLocalDateTime());
        withdraw.setStatusModificationDate(resultSet.getTimestamp("WITHDRAW_REQUEST.status_modification_date").toLocalDateTime());
        withdraw.setCurrency(currency);
        withdraw.setMerchant(merchant);
        withdraw.setAdminHolderId(resultSet.getInt("WITHDRAW_REQUEST.admin_holder_id"));
      }
    } catch (SQLException e) {
      //NOP
    }

    RefillRequest refill = null;
    try {
      resultSet.findColumn("REFILL_REQUEST.id");
      if (resultSet.getObject("REFILL_REQUEST.id") != null) {
        refill = new RefillRequest();
        refill.setId(resultSet.getInt("REFILL_REQUEST.id"));
        refill.setAddress(resultSet.getString("REFILL_REQUEST.address"));
        refill.setUserId(resultSet.getInt("REFILL_REQUEST.user_id"));
        refill.setPayerBankName(resultSet.getString("REFILL_REQUEST.payer_bank_name"));
        refill.setPayerBankCode(resultSet.getString("REFILL_REQUEST.payer_bank_code"));
        refill.setPayerAccount(resultSet.getString("REFILL_REQUEST.payer_account"));
        refill.setRecipientBankAccount(resultSet.getString("REFILL_REQUEST.payer_account"));
        refill.setUserFullName(resultSet.getString("REFILL_REQUEST.user_full_name"));
        refill.setRemark(resultSet.getString("REFILL_REQUEST.remark"));
        refill.setReceiptScan(resultSet.getString("REFILL_REQUEST.receipt_scan"));
        refill.setReceiptScanName(resultSet.getString("REFILL_REQUEST.receipt_scan_name"));
        refill.setAmount(resultSet.getBigDecimal("REFILL_REQUEST.amount"));
        refill.setCommissionAmount(resultSet.getBigDecimal("REFILL_REQUEST.commission"));
        refill.setCommissionId(resultSet.getInt("REFILL_REQUEST.commission_id"));
        refill.setStatus(RefillStatusEnum.convert(resultSet.getInt("REFILL_REQUEST.status_id")));
        refill.setDateCreation(resultSet.getTimestamp("REFILL_REQUEST.date_creation").toLocalDateTime());
        refill.setStatusModificationDate(resultSet.getTimestamp("REFILL_REQUEST.status_modification_date").toLocalDateTime());
        refill.setCurrencyId(resultSet.getInt("REFILL_REQUEST.currency_id"));
        refill.setMerchantId(resultSet.getInt("REFILL_REQUEST.merchant_id"));
        refill.setHash(resultSet.getString("REFILL_REQUEST.hash"));
        refill.setMerchantTransactionId(resultSet.getString("REFILL_REQUEST.merchant_transaction_id"));
        refill.setRecipientBankId(resultSet.getInt("REFILL_REQUEST.recipient_bank_id"));
        refill.setRecipientBankName(resultSet.getString("INVOICE_BANK.name"));
        refill.setRecipientBankAccount(resultSet.getString("INVOICE_BANK.account_number"));
        refill.setRecipientBankRecipient(resultSet.getString("INVOICE_BANK.recipient"));
        refill.setAdminHolderId(resultSet.getInt("REFILL_REQUEST.admin_holder_id"));
        refill.setConfirmations(resultSet.getInt("confirmations"));
      }
    } catch (SQLException e) {
      //NOP
    }

    Commission commission = null;
    try {
      resultSet.findColumn("COMMISSION.id");
      commission = new Commission();
      commission.setId(resultSet.getInt("COMMISSION.id"));
      commission.setOperationType(operationType);
      commission.setValue(resultSet.getBigDecimal("COMMISSION.value"));
      commission.setDateOfChange(resultSet.getTimestamp("COMMISSION.date"));
    } catch (SQLException e) {
      //NOP
    }

    CompanyWallet companyWallet = null;
    try {
      resultSet.findColumn("COMPANY_WALLET.id");
      companyWallet = new CompanyWallet();
      companyWallet.setBalance(resultSet.getBigDecimal("COMPANY_WALLET.balance"));
      companyWallet.setCommissionBalance(resultSet.getBigDecimal("COMPANY_WALLET.commission_balance"));
      companyWallet.setCurrency(currency);
      companyWallet.setId(resultSet.getInt("COMPANY_WALLET.id"));
    } catch (SQLException e) {
      //NOP
    }

    Wallet userWallet = null;
    try {
      resultSet.findColumn("WALLET.id");
      userWallet = new Wallet();
      userWallet.setActiveBalance(resultSet.getBigDecimal("WALLET.active_balance"));
      userWallet.setReservedBalance(resultSet.getBigDecimal("WALLET.reserved_balance"));
      userWallet.setId(resultSet.getInt("WALLET.id"));
      userWallet.setCurrencyId(currency.getId());
      User user = new User();
      user.setId(resultSet.getInt("user_id"));
      user.setEmail(resultSet.getString("user_email"));
      userWallet.setUser(user);
    } catch (SQLException e) {
      //NOP
    }

    Transaction transaction = new Transaction();
    transaction.setId(resultSet.getInt("TRANSACTION.id"));
    transaction.setAmount(resultSet.getBigDecimal("TRANSACTION.amount"));
    transaction.setCommissionAmount(resultSet.getBigDecimal("TRANSACTION.commission_amount"));
    transaction.setDatetime(resultSet.getTimestamp("TRANSACTION.datetime").toLocalDateTime());
    transaction.setCommission(commission);
    transaction.setCompanyWallet(companyWallet);
    transaction.setUserWallet(userWallet);
    transaction.setOperationType(operationType);
    transaction.setMerchant(merchant);
    transaction.setOrder(order);
    transaction.setCurrency(currency);
    transaction.setWithdrawRequest(withdraw);
    transaction.setRefillRequest(refill);
    transaction.setProvided(resultSet.getBoolean("provided"));
    Integer confirmations = (Integer) resultSet.getObject("confirmation");
    transaction.setConfirmation(confirmations);
    TransactionSourceType sourceType = resultSet.getString("source_type") == null ?
        null : TransactionSourceType.convert(resultSet.getString("source_type"));
    transaction.setSourceType(sourceType);
    transaction.setSourceId(resultSet.getInt("source_id"));
    return transaction;
  };

  private final String SELECT_COUNT =
      " SELECT COUNT(*)" +
          " FROM TRANSACTION " +
          "   JOIN WALLET ON TRANSACTION.user_wallet_id = WALLET.id" +
          "   JOIN USER ON WALLET.user_id = USER.id" +
          "   JOIN COMPANY_WALLET ON TRANSACTION.company_wallet_id = COMPANY_WALLET.id" +
          "   JOIN COMMISSION ON TRANSACTION.commission_id = COMMISSION.id" +
          "   JOIN CURRENCY ON TRANSACTION.currency_id = CURRENCY.id";
  private final String SELECT_ALL =
      " SELECT " +
          "   TRANSACTION.id,TRANSACTION.amount,TRANSACTION.commission_amount,TRANSACTION.datetime, " +
          "   TRANSACTION.operation_type_id,TRANSACTION.provided, TRANSACTION.confirmation, TRANSACTION.order_id, " +
          "   TRANSACTION.source_type, TRANSACTION.source_id, " +
          "   WALLET.id, WALLET.active_balance, WALLET.reserved_balance, WALLET.currency_id," +
          "   USER.id as user_id, USER.email as user_email," +
          "   COMPANY_WALLET.id,COMPANY_WALLET.balance,COMPANY_WALLET.commission_balance," +
          "   COMMISSION.id,COMMISSION.date,COMMISSION.value," +
          "   CURRENCY.id,CURRENCY.description,CURRENCY.name," +
          "   MERCHANT.id,MERCHANT.name,MERCHANT.description, " +
          "   EXORDERS.id, EXORDERS.user_id, EXORDERS.currency_pair_id, EXORDERS.operation_type_id, EXORDERS.exrate, " +
          "   EXORDERS.amount_base, EXORDERS.amount_convert, EXORDERS.commission_fixed_amount, EXORDERS.date_creation, " +
          "   EXORDERS.date_acception," +
          "   WITHDRAW_REQUEST.*, " +
          "   REFILL_REQUEST.*, " +
          "   INVOICE_BANK.name, INVOICE_BANK.account_number, INVOICE_BANK.recipient, " +
          "   (SELECT IF(MAX(confirmation_number) IS NULL, -1, MAX(confirmation_number)) FROM REFILL_REQUEST_CONFIRMATION RRC WHERE RRC.refill_request_id = REFILL_REQUEST.id) AS confirmations " +
          " FROM TRANSACTION " +
          "   JOIN WALLET ON TRANSACTION.user_wallet_id = WALLET.id" +
          "   JOIN USER ON WALLET.user_id = USER.id" +
          "   JOIN CURRENCY ON TRANSACTION.currency_id = CURRENCY.id" +
          "   LEFT JOIN COMMISSION ON TRANSACTION.commission_id = COMMISSION.id" +
          "   LEFT JOIN COMPANY_WALLET ON TRANSACTION.company_wallet_id = COMPANY_WALLET.id" +
          "   LEFT JOIN WITHDRAW_REQUEST ON (TRANSACTION.source_type='WITHDRAW') AND (WITHDRAW_REQUEST.id=TRANSACTION.source_id) " +
          "   LEFT JOIN REFILL_REQUEST ON (TRANSACTION.source_type='REFILL') AND (REFILL_REQUEST.id=TRANSACTION.source_id) " +
          "   LEFT JOIN EXORDERS ON (TRANSACTION.source_type='ORDER') AND (TRANSACTION.source_id = EXORDERS.id)" +
          "   LEFT JOIN INVOICE_BANK ON (INVOICE_BANK.id = REFILL_REQUEST.recipient_bank_id) " +
          "   LEFT JOIN MERCHANT ON " +
          "             (" +
          "               (REFILL_REQUEST.merchant_id IS NOT NULL AND MERCHANT.id = REFILL_REQUEST.merchant_id) OR " +
          "               (WITHDRAW_REQUEST.merchant_id IS NOT NULL AND MERCHANT.id = WITHDRAW_REQUEST.merchant_id) " +
          "             )";

  private String PERMISSION_CLAUSE = " JOIN USER_CURRENCY_INVOICE_OPERATION_PERMISSION IOP ON " +
      "  (WALLET.user_id=:requester_user_id) OR " +
      "  ((IOP.user_id=:requester_user_id) AND " +
      "  (IOP.currency_id=TRANSACTION.currency_id) AND " +
      "  ( " +
      "  (TRANSACTION.operation_type_id=1 AND IOP.operation_direction='REFILL') OR " +
      "  (TRANSACTION.operation_type_id=2 AND IOP.operation_direction='WITHDRAW') OR " +
      "  (TRANSACTION.operation_type_id=5 AND IOP.operation_direction='WITHDRAW') " +
      "  )) ";

  

  @Autowired
  MessageSource messageSource;
  @Autowired
  private NamedParameterJdbcTemplate jdbcTemplate;

  @Override
  public Transaction create(Transaction transaction) {
    final String sql = "INSERT INTO TRANSACTION (user_wallet_id, company_wallet_id, amount, commission_amount, " +
        " commission_id, operation_type_id, currency_id, merchant_id, datetime, order_id, confirmation, provided," +
        " active_balance_before, reserved_balance_before, company_balance_before, company_commission_balance_before, " +
        " source_type, " +
        " source_id, description)" +
        "   VALUES (:userWallet,:companyWallet,:amount,:commissionAmount,:commission,:operationType, :currency," +
        "   :merchant, :datetime, :order_id, :confirmation, :provided," +
        "   :active_balance_before, :reserved_balance_before, :company_balance_before, :company_commission_balance_before," +
        "   :source_type, " +
        "   :source_id, :description)";
    final KeyHolder keyHolder = new GeneratedKeyHolder();
    final Map<String, Object> params = new HashMap<String, Object>() {
      {
        put("userWallet", transaction.getUserWallet().getId());
        put("companyWallet", transaction.getCompanyWallet() == null ? null : transaction.getCompanyWallet().getId());
        put("amount", transaction.getAmount());
        put("commissionAmount", transaction.getCommissionAmount());
        put("commission", transaction.getCommission() == null ? null : transaction.getCommission().getId());
        put("operationType", transaction.getOperationType().type);
        put("currency", transaction.getCurrency().getId());
        put("merchant", transaction.getMerchant() == null ? null : transaction.getMerchant().getId());
        put("datetime", transaction.getDatetime() == null ? null : Timestamp.valueOf(transaction.getDatetime()));
        put("order_id", transaction.getOrder() == null ? null : transaction.getOrder().getId());
        put("confirmation", transaction.getConfirmation());
        put("provided", transaction.isProvided());
        put("active_balance_before", transaction.getActiveBalanceBefore());
        put("reserved_balance_before", transaction.getReservedBalanceBefore());
        put("company_balance_before", transaction.getCompanyBalanceBefore());
        put("company_commission_balance_before", transaction.getCompanyCommissionBalanceBefore());
        put("source_type", transaction.getSourceType() == null ? null : transaction.getSourceType().toString());
        put("source_id", transaction.getSourceId());
        put("description", transaction.getDescription());
      }
    };
    if (jdbcTemplate.update(sql, new MapSqlParameterSource(params), keyHolder) > 0) {
      transaction.setId(keyHolder.getKey().intValue());
      return transaction;
    }
    throw new RuntimeException("Transaction creating failed");
  }

  @Override
  public boolean updateForProvided(Transaction transaction) {
    final String sql = "UPDATE TRANSACTION " +
        " SET provided = :provided, " +
        "     active_balance_before = :active_balance_before, " +
        "     reserved_balance_before = :reserved_balance_before, " +
        "     company_balance_before = :company_balance_before, " +
        "     company_commission_balance_before = :company_commission_balance_before, " +
        "     source_type = :source_type, " +
        "     source_id = :source_id, " +
        "     provided_modification_date = NOW() " +
        " WHERE id = :id";
    final int PROVIDED = 1;
    final Map<String, Object> params = new HashMap<String, Object>() {
      {
        put("provided", PROVIDED);
        put("id", transaction.getId());
        put("active_balance_before", transaction.getActiveBalanceBefore());
        put("reserved_balance_before", transaction.getReservedBalanceBefore());
        put("company_balance_before", transaction.getCompanyBalanceBefore());
        put("company_commission_balance_before", transaction.getCompanyCommissionBalanceBefore());
        put("source_type", transaction.getSourceType().name());
        put("source_id", transaction.getSourceId());
      }
    };
    return jdbcTemplate.update(sql, params) > 0;
  }

  @Override
  public Transaction findById(int id) {
    final String sql = SELECT_ALL + " WHERE TRANSACTION.id = :id";
    final Map<String, Integer> params = singletonMap("id", id);
    return jdbcTemplate.queryForObject(sql, params, transactionRowMapper);
  }
  
  @Override
  public PagingData<List<Transaction>> findAllByUserWallets(
          Integer requesterUserId, List<Integer> walletIds, AdminTransactionsFilterData filterData, DataTableParams dataTableParams, Locale locale) {
    String orderByClause = dataTableParams.getOrderByClause();
    String limitAndOffset = dataTableParams.getLimitAndOffsetClause();
    final String whereClauseBasic = "WHERE TRANSACTION.user_wallet_id in (:ids)";
    Map<String, Object> params = new HashMap<>();
    params.put("ids", walletIds);
    params.put("limit", dataTableParams.getLength());
    params.put("offset", dataTableParams.getStart());
    params.put("limit", dataTableParams.getLength());
    params.put("requester_user_id", requesterUserId);
    params.putAll(filterData.getNamedParams());
    String criteria = filterData.getSQLFilterClause();
    String filterClause = criteria.isEmpty() ? "" : "AND " + criteria;

    String permissionClause = requesterUserId == null ? "" : PERMISSION_CLAUSE;

    final String selectLimitedAllSql = String.join(" ", SELECT_ALL, permissionClause, whereClauseBasic, filterClause, orderByClause, limitAndOffset);
    final String selectAllCountSql = String.join(" ", SELECT_COUNT, permissionClause, whereClauseBasic, filterClause);
    final PagingData<List<Transaction>> result = new PagingData<>();
    final int total = jdbcTemplate.queryForObject(selectAllCountSql, params, Integer.class);
    result.setData(jdbcTemplate.query(selectLimitedAllSql, params, transactionRowMapper));
    result.setFiltered(total);
    result.setTotal(total);
    return result;

  }

  @Override
  public boolean provide(int id) {
    final int PROVIDED = 1;
    final String sql = "UPDATE TRANSACTION SET provided = :provided, provided_modification_date = NOW() WHERE id = :id";
    final Map<String, Integer> params = new HashMap<String, Integer>() {
      {
        put("provided", PROVIDED);
        put("id", id);
      }
    };
    return jdbcTemplate.update(sql, params) > 0;
  }

  @Override
  public boolean delete(int id) {
    final String sql = "DELETE FROM TRANSACTION where id = :id";
    final Map<String, Integer> params = new HashMap<String, Integer>() {
      {
        put("id", id);
      }
    };
    return jdbcTemplate.update(sql, params) > 0;
  }

  @Override
  public void updateTransactionAmount(final int transactionId, final BigDecimal amount, final BigDecimal commission) {
    final String sql = "UPDATE TRANSACTION SET amount = :amount, commission_amount = :commission WHERE id = :id";
    final Map<String, Object> params = new HashMap<>();
    params.put("amount", amount);
    params.put("commission", commission);
    params.put("id", transactionId);
    jdbcTemplate.update(sql, params);
  }

  @Override
  public void updateTransactionConfirmations(final int transactionId, final int confirmations) {
    final String sql = "UPDATE TRANSACTION" +
        " SET confirmation = :confirmations" +
        " WHERE id  = :id " +
        "       AND confirmation < :confirmations ";
    final Map<String, Integer> params = new HashMap<>();
    params.put("id", transactionId);
    params.put("confirmations", confirmations);
    jdbcTemplate.update(sql, params);
  }

  @Override
  public List<AccountStatementDto> getAccountStatement(Integer walletId, Integer offset, Integer limit, Locale locale) {
    String sql = " SELECT * " +
        "  FROM " +
        "  ( " +
        "    SELECT null AS date_time, null as transaction_id, " +
        "      WALLET.active_balance AS active_balance_before, WALLET.reserved_balance AS reserved_balance_before, " +
        "      CURRENCY.name AS operation_type_id, " +
        "      null AS amount, null AS commission_amount, " +
        "      null AS source_type, null AS source_id, " +
        "      null AS status_id, null AS merchant_name, null AS user_id" +
        "    FROM WALLET  " +
        "    JOIN CURRENCY ON CURRENCY.id=WALLET.currency_id  " +
        "    WHERE WALLET.id=:wallet_id " +
        "  UNION ALL " +
        "    (" +
        "    SELECT TRANSACTION.datetime, TRANSACTION.id, " +
        "      TRANSACTION.active_balance_before, TRANSACTION.reserved_balance_before, " +
        "      TRANSACTION.operation_type_id, " +
        "      TRANSACTION.amount, TRANSACTION.commission_amount, " +
        "      TRANSACTION.source_type, TRANSACTION.source_id, " +
        "      TRANSACTION.status_id, MERCHANT.name AS merchant_name, WALLET.user_id " +
        "    FROM TRANSACTION " +
        "    JOIN WALLET ON TRANSACTION.user_wallet_id = WALLET.id " +
        "    LEFT JOIN MERCHANT ON TRANSACTION.merchant_id = MERCHANT.id" +
        "    WHERE TRANSACTION.provided=1 AND TRANSACTION.user_wallet_id = :wallet_id " +
        "    ORDER BY -TRANSACTION.datetime ASC, -TRANSACTION.id ASC " +
        (limit == -1 ? "" : "  LIMIT " + limit + " OFFSET " + offset) +
        "    )" +
        "  ) T " +
        "  ORDER BY -date_time ASC, -transaction_id ASC";
    final Map<String, Object> params = new HashMap<>();
    params.put("wallet_id", walletId);
    return jdbcTemplate.query(sql, params, new RowMapper<AccountStatementDto>() {
      @Override
      public AccountStatementDto mapRow(ResultSet rs, int i) throws SQLException {
        AccountStatementDto accountStatementDto = new AccountStatementDto();
        accountStatementDto.setDatetime(rs.getTimestamp("date_time") == null ? null : rs.getTimestamp("date_time").toLocalDateTime());
        accountStatementDto.setTransactionId(rs.getInt("transaction_id"));
        accountStatementDto.setActiveBalanceBefore(BigDecimalProcessing.formatLocale(rs.getBigDecimal("active_balance_before"), locale, true));
        accountStatementDto.setReservedBalanceBefore(BigDecimalProcessing.formatLocale(rs.getBigDecimal("reserved_balance_before"), locale, true));
        accountStatementDto.setOperationType(rs.getObject("date_time") == null ? rs.getString("operation_type_id") : OperationType.convert(rs.getInt("operation_type_id")).toString(messageSource, locale));
        accountStatementDto.setAmount(rs.getTimestamp("date_time") == null ? null : BigDecimalProcessing.formatLocale(rs.getBigDecimal("amount"), locale, true));
        accountStatementDto.setCommissionAmount(rs.getTimestamp("date_time") == null ? null : BigDecimalProcessing.formatLocale(rs.getBigDecimal("commission_amount"), locale, true));
        TransactionSourceType transactionSourceType = rs.getObject("source_type") == null ? null : TransactionSourceType.convert(rs.getString("source_type"));
        accountStatementDto.setSourceType(transactionSourceType == null ? "" : transactionSourceType.toString(messageSource, locale));
        accountStatementDto.setSourceTypeId(rs.getString("source_type"));
        accountStatementDto.setSourceId(rs.getInt("source_id"));
        accountStatementDto.setTransactionStatus(rs.getObject("status_id") == null ? null : TransactionStatus.convert(rs.getInt("status_id")));
                /**/
        int otid = rs.getObject("date_time") == null ? 0 : rs.getInt("operation_type_id");
        if (otid != 0) {
          OperationType ot = OperationType.convert(otid);
          switch (ot) {
            case INPUT: {
              accountStatementDto.setActiveBalanceAfter(BigDecimalProcessing
                  .formatLocale(BigDecimalProcessing
                          .doAction(rs.getBigDecimal("active_balance_before"), rs.getBigDecimal("amount"), ActionType.ADD)
                      , locale, true));
              accountStatementDto.setReservedBalanceAfter(accountStatementDto.getReservedBalanceBefore());
              break;
            }
            case OUTPUT: {
              accountStatementDto.setActiveBalanceAfter(BigDecimalProcessing
                  .formatLocale(BigDecimalProcessing
                          .doAction(rs.getBigDecimal("active_balance_before"), rs.getBigDecimal("amount"), ActionType.SUBTRACT)
                      , locale, true));
              accountStatementDto.setReservedBalanceAfter(accountStatementDto.getReservedBalanceBefore());
              break;
            }
            case WALLET_INNER_TRANSFER: {
              accountStatementDto.setActiveBalanceAfter(BigDecimalProcessing
                  .formatLocale(BigDecimalProcessing
                          .doAction(rs.getBigDecimal("active_balance_before"), rs.getBigDecimal("amount"), ActionType.ADD)
                      , locale, true));
              accountStatementDto.setReservedBalanceAfter(BigDecimalProcessing
                  .formatLocale(BigDecimalProcessing
                          .doAction(rs.getBigDecimal("reserved_balance_before"), rs.getBigDecimal("amount"), ActionType.SUBTRACT)
                      , locale, true));
              break;
            }
            case MANUAL: {
              accountStatementDto.setActiveBalanceAfter(BigDecimalProcessing
                  .formatLocale(BigDecimalProcessing
                          .doAction(rs.getBigDecimal("active_balance_before"), rs.getBigDecimal("amount"), ActionType.ADD)
                      , locale, true));
              accountStatementDto.setReservedBalanceAfter(accountStatementDto.getReservedBalanceBefore());
              break;
            }
          }
        }
        String merchantName = rs.getString("merchant_name");
        if (StringUtils.isEmpty(merchantName)) {
          merchantName = accountStatementDto.getSourceType();
        }
        accountStatementDto.setMerchantName(merchantName);
        accountStatementDto.setWalletId(walletId);
        accountStatementDto.setUserId(rs.getInt("user_id"));
                /**/
        return accountStatementDto;
      }
    });
  }

  @Override
  public Integer getStatementSize(Integer walletId) {
    String sql = "SELECT COUNT(*) FROM TRANSACTION WHERE TRANSACTION.provided=1 AND TRANSACTION.user_wallet_id = :wallet_id";
    Map<String, Integer> params = Collections.singletonMap("wallet_id", walletId);
    return jdbcTemplate.queryForObject(sql, params, Integer.class);
  }


  @Override
  public BigDecimal maxAmount() {
    String sql = "SELECT MAX(TRANSACTION.amount)" +
        " FROM TRANSACTION ";
    return jdbcTemplate.queryForObject(sql, Collections.EMPTY_MAP, BigDecimal.class);

  }

  @Override
  public BigDecimal maxCommissionAmount() {
    String sql = "SELECT MAX(TRANSACTION.commission_amount)" +
        " FROM TRANSACTION ";
    return jdbcTemplate.queryForObject(sql, Collections.EMPTY_MAP, BigDecimal.class);
  }

  @Override
  public void setSourceId(Integer trasactionId, Integer sourceId) {
    final String sql = "UPDATE TRANSACTION SET source_id = :source_id WHERE id  = :id";
    final Map<String, Integer> params = new HashMap<>();
    params.put("id", trasactionId);
    params.put("source_id", sourceId);
    jdbcTemplate.update(sql, params);
  }

  @Override
  public List<TransactionFlatForReportDto> findAllByDateIntervalAndRoleAndOperationTypeAndCurrencyAndSourceType(
      String startDate,
      String endDate,
      Integer operationType,
      List<Integer> roleIdList,
      List<Integer> currencyList,
      List<String> sourceTypeList) {
    String sql = "SELECT  " +
        "         USER.email AS user_email, USER.nickname AS nickname, " +
        "         TX.id AS transaction_id, TX.amount, TX.commission_amount, TX.datetime, " +
        "         TX.operation_type_id, TX.provided, TX.confirmation, TX.operation_type_id, " +
        "         TX.source_type, " +
        "         TX.provided_modification_date, " +
        "         MERCHANT.name AS merchant_name, " +
        "         CURRENCY.name AS currency_name" +
        " FROM TRANSACTION TX  " +
        " JOIN CURRENCY ON CURRENCY.id = TX.currency_id " +
        " JOIN WALLET ON WALLET.id = TX.user_wallet_id " +
        " JOIN USER AS USER ON USER.id = WALLET.user_id " +
        " JOIN MERCHANT ON MERCHANT.id = TX.merchant_id " +
        " WHERE " +
        "    TX.operation_type_id = :operation_type_id " +
        "    AND TX.source_type IN (:source_type_list) AND (TX.currency_id IN (:currency_list)) " +
        "    AND TX.datetime BETWEEN STR_TO_DATE(:start_date, '%Y-%m-%d %H:%i:%s') AND STR_TO_DATE(:end_date, '%Y-%m-%d %H:%i:%s') " +
        (roleIdList.isEmpty() ? "" :
            " AND USER.roleid IN (:role_id_list)");
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("start_date", startDate);
      put("end_date", endDate);
      if (!roleIdList.isEmpty()) {
        put("role_id_list", roleIdList);
      }
      put("currency_list", currencyList);
      put("source_type_list", sourceTypeList);
      put("operation_type_id", operationType);
    }};
    return jdbcTemplate.query(sql, params, new RowMapper<TransactionFlatForReportDto>() {
      @Override
      public TransactionFlatForReportDto mapRow(ResultSet rs, int i) throws SQLException {
        TransactionFlatForReportDto transactionFlatForReportDto = new TransactionFlatForReportDto();
        transactionFlatForReportDto.setTransactionId(rs.getInt("transaction_id"));
        transactionFlatForReportDto.setMerchant(rs.getString("merchant_name"));
        transactionFlatForReportDto.setUserNickname(rs.getString("nickname"));
        transactionFlatForReportDto.setUserEmail(rs.getString("user_email"));
        transactionFlatForReportDto.setAmount(rs.getBigDecimal("amount"));
        transactionFlatForReportDto.setCommissionAmount(rs.getBigDecimal("commission_amount"));
        transactionFlatForReportDto.setDatetime(rs.getTimestamp("datetime") == null ? null : rs.getTimestamp("datetime").toLocalDateTime());
        transactionFlatForReportDto.setProvidedDate(rs.getTimestamp("provided_modification_date") == null ? null : rs.getTimestamp("provided_modification_date").toLocalDateTime());
        transactionFlatForReportDto.setConfirmation(rs.getInt("confirmation"));
        transactionFlatForReportDto.setProvided(rs.getBoolean("provided"));
        transactionFlatForReportDto.setCurrency(rs.getString("currency_name"));
        transactionFlatForReportDto.setSourceType(TransactionSourceType.valueOf(rs.getString("source_type")));
        transactionFlatForReportDto.setOperationType(OperationType.convert(rs.getInt("operation_type_id")));
        return transactionFlatForReportDto;
      }
    });
  }

  @Override
  public boolean setStatusById(Integer trasactionId, Integer statusId) {
    String sql = "UPDATE TRANSACTION " +
        " SET status_id = :status_id" +
        " WHERE id = :transaction_id ";
    Map<String, Object> params = new HashMap<String, Object>() {{
      put("transaction_id", trasactionId);
      put("status_id", statusId);
    }};
    return jdbcTemplate.update(sql, params) > 0;
  }

  @Override
  public List<Transaction> getAllOperationsByUserForPeriod(List<Integer> walletIds, String startDate, String endDate, String sortColumn, String sortDirection) {
    /*final String whereClauseBasic = "WHERE TRANSACTION.user_wallet_id in (:ids)";
    Map<String, Object> params = new HashMap<>();
    params.put("date_from", startDate);
    params.put("date_to", endDate);
    String criteria = defineFilterClause(params);
    String filterClause = criteria.isEmpty() ? "" : "AND " + criteria;
    params.put("ids", walletIds);
    StringJoiner sqlJoiner = new StringJoiner(" ")
        .add(SELECT_ALL)
        .add(whereClauseBasic)
        .add(filterClause)
        .add("ORDER BY").add(sortColumn).add(sortDirection);
    final String selectLimitedAllSql = sqlJoiner.toString();
    LOGGER.debug(selectLimitedAllSql);
    return jdbcTemplate.query(selectLimitedAllSql, params, transactionRowMapper);*/
    return Collections.EMPTY_LIST;
  }

  @Override
  public List<UserSummaryDto> getTurnoverInfoByUserAndCurrencyForPeriodAndRoleList(
      Integer requesterUserId,
      String startDate,
      String endDate,
      List<Integer> roleIdList) {
    String sql =
        " SELECT  " +
            "   USER.nickname as user_nickname,  " +
            "   USER.email as user_email,  " +
            "   USER.regdate as user_register_date,  " +
            "   (SELECT ip FROM USER_IP WHERE USER_IP.user_id = USER.id ORDER BY -registration_date DESC LIMIT 1) as user_register_ip, " +
            "   (SELECT ip FROM USER_IP WHERE USER_IP.user_id = USER.id ORDER BY last_registration_date DESC LIMIT 1) as user_last_entry_ip, " +
            "   CURRENCY.name as currency_name,  " +
            "   WALLET.active_balance as active_balance,  " +
            "   WALLET.reserved_balance as reserved_balance, " +
            "   (SELECT SUM(INPUT.amount) " +
            "         FROM TRANSACTION INPUT " +
            "         JOIN USER_CURRENCY_INVOICE_OPERATION_PERMISSION IOP ON " +
            "             (IOP.currency_id=INPUT.currency_id) " +
            "             AND (IOP.user_id = :requester_user_id) " +
            "             AND (IOP.operation_direction='REFILL') " +
            "         WHERE (INPUT.user_wallet_id = WALLET.id)  " +
            "           AND (INPUT.operation_type_id=1)  " +
            "           AND (INPUT.status_id=1)  " +
            "           AND (INPUT.provided=1) " +
            "           AND (INPUT.datetime BETWEEN STR_TO_DATE(:start_date, '%Y-%m-%d %H:%i:%s') AND STR_TO_DATE(:end_date, '%Y-%m-%d %H:%i:%s'))) " +
            "   AS input_amount,  " +
            "   (SELECT SUM(OUTPUT.amount) " +
            "         FROM TRANSACTION OUTPUT " +
            "         JOIN USER_CURRENCY_INVOICE_OPERATION_PERMISSION IOP ON " +
            "             (IOP.currency_id=OUTPUT.currency_id) " +
            "             AND (IOP.user_id = :requester_user_id) " +
            "             AND (IOP.operation_direction='WITHDRAW') " +
            "         WHERE (OUTPUT.user_wallet_id = WALLET.id)  " +
            "           AND (OUTPUT.operation_type_id=2)  " +
            "           AND (OUTPUT.status_id=1)  " +
            "           AND (OUTPUT.provided=1) " +
            "           AND (OUTPUT.datetime BETWEEN STR_TO_DATE(:start_date, '%Y-%m-%d %H:%i:%s') AND STR_TO_DATE(:end_date, '%Y-%m-%d %H:%i:%s'))) " +
            "   AS output_amount," +
            "   (SELECT IF (COUNT(*) = 2, 1, 0) " +
            "        FROM USER_CURRENCY_INVOICE_OPERATION_PERMISSION IOP " +
            "        WHERE (IOP.currency_id=CURRENCY.id) " +
            "        AND (IOP.user_id = :requester_user_id) ) AS both_permissions_present     " +
            " FROM USER  " +
            "   LEFT JOIN WALLET ON (WALLET.user_id = USER.id) " +
            "   JOIN CURRENCY ON (CURRENCY.id = WALLET.currency_id) and (CURRENCY.hidden <> 1)" +
            (roleIdList.isEmpty() ? "" :
                " AND USER.roleid IN (:role_id_list)") +
            " WHERE " +
            "   USER.status = " + UserStatus.ACTIVE.getStatus() +
            "   AND EXISTS (" +
            "       SELECT * " +
            "           FROM USER_CURRENCY_INVOICE_OPERATION_PERMISSION IOP " +
            "           WHERE (IOP.currency_id=CURRENCY.id " +
            "                 AND (IOP.user_id = :requester_user_id)) ) ";

    Map<String, Object> namedParameters = new HashMap<>();
    namedParameters.put("start_date", startDate);
    namedParameters.put("end_date", endDate);
    namedParameters.put("role_id_list", roleIdList);
    namedParameters.put("requester_user_id", requesterUserId);
    return jdbcTemplate.query(sql, namedParameters, (rs, idx) -> {
      UserSummaryDto userSummaryDto = new UserSummaryDto();
      userSummaryDto.setUserNickname(rs.getString("user_nickname"));
      userSummaryDto.setUserEmail(rs.getString("user_email"));
      userSummaryDto.setCreationDate(rs.getTimestamp("user_register_date") == null ? "" : rs.getTimestamp("user_register_date").toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
      userSummaryDto.setRegisteredIp(rs.getString("user_register_ip"));
      userSummaryDto.setLastIp(rs.getString("user_last_entry_ip"));
      userSummaryDto.setCurrencyName(rs.getString("currency_name"));
      userSummaryDto.setActiveBalance(rs.getBigDecimal("active_balance"));
      userSummaryDto.setReservedBalance(rs.getBigDecimal("reserved_balance"));
      userSummaryDto.setInputSummary(rs.getBigDecimal("input_amount"));
      userSummaryDto.setOutputSummary(rs.getBigDecimal("output_amount"));
      userSummaryDto.setBothCurrencyPermissionsPresent(rs.getBoolean("both_permissions_present"));
      return userSummaryDto;
    });
  }

  @Override
  public List<UserSummaryOrdersDto> getUserSummaryOrdersList(Integer requesterUserId, String startDate, String endDate, List<Integer> roleIdList) {
    String sql = " SELECT USER.email AS email, CURRENCY.name AS currency_name, USER_ROLE.name AS role, " +
        "     SUM(IF(TX.operation_type_id = 1, TX.amount, 0)) AS amount_buy, " +
        "     SUM(IF(TX.operation_type_id = 1, TX.commission_amount, 0)) AS amount_buy_fee, " +
        "     SUM(IF(TX.operation_type_id = 2, TX.amount, 0)) AS amount_sell, " +
        "     SUM(IF(TX.operation_type_id = 2, TX.commission_amount, 0)) AS amount_sell_fee" +
        "   FROM WALLET " +
        "     JOIN USER ON (USER.id=WALLET.user_id) " +
        (roleIdList.isEmpty() ? "" :
            "     AND USER.roleid IN (:role_id_list)") +
        "     JOIN USER_ROLE ON (USER_ROLE.id = USER.roleid) " +
        "     JOIN CURRENCY ON (CURRENCY.id = WALLET.currency_id) " +
        "     JOIN TRANSACTION TX ON TX.operation_type_id IN (1,2) " +
        "             and TX.source_type='ORDER' " +
        "             and TX.user_wallet_id=WALLET.id " +
        "             and (TX.datetime BETWEEN STR_TO_DATE(:start_date, '%Y-%m-%d %H:%i:%s') " +
        "                                              AND STR_TO_DATE(:end_date, '%Y-%m-%d %H:%i:%s'))" +
        " WHERE EXISTS (" +
        "       SELECT * " +
        "           FROM USER_CURRENCY_INVOICE_OPERATION_PERMISSION IOP " +
        "           WHERE (IOP.currency_id=CURRENCY.id " +
        "                 AND (IOP.user_id = :requester_user_id)) ) " +
        " GROUP BY email, currency_name, role  ";
    Map<String, Object> namedParameters = new HashMap<String, Object>() {{
      put("start_date", startDate);
      put("end_date", endDate);
      put("roles", roleIdList);
      put("requester_user_id", requesterUserId);
    }};
    return jdbcTemplate.query(sql, namedParameters, (rs, idx) -> {
      UserSummaryOrdersDto userSummaryOrdersDto = new UserSummaryOrdersDto();
      userSummaryOrdersDto.setUserEmail(rs.getString("email"));
      userSummaryOrdersDto.setWallet(rs.getString("currency_name"));
      userSummaryOrdersDto.setRole(rs.getString("role"));
      userSummaryOrdersDto.setAmountBuy(rs.getBigDecimal("amount_buy"));
      userSummaryOrdersDto.setAmountBuyFee(rs.getBigDecimal("amount_buy_fee"));
      userSummaryOrdersDto.setAmountSell(rs.getBigDecimal("amount_sell"));
      userSummaryOrdersDto.setAmountSellFee(rs.getBigDecimal("amount_sell_fee"));
      return userSummaryOrdersDto;
    });
  }

}