package me.exrates.service.impl;

import lombok.SneakyThrows;
import me.exrates.dao.MerchantDao;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Currency;
import me.exrates.model.Email;
import me.exrates.model.Merchant;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.Transaction;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.model.dto.MerchantCurrencyBasicInfoDto;
import me.exrates.model.dto.MerchantCurrencyLifetimeDto;
import me.exrates.model.dto.MerchantCurrencyOptionsDto;
import me.exrates.model.dto.MerchantCurrencyScaleDto;
import me.exrates.model.dto.api.RateDto;
import me.exrates.model.dto.merchants.btc.CoreWalletDto;
import me.exrates.model.dto.mobileApiDto.MerchantCurrencyApiDto;
import me.exrates.model.dto.mobileApiDto.TransferMerchantApiDto;
import me.exrates.model.enums.*;
import me.exrates.model.enums.invoice.RefillStatusEnum;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.service.BitcoinService;
import me.exrates.service.CommissionService;
import me.exrates.service.CurrencyService;
import me.exrates.service.EDCServiceNode;
import me.exrates.service.MerchantService;
import me.exrates.service.SendMailService;
import me.exrates.service.UserService;
import me.exrates.service.api.ExchangeApi;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.exception.MerchantCurrencyBlockedException;
import me.exrates.service.exception.MerchantNotFoundException;
import me.exrates.service.exception.MerchantServiceBeanNameNotDefinedException;
import me.exrates.service.exception.MerchantServiceNotFoundException;
import me.exrates.service.exception.NoRequestedBeansFoundException;
import me.exrates.service.exception.ScaleForAmountNotSetException;
import me.exrates.service.merchantStrategy.IMerchantService;
import me.exrates.service.merchantStrategy.IRefillable;
import me.exrates.service.merchantStrategy.ITransferable;
import me.exrates.service.merchantStrategy.IWithdrawable;
import me.exrates.service.merchantStrategy.MerchantServiceContext;
import me.exrates.service.util.BigDecimalConverter;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.math.BigDecimal.ROUND_DOWN;
import static java.math.BigDecimal.ROUND_HALF_UP;
import static java.util.Objects.isNull;
import static me.exrates.configurations.CacheConfiguration.MERCHANT_BY_NAME_CACHE;
import static me.exrates.model.enums.OperationType.OUTPUT;
import static me.exrates.model.enums.OperationType.USER_TRANSFER;
import static me.exrates.service.util.CollectionUtil.isEmpty;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
@PropertySource("classpath:/merchants.properties")
@Conditional(MonolitConditional.class)
public class MerchantServiceImpl implements MerchantService {

    private static final Logger LOG = LogManager.getLogger("merchant");

    private static final BigDecimal HUNDREDTH = new BigDecimal(100L);

    @Value("${btc.walletspass.folder}")
    private String walletPropsFolder;

    @Autowired
    private MerchantDao merchantDao;
    @Autowired
    private UserService userService;
    @Autowired
    private SendMailService sendMailService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private MerchantServiceContext merchantServiceContext;
    @Autowired
    private CommissionService commissionService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private ExchangeApi exchangeApi;
    @Autowired
    private BigDecimalConverter converter;
    @Autowired
    @Qualifier(MERCHANT_BY_NAME_CACHE)
    private Cache merchantByNameCache;
    @Autowired
    @Qualifier("bitcoinServiceImpl")
    private BitcoinService bitcoinService;
    private EDCServiceNode edcServiceNode;

    @Override
    public List<Merchant> findAllByCurrency(Currency currency) {
        return merchantDao.findAllByCurrency(currency.getId());
    }

    @Override
    public List<Merchant> findAll() {
        return merchantDao.findAll();
    }

    @Override
    public String resolveTransactionStatus(final Transaction transaction, final Locale locale) {
        if (transaction.getSourceType() == TransactionSourceType.WITHDRAW) {
            WithdrawStatusEnum status = transaction.getWithdrawRequest().getStatus();
            return messageSource.getMessage("merchants.withdraw.".concat(status.name()), null, locale);
        }
        if (transaction.getSourceType() == TransactionSourceType.REFILL) {
            RefillStatusEnum status = transaction.getRefillRequest().getStatus();
            Integer confirmations = transaction.getRefillRequest().getConfirmations();
            return messageSource.getMessage("merchants.refill.".concat(status.name()), new Object[]{confirmations}, locale);
        }
        if (transaction.isProvided()) {
            return messageSource.getMessage("transaction.provided", null, locale);
        } else {
            return messageSource.getMessage("transaction.notProvided", null, locale);
        }
    }

