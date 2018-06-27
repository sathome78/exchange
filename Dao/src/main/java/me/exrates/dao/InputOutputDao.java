package me.exrates.dao;

import me.exrates.model.dto.CurrencyInputOutputSummaryDto;
import me.exrates.model.dto.InputOutputCommissionSummaryDto;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.vo.PaginationWrapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

/**
 * created by ValkSam
 */
public interface InputOutputDao {

  List<MyInputOutputHistoryDto> findMyInputOutputHistoryByOperationType(
      String email,
      Integer offset,
      Integer limit,
      List<Integer> operationTypeIdList,
      Locale locale);

    PaginationWrapper<List<MyInputOutputHistoryDto>> findUnconfirmedInvoices(Integer userId, Integer currencyId, Integer limit, Integer offset);

    List<CurrencyInputOutputSummaryDto> getInputOutputSummary(LocalDateTime startTime, LocalDateTime endTime, List<Integer> userRoleIdList);

    List<InputOutputCommissionSummaryDto> getInputOutputSummaryWithCommissions(LocalDateTime startTime, LocalDateTime endTime, List<Integer> userRoleIdList);
}
