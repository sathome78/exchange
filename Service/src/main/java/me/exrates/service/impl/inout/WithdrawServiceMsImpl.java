package me.exrates.service.impl.inout;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.exrates.model.ClientBank;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.model.dto.*;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.WithdrawFilterData;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.service.WithdrawService;
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

@Service
@Conditional(MicroserviceConditional.class)
@RequiredArgsConstructor
public class WithdrawServiceMsImpl implements WithdrawService {
    private static final String API_WITHDRAW_REQUEST_CREATE = "/api/withdraw/request/create";
    private final ObjectMapper objectMapper;
    private final RestTemplate template;
    private final InOutProperties properties;
    private final RequestUtil requestUtil;

    @Override
    public Map<String, String> createWithdrawalRequest(WithdrawRequestCreateDto requestCreateDto, Locale locale) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + API_WITHDRAW_REQUEST_CREATE);
        HttpEntity<?> entity = new HttpEntity<>(requestUtil.prepareHeaders(locale));
        ResponseEntity<Map<String, String>> response = template.exchange(
                builder.toUriString(),
                HttpMethod.POST,
                entity, new ParameterizedTypeReference<Map<String, String>>() {});

        return response.getBody();
    }

    @Override
    public void rejectError(int requestId, long timeoutInMinutes, String reasonCode) {

    }

    @Override
    public void rejectError(int requestId, String reasonCode) {

    }

    @Override
    public void rejectToReview(int requestId) {

    }

    @Override
    public void autoPostWithdrawalRequest(WithdrawRequestPostDto withdrawRequest) {

    }

    @Override
    public void finalizePostWithdrawalRequest(Integer requestId) {

    }

    @Override
    public void postWithdrawalRequest(int requestId, Integer requesterAdminId, String txHash) {

    }

    @Override
    public List<ClientBank> findClientBanksForCurrency(Integer currencyId) {
        return null;
    }

    @Override
    public void setAutoWithdrawParams(MerchantCurrencyOptionsDto merchantCurrencyOptionsDto) {

    }

    @Override
    public MerchantCurrencyAutoParamDto getAutoWithdrawParamsByMerchantAndCurrency(Integer merchantId, Integer currencyId) {
        return null;
    }

    @Override
    public List<MerchantCurrency> retrieveAddressAndAdditionalParamsForWithdrawForMerchantCurrencies(List<MerchantCurrency> merchantCurrencies) {
        return null;
    }

    @Override
    public DataTable<List<WithdrawRequestsAdminTableDto>> getWithdrawRequestByStatusList(List<Integer> requestStatus, DataTableParams dataTableParams, WithdrawFilterData withdrawFilterData, String authorizedUserEmail, Locale locale) {
        return null;
    }

    @Override
    public WithdrawRequestsAdminTableDto getWithdrawRequestById(Integer id, String authorizedUserEmail) {
        return null;
    }

    @Override
    public void revokeWithdrawalRequest(int requestId) {

    }

    @Override
    public void takeInWorkWithdrawalRequest(int requestId, Integer requesterAdminId) {

    }

    @Override
    public void returnFromWorkWithdrawalRequest(int requestId, Integer requesterAdminId) {

    }

    @Override
    public void declineWithdrawalRequest(int requestId, Integer requesterAdminId, String comment) {

    }

    @Override
    public void confirmWithdrawalRequest(int requestId, Integer requesterAdminId) {

    }

    @Override
    public void setAllAvailableInPostingStatus() {

    }

    @Override
    public List<WithdrawRequestPostDto> dirtyReadForPostByStatusList(InvoiceStatus status) {
        return null;
    }

    @Override
    public Map<String, String> correctAmountAndCalculateCommissionPreliminarily(Integer userId, BigDecimal amount, Integer currencyId, Integer merchantId, Locale locale, String destinationTag) {
        return null;
    }

    @Override
    public boolean checkOutputRequestsLimit(int merchantId, String email) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + "/api/checkOutputRequestsLimit")
                .queryParam("merchant_id", merchantId);
        HttpEntity<?> entity = new HttpEntity<>(requestUtil.prepareHeaders(email));
        ResponseEntity<Boolean> response = template.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity, Boolean.class);

        return response.getBody();
    }

    @Override
    public List<Integer> getWithdrawalStatistic(String startDate, String endDate) {
        return null;
    }

    @Override
    public List<WithdrawRequestFlatDto> getRequestsByMerchantIdAndStatus(int merchantId, List<Integer> statuses) {
        return null;
    }

    @Override
    public Optional<Integer> getRequestIdByHashAndMerchantId(String hash, int merchantId) {
        return Optional.empty();
    }

    @Override
    public WithdrawRequestInfoDto getWithdrawalInfo(Integer id, Locale locale) {
        return null;
    }

    @Override
    public List<WithdrawRequestFlatForReportDto> findAllByPeriodAndRoles(LocalDateTime startTime, LocalDateTime endTime, List<UserRole> userRoles, int requesterId) {
        return null;
    }
}
