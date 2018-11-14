package me.exrates.service;

import me.exrates.model.dto.BalancesDto;
import me.exrates.model.dto.ReportDto;
import me.exrates.model.dto.CurrencyInputOutputSummaryDto;
import me.exrates.model.dto.CurrencyPairTurnoverReportDto;
import me.exrates.model.dto.InOutReportDto;
import me.exrates.model.dto.InvoiceReportDto;
import me.exrates.model.dto.OperationViewDto;
import me.exrates.model.dto.OrdersCommissionSummaryDto;
import me.exrates.model.dto.SummaryInOutReportDto;
import me.exrates.model.dto.UserIpReportDto;
import me.exrates.model.dto.UserRoleTotalBalancesReportDto;
import me.exrates.model.dto.UserSummaryDto;
import me.exrates.model.dto.UserSummaryOrdersByCurrencyPairsDto;
import me.exrates.model.dto.UserSummaryOrdersDto;
import me.exrates.model.dto.UserSummaryTotalInOutDto;
import me.exrates.model.dto.filterData.AdminTransactionsFilterData;
import me.exrates.model.enums.ReportGroupUserRole;
import me.exrates.model.enums.UserRole;

import java.time.LocalDate;
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

//    List<InOutReportDto> getInputOutputSummaryWithCommissions(LocalDateTime startTime, LocalDateTime endTime,
//                                                              List<UserRole> roleList);

    List<UserRoleTotalBalancesReportDto<ReportGroupUserRole>> getWalletBalancesSummaryByGroups();

    boolean isReportMailingEnabled();

    List<String> retrieveReportSubscribersList(boolean selectWithPremissions);

    String retrieveReportMailingTime();

    void setReportMailingStatus(boolean newStatus);

    void addReportSubscriber(String email);

    void deleteReportSubscriber(String email);

    void updateReportMailingTime(String newMailTimeString);

    void sendReportMail();

    List<BalancesDto> getBalancesSliceStatistic();

    void generateWalletBalancesReportObject();

    List<ReportDto> getArchiveBalancesReports(LocalDate date);

    ReportDto getArchiveBalancesReportFile(Integer id) throws Exception;

    void generateInputOutputSummaryReportObject();

    List<ReportDto> getArchiveInputOutputReports(LocalDate date);

    ReportDto getArchiveInputOutputReportFile(Integer id) throws Exception;

    ReportDto getInputOutputSummaryReport(LocalDateTime startTime,
                                          LocalDateTime endTime,
                                          List<UserRole> userRoles) throws Exception;

    ReportDto getDifferenceBetweenBalancesReports(LocalDateTime startTime,
                                                  LocalDateTime endTime,
                                                  List<UserRole> roles) throws Exception;

    ReportDto getDifferenceBetweenBalancesReportsWithInOut(LocalDateTime startTime,
                                                           LocalDateTime endTime,
                                                           List<UserRole> userRoles) throws Exception;
}