package me.exrates.ngcontroller;

import lombok.extern.log4j.Log4j;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.model.dto.BalanceFilterDataDto;
import me.exrates.model.dto.TransactionFilterDataDto;
import me.exrates.model.dto.WalletTotalUsdDto;
import me.exrates.model.dto.ngDto.RefillOnConfirmationDto;
import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.dto.onlineTableDto.MyWalletsDetailedDto;
import me.exrates.model.dto.onlineTableDto.MyWalletsStatisticsDto;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.CurrencyType;
import me.exrates.model.ngExceptions.NgBalanceException;
import me.exrates.model.ngExceptions.NgDashboardException;
import me.exrates.model.ngModel.RefillPendingRequestDto;
import me.exrates.model.ngUtil.PagedResult;
import me.exrates.ngService.BalanceService;
import me.exrates.security.exception.IncorrectPinException;
import me.exrates.service.RefillService;
import me.exrates.service.WalletService;
import me.exrates.service.cache.ExchangeRatesHolder;
import me.exrates.service.exception.UserNotFoundException;
import me.exrates.service.exception.UserOperationAccessException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(value = "/api/private/v2/balances",
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Log4j
public class NgBalanceController {

    private static final Logger logger = LoggerFactory.getLogger(NgBalanceController.class);

    private final BalanceService balanceService;
    private final ExchangeRatesHolder exchangeRatesHolder;
    private final LocaleResolver localeResolver;
    private final RefillService refillService;
    private final WalletService walletService;

    @Autowired
    public NgBalanceController(BalanceService balanceService,
                               ExchangeRatesHolder exchangeRatesHolder,
                               LocaleResolver localeResolver,
                               RefillService refillService,
                               WalletService walletService) {
        this.balanceService = balanceService;
        this.exchangeRatesHolder = exchangeRatesHolder;
        this.localeResolver = localeResolver;
        this.refillService = refillService;
        this.walletService = walletService;
    }

    // apiUrl/info/private/v2/balances?limit=20&offset=0&excludeZero=false&currencyName=BTC&currencyType=CRYPTO
    // apiUrl/info/private/v2/balances?limit=20&offset=0&excludeZero=false&currencyName=USD&currencyType=FIAT
    @GetMapping
    public ResponseEntity<PagedResult<MyWalletsDetailedDto>> getBalances(
            @RequestParam(required = false, defaultValue = "20") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "false") Boolean excludeZero,
            @RequestParam(required = false, defaultValue = StringUtils.EMPTY) String currencyName,
            @RequestParam(required = false, defaultValue = "0") Integer currencyId,
            @RequestParam(required = false) CurrencyType currencyType) {
        final String email = getPrincipalEmail();

        final BalanceFilterDataDto filter = BalanceFilterDataDto.builder()
                .limit(limit)
                .offset(offset)
                .excludeZero(excludeZero)
                .currencyName(currencyName)
                .currencyId(currencyId)
                .currencyType(currencyType)
                .email(email)
                .build();
        try {
            return ResponseEntity.ok(balanceService.getWalletsDetails(filter));
        } catch (Exception ex) {
            logger.error("Failed to get user balances", ex);
            throw new NgDashboardException(String.format("Failed to get user balances: %s", ex.getMessage()), ex);
        }
    }

    // apiUrl/info/private/v2/balances/pendingRequests?limit=20&offset=0
    // response https://api.myjson.com/bins/ufcws
    @GetMapping("/pendingRequests")
    public ResponseEntity<PagedResult<RefillPendingRequestDto>> getPendingRequests(
            @RequestParam(required = false, defaultValue = "") String currencyName,
            @RequestParam(required = false, defaultValue = "20") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer offset) {
        String email = getPrincipalEmail();
        try {
            PagedResult<RefillPendingRequestDto> pendingRequests = balanceService.getPendingRequests(offset, limit, currencyName, email);
            return ResponseEntity.ok(pendingRequests);
        } catch (Exception ex) {
            logger.error("Failed to get pending requests", ex);
            throw new NgDashboardException("Failed to get pending requests: " + ex.getMessage());
        }
    }

    // apiUrl/info/private/v2/balances/pending/revoke/{requestId}/{operation}
    // requestId - pending request id
    // operation - may be only REFILL or WITHDRAW, but only REFILL is processed
    @DeleteMapping(value = "/pending/revoke/{requestId}/{operation}")
    public ResponseEntity<Void> revokeWithdrawRequest(@PathVariable Integer requestId,
                                                      @PathVariable String operation) {
        if (operation.equalsIgnoreCase("REFILL")) {
            try {
                refillService.revokeRefillRequest(requestId);
                return ResponseEntity.ok().build();
            } catch (Exception e) {
                logger.error("Failed to revoke request with id: " + requestId, e);
                e.printStackTrace();
            }
        }
        logger.error("Failed to revoke such request ({}) is not supported", operation);
        throw new NgBalanceException("Failed to revoke such for operation " + operation);
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
                resultOrders
                        .stream()
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

            walletTotalUsdDtoList.forEach(wallet -> {
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

    // /info/private/v2/balances/currencies/{currencyId}
    // 200 - ok example https://api.myjson.com/bins/13670k
    // 404 - not found
    // 400 - something went wrong
    @GetMapping("/currencies/{currencyId}")
    public ResponseEntity<MyWalletsDetailedDto> getSingleCurrency(@PathVariable Integer currencyId) {
        try {
            String email = getPrincipalEmail();
            Optional<MyWalletsDetailedDto> result = balanceService.findOne(getPrincipalEmail(), currencyId);
            result.ifPresent(myWalletsDetailedDto -> {
                List<RefillOnConfirmationDto> confirmationRefills =
                        refillService.getOnConfirmationRefills(email, currencyId);
                if (confirmationRefills == null) {
                    confirmationRefills = Collections.emptyList();
                }
                myWalletsDetailedDto.setConfirmations(confirmationRefills);
            });

            if (result.isPresent()) {
                return ResponseEntity.ok(result.get());
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Failed to get single currency balance details", e);
            throw new NgBalanceException("Failed to get single currency balance details as " + e.getMessage());
        }
    }

    //  apiUrl/info/private/v2/balances/inputOutputData?limit=20&offset=0&currencyId=0&currencyName=&dateFrom=2018-11-21&dateTo=2018-11-26
    @GetMapping("/inputOutputData")
    public ResponseEntity<PagedResult<MyInputOutputHistoryDto>> getMyInputOutputData(
            @RequestParam(required = false, defaultValue = "20") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "0") Integer currencyId,
            @RequestParam(required = false, defaultValue = StringUtils.EMPTY) String currencyName,
            @RequestParam(required = false, name = "dateFrom") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false, name = "dateTo") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            HttpServletRequest request) {
        Locale locale = localeResolver.resolveLocale(request);

        TransactionFilterDataDto filter = TransactionFilterDataDto.builder()
                .email(getPrincipalEmail())
                .currencyId(currencyId)
                .currencyName(currencyName)
                .dateFrom(dateFrom)
                .dateTo(dateTo)
                .limit(limit)
                .offset(offset)
                .build();
        try {
            PagedResult<MyInputOutputHistoryDto> page = balanceService.getUserInputOutputHistory(filter, locale);
            return ResponseEntity.ok(page);
        } catch (Exception ex) {
            logger.error("Failed to get user inputOutputData", ex);
            throw new NgBalanceException("Failed to get user inputOutputData as " + ex.getMessage());
        }
    }

    @GetMapping("/inputOutputData/default")
    public ResponseEntity<PagedResult<MyInputOutputHistoryDto>> getDefaultMyInputOutputData(
            @RequestParam(required = false, defaultValue = "15") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            HttpServletRequest request) {
        Locale locale = localeResolver.resolveLocale(request);

        TransactionFilterDataDto filter = TransactionFilterDataDto.builder()
                .email(getPrincipalEmail())
                .limit(limit)
                .offset(offset)
                .currencyId(0)
                .currencyName(StringUtils.EMPTY)
                .dateFrom(null)
                .dateTo(null)
                .build();
        try {
            PagedResult<MyInputOutputHistoryDto> page = balanceService.getDefaultInputOutputHistory(filter, locale);
            return ResponseEntity.ok(page);
        } catch (Exception ex) {
            logger.error("Failed to get user inputOutputData", ex);
            throw new NgBalanceException("Failed to get user inputOutputData as " + ex.getMessage());
        }
    }

    // /info/private/v2/balances/myBalances
//        map.put("BTC", 0.00002343);
//        map.put("USD", 32.00);
    @GetMapping("/myBalances")
    public Map<String, BigDecimal> getBtcAndUsdBalancesSum() {
        return balanceService.getBalancesSumInBtcAndUsd();
    }

    private String getPrincipalEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({NgDashboardException.class, UserNotFoundException.class, NgBalanceException.class,
            UserOperationAccessException.class, IllegalArgumentException.class, IncorrectPinException.class})
    @ResponseBody
    public ErrorInfo OtherErrorsHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

}