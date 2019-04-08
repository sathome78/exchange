package me.exrates.service;

import me.exrates.model.ClientBank;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.dto.*;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.WithdrawFilterData;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.invoice.InvoiceStatus;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

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

  void postWithdrawalRequest(int requestId, Integer requesterAdminId, String txHash);

  List<ClientBank> findClientBanksForCurrency(Integer currencyId);

  void setAutoWithdrawParams(MerchantCurrencyOptionsDto merchantCurrencyOptionsDto);

  MerchantCurrencyAutoParamDto getAutoWithdrawParamsByMerchantAndCurrency(Integer merchantId, Integer currencyId);

  List<MerchantCurrency> retrieveAddressAndAdditionalParamsForWithdrawForMerchantCurrencies(List<MerchantCurrency> merchantCurrencies);

  DataTable<List<WithdrawRequestsAdminTableDto>> getWithdrawRequestByStatusList(List<Integer> requestStatus, DataTableParams dataTableParams, WithdrawFilterData withdrawFilterData, String authorizedUserEmail, Locale locale);

  WithdrawRequestsAdminTableDto getWithdrawRequestById(Integer id, String authorizedUserEmail);

  WithdrawRequestFlatDto getFlatById(Integer id);

  void revokeWithdrawalRequest(int requestId);

  void takeInWorkWithdrawalRequest(int requestId, Integer requesterAdminId);

  void returnFromWorkWithdrawalRequest(int requestId, Integer requesterAdminId);

  void declineWithdrawalRequest(int requestId, Integer requesterAdminId, String comment);

  void confirmWithdrawalRequest(int requestId, Integer requesterAdminId);

  void setAllAvailableInPostingStatus();

  List<WithdrawRequestPostDto> dirtyReadForPostByStatusList(InvoiceStatus status);

  Map<String, String> correctAmountAndCalculateCommissionPreliminarily(Integer userId, BigDecimal amount, Integer currencyId, Integer merchantId, Locale locale, String destinationTag);

  boolean checkOutputRequestsLimit(int merchantId, String email);

  List<Integer> getWithdrawalStatistic(String startDate, String endDate);

  @Transactional(readOnly = true)
  List<WithdrawRequestFlatDto> getRequestsByMerchantIdAndStatus(int merchantId, List<Integer> statuses);

  @Transactional(readOnly = true)
  Optional<Integer> getRequestIdByHashAndMerchantId(String hash, int merchantId);

  @Transactional(readOnly = true)
  WithdrawRequestInfoDto getWithdrawalInfo(Integer id, Locale locale);

  List<WithdrawRequestFlatForReportDto> findAllByPeriodAndRoles(LocalDateTime startTime,
                                                                LocalDateTime endTime,
                                                                List<UserRole> userRoles,
                                                                int requesterId);
  void setAdditionalData(MerchantCurrency merchantCurrency);

  BigDecimal getLeftOutputRequestsSum(int id, String email);
}
