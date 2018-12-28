package me.exrates.service;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Transaction;
import me.exrates.model.dto.InOutReportDto;
import me.exrates.model.dto.OperationViewDto;
import me.exrates.model.dto.TransactionFlatForReportDto;
import me.exrates.model.dto.UserSummaryDto;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.AdminTransactionsFilterData;
import me.exrates.model.dto.onlineTableDto.AccountStatementDto;
import me.exrates.model.enums.UserRole;
import me.exrates.model.vo.CacheData;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface TransactionService {

    Transaction createTransactionRequest(CreditsOperation creditsOperation);

    Transaction findById(int id);

    void updateTransactionAmount(Transaction transaction, BigDecimal amount);

    void updateTransactionAmount(Transaction transaction);

    BigDecimal calculateNewCommission(Transaction transaction, BigDecimal amount);

    void nullifyTransactionAmountForWithdraw(Transaction transaction);

    void updateTransactionConfirmation(int transactionId, int confirmations);

    void provideTransaction(Transaction transaction);

    void invalidateTransaction(Transaction transaction);

    DataTable<List<OperationViewDto>> showUserOperationHistory(Integer requesterUserId, Integer userId, AdminTransactionsFilterData filterData, DataTableParams dataTableParams, Locale locale);

    List<AccountStatementDto> getAccountStatement(CacheData cacheData, Integer walletId, Integer offset, Integer limit, Locale locale);

    DataTable<List<AccountStatementDto>> getAccountStatementForAdmin(Integer walletId, Integer offset, Integer limit, Locale locale);

    BigDecimal maxAmount();

    BigDecimal maxCommissionAmount();

    List<AccountStatementDto> getAccountStatement(Integer walletId, Integer offset, Integer limit, Locale locale);

    void setSourceId(Integer trasactionId, Integer sourceId);

    List<TransactionFlatForReportDto> getAllByDateIntervalAndRoleAndOperationTypeAndCurrencyAndSourceType(String startDate, String endDate, Integer operationType, List<Integer> roleIdList, List<Integer> currencyList, List<String> sourceTypeList);

    boolean setStatusById(Integer trasactionId, Integer statusId);

    List<Transaction> getPayedRefTransactionsByOrderId(int orderId);

    List<InOutReportDto> getInOutSummaryByPeriodAndRoles(LocalDateTime startTime, LocalDateTime endTime, List<UserRole> userRoles);

    List<UserSummaryDto> getUsersWalletSummaryData(LocalDateTime startTime, LocalDateTime endTime, String userEmail, int requesterId);
}
