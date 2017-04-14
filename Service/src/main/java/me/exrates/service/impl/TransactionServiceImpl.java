package me.exrates.service.impl;

import me.exrates.dao.TransactionDao;
import me.exrates.model.*;
import me.exrates.model.Currency;
import me.exrates.model.dto.OperationViewDto;
import me.exrates.model.dto.TransactionFlatForReportDto;
import me.exrates.model.dto.UserSummaryDto;
import me.exrates.model.dto.UserSummaryOrdersDto;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.onlineTableDto.AccountStatementDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.TransactionType;
import me.exrates.model.vo.CacheData;
import me.exrates.service.*;
import me.exrates.service.exception.TransactionPersistException;
import me.exrates.service.exception.TransactionProvidingException;
import me.exrates.service.util.Cache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Integer.valueOf;
import static java.math.BigDecimal.ROUND_HALF_UP;

@Service
public class TransactionServiceImpl implements TransactionService {

  private static final Logger LOG = LogManager.getLogger(TransactionServiceImpl.class);
  private static final int decimalPlaces = 8;


  @Autowired
  private TransactionDao transactionDao;
  @Autowired
  private WalletService walletService;
  @Autowired
  private CompanyWalletService companyWalletService;
  @Autowired
  private UserService userService;
  @Autowired
  private OrderService orderService;
  @Autowired
  private MerchantService merchantService;
  @Autowired
  private CurrencyService currencyService;

