package me.exrates.ngcontroller;

import me.exrates.model.CurrencyPair;
import me.exrates.model.dto.TransactionFilterDataDto;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.enums.OrderStatus;
import me.exrates.ngService.BalanceService;
import me.exrates.service.CurrencyService;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@RequestMapping("/api/private/v2/download/")
@RestController
public class NgDownloadController {

    private static final Logger logger = LogManager.getLogger(NgDownloadController.class);

    private final UserService userService;
    private final OrderService orderService;
    private final CurrencyService currencyService;
    private final LocaleResolver localeResolver;
    private final BalanceService balanceService;

    public NgDownloadController(UserService userService,
                                OrderService orderService,
                                CurrencyService currencyService,
                                LocaleResolver localeResolver,
                                BalanceService balanceService) {
        this.userService = userService;
        this.orderService = orderService;
        this.currencyService = currencyService;
        this.localeResolver = localeResolver;
        this.balanceService = balanceService;
    }

    @GetMapping("/orders/{status}/export")
    public void exportExcelOrders(
            @PathVariable("status") String status,
            @RequestParam(required = false, name = "currencyPairId", defaultValue = "0") Integer currencyPairId,
            @RequestParam(required = false, name = "scope", defaultValue = "") String scope,
            @RequestParam(required = false, name = "hideCanceled", defaultValue = "false") Boolean hideCanceled,
            @RequestParam(required = false, name = "dateFrom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false, name = "dateTo") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            HttpServletRequest request, HttpServletResponse response) {

        OrderStatus orderStatus = OrderStatus.valueOf(status);

        int userId = userService.getIdByEmail(getPrincipalEmail());
        CurrencyPair currencyPair = currencyPairId > 0
                ? currencyService.findCurrencyPairById(currencyPairId)
                : null;
        Locale locale = localeResolver.resolveLocale(request);
        try {
            List<OrderWideListDto> orders =
                    orderService.getOrdersForExcel(userId, currencyPair, orderStatus, scope,
                            hideCanceled, locale, dateFrom, dateTo);

            orderService.getExcelFile(orders, orderStatus, response);

        } catch (Exception ex) {
            logger.error("Error export orders to file, e - {}", ex.getMessage());
        }
    }

    //  apiUrl/info/private/v2/download/inputOutputData/excel?&currencyId=0&currencyName=&dateFrom=2018-11-21&dateTo=2018-11-26
    @GetMapping(value = "/inputOutputData/excel")
    public void getMyInputOutputDataToExcel(
            @RequestParam(required = false, defaultValue = "0") Integer currencyId,
            @RequestParam(required = false, defaultValue = StringUtils.EMPTY) String currencyName,
            @RequestParam(required = false, name = "dateFrom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false, name = "dateTo") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            HttpServletRequest request, HttpServletResponse response) {
        Locale locale = localeResolver.resolveLocale(request);

        TransactionFilterDataDto filter = TransactionFilterDataDto.builder()
                .email(getPrincipalEmail())
                .currencyId(currencyId)
                .currencyName(currencyName)
                .dateFrom(dateFrom)
                .dateTo(dateTo)
                .build();
        try {
            List<MyInputOutputHistoryDto> transactions = balanceService.getUserInputOutputHistoryExcel(filter, locale);

            orderService.getTransactionExcelFile(transactions, response);

        } catch (Exception ex) {
            logger.error("Error export orders to file, e - {}", ex.getMessage());
        }
    }

    private String getPrincipalEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}