package me.exrates.service;

import me.exrates.dao.exception.DuplicatedMerchantTransactionIdOrAttemptToRewriteException;
import me.exrates.model.InvoiceBank;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.RefillRequestAddressShortDto;
import me.exrates.model.User;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestAddressDto;
import me.exrates.model.dto.RefillRequestBtcInfoDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.RefillRequestFlatAdditionalDataDto;
import me.exrates.model.dto.RefillRequestFlatDto;
import me.exrates.model.dto.RefillRequestFlatForReportDto;
import me.exrates.model.dto.RefillRequestManualDto;
import me.exrates.model.dto.RefillRequestPutOnBchExamDto;
import me.exrates.model.dto.RefillRequestSetConfirmationsNumberDto;
import me.exrates.model.dto.RefillRequestsAdminTableDto;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.RefillAddressFilterData;
import me.exrates.model.dto.filterData.RefillFilterData;
import me.exrates.model.dto.ngDto.RefillOnConfirmationDto;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.invoice.RefillStatusEnum;
import me.exrates.model.vo.InvoiceConfirmData;
import me.exrates.model.vo.WalletOperationMsDto;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * @author ValkSam
 */
public interface RefillService {

    Map<String, String> callRefillIRefillable(RefillRequestCreateDto request);

    Map<String, Object> createRefillRequest(RefillRequestCreateDto requestCreateDto);

    Optional<String> getAddressByMerchantIdAndCurrencyIdAndUserId(Integer merchantId, Integer currencyId, Integer userId);

    List<String> getListOfValidAddressByMerchantIdAndCurrency(Integer merchantId, Integer currencyId);

    @Transactional(readOnly = true)
    Integer getMerchantIdByAddressAndCurrencyAndUser(String address, Integer currencyId, Integer userId);

    List<MerchantCurrency> retrieveAddressAndAdditionalParamsForRefillForMerchantCurrencies(List<MerchantCurrency> merchantCurrencies, String userEmail);

    void setNeedKyc(MerchantCurrency merchantCurrency, User user);

    Integer createRefillRequestByFact(RefillRequestAcceptDto request);

    Integer createRefillRequestByFact(RefillRequestAcceptDto request, int userId, int commissionId, RefillStatusEnum statusEnum);

    void confirmRefillRequest(InvoiceConfirmData invoiceConfirmData, Locale locale);

    List<RefillRequestFlatDto> getInPendingByMerchantIdAndCurrencyIdList(Integer merchantId, Integer currencyId);

    Optional<Integer> getRequestIdByAddressAndMerchantIdAndCurrencyIdAndHash(
            String address,
            Integer merchantId,
            Integer currencyId,
            String hash);

    Optional<Integer> getRequestIdByMerchantIdAndCurrencyIdAndHash(
            Integer merchantId,
            Integer currencyId,
            String hash);

    Optional<RefillRequestFlatDto> findFlatByAddressAndMerchantIdAndCurrencyIdAndHash(
            String address, Integer merchantId,
            Integer currencyId,
            String hash);

    Optional<Integer> getRequestIdReadyForAutoAcceptByAddressAndMerchantIdAndCurrencyId(String address, Integer merchantId, Integer currencyId);

    Optional<Integer> getRequestIdInPendingByAddressAndMerchantIdAndCurrencyId(
            String address,
            Integer merchantId,
            Integer currencyId);

    List<RefillRequestFlatDto> getInExamineByMerchantIdAndCurrencyIdList(Integer merchantId, Integer currencyId);

    Optional<Integer> getUserIdByAddressAndMerchantIdAndCurrencyId(String address, Integer merchantId, Integer currencyId);

    void putOnBchExamRefillRequest(RefillRequestPutOnBchExamDto onBchExamDto) throws RefillRequestAppropriateNotFoundException;

    void setConfirmationCollectedNumber(RefillRequestSetConfirmationsNumberDto confirmationsNumberDto) throws RefillRequestAppropriateNotFoundException;

    @Transactional
    Integer createAndAutoAcceptRefillRequest(RefillRequestAcceptDto requestAcceptDto);

    Integer createAndAutoAcceptRefillRequest(RefillRequestAcceptDto requestAcceptDto, int userId);

    void autoAcceptRefillRequest(RefillRequestAcceptDto requestAcceptDto) throws RefillRequestAppropriateNotFoundException;

    void autoAcceptRefillEmptyRequest(RefillRequestAcceptDto requestAcceptDto) throws RefillRequestAppropriateNotFoundException;

    void acceptRefillRequest(RefillRequestAcceptDto requestAcceptDto);

    void finalizeAcceptRefillRequest(Integer requestId);

    @Transactional
    void declineMerchantRefillRequest(Integer requestId);

    RefillRequestFlatDto getFlatById(Integer id);

    void revokeRefillRequest(int requestId);

    List<InvoiceBank> findBanksForCurrency(Integer currencyId);

