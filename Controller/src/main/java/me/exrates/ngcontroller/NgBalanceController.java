package me.exrates.ngcontroller;

import lombok.extern.log4j.Log4j;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.dto.onlineTableDto.MyWalletsDetailedDto;
import me.exrates.ngcontroller.model.RefillPendingRequestDto;
import me.exrates.ngcontroller.service.BalanceService;
import me.exrates.ngcontroller.util.PagedResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Locale;

@RestController
@RequestMapping(value = "/info/private/v2/balances",
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Log4j
public class NgBalanceController {

    private final BalanceService balanceService;
    private final LocaleResolver localeResolver;

    @Autowired
    public NgBalanceController(BalanceService balanceService,
                               LocaleResolver localeResolver) {
        this.balanceService = balanceService;
        this.localeResolver = localeResolver;
    }

    @GetMapping
    public ResponseEntity<PagedResult<MyWalletsDetailedDto>> getBalances(
            @RequestParam(required = false, defaultValue = "20") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer offset) {
        String email = getPrincipalEmail();
        try {
            PagedResult<MyWalletsDetailedDto> pagedResult = balanceService.getWalletsDetails(offset, limit, email);
            return ResponseEntity.ok(pagedResult);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/pendingRequests")
    public ResponseEntity<PagedResult<RefillPendingRequestDto>> getPendingRequests(
            @RequestParam(required = false, defaultValue = "20") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer offset) {
        String email = getPrincipalEmail();
        try {
            PagedResult<RefillPendingRequestDto> pendingRequests = balanceService.getPendingRequests(offset, limit, email);
            return ResponseEntity.ok(pendingRequests);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/inputOutputData")
    public ResponseEntity<PagedResult<MyInputOutputHistoryDto>> getMyInputOutputData(
            @RequestParam(required = false, defaultValue = "20") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "0") Integer currencyId,
            @RequestParam(required = false, name = "dateFrom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false, name = "dateTo") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            HttpServletRequest request) {
        String email = getPrincipalEmail();
        Locale locale = localeResolver.resolveLocale(request);
        try {
            PagedResult<MyInputOutputHistoryDto> page =
                    balanceService.getUserInputOutputHistory(email, limit, offset, currencyId, dateFrom, dateTo, locale);
            return ResponseEntity.ok(page);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    private String getPrincipalEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}