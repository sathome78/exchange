package me.exrates.service;

import lombok.SneakyThrows;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.Transaction;
import me.exrates.model.dto.MerchantCurrencyBasicInfoDto;
import me.exrates.model.dto.MerchantCurrencyLifetimeDto;
import me.exrates.model.dto.MerchantCurrencyOptionsDto;
import me.exrates.model.dto.MerchantCurrencyScaleDto;
import me.exrates.model.dto.merchants.btc.CoreWalletDto;
import me.exrates.model.dto.mobileApiDto.MerchantCurrencyApiDto;
import me.exrates.model.dto.mobileApiDto.TransferMerchantApiDto;
import me.exrates.model.enums.OperationType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface MerchantService {

    List<Merchant> findAllByCurrency(Currency currency);

    List<Merchant> findAll();

    String resolveTransactionStatus(Transaction transaction, Locale locale);

    String sendDepositNotification(String toWallet, String email,
                                   Locale locale, CreditsOperation creditsOperation, String depositNotification);

    Merchant findById(int id);

    Merchant findByName(String name);

    List<MerchantCurrency> getAllUnblockedForOperationTypeByCurrencies(List<Integer> currenciesId, OperationType operationType);

    List<MerchantCurrencyApiDto> findNonTransferMerchantCurrencies(Integer currencyId);

    Optional<MerchantCurrency> findByMerchantAndCurrency(int merchantId, int currencyId);

    List<TransferMerchantApiDto> findTransferMerchants();

    List<MerchantCurrencyOptionsDto> findMerchantCurrencyOptions(List<String> processTypes);

    Map<String, String> formatResponseMessage(CreditsOperation creditsOperation);

    Map<String, String> formatResponseMessage(Transaction transaction);

    void toggleSubtractMerchantCommissionForWithdraw(String merchantName, String currencyName, boolean subtractMerchantCommissionForWithdraw);

    void toggleMerchantBlock(Integer merchantId, Integer currencyId, OperationType operationType);

    void setBlockForAll(OperationType operationType, boolean blockStatus);

    void setBlockForMerchant(Integer merchantId, Integer currencyId, OperationType operationType, boolean blockStatus);

    BigDecimal getMinSum(Integer merchantId, Integer currencyId);

    void checkAmountForMinSum(Integer merchantId, Integer currencyId, BigDecimal amount);

    List<MerchantCurrencyLifetimeDto> getMerchantCurrencyWithRefillLifetime();

    MerchantCurrencyLifetimeDto getMerchantCurrencyLifetimeByMerchantIdAndCurrencyId(Integer merchantId, Integer currencyId);

    MerchantCurrencyScaleDto getMerchantCurrencyScaleByMerchantIdAndCurrencyId(Integer merchantId, Integer currencyId);

    void checkMerchantIsBlocked(Integer merchantId, Integer currencyId, OperationType operationType);

    List<String> retrieveBtcCoreBasedMerchantNames();

    CoreWalletDto retrieveCoreWalletByMerchantName(String merchantName, Locale locale);

    List<CoreWalletDto> retrieveCoreWallets(Locale locale);

    Optional<String> getCoreWalletPassword(String merchantName, String currencyName);

    @SneakyThrows
    Properties getPassMerchantProperties(String merchantName);

    Map<String, String> computeCommissionAndMapAllToString(BigDecimal amount,
                                                           OperationType type,
                                                           String currency,
                                                           String merchant);

    void checkDestinationTag(Integer merchantId, String memo);

    boolean isValidDestinationAddress(Integer merchantId, String address);

    List<String> getWarningsForMerchant(OperationType operationType, Integer merchantId, Locale locale);

    List<Integer> getIdsByProcessType(List<String> processType);

    boolean getSubtractFeeFromAmount(Integer merchantId, Integer currencyId);

    void setSubtractFeeFromAmount(Integer merchantId, Integer currencyId, boolean subtractFeeFromAmount);

    List<MerchantCurrencyBasicInfoDto> findTokenMerchantsByParentId(Integer parentId);

    BigDecimal getMerchantInputCommission(int merchantId, int currencyId, String childMerchant);

    boolean setPropertyRecalculateCommissionLimitToUsd(String merchantName, String currencyName, Boolean recalculateToUsd);

    void updateMerchantCommissionsLimits();
}