    Map<String, String> correctAmountAndCalculateCommission(Integer userId, BigDecimal amount, Integer currencyId, Integer merchantId, Locale locale);

    Integer clearExpiredInvoices() throws Exception;

    DataTable<List<RefillRequestsAdminTableDto>> getRefillRequestByStatusList(List<Integer> requestStatus, DataTableParams dataTableParams, RefillFilterData refillFilterData, String authorizedUserEmail, Locale locale);

    boolean checkInputRequestsLimit(int currencyId, String email);

    void takeInWorkRefillRequest(int requestId, Integer requesterAdminId);

    void returnFromWorkRefillRequest(int requestId, Integer requesterAdminId);

    void declineRefillRequest(int requestId, Integer requesterAdminId, String comment);

    Boolean existsClosedRefillRequestForAddress(String address, Integer merchantId, Integer currencyId);

    RefillRequestsAdminTableDto getRefillRequestById(Integer id, String authorizedUserEmail);

    RefillRequestFlatAdditionalDataDto getAdditionalData(int requestId);

    @Transactional
    Integer manualCreateRefillRequestCrypto(RefillRequestManualDto refillDto, Locale locale) throws DuplicatedMerchantTransactionIdOrAttemptToRewriteException;

    Optional<RefillRequestBtcInfoDto> findRefillRequestByAddressAndMerchantTransactionId(String address,
                                                                                         String merchantTransactionId,
                                                                                         String merchantName,
                                                                                         String currencyName);

    Optional<String> getLastBlockHashForMerchantAndCurrency(Integer merchantId, Integer currencyId);

    Optional<InvoiceBank> findInvoiceBankById(Integer id);

    List<String> findAllAddresses(Integer merchantId, Integer currencyId);

    List<String> findAllAddresses(Integer merchantId, Integer currencyId, List<Boolean> isValidStatuses);

    List<RefillRequestFlatDto> findAllNotAcceptedByAddressAndMerchantAndCurrency(String address, Integer merchantId, Integer currencyId);

    int getTxOffsetForAddress(String address);

    void updateTxOffsetForAddress(String address, Integer offset);

    void updateAddressNeedTransfer(String address, Integer merchantId, Integer currencyId, boolean isNeeded);

    List<RefillRequestAddressDto> findAllAddressesNeededToTransfer(Integer merchantId, Integer currencyId);

    List<RefillRequestAddressDto> findByAddressMerchantAndCurrency(String address, Integer merchantId, Integer currencyId);

    DataTable<List<RefillRequestAddressShortDto>> getAdressesShortDto(DataTableParams dataTableParams, RefillAddressFilterData filterData);

    List<Integer> getUnconfirmedTxsCurrencyIdsForTokens(int parentTokenId);

    List<RefillRequestFlatDto> getInExamineWithChildTokensByMerchantIdAndCurrencyIdList(int merchantId, int currencyId);

    List<RefillRequestAddressDto> findAddressDtos(Integer merchantId, Integer currencyId);

    void invalidateAddress(String address, Integer merchantId, Integer currencyId);

    List<RefillRequestFlatForReportDto> findAllByPeriodAndRoles(LocalDateTime startTime,
                                                                LocalDateTime endTime,
                                                                List<UserRole> roles,
                                                                int requesterId);

    String getUsernameByAddressAndCurrencyIdAndMerchantId(String address, int currencyId, int merchantId);

    String getUserGAByRequestId(int requestId);

    Integer getRequestId(RefillRequestAcceptDto requestAcceptDto) throws RefillRequestAppropriateNotFoundException;

    void blockUserByFrozeTx(String address, int merchantId, int currencyId);

    List<RefillRequestAddressShortDto> getBlockedAddresses(int merchantId, int currencyId);

    @Transactional
    int createRequestByFactAndSetHash(RefillRequestAcceptDto requestAcceptDto);

    @Transactional
    void setHashByRequestId(int requestId, String hash) throws DuplicatedMerchantTransactionIdOrAttemptToRewriteException;

    @Transactional
    void setInnerTransferHash(int requestId, String hash);

    List<RefillRequestAddressDto> findAddressDtosWithMerchantChild(int merchantId);

    List<RefillOnConfirmationDto> getOnConfirmationRefills(String email, int currencyId);

    Integer findFlatByUserIdAndMerchantIdAndCurrencyId(int userId, int id, int currencyId);

    default void processRefillRequest(WalletOperationMsDto walletOperationMsDto) {
        throw new NotImplementedException();
    }

    String getPrivKeyByAddress(String address);

    boolean setPropertyNeedTransfer(int userId, int currencyId, int merchantId, String address, Boolean needTransfer);

    boolean changeRefillRequestStatusToOnPending(int id);

    Map<String, String> correctAmountAndCalculateCommissionPreliminarily(Integer userId, BigDecimal amount,
                                                                         Integer currencyId, Integer merchantId,
                                                                         Locale locale, String destinationTag);

}
