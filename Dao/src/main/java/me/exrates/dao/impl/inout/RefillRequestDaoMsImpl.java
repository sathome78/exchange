package me.exrates.dao.impl.inout;

import me.exrates.dao.RefillRequestDao;
import me.exrates.dao.exception.DuplicatedMerchantTransactionIdOrAttemptToRewriteException;
import me.exrates.model.InvoiceBank;
import me.exrates.model.PagingData;
import me.exrates.model.RefillRequestAddressShortDto;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.model.dto.*;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.RefillAddressFilterData;
import me.exrates.model.dto.filterData.RefillFilterData;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.vo.InvoiceConfirmData;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Conditional(MicroserviceConditional.class)
public class RefillRequestDaoMsImpl implements RefillRequestDao {

    @Override
    public Optional<Integer> findIdByAddressAndMerchantIdAndCurrencyIdAndStatusId(String address, Integer merchantId, Integer currencyId, List<Integer> statusList) {
        return Optional.empty();
    }

    @Override
    public Optional<Integer> findIdWithoutConfirmationsByAddressAndMerchantIdAndCurrencyIdAndStatusId(String address, Integer merchantId, Integer currencyId, List<Integer> statusList) {
        return Optional.empty();
    }

    @Override
    public Optional<Integer> findIdByAddressAndMerchantIdAndCurrencyIdAndHash(String address, Integer merchantId, Integer currencyId, String hash) {
        return Optional.empty();
    }

    @Override
    public Optional<Integer> findIdByMerchantIdAndCurrencyIdAndHash(Integer merchantId, Integer currencyId, String hash) {
        return Optional.empty();
    }

    @Override
    public Optional<RefillRequestFlatDto> findFlatByAddressAndMerchantIdAndCurrencyIdAndHash(String address, Integer merchantId, Integer currencyId, String hash) {
        return Optional.empty();
    }

    @Override
    public List<RefillRequestFlatDto> findAllWithoutConfirmationsByMerchantIdAndCurrencyIdAndStatusId(Integer merchantId, Integer currencyId, List<Integer> statusList) {
        return null;
    }

    @Override
    public List<RefillRequestFlatDto> findAllWithConfirmationsByMerchantIdAndCurrencyIdAndStatusId(Integer merchantId, Integer currencyId, List<Integer> statusIdList) {
        return null;
    }

    @Override
    public Integer getCountByMerchantIdAndCurrencyIdAndAddressAndStatusId(String address, Integer merchantId, Integer currencyId, List<Integer> statusList) {
        return null;
    }

    @Override
    public Optional<Integer> findUserIdByAddressAndMerchantIdAndCurrencyId(String address, Integer merchantId, Integer currencyId) {
        return Optional.empty();
    }

    @Override
    public Optional<Integer> create(RefillRequestCreateDto request) {
        return Optional.empty();
    }

    @Override
    public Optional<String> findLastValidAddressByMerchantIdAndCurrencyIdAndUserId(Integer merchantId, Integer currencyId, Integer userId) {
        return Optional.empty();
    }

    @Override
    public List<String> getListOfValidAddressByMerchantIdAndCurrency(Integer merchantId, Integer currencyId) {
        return null;
    }

    @Override
    public void setStatusById(Integer id, InvoiceStatus newStatus) {

    }

    @Override
    public void setStatusAndConfirmationDataById(Integer id, InvoiceStatus newStatus, InvoiceConfirmData invoiceConfirmData) {

    }

    @Override
    public void setMerchantRequestSignById(Integer id, String sign) {

    }

    @Override
    public List<InvoiceBank> findInvoiceBankListByCurrency(Integer currencyId) {
        return null;
    }

    @Override
    public Optional<InvoiceBank> findInvoiceBankById(Integer id) {
        return Optional.empty();
    }

    @Override
    public Optional<LocalDateTime> getAndBlockByIntervalAndStatus(Integer merchantId, Integer currencyId, Integer intervalHours, List<Integer> statusIdList) {
        return Optional.empty();
    }

    @Override
    public Optional<RefillRequestFlatDto> getFlatByIdAndBlock(Integer id) {
        return Optional.empty();
    }

    @Override
    public Optional<RefillRequestFlatDto> getFlatById(Integer id) {
        return Optional.empty();
    }

    @Override
    public void setNewStatusByDateIntervalAndStatus(Integer merchantId, Integer currencyId, LocalDateTime boundDate, Integer intervalHours, Integer newStatusId, List<Integer> statusIdList) {

    }

