package me.exrates.ngcontroller;

import com.google.common.collect.ImmutableList;
import lombok.extern.log4j.Log4j;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.dto.onlineTableDto.MyWalletsDetailedDto;
import me.exrates.ngcontroller.model.RefillPendingRequestDto;
import me.exrates.ngcontroller.service.BalanceService;
import me.exrates.ngcontroller.service.NgWalletService;
import me.exrates.ngcontroller.service.RefillPendingRequestService;
import me.exrates.ngcontroller.util.PagedResult;
import me.exrates.service.InputOutputService;
import me.exrates.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping(value = "/info/private/v2/balances",
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Log4j
public class NgBalanceController {

    private final UserService userService;

    private final BalanceService balanceService;

    private final RefillPendingRequestService refillPendingRequestService;

    private final InputOutputService inputOutputService;

    private final LocaleResolver localeResolver;

    private final NgWalletService ngWalletService;

    @Autowired
    public NgBalanceController(UserService userService, BalanceService balanceService, RefillPendingRequestService refillPendingRequestService, InputOutputService inputOutputService, LocaleResolver localeResolver, NgWalletService ngWalletService) {
        this.userService = userService;
        this.balanceService = balanceService;
        this.refillPendingRequestService = refillPendingRequestService;
        this.inputOutputService = inputOutputService;
        this.localeResolver = localeResolver;
        this.ngWalletService = ngWalletService;
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

    @GetMapping("/getPendingRequests")
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

    @RequestMapping(value = "/getInputOutputData", method = RequestMethod.GET)
    public ResponseEntity<List<MyInputOutputHistoryDto>> getMyInputOutputData(
            @RequestParam(required = false, defaultValue = "20") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "0") Integer currencyId,
            @RequestParam("dateFrom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam("dateFrom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            HttpServletRequest request) {
        String email = getPrincipalEmail();
        Locale locale = localeResolver.resolveLocale(request);
//        try {
//            return ResponseEntity.ok(inputOutputService.getMyInputOutputHistory(email, offset == null ? 0 : offset, limit == null ? 28 : limit, dateFrom, dateTo, currency, localeResolver.resolveLocale(request)));
//        } catch (Exception ex) {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
        throw new UnsupportedOperationException();
    }

    private String getPrincipalEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public static void main(String[] args) {
        List<Integer> integers = ImmutableList.of(1,2,3,4);
        System.out.println(integers.subList(5, 7));
    }

}