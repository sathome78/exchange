package me.exrates.service;

import me.exrates.dao.exception.DuplicatedMerchantTransactionIdOrAttemptToRewriteException;
import me.exrates.model.InvoiceBank;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.RefillRequestAddressShortDto;
import me.exrates.model.dto.*;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.RefillAddressFilterData;
import me.exrates.model.dto.filterData.RefillFilterData;
import me.exrates.model.enums.UserRole;
import me.exrates.model.vo.InvoiceConfirmData;
import me.exrates.model.vo.WalletOperationData;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;


public class RefillMicroserviceImpl implements RefillService {

    @Override
    public Map<String, String> callRefillIRefillable(RefillRequestCreateDto request) {
        return null;
    }

    @Override
    public Map<String, Object> createRefillRequest(RefillRequestCreateDto requestCreateDto) {
        return null;
    }

    @Override
    public Optional<String> getAddressByMerchantIdAndCurrencyIdAndUserId(Integer merchantId, Integer currencyId, Integer userId) {
        return Optional.empty();
    }

    @Override
    public List<String> getListOfValidAddressByMerchantIdAndCurrency(Integer merchantId, Integer currencyId) {
        return null;
    }

    @Override
    public Integer getMerchantIdByAddressAndCurrencyAndUser(String address, Integer currencyId, Integer userId) {
        return null;
    }

    @Override
    public List<MerchantCurrency> retrieveAddressAndAdditionalParamsForRefillForMerchantCurrencies(List<MerchantCurrency> merchantCurrencies, String userEmail) {
        return null;
    }

    @Override
    public Integer createRefillRequestByFact(RefillRequestAcceptDto request) {
        return null;
    }

    @Override
    public void confirmRefillRequest(InvoiceConfirmData invoiceConfirmData, Locale locale) {

    }

    @Override
    public List<RefillRequestFlatDto> getInPendingByMerchantIdAndCurrencyIdList(Integer merchantId, Integer currencyId) {
        return null;
    }

    @Override
    public Optional<Integer> getRequestIdByAddressAndMerchantIdAndCurrencyIdAndHash(String address, Integer merchantId, Integer currencyId, String hash) {
        return Optional.empty();
    }

    @Override
    public Optional<Integer> getRequestIdByMerchantIdAndCurrencyIdAndHash(Integer merchantId, Integer currencyId, String hash) {
        return Optional.empty();
    }

    @Override
    public Optional<RefillRequestFlatDto> findFlatByAddressAndMerchantIdAndCurrencyIdAndHash(String address, Integer merchantId, Integer currencyId, String hash) {
        return Optional.empty();
    }

    @Override
    public Optional<Integer> getRequestIdReadyForAutoAcceptByAddressAndMerchantIdAndCurrencyId(String address, Integer merchantId, Integer currencyId) {
        return Optional.empty();
    }

    @Override
    public Optional<Integer> getRequestIdInPendingByAddressAndMerchantIdAndCurrencyId(String address, Integer merchantId, Integer currencyId) {
        return Optional.empty();
    }

    @Override
    public List<RefillRequestFlatDto> getInExamineByMerchantIdAndCurrencyIdList(Integer merchantId, Integer currencyId) {
        return null;
    }

    @Override
    public Optional<Integer> getUserIdByAddressAndMerchantIdAndCurrencyId(String address, Integer merchantId, Integer currencyId) {
        return Optional.empty();
    }

    @Override
    public void putOnBchExamRefillRequest(RefillRequestPutOnBchExamDto onBchExamDto) throws RefillRequestAppropriateNotFoundException {

    }

    @Override
    public void setConfirmationCollectedNumber(RefillRequestSetConfirmationsNumberDto confirmationsNumberDto) throws RefillRequestAppropriateNotFoundException {

    }

    @Override
    public void autoAcceptRefillRequest(RefillRequestAcceptDto requestAcceptDto) throws RefillRequestAppropriateNotFoundException {

    }

    @Override
    public void autoAcceptRefillEmptyRequest(RefillRequestAcceptDto requestAcceptDto) throws RefillRequestAppropriateNotFoundException {

    }

    @Override
    public void acceptRefillRequest(RefillRequestAcceptDto requestAcceptDto) {

    }

    @Override
    public void finalizeAcceptRefillRequest(Integer requestId) {

    }

    @Override
    public void declineMerchantRefillRequest(Integer requestId) {

    }

    @Override
    public RefillRequestFlatDto getFlatById(Integer id) {
        return null;
    }

    @Override
    public void revokeRefillRequest(int requestId) {

    }

    @Override
    public List<InvoiceBank> findBanksForCurrency(Integer currencyId) {
        return null;
    }

    @Override
    public Map<String, String> correctAmountAndCalculateCommission(Integer userId, BigDecimal amount, Integer currencyId, Integer merchantId, Locale locale) {
        return null;
    }

    @Override
    public Integer clearExpiredInvoices() throws Exception {
        return null;
    }

