package me.exrates.service.impl.inout;

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
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
@Conditional(MicroserviceConditional.class)
public class MerchantServiceMsImpl implements MerchantService {
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
    public void checkDestinationTag(Integer merchantId, String memo) {

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
