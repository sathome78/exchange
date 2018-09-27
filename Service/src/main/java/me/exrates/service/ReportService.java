package me.exrates.service;

import me.exrates.model.dto.*;
import me.exrates.model.dto.filterData.AdminTransactionsFilterData;
import me.exrates.model.enums.ReportGroupUserRole;
import me.exrates.model.enums.UserRole;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ReportService {
  List<InvoiceReportDto> getInvoiceReport(String requesterUserEmail, String startDate, String endDate, String businessRole, String direction, List<String> currencyList);

  List<SummaryInOutReportDto> getUsersSummaryInOutList(String requesterUserEmail, String startDate, String endDate, String businessRole, List<String> currencyList);

  Map<String, UserSummaryTotalInOutDto> getUsersSummaryInOutMap(List<SummaryInOutReportDto> resultList);

  List<UserSummaryDto> getTurnoverInfoByUserAndCurrencyForPeriodAndRoleList(String requesterUserEmail, String startDate, String endDate, String businessRole, List<String> currencyList);

  List<UserSummaryOrdersDto> getUserSummaryOrdersList(String requesterUserEmail, String startDate, String endDate, String businessRole, List<String> currencyList);

  List<UserSummaryOrdersByCurrencyPairsDto> getUserSummaryOrdersByCurrencyPairList(String requesterUserEmail, String startDate, String endDate, String businessRole);

  List<OperationViewDto> getTransactionsHistory(String requesterUserEmail, Integer userId, AdminTransactionsFilterData filterData);

  List<UserIpReportDto> getUserIpReport(String businessRole);

  List<CurrencyPairTurnoverReportDto> getCurrencyPairTurnoverForRealMoneyUsers(LocalDateTime startTime, LocalDateTime endTime);

  List<CurrencyInputOutputSummaryDto> getCurrencyTurnoverForRealMoneyUsers(LocalDateTime startTime, LocalDateTime endTime);

  List<CurrencyPairTurnoverReportDto> getCurrencyPairTurnoverForRoleList(LocalDateTime startTime, LocalDateTime endTime,
                                                                         List<UserRole> roleList);

  List<OrdersCommissionSummaryDto> getOrderCommissionsByPairsForPeriod(LocalDateTime startTime, LocalDateTime endTime,
                                                                       List<UserRole> roleList);

  List<CurrencyInputOutputSummaryDto> getCurrencyTurnoverForRoleList(LocalDateTime startTime, LocalDateTime endTime,
                                                                     List<UserRole> roleList);

  List<InputOutputCommissionSummaryDto> getInputOutputSummaryWithCommissions(LocalDateTime startTime, LocalDateTime endTime,
                                                                             List<UserRole> roleList);

  List<UserRoleTotalBalancesReportDto<UserRole>> getWalletBalancesSummaryByRoles(List<UserRole> roles);

  List<UserRoleTotalBalancesReportDto<ReportGroupUserRole>> getWalletBalancesSummaryByGroups();

  boolean isReportMailingEnabled();

  List<String> retrieveReportSubscribersList(boolean selectWithPremissions);

  String retrieveReportMailingTime();

  void setReportMailingStatus(boolean newStatus);

  void addReportSubscriber(String email);

  void deleteReportSubscriber(String email);

  void updateReportMailingTime(String newMailTimeString);

  void sendReportMail();

  List<ExternalWalletsDto> getBalancesWithExternalWallets();
}