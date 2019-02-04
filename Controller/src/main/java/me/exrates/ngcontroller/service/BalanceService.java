package me.exrates.ngcontroller.service;

import me.exrates.model.dto.BalanceFilterDataDto;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.dto.onlineTableDto.MyWalletsDetailedDto;
import me.exrates.ngcontroller.model.RefillPendingRequestDto;
import me.exrates.ngcontroller.model.UserBalancesDto;
import me.exrates.ngcontroller.util.PagedResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
public interface BalanceService {

    List<UserBalancesDto> getUserBalances(String tikerName, String sortByCreated, Integer page, Integer limit, int userId);

    PagedResult<MyWalletsDetailedDto> getWalletsDetails(BalanceFilterDataDto filterDataDto);

    Optional<MyWalletsDetailedDto> findOne(String email, Integer currencyId);

    PagedResult<RefillPendingRequestDto> getPendingRequests(int offset, int limit, String currencyName, String email);

    PagedResult<MyInputOutputHistoryDto> getUserInputOutputHistory(String email, int limit, int offset, int currencyId,
                                                                   LocalDate dateFrom, LocalDate dateTo, Locale locale);

    PagedResult<MyInputOutputHistoryDto> getDefaultInputOutputHistory(String email, int limit, int offset, Locale locale);

    List<MyInputOutputHistoryDto> getUserInputOutputHistoryExcel(String email, int currencyId, LocalDate dateFrom,
                                                                 LocalDate dateTo, Locale locale);

    Map<String, BigDecimal> getBalancesSumInBtcAndUsd();
}
