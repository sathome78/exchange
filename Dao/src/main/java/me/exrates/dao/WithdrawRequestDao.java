package me.exrates.dao;

import me.exrates.model.ClientBank;
import me.exrates.model.PagingData;
import me.exrates.model.dto.*;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.WithdrawFilterData;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.enums.invoice.InvoiceStatus;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * created by ValkSam
 */
public interface WithdrawRequestDao {

  List<WithdrawRequestFlatForReportDto> findAllByDateIntervalAndRoleAndCurrency(
      String startDate,
      String endDate,
      List<Integer> roleIdList,
      List<Integer> currencyList);

  Integer findStatusIdByRequestId(Integer withdrawRequestId);

  int create(WithdrawRequestCreateDto withdrawRequest);

  void setStatusById(Integer id, InvoiceStatus newStatus);

  Optional<WithdrawRequestFlatDto> getFlatByIdAndBlock(int id);

  Optional<WithdrawRequestFlatDto> getFlatById(int id);

  PagingData<List<WithdrawRequestFlatDto>> getPermittedFlatByStatus(List<Integer> statusIdList, Integer requesterUserId, DataTableParams dataTableParams, WithdrawFilterData withdrawFilterData);

  WithdrawRequestFlatDto getPermittedFlatById(Integer id, Integer requesterUserId);

  List<WithdrawRequestPostDto> getForPostByStatusList(Integer statusId);

  WithdrawRequestFlatAdditionalDataDto getAdditionalDataForId(int id);

  void setHolderById(Integer id, Integer holderId);

  void setInPostingStatusByStatus(Integer inPostingStatusId, List<Integer> statusIdList);

  List<ClientBank> findClientBanksForCurrency(Integer currencyId);
}
