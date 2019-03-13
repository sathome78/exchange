package me.exrates.service.impl.inout;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.exrates.model.Currency;
import me.exrates.model.*;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.model.dto.MerchantCurrencyBasicInfoDto;
import me.exrates.model.dto.MerchantCurrencyLifetimeDto;
import me.exrates.model.dto.MerchantCurrencyOptionsDto;
import me.exrates.model.dto.MerchantCurrencyScaleDto;
import me.exrates.model.dto.merchants.btc.CoreWalletDto;
import me.exrates.model.dto.mobileApiDto.MerchantCurrencyApiDto;
import me.exrates.model.dto.mobileApiDto.TransferMerchantApiDto;
import me.exrates.model.enums.OperationType;
import me.exrates.service.MerchantService;
import me.exrates.service.exception.CheckDestinationTagException;
import me.exrates.service.properties.InOutProperties;
import me.exrates.service.util.RequestUtil;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.*;

@Service
@Conditional(MicroserviceConditional.class)
@RequiredArgsConstructor
public class MerchantServiceMsImpl implements MerchantService {
    private static final String API_CHECK_DESTINATION_TAG = "/api/checkDestinationTag";
    private final ObjectMapper objectMapper;
    private final RestTemplate template;
    private final InOutProperties properties;
    private final RequestUtil requestUtil;

    @Override
    public List<Merchant> findAllByCurrency(Currency currency) {
        return null;
    }

    @Override
    public List<Merchant> findAll() {
        return null;
    }

    @Override
    public String resolveTransactionStatus(Transaction transaction, Locale locale) {
        return null;
    }

    @Override
    public String sendDepositNotification(String toWallet, String email, Locale locale, CreditsOperation creditsOperation, String depositNotification) {
        return null;
    }

    @Override
    public Merchant findById(int id) {
        return null;
    }

    @Override
    public Merchant findByName(String name) {
        return null;
    }

    @Override
    public List<MerchantCurrency> getAllUnblockedForOperationTypeByCurrencies(List<Integer> currenciesId, OperationType operationType) {
        return null;
    }

    @Override
    public List<MerchantCurrencyApiDto> findNonTransferMerchantCurrencies(Integer currencyId) {
        return null;
    }

    @Override
    public Optional<MerchantCurrency> findByMerchantAndCurrency(int merchantId, int currencyId) {
        return Optional.empty();
    }

    @Override
    public List<TransferMerchantApiDto> findTransferMerchants() {
        return null;
    }

    @Override
    public List<MerchantCurrencyOptionsDto> findMerchantCurrencyOptions(List<String> processTypes) {
        return null;
    }

    @Override
    public Map<String, String> formatResponseMessage(CreditsOperation creditsOperation) {
        return null;
    }

    @Override
    public Map<String, String> formatResponseMessage(Transaction transaction) {
        return null;
    }

    @Override
    public void toggleSubtractMerchantCommissionForWithdraw(String merchantName, String currencyName, boolean subtractMerchantCommissionForWithdraw) {

    }

    @Override
    public void toggleMerchantBlock(Integer merchantId, Integer currencyId, OperationType operationType) {

    }

    @Override
    public void setBlockForAll(OperationType operationType, boolean blockStatus) {

    }

    @Override
    public void setBlockForMerchant(Integer merchantId, Integer currencyId, OperationType operationType, boolean blockStatus) {

    }

    @Override
    public BigDecimal getMinSum(Integer merchantId, Integer currencyId) {
        return null;
    }

    @Override
    public void setMinSum(double merchantId, double currencyId, double minSum) {

    }

    @Override
    public void checkAmountForMinSum(Integer merchantId, Integer currencyId, BigDecimal amount) {

    }

    @Override
    public List<MerchantCurrencyLifetimeDto> getMerchantCurrencyWithRefillLifetime() {
        return null;
    }

    @Override
    public MerchantCurrencyLifetimeDto getMerchantCurrencyLifetimeByMerchantIdAndCurrencyId(Integer merchantId, Integer currencyId) {
        return null;
    }

    @Override
    public MerchantCurrencyScaleDto getMerchantCurrencyScaleByMerchantIdAndCurrencyId(Integer merchantId, Integer currencyId) {
        return null;
    }

    @Override
    public void checkMerchantIsBlocked(Integer merchantId, Integer currencyId, OperationType operationType) {

    }

    @Override
    public List<String> retrieveBtcCoreBasedMerchantNames() {
        return null;
    }

    @Override
    public CoreWalletDto retrieveCoreWalletByMerchantName(String merchantName, Locale locale) {
        return null;
    }

    @Override
    public List<CoreWalletDto> retrieveCoreWallets(Locale locale) {
        return null;
    }

    @Override
    public Optional<String> getCoreWalletPassword(String merchantName, String currencyName) {
        return Optional.empty();
    }

    @Override
    public Properties getPassMerchantProperties(String merchantName) {
        return null;
    }

    @Override
    public Map<String, String> computeCommissionAndMapAllToString(BigDecimal amount, OperationType type, String currency, String merchant) {
        return null;
    }

    @Override
    @SneakyThrows
    public void checkDestinationTag(Integer merchantId, String memo) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + API_CHECK_DESTINATION_TAG)
                .queryParam("merchant_id", merchantId)
                .queryParam("memo", memo);
        HttpEntity<?> entity = new HttpEntity<>(requestUtil.prepareHeaders());
        ResponseEntity<String> response = template.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity, String.class);

        if(response.getStatusCodeValue() == 400){
            throw objectMapper.readValue(response.getBody(), CheckDestinationTagException.class);
        }
    }

    @Override
    public boolean isValidDestinationAddress(Integer merchantId, String address) {
        return false;
    }

    @Override
    public List<String> getWarningsForMerchant(OperationType operationType, Integer merchantId, Locale locale) {
        return null;
    }

    @Override
    public List<Integer> getIdsByProcessType(List<String> processType) {
        return null;
    }

    @Override
    public boolean getSubtractFeeFromAmount(Integer merchantId, Integer currencyId) {
        return false;
    }

    @Override
    public void setSubtractFeeFromAmount(Integer merchantId, Integer currencyId, boolean subtractFeeFromAmount) {

    }

    @Override
    public List<MerchantCurrencyBasicInfoDto> findTokenMerchantsByParentId(Integer parentId) {
        return null;
    }

    @Override
    public boolean setPropertyRecalculateCommissionLimitToUsd(String merchantName, String currencyName, Boolean recalculateToUsd) {
        return false;
    }

    @Override
    public void updateMerchantCommissionsLimits() {

    }
}
