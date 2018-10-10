package me.exrates.service.impl;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.ReportDao;
import me.exrates.model.Email;
import me.exrates.model.dto.*;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.AdminTransactionsFilterData;
import me.exrates.model.enums.ReportGroupUserRole;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.invoice.InvoiceOperationDirection;
import me.exrates.service.*;
import me.exrates.service.job.report.ReportMailingJob;
import org.apache.commons.codec.Charsets;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static me.exrates.model.enums.invoice.InvoiceOperationDirection.REFILL;
import static me.exrates.model.enums.invoice.InvoiceOperationDirection.WITHDRAW;

/**
 * Created by ValkSam
 */
@Service
@Log4j2
public class ReportServiceImpl implements ReportService {

    @Autowired
    TransactionService transactionService;

    @Autowired
    MerchantService merchantService;

    @Autowired
    CurrencyService currencyService;

    @Autowired
    UserService userService;

    @Autowired
    UserRoleService userRoleService;

    @Autowired
    WithdrawService withdrawService;

    @Autowired
    RefillService refillService;

    @Autowired
    OrderService orderService;

    @Autowired
    InputOutputService inputOutputService;

    @Autowired
    SendMailService sendMailService;

    @Autowired
    WalletService walletService;

    @Autowired
    ReportDao reportDao;

    @Autowired
    Scheduler reportScheduler;


    private final String MAIL_JOB_NAME = "REPORT_MAIL_JOB";
    private final String MAIL_TRIGGER_NAME = "REPORT_MAIL_TRIGGER";

    @PostConstruct
    private void initMailing() {
        try {
            if (isReportMailingEnabled()) {
                scheduleMailingJob();
            }
        } catch (SchedulerException e) {
            log.error(e);
        }
    }

    @Override
    public List<InvoiceReportDto> getInvoiceReport(
            String requesterUserEmail,
            String startDate,
            String endDate,
            String businessRole,
            String direction,
            List<String> currencyList) {
        AvailableCurrencies availableCurrencies = new AvailableCurrencies(requesterUserEmail, currencyList);
        List<Integer> currencyListForRefillOperation = availableCurrencies.getCurrencyListForRefillOperation();
        List<Integer> currencyListForWithdrawOperation = availableCurrencies.getCurrencyListForWithdrawOperation();
        /**/
        direction = "ANY".equals(direction) ? "" : InvoiceOperationDirection.valueOf(direction).name();
        List<Integer> realRoleIdList = userRoleService.getRealUserRoleIdByBusinessRoleList(businessRole);
        /**/
        List<InvoiceReportDto> result = new ArrayList<>();
        /**/
        if ((StringUtils.isEmpty(direction) || InvoiceOperationDirection.valueOf(direction) == WITHDRAW)
                && !currencyListForWithdrawOperation.isEmpty()) {
            List<WithdrawRequestFlatForReportDto> withdrawRequestList = withdrawService.findAllByDateIntervalAndRoleAndCurrency(
                    startDate, endDate, realRoleIdList, currencyListForWithdrawOperation);
            result.addAll(withdrawRequestList.stream()
                    .map(InvoiceReportDto::new)
                    .collect(Collectors.toList()));
        }
        if ((StringUtils.isEmpty(direction) || InvoiceOperationDirection.valueOf(direction) == REFILL)
                && !currencyListForRefillOperation.isEmpty()) {
            List<RefillRequestFlatForReportDto> refillRequestList = refillService.findAllByDateIntervalAndRoleAndCurrency(
                    startDate, endDate, realRoleIdList, currencyListForWithdrawOperation);
            result.addAll(refillRequestList.stream()
                    .map(InvoiceReportDto::new)
                    .collect(Collectors.toList()));
        }
        //wolper 24.04.18
        Map<String, RatesUSDForReportDto> rates = orderService.getRatesToUSDForReportByCurName();
        result.stream().forEach(s -> s.setRateToUSD(rates.get(s.getCurrency()) == null ? BigDecimal.ZERO : rates.get(s.getCurrency()).getRate()));
        //
        return result.stream()
                .sorted((a, b) -> a.getCreationDate().compareTo(b.getCreationDate()))
                .collect(Collectors.toList());
    }

