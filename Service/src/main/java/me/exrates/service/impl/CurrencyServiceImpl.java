package me.exrates.service.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.CurrencyDao;
import me.exrates.dao.exception.notfound.CurrencyPairNotFoundException;
import me.exrates.model.Currency;
import me.exrates.model.CurrencyLimit;
import me.exrates.model.CurrencyPair;
import me.exrates.model.CurrencyPairRestrictionsEnum;
import me.exrates.model.CurrencyPairWithRestriction;
import me.exrates.model.MarketVolume;
import me.exrates.model.User;
import me.exrates.model.dto.CurrencyPairLimitDto;
import me.exrates.model.dto.CurrencyReportInfoDto;
import me.exrates.model.dto.MerchantCurrencyScaleDto;
import me.exrates.model.dto.UserCurrencyOperationPermissionDto;
import me.exrates.model.dto.api.BalanceDto;
import me.exrates.model.dto.api.RateDto;
import me.exrates.model.dto.mobileApiDto.TransferLimitDto;
import me.exrates.model.dto.mobileApiDto.dashboard.CurrencyPairWithLimitsDto;
import me.exrates.model.dto.openAPI.CurrencyPairInfoItem;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.Market;
import me.exrates.model.enums.MerchantProcessType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderType;
import me.exrates.model.enums.UserCommentTopicEnum;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.invoice.InvoiceOperationDirection;
import me.exrates.service.CurrencyService;
import me.exrates.service.UserRoleService;
import me.exrates.service.UserService;
import me.exrates.service.api.ExchangeApi;
import me.exrates.service.aspect.CheckCurrencyPairVisibility;
import me.exrates.service.bitshares.memo.Preconditions;
import me.exrates.service.exception.ScaleForAmountNotSetException;
import me.exrates.service.util.BigDecimalConverter;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ROUND_HALF_UP;
import static java.util.Objects.isNull;
import static me.exrates.configurations.CacheConfiguration.CURRENCY_BY_NAME_CACHE;
import static me.exrates.configurations.CacheConfiguration.CURRENCY_PAIRS_LIST_BY_TYPE_CACHE;
import static me.exrates.configurations.CacheConfiguration.CURRENCY_PAIR_BY_ID_CACHE;
import static me.exrates.configurations.CacheConfiguration.CURRENCY_PAIR_BY_NAME_CACHE;
import static me.exrates.service.util.CollectionUtil.isEmpty;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Log4j2
@Service
public class CurrencyServiceImpl implements CurrencyService {

    private static final Set<String> CRYPTO = new HashSet<String>() {
        {
            add("EDRC");
            add("BTC");
            add("LTC");
            add("EDR");
            add("ETH");
            add("ETC");
            add("DASH");
        }
    };
    private static final int CRYPTO_PRECISION = 8;
    private static final int DEFAULT_PRECISION = 2;
    private static final int EDC_OUTPUT_PRECISION = 3;

    private static Map<Integer, CurrencyPair> allPairs = new HashMap<>();
    private static Map<String, BigDecimal> defaultMarketVolumes = new HashMap<>();
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private LoadingCache<Integer, CurrencyPairWithRestriction> currencyRestrictionsCache = CacheBuilder.newBuilder()
            .refreshAfterWrite(1, TimeUnit.HOURS)
            .build(createCacheLoader());

    @Autowired
    UserRoleService userRoleService;
    @Autowired
    private CurrencyDao currencyDao;
    @Autowired
    private UserService userService;
    @Autowired
    private ExchangeApi exchangeApi;
    @Autowired
    private BigDecimalConverter converter;
    @Autowired
    @Qualifier(CURRENCY_BY_NAME_CACHE)
    private Cache currencyByNameCache;
    @Autowired
    @Qualifier(CURRENCY_PAIR_BY_NAME_CACHE)
    private Cache currencyPairByNameCache;
    @Autowired
    @Qualifier(CURRENCY_PAIR_BY_ID_CACHE)
    private Cache currencyPairByIdCache;
    @Autowired
    @Qualifier(CURRENCY_PAIRS_LIST_BY_TYPE_CACHE)
    private Cache currencyPairsListByTypeCache;