    @Override
    public String sendDepositNotification(final String toWallet,
                                          final String email,
                                          final Locale locale,
                                          final CreditsOperation creditsOperation,
                                          final String depositNotification) {
        final BigDecimal amount = creditsOperation
                .getAmount()
                .add(creditsOperation.getCommissionAmount());
        final String sumWithCurrency = BigDecimalProcessing.formatSpacePoint(amount, false) + " " +
                creditsOperation
                        .getCurrency()
                        .getName();
        final String notification = messageSource.getMessage(depositNotification,
                new Object[]{sumWithCurrency, toWallet},
                locale);
        final Email mail = new Email();
        mail.setTo(email);
        mail.setSubject(messageSource
                .getMessage("merchants.depositNotification.header", null, locale));
        mail.setMessage(notification);

        try {
      /* TODO temporary disable
      notificationService.createLocalizedNotification(email, NotificationEvent.IN_OUT,
          "merchants.depositNotification.header", depositNotification,
          new Object[]{sumWithCurrency, toWallet});*/
            sendMailService.sendInfoMail(mail);
        } catch (MailException e) {
            LOG.error(e);
        }
        return notification;
    }

    private Map<Integer, List<Merchant>> mapMerchantsToCurrency(List<Currency> currencies) {
        return currencies.stream()
                .map(Currency::getId)
                .map(currencyId -> Pair.of(currencyId, merchantDao.findAllByCurrency(currencyId)))
                .collect(Collectors.toMap(
                        Pair::getKey,
                        Pair::getValue));
    }

