package me.exrates.dao;

import me.exrates.model.Merchant;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.dto.*;
import me.exrates.model.dto.mobileApiDto.MerchantCurrencyApiDto;
import me.exrates.model.dto.mobileApiDto.TransferMerchantApiDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface MerchantDao {

  Merchant create(Merchant merchant);

  Merchant findById(int id);

  Merchant findByName(String name);

  List<Merchant> findAll();

  List<Merchant> findAllByCurrency(int currencyId);

  BigDecimal getMinSum(int merchant, int currency);

    Optional<MerchantCurrency> findByMerchantAndCurrency(int merchantId, int currencyId);

    List<MerchantCurrency> findAllUnblockedForOperationTypeByCurrencies(List<Integer> currenciesId, OperationType operationType);

  List<MerchantCurrencyApiDto> findAllMerchantCurrencies(Integer currencyId, UserRole userRole, List<String> merchantProcessTypes);

  List<TransferMerchantApiDto> findTransferMerchants();

    List<Integer> findCurrenciesIdsByType(List<String> processTypes);

    List<MerchantCurrencyOptionsDto> findMerchantCurrencyOptions(List<String> processTypes);

    void toggleSubtractMerchantCommissionForWithdraw(Integer merchantId, Integer currencyId, boolean subtractMerchantCommissionForWithdraw);

    void toggleMerchantBlock(Integer merchantId, Integer currencyId, OperationType operationType);

  void setBlockForAll(OperationType operationType, boolean blockStatus);

  void setBlockForMerchant(Integer merchantId, Integer currencyId, OperationType operationType, boolean blockStatus);

  boolean checkMerchantBlock(Integer merchantId, Integer currencyId, OperationType operationType);

  void setAutoWithdrawParamsByMerchantAndCurrency(Integer merchantId, Integer currencyId, Boolean withdrawAutoEnabled, Integer withdrawAutoDelaySeconds, BigDecimal withdrawAutoThresholdAmount);

  MerchantCurrencyAutoParamDto findAutoWithdrawParamsByMerchantAndCurrency(Integer merchantId, Integer currencyId);

  List<String> retrieveBtcCoreBasedMerchantNames();

  Optional<String> retrieveCoreWalletCurrencyNameByMerchant(String merchantName);

  List<MerchantCurrencyLifetimeDto> findMerchantCurrencyWithRefillLifetime();

  MerchantCurrencyLifetimeDto findMerchantCurrencyLifetimeByMerchantIdAndCurrencyId(Integer merchantId, Integer currencyId);

  MerchantCurrencyScaleDto findMerchantCurrencyScaleByMerchantIdAndCurrencyId(Integer merchantId, Integer currencyId);

  boolean getSubtractFeeFromAmount(Integer merchantId, Integer currencyId);

  void setSubtractFeeFromAmount(Integer merchantId, Integer currencyId, boolean subtractFeeFromAmount);

    List<MerchantCurrencyBasicInfoDto> findTokenMerchantsByParentId(Integer parentId);
}