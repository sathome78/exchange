package me.exrates.ngcontroller;

import lombok.extern.log4j.Log4j;
import me.exrates.model.User;
import me.exrates.model.dto.StatisticForMarket;
import me.exrates.model.dto.TableParams;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.dto.onlineTableDto.MyWalletsDetailedDto;
import me.exrates.model.enums.PagingDirection;
import me.exrates.model.vo.CacheData;
import me.exrates.ngcontroller.model.RefillPendingRequestDto;
import me.exrates.ngcontroller.model.UserBalancesDto;
import me.exrates.ngcontroller.service.BalanceService;
import me.exrates.ngcontroller.service.NgWalletService;
import me.exrates.ngcontroller.service.RefillPendingRequestService;
import me.exrates.ngcontroller.util.PagedResult;
import me.exrates.security.annotation.OnlineMethod;
import me.exrates.service.InputOutputService;
import me.exrates.service.MerchantService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.cache.MarketRatesHolder;
import org.apache.logging.log4j.core.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/info/private/v2/balances/",
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Log4j
public class NgBalanceController {

    private final UserService userService;

    private final RefillPendingRequestService refillPendingRequestService;

    private final InputOutputService inputOutputService;

    private final LocaleResolver localeResolver;

    private final NgWalletService ngWalletService;

    @Autowired
    public NgBalanceController(UserService userService, RefillPendingRequestService refillPendingRequestService, InputOutputService inputOutputService, LocaleResolver localeResolver, NgWalletService ngWalletService) {
        this.userService = userService;
        this.refillPendingRequestService = refillPendingRequestService;
        this.inputOutputService = inputOutputService;
        this.localeResolver = localeResolver;
        this.ngWalletService = ngWalletService;
    }

    @GetMapping
    public List<MyWalletsDetailedDto> getBalances(Principal principal) {
        return ngWalletService.getAllWalletsForUserDetailed(principal.getName(), Locale.ENGLISH);
    }

    @GetMapping("/getPendingRequests")
    public List<RefillPendingRequestDto> getPendingRequests() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return refillPendingRequestService.getPendingRefillRequests(userService.getIdByEmail(email));
    }

    @OnlineMethod
    @RequestMapping(value = "/getInputOutputData/{tableId}", method = RequestMethod.GET)
    public List<MyInputOutputHistoryDto> getMyInputoutputData(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset,
            @RequestParam String dateFrom,
            @RequestParam String dateTo,
            HttpServletRequest request,
            Principal principal) {
        log.info("Trololo " + limit + " " + offset + " " + dateFrom + " " + dateTo);
        List<MyInputOutputHistoryDto> result = inputOutputService.getMyInputOutputHistory(principal.getName(), offset == null ? 0 : offset, limit == null ? 28 : limit, dateFrom, dateTo, localeResolver.resolveLocale(request));
        return result;
    }

}