    @PostConstruct
    public void fillCurrencyPairs() {
        allPairs = findAllCurrencyPair()
                .stream().collect(Collectors.toMap(CurrencyPair::getId, Function.identity()));

        defaultMarketVolumes = getAllMarketVolumes().stream()
                .collect(Collectors.toMap(MarketVolume::getName, MarketVolume::getMarketVolume));

        findAllCurrencyPairWithRestrictions()
                .forEach(cp -> currencyRestrictionsCache.put(cp.getId(), cp));
    }

    @Override
    @Transactional(readOnly = true)
    public String getCurrencyName(int currencyId) {
        return currencyDao.getCurrencyName(currencyId);
    }

    @Override
    public List<Currency> getAllActiveCurrencies() {
        return currencyDao.getAllActiveCurrencies();
    }

    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    @Override
    public List<Currency> getAllCurrencies() {
        return currencyDao.getAllCurrencies();
    }

    @Transactional(readOnly = true)
    @Override
    public Currency findByName(String name) {
        if (isNull(currencyByNameCache)) {
            return currencyDao.findByName(name);
        }
        return currencyByNameCache.get(name, () -> currencyDao.findByName(name));
    }

    @Override
    public Currency findById(int id) {
        return currencyDao.findById(id);
    }

    @Override
    public List<Currency> findAllCurrencies() {
        return currencyDao.findAllCurrencies();
    }

    @Override
    public void updateCurrencyLimit(int currencyId, OperationType operationType, String roleName, BigDecimal minAmount, BigDecimal minAmountUSD, BigDecimal maxAmount, BigDecimal maxAmountUSD, Integer maxDailyRequest) {
        currencyDao.updateCurrencyLimit(currencyId, operationType, userRoleService.getRealUserRoleIdByBusinessRoleList(roleName), minAmount, minAmountUSD, maxAmount, maxAmountUSD, maxDailyRequest);
    }

    @Override
    public void updateCurrencyLimit(int currencyId, OperationType operationType, BigDecimal minAmount, BigDecimal minAmountUSD, BigDecimal maxAmount, BigDecimal maxAmountUSD, Integer maxDailyRequest) {

        currencyDao.updateCurrencyLimit(currencyId, operationType, minAmount, minAmountUSD, maxAmount, maxAmountUSD, maxDailyRequest);
    }

    @Override
    public List<CurrencyLimit> retrieveCurrencyLimitsForRole(String roleName, OperationType operationType) {
        return currencyDao.retrieveCurrencyLimitsForRoles(
                userRoleService.getRealUserRoleIdByBusinessRoleList(roleName),
                operationType);
    }

    @Override
    public CurrencyLimit getCurrencyLimit(Integer currencyId, Integer operationType, Integer roleId) {
        return currencyDao.getCurrencyLimit(currencyId, roleId, operationType);
    }

    @Transactional(readOnly = true)
    @Override
    public BigDecimal retrieveMinLimitForRoleAndCurrency(UserRole userRole, OperationType operationType, Integer currencyId) {
        return currencyDao.retrieveMinLimitForRoleAndCurrency(userRole, operationType, currencyId);
    }

