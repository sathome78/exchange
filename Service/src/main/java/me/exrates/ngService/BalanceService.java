package me.exrates.ngService;

import me.exrates.model.dto.BalanceFilterDataDto;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.dto.onlineTableDto.MyWalletsDetailedDto;
import me.exrates.model.ngModel.RefillPendingRequestDto;
import me.exrates.model.ngModel.UserBalancesDto;
import me.exrates.model.ngUtil.PagedResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    PagedResult<MyInputOutputHistoryDto> getUserInputOutputHistory(String userEmail, Integer currencyId, String currencyName,
                                                                   LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo,
                                                                   Integer limit, Integer offset, Locale locale);

    List<MyInputOutputHistoryDto> getUserInputOutputHistoryExcel(String userEmail, Integer currencyId, String currencyName,
                                                                 LocalDateTime dateTimeFrom, LocalDateTime dateTimeTo,
                                                                 Integer limit, Integer offset, Locale locale);

    Map<String, BigDecimal> getBalancesSumInBtcAndUsd();

    BigDecimal getActiveBalanceByCurrencyNameAndEmail(String email, String currencyName);
}
