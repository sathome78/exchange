package me.exrates.ngcontroller.service.impl;

import me.exrates.model.dto.onlineTableDto.MyWalletsDetailedDto;
import me.exrates.ngcontroller.dao.BalanceDao;
import me.exrates.ngcontroller.model.RefillPendingRequestDto;
import me.exrates.ngcontroller.model.UserBalancesDto;
import me.exrates.ngcontroller.service.BalanceService;
import me.exrates.ngcontroller.service.NgWalletService;
import me.exrates.ngcontroller.service.RefillPendingRequestService;
import me.exrates.ngcontroller.util.PagedResult;
import me.exrates.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Service
public class BalanceServiceImpl implements BalanceService {

    private final BalanceDao balanceDao;
    private final RefillPendingRequestService refillPendingRequestService;
    private final NgWalletService ngWalletService;
    private final UserService userService;

    @Autowired
    public BalanceServiceImpl(BalanceDao balanceDao,
                              RefillPendingRequestService refillPendingRequestService,
                              NgWalletService ngWalletService,
                              UserService userService) {
        this.balanceDao = balanceDao;
        this.refillPendingRequestService = refillPendingRequestService;
        this.ngWalletService = ngWalletService;
        this.userService = userService;
    }

    @Override
    public List<UserBalancesDto> getUserBalances(String tikerName, String sortByCreated, Integer page, Integer limit, int userId) {
        return balanceDao.getUserBalances(tikerName, sortByCreated, page, limit,userId);
    }

    @Override
    public PagedResult<MyWalletsDetailedDto> getWalletsDetails(int offset, int limit, String email) {
        List<MyWalletsDetailedDto> details = ngWalletService.getAllWalletsForUserDetailed(email, Locale.ENGLISH);
        return getSafeSubList(details, offset, limit);
    }

    @Override
    public PagedResult<RefillPendingRequestDto> getPendingRequests(int offset, int limit, String email) {
        List<RefillPendingRequestDto> requests =
                refillPendingRequestService.getPendingRefillRequests(userService.getIdByEmail(email));
        return getSafeSubList(requests, offset, limit);
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