    @Override
    public List<SummaryInOutReportDto> getUsersSummaryInOutList(
            String requesterUserEmail,
            String startDate,
            String endDate,
            String businessRole,
            List<String> currencyList) {
        List<InvoiceReportDto> result = getInvoiceReport(
                requesterUserEmail,
                startDate,
                endDate,
                businessRole,
                "ANY",
                currencyList
        )
                .stream()
                .filter(e -> e.getStatusEnum() == null ? "PROVIDED".equals(e.getStatus()) : e.getStatusEnum().isSuccessEndStatus())
                .collect(Collectors.toList());
        return result.stream()
                .map(SummaryInOutReportDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, UserSummaryTotalInOutDto> getUsersSummaryInOutMap(List<SummaryInOutReportDto> resultList) {
        Map<String, UserSummaryTotalInOutDto> resultMap = new HashMap<String, UserSummaryTotalInOutDto>() {
            @Override
            public UserSummaryTotalInOutDto put(String key, UserSummaryTotalInOutDto value) {
                if (this.get(key) == null) {
                    return super.put(key, value);
                } else {
                    UserSummaryTotalInOutDto storedValue = this.get(key);
                    storedValue.setTotalIn(storedValue.getTotalIn().add(value.getTotalIn()));
                    storedValue.setTotalOut(storedValue.getTotalOut().add(value.getTotalOut()));
                    return super.put(key, storedValue);
                }
            }
        };
        resultList.forEach(e -> resultMap.put(
                e.getCurrency(),
                new UserSummaryTotalInOutDto(e.getCurrency(), StringUtils.isEmpty(e.getCreationDateIn()) ? BigDecimal.ZERO : e.getAmount(), StringUtils.isEmpty(e.getCreationDateOut()) ? BigDecimal.ZERO : e.getAmount())
        ));
        return resultMap;
    }

    @Override
    public List<UserSummaryDto> getTurnoverInfoByUserAndCurrencyForPeriodAndRoleList(
            String requesterUserEmail,
            String startDate,
            String endDate,
            String businessRole,
            List<String> currencyList) {
        Integer requesterUserId = userService.getIdByEmail(requesterUserEmail);
        List<Integer> realRoleIdList = userRoleService.getRealUserRoleIdByBusinessRoleList(businessRole);
        return transactionService.getTurnoverInfoByUserAndCurrencyForPeriodAndRoleList(requesterUserId, startDate, endDate, realRoleIdList);
    }


    @Override
    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    public List<UserSummaryOrdersDto> getUserSummaryOrdersList(
            String requesterUserEmail,
            String startDate,
            String endDate,
            String businessRole,
            List<String> currencyList) {
        Integer requesterUserId = userService.getIdByEmail(requesterUserEmail);
        List<Integer> realRoleIdList = userRoleService.getRealUserRoleIdByBusinessRoleList(businessRole);
        return transactionService.getUserSummaryOrdersList(requesterUserId, startDate, endDate, realRoleIdList);
    }

    @Override
    public List<UserSummaryOrdersByCurrencyPairsDto> getUserSummaryOrdersByCurrencyPairList(
            String requesterUserEmail,
            String startDate,
            String endDate,
            String businessRole) {
        Integer requesterUserId = userService.getIdByEmail(requesterUserEmail);
        List<Integer> realRoleIdList = userRoleService.getRealUserRoleIdByBusinessRoleList(businessRole);
        return orderService.getUserSummaryOrdersByCurrencyPairList(requesterUserId, startDate, endDate, realRoleIdList);
    }

    @Getter
    private class AvailableCurrencies {
        private String requesterUserEmail;
        private List<String> currencyList;
        private List<Integer> currencyListForRefillOperation;
        private List<Integer> currencyListForWithdrawOperation;

        AvailableCurrencies(String requesterUserEmail, List<String> currencyList) {
            this.requesterUserEmail = requesterUserEmail;
            this.currencyList = currencyList;
            init();
        }

        private void init() {
            Integer requesterUserId = userService.getIdByEmail(requesterUserEmail);
            /**/
            List<UserCurrencyOperationPermissionDto> userCurrencyOperationPermissionDtoList = currencyService.getCurrencyPermittedOperationList(requesterUserId);
            if (currencyList.contains("ALL")) {
                currencyList.clear();
                currencyList.add("ALL");
            }
            currencyListForRefillOperation = userCurrencyOperationPermissionDtoList.stream()
                    .filter(e -> e.getInvoiceOperationDirection() == REFILL
                            && (currencyList.contains("ALL") || currencyList.contains(e.getCurrencyName())))
                    .map(UserCurrencyOperationPermissionDto::getCurrencyId)
                    .collect(Collectors.toList());
            currencyListForWithdrawOperation = userCurrencyOperationPermissionDtoList.stream()
                    .filter(e -> e.getInvoiceOperationDirection() == WITHDRAW
                            && (currencyList.contains("ALL") || currencyList.contains(e.getCurrencyName())))
                    .map(UserCurrencyOperationPermissionDto::getCurrencyId)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<OperationViewDto> getTransactionsHistory(
            String requesterUserEmail,
            Integer userId,
            AdminTransactionsFilterData filterData) {
        Integer requesterUserId = userService.getIdByEmail(requesterUserEmail);
        String sortColumn = "TRANSACTION.datetime";
        String sortDirection = "DESC";
        DataTableParams dataTableParams = DataTableParams.sortNoPaginationParams(sortColumn, sortDirection);
        DataTable<List<OperationViewDto>> history = transactionService.showUserOperationHistory(
                requesterUserId,
                userId,
                filterData,
                dataTableParams,
                Locale.ENGLISH);
        return history.getData();
    }

    @Override
    public List<UserIpReportDto> getUserIpReport(String businessRole) {
        List<Integer> realUserRoleIds = StringUtils.isEmpty(businessRole) ? Collections.emptyList() :
                userRoleService.getRealUserRoleIdByBusinessRoleList(businessRole);

        return userService.getUserIpReportForRoles(realUserRoleIds);
    }

    @Override
    public List<CurrencyPairTurnoverReportDto> getCurrencyPairTurnoverForRealMoneyUsers(LocalDateTime startTime, LocalDateTime endTime) {
        List<UserRole> realMoneyUsengRoles = userRoleService.getRolesUsingRealMoney();
        return getCurrencyPairTurnoverForRoleList(startTime, endTime, realMoneyUsengRoles);
    }

    @Override
    public List<CurrencyInputOutputSummaryDto> getCurrencyTurnoverForRealMoneyUsers(LocalDateTime startTime, LocalDateTime endTime) {
        List<UserRole> realMoneyUsengRoles = userRoleService.getRolesUsingRealMoney();
        List<CurrencyInputOutputSummaryDto> report = getCurrencyTurnoverForRoleList(startTime, endTime, realMoneyUsengRoles);
        //wolper 24.04.19
        Map<Integer, RatesUSDForReportDto> rates = orderService.getRatesToUSDForReport();
        report.stream().forEach(s -> s.setRateToUSD(rates.get(s.getCurId()) == null ? BigDecimal.ZERO : rates.get(s.getCurId()).getRate()));
        //
        return report;
    }

    @Override
    public List<CurrencyPairTurnoverReportDto> getCurrencyPairTurnoverForRoleList(LocalDateTime startTime, LocalDateTime endTime,
                                                                                  List<UserRole> roleList) {
        Preconditions.checkArgument(!roleList.isEmpty(), "At least one role must be specified");
        return orderService.getCurrencyPairTurnoverForPeriod(startTime, endTime, roleList.stream()
                .map(UserRole::getRole).collect(Collectors.toList()));
    }

    @Override
    public List<OrdersCommissionSummaryDto> getOrderCommissionsByPairsForPeriod(LocalDateTime startTime, LocalDateTime endTime,
                                                                                List<UserRole> roleList) {
        Preconditions.checkArgument(!roleList.isEmpty(), "At least one role must be specified");
        return orderService.getOrderCommissionsByPairsForPeriod(startTime, endTime, roleList.stream()
                .map(UserRole::getRole).collect(Collectors.toList()));
    }

    @Override
    public List<CurrencyInputOutputSummaryDto> getCurrencyTurnoverForRoleList(LocalDateTime startTime, LocalDateTime endTime,
                                                                              List<UserRole> roleList) {
        Preconditions.checkArgument(!roleList.isEmpty(), "At least one role must be specified");
        List<CurrencyInputOutputSummaryDto> report = inputOutputService.getInputOutputSummary(startTime, endTime, roleList.stream()
                .map(UserRole::getRole).collect(Collectors.toList()));
        //wolper 24.04.19
        Map<Integer, RatesUSDForReportDto> rates = orderService.getRatesToUSDForReport();
        //
        report.stream().forEach(s -> s.setRateToUSD(rates.get(s.getCurId()) == null ? BigDecimal.ZERO : rates.get(s.getCurId()).getRate()));
        return report;
    }

    @Override
    public List<InputOutputCommissionSummaryDto> getInputOutputSummaryWithCommissions(LocalDateTime startTime, LocalDateTime endTime,
                                                                                      List<UserRole> roleList) {
        Preconditions.checkArgument(!roleList.isEmpty(), "At least one role must be specified");
        List<InputOutputCommissionSummaryDto> report = inputOutputService.getInputOutputSummaryWithCommissions(startTime, endTime, roleList.stream()
                .map(UserRole::getRole).collect(Collectors.toList()));
        //wolper 24.04.19
        Map<Integer, RatesUSDForReportDto> rates = orderService.getRatesToUSDForReport();
        report.stream().forEach(s -> s.setRateToUSD(rates.get(s.getCurId()) == null ? BigDecimal.ZERO : rates.get(s.getCurId()).getRate()));
        //
        return report;
    }


    @Override
    public List<UserRoleTotalBalancesReportDto<UserRole>> getWalletBalancesSummaryByRoles(List<UserRole> roles) {
        Preconditions.checkArgument(!roles.isEmpty(), "At least one role must be specified");
        List<UserRoleTotalBalancesReportDto<UserRole>> report = walletService.getWalletBalancesSummaryByRoles(roles);
        //wolper 24.04.18
        Map<Integer, RatesUSDForReportDto> rates = orderService.getRatesToUSDForReport();
        report.stream().forEach(s -> s.setRateToUSD(rates.get(s.getCurId()) == null ? BigDecimal.ZERO : rates.get(s.getCurId()).getRate()));
        //
        return report;
    }

    @Override
    public List<UserRoleTotalBalancesReportDto<ReportGroupUserRole>> getWalletBalancesSummaryByGroups() {
        List<UserRoleTotalBalancesReportDto<ReportGroupUserRole>> report = walletService.getWalletBalancesSummaryByGroups();
        //wolper 24.04.18
        Map<Integer, RatesUSDForReportDto> ratesList = orderService.getRatesToUSDForReport();
        report.stream().forEach(s -> s.setRateToUSD(ratesList.get(s.getCurId()) == null ? BigDecimal.ZERO : ratesList.get(s.getCurId()).getRate()));
        //
        return report;
    }


    @Override
    public boolean isReportMailingEnabled() {
        return reportDao.isReportMailingEnabled();
    }

    @Override
    public List<String> retrieveReportSubscribersList(boolean selectWithPremissions) {
        return reportDao.retrieveReportSubscribersList(selectWithPremissions);
    }

    @Override
    public void addReportSubscriber(String email) {
        reportDao.addReportSubscriber(email);
    }

    @Override
    public void deleteReportSubscriber(String email) {
        reportDao.deleteReportSubscriber(email);
    }

    @Override
    public String retrieveReportMailingTime() {
        return reportDao.retrieveReportMailingTime();
    }


    @Override
    public void setReportMailingStatus(boolean newStatus) {
        boolean oldStatus = isReportMailingEnabled();
        if (newStatus != oldStatus) {
            try {
                reportScheduler.deleteJob(JobKey.jobKey(MAIL_JOB_NAME));
                if (newStatus) {
                    scheduleMailingJob();
                }
                reportDao.updateReportMailingEnableStatus(newStatus);
            } catch (SchedulerException e) {
                log.error(e);
            }
        }
    }

    private void scheduleMailingJob() throws SchedulerException {
        LocalTime triggerFireTime = parseTime(retrieveReportMailingTime());
        JobDetail jobDetail = createJob();
        Trigger trigger = createTrigger(triggerFireTime.getHour(), triggerFireTime.getMinute());
        reportScheduler.scheduleJob(jobDetail, trigger);
    }

    @Override
    public void updateReportMailingTime(String newMailTimeString) {
        LocalTime newMailTime = parseTime(newMailTimeString);
        if (isReportMailingEnabled()) {
            rescheduleMailJob(newMailTime);
        }
        reportDao.updateReportMailingTime(newMailTimeString);

    }


    @Override
    public void sendReportMail() {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusHours(24L);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm");
        String title = String.format("Report for period %s - %s", startTime.format(dateTimeFormatter), endTime.format(dateTimeFormatter));

        String message = "New users: " + userService.getNewRegisteredUserNumber(startTime, endTime);

        List<CurrencyPairTurnoverReportDto> currencyPairTurnoverList = getCurrencyPairTurnoverForRealMoneyUsers(startTime, endTime);
        List<CurrencyInputOutputSummaryDto> currencyIOSummaryList = getCurrencyTurnoverForRealMoneyUsers(startTime, endTime);
        List<UserRoleTotalBalancesReportDto<ReportGroupUserRole>> balancesList = getWalletBalancesSummaryByGroups();


        String currencyPairReportContent = currencyPairTurnoverList.stream().map(CurrencyPairTurnoverReportDto::toString)
                .collect(Collectors.joining("", CurrencyPairTurnoverReportDto.getTitle(), ""));
        String currencyIOReportContent = currencyIOSummaryList.stream().map(CurrencyInputOutputSummaryDto::toString)
                .collect(Collectors.joining("", CurrencyInputOutputSummaryDto.getTitle(), ""));
        String balancesReportContent = balancesList.stream().map(UserRoleTotalBalancesReportDto::toString)
                .collect(Collectors.joining("", UserRoleTotalBalancesReportDto.getTitle(ReportGroupUserRole.class), ""));

        List<Email.Attachment> attachments = Arrays.asList(
                new Email.Attachment("currency_pairs.csv",
                        new ByteArrayResource(currencyPairReportContent.getBytes(Charsets.UTF_8)), "text/csv"),
                new Email.Attachment("currencies.csv",
                        new ByteArrayResource(currencyIOReportContent.getBytes(Charsets.UTF_8)), "text/csv"),
                new Email.Attachment("balances.csv",
                        new ByteArrayResource(balancesReportContent.getBytes(Charsets.UTF_8)), "text/csv")

        );


        List<String> subscribers = retrieveReportSubscribersList(true);
        subscribers.forEach(emailAddress -> {
            try {
                Email email = new Email();
                email.setSubject(title);
                email.setMessage(message);
                email.setTo(emailAddress);
                email.setAttachments(attachments);
                sendMailService.sendInfoMail(email);
            } catch (Exception e) {
                log.error(e);
            }
        });
    }


    @Override
    public List<ExternalWalletsDto> getBalancesWithExternalWallets() {
        return walletService.getBalancesWithExternalWallets();
    }


    private void rescheduleMailJob(LocalTime newMailTime) {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(MAIL_TRIGGER_NAME);
            JobDetail jobDetail = reportScheduler.getJobDetail(JobKey.jobKey(MAIL_JOB_NAME));
            if (jobDetail != null) {
                reportScheduler.rescheduleJob(triggerKey, createTrigger(newMailTime.getHour(), newMailTime.getMinute(), jobDetail));
            }
        } catch (SchedulerException e) {
            log.error(e);
        }
    }

    private JobDetail createJob() {
        return JobBuilder.newJob(ReportMailingJob.class)
                .withIdentity(MAIL_JOB_NAME)
                .build();
    }


    private Trigger createTrigger(int hour, int minute, JobDetail jobDetail) {
        return TriggerBuilder.newTrigger()
                .withIdentity(MAIL_TRIGGER_NAME)
                .forJob(jobDetail)
                .startNow()
                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(hour, minute)
                        .withMisfireHandlingInstructionFireAndProceed())
                .build();
    }

    private Trigger createTrigger(int hour, int minute) {
        return TriggerBuilder.newTrigger()
                .withIdentity(MAIL_TRIGGER_NAME)
                .startNow()
                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(hour, minute)
                        .withMisfireHandlingInstructionFireAndProceed())
                .build();
    }


    private LocalTime parseTime(String timeString) {
        return LocalTime.from(DateTimeFormatter.ofPattern("HH:mm").parse(timeString));
    }

}
