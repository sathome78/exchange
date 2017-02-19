package me.exrates.service.impl;

import javafx.util.Pair;
import me.exrates.dao.MerchantDao;
import me.exrates.dao.WithdrawRequestDao;
import me.exrates.model.*;
import me.exrates.model.Currency;
import me.exrates.model.dto.MerchantCurrencyOptionsDto;
import me.exrates.model.dto.mobileApiDto.MerchantCurrencyApiDto;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.enums.NotificationEvent;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.WithdrawalRequestStatus;
import me.exrates.model.enums.invoice.InvoiceRequestStatusEnum;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.model.vo.CacheData;
import me.exrates.model.vo.WithdrawData;
import me.exrates.service.*;
import me.exrates.service.exception.MerchantCurrencyBlockedException;
import me.exrates.service.exception.MerchantInternalException;
import me.exrates.service.exception.UnsupportedMerchantException;
import me.exrates.service.util.Cache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.math.BigDecimal.*;
import static java.math.BigDecimal.valueOf;
import static java.util.Collections.singletonMap;
import static me.exrates.model.enums.OperationType.*;
import static me.exrates.model.enums.WithdrawalRequestStatus.*;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
public class MerchantServiceImpl implements MerchantService {

  @Autowired
  private MerchantDao merchantDao;

  @Autowired
  private CommissionService commissionService;

  @Autowired
  private UserService userService;

  @Autowired
  private CurrencyService currencyService;

  @Autowired
  private SendMailService sendMailService;

  @Autowired
  private MessageSource messageSource;

  @Autowired
  private TransactionService transactionService;

  @Autowired
  private WithdrawRequestDao withdrawRequestDao;

  @Autowired
  private WalletService walletService;

  @Autowired
  private NotificationService notificationService;

  @Autowired
  private InvoiceService invoiceService;

  private static final BigDecimal HUNDREDTH = new BigDecimal(100L);
  private static final Logger LOG = LogManager.getLogger("merchant");

