package me.exrates.service.impl;

import javafx.util.Pair;
import me.exrates.dao.MerchantDao;
import me.exrates.model.*;
import me.exrates.model.Currency;
import me.exrates.model.dto.MerchantCurrencyLifetimeDto;
import me.exrates.model.dto.MerchantCurrencyOptionsDto;
import me.exrates.model.dto.MerchantCurrencyScaleDto;
import me.exrates.model.dto.mobileApiDto.MerchantCurrencyApiDto;
import me.exrates.model.enums.NotificationEvent;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.invoice.InvoiceRequestStatusEnum;
import me.exrates.model.enums.invoice.PendingPaymentStatusEnum;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.service.*;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.exception.MerchantCurrencyBlockedException;
import me.exrates.service.exception.MerchantInternalException;
import me.exrates.service.exception.ScaleForAmountNotSetException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ROUND_HALF_UP;
import static me.exrates.model.enums.OperationType.OUTPUT;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
public class MerchantServiceImpl implements MerchantService {

  private static final Logger LOG = LogManager.getLogger("merchant");

  @Autowired
  private MerchantDao merchantDao;

  @Autowired
  private UserService userService;

  @Autowired
  private SendMailService sendMailService;

  @Autowired
  private MessageSource messageSource;

  @Autowired
  private NotificationService notificationService;

  @Autowired
  private InvoiceService invoiceService;

  @Autowired
  private BitcoinService bitcoinService;

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
    if (transaction.getSourceType() == TransactionSourceType.REFILL) {
      Integer statusId = invoiceService.getInvoiceRequestStatusByInvoiceId(transaction.getSourceId());
      InvoiceRequestStatusEnum invoiceRequestStatus = InvoiceRequestStatusEnum.convert(statusId);
      return messageSource.getMessage("merchants.invoice.".concat(invoiceRequestStatus.name()), null, locale);
    }
    if (transaction.getSourceType() == TransactionSourceType.WITHDRAW) {
      if (transaction.getOperationType() != OUTPUT) {
        return "";
      } else {
        WithdrawStatusEnum status = transaction.getWithdrawRequest().getStatus();
        return messageSource.getMessage("merchants.withdraw.".concat(status.name()), null, locale);
      }
    }
    if (transaction.getSourceType() == TransactionSourceType.REFILL) {
      Integer statusId = bitcoinService.getPendingPaymentStatusByInvoiceId(transaction.getSourceId());
      PendingPaymentStatusEnum pendingPaymentStatus = PendingPaymentStatusEnum.convert(statusId);
      String message = messageSource.getMessage("merchants.invoice.".concat(pendingPaymentStatus.name()), null, locale);
      if (message.isEmpty()) {
        message = messageSource.getMessage("transaction.confirmations",
            new Object[]{
                transaction.getConfirmation(),
                BitcoinService.CONFIRMATION_NEEDED_COUNT
            }, locale);
      }
      return message;
    }
    if (transaction.isProvided()) {
      return messageSource.getMessage("transaction.provided", null, locale);
    }
    if (transaction.getConfirmation() == null || transaction.getConfirmation() == -1) {
      return messageSource.getMessage("transaction.notProvided", null, locale);
    }
    final String name = transaction.getCurrency().getName();
    final int acceptableConfirmations;
    switch (name) {
      case "EDRC":
        acceptableConfirmations = EDRCService.CONFIRMATIONS;
        break;
      case "BTC":
        acceptableConfirmations = BlockchainService.CONFIRMATIONS;
        break;
      default:
        throw new MerchantInternalException("Unknown confirmations number on " + transaction.getCurrency() +
            " " + transaction.getMerchant());
    }
    return messageSource.getMessage("transaction.confirmations",
        new Object[]{
            transaction.getConfirmation(),
            acceptableConfirmations
        }, locale);
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
      notificationService.createLocalizedNotification(email, NotificationEvent.IN_OUT,
          "merchants.depositNotification.header", depositNotification,
          new Object[]{sumWithCurrency, toWallet});
      sendMailService.sendInfoMail(mail);
    } catch (MailException e) {
      LOG.error(e);
    }
    return notification;
  }

  private Map<Integer, List<Merchant>> mapMerchantsToCurrency(List<Currency> currencies) {
    return currencies.stream()
        .map(Currency::getId)
        .map(currencyId -> new Pair<>(currencyId, merchantDao.findAllByCurrency(currencyId)))
        .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
  }

  @Override
  public Merchant findById(int id) {
    return merchantDao.findById(id);
  }

  @Override
  public Merchant findByNName(String name) {
    return merchantDao.findByName(name);
  }

  @Override
  public List<MerchantCurrency> getAllUnblockedForOperationTypeByCurrencies(List<Integer> currenciesId, OperationType operationType) {
    if (currenciesId.isEmpty()) {
      return null;
    }
    return merchantDao.findAllUnblockedForOperationTypeByCurrencies(currenciesId, operationType);
  }

  @Override
  public List<MerchantCurrencyApiDto> findAllMerchantCurrencies(Integer currencyId) {
    return merchantDao.findAllMerchantCurrencies(currencyId, userService.getUserRoleFromSecurityContext());
  }

  @Override
  public List<MerchantCurrencyOptionsDto> findMerchantCurrencyOptions() {
    return merchantDao.findMerchantCurrencyOptions();
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
  @Transactional
  public void toggleMerchantBlock(Integer merchantId, Integer currencyId, OperationType operationType) {
    merchantDao.toggleMerchantBlock(merchantId, currencyId, operationType);
  }

  @Override
  @Transactional
  public void setBlockForAll(OperationType operationType, boolean blockStatus) {
    merchantDao.setBlockForAll(operationType, blockStatus);
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
    if (amount.compareTo(getMinSum(merchantId, currencyId)) < 0){
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
    Optional.ofNullable(result.getScaleForRefill()).orElseThrow(()->new ScaleForAmountNotSetException("currency: "+currencyId));
    Optional.ofNullable(result.getScaleForWithdraw()).orElseThrow(()->new ScaleForAmountNotSetException("currency: "+currencyId));
    return  result;
  }

  @Override
  @Transactional
  public void checkMerchantIsBlocked(Integer merchantId, Integer currencyId, OperationType operationType) {
    boolean isBlocked = merchantDao.checkMerchantBlock(merchantId, currencyId, operationType);
    if (isBlocked) {
      throw new MerchantCurrencyBlockedException("Operation " + operationType + " is blocked for this currency! ");
    }
  }


}
