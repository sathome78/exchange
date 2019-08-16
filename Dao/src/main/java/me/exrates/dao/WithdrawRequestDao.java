package me.exrates.dao;

import me.exrates.model.ClientBank;
import me.exrates.model.PagingData;
import me.exrates.model.dto.WithdrawRequestCreateDto;
import me.exrates.model.dto.WithdrawRequestFlatAdditionalDataDto;
import me.exrates.model.dto.WithdrawRequestFlatDto;
import me.exrates.model.dto.WithdrawRequestFlatForReportDto;
import me.exrates.model.dto.WithdrawRequestInfoDto;
import me.exrates.model.dto.WithdrawRequestPostDto;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.WithdrawFilterData;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.invoice.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * created by ValkSam
 */
public interface WithdrawRequestDao {

    Integer findStatusIdByRequestId(Integer withdrawRequestId);

    int create(WithdrawRequestCreateDto withdrawRequest);

    void setStatusById(Integer id, InvoiceStatus newStatus);

    void setHashAndParamsById(Integer id, Map<String, String> hash);

    Optional<WithdrawRequestFlatDto> getFlatByIdAndBlock(int id);

    Optional<WithdrawRequestFlatDto> getFlatById(int id);

    PagingData<List<WithdrawRequestFlatDto>> getPermittedFlatByStatus(List<Integer> statusIdList, Integer requesterUserId, DataTableParams dataTableParams, WithdrawFilterData withdrawFilterData);

    WithdrawRequestFlatDto getPermittedFlatById(Integer id, Integer requesterUserId);

    List<WithdrawRequestPostDto> getForPostByStatusList(Integer statusId);

    WithdrawRequestFlatAdditionalDataDto getAdditionalDataForId(int id);

    void setHolderById(Integer id, Integer holderId);

    void setInPostingStatusByStatus(Integer inPostingStatusId, List<Integer> statusIdList);

    List<ClientBank> findClientBanksForCurrency(Integer currencyId);

    boolean checkOutputRequests(int currencyId, String email);

    boolean checkOutputMaxSum(int currencyId, String email, BigDecimal newSum);

    Optional<Integer> findUserIdById(Integer requestId);

    Optional<Integer> getIdByHashAndMerchantId(String hash, Integer merchantId);

    List<WithdrawRequestFlatDto> findRequestsByStatusAndMerchant(Integer merchantId, List<Integer> statusId);

    List<Integer> getWithdrawalStatistic(String startDate, String endDate);

    WithdrawRequestInfoDto findWithdrawInfo(Integer id);

    List<WithdrawRequestFlatForReportDto> findAllByPeriodAndRoles(LocalDateTime startTime, 
                                                                  LocalDateTime endTime, 
                                                                  List<UserRole> userRoles, 
                                                                  int requesterId);

    BigDecimal getLeftOutputRequestsCount(int currencyId, String email);

    BigDecimal getDailyWithdrawalSumByCurrency(String email, Integer currencyId);
}
