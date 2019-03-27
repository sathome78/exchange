package me.exrates.dao.impl.inout;

import me.exrates.dao.CurrencyDao;
import me.exrates.model.Currency;
import me.exrates.model.CurrencyLimit;
import me.exrates.model.CurrencyPair;
import me.exrates.model.dto.CurrencyPairLimitDto;
import me.exrates.model.dto.CurrencyReportInfoDto;
import me.exrates.model.dto.MerchantCurrencyScaleDto;
import me.exrates.model.dto.UserCurrencyOperationPermissionDto;
import me.exrates.model.dto.mobileApiDto.TransferLimitDto;
import me.exrates.model.dto.mobileApiDto.dashboard.CurrencyPairWithLimitsDto;
import me.exrates.model.dto.openAPI.CurrencyPairInfoItem;
import me.exrates.model.enums.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static me.exrates.dao.impl.CurrencyDaoImpl.currencyPairRowMapper;

//@Repository
//@Conditional(MicroserviceConditional.class)
public class CurrencyDaoMsImpl implements CurrencyDao {

    @Autowired
    @Qualifier(value = "masterTemplate")
    private NamedParameterJdbcTemplate npJdbcTemplate;

    @Override
    public List<Currency> getAllActiveCurrencies() {
        return null;
    }

    @Override
    public List<Currency> getAllCurrencies() {
        return null;
    }

    @Override
    public int getCurrencyId(int walletId) {
        return 0;
    }

    @Override
    public String getCurrencyName(int currencyId) {
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
    public List<CurrencyLimit> retrieveCurrencyLimitsForRoles(List<Integer> roleIds, OperationType operationType) {
        return null;
    }

    @Override
    public List<TransferLimitDto> retrieveMinTransferLimits(List<Integer> currencyIds, Integer roleId) {
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
    public void updateCurrencyLimit(int currencyId, OperationType operationType, List<Integer> roleIds, BigDecimal minAmount, BigDecimal minAmountUSD, Integer maxDailyRequest) {

    }

    @Override
    public void updateCurrencyLimit(int currencyId, OperationType operationType, BigDecimal minAmount, BigDecimal minAmountUSD, Integer maxDailyRequest) {

    }

    @Override
    public List<CurrencyPair> getAllCurrencyPairs(CurrencyPairType type) {
        String typeClause = "";
        if (type != null && type != CurrencyPairType.ALL) {
            typeClause = " AND type =:pairType ";
        }
        String sql = "SELECT id, currency1_id, currency2_id, name, market, type, " +
                "(select name from CURRENCY where id = currency1_id) as currency1_name, " +
                "(select name from CURRENCY where id = currency2_id) as currency2_name " +
                " FROM CURRENCY_PAIR " +
                " WHERE hidden IS NOT TRUE " + typeClause +
                " ORDER BY -pair_order DESC";
        return npJdbcTemplate.query(sql, Collections.singletonMap("pairType", type.name()), currencyPairRowMapper);
    }

    @Override
    public List<CurrencyPair> getAllCurrencyPairsWithHidden(CurrencyPairType type) {
        return null;
    }

    @Override
    public CurrencyPair getCurrencyPairById(int currency1Id, int currency2Id) {
        return null;
    }

    @Override
    public CurrencyPair findCurrencyPairById(int currencyPairId) {
        return null;
    }

    @Override
    public List<UserCurrencyOperationPermissionDto> findCurrencyOperationPermittedByUserAndDirection(Integer userId, String operationDirection) {
        return null;
    }

    @Override
    public List<UserCurrencyOperationPermissionDto> findAllCurrencyOperationPermittedByUserAndDirection(Integer userId, String operationDirection) {
        return null;
    }

    @Override
    public List<UserCurrencyOperationPermissionDto> findCurrencyOperationPermittedByUserList(Integer userId) {
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
    public CurrencyPair findCurrencyPairByOrderId(int orderId) {
        return null;
    }

    @Override
    public CurrencyPairLimitDto findCurrencyPairLimitForRoleByPairAndType(Integer currencyPairId, Integer roleId, Integer orderTypeId) {
        return null;
    }

    @Override
    public List<CurrencyPairLimitDto> findLimitsForRolesByType(List<Integer> roleIds, Integer orderTypeId) {
        return null;
    }

    @Override
    public void setCurrencyPairLimit(Integer currencyPairId, List<Integer> roleIds, Integer orderTypeId, BigDecimal minRate, BigDecimal maxRate, BigDecimal minAmount, BigDecimal maxAmount) {

    }

    @Override
    public List<CurrencyPairWithLimitsDto> findAllCurrencyPairsWithLimits(Integer roleId) {
        return null;
    }

    @Override
    public List<Currency> findAllCurrenciesWithHidden() {
        return null;
    }

    @Override
    public MerchantCurrencyScaleDto findCurrencyScaleByCurrencyId(Integer currencyId) {
        return null;
    }

    @Override
    public CurrencyPair findCurrencyPairByName(String currencyPair) {
        return null;
    }

    @Override
    public List<Currency> findAllCurrenciesByProcessType(MerchantProcessType processType) {
        return null;
    }

    @Override
    public List<CurrencyPair> findPermitedCurrencyPairs(CurrencyPairType currencyPairType) {
        return null;
    }

    @Override
    public CurrencyPair getNotHiddenCurrencyPairByName(String currencyPair) {
        return null;
    }

    @Override
    public boolean isCurrencyIco(Integer currencyId) {
        return false;
    }

    @Override
    public List<CurrencyPairInfoItem> findActiveCurrencyPairs() {
        return null;
    }

    @Override
    public Optional<Integer> findOpenCurrencyPairIdByName(String pairName) {
        return Optional.empty();
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
    public boolean setPropertyCalculateLimitToUsd(int currencyId, OperationType operationType, List<Integer> roleIds, Boolean recalculateToUsd) {
        return false;
    }

    @Override
    public List<CurrencyLimit> getAllCurrencyLimits() {
        return null;
    }

    @Override
    public void updateWithdrawLimits(List<CurrencyLimit> currencyLimits) {

    }

    @Override
    public List<Currency> getCurrencies(MerchantProcessType... processType) {
        return null;
    }

    @Override
    public List<CurrencyPair> findAllCurrenciesByFirstPartName(String partName) {
        return null;
    }

    @Override
    public List<CurrencyPair> findAllCurrenciesBySecondPartName(String partName) {
        return null;
    }
}
