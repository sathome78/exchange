package me.exrates.dao;

import me.exrates.model.Merchant;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.model.dto.*;
import me.exrates.model.dto.merchants.btc.CoreWalletDto;
import me.exrates.model.dto.mobileApiDto.MerchantCurrencyApiDto;
import me.exrates.model.dto.mobileApiDto.TransferMerchantApiDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.TransferTypeVoucher;
import me.exrates.model.enums.UserRole;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Conditional(MicroserviceConditional.class)
public class MerchantDaoMsImpl implements MerchantDao {
    @Override
    public Merchant create(Merchant merchant) {
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
    public List<Merchant> findAll() {
        return null;
    }

    @Override
    public List<Merchant> findAllByCurrency(int currencyId) {
        return null;
    }

    @Override
    public BigDecimal getMinSum(int merchant, int currency) {
        return null;
    }

    @Override
    public Optional<MerchantCurrency> findByMerchantAndCurrency(int merchantId, int currencyId) {
        return Optional.empty();
    }

    @Override
    public List<MerchantCurrency> findAllUnblockedForOperationTypeByCurrencies(List<Integer> currenciesId, OperationType operationType) {
        return null;
    }

    @Override
    public List<MerchantCurrencyApiDto> findAllMerchantCurrencies(Integer currencyId, UserRole userRole, List<String> merchantProcessTypes) {
        return null;
    }

    @Override
    public List<TransferMerchantApiDto> findTransferMerchants() {
        return null;
    }

    @Override
    public List<Integer> findCurrenciesIdsByType(List<String> processTypes) {
        return null;
    }

    @Override
    public List<MerchantCurrencyOptionsDto> findMerchantCurrencyOptions(List<String> processTypes) {
        return null;
    }

    @Override
    public void toggleSubtractMerchantCommissionForWithdraw(String merchantName, String currencyName, boolean subtractMerchantCommissionForWithdraw) {

    }

    @Override
    public void toggleMerchantBlock(Integer merchantId, Integer currencyId, OperationType operationType) {

    }

    @Override
    public void setBlockForAllNonTransfer(OperationType operationType) {

    }

    @Override
    public boolean isBlockStateBackupValid(OperationType operationType) {
        return false;
    }

    @Override
    public boolean isBlockStateValid(OperationType operationType) {
        return false;
    }

    @Override
    public void backupBlockState(OperationType operationType) {

    }

    @Override
    public void restoreBlockState(OperationType operationType) {

    }

    @Override
    public void setBlockForMerchant(Integer merchantId, Integer currencyId, OperationType operationType, boolean blockStatus) {

    }

    @Override
    public boolean checkMerchantBlock(Integer merchantId, Integer currencyId, OperationType operationType) {
        return false;
    }

    @Override
    public void setAutoWithdrawParamsByMerchantAndCurrency(Integer merchantId, Integer currencyId, Boolean withdrawAutoEnabled, Integer withdrawAutoDelaySeconds, BigDecimal withdrawAutoThresholdAmount) {

    }

    @Override
    public MerchantCurrencyAutoParamDto findAutoWithdrawParamsByMerchantAndCurrency(Integer merchantId, Integer currencyId) {
        return null;
    }

    @Override
    public List<String> retrieveBtcCoreBasedMerchantNames() {
        return null;
    }

    @Override
    public Optional<CoreWalletDto> retrieveCoreWalletByMerchantName(String merchantName) {
        return Optional.empty();
    }

    @Override
    public List<CoreWalletDto> retrieveCoreWallets() {
        return null;
    }

    @Override
    public List<MerchantCurrencyLifetimeDto> findMerchantCurrencyWithRefillLifetime() {
        return null;
    }

    @Override
    public MerchantCurrencyLifetimeDto findMerchantCurrencyLifetimeByMerchantIdAndCurrencyId(Integer merchantId, Integer currencyId) {
        return null;
    }

    @Override
    public MerchantCurrencyScaleDto findMerchantCurrencyScaleByMerchantIdAndCurrencyId(Integer merchantId, Integer currencyId) {
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
    public Optional<String> getCoreWalletPassword(String merchantName, String currencyName) {
        return Optional.empty();
    }

    @Override
    public List<MerchantCurrencyBasicInfoDto> findTokenMerchantsByParentId(Integer parentId) {
        return null;
    }

    @Override
    public BigDecimal getMerchantInputCommission(int merchantId, int currencyId, String childMerchant) {
        return null;
    }

    @Override
    public boolean setPropertyRecalculateCommissionLimitToUsd(String merchantName, String currencyName, Boolean recalculateToUsd) {
        return false;
    }

    @Override
    public List<MerchantCurrencyOptionsDto> getAllMerchantCommissionsLimits() {
        return null;
    }

    @Override
    public void updateMerchantCommissionsLimits(MerchantCurrencyOptionsDto merchantCommissionsLimit) {

    }

    @Override
    public boolean checkAvailable(Integer currencyId, Integer merchantId) {
        return false;
    }

    @Override
    public MerchantCurrency getMerchantByCurrencyForVoucher(Integer currencyId, TransferTypeVoucher transferType) {
        return null;
    }
}
