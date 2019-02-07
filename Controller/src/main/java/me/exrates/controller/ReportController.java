package me.exrates.controller;

import com.google.common.io.ByteSource;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.*;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.AdminOrderFilterData;
import me.exrates.model.dto.filterData.AdminStopOrderFilterData;
import me.exrates.model.dto.filterData.AdminTransactionsFilterData;
import me.exrates.model.enums.ReportGroupUserRole;
import me.exrates.model.enums.UserRole;
import me.exrates.service.OrderService;
import me.exrates.service.ReportService;
import me.exrates.service.stopOrder.StopOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@Log4j2
public class ReportController {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ReportService reportService;

    @Autowired
    private LocaleResolver localeResolver;

    private final static String DATE_TIME_PATTERN = "yyyy-MM-dd_HH:mm";

    @RequestMapping(value = "/2a8fy7b07dxe44/report/downloadTransactions")
    @ResponseBody
    public String getUserTransactions(
            @RequestParam Integer id,
            AdminTransactionsFilterData filterData,
            Principal principal,
            HttpServletResponse response) throws IOException {
        filterData.initFilterItems();
        List<OperationViewDto> list = reportService.getTransactionsHistory(
                principal.getName(),
                id,
                filterData);
        String value = list.stream()
                .map(OperationViewDto::toString)
                .collect(Collectors.joining());
        return value;
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/generalStats/groupTotalBalances", method = GET)
    public List<UserRoleTotalBalancesReportDto<ReportGroupUserRole>> getTotalBalancesReportByGroups() {

        return reportService.getWalletBalancesSummaryByGroups();
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/generalStats/balancesSliceStatistic", method = GET)
    public List<BalancesDto> getBalancesExternalWallets() {

        return reportService.getBalancesSliceStatistic();
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/generalStats/mail/time", method = GET)
    public String getMailingTime() {
        return reportService.retrieveReportMailingTime();
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/generalStats/mail/status", method = GET)
    public Boolean getMailingStatus() {
        return reportService.isReportMailingEnabled();
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/generalStats/mail/emails", method = GET)
    public List<List<String>> getReportSubscriberEmails() {
        return reportService.retrieveReportSubscribersList(false).stream().map(Collections::singletonList).collect(Collectors.toList());
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/generalStats/mail/time/update", method = POST)
    public void updateMailingTime(@RequestParam String newTime) {
        reportService.updateReportMailingTime(newTime);
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/generalStats/mail/status/update", method = POST)
    public void updateMailingStatus(@RequestParam Boolean newStatus) {
        reportService.setReportMailingStatus(newStatus);
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/generalStats/mail/emails/add", method = POST)
    public void addSubscriber(@RequestParam String email) {
        reportService.addReportSubscriber(email);
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/generalStats/mail/emails/delete", method = POST)
    public void deleteSubscriber(@RequestParam String email) {
        reportService.deleteReportSubscriber(email);
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/generalStats/archiveBalancesReports", method = GET)
    public ResponseEntity<List<ReportDto>> getArchiveBalancesReports(@RequestParam("date") String dateString) {
        String dateTimePattern = "yyyy-MM-dd_HH:mm";
        final LocalDate date = LocalDateTime.from(DateTimeFormatter.ofPattern(dateTimePattern).parse(dateString)).toLocalDate();

        return ResponseEntity.ok(reportService.getArchiveBalancesReports(date));
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/generalStats/archiveBalancesReport/{id}", method = GET)
    public ResponseEntity getArchiveBalancesReportFile(@PathVariable Integer id,
                                                       Locale locale) {
        ReportDto reportDto;
        try {
            reportDto = reportService.getArchiveBalancesReportFile(id);
        } catch (Exception ex) {
            log.error("Downloaded file is corrupted");
            return new ResponseEntity<>(messageSource.getMessage("reports.error", null, locale), HttpStatus.NO_CONTENT);
        }
        final byte[] content = reportDto.getContent();
        final String fileName = reportDto.getFileName();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentLength(content.length);
        headers.setContentDispositionFormData("attachment", fileName);

        try {
            InputStreamResource isr = new InputStreamResource(ByteSource.wrap(content).openStream());
            return new ResponseEntity<>(isr, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error("Downloaded file is corrupted");
            return new ResponseEntity<>(messageSource.getMessage("reports.error", null, locale), HttpStatus.NO_CONTENT);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/generalStats/archiveInOutReports", method = GET)
    public ResponseEntity<List<ReportDto>> getArchiveInputOutputReports(@RequestParam("date") String dateString) {
        String dateTimePattern = "yyyy-MM-dd_HH:mm";
        final LocalDate date = LocalDateTime.from(DateTimeFormatter.ofPattern(dateTimePattern).parse(dateString)).toLocalDate();

        return ResponseEntity.ok(reportService.getArchiveInputOutputReports(date));
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/generalStats/archiveInOutReport/{id}", method = GET)
    public ResponseEntity getArchiveInputOutputReportFile(@PathVariable Integer id,
                                                          Locale locale) {
        ReportDto reportDto;
        try {
            reportDto = reportService.getArchiveInputOutputReportFile(id);
        } catch (Exception ex) {
            log.error("Downloaded file is corrupted");
            return new ResponseEntity<>(messageSource.getMessage("reports.error", null, locale), HttpStatus.NO_CONTENT);
        }
        final byte[] content = reportDto.getContent();
        final String fileName = reportDto.getFileName();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentLength(content.length);
        headers.setContentDispositionFormData("attachment", fileName);

        try {
            InputStreamResource isr = new InputStreamResource(ByteSource.wrap(content).openStream());
            return new ResponseEntity<>(isr, headers, HttpStatus.OK);
        } catch (IOException e) {
            log.error("Downloaded file is corrupted");
            return new ResponseEntity<>(messageSource.getMessage("reports.error", null, locale), HttpStatus.NO_CONTENT);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/generalStats/archiveInOutSummaryReportForPeriod", method = GET)
    public ResponseEntity getInputOutputSummaryForPeriod(@RequestParam("startTime") String startTimeString,
                                                         @RequestParam("endTime") String endTimeString,
                                                         @RequestParam("roles") List<UserRole> userRoles,
                                                         Locale locale) {
        LocalDateTime startTime = LocalDateTime.from(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN).parse(startTimeString));
        LocalDateTime endTime = LocalDateTime.from(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN).parse(endTimeString));

        ReportDto reportDto;
        try {
            reportDto = reportService.getInputOutputSummaryReport(startTime, endTime, userRoles);
        } catch (Exception ex) {
            log.error("Downloaded file is corrupted");
            return new ResponseEntity<>(messageSource.getMessage("reports.error", null, locale), HttpStatus.NO_CONTENT);
        }
        final byte[] content = reportDto.getContent();
        final String fileName = reportDto.getFileName();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentLength(content.length);
        headers.setContentDispositionFormData("attachment", fileName);

        try {
            InputStreamResource isr = new InputStreamResource(ByteSource.wrap(content).openStream());
            return new ResponseEntity<>(isr, headers, HttpStatus.OK);
        } catch (IOException ex) {
            log.error("Downloaded file is corrupted");
            return new ResponseEntity<>(messageSource.getMessage("reports.error", null, locale), HttpStatus.NO_CONTENT);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/generalStats/archiveBalancesReportForPeriod", method = GET)
    public ResponseEntity getDifferenceBetweenBalancesReportsForPeriod(@RequestParam("startTime") String startTimeString,
                                                                       @RequestParam("endTime") String endTimeString,
                                                                       @RequestParam("roles") List<UserRole> userRoles,
                                                                       Locale locale) {
        LocalDateTime startTime = LocalDateTime.from(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN).parse(startTimeString));
        LocalDateTime endTime = LocalDateTime.from(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN).parse(endTimeString));

        ReportDto reportDto;
        try {
            reportDto = reportService.getDifferenceBetweenBalancesReports(startTime, endTime, userRoles);
        } catch (Exception ex) {
            log.error("Downloaded file is corrupted");
            return new ResponseEntity<>(messageSource.getMessage("reports.error", null, locale), HttpStatus.NO_CONTENT);
        }
        final byte[] content = reportDto.getContent();
        final String fileName = reportDto.getFileName();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentLength(content.length);
        headers.setContentDispositionFormData("attachment", fileName);

        try {
            InputStreamResource isr = new InputStreamResource(ByteSource.wrap(content).openStream());
            return new ResponseEntity<>(isr, headers, HttpStatus.OK);
        } catch (IOException ex) {
            log.error("Downloaded file is corrupted");
            return new ResponseEntity<>(messageSource.getMessage("reports.error", null, locale), HttpStatus.NO_CONTENT);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/generalStats/archiveBalancesReportForPeriodWithInOut", method = GET)
    public ResponseEntity getDifferenceBetweenBalancesReportsForPeriodWithInOut(@RequestParam("startTime") String startTimeString,
                                                                                @RequestParam("endTime") String endTimeString,
                                                                                @RequestParam("roles") List<UserRole> userRoles,
                                                                                Locale locale) {
        LocalDateTime startTime = LocalDateTime.from(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN).parse(startTimeString));
        LocalDateTime endTime = LocalDateTime.from(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN).parse(endTimeString));

        ReportDto reportDto;
        try {
            reportDto = reportService.getDifferenceBetweenBalancesReportsWithInOut(startTime, endTime, userRoles);
        } catch (Exception ex) {
            log.error("Downloaded file is corrupted");
            return new ResponseEntity<>(messageSource.getMessage("reports.error", null, locale), HttpStatus.NO_CONTENT);
        }
        final byte[] content = reportDto.getContent();
        final String fileName = reportDto.getFileName();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentLength(content.length);
        headers.setContentDispositionFormData("attachment", fileName);

        try {
            InputStreamResource isr = new InputStreamResource(ByteSource.wrap(content).openStream());
            return new ResponseEntity<>(isr, headers, HttpStatus.OK);
        } catch (IOException ex) {
            log.error("Downloaded file is corrupted");
            return new ResponseEntity<>(messageSource.getMessage("reports.error", null, locale), HttpStatus.NO_CONTENT);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/report/usersWalletsSummary", method = GET)
    public ResponseEntity getUsersWalletsSummaryData(@RequestParam("startTime") String startTimeString,
                                                     @RequestParam("endTime") String endTimeString,
                                                     @RequestParam("userEmail") String userEmail,
                                                     Principal principal,
                                                     Locale locale) {
        LocalDateTime startTime = LocalDateTime.from(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN).parse(startTimeString));
        LocalDateTime endTime = LocalDateTime.from(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN).parse(endTimeString));

        ReportDto reportDto;
        try {
            reportDto = reportService.getUsersWalletSummaryData(startTime, endTime, userEmail, principal.getName());
        } catch (Exception ex) {
            log.error("Downloaded file is corrupted");
            return new ResponseEntity<>(messageSource.getMessage("reports.error", null, locale), HttpStatus.NO_CONTENT);
        }
        final byte[] content = reportDto.getContent();
        final String fileName = reportDto.getFileName();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentLength(content.length);
        headers.setContentDispositionFormData("attachment", fileName);

        try {
            InputStreamResource isr = new InputStreamResource(ByteSource.wrap(content).openStream());
            return new ResponseEntity<>(isr, headers, HttpStatus.OK);
        } catch (IOException ex) {
            log.error("Downloaded file is corrupted");
            return new ResponseEntity<>(messageSource.getMessage("reports.error", null, locale), HttpStatus.NO_CONTENT);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/report/userSummaryOrders", method = GET)
    public ResponseEntity getUserSummaryOrdersData(@RequestParam("startTime") String startTimeString,
                                                   @RequestParam("endTime") String endTimeString,
                                                   @RequestParam("roles") List<UserRole> userRoles,
                                                   Principal principal,
                                                   Locale locale) {
        LocalDateTime startTime = LocalDateTime.from(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN).parse(startTimeString));
        LocalDateTime endTime = LocalDateTime.from(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN).parse(endTimeString));

        ReportDto reportDto;
        try {
            reportDto = reportService.getUserSummaryOrdersData(startTime, endTime, userRoles, principal.getName());
        } catch (Exception ex) {
            log.error("Downloaded file is corrupted");
            return new ResponseEntity<>(messageSource.getMessage("reports.error", null, locale), HttpStatus.NO_CONTENT);
        }
        final byte[] content = reportDto.getContent();
        final String fileName = reportDto.getFileName();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentLength(content.length);
        headers.setContentDispositionFormData("attachment", fileName);

        try {
            InputStreamResource isr = new InputStreamResource(ByteSource.wrap(content).openStream());
            return new ResponseEntity<>(isr, headers, HttpStatus.OK);
        } catch (IOException ex) {
            log.error("Downloaded file is corrupted");
            return new ResponseEntity<>(messageSource.getMessage("reports.error", null, locale), HttpStatus.NO_CONTENT);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/report/inputOutputSummary", method = GET)
    public ResponseEntity getUsersWalletsSummeryTotalInOut(@RequestParam("startTime") String startTimeString,
                                                           @RequestParam("endTime") String endTimeString,
                                                           @RequestParam("roles") List<UserRole> userRoles,
                                                           Principal principal,
                                                           Locale locale) {
        LocalDateTime startTime = LocalDateTime.from(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN).parse(startTimeString));
        LocalDateTime endTime = LocalDateTime.from(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN).parse(endTimeString));

        ReportDto reportDto;
        try {
            reportDto = reportService.getInvoiceReport(startTime, endTime, userRoles, principal.getName());
        } catch (Exception ex) {
            log.error("Downloaded file is corrupted");
            return new ResponseEntity<>(messageSource.getMessage("reports.error", null, locale), HttpStatus.NO_CONTENT);
        }
        final byte[] content = reportDto.getContent();
        final String fileName = reportDto.getFileName();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentLength(content.length);
        headers.setContentDispositionFormData("attachment", fileName);

        try {
            InputStreamResource isr = new InputStreamResource(ByteSource.wrap(content).openStream());
            return new ResponseEntity<>(isr, headers, HttpStatus.OK);
        } catch (IOException ex) {
            log.error("Downloaded file is corrupted");
            return new ResponseEntity<>(messageSource.getMessage("reports.error", null, locale), HttpStatus.NO_CONTENT);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/generalStats/currencyPairTurnover", method = GET)
    public ResponseEntity getCurrenciesTurnover(@RequestParam("startTime") String startTimeString,
                                                @RequestParam("endTime") String endTimeString,
                                                @RequestParam("roles") List<UserRole> userRoles,
                                                Locale locale) {
        LocalDateTime startTime = LocalDateTime.from(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN).parse(startTimeString));
        LocalDateTime endTime = LocalDateTime.from(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN).parse(endTimeString));

        ReportDto reportDto;
        try {
            reportDto = reportService.getCurrenciesTurnover(startTime, endTime, userRoles);
        } catch (Exception ex) {
            log.error("Downloaded file is corrupted");
            return new ResponseEntity<>(messageSource.getMessage("reports.error", null, locale), HttpStatus.NO_CONTENT);
        }
        final byte[] content = reportDto.getContent();
        final String fileName = reportDto.getFileName();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentLength(content.length);
        headers.setContentDispositionFormData("attachment", fileName);

        try {
            InputStreamResource isr = new InputStreamResource(ByteSource.wrap(content).openStream());
            return new ResponseEntity<>(isr, headers, HttpStatus.OK);
        } catch (IOException ex) {
            log.error("Downloaded file is corrupted");
            return new ResponseEntity<>(messageSource.getMessage("reports.error", null, locale), HttpStatus.NO_CONTENT);
        }
    }

    @ResponseBody
    @PostMapping(value = "/2a8fy7b07dxe44/report/orders")
    public ResponseEntity getOrders(AdminOrderFilterData adminOrderFilterData, HttpServletRequest request) {
        ReportDto reportDto;
        try {
            reportDto = reportService.getOrders(adminOrderFilterData);
        } catch (Exception ex) {
            log.error("Downloaded file is corrupted");
            return new ResponseEntity<>(messageSource.getMessage("reports.error", null, localeResolver.resolveLocale(request)), HttpStatus.NO_CONTENT);
        }
        final byte[] content = reportDto.getContent();
        final String fileName = reportDto.getFileName();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentLength(content.length);
        headers.setContentDispositionFormData("attachment", fileName);

        try {
            InputStreamResource isr = new InputStreamResource(ByteSource.wrap(content).openStream());
            return new ResponseEntity<>(isr, headers, HttpStatus.OK);
        } catch (IOException ex) {
            log.error("Downloaded file is corrupted");
            return new ResponseEntity<>(messageSource.getMessage("reports.error", null, localeResolver.resolveLocale(request)), HttpStatus.NO_CONTENT);
        }
    }

    @ResponseBody
    @PostMapping(value = "/2a8fy7b07dxe44/report/coin")
    public ResponseEntity getStatsByCoin(int currencyId, HttpServletRequest request) {
        ReportDto reportDto = null;
        try {
//            reportDto = reportService.getOrders(adminOrderFilterData);
        } catch (Exception ex) {
            log.error("Downloaded file is corrupted");
            return new ResponseEntity<>(messageSource.getMessage("reports.error", null, localeResolver.resolveLocale(request)), HttpStatus.NO_CONTENT);
        }
        final byte[] content = null;
        final String fileName = reportDto.getFileName();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentLength(content.length);
        headers.setContentDispositionFormData("attachment", fileName);

        try {
            InputStreamResource isr = new InputStreamResource(ByteSource.wrap(content).openStream());
            return new ResponseEntity<>(isr, headers, HttpStatus.OK);
        } catch (IOException ex) {
            log.error("Downloaded file is corrupted");
            return new ResponseEntity<>(messageSource.getMessage("reports.error", null, localeResolver.resolveLocale(request)), HttpStatus.NO_CONTENT);
        }
    }
}