  @Override
  public Map<String, String> acceptWithdrawalRequest(final int requestId,
                                                     final Locale locale,
                                                     final Principal principal) {
    final Optional<WithdrawRequest> withdraw = withdrawRequestDao.findById(requestId);
    if (!withdraw.isPresent()) {
      return singletonMap("error", messageSource.getMessage("merchants.WithdrawRequestError", null, locale));
    }
    final WithdrawRequest request = withdraw.get();
    request.setProcessedBy(principal.getName());
    request.setStatus(ACCEPTED);
    withdrawRequestDao.update(request);
    final Optional<WithdrawRequest> withdrawUpdated = withdrawRequestDao.findById(requestId);
    if (!withdrawUpdated.isPresent()) {
      return singletonMap("error", messageSource.getMessage("merchants.WithdrawRequestError", null, locale));
    }
    final WithdrawRequest requestUpdated = withdrawUpdated.get();
    transactionService.provideTransaction(request.getTransaction());
    Locale userLocale = new Locale(userService.getPreferedLang(request.getUserId()));
    sendWithdrawalNotification(request, ACCEPTED, userLocale);
    final HashMap<String, String> params = new HashMap<>();
    final String message = messageSource.getMessage("merchants.WithdrawRequestAccept", null, locale);
    params.put("success", message);
    params.put("acceptance", requestUpdated.getAcceptance().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    params.put("email", principal.getName());
    return params;
  }

  @Override
  public Map<String, Object> declineWithdrawalRequest(final int requestId, final Locale locale, String email) {
    final Optional<WithdrawRequest> withdraw = withdrawRequestDao.findById(requestId);
    if (!withdraw.isPresent()) {
      return singletonMap("error", messageSource.getMessage("merchants.WithdrawRequestError", null, locale));
    }
    final WithdrawRequest request = withdraw.get();
    request.setProcessedBy(email);
    request.setStatus(DECLINED);
    final Transaction transaction = request.getTransaction();
    withdrawRequestDao.update(request);
    final Optional<WithdrawRequest> withdrawUpdated = withdrawRequestDao.findById(requestId);
    if (!withdrawUpdated.isPresent()) {
      return singletonMap("error", messageSource.getMessage("merchants.WithdrawRequestError", null, locale));
    }
    final WithdrawRequest requestUpdated = withdrawUpdated.get();
    transactionService.nullifyTransactionAmountForWithdraw(requestUpdated.getTransaction());
    final BigDecimal amount = transaction.getAmount().add(transaction.getCommissionAmount());
    walletService.withdrawReservedBalance(transaction.getUserWallet(), amount);
    walletService.depositActiveBalance(transaction.getUserWallet(), amount);
    Locale userLocale = new Locale(userService.getPreferedLang(request.getUserId()));
    sendWithdrawalNotification(request, DECLINED, userLocale);
    final HashMap<String, Object> params = new HashMap<>();
    final String message = messageSource.getMessage("merchants.WithdrawRequestDecline", null, locale);
    params.put("success", message);
    params.put("acceptance", requestUpdated.getAcceptance().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    params.put("email", email);
    params.put("userEmail", request.getUserEmail());
    return params;
  }

  @Override
  public List<WithdrawRequest> findAllWithdrawRequests() {
    return withdrawRequestDao.findAll();
  }

  @Override
  public List<Merchant> findAllByCurrency(Currency currency) {
    return merchantDao.findAllByCurrency(currency.getId());
  }

  @Override
  public List<Merchant> findAll() {
    return merchantDao.findAll();
  }

  @Override
  @Transactional
  public Map<String, String> withdrawRequest(final CreditsOperation creditsOperation,
                                             WithdrawData withdrawData, final String userEmail, final Locale locale) {
    final Transaction transaction = transactionService.createTransactionRequest(creditsOperation);
    final BigDecimal reserved = transaction
        .getAmount()
        .add(transaction.getCommissionAmount()).setScale(currencyService.resolvePrecision(creditsOperation.getCurrency().getName()), BigDecimal.ROUND_HALF_UP);
    walletService.depositReservedBalance(transaction.getUserWallet(), reserved);
    final WithdrawRequest request = new WithdrawRequest();
    request.setUserEmail(userEmail);
    if (creditsOperation.getDestination().isPresent() && !creditsOperation.getDestination().get().isEmpty()) {
      request.setWallet(creditsOperation.getDestination().get());
    } else {
      request.setWallet(withdrawData.getUserAccount());
    }
    creditsOperation
        .getMerchantImage()
        .ifPresent(request::setMerchantImage);
    request.setTransaction(transaction);
    request.setRecipientBankName(withdrawData.getRecipientBankName());
    request.setRecipientBankCode(withdrawData.getRecipientBankCode());
    request.setUserFullName(withdrawData.getUserFullName());
    request.setRemark(withdrawData.getRemark());
    withdrawRequestDao.create(request);
    String notification = null;
    try {
      notification = sendWithdrawalNotification(request, NEW, locale);
    } catch (final MailException e) {
      LOG.error(e);
    }
    final BigDecimal newAmount = transaction
        .getUserWallet()
        .getActiveBalance();
    final String currency = transaction
        .getCurrency()
        .getName();
    final String balance = currency + " " + currencyService.amountToString(newAmount, currency);
    final Map<String, String> result = new HashMap<>();
    result.put("success", notification);
    result.put("balance", balance);
    return result;
  }

  @Override
  public String resolveTransactionStatus(final Transaction transaction, final Locale locale) {
    if (transaction.getSourceType() == TransactionSourceType.INVOICE) {
      Integer statusId = invoiceService.getInvoiceRequestStatusByInvoiceId(transaction.getSourceId());
      InvoiceRequestStatusEnum invoiceRequestStatus = InvoiceRequestStatusEnum.convert(statusId);
      return messageSource.getMessage("merchants.invoice.".concat(invoiceRequestStatus.name()), null, locale);
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
  public String sendWithdrawalNotification(final WithdrawRequest withdrawRequest,
                                           final WithdrawalRequestStatus status,
                                           final Locale locale) {
    final String notification;
    final Transaction transaction = withdrawRequest.getTransaction();
    final Object[] messageParams = {
        transaction
            .getId(),
        transaction
            .getMerchant()
            .getDescription()
    };
    String notificationMessageCode;
    switch (status) {
      case NEW:
        notificationMessageCode = "merchants.withdrawNotification";
        break;
      case ACCEPTED:
        notificationMessageCode = "merchants.withdrawNotificationAccepted";
        break;
      case DECLINED:
        notificationMessageCode = "merchants.withdrawNotificationDeclined";
        break;
      default:
        throw new MerchantInternalException(status + "Withdrawal status is invalid");
    }
    notification = messageSource
        .getMessage(notificationMessageCode, messageParams, locale);
    notificationService.notifyUser(withdrawRequest.getUserEmail(), NotificationEvent.IN_OUT,
        "merchants.withdrawNotification.header", notificationMessageCode, messageParams);
    return notification;
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

  @Override
  public Map<Integer, List<Merchant>> mapMerchantsToCurrency(List<Currency> currencies) {
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
  public List<MerchantCurrency> findAllByCurrencies(List<Integer> currenciesId, OperationType operationType) {
    if (currenciesId.isEmpty()) {
      return null;
    }
    return merchantDao.findAllByCurrencies(currenciesId, operationType);
  }

  @Override
  public List<MerchantCurrencyApiDto> findAllMerchantCurrencies(Integer currencyId) {
    return merchantDao.findAllMerchantCurrencies(currencyId, userService.getCurrentUserRole());
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
  public Map<String, String> computeCommissionAndMapAllToString(final BigDecimal amount,
                                                                final OperationType type,
                                                                final String currency,
                                                                final String merchant) {
    final Map<String, String> result = new HashMap<>();
    final BigDecimal commission = commissionService.findCommissionByTypeAndRole(type, userService.getCurrentUserRole()).getValue();

    final BigDecimal commissionMerchant = type == USER_TRANSFER ? BigDecimal.ZERO : commissionService.getCommissionMerchant(merchant, currency);
    final BigDecimal commissionTotal = type == OUTPUT ? commission.add(commissionMerchant).setScale(currencyService.resolvePrecision(currency), ROUND_HALF_UP) :
        commission;
    BigDecimal commissionAmount = amount.multiply(commissionTotal).divide(HUNDREDTH).setScale(currencyService.resolvePrecision(currency), ROUND_HALF_UP);

    //TODO fix method
    //     commissionAmount = addMinimalCommission(commissionAmount, currency);

    final BigDecimal resultAmount = type != OUTPUT ? amount.add(commissionAmount).setScale(currencyService.resolvePrecision(currency), ROUND_HALF_UP) :
        amount.subtract(commissionAmount).setScale(currencyService.resolvePrecision(currency), ROUND_DOWN);
    result.put("commission", commissionTotal.stripTrailingZeros().toString());
    result.put("commissionAmount", currencyService.amountToString(commissionAmount, currency));
    result.put("amount", currencyService.amountToString(resultAmount, currency));
    return result;
  }

  @Override
  public Optional<CreditsOperation> prepareCreditsOperation(Payment payment, BigDecimal addition, String userEmail) {
    checkMerchantBlock(payment.getMerchant(), payment.getCurrency(), payment.getOperationType());
    final OperationType operationType = payment.getOperationType();
    BigDecimal amount = valueOf(payment.getSum()).add(addition);
    //Addition of three digits is required for IDR input
    final Merchant merchant = merchantDao.findById(payment.getMerchant());
    final Currency currency = currencyService.findById(payment.getCurrency());
    final String destination = payment.getDestination();
    final MerchantImage merchantImage = new MerchantImage();
    merchantImage.setId(payment.getMerchantImage());
    try {
      if (!isPayable(merchant, currency, amount)) {
        LOG.warn("Merchant respond as not support this pay " + payment);
        return Optional.empty();
      }
    } catch (EmptyResultDataAccessException e) {
      final String exceptionMessage = "MerchantService".concat(operationType == INPUT ?
          "Input" : "Output");
      throw new UnsupportedMerchantException(exceptionMessage);
    }
    final Commission commissionByType = commissionService.findCommissionByTypeAndRole(operationType, userService.getCurrentUserRole());
    final BigDecimal commissionMerchant = commissionService.getCommissionMerchant(merchant.getName(), currency.getName());
    final BigDecimal commissionTotal = operationType == OUTPUT ? commissionByType.getValue().add(commissionMerchant)
        .setScale(currencyService.resolvePrecision(currency.getName()), ROUND_HALF_UP) :
        commissionByType.getValue();
    BigDecimal commissionAmount =
        commissionTotal
            .multiply(amount)
            .divide(valueOf(100), currencyService.resolvePrecision(currency.getName()), ROUND_HALF_UP);
    //  commissionAmount = addMinimalCommission(commissionAmount, currency.getName());
    final User user = userService.findByEmail(userEmail);
    final BigDecimal newAmount = payment.getOperationType() == INPUT ?
        amount :
        amount.subtract(commissionAmount).setScale(currencyService.resolvePrecision(currency.getName()), ROUND_DOWN);
    final CreditsOperation creditsOperation = new CreditsOperation.Builder()
        .amount(newAmount)
        .commissionAmount(commissionAmount)
        .commission(commissionByType)
        .operationType(operationType)
        .user(user)
        .currency(currency)
        .merchant(merchant)
        .destination(destination)
        .merchantImage(merchantImage)
        .transactionSourceType(TransactionSourceType.convert(merchant.getTransactionSourceTypeId()))
        .build();
    return Optional.of(creditsOperation);
  }

  public Optional<CreditsOperation> prepareCreditsOperation(Payment payment, String userEmail) {
    return prepareCreditsOperation(payment, BigDecimal.ZERO, userEmail);
  }

  private BigDecimal addMinimalCommission(BigDecimal commissionAmount, String name) {
    if (commissionAmount.compareTo(BigDecimal.ZERO) == 0) {
      if (currencyService.resolvePrecision(name) == 2) {
        commissionAmount = commissionAmount.add(new BigDecimal("0.01"));
      } else {
        commissionAmount = commissionAmount.add(new BigDecimal("0.00000001"));
      }
    }
    return commissionAmount;
  }

  private boolean isPayable(Merchant merchant, Currency currency, BigDecimal sum) {
    final BigDecimal minSum = merchantDao.getMinSum(merchant.getId(), currency.getId());
    return sum.compareTo(minSum) >= 0;
  }

  @Override
  public List<MyInputOutputHistoryDto> getMyInputOutputHistory(CacheData cacheData, String email, Integer offset, Integer limit, Locale locale) {
    List<Integer> operationTypeList = OperationType.getInputOutputOperationsList()
        .stream()
        .map(e -> e.getType())
        .collect(Collectors.toList());
    List<MyInputOutputHistoryDto> result = merchantDao.findMyInputOutputHistoryByOperationType(email, offset, limit, operationTypeList, locale);
    if (Cache.checkCache(cacheData, result)) {
      result = new ArrayList<MyInputOutputHistoryDto>() {{
        add(new MyInputOutputHistoryDto(false));
      }};
    }
    return result;
  }

  @Override
  public List<MyInputOutputHistoryDto> getMyInputOutputHistory(String email, Integer offset, Integer limit, Locale locale) {
    List<Integer> operationTypeList = OperationType.getInputOutputOperationsList()
        .stream()
        .map(e -> e.getType())
        .collect(Collectors.toList());
    return merchantDao.findMyInputOutputHistoryByOperationType(email, offset, limit, operationTypeList, locale);
  }

  @Override
  public boolean checkInputRequestsLimit(int merchantId, String email) {
    boolean inLimit = merchantDao.getInputRequests(merchantId, email) < 10;

    return inLimit;
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


  private void checkMerchantBlock(Integer merchantId, Integer currencyId, OperationType operationType) {
    boolean isBlocked = merchantDao.checkMerchantBlock(merchantId, currencyId, operationType);
    if (isBlocked) {
      throw new MerchantCurrencyBlockedException("Operation " + operationType + " is blocked for this currency! ");
    }
  }


}
