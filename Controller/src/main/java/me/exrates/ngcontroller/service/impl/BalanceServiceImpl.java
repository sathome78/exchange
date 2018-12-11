package me.exrates.ngcontroller.service.impl;

import me.exrates.model.dto.onlineTableDto.ExOrderStatisticsShortByPairsDto;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.dto.onlineTableDto.MyWalletsDetailedDto;
import me.exrates.model.dto.openAPI.WalletBalanceDto;
import me.exrates.model.enums.CurrencyType;
import me.exrates.ngcontroller.dao.BalanceDao;
import me.exrates.ngcontroller.model.RefillPendingRequestDto;
import me.exrates.ngcontroller.model.UserBalancesDto;
import me.exrates.ngcontroller.service.BalanceService;
import me.exrates.ngcontroller.service.NgWalletService;
import me.exrates.ngcontroller.service.RefillPendingRequestService;
import me.exrates.ngcontroller.util.PagedResult;
import me.exrates.service.CurrencyService;
import me.exrates.service.InputOutputService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.cache.ExchangeRatesHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class BalanceServiceImpl implements BalanceService {

    private final BalanceDao balanceDao;
    private final InputOutputService inputOutputService;
    private final RefillPendingRequestService refillPendingRequestService;
    private final NgWalletService ngWalletService;
    private final UserService userService;
    private final ExchangeRatesHolder exchangeRatesHolder;
    @Autowired
    private WalletService walletService;
    @Autowired
    private CurrencyService currencyService;

    @Autowired
    public BalanceServiceImpl(BalanceDao balanceDao,
                              InputOutputService inputOutputService,
                              NgWalletService ngWalletService,
                              RefillPendingRequestService refillPendingRequestService,
                              UserService userService, ExchangeRatesHolder exchangeRatesHolder) {
        this.balanceDao = balanceDao;
        this.inputOutputService = inputOutputService;
        this.refillPendingRequestService = refillPendingRequestService;
        this.ngWalletService = ngWalletService;
        this.userService = userService;
        this.exchangeRatesHolder = exchangeRatesHolder;
    }

    @Override
    public List<UserBalancesDto> getUserBalances(String tikerName, String sortByCreated, Integer page, Integer limit, int userId) {
        return balanceDao.getUserBalances(tikerName, sortByCreated, page, limit,userId);
    }

    @Override
    public PagedResult<MyWalletsDetailedDto> getWalletsDetails(int offset, int limit, String email, boolean excludeZero, CurrencyType currencyType) {
        List<MyWalletsDetailedDto> details = ngWalletService.getAllWalletsForUserDetailed(email, Locale.ENGLISH, currencyType);
        if (excludeZero) {
            details = details.stream().filter(filterZeroActiveBalance()).collect(Collectors.toList());
        }
        setBt
        return getSafeSubList(details, offset, limit);
    }

    private Predicate<MyWalletsDetailedDto> filterZeroActiveBalance() {
        return wallet -> new BigDecimal(wallet.getActiveBalance()).compareTo(BigDecimal.ZERO) > 0;
    }

    @Override
    public PagedResult<RefillPendingRequestDto> getPendingRequests(int offset, int limit, String email) {
        List<RefillPendingRequestDto> requests =
                refillPendingRequestService.getPendingRefillRequests(userService.getIdByEmail(email));
        return getSafeSubList(requests, offset, limit);
    }

    @Override
    public PagedResult<MyInputOutputHistoryDto> getUserInputOutputHistory(String email, int limit, int offset,
                                                                          int currencyId, LocalDate dateFrom,
                                                                          LocalDate dateTo, Locale locale) {
        if (dateFrom == null) {
            dateFrom = LocalDate.now().minusMonths(1);
        }
        if (dateTo == null) {
            dateTo = LocalDate.now();
        }
        PagedResult<MyInputOutputHistoryDto> pagedResult = new PagedResult<>();
        pagedResult.setCount(inputOutputService.getUserInputOutputHistoryCount(email, dateFrom, dateTo, currencyId, locale));
        List<MyInputOutputHistoryDto> history =
                inputOutputService.getUserInputOutputHistory(email, offset, limit, dateFrom, dateTo, currencyId, locale);
        pagedResult.setItems(history);
        return pagedResult;
    }

    @Override
    public Map<String, BigDecimal> getBalancesInBtcAndUsd() {
        List<WalletBalanceDto> userBalances = walletService.getBalancesForUser();
        BigDecimal btcBalances = BigDecimal.ZERO;
        BigDecimal usdBalances = BigDecimal.ZERO;
        List<ExOrderStatisticsShortByPairsDto> rates = exchangeRatesHolder.getAllRates();
        rates.forEach(System.out::println);
        Map<Integer, String> btcRateMapped = rates.stream()
                .filter(p->p.getMarket().equals("BTC"))
                .collect(Collectors.toMap(ExOrderStatisticsShortByPairsDto::getCurrency1Id, ExOrderStatisticsShortByPairsDto::getLastOrderRate, (oldValue, newValue) -> oldValue));
        Map<Integer, String> usdRateMapped = rates.stream()
                .filter(p->p.getMarket().equals("USD"))
                .collect(Collectors.toMap(ExOrderStatisticsShortByPairsDto::getCurrency1Id, ExOrderStatisticsShortByPairsDto::getLastOrderRate, (oldValue, newValue) -> oldValue));
        BigDecimal btcUsdRate = new BigDecimal(usdRateMapped.get(currencyService.findByName("BTC").getId()));
        for (WalletBalanceDto p : userBalances) {
            BigDecimal sumBalances = p.getActiveBalance().add(p.getReservedBalance());
            if (sumBalances.compareTo(BigDecimal.ZERO) > 0) {
                switch (p.getCurrencyName()) {
                    case "BTC":
                        btcBalances = btcBalances.add(sumBalances);
                        usdBalances = usdBalances.add(sumBalances.multiply(btcUsdRate));
                        break;
                    case "USD":
                        usdBalances = usdBalances.add(sumBalances);
                        btcBalances = btcBalances.add(sumBalances.divide(btcUsdRate, RoundingMode.HALF_UP).setScale(8, RoundingMode.HALF_UP));
                        break;
                    default:
                        BigDecimal usdRate = new BigDecimal(usdRateMapped.getOrDefault(p.getCurrencyId(), "0"));
                        BigDecimal btcRate = new BigDecimal(btcRateMapped.getOrDefault(p.getCurrencyId(), "0"));
                        if (usdRate.compareTo(BigDecimal.ZERO) <= 0) {
                            usdRate = btcRate.multiply(btcUsdRate);
                        }
                        btcBalances = btcBalances.add(btcRate.multiply(sumBalances));
                        usdBalances = usdBalances.add(usdRate.multiply(sumBalances));
                        break;
                }
            }
        }
        Map<String, BigDecimal> balancesMap = new HashMap<>();
        balancesMap.put("BTC", btcBalances.setScale(8, RoundingMode.HALF_DOWN));
        balancesMap.put("USD", usdBalances.setScale(2, RoundingMode.HALF_DOWN));
        return balancesMap;
    }

    private


    private <T> PagedResult<T>  getSafeSubList(List<T> items, int offset, int limit) {
        if (items.isEmpty() || offset >= items.size()) {
            return new PagedResult<>(0, Collections.emptyList());
        }
        if ((offset + limit) > items.size()) {
            return new PagedResult<>(items.size(), items.subList(offset, items.size()));
        }
        return new PagedResult<>(items.size(), items.subList(offset, offset + limit));
    }

}
