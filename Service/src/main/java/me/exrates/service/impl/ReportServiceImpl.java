package me.exrates.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.ReportDao;
import me.exrates.model.Currency;
import me.exrates.model.dto.BalancesDto;
import me.exrates.model.dto.CurrencyPairTurnoverReportDto;
import me.exrates.model.dto.CurrencyRateDto;
import me.exrates.model.dto.ExternalWalletBalancesDto;
import me.exrates.model.dto.InOutReportDto;
import me.exrates.model.dto.InternalWalletBalancesDto;
import me.exrates.model.dto.InvoiceReportDto;
import me.exrates.model.dto.OperationViewDto;
import me.exrates.model.dto.RefillRequestFlatForReportDto;
import me.exrates.model.dto.ReportDto;
import me.exrates.model.dto.UserRoleTotalBalancesReportDto;
import me.exrates.model.dto.UserSummaryDto;
import me.exrates.model.dto.UserSummaryOrdersDto;
import me.exrates.model.dto.WalletBalancesDto;
import me.exrates.model.dto.WithdrawRequestFlatForReportDto;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.AdminTransactionsFilterData;
import me.exrates.model.enums.ReportGroupUserRole;
import me.exrates.model.enums.UserRole;
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
import me.exrates.service.util.ReportEightExcelGeneratorUtil;
import me.exrates.service.util.ReportFiveExcelGeneratorUtil;
import me.exrates.service.util.ReportFourExcelGeneratorUtil;
import me.exrates.service.util.ReportNineExcelGeneratorUtil;
import me.exrates.service.util.ReportSevenExcelGeneratorUtil;
import me.exrates.service.util.ReportSixExcelGeneratorUtil;
import me.exrates.service.util.ReportThreeExcelGeneratorUtil;
import me.exrates.service.util.ReportTwoExcelGeneratorUtil;
import me.exrates.service.util.ZipUtil;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.zip.DataFormatException;

