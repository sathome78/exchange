package me.exrates.ngcontroller.service;

import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.dto.onlineTableDto.MyWalletsDetailedDto;
import me.exrates.ngcontroller.model.RefillPendingRequestDto;
import me.exrates.ngcontroller.model.UserBalancesDto;
import me.exrates.ngcontroller.util.PagedResult;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Service
public interface BalanceService {


    List<UserBalancesDto> getUserBalances(String tikerName, String sortByCreated, Integer page, Integer limit, int userId);

    PagedResult<MyWalletsDetailedDto> getWalletsDetails(int offset, int limit, String email);

    PagedResult<RefillPendingRequestDto> getPendingRequests(int offset, int limit, String email);

    PagedResult<MyInputOutputHistoryDto> getUserInputOutputHistory(int limit, int offset, int currencyId,
                                                                   LocalDate dateFrom, LocalDate dateTo, Locale locale);
}
