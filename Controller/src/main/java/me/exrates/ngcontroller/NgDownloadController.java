package me.exrates.ngcontroller;

import com.google.common.io.ByteSource;
import me.exrates.model.CurrencyPair;
import me.exrates.model.dto.ReportDto;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.dto.onlineTableDto.OrderWideListDto;
import me.exrates.model.enums.OrderStatus;
import me.exrates.ngService.BalanceService;
import me.exrates.service.CurrencyService;
import me.exrates.service.OrderService;
import me.exrates.service.UserService;
import me.exrates.service.util.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
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
    public ResponseEntity exportExcelOrders(@PathVariable("status") String status,
                                            @RequestParam(required = false, name = "currencyPairId", defaultValue = "0") Integer currencyPairId,
                                            @RequestParam(required = false, name = "scope", defaultValue = StringUtils.EMPTY) String scope,
                                            @RequestParam(required = false, name = "hideCanceled", defaultValue = "false") Boolean hideCanceled,
                                            @RequestParam(required = false, name = "dateFrom") String dateFrom,
                                            @RequestParam(required = false, name = "dateTo") String dateTo,
                                            HttpServletRequest request) {
        Integer userId = userService.getIdByEmail(getPrincipalEmail());
        Locale locale = localeResolver.resolveLocale(request);
        OrderStatus orderStatus = OrderStatus.valueOf(status);
        LocalDateTime dateTimeFrom = DateUtils.convert(dateFrom, false);
        LocalDateTime dateTimeTo = DateUtils.convert(dateTo, true);

        CurrencyPair currencyPair = null;
        if (currencyPairId > 0) {
            currencyPair = currencyService.findCurrencyPairById(currencyPairId);
        }

        ReportDto reportDto;
        try {
            List<OrderWideListDto> orders = orderService.getOrdersForExcel(
                    userId,
                    currencyPair,
                    StringUtils.EMPTY,
                    orderStatus,
                    scope,
                    0,
                    0,
                    hideCanceled,
                    "DESC",
                    dateTimeFrom,
                    dateTimeTo,
                    locale);

            reportDto = orderService.getOrderExcelFile(orders);

            final byte[] content = reportDto.getContent();
            final String fileName = reportDto.getFileName();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentLength(content.length);
            headers.setContentDispositionFormData("attachment", fileName);

            InputStreamResource isr = new InputStreamResource(ByteSource.wrap(content).openStream());
            return new ResponseEntity<>(isr, headers, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("Downloaded file is corrupted");
            return ResponseEntity.noContent().build();
        }
    }

    //  apiUrl/info/private/v2/download/inputOutputData/excel?&currencyId=0&currencyName=&dateFrom=2018-11-21&dateTo=2018-11-26
    @GetMapping(value = "/inputOutputData/excel")
    public ResponseEntity getMyInputOutputDataToExcel(@RequestParam(required = false, defaultValue = "0") Integer currencyId,
                                                      @RequestParam(required = false, defaultValue = StringUtils.EMPTY) String currencyName,
                                                      @RequestParam(required = false, name = "dateFrom") String dateFrom,
                                                      @RequestParam(required = false, name = "dateTo") String dateTo,
                                                      HttpServletRequest request) {
        Locale locale = localeResolver.resolveLocale(request);
        String userEmail = getPrincipalEmail();
        LocalDateTime dateTimeFrom = DateUtils.convert(dateFrom, false);
        LocalDateTime dateTimeTo = DateUtils.convert(dateTo, true);

        ReportDto reportDto;
        try {
            List<MyInputOutputHistoryDto> transactions = balanceService.getUserInputOutputHistoryExcel(
                    userEmail,
                    currencyId,
                    currencyName,
                    dateTimeFrom,
                    dateTimeTo,
                    0,
                    0,
                    locale);

            reportDto = orderService.getTransactionExcelFile(transactions);

            final byte[] content = reportDto.getContent();
            final String fileName = reportDto.getFileName();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentLength(content.length);
            headers.setContentDispositionFormData("attachment", fileName);

            InputStreamResource isr = new InputStreamResource(ByteSource.wrap(content).openStream());
            return new ResponseEntity<>(isr, headers, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("Downloaded file is corrupted");
            return ResponseEntity.noContent().build();
        }
    }

    private String getPrincipalEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