    @Transactional(readOnly = true)
    @Override
    public BigDecimal retrieveMaxDailyRequestForRoleAndCurrency(UserRole userRole, OperationType operationType, Integer currencyId) {
        return currencyDao.retrieveMaxDailyRequestForRoleAndCurrency(userRole, operationType, currencyId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CurrencyPair> getAllCurrencyPairs(CurrencyPairType type) {
        return currencyPairsListByTypeCache.get(type, () -> currencyDao.getAllCurrencyPairs(type));
    }

    @Override
    public List<CurrencyPair> getAllCurrencyPairsWithHidden(CurrencyPairType type) {
        return currencyDao.getAllCurrencyPairsWithHidden(type);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CurrencyPair> getAllCurrencyPairsInAlphabeticOrder(CurrencyPairType type) {
        List<CurrencyPair> result = currencyPairsListByTypeCache.get(type, () -> currencyDao.getAllCurrencyPairs(type));
        result.sort(Comparator.comparing(CurrencyPair::getName));
        return result;
    }

    @Override
    public List<CurrencyPair> getAllCurrencyPairsWithHiddenInAlphabeticOrder(CurrencyPairType type) {
        List<CurrencyPair> result = currencyDao.getAllCurrencyPairsWithHidden(type);
        result.sort(Comparator.comparing(CurrencyPair::getName));
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public CurrencyPair findCurrencyPairById(int currencyPairId) {
        try {
            return currencyPairByIdCache.get(currencyPairId, () -> currencyDao.findCurrencyPairById(currencyPairId));
        } catch (EmptyResultDataAccessException | Cache.ValueRetrievalException ex) {
            throw new CurrencyPairNotFoundException("Currency pair not found");
        }
    }

    @Override
    public String amountToString(final BigDecimal amount, final String currency) {
        return amount.setScale(resolvePrecision(currency), ROUND_HALF_UP)
                .toPlainString();
    }

    @Override
    public int resolvePrecision(final String currency) {
        return CRYPTO.contains(currency) ? CRYPTO_PRECISION : DEFAULT_PRECISION;
    }

    @Override
    public int resolvePrecisionByOperationType(final String currency, OperationType operationType) {

        return currency.equals(currencyDao.findByName("EDR").getName()) && (operationType == OperationType.OUTPUT) ?
                EDC_OUTPUT_PRECISION :
                CRYPTO.contains(currency) ? CRYPTO_PRECISION :
                        DEFAULT_PRECISION;
    }

    @Override
    public List<TransferLimitDto> retrieveMinTransferLimits(List<Integer> currencyIds) {
        Integer roleId = userService.getUserRoleFromSecurityContext().getRole();
        return currencyDao.retrieveMinTransferLimits(currencyIds, roleId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserCurrencyOperationPermissionDto> findWithOperationPermissionByUserAndDirection(Integer userId, InvoiceOperationDirection operationDirection) {
        return currencyDao.findCurrencyOperationPermittedByUserAndDirection(userId, operationDirection.name());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserCurrencyOperationPermissionDto> getCurrencyOperationPermittedForRefill(String userEmail) {
        return getCurrencyOperationPermittedList(userEmail, InvoiceOperationDirection.REFILL);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserCurrencyOperationPermissionDto> getAllCurrencyOperationPermittedForRefill(String userEmail) {
        return getAllCurrencyOperationPermittedList(userEmail, InvoiceOperationDirection.REFILL);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserCurrencyOperationPermissionDto> getCurrencyOperationPermittedForWithdraw(String userEmail) {
        return getCurrencyOperationPermittedList(userEmail, InvoiceOperationDirection.WITHDRAW);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserCurrencyOperationPermissionDto> getAllCurrencyOperationPermittedForWithdraw(String userEmail) {
        return getAllCurrencyOperationPermittedList(userEmail, InvoiceOperationDirection.WITHDRAW);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> getCurrencyPermittedNameList(String userEmail) {
        Integer userId = userService.getIdByEmail(userEmail);
        return getCurrencyPermittedNameList(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserCurrencyOperationPermissionDto> getCurrencyPermittedOperationList(Integer userId) {
        return currencyDao.findCurrencyOperationPermittedByUserList(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> getCurrencyPermittedNameList(Integer userId) {
        return currencyDao.findCurrencyOperationPermittedByUserList(userId).stream()
                .map(UserCurrencyOperationPermissionDto::getCurrencyName)
                .collect(Collectors.toSet());
    }

    private List<UserCurrencyOperationPermissionDto> getCurrencyOperationPermittedList(String userEmail, InvoiceOperationDirection direction) {
        Integer userId = userService.getIdByEmail(userEmail);
        return findWithOperationPermissionByUserAndDirection(userId, direction);
    }

    private List<UserCurrencyOperationPermissionDto> getAllCurrencyOperationPermittedList(String userEmail, InvoiceOperationDirection direction) {
        Integer userId = userService.getIdByEmail(userEmail);
        return currencyDao.findAllCurrencyOperationPermittedByUserAndDirection(userId, direction.name());
    }

    @Override
    public List<String> getWarningForCurrency(Integer currencyId, UserCommentTopicEnum currencyWarningTopicEnum) {
        return currencyDao.getWarningForCurrency(currencyId, currencyWarningTopicEnum);
    }

    @Override
    public List<String> getWarningsByTopic(UserCommentTopicEnum currencyWarningTopicEnum) {
        return currencyDao.getWarningsByTopic(currencyWarningTopicEnum);
    }

    @Override
    public List<String> getWarningForMerchant(Integer merchantId, UserCommentTopicEnum currencyWarningTopicEnum) {
        return currencyDao.getWarningForMerchant(merchantId, currencyWarningTopicEnum);
    }

    @Override
    @Transactional(readOnly = true)
    public Currency getById(int id) {
        return currencyDao.findById(id);
    }

    @Override
    public CurrencyPairLimitDto findLimitForRoleByCurrencyPairAndType(Integer currencyPairId, OperationType operationType) {
        UserRole userRole = userService.getUserRoleFromSecurityContext();
        OrderType orderType = OrderType.convert(operationType.name());
        return currencyDao.findCurrencyPairLimitForRoleByPairAndType(currencyPairId, userRole.getRole(), orderType.getType());
    }

    @Override
    public CurrencyPairLimitDto findLimitForRoleByCurrencyPairAndTypeAndUser(Integer currencyPairId, OperationType operationType, User user) {
        UserRole userRole = user.getRole();
        OrderType orderType = OrderType.convert(operationType.name());
        return currencyDao.findCurrencyPairLimitForRoleByPairAndType(currencyPairId, userRole.getRole(), orderType.getType());

    }

    @Override
    public List<CurrencyPairLimitDto> findAllCurrencyLimitsForRoleAndType(String roleName, OrderType orderType) {
        return currencyDao.findLimitsForRolesByType(userRoleService.getRealUserRoleIdByBusinessRoleList(roleName), orderType.getType());
    }

    @Override
    public void updateCurrencyPairLimit(Integer currencyPairId, OrderType orderType, String roleName,
                                        BigDecimal minRate, BigDecimal maxRate, BigDecimal minAmount, BigDecimal maxAmount, BigDecimal minTotal) {
        currencyDao.setCurrencyPairLimit(currencyPairId, userRoleService.getRealUserRoleIdByBusinessRoleList(roleName), orderType.getType(), minRate,
                maxRate, minAmount, maxAmount, minTotal);
    }

    @Override
    public List<CurrencyPairWithLimitsDto> findCurrencyPairsWithLimitsForUser() {
        Integer userRoleId = userService.getUserRoleFromSecurityContext().getRole();
        return currencyDao.findAllCurrencyPairsWithLimits(userRoleId);
    }

    @Override
    public List<Currency> findAllCurrenciesWithHidden() {
        return currencyDao.findAllCurrenciesWithHidden();
    }

    @Override
    public BigDecimal computeRandomizedAddition(Integer currencyId, OperationType operationType) {
        Optional<OperationType.AdditionalRandomAmountParam> randomAmountParam = operationType.getRandomAmountParam(currencyId);
        if (!randomAmountParam.isPresent()) {
            return BigDecimal.ZERO;
        } else {
            OperationType.AdditionalRandomAmountParam param = randomAmountParam.get();
            return BigDecimal.valueOf(Math.random() * (param.highBound - param.lowBound) + param.lowBound).setScale(0, BigDecimal.ROUND_DOWN);
        }
    }

    @Override
    public boolean isIco(Integer currencyId) {
        return currencyDao.isCurrencyIco(currencyId);
    }

    @Override
    @Transactional
    public MerchantCurrencyScaleDto getCurrencyScaleByCurrencyId(Integer currencyId) {
        MerchantCurrencyScaleDto result = currencyDao.findCurrencyScaleByCurrencyId(currencyId);
        Optional.ofNullable(result.getScaleForRefill()).orElseThrow(() -> new ScaleForAmountNotSetException("currency: " + currencyId));
        Optional.ofNullable(result.getScaleForWithdraw()).orElseThrow(() -> new ScaleForAmountNotSetException("currency: " + currencyId));
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public CurrencyPair getCurrencyPairByName(String currencyPair) {
        return currencyPairByNameCache.get(currencyPair, () -> currencyDao.findCurrencyPairByName(currencyPair));
    }

    @Override
    public Integer findCurrencyPairIdByName(String pairName) {
        return currencyDao.findOpenCurrencyPairIdByName(pairName).orElseThrow(() -> {
            String massage = "Failed to find currency pair details for pairName " + (pairName == null ? "null" : pairName);
            return new CurrencyPairNotFoundException(massage);
        });
    }

    @Override
    public List<Currency> findAllCurrenciesByProcessType(MerchantProcessType processType) {
        return currencyDao.findAllCurrenciesByProcessType(processType);
    }

    @Override
    public List<CurrencyPair> findPermitedCurrencyPairs(CurrencyPairType currencyPairType) {
        return currencyDao.findPermitedCurrencyPairs(currencyPairType);
    }

    @Override
    public CurrencyPair getNotHiddenCurrencyPairByName(String currencyPair) {
        return currencyDao.getNotHiddenCurrencyPairByName(currencyPair);
    }


    @Override
    public List<CurrencyPairInfoItem> findActiveCurrencyPairs() {
        return currencyDao.findActiveCurrencyPairs();
    }

    @Override
    public List<Currency> findAllCurrency() {
        return currencyDao.findAllCurrency();
    }

    @Override
    public boolean updateVisibilityCurrencyById(int currencyId) {
        return currencyDao.updateVisibilityCurrencyById(currencyId);
    }

    @Override
    public List<CurrencyPair> findAllCurrencyPair() {
        return currencyDao.findAllCurrencyPair();
    }

    @Override
    public List<CurrencyPairWithRestriction> findAllCurrencyPairWithRestrictions() {
        return currencyDao.findAllCurrencyPairWithRestrictions();
    }

    @CheckCurrencyPairVisibility
    @Override
    public boolean updateVisibilityCurrencyPairById(int currencyPairId) {
        return currencyDao.updateVisibilityCurrencyPairById(currencyPairId);
    }

    @Override
    public boolean updateAccessToDirectLinkCurrencyPairById(int currencyPairId) {
        return currencyDao.updateAccessToDirectLinkCurrencyPairById(currencyPairId);
    }

    @Override
    public List<CurrencyReportInfoDto> getStatsByCoin(int currencyId) {
        return currencyDao.getStatsByCoin(currencyId);
    }

    @Override
    public boolean setPropertyCalculateLimitToUsd(int currencyId, OperationType operationType, String roleName, Boolean recalculateToUsd) {
        return currencyDao.setPropertyCalculateLimitToUsd(currencyId, operationType, userRoleService.getRealUserRoleIdByBusinessRoleList(roleName), recalculateToUsd);
    }

    @Transactional
    @Override
    public void updateWithdrawLimits() {
        StopWatch stopWatch = StopWatch.createStarted();
        log.info("Process of updating withdraw limits start...");

        List<CurrencyLimit> currencyLimits = currencyDao.getAllCurrencyLimits();
        if (isEmpty(currencyLimits)) {
            return;
        }

        final Map<String, RateDto> rates = exchangeApi.getRates();
        if (rates.isEmpty()) {
            log.info("Exchange api did not return data");
            return;
        }

        for (CurrencyLimit currencyLimit : currencyLimits) {
            final String currencyName = currencyLimit.getCurrency().getName();
            final boolean recalculateToUsd = currencyLimit.isRecalculateToUsd();
            BigDecimal minSumUsdRate = currencyLimit.getMinSumUsdRate();
            BigDecimal minSum = currencyLimit.getMinSum();

            BigDecimal maxSumUsdRate = currencyLimit.getMaxSumUsd();
            BigDecimal maxSum = currencyLimit.getMaxSum();

            RateDto rateDto = rates.get(currencyName);
            if (isNull(rateDto)) {
                continue;
            }

            final BigDecimal usdRate = rateDto.getUsdRate();
            if (usdRate.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }

            currencyLimit.setCurrencyUsdRate(usdRate);

            if (recalculateToUsd) {
                minSum = converter.convert(minSumUsdRate.divide(usdRate, RoundingMode.HALF_UP));
                currencyLimit.setMinSum(minSum);

                if (!Objects.isNull(maxSum)) {
                    maxSum = converter.convert(maxSumUsdRate.divide(usdRate, RoundingMode.HALF_UP));
                    currencyLimit.setMaxSum(maxSum);
                }
            } else {
                minSumUsdRate = minSum.multiply(usdRate);
                currencyLimit.setMinSumUsdRate(minSumUsdRate);

                if (!Objects.isNull(maxSum)) {
                    maxSumUsdRate = maxSum.multiply(usdRate);
                    currencyLimit.setMaxSumUsd(maxSumUsdRate);
                }
            }
        }
        currencyDao.updateWithdrawLimits(currencyLimits);

        log.info("Process of updating withdraw limits end... Time: {}", stopWatch.getTime(TimeUnit.MILLISECONDS));
    }

    @Override
    public List<Currency> getCurrencies(MerchantProcessType... processType) {
        return currencyDao.getCurrencies(processType);
    }

    @Override
    public List<CurrencyPair> getPairsByFirstPartName(String partName) {
        return currencyDao.findAllCurrenciesByFirstPartName(partName);
    }

    @Override
    public List<CurrencyPair> getPairsBySecondPartName(String partName) {
        return currencyDao.findAllCurrenciesBySecondPartName(partName);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean isCurrencyPairHidden(int currencyPairId) {
        return currencyDao.isCurrencyPairHidden(currencyPairId);
    }

    @Override
    @Transactional
    public void addCurrencyForIco(String name, String description) {
        currencyDao.addCurrency(name, description, "no_bean", "/client/img/merchants/ico.png", true, true);
    }


    @Override
    @Transactional
    public void addCurrencyPairForIco(String firstCurrencyName, String secondCurrencyName) {
        Currency currency1 = findByName(firstCurrencyName);
        Currency currency2 = findByName(secondCurrencyName);
        Preconditions.checkArgument(currency1 != null && currency2 != null);
        String newPairName = String.format("%s/%s", firstCurrencyName, secondCurrencyName);
        try {
            getCurrencyPairByName(newPairName);
        } catch (CurrencyPairNotFoundException | Cache.ValueRetrievalException e) {
            currencyDao.addCurrencyPair(currency1, currency2, newPairName, CurrencyPairType.ICO, Market.ICO, newPairName, true);
            return;
        }
        throw new RuntimeException("pair allready exist");
    }

    @Transactional
    @Override
    public void updateCurrencyExchangeRates(List<RateDto> rates) {
        if (isEmpty(rates)) {
            return;
        }
        currencyDao.updateCurrencyExchangeRates(rates);
    }

    @Transactional(readOnly = true)
    @Override
    public List<RateDto> getCurrencyRates() {
        return currencyDao.getCurrencyRates();
    }

    @Transactional
    @Override
    public void updateCurrencyBalances(List<BalanceDto> balances) {
        if (isEmpty(balances)) {
            return;
        }
        currencyDao.updateCurrencyBalances(balances);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BalanceDto> getCurrencyBalances() {
        return currencyDao.getCurrencyBalances();
    }

    @Override
    public boolean updateCurrencyPair(CurrencyPair currencyPair) {
        return currencyDao.updateCurrencyPair(currencyPair);
    }

    @Override
    public Map<Integer, CurrencyPair> getAllCurrencyPairCached() {
        return allPairs;
    }

    @Override
    public boolean updateMarketVolumeCurrecencyPair(Integer currencyPairId, BigDecimal volume) {
        CurrencyPair currencyPair = allPairs.get(currencyPairId);
        currencyPair.setTopMarketVolume(volume);
        return currencyDao.updateCurrencyPairVolume(currencyPairId, volume);
    }

    @Override
    public List<MarketVolume> getAllMarketVolumes() {
        if (defaultMarketVolumes != null && !defaultMarketVolumes.isEmpty()) {
            return defaultMarketVolumes.entrySet().stream()
                    .map(o -> new MarketVolume(o.getKey(), o.getValue()))
                    .collect(Collectors.toList());
        }
        return currencyDao.getAllMarketVolumes();
    }

    @Override
    public boolean updateDefaultMarketVolume(String name, BigDecimal volume) {
        defaultMarketVolumes.remove(name);
        defaultMarketVolumes.put(name, volume);
        return currencyDao.updateDefaultMarketVolume(name, volume);
    }

    @Override
    public CurrencyPairWithRestriction findCurrencyPairByIdWithRestrictions(Integer currencyPairId) {
        try {
            return currencyRestrictionsCache.get(currencyPairId);
        } catch (ExecutionException e) {
            log.warn("Failed to retrieve cache data for currency pair with id: " + currencyPairId, e);
            CurrencyPairWithRestriction currencyPairWithRestriction = new CurrencyPairWithRestriction();
            currencyPairWithRestriction.setId(currencyPairId);
            currencyPairWithRestriction.setTradeRestriction(Collections.emptyList());
            return currencyPairWithRestriction;
        }
    }

    @Override
    public void addRestrictionForCurrencyPairById(int currencyPairId, CurrencyPairRestrictionsEnum restrictionsEnum) {
        currencyDao.insertCurrencyPairRestriction(currencyPairId, restrictionsEnum);
        currencyRestrictionsCache.refresh(currencyPairId);
    }

    @Override
    public void deleteRestrictionForCurrencyPairById(int currencyPairId, CurrencyPairRestrictionsEnum restrictionsEnum) {
        currencyDao.deleteCurrencyPairRestriction(currencyPairId, restrictionsEnum);
        currencyRestrictionsCache.invalidate(currencyPairId);
    }

    @Override
    public List<String> findSuitableForCommission() {
        return currencyDao.findSuitableForCommission();
    }

    private CacheLoader<Integer, CurrencyPairWithRestriction> createCacheLoader() {
        return new CacheLoader<Integer, CurrencyPairWithRestriction>() {
            @Override
            public CurrencyPairWithRestriction load(Integer currencyPairId) {
                return currencyDao.findCurrencyPairWithRestrictionRestrictions(currencyPairId);
            }

            @Override
            public ListenableFuture<CurrencyPairWithRestriction> reload(final Integer currencyPairId,
                                                                        CurrencyPairWithRestriction dto) {
                if (dto.getTradeRestriction().isEmpty()) {
                    return Futures.immediateFuture(dto);
                }

                ListenableFutureTask<CurrencyPairWithRestriction> command = ListenableFutureTask
                                .create(() -> currencyDao.findCurrencyPairWithRestrictionRestrictions(currencyPairId));
                executorService.execute(command);
                return command;
            }
        };
    }
}
