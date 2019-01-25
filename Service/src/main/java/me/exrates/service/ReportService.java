package me.exrates.service;

import me.exrates.model.dto.BalancesDto;
import me.exrates.model.dto.OperationViewDto;
import me.exrates.model.dto.ReportDto;
import me.exrates.model.dto.UserRoleTotalBalancesReportDto;
import me.exrates.model.dto.filterData.AdminOrderFilterData;
import me.exrates.model.dto.filterData.AdminTransactionsFilterData;
import me.exrates.model.enums.ReportGroupUserRole;
import me.exrates.model.enums.UserRole;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReportService {

    List<OperationViewDto> getTransactionsHistory(String requesterUserEmail, Integer userId, AdminTransactionsFilterData filterData);

    List<UserRoleTotalBalancesReportDto<ReportGroupUserRole>> getWalletBalancesSummaryByGroups();

    boolean isReportMailingEnabled();

    List<String> retrieveReportSubscribersList(boolean selectWithPremissions);

    String retrieveReportMailingTime();

    void setReportMailingStatus(boolean newStatus);

    void addReportSubscriber(String email);

    void deleteReportSubscriber(String email);

    void updateReportMailingTime(String newMailTimeString);

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

    ReportDto getUsersWalletSummaryData(LocalDateTime startTime,
                                        LocalDateTime endTime,
                                        String userEmail,
                                        String requesterEmail) throws Exception;

    ReportDto getUserSummaryOrdersData(LocalDateTime startTime,
                                       LocalDateTime endTime,
                                       List<UserRole> userRoles,
                                       String requesterEmail) throws Exception;

    ReportDto getInvoiceReport(LocalDateTime startTime,
                               LocalDateTime endTime,
                               List<UserRole> userRoles,
                               String requesterEmail) throws Exception;

    ReportDto getCurrenciesTurnover(LocalDateTime startTime,
                                    LocalDateTime endTime,
                                    List<UserRole> userRoles) throws Exception;

    ReportDto getOrders(AdminOrderFilterData adminOrderFilterData) throws Exception;
}