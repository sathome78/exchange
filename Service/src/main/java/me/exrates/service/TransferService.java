package me.exrates.service;

import me.exrates.model.MerchantCurrency;
import me.exrates.model.dto.TransferDto;
import me.exrates.model.dto.TransferRequestCreateDto;
import me.exrates.model.dto.TransferRequestFlatDto;
import me.exrates.model.dto.VoucherAdminTableDto;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.VoucherFilterData;
import me.exrates.model.enums.invoice.InvoiceActionTypeEnum;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Created by ValkSam on 02.06.2017.
 */
public interface TransferService {
  Map<String, Object> createTransferRequest(TransferRequestCreateDto request);

  List<MerchantCurrency> retrieveAdditionalParamsForWithdrawForMerchantCurrencies(List<MerchantCurrency> merchantCurrencies);

  void revokeByUser(int requestId, Principal principal);

  void revokeByAdmin(int requestId, Principal principal);

  List<TransferRequestFlatDto> getRequestsByMerchantIdAndStatus(int merchantId, List<Integer> statuses);

  TransferRequestFlatDto getFlatById(Integer id);

  Map<String, String> correctAmountAndCalculateCommissionPreliminarily(Integer userId, BigDecimal amount, Integer currencyId, Integer merchantId, Locale locale);

  Optional<TransferRequestFlatDto> getByHashAndStatus(String code, Integer requiredStatus, boolean block);

  boolean checkRequest(TransferRequestFlatDto transferRequestFlatDto, String userEmail);

  TransferDto performTransfer(TransferRequestFlatDto transferRequestFlatDto, Locale locale, InvoiceActionTypeEnum action);

  String getUserEmailByTrnasferId(int id);

  @Transactional
  DataTable<List<VoucherAdminTableDto>> getAdminVouchersList(
          DataTableParams dataTableParams,
          VoucherFilterData withdrawFilterData,
          String authorizedUserEmail,
          Locale locale);

    String getHash(Integer id, Principal principal);

    void revokeTransferRequest(Integer requestId);
}
