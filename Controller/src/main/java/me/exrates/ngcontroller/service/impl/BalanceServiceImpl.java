package me.exrates.ngcontroller.service.impl;

import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.dto.onlineTableDto.MyWalletsDetailedDto;
import me.exrates.model.enums.CurrencyType;
import me.exrates.ngcontroller.dao.BalanceDao;
import me.exrates.ngcontroller.model.RefillPendingRequestDto;
import me.exrates.ngcontroller.model.UserBalancesDto;
import me.exrates.ngcontroller.service.BalanceService;
import me.exrates.ngcontroller.service.NgWalletService;
import me.exrates.ngcontroller.service.RefillPendingRequestService;
import me.exrates.ngcontroller.util.PagedResult;
import me.exrates.service.InputOutputService;
import me.exrates.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class BalanceServiceImpl implements BalanceService {

    private final BalanceDao balanceDao;
    private final InputOutputService inputOutputService;
    private final RefillPendingRequestService refillPendingRequestService;
    private final NgWalletService ngWalletService;
    private final UserService userService;

    @Autowired
    public BalanceServiceImpl(BalanceDao balanceDao,
                              InputOutputService inputOutputService,
                              NgWalletService ngWalletService,
                              RefillPendingRequestService refillPendingRequestService,
                              UserService userService) {
        this.balanceDao = balanceDao;
        this.inputOutputService = inputOutputService;
        this.refillPendingRequestService = refillPendingRequestService;
        this.ngWalletService = ngWalletService;
        this.userService = userService;
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
