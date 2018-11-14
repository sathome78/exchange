package me.exrates.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.ReportDao;
import me.exrates.model.Currency;
import me.exrates.model.Email;
import me.exrates.model.dto.BalancesDto;
import me.exrates.model.dto.CurrencyInputOutputSummaryDto;
import me.exrates.model.dto.CurrencyPairTurnoverReportDto;
import me.exrates.model.dto.ExternalWalletBalancesDto;
import me.exrates.model.dto.InOutReportDto;
import me.exrates.model.dto.InternalWalletBalancesDto;
import me.exrates.model.dto.InvoiceReportDto;
import me.exrates.model.dto.OperationViewDto;
import me.exrates.model.dto.OrdersCommissionSummaryDto;
import me.exrates.model.dto.RefillRequestFlatForReportDto;
import me.exrates.model.dto.ReportDto;
import me.exrates.model.dto.SummaryInOutReportDto;
import me.exrates.model.dto.UserCurrencyOperationPermissionDto;
import me.exrates.model.dto.UserIpReportDto;
import me.exrates.model.dto.UserRoleTotalBalancesReportDto;
import me.exrates.model.dto.UserSummaryDto;
import me.exrates.model.dto.UserSummaryOrdersByCurrencyPairsDto;
import me.exrates.model.dto.UserSummaryOrdersDto;
import me.exrates.model.dto.UserSummaryTotalInOutDto;
import me.exrates.model.dto.WalletBalancesDto;
import me.exrates.model.dto.WithdrawRequestFlatForReportDto;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.AdminTransactionsFilterData;
import me.exrates.model.enums.ReportGroupUserRole;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.invoice.InvoiceOperationDirection;
import me.exrates.service.CurrencyService;
import me.exrates.service.InputOutputService;
import me.exrates.service.MerchantService;
import me.exrates.service.OrderService;
import me.exrates.service.RefillService;
import me.exrates.service.ReportService;
import me.exrates.service.SendMailService;
import me.exrates.service.TransactionService;
import me.exrates.service.UserRoleService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.WithdrawService;
import me.exrates.service.api.ExchangeApi;
import me.exrates.service.job.report.ReportMailingJob;
import me.exrates.service.util.ReportFiveExcelGeneratorUtil;
import me.exrates.service.util.ReportFourExcelGeneratorUtil;
import me.exrates.service.util.ReportSixExcelGeneratorUtil;
import me.exrates.service.util.ReportTwoExcelGeneratorUtil;
import me.exrates.service.util.ZipUtil;
import org.apache.commons.codec.Charsets;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.lang3.tuple.Pair;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static me.exrates.model.enums.invoice.InvoiceOperationDirection.REFILL;
import static me.exrates.model.enums.invoice.InvoiceOperationDirection.WITHDRAW;
import static me.exrates.service.util.CollectionUtil.isEmpty;

/**
 * Created by ValkSam
 */
@Service
@Log4j2
public class ReportServiceImpl implements ReportService {

    private static final DateTimeFormatter FORMATTER_FOR_NAME = DateTimeFormatter.ofPattern("dd_MM_yyyy_HH_mm");

    private static final Integer TIME_RANGE = 3;

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

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    ExchangeApi exchangeApi;


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
        Map<String, Pair<BigDecimal, BigDecimal>> rates = exchangeApi.getRates();

        result.forEach(s -> s.setRateToUSD(isNull(rates.get(s.getCurrency())) ? BigDecimal.ZERO : rates.get(s.getCurrency()).getLeft()));
        //
        return result.stream()
                .sorted(Comparator.comparing(InvoiceReportDto::getCreationDate))
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

        Map<String, Pair<BigDecimal, BigDecimal>> rates = exchangeApi.getRates();

        report.forEach(s -> s.setRateToUSD(isNull(rates.get(s.getCurrencyName())) ? BigDecimal.ZERO : rates.get(s.getCurrencyName()).getLeft()));
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

        Map<String, Pair<BigDecimal, BigDecimal>> rates = exchangeApi.getRates();

        report.forEach(s -> s.setRateToUSD(isNull(rates.get(s.getCurrencyName())) ? BigDecimal.ZERO : rates.get(s.getCurrencyName()).getLeft()));
        return report;
    }

