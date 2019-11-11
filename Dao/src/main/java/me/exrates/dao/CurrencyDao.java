package me.exrates.dao;

import me.exrates.model.Currency;
import me.exrates.model.CurrencyLimit;
import me.exrates.model.CurrencyPair;
import me.exrates.model.CurrencyPairRestrictionsEnum;
import me.exrates.model.CurrencyPairWithRestriction;
import me.exrates.model.MarketVolume;
import me.exrates.model.dto.CurrencyPairLimitDto;
import me.exrates.model.dto.CurrencyReportInfoDto;
import me.exrates.model.dto.MerchantCurrencyScaleDto;
import me.exrates.model.dto.UserCurrencyOperationPermissionDto;
import me.exrates.model.dto.api.BalanceDto;
import me.exrates.model.dto.api.RateDto;
import me.exrates.model.dto.mobileApiDto.TransferLimitDto;
import me.exrates.model.dto.mobileApiDto.dashboard.CurrencyPairWithLimitsDto;
import me.exrates.model.dto.openAPI.CurrencyPairInfoItem;
import me.exrates.model.enums.RestrictedOperation;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.Market;
import me.exrates.model.enums.MerchantProcessType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserCommentTopicEnum;
import me.exrates.model.enums.UserRole;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CurrencyDao {

    List<Currency> getAllActiveCurrencies();

    List<Currency> getAllCurrencies();

    int getCurrencyId(int walletId);

    String getCurrencyName(int currencyId);

    Currency findByName(String name);

    Currency findById(int id);

    List<Currency> findAllCurrencies();

    List<CurrencyLimit> retrieveCurrencyLimitsForRoles(List<Integer> roleIds, OperationType operationType);

    List<TransferLimitDto> retrieveMinTransferLimits(List<Integer> currencyIds, Integer roleId);

    BigDecimal retrieveMinLimitForRoleAndCurrency(UserRole userRole, OperationType operationType, Integer currencyId);

    BigDecimal retrieveMaxDailyRequestForRoleAndCurrency(UserRole userRole, OperationType operationType, Integer currencyId);

    void updateCurrencyLimit(int currencyId, OperationType operationType, List<Integer> roleIds, BigDecimal minAmount, BigDecimal minAmountUSD, BigDecimal maxAmount, BigDecimal maxAmountUSD, Integer maxDailyRequest);

    void updateCurrencyLimit(int currencyId, OperationType operationType, BigDecimal minAmount, BigDecimal minAmountUSD, BigDecimal maxAmount, BigDecimal maxAmountUSD, Integer maxDailyRequest);

    List<CurrencyPair> getAllCurrencyPairs(CurrencyPairType type);

    List<CurrencyPair> getAllCurrencyPairsWithHidden(CurrencyPairType type);

    CurrencyPair getCurrencyPairById(int currency1Id, int currency2Id);

    CurrencyPair findCurrencyPairById(int currencyPairId);

    List<UserCurrencyOperationPermissionDto> findCurrencyOperationPermittedByUserAndDirection(Integer userId, String operationDirection);

    List<UserCurrencyOperationPermissionDto> findAllCurrencyOperationPermittedByUserAndDirection(Integer userId, String operationDirection);

    List<UserCurrencyOperationPermissionDto> findCurrencyOperationPermittedByUserList(Integer userId);

    List<String> getWarningForCurrency(Integer currencyId, UserCommentTopicEnum currencyWarningTopicEnum);

    List<String> getWarningsByTopic(UserCommentTopicEnum currencyWarningTopicEnum);

    List<String> getWarningForMerchant(Integer merchantId, UserCommentTopicEnum currencyWarningTopicEnum);

    CurrencyPair findCurrencyPairByOrderId(int orderId);

    CurrencyPairLimitDto findCurrencyPairLimitForRoleByPairAndType(Integer currencyPairId, Integer roleId, Integer orderTypeId);

    List<CurrencyPairLimitDto> findLimitsForRolesByType(List<Integer> roleIds, Integer orderTypeId);

    void setCurrencyPairLimit(Integer currencyPairId, List<Integer> roleIds, Integer orderTypeId,
                              BigDecimal minRate, BigDecimal maxRate, BigDecimal minAmount, BigDecimal maxAmount, BigDecimal minTotal);

    List<CurrencyPairWithLimitsDto> findAllCurrencyPairsWithLimits(Integer roleId);

    List<Currency> findAllCurrenciesWithHidden();

    MerchantCurrencyScaleDto findCurrencyScaleByCurrencyId(Integer currencyId);

    CurrencyPair findCurrencyPairByName(String currencyPair);

    List<Currency> findAllCurrenciesByProcessType(MerchantProcessType processType);

    List<CurrencyPair> findPermitedCurrencyPairs(CurrencyPairType currencyPairType);

    CurrencyPair getNotHiddenCurrencyPairByName(String currencyPair);

    boolean isCurrencyIco(Integer currencyId);

    List<CurrencyPairInfoItem> findActiveCurrencyPairs();

    Optional<Integer> findOpenCurrencyPairIdByName(String pairName);

    List<Currency> findAllCurrency();

    boolean updateVisibilityCurrencyById(int currencyId);

    List<CurrencyPair> findAllCurrencyPair();

    boolean updateVisibilityCurrencyPairById(int currencyPairId);

    boolean updateAccessToDirectLinkCurrencyPairById(int currencyPairId);

    List<CurrencyReportInfoDto> getStatsByCoin(int currencyId);

    boolean setPropertyCalculateLimitToUsd(int currencyId, OperationType operationType, List<Integer> roleIds, Boolean recalculateToUsd);

    List<CurrencyLimit> getAllCurrencyLimits();

    void updateWithdrawLimits(List<CurrencyLimit> currencyLimits);

    List<Currency> getCurrencies(MerchantProcessType... processType);

    List<CurrencyPair> findAllCurrenciesByFirstPartName(String partName);

    List<CurrencyPair> findAllCurrenciesBySecondPartName(String partName);

    List<Currency> findAllByNames(Collection<String> names);

    boolean isCurrencyPairHidden(int currencyPairId);

    void addCurrency(String currencyName, String description, String beanName, String imgPath, boolean hidden, boolean lockInOut);

    void addCurrencyPair(Currency currency1, Currency currency2, String newPairName, CurrencyPairType type, Market market, String tiker, boolean hidden);

    void updateCurrencyExchangeRates(List<RateDto> rates);

    List<RateDto> getCurrencyRates();

    void updateCurrencyBalances(List<BalanceDto> balances);

    List<BalanceDto> getCurrencyBalances();

    boolean updateCurrencyPair(CurrencyPair currencyPair);

    boolean updateCurrencyPairVolume(Integer currencyPairId, BigDecimal volume);

    List<MarketVolume> getAllMarketVolumes();

    boolean updateDefaultMarketVolume(String name, BigDecimal marketVolume);

    CurrencyLimit getCurrencyLimit(Integer currencyId, Integer currencyLimit, Integer operationType);


    CurrencyPairWithRestriction findCurrencyPairWithRestrictionRestrictions(Integer currencyPairId);

    void insertCurrencyPairRestriction(Integer currencyPairId, CurrencyPairRestrictionsEnum restrictionsEnum);

    void deleteCurrencyPairRestriction(Integer currencyPairId, CurrencyPairRestrictionsEnum restrictionsEnum);

    List<CurrencyPairWithRestriction> findAllCurrencyPairWithRestrictions();
}
