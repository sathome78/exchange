package me.exrates.ngService;

import me.exrates.model.dto.BalanceFilterDataDto;
import me.exrates.model.dto.TransactionFilterDataDto;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.dto.onlineTableDto.MyWalletsDetailedDto;
import me.exrates.model.ngModel.RefillPendingRequestDto;
import me.exrates.model.ngModel.UserBalancesDto;
import me.exrates.model.ngUtil.PagedResult;
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

    PagedResult<MyInputOutputHistoryDto> getUserInputOutputHistory(TransactionFilterDataDto filter, Locale locale);

    List<MyInputOutputHistoryDto> getUserInputOutputHistoryExcel(TransactionFilterDataDto filter, Locale locale);

    Map<String, BigDecimal> getBalancesSumInBtcAndUsd();
}
