package me.exrates.service.impl.inout;

import lombok.RequiredArgsConstructor;
import me.exrates.dao.CurrencyDao;
import me.exrates.model.Currency;
import me.exrates.model.CurrencyLimit;
import me.exrates.model.CurrencyPair;
import me.exrates.model.User;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.model.dto.CurrencyPairLimitDto;
import me.exrates.model.dto.CurrencyReportInfoDto;
import me.exrates.model.dto.MerchantCurrencyScaleDto;
import me.exrates.model.dto.UserCurrencyOperationPermissionDto;
import me.exrates.model.dto.mobileApiDto.TransferLimitDto;
import me.exrates.model.dto.mobileApiDto.dashboard.CurrencyPairWithLimitsDto;
import me.exrates.model.dto.openAPI.CurrencyPairInfoItem;
import me.exrates.model.enums.*;
import me.exrates.model.enums.invoice.InvoiceOperationDirection;
import me.exrates.service.CurrencyService;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
@Conditional(MicroserviceConditional.class)
@RequiredArgsConstructor
public class CurrencyServiceMsImpl implements CurrencyService {


    private final CurrencyDao currencyDao;

    @Override
    public String getCurrencyName(int currencyId) {
        return null;
    }

    @Override
    public List<Currency> getAllActiveCurrencies() {
        return null;
    }

    @Override
    public List<Currency> getAllCurrencies() {
        return null;
    }

    @Override
    public Currency findByName(String name) {
        return null;
    }

    @Override
    public Currency findById(int id) {
        return null;
    }

    @Override
    public List<Currency> findAllCurrencies() {
        return null;
    }

    @Override
    public void updateCurrencyLimit(int currencyId, OperationType operationType, String roleName, BigDecimal minAmount, BigDecimal minAmountUSD, Integer maxDailyRequest) {

    }

    @Override
    public void updateCurrencyLimit(int currencyId, OperationType operationType, BigDecimal minAmount, BigDecimal minAmountUSD, Integer maxDailyRequest) {

    }

    @Override
    public List<CurrencyLimit> retrieveCurrencyLimitsForRole(String roleName, OperationType operationType) {
        return null;
    }

    @Override
    public BigDecimal retrieveMinLimitForRoleAndCurrency(UserRole userRole, OperationType operationType, Integer currencyId) {
        return null;
    }

    @Override
    public BigDecimal retrieveMaxDailyRequestForRoleAndCurrency(UserRole userRole, OperationType operationType, Integer currencyId) {
        return null;
    }

    @Override
    public List<CurrencyPair> getAllCurrencyPairs(CurrencyPairType type) {
        return null;
    }

    @Override
    public List<CurrencyPair> getAllCurrencyPairsWithHidden(CurrencyPairType type) {
        return currencyDao.getAllCurrencyPairsWithHidden(type); //TODO remove
    }

    @Override
    public List<CurrencyPair> getAllCurrencyPairsInAlphabeticOrder(CurrencyPairType type) {
        return null;
    }

    @Override
    public CurrencyPair findCurrencyPairById(int currencyPairId) {
        return null;
    }

    @Override
    public String amountToString(BigDecimal amount, String currency) {
        return null;
    }

    @Override
    public int resolvePrecision(String currency) {
        return 0;
    }

    @Override
    public int resolvePrecisionByOperationType(String currency, OperationType operationType) {
        return 0;
    }

    @Override
    public List<TransferLimitDto> retrieveMinTransferLimits(List<Integer> currencyIds) {
        return null;
    }

    @Override
    public List<UserCurrencyOperationPermissionDto> getCurrencyOperationPermittedForRefill(String userEmail) {
        return null;
    }

    @Override
    public List<UserCurrencyOperationPermissionDto> getCurrencyOperationPermittedForWithdraw(String userEmail) {
        return null;
    }

    @Override
    public List<UserCurrencyOperationPermissionDto> findWithOperationPermissionByUserAndDirection(Integer userId, InvoiceOperationDirection operationDirection) {
        return null;
    }