import static java.util.Comparator.comparing;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static me.exrates.service.util.CollectionUtil.isEmpty;
import static me.exrates.service.util.CollectionUtil.isNotEmpty;

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
    public void generateWalletBalancesReportObject() {
        StopWatch stopWatch = StopWatch.createStarted();
        log.info("Process of generating report object as byte array start...");

        List<Currency> currencies = currencyService.findAllCurrenciesWithHidden();

        final Map<String, ExternalWalletBalancesDto> externalWalletBalances = walletService.getExternalWalletBalances().stream()
                .collect(toMap(
                        ExternalWalletBalancesDto::getCurrencyName,
                        Function.identity()));
        final Map<String, List<InternalWalletBalancesDto>> internalWalletBalances = walletService.getInternalWalletBalances().stream()
                .collect(groupingBy(InternalWalletBalancesDto::getCurrencyName));

        final Map<String, CurrencyRateDto> ratesMap = exchangeApi.getRates().entrySet().stream()
                .collect(toMap(
                        Map.Entry::getKey,
                        entry -> new CurrencyRateDto(entry.getValue().getLeft(), entry.getValue().getRight())
                ));

        List<WalletBalancesDto> balances = currencies.stream()
                .map(currency ->
                        currency.isHidden()
                                ? WalletBalancesDto.buildForHiddenCurrency(currency.getId(), currency.getName())
                                : WalletBalancesDto.builder()
                                .currencyName(currency.getName())
                                .external(externalWalletBalances.get(currency.getName()))
                                .internals(internalWalletBalances.get(currency.getName()))
                                .rate(ratesMap.get(currency.getName()))
                                .build())
                .filter(walletBalancesDto -> nonNull(walletBalancesDto.getExternal())
                        && nonNull(walletBalancesDto.getInternals())
                        && nonNull(walletBalancesDto.getRate()))
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
        List<Currency> currencies = currencyService.getAllCurrencies();

        final Map<String, ExternalWalletBalancesDto> externalWalletBalances = walletService.getExternalWalletBalances().stream()
                .collect(toMap(
                        ExternalWalletBalancesDto::getCurrencyName,
                        Function.identity()));
        final Map<String, List<InternalWalletBalancesDto>> internalWalletBalances = walletService.getInternalWalletBalances().stream()
                .collect(groupingBy(InternalWalletBalancesDto::getCurrencyName));

        final Map<String, Pair<BigDecimal, BigDecimal>> rates = exchangeApi.getRates();

        final LocalDateTime lastUpdated = LocalDateTime.now().withNano(0);

        return currencies.stream()
                .map(currency -> {
                    final int currencyId = currency.getId();
                    final String currencyName = currency.getName();

                    Pair<BigDecimal, BigDecimal> ratePair = rates.get(currencyName);
                    if (isNull(ratePair)) {
                        ratePair = Pair.of(BigDecimal.ZERO, BigDecimal.ZERO);
                    }

                    final BigDecimal usdRate = ratePair.getLeft();
                    final BigDecimal btcRate = ratePair.getRight();

                    ExternalWalletBalancesDto extWalletBalance = externalWalletBalances.get(currencyName);
                    List<InternalWalletBalancesDto> intWalletBalances = internalWalletBalances.get(currencyName);

                    if (isNull(extWalletBalance) || isEmpty(intWalletBalances)) {
                        return null;
                    }

                    final boolean signOfCertainty = extWalletBalance.isSignOfCertainty();

                    final BigDecimal externalTotalBalance = extWalletBalance.getTotalBalance();
                    final BigDecimal externalTotalBalanceUSD = externalTotalBalance.multiply(usdRate);
                    final BigDecimal externalTotalBalanceBTC = externalTotalBalance.multiply(btcRate);

                    final BigDecimal internalTotalBalance = intWalletBalances.stream()
                            .filter(inWallet -> inWallet.getRoleName() != UserRole.BOT_TRADER)
                            .map(InternalWalletBalancesDto::getTotalBalance)
                            .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
                    final BigDecimal internalTotalBalanceUSD = internalTotalBalance.multiply(usdRate);
                    final BigDecimal internalTotalBalanceBTC = internalTotalBalance.multiply(btcRate);

                    final BigDecimal deviation = signOfCertainty ? externalTotalBalance.subtract(internalTotalBalance) : BigDecimal.ZERO;
                    final BigDecimal deviationUSD = deviation.multiply(usdRate);
                    final BigDecimal deviationBTC = deviation.multiply(btcRate);

                    return BalancesDto.builder()
                            .currencyId(currencyId)
                            .currencyName(currencyName)
                            .signOfCertainty(signOfCertainty)
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

    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    @Override
    public List<ReportDto> getArchiveBalancesReports(LocalDate date) {
        return reportDao.getBalancesReportsNames(date.atTime(LocalTime.MIN), date.atTime(LocalTime.MAX));
    }

    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    @Override
    public ReportDto getArchiveBalancesReportFile(Integer id) throws Exception {
        final List<Currency> currencies = currencyService.getAllCurrencies();

        ReportDto balancesReport = reportDao.getBalancesReportById(id);
        final byte[] zippedBytes = balancesReport.getContent();
        final LocalDateTime createdAt = balancesReport.getCreatedAt();

        Map<String, WalletBalancesDto> balancesMap = getWalletBalances(zippedBytes);

        return balancesReport.toBuilder()
                .content(ReportFourExcelGeneratorUtil.generate(
                        currencies,
                        balancesMap,
                        createdAt))
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

    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    @Override
    public List<ReportDto> getArchiveInputOutputReports(LocalDate date) {
        return reportDao.getInOutReportsNames(date.atTime(LocalTime.MIN), date.atTime(LocalTime.MAX));
    }

    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    @Override
    public ReportDto getArchiveInputOutputReportFile(Integer id) throws Exception {
        final List<Currency> currencies = currencyService.getAllCurrencies();

        ReportDto inOutReport = reportDao.getInOutReportById(id);
        final byte[] zippedBytes = inOutReport.getContent();

        Map<String, InOutReportDto> inOutMap = getInOutSummary(zippedBytes);

        final Map<String, Pair<BigDecimal, BigDecimal>> ratesMap = exchangeApi.getRates();

        return inOutReport.toBuilder()
                .content(ReportTwoExcelGeneratorUtil.generate(
                        currencies,
                        inOutMap,
                        ratesMap))
                .build();
    }

    @Override
    public ReportDto getInputOutputSummaryReport(LocalDateTime startTime,
                                                 LocalDateTime endTime,
                                                 List<UserRole> roles) throws Exception {
        Preconditions.checkArgument(!roles.isEmpty(), "At least one role must be specified");

        final List<Currency> currencies = currencyService.getAllCurrencies();

        List<InOutReportDto> inOutSummary = transactionService.getInOutSummaryByPeriodAndRoles(startTime, endTime, roles);
        if (isEmpty(inOutSummary)) {
            throw new Exception(String.format("No input/output information found for period: [%s, %s] and user roles: [%s]",
                    startTime.toString(),
                    endTime.toString(),
                    roles.stream().map(UserRole::getName).collect(joining(", "))));
        }
        final Map<String, InOutReportDto> inOutMap = inOutSummary.stream().collect(toMap(InOutReportDto::getCurrencyName, Function.identity()));

        final Map<String, Pair<BigDecimal, BigDecimal>> ratesMap = exchangeApi.getRates();

        return ReportDto.builder()
                .fileName(String.format("input_output_summary_%s-%s", startTime.format(FORMATTER_FOR_NAME), endTime.format(FORMATTER_FOR_NAME)))
                .content(ReportTwoExcelGeneratorUtil.generate(
                        currencies,
                        inOutMap,
                        ratesMap))
                .build();
    }

    @Override
    public ReportDto getDifferenceBetweenBalancesReports(LocalDateTime startTime,
                                                         LocalDateTime endTime,
                                                         List<UserRole> roles) throws Exception {
        Preconditions.checkArgument(!roles.isEmpty(), "At least one role must be specified");

        final List<Currency> currencies = currencyService.getAllCurrencies();

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
                .fileName(String.format("report_difference_between_balances_%s-%s", startTime.format(FORMATTER_FOR_NAME), endTime.format(FORMATTER_FOR_NAME)))
                .content(ReportFiveExcelGeneratorUtil.generate(
                        currencies,
                        firstBalancesMap,
                        firstCreatedAt,
                        secondBalancesMap,
                        secondCreatedAt,
                        roles,
                        ratesMap))
                .build();
    }

    @Override
    public ReportDto getDifferenceBetweenBalancesReportsWithInOut(LocalDateTime startTime,
                                                                  LocalDateTime endTime,
                                                                  List<UserRole> roles) throws Exception {
        Preconditions.checkArgument(!roles.isEmpty(), "At least one role must be specified");

        final List<Currency> currencies = currencyService.getAllCurrencies();

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
                    roles.stream().map(UserRole::getName).collect(joining(", "))));
        }
        final Map<String, InOutReportDto> inOutMap = inOutSummary.stream().collect(toMap(InOutReportDto::getCurrencyName, Function.identity()));

        final Map<String, Pair<BigDecimal, BigDecimal>> ratesMap = exchangeApi.getRates();

        return ReportDto.builder()
                .fileName(String.format("report_imbalance_of_coins_%s-%s", startTime.format(FORMATTER_FOR_NAME), endTime.format(FORMATTER_FOR_NAME)))
                .content(ReportSixExcelGeneratorUtil.generate(
                        currencies,
                        firstBalancesMap,
                        firstCreatedAt,
                        secondBalancesMap,
                        secondCreatedAt,
                        roles,
                        inOutMap,
                        ratesMap))
                .build();
    }

    @Override
    public ReportDto getUsersWalletSummaryData(LocalDateTime startTime,
                                               LocalDateTime endTime,
                                               String userEmail,
                                               String requesterEmail) throws Exception {
        final int requesterId = userService.getIdByEmail(requesterEmail);

        List<UserSummaryDto> summaryData = transactionService.getUsersWalletSummaryData(startTime, endTime, userEmail, requesterId);
        if (isEmpty(summaryData)) {
            throw new Exception(String.format("No user wallet information found for period: [%s, %s] and user email: [%s]",
                    startTime.toString(),
                    endTime.toString(),
                    userEmail));
        }

        return ReportDto.builder()
                .fileName(String.format("report_user_wallet_summary_data_%s-%s", startTime.format(FORMATTER_FOR_NAME), endTime.format(FORMATTER_FOR_NAME)))
                .content(ReportNineExcelGeneratorUtil.generate(summaryData))
                .build();
    }

    @Override
    public ReportDto getUserSummaryOrdersData(LocalDateTime startTime,
                                              LocalDateTime endTime,
                                              List<UserRole> roles,
                                              String requesterEmail) throws Exception {
        final int requesterId = userService.getIdByEmail(requesterEmail);

        Map<String, List<UserSummaryOrdersDto>> summaryOrdersData = orderService.getUserSummaryOrdersData(startTime, endTime, roles, requesterId);

        if (summaryOrdersData.values().stream().mapToLong(List::size).sum() <= 0) {
            throw new Exception(String.format("No user orders information found for period: [%s, %s] and user roles: [%s]",
                    startTime.toString(),
                    endTime.toString(),
                    roles.stream().map(UserRole::getName).collect(joining(", "))));
        }

        final Map<String, Pair<BigDecimal, BigDecimal>> ratesMap = exchangeApi.getRates();

        return ReportDto.builder()
                .fileName(String.format("report_user_orders_summary_data_%s-%s", startTime.format(FORMATTER_FOR_NAME), endTime.format(FORMATTER_FOR_NAME)))
                .content(ReportEightExcelGeneratorUtil.generate(
                        summaryOrdersData,
                        ratesMap))
                .build();
    }

    @Override
    public ReportDto getInvoiceReport(LocalDateTime startTime,
                                      LocalDateTime endTime,
                                      List<UserRole> roles,
                                      String requesterEmail) throws Exception {
        final int requesterId = userService.getIdByEmail(requesterEmail);

        List<InvoiceReportDto> invoiceReportData = new ArrayList<>();

        List<WithdrawRequestFlatForReportDto> withdrawRequests = withdrawService.findAllByPeriodAndRoles(startTime, endTime, roles, requesterId);
        if (isNotEmpty(withdrawRequests)) {
            invoiceReportData.addAll(withdrawRequests.stream().map(InvoiceReportDto::new).collect(toList()));
        }
        List<RefillRequestFlatForReportDto> refillRequests = refillService.findAllByPeriodAndRoles(startTime, endTime, roles, requesterId);
        if (isNotEmpty(refillRequests)) {
            invoiceReportData.addAll(refillRequests.stream().map(InvoiceReportDto::new).collect(toList()));
        }

        if (isEmpty(invoiceReportData)) {
            throw new Exception(String.format("No input/output information found for period: [%s, %s] and user roles: [%s]",
                    startTime.toString(),
                    endTime.toString(),
                    roles.stream().map(UserRole::getName).collect(joining(", "))));
        }

        final Map<String, Pair<BigDecimal, BigDecimal>> ratesMap = exchangeApi.getRates();

        return ReportDto.builder()
                .fileName(String.format("report_input_output_data_%s-%s", startTime.format(FORMATTER_FOR_NAME), endTime.format(FORMATTER_FOR_NAME)))
                .content(ReportSevenExcelGeneratorUtil.generate(
                        invoiceReportData.stream()
                                .filter(invoiceReportDto -> !invoiceReportDto.isEmpty())
                                .sorted(comparing(InvoiceReportDto::getCreationDate))
                                .collect(toList()),
                        ratesMap))
                .build();
    }

    @Override
    public ReportDto getCurrenciesTurnover(LocalDateTime startTime,
                                           LocalDateTime endTime,
                                           List<UserRole> roles) throws Exception {
        Preconditions.checkArgument(!roles.isEmpty(), "At least one role must be specified");

        List<CurrencyPairTurnoverReportDto> currencyPairsTurnover = orderService.getCurrencyPairTurnoverByPeriodAndRoles(startTime, endTime, roles);
        if (isEmpty(currencyPairsTurnover)) {
            throw new Exception(String.format("No currency turnover information found for period: [%s, %s] and user roles: [%s]",
                    startTime.toString(),
                    endTime.toString(),
                    roles.stream().map(UserRole::getName).collect(joining(", "))));
        }

        final Map<String, Pair<BigDecimal, BigDecimal>> ratesMap = exchangeApi.getRates();

        return ReportDto.builder()
                .fileName(String.format("currency_pairs_turnover_%s-%s", startTime.format(FORMATTER_FOR_NAME), endTime.format(FORMATTER_FOR_NAME)))
                .content(ReportThreeExcelGeneratorUtil.generate(currencyPairsTurnover, ratesMap))
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
