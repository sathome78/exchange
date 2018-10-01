package me.exrates.controller;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.dto.*;
import me.exrates.model.dto.filterData.AdminTransactionsFilterData;
import me.exrates.model.enums.ReportGroupUserRole;
import me.exrates.model.enums.UserRole;
import me.exrates.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@Log4j2
public class ReportController {

    @Autowired
    ReportService reportService;

    @RequestMapping(value = "/2a8fy7b07dxe44/report/InputOutputSummary", method = RequestMethod.GET, produces = "text/plain;charset=utf-8")
    @ResponseBody
    public String getUsersWalletsSummeryTotalInOut(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String role,
            @RequestParam String direction,
            @RequestParam List<String> currencyList,
            Principal principal) {
        String value = InvoiceReportDto.getTitle() +
                reportService.getInvoiceReport(principal.getName(), startDate, endDate, role, direction, currencyList)
                        .stream()
                        .map(e -> e.toString())
                        .collect(Collectors.joining());
        return value;
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/report/UsersWalletsSummaryInOut", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Map<String, String> getUsersWalletsSummeryInOut(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String role,
            @RequestParam(required = false) List<String> currencyList,
            Principal principal) {
        List<SummaryInOutReportDto> resultList = reportService.getUsersSummaryInOutList(principal.getName(), startDate, endDate, role, currencyList);
        String listString = SummaryInOutReportDto.getTitle() +
                resultList.stream()
                        .map(e -> e.toString())
                        .collect(Collectors.joining());
        /**/
        Map<String, UserSummaryTotalInOutDto> resultMap = reportService.getUsersSummaryInOutMap(resultList);
        /**/
        String summaryString = UserSummaryTotalInOutDto.getTitle() +
                resultMap.values().stream()
                        .map(e -> e.toString())
                        .collect(Collectors.joining());
        return new HashMap<String, String>() {{
            put("list", listString);
            put("summary", summaryString);
        }};
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/report/usersWalletsSummary", method = RequestMethod.GET, produces = "text/plain;charset=utf-8")
    @ResponseBody
    public String getUsersWalletsSummeryTxt(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String role,
            @RequestParam(required = false) List<String> currencyList,
            @RequestParam Boolean includeEmpty,
            Principal principal) {
        return
                UserSummaryDto.getTitle() +
                        reportService.getTurnoverInfoByUserAndCurrencyForPeriodAndRoleList(principal.getName(), startDate, endDate, role, currencyList)
                                .stream()
                                .filter(e -> includeEmpty || !e.isEmpty())
                                .map(e -> e.toString())
                                .collect(Collectors.joining());
    }


    @RequestMapping(value = "/2a8fy7b07dxe44/report/userSummaryOrders", method = RequestMethod.GET, produces = "text/plain;charset=utf-8")
    @ResponseBody
    public String getUserSummaryOrders(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String role,
            @RequestParam(required = false) List<String> currencyList,
            Principal principal) {
        List<UserSummaryOrdersDto> list = reportService.getUserSummaryOrdersList(principal.getName(), startDate, endDate, role, currencyList);
        BigDecimal sumAmountBuy = new BigDecimal(0.00);
        BigDecimal sumAmountBuyFee = new BigDecimal(0.00);
        BigDecimal sumAmountSell = new BigDecimal(0.00);
        BigDecimal sumAmountSellFee = new BigDecimal(0.00);

        String value = "Orders from " + startDate.substring(0, 10) + " till " + endDate.substring(0, 10) + ": \n \n" + UserSummaryOrdersDto.getTitle() +
                list.stream()
                        .map(e -> e.toString())
                        .collect(Collectors.joining());

        for (UserSummaryOrdersDto userSummaryOrdersDto : list) {
            if (userSummaryOrdersDto.getAmountBuy() != null) {
                sumAmountBuy = sumAmountBuy.add(userSummaryOrdersDto.getAmountBuy());
            }
            if (userSummaryOrdersDto.getAmountBuyFee() != null) {
                sumAmountBuyFee = sumAmountBuyFee.add(userSummaryOrdersDto.getAmountBuyFee());
            }
            if (userSummaryOrdersDto.getAmountSell() != null) {
                sumAmountSell = sumAmountSell.add(userSummaryOrdersDto.getAmountSell());
            }
            if (userSummaryOrdersDto.getAmountSellFee() != null) {
                sumAmountSellFee = sumAmountSellFee.add(userSummaryOrdersDto.getAmountSellFee());
            }
        }
        value += "\n sumBuy: " + sumAmountBuy.toString() + "\n sumBuyFee: " + sumAmountBuyFee.toString();
        value += "\n sumSell: " + sumAmountSell.toString() + "\n sumSellFee: " + sumAmountSellFee.toString();

        return value;
    }

    @RequestMapping(value = "/2a8fy7b07dxe44/report/userSummaryOrdersByCurrencyPairs", method = RequestMethod.GET, produces = "text/plain;charset=utf-8")
    @ResponseBody
    public String getUserSummaryOrdersByCurrencyPairs(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam String role,
            Principal principal) {
        List<UserSummaryOrdersByCurrencyPairsDto> list = reportService.getUserSummaryOrdersByCurrencyPairList(principal.getName(), startDate, endDate, role);
        String value = "Orders by currency pairs from" + startDate.substring(0, 10) + " till " + endDate.substring(0, 10) + ": \n \n" + UserSummaryOrdersByCurrencyPairsDto.getTitle() +
                list.stream()
                        .map(e -> e.toString())
                        .collect(Collectors.joining());
        return value;
    }

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

    @RequestMapping(value = "/2a8fy7b07dxe44/report/downloadUserIpInfo", method = RequestMethod.GET, produces = "text/plain;charset=utf-8")
    @ResponseBody
    public String getUserIpInfo(@RequestParam(required = false) String role) {
        List<UserIpReportDto> result = reportService.getUserIpReport(role);
        return result.stream().map(UserIpReportDto::toString)
                .collect(Collectors.joining("", UserIpReportDto.getTitle(), ""));
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/generalStats/currencyPairTurnover", method = GET)
    public String getCurrenciesTurnover(@RequestParam("startTime") String startTimeString,
                                        @RequestParam("endTime") String endTimeString,
                                        @RequestParam("roles") List<UserRole> userRoles) {
        String dateTimePattern = "yyyy-MM-dd_HH:mm";
        LocalDateTime startTime = LocalDateTime.from(DateTimeFormatter.ofPattern(dateTimePattern).parse(startTimeString));
        LocalDateTime endTime = LocalDateTime.from(DateTimeFormatter.ofPattern(dateTimePattern).parse(endTimeString));
        List<CurrencyPairTurnoverReportDto> result = reportService.getCurrencyPairTurnoverForRoleList(startTime, endTime, userRoles);
        return result.stream().map(CurrencyPairTurnoverReportDto::toString)
                .collect(Collectors.joining("", CurrencyPairTurnoverReportDto.getTitle(), ""));
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/generalStats/ordersCommissions", method = GET)
    public String getCurrencyPairComissions(@RequestParam("startTime") String startTimeString,
                                            @RequestParam("endTime") String endTimeString,
                                            @RequestParam("roles") List<UserRole> userRoles) {
        String dateTimePattern = "yyyy-MM-dd_HH:mm";
        LocalDateTime startTime = LocalDateTime.from(DateTimeFormatter.ofPattern(dateTimePattern).parse(startTimeString));
        LocalDateTime endTime = LocalDateTime.from(DateTimeFormatter.ofPattern(dateTimePattern).parse(endTimeString));
        List<OrdersCommissionSummaryDto> result = reportService.getOrderCommissionsByPairsForPeriod(startTime, endTime, userRoles);
        return result.stream().map(OrdersCommissionSummaryDto::toString)
                .collect(Collectors.joining("", OrdersCommissionSummaryDto.getTitle(), ""));
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/generalStats/inputOutputSummaryWithCommissions", method = GET)
    public String getInputOutputSummaryWithCommissions(@RequestParam("startTime") String startTimeString,
                                                       @RequestParam("endTime") String endTimeString,
                                                       @RequestParam("roles") List<UserRole> userRoles) {
        String dateTimePattern = "yyyy-MM-dd_HH:mm";
        LocalDateTime startTime = LocalDateTime.from(DateTimeFormatter.ofPattern(dateTimePattern).parse(startTimeString));
        LocalDateTime endTime = LocalDateTime.from(DateTimeFormatter.ofPattern(dateTimePattern).parse(endTimeString));
        List<InputOutputCommissionSummaryDto> result = reportService.getInputOutputSummaryWithCommissions(startTime, endTime, userRoles);
        return result.stream().map(InputOutputCommissionSummaryDto::toString)
                .collect(Collectors.joining("", InputOutputCommissionSummaryDto.getTitle(), ""));
    }


    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/generalStats/currencyTurnover", method = GET)
    public String getCurrencyPairsTurnover(@RequestParam("startTime") String startTimeString,
                                           @RequestParam("endTime") String endTimeString,
                                           @RequestParam("roles") List<UserRole> userRoles) {
        String dateTimePattern = "yyyy-MM-dd_HH:mm";
        LocalDateTime startTime = LocalDateTime.from(DateTimeFormatter.ofPattern(dateTimePattern).parse(startTimeString));
        LocalDateTime endTime = LocalDateTime.from(DateTimeFormatter.ofPattern(dateTimePattern).parse(endTimeString));
        List<CurrencyInputOutputSummaryDto> result = reportService.getCurrencyTurnoverForRoleList(startTime, endTime, userRoles);
        return result.stream().map(CurrencyInputOutputSummaryDto::toString)
                .collect(Collectors.joining("", CurrencyInputOutputSummaryDto.getTitle(), ""));
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/generalStats/totalBalances", method = GET)
    public String getTotalBalancesReportByRoles(@RequestParam("roles") List<UserRole> userRoles) {

        List<UserRoleTotalBalancesReportDto<UserRole>> result = reportService.getWalletBalancesSummaryByRoles(userRoles);
        return result.stream().map(UserRoleTotalBalancesReportDto::toString)
                .collect(Collectors.joining("", UserRoleTotalBalancesReportDto.getTitle(UserRole.class), ""));
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/generalStats/groupTotalBalances", method = GET)
    public Future<List<UserRoleTotalBalancesReportDto<ReportGroupUserRole>>> getTotalBalancesReportByGroups() {
        return CompletableFuture.supplyAsync(() -> reportService.getWalletBalancesSummaryByGroups());
    }

    @ResponseBody
    @RequestMapping(value = "/2a8fy7b07dxe44/generalStats/balancesExternalWallets", method = GET)
    public Future<List<ExternalWalletsDto>> getBalancesExternalWallets() {
        return CompletableFuture.supplyAsync(() -> reportService.getBalancesWithExternalWallets());
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


}