    @Override
    public Merchant findById(int id) {
        return merchantDao.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Merchant findByName(String name) {
        if (isNull(merchantByNameCache)) {
            return merchantDao.findByName(name);
        }
        return merchantByNameCache.get(name, () -> merchantDao.findByName(name));
    }

    @Override
    public List<MerchantCurrency> getAllUnblockedForOperationTypeByCurrencies(List<Integer> currenciesId, OperationType operationType) {
        if (currenciesId.isEmpty()) {
            return null;
        }
        return merchantDao.findAllUnblockedForOperationTypeByCurrencies(currenciesId, operationType);
    }

    @Override
    public List<MerchantCurrencyApiDto> findNonTransferMerchantCurrencies(Integer currencyId) {
        return findMerchantCurrenciesByCurrencyAndProcessTypes(currencyId, Arrays.stream(MerchantProcessType.values())
                .filter(item -> item != MerchantProcessType.TRANSFER).map(Enum::name).collect(Collectors.toList()));
    }

    @Override
    public Optional<MerchantCurrency> findByMerchantAndCurrency(int merchantId, int currencyId) {
        return merchantDao.findByMerchantAndCurrency(merchantId, currencyId);
    }

    @Override
    public List<TransferMerchantApiDto> findTransferMerchants() {
        List<TransferMerchantApiDto> result = merchantDao.findTransferMerchants();
        result.forEach(item -> {
            IMerchantService merchantService = merchantServiceContext.getMerchantService(item.getServiceBeanName());
            if (merchantService instanceof ITransferable) {
                ITransferable transferService = (ITransferable) merchantService;
                item.setIsVoucher(transferService.isVoucher());
                item.setRecipientUserIsNeeded(transferService.recipientUserIsNeeded());
            }
        });
        return result;
    }


    private List<MerchantCurrencyApiDto> findMerchantCurrenciesByCurrencyAndProcessTypes(Integer currencyId, List<String> processTypes) {
        List<MerchantCurrencyApiDto> result = merchantDao.findAllMerchantCurrencies(currencyId, userService.getUserRoleFromSecurityContext(), processTypes);
        result.forEach(item -> {
            try {
                IMerchantService merchantService = merchantServiceContext.getMerchantService(item.getServiceBeanName());
                if (merchantService instanceof IWithdrawable) {
                    IWithdrawable withdrawService = (IWithdrawable) merchantService;
                    if (withdrawService.additionalTagForWithdrawAddressIsUsed()) {
                        item.setAdditionalFieldName(withdrawService.additionalWithdrawFieldName());
                        item.setWithdrawCommissionDependsOnDestinationTag(withdrawService.comissionDependsOnDestinationTag());
                    }
                } else if (merchantService instanceof IRefillable) {
                    if (((IRefillable) merchantService).additionalFieldForRefillIsUsed()) {
                        item.setAdditionalFieldName(((IRefillable) merchantService).additionalRefillFieldName());
                    }
                }
            } catch (MerchantServiceNotFoundException | MerchantServiceBeanNameNotDefinedException e) {
                LOG.warn(e);
            }
        });
        return result;
    }

    @Override
    public List<MerchantCurrencyOptionsDto> findMerchantCurrencyOptions(List<String> processTypes) {
        return merchantDao.findMerchantCurrencyOptions(processTypes);
    }

    @Override
    public Map<String, String> formatResponseMessage(CreditsOperation creditsOperation) {
        final OperationType operationType = creditsOperation.getOperationType();
        final String commissionPercent = creditsOperation
                .getCommission()
                .getValue()
                .setScale(2, ROUND_HALF_UP)
                .toString();
        String finalAmount = null;
        String sumCurrency = null;
        switch (operationType) {
            case INPUT:
                finalAmount = creditsOperation
                        .getAmount()
                        .setScale(2, ROUND_HALF_UP) + " "
                        + creditsOperation
                        .getCurrency()
                        .getName();
                sumCurrency = creditsOperation
                        .getAmount()
                        .add(creditsOperation.getCommissionAmount())
                        .setScale(2, ROUND_HALF_UP) + " "
                        + creditsOperation
                        .getCurrency()
                        .getName();
                break;
            case OUTPUT:
                finalAmount = creditsOperation
                        .getAmount()
                        .subtract(creditsOperation.getCommissionAmount())
                        .setScale(2, ROUND_HALF_UP) + " "
                        + creditsOperation
                        .getCurrency()
                        .getName();
                sumCurrency = creditsOperation
                        .getAmount()
                        .setScale(2, ROUND_HALF_UP) + " "
                        + creditsOperation
                        .getCurrency()
                        .getName();
                break;

        }
        final Map<String, String> result = new HashMap<>();
        result.put("commissionPercent", commissionPercent);
        result.put("sumCurrency", sumCurrency);
        result.put("finalAmount", finalAmount);
        return result;
    }

    @Override
    public Map<String, String> formatResponseMessage(Transaction transaction) {
        final CreditsOperation creditsOperation = new CreditsOperation.Builder()
                .operationType(transaction.getOperationType())
                .amount(transaction.getAmount())
                .commissionAmount(transaction.getCommissionAmount())
                .commission(transaction.getCommission())
                .currency(transaction.getCurrency())
                .build();
        return formatResponseMessage(creditsOperation);

    }

    @Override
    public void toggleSubtractMerchantCommissionForWithdraw(String merchantName, String currencyName, boolean subtractMerchantCommissionForWithdraw) {
        merchantDao.toggleSubtractMerchantCommissionForWithdraw(merchantName, currencyName, subtractMerchantCommissionForWithdraw);
    }

    @Override
    @Transactional
    public void toggleMerchantBlock(Integer merchantId, Integer currencyId, OperationType operationType) {
        merchantDao.toggleMerchantBlock(merchantId, currencyId, operationType);
    }

    @Override
    @Transactional
    public void setBlockForAll(OperationType operationType, boolean blockStatus) {

        if (blockStatus) {
            if (merchantDao.isBlockStateValid(operationType)) {
                merchantDao.backupBlockState(operationType);
            }
            merchantDao.setBlockForAllNonTransfer(operationType);
        } else {
            //check for do not restore all 1 or all 0
            if (merchantDao.isBlockStateBackupValid(operationType)) {
                merchantDao.restoreBlockState(operationType);
            }
        }
    }

    @Override
    @Transactional
    public void setBlockForMerchant(Integer merchantId, Integer currencyId, OperationType operationType, boolean blockStatus) {
        merchantDao.setBlockForMerchant(merchantId, currencyId, operationType, blockStatus);
    }

    @Override
    @Transactional
    public BigDecimal getMinSum(Integer merchantId, Integer currencyId) {
        return merchantDao.getMinSum(merchantId, currencyId);
    }

    @Override
    @Transactional
    public void checkAmountForMinSum(Integer merchantId, Integer currencyId, BigDecimal amount) {
        if (amount.compareTo(getMinSum(merchantId, currencyId)) < 0) {
            throw new InvalidAmountException(String.format("merchant: %s currency: %s amount %s", merchantId, currencyId, amount.toString()));
        }
    }

    /*============================*/

    @Override
    @Transactional
    public List<MerchantCurrencyLifetimeDto> getMerchantCurrencyWithRefillLifetime() {
        return merchantDao.findMerchantCurrencyWithRefillLifetime();
    }

    @Override
    @Transactional
    public MerchantCurrencyLifetimeDto getMerchantCurrencyLifetimeByMerchantIdAndCurrencyId(
            Integer merchantId,
            Integer currencyId) {
        return merchantDao.findMerchantCurrencyLifetimeByMerchantIdAndCurrencyId(merchantId, currencyId);
    }

    @Override
    @Transactional
    public MerchantCurrencyScaleDto getMerchantCurrencyScaleByMerchantIdAndCurrencyId(
            Integer merchantId,
            Integer currencyId) {
        MerchantCurrencyScaleDto result = merchantDao.findMerchantCurrencyScaleByMerchantIdAndCurrencyId(merchantId, currencyId);
        Optional.ofNullable(result.getScaleForRefill()).orElseThrow(() -> new ScaleForAmountNotSetException("currency: " + currencyId));
        Optional.ofNullable(result.getScaleForWithdraw()).orElseThrow(() -> new ScaleForAmountNotSetException("currency: " + currencyId));
        Optional.ofNullable(result.getScaleForTransfer()).orElseThrow(() -> new ScaleForAmountNotSetException("currency: " + currencyId));
        return result;
    }

    @Override
    @Transactional
    public void checkMerchantIsBlocked(Integer merchantId, Integer currencyId, OperationType operationType) {
        boolean isBlocked = merchantDao.checkMerchantBlock(merchantId, currencyId, operationType);
        if (isBlocked) {
            throw new MerchantCurrencyBlockedException("Operation " + operationType + " is blocked for this currency! ");
        }
    }

    @Override
    public List<String> retrieveBtcCoreBasedMerchantNames() {
        return merchantDao.retrieveBtcCoreBasedMerchantNames();
    }

    @Override
    public CoreWalletDto retrieveCoreWalletByMerchantName(String merchantName, Locale locale) {
        CoreWalletDto result = merchantDao.retrieveCoreWalletByMerchantName(merchantName).orElseThrow(() -> new MerchantNotFoundException(merchantName));
        result.localizeTitle(messageSource, locale);
        return result;
    }

    @Override
    public List<CoreWalletDto> retrieveCoreWallets(Locale locale) {

        List<CoreWalletDto> result = merchantDao.retrieveCoreWallets();
        result.forEach(dto -> dto.localizeTitle(messageSource, locale));
        return result;
    }


    @Override
    public Optional<String> getCoreWalletPassword(String merchantName, String currencyName) {
        Properties props = getPassMerchantProperties(merchantName);
        return Optional.ofNullable(props.getProperty("wallet.password"));
    }

    /*pass file format : classpath: merchants/pass/<merchant>_pass.properties
     * stored values: wallet.password
     *                node.bitcoind.rpc.user
     *                node.bitcoind.rpc.password
     * */
    @SneakyThrows
    @Override
    public Properties getPassMerchantProperties(String merchantName) {
        Properties props = new Properties();
        String fullPath = String.join("", walletPropsFolder, merchantName, "_pass.properties");
        FileInputStream inputStream = new FileInputStream(new File(fullPath));
        props.load(inputStream);
        return props;
    }

    @Override
    public Map<String, String> computeCommissionAndMapAllToString(final BigDecimal amount,
                                                                  final OperationType type,
                                                                  final String currency,
                                                                  final String merchant) {
        final Map<String, String> result = new HashMap<>();
        final BigDecimal commission = commissionService.findCommissionByTypeAndRole(type, userService.getUserRoleFromSecurityContext()).getValue();
        final BigDecimal commissionMerchant = type == USER_TRANSFER ? BigDecimal.ZERO : commissionService.getCommissionMerchant(merchant, currency, type);
        final BigDecimal commissionTotal = commission.add(commissionMerchant).setScale(currencyService.resolvePrecisionByOperationType(currency, type), ROUND_HALF_UP);
        BigDecimal commissionAmount = amount.multiply(commissionTotal).divide(HUNDREDTH).setScale(currencyService.resolvePrecisionByOperationType(currency, type), ROUND_HALF_UP);
        String commissionString = Stream.of("(", commissionTotal.stripTrailingZeros().toString(), "%)").collect(Collectors.joining(""));
        if (type == OUTPUT) {
            BigDecimal merchantMinFixedCommission = commissionService.getMinFixedCommission(currencyService.findByName(currency).getId(), this.findByName(merchant).getId());
            if (commissionAmount.compareTo(merchantMinFixedCommission) < 0) {
                commissionAmount = merchantMinFixedCommission;
                commissionString = "";
            }
        }
        LOG.debug("commission: " + commissionString);
        final BigDecimal resultAmount = type != OUTPUT ? amount.add(commissionAmount).setScale(currencyService.resolvePrecisionByOperationType(currency, type), ROUND_HALF_UP) :
                amount.subtract(commissionAmount).setScale(currencyService.resolvePrecisionByOperationType(currency, type), ROUND_DOWN);
        if (resultAmount.signum() <= 0) {
            throw new InvalidAmountException("merchants.invalidSum");
        }
        result.put("commission", commissionString);
        result.put("commissionAmount", currencyService.amountToString(commissionAmount, currency));
        result.put("amount", currencyService.amountToString(resultAmount, currency));
        return result;
    }

    @Override
    public void checkDestinationTag(Integer merchantId, String destinationTag) {
        IMerchantService merchantService = merchantServiceContext.getMerchantService(merchantId);
        if (merchantService instanceof IWithdrawable && ((IWithdrawable) merchantService).additionalTagForWithdrawAddressIsUsed()) {
            ((IWithdrawable) merchantService).checkDestinationTag(destinationTag);
        }
    }

    @Override
    public boolean isValidDestinationAddress(Integer merchantId, String address) {

        IMerchantService merchantService = merchantServiceContext.getMerchantService(merchantId);
        if (merchantService instanceof IWithdrawable) {
            return ((IWithdrawable) merchantService).isValidDestinationAddress(address);
        } else {
            return true;
        }
    }

    @Override
    public List<String> getWarningsForMerchant(OperationType operationType, Integer merchantId, Locale locale) {
        UserCommentTopicEnum commentTopic;
        switch (operationType) {
            case INPUT:
                commentTopic = UserCommentTopicEnum.REFILL_MERCHANT_WARNING;
                break;
            case OUTPUT:
                commentTopic = UserCommentTopicEnum.WITHDRAW_MERCHANT_WARNING;
                break;
            default:
                throw new IllegalArgumentException(String.format("Illegal operation type %s", operationType.name()));
        }
        List<String> result = currencyService.getWarningForMerchant(merchantId, commentTopic);
        LOG.info("Warning result: " + result);
        List<String> resultLocalized = currencyService.getWarningForMerchant(merchantId, commentTopic).stream()
                .map(code -> messageSource.getMessage(code, null, locale)).collect(Collectors.toList());
        LOG.info("Localized result: " + resultLocalized);
        return resultLocalized;
    }

    @Override
    public List<Integer> getIdsByProcessType(List<String> processType) {
        return merchantDao.findCurrenciesIdsByType(processType);
    }

    @Override
    public boolean getSubtractFeeFromAmount(Integer merchantId, Integer currencyId) {
        return merchantDao.getSubtractFeeFromAmount(merchantId, currencyId);
    }

    @Override
    public void setSubtractFeeFromAmount(Integer merchantId, Integer currencyId, boolean subtractFeeFromAmount) {
        merchantDao.setSubtractFeeFromAmount(merchantId, currencyId, subtractFeeFromAmount);
    }

    @Override
    public List<MerchantCurrencyBasicInfoDto> findTokenMerchantsByParentId(Integer parentId) {
        return merchantDao.findTokenMerchantsByParentId(parentId);
    }

    @Transactional(readOnly = true)
    @Override
    public BigDecimal getMerchantInputCommission(int merchantId, int currencyId, String childMerchant) {
        return merchantDao.getMerchantInputCommission(merchantId, currencyId, childMerchant);
    }

    @Override
    public boolean setPropertyRecalculateCommissionLimitToUsd(String merchantName, String currencyName, Boolean recalculateToUsd) {
        return merchantDao.setPropertyRecalculateCommissionLimitToUsd(merchantName, currencyName, recalculateToUsd);
    }

    @Transactional
    @Override
    public void updateMerchantCommissionsLimits() {
        StopWatch stopWatch = StopWatch.createStarted();
        LOG.info("Process of updating merchant commissions limits start...");

        List<MerchantCurrencyOptionsDto> merchantCommissionsLimits = merchantDao.getAllMerchantCommissionsLimits();
        if (isEmpty(merchantCommissionsLimits)) {
            return;
        }

        final Map<String, RateDto> rates = exchangeApi.getRates();
        if (rates.isEmpty()) {
            LOG.info("Exchange api did not return data");
            return;
        }

        for (MerchantCurrencyOptionsDto merchantCommissionsLimit : merchantCommissionsLimits) {
            final String currencyName = merchantCommissionsLimit.getCurrencyName();
            final boolean recalculateToUsd = merchantCommissionsLimit.isRecalculateToUsd();
            BigDecimal minFixedCommissionUsdRate = merchantCommissionsLimit.getMinFixedCommissionUsdRate();
            BigDecimal minFixedCommission = merchantCommissionsLimit.getMinFixedCommission();

            RateDto rateDto = rates.get(currencyName);
            if (isNull(rateDto)) {
                continue;
            }

            final BigDecimal usdRate = rateDto.getUsdRate();
            if (usdRate.compareTo(BigDecimal.ZERO) == 0) {
                continue;
            }

            merchantCommissionsLimit.setCurrencyUsdRate(usdRate);

            if (recalculateToUsd) {
                minFixedCommission = converter.convert(minFixedCommissionUsdRate.divide(usdRate, RoundingMode.HALF_UP));
                merchantCommissionsLimit.setMinFixedCommission(minFixedCommission);
            } else {
                minFixedCommissionUsdRate = minFixedCommission.multiply(usdRate);
                merchantCommissionsLimit.setMinFixedCommissionUsdRate(minFixedCommissionUsdRate);
            }
        }
        merchantDao.updateMerchantCommissionsLimits(merchantCommissionsLimits);
        LOG.info("Process of updating merchant commissions limits end... Time: {}", stopWatch.getTime(TimeUnit.MILLISECONDS));
    }

    @Override
    public boolean checkAvailableRefill(Integer currencyId, Integer merchantId) {
        return merchantDao.checkAvailable(currencyId, merchantId);
    }

    @Override
    public MerchantCurrency findMerchantForTransferByCurrencyId(Integer currencyId, TransferTypeVoucher transferType) {
        return merchantDao.getMerchantByCurrencyForVoucher(currencyId, transferType);
    }

    @Override
    @SneakyThrows
    public Map<String, String> getWalletBalanceByCurrencyName(String currencyName, String token, String address) {
        if (!token.equals("ZXzG8z13nApRXDzvOv7hU41kYHAJSLET")) {
            throw new RuntimeException("Some unexpected exception");
        }
        if (currencyName.equals("EDR")) {
            String balance = edcServiceNode.extractBalance(address, 0);
            Map<String, String> response = new HashMap<>();
            response.put("EDR", balance);
            return response;
        }
        Currency byName = currencyService.findByName(currencyName);

        List<Merchant> allByCurrency = findAllByCurrency(byName);
        List<Merchant> collect = allByCurrency
                .stream().
                        filter(merchant -> merchant.getProcessType() == MerchantProcessType.CRYPTO).collect(Collectors.toList());
        Map<String, String> collect1 = collect.
                stream().
                collect(Collectors.toMap(
                        Merchant::getName,
                        merchant -> getBitcoinServiceByMerchantName(merchant.getName()).getWalletInfo().getBalance()));


        return collect1;
    }

    private BitcoinService getBitcoinServiceByMerchantName(String merchantName) {
        String serviceBeanName = findByName(merchantName).getServiceBeanName();
        IMerchantService merchantService = merchantServiceContext.getMerchantService(serviceBeanName);
        if (merchantService == null || !(merchantService instanceof BitcoinService)) {
            throw new NoRequestedBeansFoundException(serviceBeanName);
        }
        return (BitcoinService) merchantService;
    }
}
