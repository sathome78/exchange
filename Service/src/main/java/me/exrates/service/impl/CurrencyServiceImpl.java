package me.exrates.service.impl;

import me.exrates.dao.CurrencyDao;
import me.exrates.model.Currency;
import me.exrates.model.CurrencyLimit;
import me.exrates.model.CurrencyPair;
import me.exrates.model.dto.CurrencyPairLimitDto;
import me.exrates.model.dto.UserCurrencyOperationPermissionDto;
import me.exrates.model.dto.mobileApiDto.TransferLimitDto;
import me.exrates.model.dto.mobileApiDto.dashboard.CurrencyPairWithLimitsDto;
import me.exrates.model.enums.CurrencyWarningType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.OrderType;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.invoice.InvoiceOperationDirection;
import me.exrates.service.CurrencyService;
import me.exrates.service.UserRoleService;
import me.exrates.service.UserService;
import me.exrates.service.exception.CurrencyPairNotFoundException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ROUND_HALF_UP;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
public class CurrencyServiceImpl implements CurrencyService {

    @Autowired
    private CurrencyDao currencyDao;

    @Autowired
    private UserService userService;

  @Autowired
  UserRoleService userRoleService;

    private static final Logger logger = LogManager.getLogger(CurrencyServiceImpl.class);
    private static final Set<String> CRYPTO = new HashSet<String>() {
        {
            add("EDRC");
            add("BTC");
            add("LTC");
            add("EDR");
        }
    };
    private static final int CRYPTO_PRECISION = 8;
    private static final int DEFAULT_PRECISION = 2;

    @Override
    @Transactional(readOnly = true)
    public String getCurrencyName(int currencyId) {
        return currencyDao.getCurrencyName(currencyId);
    }

    @Override
    public List<Currency> getAllCurrencies() {
        return currencyDao.getCurrList();
    }

    @Override
    public Currency findByName(String name) {
        return currencyDao.findByName(name);
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
    public void updateCurrencyLimit(int currencyId, OperationType operationType, String roleName, BigDecimal minAmount, Integer maxDailyRequest) {
        currencyDao.updateCurrencyLimit(currencyId, operationType, userRoleService.getRealUserRoleIdByBusinessRoleList(roleName), minAmount, maxDailyRequest);
    }

    @Override
    public List<CurrencyLimit> retrieveCurrencyLimitsForRole(String roleName, OperationType operationType) {
        return currencyDao.retrieveCurrencyLimitsForRoles(userRoleService.getRealUserRoleIdByBusinessRoleList(roleName), operationType);
    }

    @Override
    public BigDecimal retrieveMinLimitForRoleAndCurrency(UserRole userRole, OperationType operationType, Integer currencyId) {
        return currencyDao.retrieveMinLimitForRoleAndCurrency(userRole, operationType, currencyId);
    }

    @Override
    public List<CurrencyPair> getAllCurrencyPairs() {
        return currencyDao.getAllCurrencyPairs();
    }

    @Override
    public CurrencyPair findCurrencyPairById(int currencyPairId) {
        try {
            return currencyDao.findCurrencyPairById(currencyPairId);
        } catch (EmptyResultDataAccessException e) {
            throw new CurrencyPairNotFoundException("Currency pair not found");
        }
    }

    @Override
    public String amountToString(final BigDecimal amount, final String currency) {
        return amount.setScale(resolvePrecision(currency), ROUND_HALF_UP)
//                .stripTrailingZeros()
                .toPlainString();
    }

    @Override
    public int resolvePrecision(final String currency) {
        return CRYPTO.contains(currency) ? CRYPTO_PRECISION : DEFAULT_PRECISION;
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
    public List<UserCurrencyOperationPermissionDto> getCurrencyOperationPermittedForWithdraw(String userEmail) {
        return getCurrencyOperationPermittedList(userEmail, InvoiceOperationDirection.WITHDRAW);
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
            .map(e -> e.getCurrencyName())
            .collect(Collectors.toSet());
    }

    private List<UserCurrencyOperationPermissionDto> getCurrencyOperationPermittedList(String userEmail, InvoiceOperationDirection direction) {
        Integer userId = userService.getIdByEmail(userEmail);
        return findWithOperationPermissionByUserAndDirection(userId, direction);
    }
    
    @Override
    public Optional<String> getWarningForCurrency(Integer currencyId, CurrencyWarningType currencyWarningType) {
      return currencyDao.getWarningForCurrency(currencyId, currencyWarningType);
    }

  @Override
  @Transactional(readOnly = true)
  public Currency getById(int id){
    return currencyDao.findById(id);
  }
  
  @Override
  public CurrencyPairLimitDto findLimitForRoleByCurrencyPairAndType(Integer currencyPairId, OperationType operationType) {
    UserRole userRole = userService.getUserRoleFromSecurityContext();
    OrderType orderType = OrderType.convert(operationType.name());
    return currencyDao.findCurrencyPairLimitForRoleByPairAndType(currencyPairId, userRole.getRole(), orderType.getType());
  }
  
  @Override
  public List<CurrencyPairLimitDto> findAllCurrencyLimitsForRoleAndType(String roleName, OrderType orderType) {
    return currencyDao.findLimitsForRolesByType(userRoleService.getRealUserRoleIdByBusinessRoleList(roleName), orderType.getType());
  }
  
  @Override
  public void updateCurrencyPairLimit(Integer currencyPairId, OrderType orderType, String roleName, BigDecimal minRate, BigDecimal maxRate) {
    currencyDao.setCurrencyPairLimit(currencyPairId, userRoleService.getRealUserRoleIdByBusinessRoleList(roleName), orderType.getType(), minRate, maxRate);
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
  
  
}
