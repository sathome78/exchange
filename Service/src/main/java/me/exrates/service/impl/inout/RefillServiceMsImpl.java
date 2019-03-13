package me.exrates.service.impl.inout;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.exrates.dao.exception.DuplicatedMerchantTransactionIdOrAttemptToRewriteException;
import me.exrates.model.*;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.model.dto.*;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.RefillAddressFilterData;
import me.exrates.model.dto.filterData.RefillFilterData;
import me.exrates.model.dto.ngDto.RefillOnConfirmationDto;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.WalletTransferStatus;
import me.exrates.model.enums.invoice.RefillStatusEnum;
import me.exrates.model.vo.InvoiceConfirmData;
import me.exrates.model.vo.WalletOperationData;
import me.exrates.model.vo.WalletOperationMsDto;
import me.exrates.service.CompanyWalletService;
import me.exrates.service.RefillService;
import me.exrates.service.WalletService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.exception.RefillRequestRevokeException;
import me.exrates.service.properties.InOutProperties;
import me.exrates.service.util.RequestUtil;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static me.exrates.model.enums.WalletTransferStatus.SUCCESS;

@Service
@Conditional(MicroserviceConditional.class)
@RequiredArgsConstructor
public class RefillServiceMsImpl implements RefillService {

    private static final String GET_ADDRESS_BY_MERCHANT_ID_AND_CURRENCY_ID_AND_USER_ID = "/api/getAddressByMerchantIdAndCurrencyIdAndUserId";
    private static final String CHECK_INPUT_REQUESTS_LIMIT = "/api/checkInputRequestsLimit";
    private static final String CREATE_REFILL_REQUEST = "/api/createRefillRequest";
    private final InOutProperties properties;
    private final ObjectMapper objectMapper;
    private final RestTemplate template;
    private final RequestUtil requestUtil;
    private final WalletService walletService;
    private final CompanyWalletService companyWalletService;

    @Override
    public Map<String, String> callRefillIRefillable(RefillRequestCreateDto request) {
        return null;
    }

    @Override
    @SneakyThrows
    public Map<String, Object> createRefillRequest(RefillRequestCreateDto requestCreateDto) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + CREATE_REFILL_REQUEST);
        HttpEntity<?> entity = new HttpEntity<>(objectMapper.writeValueAsString(requestCreateDto), requestUtil.prepareHeaders(requestCreateDto.getUserId()));
        ResponseEntity<Map<String, Object>> response = template.exchange(
                builder.toUriString(),
                HttpMethod.POST,
                entity, new ParameterizedTypeReference<Map<String, Object>>() {});

        return response.getBody();
    }

    @Override
    public Optional<String> getAddressByMerchantIdAndCurrencyIdAndUserId(Integer merchantId, Integer currencyId, Integer userId) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + GET_ADDRESS_BY_MERCHANT_ID_AND_CURRENCY_ID_AND_USER_ID)
                .queryParam("currency_id", currencyId)
                .queryParam("merchant_id", merchantId);

        HttpEntity<?> entity = new HttpEntity<>(requestUtil.prepareHeaders(userId));
        ResponseEntity<Optional<String>> response = template.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity, new ParameterizedTypeReference<Optional<String>>() {});
        return response.getBody();
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
    public Integer createRefillRequestByFact(RefillRequestAcceptDto request, int userId, int commissionId, RefillStatusEnum statusEnum) {
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
    public Integer createAndAutoAcceptRefillRequest(RefillRequestAcceptDto requestAcceptDto) {
        return null;
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
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + CHECK_INPUT_REQUESTS_LIMIT)
                .queryParam("currency_id", currencyId);
        HttpEntity<?> entity = new HttpEntity<>(requestUtil.prepareHeaders(email));
        ResponseEntity<Boolean> response = template.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity, Boolean.class);

        return response.getBody();
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
    public String getUsernameByAddressAndCurrencyIdAndMerchantId(String address, int currencyId, int merchantId) {
        return null;
    }

    @Override
    public String getUsernameByRequestId(int requestId) {
        return null;
    }

    @Override
    public Integer getRequestId(RefillRequestAcceptDto requestAcceptDto) throws RefillRequestAppropriateNotFoundException {
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
    public void setHashByRequestId(int requestId, String hash) throws DuplicatedMerchantTransactionIdOrAttemptToRewriteException {

    }

    @Override
    public void setInnerTransferHash(int requestId, String hash) {

    }

    @Override
    public List<RefillRequestAddressDto> findAddressDtosWithMerchantChild(int merchantId) {
        return null;
    }

    @Override
    public List<RefillOnConfirmationDto> getOnConfirmationRefills(String email, int currencyId) {
        return null;
    }

    @Override
    public Integer findFlatByUserIdAndMerchantIdAndCurrencyId(int userId, int id, int currencyId) {
        return null;
    }

    @Override
    public void processRefillRequest(WalletOperationMsDto dto) {
        WalletOperationData walletOperationData = dto.getWalletOperationData();
        WalletTransferStatus walletTransferStatus = walletService.walletBalanceChange(walletOperationData);
        if (walletTransferStatus != SUCCESS) {
            throw new RefillRequestRevokeException(walletTransferStatus.name());
        }
        CompanyWallet companyWallet = companyWalletService.findByCurrency(new Currency(dto.getCurrencyId()));
        companyWalletService.deposit(
                companyWallet,
                walletOperationData.getAmount(),
                walletOperationData.getCommissionAmount()
        );
    }
}
