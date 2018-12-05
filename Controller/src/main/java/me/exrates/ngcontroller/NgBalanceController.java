package me.exrates.ngcontroller;

import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j;
import me.exrates.controller.exception.RequestsLimitExceedException;
import me.exrates.model.dto.TransferDto;
import me.exrates.model.dto.TransferRequestFlatDto;
import me.exrates.model.dto.WalletTotalUsdDto;
import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.dto.onlineTableDto.MyWalletsDetailedDto;
import me.exrates.model.dto.onlineTableDto.MyWalletsStatisticsDto;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.CurrencyType;
import me.exrates.model.enums.invoice.InvoiceActionTypeEnum;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.enums.invoice.TransferStatusEnum;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.ngcontroller.model.RefillPendingRequestDto;
import me.exrates.ngcontroller.service.BalanceService;
import me.exrates.ngcontroller.util.PagedResult;
import me.exrates.service.CurrencyService;
import me.exrates.service.TransferService;
import me.exrates.service.WalletService;
import me.exrates.service.cache.ExchangeRatesHolder;
import me.exrates.service.exception.invoice.InvoiceNotFoundException;
import me.exrates.service.util.RateLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.PRESENT_VOUCHER;

@RestController
@RequestMapping(value = "/info/private/v2/balances",
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Log4j
public class NgBalanceController {

    private final BalanceService balanceService;
    private final CurrencyService currencyService;
    private final ExchangeRatesHolder exchangeRatesHolder;
    private final LocaleResolver localeResolver;
    private final MessageSource messageSource;
    private final RateLimitService rateLimitService;
    private final TransferService transferService;
    private final WalletService walletService;

    @Autowired
    public NgBalanceController(BalanceService balanceService,
                               CurrencyService currencyService, ExchangeRatesHolder exchangeRatesHolder,
                               LocaleResolver localeResolver,
                               MessageSource messageSource,
                               RateLimitService rateLimitService,
                               TransferService transferService,
                               WalletService walletService) {
        this.balanceService = balanceService;
        this.currencyService = currencyService;
        this.exchangeRatesHolder = exchangeRatesHolder;
        this.localeResolver = localeResolver;
        this.messageSource = messageSource;
        this.rateLimitService = rateLimitService;
        this.transferService = transferService;
        this.walletService = walletService;
    }

    // apiUrl/info/private/v2/balances?limit=20&offset=0&excludeZero=false
    @GetMapping
    public ResponseEntity<PagedResult<MyWalletsDetailedDto>> getBalances(
            @RequestParam(required = false, defaultValue = "20") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "false") Boolean excludeZero,
            @RequestParam(required = false) CurrencyType currencyType) {
        String email = getPrincipalEmail();
        try {
            PagedResult<MyWalletsDetailedDto> pagedResult = balanceService.getWalletsDetails(offset, limit, email, excludeZero, currencyType);
            return ResponseEntity.ok(pagedResult);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // apiUrl/info/private/v2/balances/pendingRequests?limit=20&offset=0
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

    // apiUrl/info/private/v2/balances/totalBalance
    @SuppressWarnings("Duplicates")
    @GetMapping("/totalBalance")
    @ResponseBody
    public Map<String, Object> getUserTotalBalance(HttpServletRequest request) {
        List<MyWalletsStatisticsDto> resultWallet = walletService.getAllWalletsForUserReduced(null, getPrincipalEmail(),
                localeResolver.resolveLocale(request), CurrencyPairType.MAIN);
        HashMap<String, Object> map = new HashMap<>();
        map.put("mapWallets", resultWallet);

        if (resultWallet.size() > 1) {
            List<ExOrderStatisticsShortByPairsDto> resultOrders = exchangeRatesHolder.getAllRates();

            final HashMap<String, BigDecimal> ratesBTC_ETH = new HashMap<>();
            resultOrders
                    .stream()
                    .filter(p -> p.getCurrencyPairName().contains("BTC/USD") || p.getCurrencyPairName().contains("ETH/USD"))
                    .forEach(p -> ratesBTC_ETH.put(p.getCurrencyPairName(), new BigDecimal(p.getLastOrderRate())));

            final List<WalletTotalUsdDto> walletTotalUsdDtoList = new ArrayList<>();
            for (MyWalletsStatisticsDto myWalletsStatisticsDto : resultWallet) {
                WalletTotalUsdDto walletTotalUsdDto = new WalletTotalUsdDto(myWalletsStatisticsDto.getCurrencyName());
                Map<String, BigDecimal> mapWalletTotalUsdDto = new HashMap<>();
                if (myWalletsStatisticsDto.getCurrencyName().equals("USD")) {
                    walletTotalUsdDto.setSumUSD(new BigDecimal(myWalletsStatisticsDto.getTotalBalance()));
                    walletTotalUsdDto.setRates(mapWalletTotalUsdDto);
                    walletTotalUsdDtoList.add(walletTotalUsdDto);
                }
                resultOrders.stream()
                        .filter(o -> o.getCurrencyPairName().equals(myWalletsStatisticsDto.getCurrencyName().concat("/USD"))
                                || o.getCurrencyPairName().equals(myWalletsStatisticsDto.getCurrencyName().concat("/BTC"))
                                || o.getCurrencyPairName().equals(myWalletsStatisticsDto.getCurrencyName().concat("/ETH"))
                        )
                        .forEach(o -> {
                            mapWalletTotalUsdDto.put(o.getCurrencyPairName(), new BigDecimal(o.getLastOrderRate()));
                        });
                if (!mapWalletTotalUsdDto.isEmpty()) {
                    walletTotalUsdDto.setTotalBalance(new BigDecimal(myWalletsStatisticsDto.getTotalBalance()));
                    walletTotalUsdDto.setRates(mapWalletTotalUsdDto);
                    walletTotalUsdDtoList.add(walletTotalUsdDto);
                }
            }

            walletTotalUsdDtoList.stream().forEach(wallet -> {
                if (wallet.getRates().containsKey(wallet.getCurrency().concat("/USD"))) {
                    wallet.setSumUSD(wallet.getRates().get(wallet.getCurrency().concat("/USD")).multiply(wallet.getTotalBalance()));
                } else if (wallet.getRates().containsKey(wallet.getCurrency().concat("/BTC"))) {
                    wallet.setSumUSD(wallet.getRates().get(wallet.getCurrency().concat("/BTC"))
                            .multiply(wallet.getTotalBalance()).multiply(ratesBTC_ETH.get("BTC/USD")));
                } else if (wallet.getRates().containsKey(wallet.getCurrency().concat("/ETH"))) {
                    wallet.setSumUSD(wallet.getRates().get(wallet.getCurrency().concat("/ETH"))
                            .multiply(wallet.getTotalBalance()).multiply(ratesBTC_ETH.get("ETH/USD")));
                }
            });

            map.put("sumTotalUSD", walletTotalUsdDtoList.stream().mapToDouble(w -> w.getSumUSD().doubleValue()).sum());
        }

        return map;
    }

    //  apiUrl/info/private/v2/balances/inputOutputData?limit=20&offset=0&currencyId=0&dateFrom=2018-11-21&dateTo=2018-11-26
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