  @Override
  @Transactional(propagation = Propagation.MANDATORY)
  public Transaction createTransactionRequest(CreditsOperation creditsOperation) {
    final Currency currency = creditsOperation.getCurrency();
    final User user = creditsOperation.getUser();
    final String currencyName = currency.getName();

    CompanyWallet companyWallet = companyWalletService.findByCurrency(currency);
    companyWallet = companyWallet == null ? companyWalletService.create(currency) : companyWallet;

    Wallet userWallet = walletService.findByUserAndCurrency(user, currency);
    userWallet = userWallet == null ? walletService.create(user, currency) : userWallet;

    Transaction transaction = new Transaction();
    transaction.setAmount(creditsOperation.getAmount());
    transaction.setCommissionAmount(creditsOperation.getCommissionAmount());
    transaction.setCommission(creditsOperation.getCommission());
    transaction.setCompanyWallet(companyWallet);
    transaction.setUserWallet(userWallet);
    transaction.setCurrency(currency);
    transaction.setDatetime(LocalDateTime.now());
    transaction.setMerchant(creditsOperation.getMerchant());
    transaction.setOperationType(creditsOperation.getOperationType());
    transaction.setProvided(false);
    transaction.setConfirmation((currencyName).equals("BTC") ? -1 : -1);
    transaction.setSourceType(creditsOperation.getTransactionSourceType());
    transaction = transactionDao.create(transaction);
    if (transaction == null) {
      throw new TransactionPersistException("Failed to provide transaction ");
    }
    LOG.info("Transaction created:" + transaction);
    return transaction;
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
  public Transaction findById(int id) {
    return transactionDao.findById(id);
  }

  @Override
  public void updateTransactionAmount(Transaction transaction) {
    updateAmount(transaction, transaction.getAmount());
  }

  @Override
  public void updateTransactionAmount(final Transaction transaction, final BigDecimal amount) {
    if (transaction.getOperationType() != OperationType.INPUT) {
      throw new IllegalArgumentException("Updating amount only available for INPUT operation");
    }
    updateAmount(transaction, amount);
  }

  @Override
  public BigDecimal calculateNewCommission(Transaction transaction, BigDecimal amount) {
    return calculateCommissionFromAmpunt(amount, transaction.getCommission().getValue(),
        currencyService.resolvePrecision(transaction.getCurrency().getName()));
  }

  private void updateAmount(Transaction transaction, BigDecimal amount) {
    int scale = currencyService.resolvePrecision(transaction.getCurrency().getName());
    BigDecimal commissionRate = transaction.getCommission().getValue();
    BigDecimal commission = calculateCommissionFromAmpunt(amount, commissionRate, scale);
    final BigDecimal newAmount = amount.subtract(commission).setScale(scale, ROUND_HALF_UP);
    transaction.setCommissionAmount(commission);
    transaction.setAmount(newAmount);
    transactionDao.updateTransactionAmount(transaction.getId(), newAmount, commission);
  }

  private BigDecimal calculateCommissionFromAmpunt(BigDecimal amount, BigDecimal commissionRate, int scale) {
    BigDecimal mass = BigDecimal.valueOf(100L).add(commissionRate);
    return amount.multiply(commissionRate)
        .divide(mass, scale, ROUND_HALF_UP).setScale(scale, ROUND_HALF_UP);
  }

  @Override
  public void nullifyTransactionAmountForWithdraw(final Transaction transaction) {
    if (transaction.getOperationType() != OperationType.OUTPUT) {
      throw new IllegalArgumentException("Nullifying amount only available for OUTPUT operations");
    }
    updateAmount(transaction, BigDecimal.ZERO);
  }

  @Override
  public void updateTransactionConfirmation(final int transactionId, final int confirmations) {
    transactionDao.updateTransactionConfirmations(transactionId, confirmations);
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void provideTransaction(Transaction transaction) {
    switch (transaction.getOperationType()) {
      case INPUT:
        walletService.depositActiveBalance(transaction.getUserWallet(), transaction.getAmount());
        companyWalletService.deposit(transaction.getCompanyWallet(), transaction.getAmount(),
            transaction.getCommissionAmount());
        break;
      case OUTPUT:
        walletService.withdrawReservedBalance(transaction.getUserWallet(), transaction.getAmount().add(transaction.getCommissionAmount()));
        companyWalletService.withdraw(transaction.getCompanyWallet(), transaction.getAmount(),
            transaction.getCommissionAmount());
        break;
    }
    if (!transactionDao.provide(transaction.getId())) {
      throw new TransactionProvidingException("Failed to provide transaction #" + transaction.getId());
    }
  }

  @Override
  @Transactional(propagation = Propagation.REQUIRED)
  public void invalidateTransaction(Transaction transaction) {
    if (!transactionDao.delete(transaction.getId())) {
      throw new TransactionProvidingException("Failed to delete transaction #" + transaction.getId());
    }
  }

  @Override
  public DataTable<List<OperationViewDto>> showMyOperationHistory(
      Integer requesterUserId,
      String email, final Integer status,
      final List<TransactionType> types, final List<Integer> merchantIds,
      final String dateFrom, final String dateTo,
      final BigDecimal fromAmount, final BigDecimal toAmount,
      final BigDecimal fromCommissionAmount, final BigDecimal toCommissionAmount,
      int offset, int limit,
      String sortColumn, String sortDirection, Locale locale) {
    final int userId = userService.getIdByEmail(email);
    requesterUserId = requesterUserId.equals(userId) ? null : requesterUserId;
    final List<Integer> wallets = walletService.getAllWallets(userId).stream()
        .mapToInt(Wallet::getId)
        .boxed()
        .collect(Collectors.toList());
    final DataTable<List<OperationViewDto>> result = new DataTable<>();
    if (wallets.isEmpty()) {
      result.setData(new ArrayList<>());
      return result;
    }
    final PagingData<List<Transaction>> transactions = transactionDao.findAllByUserWallets(requesterUserId, wallets, status, types, merchantIds,
        dateFrom, dateTo, fromAmount, toAmount, fromCommissionAmount, toCommissionAmount, offset, limit, sortColumn, sortDirection, locale);
    final List<OperationViewDto> operationViews = new ArrayList<>();
    for (final Transaction t : transactions.getData()) {
      OperationViewDto view = new OperationViewDto();
      view.setDatetime(t.getDatetime());
      view.setAmount(t.getAmount());
      view.setCommissionAmount(t.getCommissionAmount());
      view.setCurrency(t.getCurrency().getName());
      view.setOrder(t.getOrder());
      view.setStatus(merchantService.resolveTransactionStatus(t, locale));
      view.setOperationType(TransactionType.resolveFromOperationTypeAndSource(t.getSourceType(), t.getOperationType(), t.getAmount()));
      view.setMerchant(t.getMerchant() == null ? new Merchant(0, null, t.getSourceType().name(), null) : t.getMerchant());
      view.setSourceType(t.getSourceType().name());
      view.setSourceId(t.getSourceId());
      setTransactionMerchantAndOrder(view, t);
      operationViews.add(view);
    }
    result.setData(operationViews);
    result.setRecordsFiltered(transactions.getFiltered());
    result.setRecordsTotal(transactions.getTotal());
    return result;
  }

  private void setTransactionMerchantAndOrder(OperationViewDto view, Transaction transaction) {
    TransactionSourceType sourceType = transaction.getSourceType();
    OperationType operationType = transaction.getOperationType();
    BigDecimal amount = transaction.getAmount();
    view.setOperationType(TransactionType.resolveFromOperationTypeAndSource(sourceType, operationType, amount));
    if (sourceType == TransactionSourceType.MERCHANT || sourceType == TransactionSourceType.WITHDRAW) {
      view.setMerchant(transaction.getMerchant());
    } else {
      view.setMerchant(new Merchant(0, sourceType.name(), sourceType.name(), null));
    }

  }

  @Override
  public DataTable<List<OperationViewDto>> showMyOperationHistory(Integer requesterUserId, String email, Locale locale, int offset, int limit) {
    return showMyOperationHistory(requesterUserId, email, null, null, null, null, null, null, null, null, null, offset, limit, "", "ASC", locale);
  }

  @Override
  public DataTable<List<OperationViewDto>> showMyOperationHistory(Integer requesterUserId, final String email, final Locale locale) {
    return showMyOperationHistory(requesterUserId, email, locale, -1, -1);
  }

  @Override
  public DataTable<List<OperationViewDto>> showUserOperationHistory(Integer requesterUserId, final int id, final Locale locale) {
    return showMyOperationHistory(requesterUserId, userService.getUserById(id).getEmail(), locale);
  }

  @Override
  public DataTable<List<OperationViewDto>> showUserOperationHistory(
      Integer requesterUserId,
      int id,
      Integer status,
      List<TransactionType> types,
      List<Integer> merchantIds,
      String dateFrom,
      String dateTo,
      BigDecimal fromAmount,
      BigDecimal toAmount,
      BigDecimal fromCommissionAmount,
      BigDecimal toCommissionAmount,
      Locale locale,
      Map<String, String> viewParams) {
    if (viewParams.containsKey("start") && viewParams.containsKey("length")) {
      String sortColumnKey = "columns[" + viewParams.getOrDefault("order[0][column]", "0") + "][data]";
      String sortColumn = viewParams.getOrDefault(sortColumnKey, "");
      String sortDirection = viewParams.getOrDefault("order[0][dir]", "asc").toUpperCase();
      return showMyOperationHistory(requesterUserId, userService.getUserById(id).getEmail(), status, types, merchantIds, dateFrom, dateTo, fromAmount, toAmount,
          fromCommissionAmount, toCommissionAmount,
          valueOf(viewParams.get("start")), valueOf(viewParams.get("length")), sortColumn, sortDirection, locale);
    }
    return showUserOperationHistory(requesterUserId, id, locale);
  }

  @Override
  public List<AccountStatementDto> getAccountStatement(CacheData cacheData, Integer walletId, Integer offset, Integer limit, Locale locale) {
    List<AccountStatementDto> result = transactionDao.getAccountStatement(walletId, offset, limit, locale);
    if (Cache.checkCache(cacheData, result)) {
      result = new ArrayList<AccountStatementDto>() {{
        add(new AccountStatementDto(false));
      }};
    }
    return result;
  }

  @Override
  public DataTable<List<AccountStatementDto>> getAccountStatementForAdmin(Integer walletId, Integer offset, Integer limit, Locale locale) {
    DataTable<List<AccountStatementDto>> result = new DataTable<>();
    int total = transactionDao.getStatementSize(walletId);
    result.setRecordsFiltered(total);
    result.setRecordsTotal(total);
    result.setData(transactionDao.getAccountStatement(walletId, offset, limit, locale)
        .stream().filter(statement -> statement.getTransactionId() > 0).collect(Collectors.toList()));
    LOG.debug(result);
    return result;
  }

  @Override
  public BigDecimal maxAmount() {
    return transactionDao.maxAmount();
  }

  @Override
  public BigDecimal maxCommissionAmount() {
    return transactionDao.maxCommissionAmount();
  }

  @Override
  public List<AccountStatementDto> getAccountStatement(Integer walletId, Integer offset, Integer limit, Locale locale) {
    return transactionDao.getAccountStatement(walletId, offset, limit, locale);
  }

  @Override
  @Transactional
  public void setSourceId(Integer trasactionId, Integer sourceId) {
    transactionDao.setSourceId(trasactionId, sourceId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<TransactionFlatForReportDto> getAllByDateIntervalAndRoleAndOperationTypeAndCurrencyAndSourceType(
      String startDate,
      String endDate,
      Integer operationType,
      List<Integer> roleIdList,
      List<Integer> currencyList,
      List<String> sourceTypeList) {
    return transactionDao.findAllByDateIntervalAndRoleAndOperationTypeAndCurrencyAndSourceType(startDate, endDate, operationType, roleIdList, currencyList, sourceTypeList);
  }

  @Override
  @Transactional
  public boolean setStatusById(Integer trasactionId, Integer statusId) {
    return transactionDao.setStatusById(trasactionId, statusId);
  }

  @Override
  public List<String> getCSVTransactionsHistory(int requesterUserId, String email, String startDate, String endDate) {

    final List<Integer> wallets = walletService.getAllWallets(requesterUserId).stream()
        .mapToInt(Wallet::getId)
        .boxed()
        .collect(Collectors.toList());
    if (wallets.isEmpty()) {
      return Collections.emptyList();
    }
    String sortColumn = "TRANSACTION.datetime";
    String sortDirection = "DESC";
    List<Transaction> transactions = transactionDao.getAllOperationsByUserForPeriod(wallets, startDate, endDate, sortColumn, sortDirection);
    DataTable<List<OperationViewDto>> history =
            showMyOperationHistory(requesterUserId, email, null, null, null, startDate, endDate,
                    null, null, null, null, -1, -1, sortColumn, sortDirection, Locale.ENGLISH);

    return convertTrListToString(history.getData());
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserSummaryDto> getTurnoverInfoByUserAndCurrencyForPeriodAndRoleList(
      Integer requesterUserId,
      String startDate,
      String endDate,
      List<Integer> roleIdList) {
    return transactionDao.getTurnoverInfoByUserAndCurrencyForPeriodAndRoleList(requesterUserId, startDate, endDate, roleIdList);
  }

  @Override
  public List<UserSummaryOrdersDto> getUserSummaryOrdersList(Integer requesterUserId, String startDate, String endDate, List<Integer> roles) {
    return transactionDao.getUserSummaryOrdersList(requesterUserId, startDate, endDate, roles);
  }

  private List<String> convertTrListToString(List<OperationViewDto> transactions) {
    List<String> transactionsResult = new ArrayList<>();
    transactionsResult.add(getCSVTransactionsHeader());
    transactionsResult.add("\n");
    transactions.forEach(i -> {
      StringBuilder sb = new StringBuilder();
      sb.append(i.getDatetime())
          .append(";")
          .append(i.getOperationType())
          .append(";")
          .append(i.getStatus())
          .append(";")
          .append(i.getCurrency())
          .append(";")
          .append(i.getAmount())
          .append(";")
          .append(i.getCommissionAmount())
          .append(";")
          .append(i.getMerchant().getName())
          .append(";")
          .append(i.getOrder() != null ? i.getOrder().getId() : 0);
      transactionsResult.add(sb.toString());
      transactionsResult.add("\n");
    });
    return transactionsResult;
  }

  private String getCSVTransactionsHeader() {
    return "Date;Operation Type;Status;Currency;Amount;Comission;Merchant;Source Id";
  }

}