    @Override
    public List<OperationUserDto> findListByMerchantIdAndCurrencyIdStatusChangedAtDate(Integer merchantId, Integer currencyId, Integer statusId, LocalDateTime dateWhenChanged) {
        return null;
    }

    @Override
    public PagingData<List<RefillRequestFlatDto>> getPermittedFlatByStatus(List<Integer> statusIdList, Integer requesterUserId, DataTableParams dataTableParams, RefillFilterData refillFilterData) {
        return null;
    }

    @Override
    public RefillRequestFlatDto getPermittedFlatById(Integer id, Integer requesterUserId) {
        return null;
    }

    @Override
    public RefillRequestFlatAdditionalDataDto getAdditionalDataForId(int id) {
        return null;
    }

    @Override
    public void setHolderById(Integer id, Integer holderId) {

    }

    @Override
    public void setRemarkById(Integer id, String remark) {

    }

    @Override
    public void setMerchantTransactionIdById(Integer id, String merchantTransactionId) throws DuplicatedMerchantTransactionIdOrAttemptToRewriteException {

    }

    @Override
    public boolean checkInputRequests(int currencyId, String email) {
        return false;
    }

    @Override
    public Integer findConfirmationsNumberByRequestId(Integer requestId) {
        return null;
    }

    @Override
    public void setConfirmationsNumberByRequestId(Integer requestId, BigDecimal amount, Integer confirmations, String blockhash) {

    }

    @Override
    public Optional<Integer> findUserIdById(Integer requestId) {
        return Optional.empty();
    }

    @Override
    public Optional<RefillRequestBtcInfoDto> findRefillRequestByAddressAndMerchantTransactionId(String address, String merchantTransactionId, Integer merchantId, Integer currencyId) {
        return Optional.empty();
    }

    @Override
    public Optional<String> getLastBlockHashForMerchantAndCurrency(Integer merchantId, Integer currencyId) {
        return Optional.empty();
    }

    @Override
    public List<String> findAllAddresses(Integer merchantId, Integer currencyId, List<Boolean> isValidStatuses) {
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
    public boolean isToken(Integer merchantId) {
        return false;
    }

    @Override
    public List<Map<String, Integer>> getTokenMerchants(Integer merchantId) {
        return null;
    }

    @Override
    public Integer findMerchantIdByAddressAndCurrencyAndUser(String address, Integer currencyId, Integer userId) {
        return null;
    }

    @Override
    public void updateAddressNeedTransfer(String address, Integer merchantId, Integer currencyId, boolean isNeeded) {

    }

    @Override
    public void invalidateAddress(String address, Integer merchantId, Integer currencyId) {

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
    public List<RefillRequestAddressDto> findAddressDtosByMerchantAndCurrency(Integer merchantId, Integer currencyId) {
        return null;
    }

    @Override
    public PagingData<List<RefillRequestAddressShortDto>> getAddresses(DataTableParams dataTableParams, RefillAddressFilterData data) {
        return null;
    }

    @Override
    public List<Integer> getUnconfirmedTxsCurrencyIdsForTokens(int parentTokenId) {
        return null;
    }

    @Override
    public List<RefillRequestFlatDto> findAllWithChildTokensWithConfirmationsByMerchantIdAndCurrencyIdAndStatusId(int merchantId, int currencyId, List<Integer> collect) {
        return null;
    }

    @Override
    public List<RefillRequestFlatForReportDto> findAllByPeriodAndRoles(LocalDateTime startTime, LocalDateTime endTime, List<UserRole> roles, int requesterId) {
        return null;
    }

    @Override
    public List<RefillRequestAddressDto> findByAddress(String address) {
        return null;
    }

    @Override
    public boolean setAddressBlocked(String address, int merchantId, int currencyId, boolean blocked) {
        return false;
    }

    @Override
    public List<RefillRequestAddressShortDto> getBlockedAddresses(int merchantId, int currencyId) {
        return null;
    }

    @Override
    public void setInnerTransferHash(int requestId, String hash) {

    }

    @Override
    public Optional<RefillRequestBtcInfoDto> findRefillRequestByAddressAndMerchantIdAndCurrencyIdAndTransactionId(int merchantId, int currencyId, String txHash) {
        return Optional.empty();
    }

    @Override
    public List<RefillRequestAddressDto> findAllAddressesByMerchantWithChilds(int merchantId) {
        return null;
    }
}