    @Override
    public Set<String> getCurrencyPermittedNameList(String userEmail) {
        return null;
    }

    @Override
    public List<UserCurrencyOperationPermissionDto> getCurrencyPermittedOperationList(Integer userId) {
        return null;
    }

    @Override
    public Set<String> getCurrencyPermittedNameList(Integer userId) {
        return null;
    }

    @Override
    public List<String> getWarningForCurrency(Integer currencyId, UserCommentTopicEnum currencyWarningTopicEnum) {
        return null;
    }

    @Override
    public List<String> getWarningsByTopic(UserCommentTopicEnum currencyWarningTopicEnum) {
        return null;
    }

    @Override
    public List<String> getWarningForMerchant(Integer merchantId, UserCommentTopicEnum currencyWarningTopicEnum) {
        return null;
    }

    @Override
    public Currency getById(int id) {
        return null;
    }

    @Override
    public CurrencyPairLimitDto findLimitForRoleByCurrencyPairAndType(Integer currencyPairId, OperationType operationType) {
        return null;
    }

    @Override
    public CurrencyPairLimitDto findLimitForRoleByCurrencyPairAndTypeAndUser(Integer currencyPairId, OperationType operationType, User user) {
        return null;
    }

    @Override
    public List<CurrencyPairLimitDto> findAllCurrencyLimitsForRoleAndType(String roleName, OrderType orderType) {
        return null;
    }

    @Override
    public void updateCurrencyPairLimit(Integer currencyPairId, OrderType orderType, String roleName, BigDecimal minRate, BigDecimal maxRate, BigDecimal minAmount, BigDecimal maxAmount) {

    }

    @Override
    public List<CurrencyPairWithLimitsDto> findCurrencyPairsWithLimitsForUser() {
        return null;
    }

    @Override
    public List<Currency> findAllCurrenciesWithHidden() {
        return null;
    }

    @Override
    public BigDecimal computeRandomizedAddition(Integer currencyId, OperationType operationType) {
        return null;
    }

    @Override
    public MerchantCurrencyScaleDto getCurrencyScaleByCurrencyId(Integer currencyId) {
        return null;
    }

    @Override
    public CurrencyPair getCurrencyPairByName(String currencyPair) {
        return null;
    }

    @Override
    public Integer findCurrencyPairIdByName(String pairName) {
        return null;
    }

    @Override
    public List<Currency> findAllCurrenciesByProcessType(MerchantProcessType processType) {
        return null;
    }

    @Override
    public List<CurrencyPair> findPermitedCurrencyPairs(CurrencyPairType ico) {
        return null;
    }

    @Override
    public CurrencyPair getNotHiddenCurrencyPairByName(String currencyPair) {
        return null;
    }

    @Override
    public boolean isIco(Integer currencyId) {
        return false;
    }

    @Override
    public List<CurrencyPairInfoItem> findActiveCurrencyPairs() {
        return null;
    }

    @Override
    public List<Currency> findAllCurrency() {
        return null;
    }

    @Override
    public boolean updateVisibilityCurrencyById(int currencyId) {
        return false;
    }

    @Override
    public List<CurrencyPair> findAllCurrencyPair() {
        return null;
    }

    @Override
    public boolean updateVisibilityCurrencyPairById(int currencyPairId) {
        return false;
    }

    @Override
    public boolean updateAccessToDirectLinkCurrencyPairById(int currencyPairId) {
        return false;
    }

    @Override
    public List<CurrencyReportInfoDto> getStatsByCoin(int currencyId) {
        return null;
    }

    @Override
    public boolean setPropertyCalculateLimitToUsd(int currencyId, OperationType operationType, String roleName, Boolean recalculateToUsd) {
        return false;
    }

    @Override
    public void updateWithdrawLimits() {

    }

    @Override
    public List<Currency> getCurrencies(MerchantProcessType... processType) {
        return null;
    }

    @Override
    public List<CurrencyPair> getPairsByFirstPartName(String partName) {
        return null;
    }

    @Override
    public List<CurrencyPair> getPairsBySecondPartName(String partName) {
        return null;
    }
}