//    @Override
//    public List<InOutReportDto> getInputOutputSummaryWithCommissions(LocalDateTime startTime, LocalDateTime endTime,
//                                                                     List<UserRole> roleList) {
//        Preconditions.checkArgument(!roleList.isEmpty(), "At least one role must be specified");
//        List<InOutReportDto> report = inputOutputService.getInputOutputSummaryWithCommissions(startTime, endTime, roleList.stream()
//                .map(UserRole::getRole).collect(Collectors.toList()));
//
//        Map<String, Pair<BigDecimal, BigDecimal>> rates = exchangeApi.getRates();
//
//        report.forEach(s -> s.setRateToUSD(isNull(rates.get(s.getCurrencyName())) ? BigDecimal.ZERO : rates.get(s.getCurrencyName()).getLeft()));
//        //
//        return report;
//    }

    @Override
    public List<UserRoleTotalBalancesReportDto<ReportGroupUserRole>> getWalletBalancesSummaryByGroups() {
        List<UserRoleTotalBalancesReportDto<ReportGroupUserRole>> report = walletService.getWalletBalancesSummaryByGroups();

        Map<String, Pair<BigDecimal, BigDecimal>> rates = exchangeApi.getRates();

        report.forEach(s -> s.setRateToUSD(isNull(rates.get(s.getCurrency())) ? BigDecimal.ZERO : rates.get(s.getCurrency()).getLeft()));
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
    public void generateWalletBalancesReportObject() {
        StopWatch stopWatch = StopWatch.createStarted();
        log.info("Process of generating report object as byte array start...");

        List<String> curNames = currencyService.getAllCurrencies().stream().map(Currency::getName).collect(toList());

        final Map<String, ExternalWalletBalancesDto> externalWalletBalances = walletService.getExternalWalletBalances().stream()
                .collect(toMap(
                        ExternalWalletBalancesDto::getCurrencyName,
                        Function.identity()));
        final Map<String, List<InternalWalletBalancesDto>> internalWalletBalances = walletService.getInternalWalletBalances().stream()
                .collect(groupingBy(InternalWalletBalancesDto::getCurrencyName));

        List<WalletBalancesDto> balances = curNames.stream()
                .map(name -> new WalletBalancesDto(name, externalWalletBalances.get(name), internalWalletBalances.get(name)))
                .filter(walletBalancesDto -> nonNull(walletBalancesDto.getExternal()) && nonNull(walletBalancesDto.getInternals()))
                .collect(toList());

        byte[] zippedBytes;
        try {
            byte[] balancesBytes = objectMapper.writeValueAsBytes(balances);

            zippedBytes = ZipUtil.zip(balancesBytes);
        } catch (IOException ex) {
            log.warn("Problem with write balances object to byte array", ex);
            return;
        }
        final String fileName = String.format("report_balances_%s", LocalDateTime.now().format(FORMATTER_FOR_NAME));

        reportDao.addNewBalancesReportObject(zippedBytes, fileName);
        log.info("Process of generating report object as byte array end... Time: {}", stopWatch.getTime(TimeUnit.MILLISECONDS));
    }

    @Override
    public List<BalancesDto> getBalancesSliceStatistic() {
        List<String> curNames = currencyService.getAllCurrencies().stream().map(Currency::getName).collect(toList());

        final Map<String, ExternalWalletBalancesDto> externalWalletBalances = walletService.getExternalWalletBalances().stream()
                .collect(toMap(
                        ExternalWalletBalancesDto::getCurrencyName,
                        Function.identity()));
        final Map<String, List<InternalWalletBalancesDto>> internalWalletBalances = walletService.getInternalWalletBalances().stream()
                .collect(groupingBy(InternalWalletBalancesDto::getCurrencyName));

        final Map<String, Pair<BigDecimal, BigDecimal>> rates = exchangeApi.getRates();

        final LocalDateTime lastUpdated = LocalDateTime.now().withNano(0);

        return curNames.stream()
                .map(name -> {
                    Pair<BigDecimal, BigDecimal> ratePair = rates.get(name);
                    if (isNull(ratePair)) {
                        ratePair = Pair.of(BigDecimal.ZERO, BigDecimal.ZERO);
                    }

                    final BigDecimal usdRate = ratePair.getLeft();
                    final BigDecimal btcRate = ratePair.getRight();

                    ExternalWalletBalancesDto extWalletBalance = externalWalletBalances.get(name);
                    List<InternalWalletBalancesDto> intWalletBalances = internalWalletBalances.get(name);

                    if (isNull(extWalletBalance) || isEmpty(intWalletBalances)) {
                        return null;
                    }

                    final Integer currencyId = extWalletBalance.getCurrencyId();
                    final String currencyName = extWalletBalance.getCurrencyName();

                    final BigDecimal externalTotalBalance = extWalletBalance.getTotalBalance();
                    final BigDecimal externalTotalBalanceUSD = externalTotalBalance.multiply(usdRate);
                    final BigDecimal externalTotalBalanceBTC = externalTotalBalance.multiply(btcRate);

                    final BigDecimal internalTotalBalance = intWalletBalances.stream()
                            .filter(inWallet -> inWallet.getRoleName() != UserRole.BOT_TRADER)
                            .map(InternalWalletBalancesDto::getTotalBalance)
                            .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
                    final BigDecimal internalTotalBalanceUSD = internalTotalBalance.multiply(usdRate);
                    final BigDecimal internalTotalBalanceBTC = internalTotalBalance.multiply(btcRate);

                    final BigDecimal deviation = externalTotalBalance.subtract(internalTotalBalance);
                    final BigDecimal deviationUSD = deviation.multiply(usdRate);
                    final BigDecimal deviationBTC = deviation.multiply(btcRate);

                    return BalancesDto.builder()
                            .currencyId(currencyId)
                            .currencyName(currencyName)
                            .usdRate(usdRate)
                            .btcRate(btcRate)
                            .totalWalletBalance(externalTotalBalance)
                            .totalWalletBalanceUSD(externalTotalBalanceUSD)
                            .totalWalletBalanceBTC(externalTotalBalanceBTC)
                            .totalExratesBalance(internalTotalBalance)
                            .totalExratesBalanceUSD(internalTotalBalanceUSD)
                            .totalExratesBalanceBTC(internalTotalBalanceBTC)
                            .deviation(deviation)
                            .deviationUSD(deviationUSD)
                            .deviationBTC(deviationBTC)
                            .lastUpdatedDate(lastUpdated)
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReportDto> getArchiveBalancesReports(LocalDate date) {
        return reportDao.getBalancesReportsNames(date.atTime(LocalTime.MIN), date.atTime(LocalTime.MAX));
    }

    @Transactional(readOnly = true)
    @Override
    public ReportDto getArchiveBalancesReportFile(Integer id) throws Exception {
        ReportDto balancesReport = reportDao.getBalancesReportById(id);
        final byte[] zippedBytes = balancesReport.getContent();
        final LocalDateTime createdAt = balancesReport.getCreatedAt();

        Map<String, WalletBalancesDto> balancesMap = getWalletBalances(zippedBytes);

        final Map<String, Pair<BigDecimal, BigDecimal>> ratesMap = exchangeApi.getRates();

        return balancesReport.toBuilder()
                .content(ReportFourExcelGeneratorUtil.generate(
                        new TreeMap<>(balancesMap),
                        createdAt,
                        ratesMap))
                .build();
    }

    @Override
    public void generateInputOutputSummaryReportObject() {
        StopWatch stopWatch = StopWatch.createStarted();
        log.info("Process of generating report object as byte array start...");

        LocalDateTime now = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);

        final LocalDateTime startTime = now.minusHours(1);
        final LocalDateTime endTime = now.minusNanos(1);
        final List<UserRole> roles = Arrays.stream(UserRole.values()).collect(toList());

        List<InOutReportDto> inOut = transactionService.getInOutSummaryByPeriodAndRoles(startTime, endTime, roles);

        byte[] zippedBytes;
        try {
            byte[] inOutBytes = objectMapper.writeValueAsBytes(inOut);

            zippedBytes = ZipUtil.zip(inOutBytes);
        } catch (IOException ex) {
            log.warn("Problem with write in/out object to byte array", ex);
            return;
        }
        final String fileName = String.format("report_input_output_%s-%s", startTime.format(FORMATTER_FOR_NAME), endTime.format(FORMATTER_FOR_NAME));

        reportDao.addNewInOutReportObject(zippedBytes, fileName);
        log.info("Process of generating report object as byte array end... Time: {}", stopWatch.getTime(TimeUnit.MILLISECONDS));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ReportDto> getArchiveInputOutputReports(LocalDate date) {
        return reportDao.getInOutReportsNames(date.atTime(LocalTime.MIN), date.atTime(LocalTime.MAX));
    }

    @Transactional(readOnly = true)
    @Override
    public ReportDto getArchiveInputOutputReportFile(Integer id) throws Exception {
        ReportDto inOutReport = reportDao.getInOutReportById(id);
        final byte[] zippedBytes = inOutReport.getContent();

        Map<String, InOutReportDto> inOutMap = getInOutSummary(zippedBytes);

        final Map<String, Pair<BigDecimal, BigDecimal>> ratesMap = exchangeApi.getRates();

        return inOutReport.toBuilder()
                .content(ReportTwoExcelGeneratorUtil.generate(
                        new TreeMap<>(inOutMap),
                        ratesMap))
                .build();
    }

    @Override
    public ReportDto getInputOutputSummaryReport(LocalDateTime startTime,
                                                 LocalDateTime endTime,
                                                 List<UserRole> roles) throws Exception {
        Preconditions.checkArgument(!roles.isEmpty(), "At least one role must be specified");

        List<InOutReportDto> inOutSummary = transactionService.getInOutSummaryByPeriodAndRoles(startTime, endTime, roles);
        if (isEmpty(inOutSummary)) {
            throw new Exception(String.format("No input/output information found for period: [%s, %s] and user roles: [%s]",
                    startTime.toString(),
                    endTime.toString(),
                    roles.stream().map(Enum::name).collect(joining(", "))));
        }
        final Map<String, InOutReportDto> inOutMap = inOutSummary.stream().collect(toMap(InOutReportDto::getCurrencyName, Function.identity()));

        final Map<String, Pair<BigDecimal, BigDecimal>> ratesMap = exchangeApi.getRates();

        return ReportDto.builder()
                .fileName(String.format("input_output_summary_%s", LocalDateTime.now().format(FORMATTER_FOR_NAME)))
                .content(ReportTwoExcelGeneratorUtil.generate(
                        new TreeMap<>(inOutMap),
                        ratesMap))
                .build();
    }

    @Override
    public ReportDto getDifferenceBetweenBalancesReports(LocalDateTime startTime,
                                                         LocalDateTime endTime,
                                                         List<UserRole> roles) throws Exception {
        Preconditions.checkArgument(!roles.isEmpty(), "At least one role must be specified");

        //first balance
        ReportDto firstBalancesReport = reportDao.getBalancesReportByTime(startTime.minusMinutes(TIME_RANGE), startTime.plusMinutes(TIME_RANGE));
        if (isNull(firstBalancesReport)) {
            throw new Exception(String.format("No balances report object found for time: %s", startTime.toString()));
        }
        byte[] zippedBytes = firstBalancesReport.getContent();
        final LocalDateTime firstCreatedAt = firstBalancesReport.getCreatedAt();

        Map<String, WalletBalancesDto> firstBalancesMap = getWalletBalances(zippedBytes);

        //second balance
        ReportDto secondBalancesReport = reportDao.getBalancesReportByTime(endTime.minusMinutes(TIME_RANGE), endTime.plusMinutes(TIME_RANGE));
        if (isNull(secondBalancesReport)) {
            throw new Exception(String.format("No balances report object found for time: %s", endTime.toString()));
        }
        zippedBytes = secondBalancesReport.getContent();
        final LocalDateTime secondCreatedAt = secondBalancesReport.getCreatedAt();

        Map<String, WalletBalancesDto> secondBalancesMap = getWalletBalances(zippedBytes);

        final Map<String, Pair<BigDecimal, BigDecimal>> ratesMap = exchangeApi.getRates();

        return ReportDto.builder()
                .fileName(String.format("report_difference_between_balances_%s", LocalDateTime.now().format(FORMATTER_FOR_NAME)))
                .content(ReportFiveExcelGeneratorUtil.generate(
                        new TreeMap<>(firstBalancesMap),
                        firstCreatedAt,
                        new TreeMap<>(secondBalancesMap),
                        secondCreatedAt,
                        roles,
                        ratesMap))
                .build();
    }

    @Override
    public ReportDto getDifferenceBetweenBalancesReportsWithInOut(LocalDateTime startTime,
                                                                  LocalDateTime endTime,
                                                                  List<UserRole> roles) throws Exception {
        //first balance
        ReportDto firstBalancesReport = reportDao.getBalancesReportByTime(startTime.minusMinutes(TIME_RANGE), startTime.plusMinutes(TIME_RANGE));
        if (isNull(firstBalancesReport)) {
            throw new Exception(String.format("No balances report object found for time: %s", startTime.toString()));
        }
        byte[] zippedBytes = firstBalancesReport.getContent();
        final LocalDateTime firstCreatedAt = firstBalancesReport.getCreatedAt();

        Map<String, WalletBalancesDto> firstBalancesMap = getWalletBalances(zippedBytes);

        //second balance
        ReportDto secondBalancesReport = reportDao.getBalancesReportByTime(endTime.minusMinutes(TIME_RANGE), endTime.plusMinutes(TIME_RANGE));
        if (isNull(secondBalancesReport)) {
            throw new Exception(String.format("No balances report object found for time: %s", endTime.toString()));
        }
        zippedBytes = secondBalancesReport.getContent();
        final LocalDateTime secondCreatedAt = secondBalancesReport.getCreatedAt();

        Map<String, WalletBalancesDto> secondBalancesMap = getWalletBalances(zippedBytes);

        List<InOutReportDto> inOutSummary = transactionService.getInOutSummaryByPeriodAndRoles(startTime, endTime, roles);
        if (isEmpty(inOutSummary)) {
            throw new Exception(String.format("No input/output information found for period: [%s, %s] and user roles: [%s]",
                    startTime.toString(),
                    endTime.toString(),
                    roles.stream().map(Enum::name).collect(joining(", "))));
        }
        final Map<String, InOutReportDto> inOutMap = inOutSummary.stream().collect(toMap(InOutReportDto::getCurrencyName, Function.identity()));

        final Map<String, Pair<BigDecimal, BigDecimal>> ratesMap = exchangeApi.getRates();

        return ReportDto.builder()
                .fileName(String.format("report_imbalance_of_coins_%s", LocalDateTime.now().format(FORMATTER_FOR_NAME)))
                .content(ReportSixExcelGeneratorUtil.generate(
                        new TreeMap<>(firstBalancesMap),
                        firstCreatedAt,
                        new TreeMap<>(secondBalancesMap),
                        secondCreatedAt,
                        roles,
                        inOutMap,
                        ratesMap))
                .build();
    }

    private Map<String, WalletBalancesDto> getWalletBalances(byte[] zippedBytes) throws Exception {
        try {
            byte[] balancesBytes = ZipUtil.unzip(zippedBytes);

            List<WalletBalancesDto> balances = objectMapper.readValue(balancesBytes, new TypeReference<List<WalletBalancesDto>>() {
            });
            return balances.stream().collect(toMap(WalletBalancesDto::getCurrencyName, Function.identity()));
        } catch (IOException | DataFormatException ex) {
            throw new Exception("Problem with read balances object from byte array", ex);
        }
    }

    private Map<String, InOutReportDto> getInOutSummary(byte[] zippedBytes) throws Exception {
        try {
            byte[] inOutBytes = ZipUtil.unzip(zippedBytes);

            List<InOutReportDto> inOut = objectMapper.readValue(inOutBytes, new TypeReference<List<InOutReportDto>>() {
            });
            return inOut.stream().collect(toMap(InOutReportDto::getCurrencyName, Function.identity()));
        } catch (IOException | DataFormatException ex) {
            throw new Exception("Problem with read in/out object from byte array", ex);
        }
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