    @Override
    public DataTable<List<RefillRequestsAdminTableDto>> getRefillRequestByStatusList(List<Integer> requestStatus, DataTableParams dataTableParams, RefillFilterData refillFilterData, String authorizedUserEmail, Locale locale) {
        return null;
    }

    @Override
    public boolean checkInputRequestsLimit(int currencyId, String email) {
        return false;
    }

    @Override
    public void takeInWorkRefillRequest(int requestId, Integer requesterAdminId) {

    }

    @Override
    public void returnFromWorkRefillRequest(int requestId, Integer requesterAdminId) {

    }

    @Override
    public void declineRefillRequest(int requestId, Integer requesterAdminId, String comment) {

    }

    @Override
    public Boolean existsClosedRefillRequestForAddress(String address, Integer merchantId, Integer currencyId) {
        return null;
    }

    @Override
    public RefillRequestsAdminTableDto getRefillRequestById(Integer id, String authorizedUserEmail) {
        return null;
    }

    @Override
    public RefillRequestFlatAdditionalDataDto getAdditionalData(int requestId) {
        return null;
    }

    @Override
    public Integer manualCreateRefillRequestCrypto(RefillRequestManualDto refillDto, Locale locale) throws DuplicatedMerchantTransactionIdOrAttemptToRewriteException {
        return null;
    }

    @Override
    public Optional<RefillRequestBtcInfoDto> findRefillRequestByAddressAndMerchantTransactionId(String address, String merchantTransactionId, String merchantName, String currencyName) {
        return Optional.empty();
    }

    @Override
    public Optional<String> getLastBlockHashForMerchantAndCurrency(Integer merchantId, Integer currencyId) {
        return Optional.empty();
    }

    @Override
    public Optional<InvoiceBank> findInvoiceBankById(Integer id) {
        return Optional.empty();
    }

    @Override
    public List<String> findAllAddresses(Integer merchantId, Integer currencyId) {
        return null;
    }

    @Override
    public List<String> findAllAddresses(Integer merchantId, Integer currencyId, List<Boolean> isValidStatuses) {
        return null;
    }

    @Override
    public String getPaymentMessageForTag(String serviceBeanName, String tag, Locale locale) {
        return null;
    }

    @Override
    public List<RefillRequestFlatDto> findAllNotAcceptedByAddressAndMerchantAndCurrency(String address, Integer merchantId, Integer currencyId) {
        return null;
    }

    @Override
    public int getTxOffsetForAddress(String address) {
        return 0;
    }

    @Override
    public void updateTxOffsetForAddress(String address, Integer offset) {

    }

    @Override
    public void updateAddressNeedTransfer(String address, Integer merchantId, Integer currencyId, boolean isNeeded) {

    }

    @Override
    public List<RefillRequestAddressDto> findAllAddressesNeededToTransfer(Integer merchantId, Integer currencyId) {
        return null;
    }

    @Override
    public List<RefillRequestAddressDto> findByAddressMerchantAndCurrency(String address, Integer merchantId, Integer currencyId) {
        return null;
    }

    @Override
    public DataTable<List<RefillRequestAddressShortDto>> getAdressesShortDto(DataTableParams dataTableParams, RefillAddressFilterData filterData) {
        return null;
    }

    @Override
    public List<Integer> getUnconfirmedTxsCurrencyIdsForTokens(int parentTokenId) {
        return null;
    }

    @Override
    public List<RefillRequestFlatDto> getInExamineWithChildTokensByMerchantIdAndCurrencyIdList(int merchantId, int currencyId) {
        return null;
    }

    @Override
    public List<RefillRequestAddressDto> findAddressDtos(Integer merchantId, Integer currencyId) {
        return null;
    }

    @Override
    public void invalidateAddress(String address, Integer merchantId, Integer currencyId) {

    }

    @Override
    public List<RefillRequestFlatForReportDto> findAllByPeriodAndRoles(LocalDateTime startTime, LocalDateTime endTime, List<UserRole> roles, int requesterId) {
        return null;
    }

    @Override
    public void blockUserByFrozeTx(String address, int merchantId, int currencyId) {

    }

    @Override
    public List<RefillRequestAddressShortDto> getBlockedAddresses(int merchantId, int currencyId) {
        return null;
    }

    @Override
    public int createRequestByFactAndSetHash(RefillRequestAcceptDto requestAcceptDto) {
        return 0;
    }

    @Override
    public void setHashByReqestId(int requestId, String hash) throws DuplicatedMerchantTransactionIdOrAttemptToRewriteException {

    }

    @Override
    public void setInnerTransferHash(int requestId, String hash) {

    }

    @Override
    public Optional<RefillRequestBtcInfoDto> findRefillRequestByAddressAndMerchantIdAndCurrencyIdAndTransactionId(int merchantId, int currencyId, String txHash) {
        return Optional.empty();
    }

    @Override
    public List<RefillRequestAddressDto> findAddressDtosWithMerchantChild(int merchantId) {
        return null;
    }

    @Override
    public void processRefillRequest(WalletOperationData walletOperationData) {

    }
}
