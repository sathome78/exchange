package me.exrates.service;

import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.dto.CurrencyInputOutputSummaryDto;
import me.exrates.model.dto.InOutReportDto;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.enums.invoice.InvoiceOperationPermission;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.vo.CacheData;
import me.exrates.model.vo.PaginationWrapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * @author ValkSam
 */
public interface InputOutputService {
    List<MyInputOutputHistoryDto> getMyInputOutputHistory(CacheData cacheData, String email, Integer offset, Integer limit, Locale locale);

    List<MyInputOutputHistoryDto> getMyInputOutputHistory(String email, Integer offset, Integer limit, Locale locale);

    PaginationWrapper<List<MyInputOutputHistoryDto>> findUnconfirmedInvoices(String userEmail, String currencyName, Integer limit, Integer offset, Locale locale);

    List<Map<String, Object>> generateAndGetButtonsSet(InvoiceStatus status, InvoiceOperationPermission permittedOperation, boolean authorisedUserIsHolder, Locale locale);

    Optional<CreditsOperation> prepareCreditsOperation(Payment payment, String userEmail, Locale locale);

    List<CurrencyInputOutputSummaryDto> getInputOutputSummary(LocalDateTime startTime, LocalDateTime endTime,
                                                              List<Integer> userRoleIdList);

    List<InOutReportDto> getInputOutputSummaryWithCommissions(LocalDateTime startTime, LocalDateTime endTime,
                                                              List<Integer> userRoleIdList);

    Integer getUserInputOutputHistoryCount(String userEmail, Integer currencyId, String currencyName, LocalDateTime dateTimeFrom,
                                           LocalDateTime dateTimeTo, Locale locale);

    List<MyInputOutputHistoryDto> getUserInputOutputHistory(String userEmail, Integer currencyId, String currencyName, LocalDateTime dateTimeFrom,
                                                            LocalDateTime dateTimeTo, Integer limit, Integer offset, Locale locale);
}
