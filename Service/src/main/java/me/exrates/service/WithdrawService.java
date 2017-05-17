package me.exrates.service;

import me.exrates.model.ClientBank;
import me.exrates.model.dto.*;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.WithdrawFilterData;
import me.exrates.model.enums.invoice.InvoiceStatus;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author ValkSam
 */
public interface WithdrawService {

  Map<String, String> createWithdrawalRequest(WithdrawRequestCreateDto requestCreateDto, Locale locale);

  void rejectError(int requestId, long timeoutInMinutes, String reasonCode);

  void rejectError(int requestId, String reasonCode);

  void rejectToReview(int requestId);

  void autoPostWithdrawalRequest(WithdrawRequestPostDto withdrawRequest);

  @Transactional
  void finalizePostWithdrawalRequest(Integer requestId);

  void postWithdrawalRequest(int requestId, Integer requesterAdminId);

  List<ClientBank> findClientBanksForCurrency(Integer currencyId);

  List<WithdrawRequestFlatForReportDto> findAllByDateIntervalAndRoleAndCurrency(String startDate, String endDate, List<Integer> roleIdList, List<Integer> currencyList);

  void setAutoWithdrawParams(MerchantCurrencyOptionsDto merchantCurrencyOptionsDto);

  MerchantCurrencyAutoParamDto getAutoWithdrawParamsByMerchantAndCurrency(Integer merchantId, Integer currencyId);

  DataTable<List<WithdrawRequestsAdminTableDto>> getWithdrawRequestByStatusList(List<Integer> requestStatus, DataTableParams dataTableParams, WithdrawFilterData withdrawFilterData, String authorizedUserEmail, Locale locale);

  WithdrawRequestsAdminTableDto getWithdrawRequestById(Integer id, String authorizedUserEmail);

  void revokeWithdrawalRequest(int requestId);

  void takeInWorkWithdrawalRequest(int requestId, Integer requesterAdminId);

  void returnFromWorkWithdrawalRequest(int requestId, Integer requesterAdminId);

  void declineWithdrawalRequest(int requestId, Integer requesterAdminId, String comment);

  void confirmWithdrawalRequest(int requestId, Integer requesterAdminId);

  void setAllAvailableInPostingStatus();

  List<WithdrawRequestPostDto> dirtyReadForPostByStatusList(InvoiceStatus status);

  Map<String, String> correctAmountAndCalculateCommissionPreliminarily(Integer userId, BigDecimal amount, Integer currencyId, Integer merchantId, Locale locale);

  boolean checkOutputRequestsLimit(int merchantId, String email);

    @Transactional(readOnly = true)
    List<WithdrawRequestFlatDto> getRequestsByMerchantIdAndStatus(int merchantId, List<Integer> statuses);
}
