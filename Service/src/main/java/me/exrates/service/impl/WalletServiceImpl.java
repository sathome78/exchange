package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.WalletDao;
import me.exrates.model.*;
import me.exrates.model.dto.*;
import me.exrates.model.dto.mobileApiDto.dashboard.MyWalletsStatisticsApiDto;
import me.exrates.model.dto.onlineTableDto.MyWalletsDetailedDto;
import me.exrates.model.dto.onlineTableDto.MyWalletsStatisticsDto;
import me.exrates.model.enums.*;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.enums.invoice.RefillStatusEnum;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.model.vo.CacheData;
import me.exrates.model.vo.WalletOperationData;
import me.exrates.service.*;
import me.exrates.service.exception.*;
import me.exrates.service.util.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ROUND_HALF_UP;
import static java.math.BigDecimal.ZERO;

@Log4j2
@Service
@Transactional
public final class WalletServiceImpl implements WalletService {

  private static final int decimalPlaces = 9;

  @Autowired
  private WalletDao walletDao;
  @Autowired
  private CurrencyService currencyService;
  @Autowired
  private UserService userService;
  @Autowired
  private CommissionService commissionService;
  @Autowired
  private CompanyWalletService companyWalletService;
  @Autowired
  private NotificationService notificationService;
  @Autowired
  private MessageSource messageSource;
  @Autowired
  private UserTransferService userTransferService;

  @Override
  public void balanceRepresentation(final Wallet wallet) {
    wallet
        .setActiveBalance(wallet.getActiveBalance());
//				.setScale(currencyService.resolvePrecision(wallet.getName()), ROUND_CEILING));
  }

  @Transactional(readOnly = true)
  @Override
  public List<Wallet> getAllWallets(int userId) {
    final List<Wallet> wallets = walletDao.findAllByUser(userId);
    wallets.forEach(this::balanceRepresentation);
    return wallets;
  }

  @Override
  public List<WalletFormattedDto> getAllUserWalletsForAdminDetailed(Integer userId) {
    return walletDao.getAllUserWalletsForAdminDetailed(userId,
            WithdrawStatusEnum.getEndStatesSet().stream().map(InvoiceStatus::getCode).collect(Collectors.toList()),
            WithdrawStatusEnum.getEndStatesSet().stream().filter(InvoiceStatus::isSuccessEndStatus).map(InvoiceStatus::getCode).collect(Collectors.toList()),
            RefillStatusEnum.getEndStatesSet().stream().filter(InvoiceStatus::isSuccessEndStatus).map(InvoiceStatus::getCode).collect(Collectors.toList()));
  }





  @Transactional(readOnly = true)
  @Override
  public List<MyWalletsDetailedDto> getAllWalletsForUserDetailed(CacheData cacheData,
                                                                 String email, Locale locale) {
    List<Integer> withdrawStatusIdForWhichMoneyIsReserved = WithdrawStatusEnum.getEndStatesSet().stream().map(InvoiceStatus::getCode).collect(Collectors.toList());
    List<MyWalletsDetailedDto> result = walletDao.getAllWalletsForUserDetailed(email, withdrawStatusIdForWhichMoneyIsReserved, locale);
    if (Cache.checkCache(cacheData, result)) {
      result = new ArrayList<MyWalletsDetailedDto>() {{
        add(new MyWalletsDetailedDto(false));
      }};
    }
    return result;
  }

  @Transactional(readOnly = true)
  @Override
  public List<MyWalletsDetailedDto> getAllWalletsForUserDetailed(String email, Locale locale) {
    List<Integer> withdrawStatusIdForWhichMoneyIsReserved = WithdrawStatusEnum.getEndStatesSet().stream().map(InvoiceStatus::getCode).collect(Collectors.toList());
    List<MyWalletsDetailedDto> result = walletDao.getAllWalletsForUserDetailed(email, withdrawStatusIdForWhichMoneyIsReserved, locale);
    return result;
  }

  @Transactional(readOnly = true)
  @Override
  public List<MyWalletsStatisticsDto> getAllWalletsForUserReduced(CacheData cacheData, String email, Locale locale) {
    List<MyWalletsStatisticsDto> result = walletDao.getAllWalletsForUserReduced(email, locale);
    if (Cache.checkCache(cacheData, result)) {
      result = new ArrayList<MyWalletsStatisticsDto>() {{
        add(new MyWalletsStatisticsDto(false));
      }};
    }
    return result;
  }

  @Override
  public int getWalletId(int userId, int currencyId) {
    return walletDao.getWalletId(userId, currencyId);
  }

  @Override
  @Transactional(propagation = Propagation.NESTED)
  public BigDecimal getWalletABalance(int walletId) {
    return walletDao.getWalletABalance(walletId);
  }

  @Override
  @Transactional(propagation = Propagation.NESTED)
  public BigDecimal getWalletRBalance(int walletId) {
    return walletDao.getWalletRBalance(walletId);
  }

  @Transactional(readOnly = true)
  @Override
  public boolean ifEnoughMoney(int walletId, BigDecimal amountForCheck) {
    BigDecimal balance = getWalletABalance(walletId);
    return balance.compareTo(amountForCheck) >= 0;
  }

  @Transactional(propagation = Propagation.NESTED)
  @Override
  public int createNewWallet(Wallet wallet) {
    return walletDao.createNewWallet(wallet);
  }

  @Override
  public int getUserIdFromWallet(int walletId) {
    return walletDao.getUserIdFromWallet(walletId);
  }

  @Override
  @Transactional(readOnly = true)
  public Wallet findByUserAndCurrency(User user, Currency currency) {
    return walletDao.findByUserAndCurrency(user.getId(), currency.getId());
  }

  @Override
  public Wallet create(User user, Currency currency) {
    final Wallet wallet = walletDao.createWallet(user, currency.getId());
    wallet.setName(currency.getName());
    return wallet;
  }

  @Override
  @Transactional(propagation = Propagation.NESTED)
  public void depositActiveBalance(final Wallet wallet, final BigDecimal sum) {
    walletDao.addToWalletBalance(wallet.getId(), sum, BigDecimal.ZERO);
  }

  @Override
  @Transactional(propagation = Propagation.NESTED)
  public void withdrawActiveBalance(final Wallet wallet, final BigDecimal sum) {
    final BigDecimal newBalance = wallet.getActiveBalance().subtract(sum).setScale(decimalPlaces, ROUND_HALF_UP);
    if (newBalance.compareTo(ZERO) < 0) {
      throw new NotEnoughUserWalletMoneyException("Not enough money to withdraw on user wallet " +
          wallet.toString());
    }
    walletDao.addToWalletBalance(wallet.getId(), sum.negate(), BigDecimal.ZERO);
  }

  @Override
  @Transactional(propagation = Propagation.NESTED)
  public void depositReservedBalance(final Wallet wallet, final BigDecimal sum) {
    wallet.setActiveBalance(wallet.getActiveBalance().subtract(sum).setScale(decimalPlaces, ROUND_HALF_UP));
    if (wallet.getActiveBalance().compareTo(ZERO) < 0) {
      throw new NotEnoughUserWalletMoneyException("Not enough money to withdraw on user wallet " + wallet);
    }
    walletDao.addToWalletBalance(wallet.getId(), sum.negate(), sum);
  }

  @Override
  @Transactional(propagation = Propagation.NESTED)
  public void withdrawReservedBalance(final Wallet wallet, final BigDecimal sum) {
    wallet.setReservedBalance(wallet.getReservedBalance().subtract(sum).setScale(decimalPlaces, ROUND_HALF_UP));
    if (wallet.getReservedBalance().compareTo(ZERO) < 0) {
      throw new NotEnoughUserWalletMoneyException("Not enough money to withdraw on user wallet " + wallet);
    }
    walletDao.addToWalletBalance(wallet.getId(), BigDecimal.ZERO, sum.negate());
  }

  @Override
  @Transactional
  public WalletTransferStatus walletInnerTransfer(int walletId, BigDecimal amount, TransactionSourceType sourceType, int sourceId) {
    return walletInnerTransfer(walletId, amount, sourceType, sourceId, null);
  }

  @Override
  @Transactional
  public WalletTransferStatus walletInnerTransfer(int walletId, BigDecimal amount, TransactionSourceType sourceType, int sourceId, String description) {
    return walletDao.walletInnerTransfer(walletId, amount, sourceType, sourceId, description);
  }

  @Override
  public WalletTransferStatus walletBalanceChange(final WalletOperationData walletOperationData) {
    return walletDao.walletBalanceChange(walletOperationData);
  }

  @Override
  public List<MyWalletConfirmationDetailDto> getWalletConfirmationDetail(Integer walletId, Locale locale) {
    return walletDao.getWalletConfirmationDetail(walletId, locale);
  }

  @Override
  @Transactional(readOnly = true)
  public MyWalletsStatisticsApiDto getUserWalletShortStatistics(int walletId) {
    return walletDao.getWalletShortStatistics(walletId);
  }

     /*
    * Methods defined below are overloaded versions of dashboard info supplier methods.
    * They are supposed to use with REST API which is stateless and cannot use session-based caching.
    * */


  @Transactional(readOnly = true)
  @Override
  public List<MyWalletsDetailedDto> getAllWalletsForUserDetailed(String email, List<Integer> currencyIds, Locale locale) {
    List<Integer> withdrawStatusIdForWhichMoneyIsReserved = WithdrawStatusEnum.getEndStatesSet().stream().map(InvoiceStatus::getCode).collect(Collectors.toList());
    return walletDao.getAllWalletsForUserDetailed(email, currencyIds, withdrawStatusIdForWhichMoneyIsReserved, locale);
  }

  @Transactional(readOnly = true)
  @Override
  public List<MyWalletsStatisticsDto> getAllWalletsForUserReduced(String email, Locale locale) {
    return walletDao.getAllWalletsForUserReduced(email, locale);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void manualBalanceChange(Integer userId, Integer currencyId, BigDecimal amount, String adminEmail) {
    if (amount.equals(BigDecimal.ZERO)) {
      return;
    }
    Wallet wallet = walletDao.findByUserAndCurrency(userId, currencyId);
    if (amount.signum() == -1 && amount.abs().compareTo(wallet.getActiveBalance()) > 0) {
      throw new InvalidAmountException("Negative amount exceeds current balance!");
    }
    if (!isUserAllowedToManuallyChangeWalletBalance(adminEmail, wallet.getUser().getId())) {
      throw new ForbiddenOperationException(String.format("admin: %s, wallet %s", adminEmail, wallet.getId()));
    }
    changeWalletActiveBalance(amount, wallet, OperationType.MANUAL, TransactionSourceType.MANUAL);

  }


  private void changeWalletActiveBalance(BigDecimal amount, Wallet wallet, OperationType operationType,
                                         TransactionSourceType transactionSourceType) {
    changeWalletActiveBalance(amount, wallet, operationType, transactionSourceType, null, null);
  }

  private void changeWalletActiveBalance(BigDecimal amount, Wallet wallet, OperationType operationType,
                                         TransactionSourceType transactionSourceType,
                                         BigDecimal specialCommissionAmount, Integer sourceId) {
    WalletOperationData walletOperationData = new WalletOperationData();
    walletOperationData.setWalletId(wallet.getId());
    walletOperationData.setAmount(amount);
    walletOperationData.setBalanceType(WalletOperationData.BalanceType.ACTIVE);
    walletOperationData.setOperationType(operationType);
    walletOperationData.setSourceId(sourceId);
    Commission commission = commissionService.findCommissionByTypeAndRole(operationType, userService.getUserRoleFromSecurityContext());
    walletOperationData.setCommission(commission);
    BigDecimal commissionAmount = specialCommissionAmount == null ?
        BigDecimalProcessing.doAction(amount, commission.getValue(), ActionType.MULTIPLY_PERCENT) : specialCommissionAmount;
    walletOperationData.setCommissionAmount(commissionAmount);
    walletOperationData.setSourceType(transactionSourceType);
    WalletTransferStatus status = walletBalanceChange(walletOperationData);
    if (status != WalletTransferStatus.SUCCESS) {
      throw new BalanceChangeException(status.name());
    }
    if (commissionAmount.signum() > 0) {

      CompanyWallet companyWallet = companyWalletService.findByCurrency(currencyService.getById(wallet.getCurrencyId()));
      companyWalletService.deposit(companyWallet, BigDecimal.ZERO, commissionAmount);
    }
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public TransferDto transferCostsToUser(Integer fromUserWalletId, Integer toUserId, BigDecimal amount,
                                         Locale locale, int sourceId) {
    if (amount.signum() <= 0) {
      throw new InvalidAmountException(messageSource.getMessage("transfer.negativeAmount", null, locale));
    }
    Wallet fromUserWallet = walletDao.findById(fromUserWalletId);
    Integer currencyId = fromUserWallet.getCurrencyId();
    Commission commission = commissionService
            .findCommissionByTypeAndRole(OperationType.USER_TRANSFER,
                    userService.getUserRoleFromDB(fromUserWallet.getUser().getId()));
    BigDecimal commissionAmount = BigDecimalProcessing.doAction(amount, commission.getValue(), ActionType.MULTIPLY_PERCENT);
    BigDecimal totalAmount = amount.add(commissionAmount);
    log.debug(commission.getValue());
    log.debug(commissionAmount.toString());
    log.debug(totalAmount.toString());
    if (totalAmount.compareTo(fromUserWallet.getActiveBalance()) > 0) {
      throw new InvalidAmountException(messageSource.getMessage("transfer.invalidAmount", null, locale));
    }
    Wallet toUserWallet = walletDao.findByUserAndCurrency(toUserId, currencyId);
    if (toUserWallet == null) {
      throw new WalletNotFoundException(messageSource.getMessage("transfer.walletNotFound", null, locale));
    }
      changeWalletActiveBalance(totalAmount, fromUserWallet, OperationType.OUTPUT,
              TransactionSourceType.USER_TRANSFER, commissionAmount, sourceId);
      changeWalletActiveBalance(amount, toUserWallet, OperationType.INPUT,
              TransactionSourceType.USER_TRANSFER, BigDecimal.ZERO, sourceId);
    CompanyWallet companyWallet = companyWalletService.findByCurrency(currencyService.getById(currencyId));
    companyWalletService.deposit(companyWallet, new BigDecimal(0), commissionAmount);
    String notyAmount = amount.setScale(decimalPlaces, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
    return TransferDto.builder()
            .commission(commission)
            .comissionAmount(commissionAmount)
            .notyAmount(notyAmount)
            .walletUserFrom(fromUserWallet)
            .walletUserTo(toUserWallet)
            .initialAmount(amount)
            .currencyId(currencyId)
            .userFromId(fromUserWallet.getUser().getId())
            .userToId(toUserId)
            .build();
  }



  @Override
  @Transactional(rollbackFor = Exception.class)
  public String transferCostsToUser(Integer userId, Integer fromUserWalletId, String toUserNickname, BigDecimal amount,
                                    Locale locale, int sourceId) {
    Integer toUserId = userService.getIdByNickname(toUserNickname);
    if (toUserId == 0) {
      throw new UserNotFoundException(messageSource.getMessage("transfer.userNotFound", new Object[]{toUserNickname}, locale));
    }
    TransferDto dto = transferCostsToUser(fromUserWalletId, toUserId, amount, locale, sourceId);
    String currencyName = currencyService.getCurrencyName(dto.getCurrencyId());
    String result = messageSource.getMessage("transfer.successful", new Object[]{dto.getNotyAmount(), currencyName, toUserNickname}, locale);
    sendNotificationsAboutTrnasfer(userId, dto.getNotyAmount(), currencyName, dto.getUserToId(), toUserNickname);
    return result;
  }


  private void sendNotificationsAboutTrnasfer(int fromUserId, String notyAmount, String currencyName, int toUserId, String toNickName) {
    log.debug("from {} to {}", fromUserId, toUserId);
    notificationService.notifyUser(fromUserId, NotificationEvent.IN_OUT, "wallets.transferTitle",
            "transfer.successful", new Object[]{notyAmount, currencyName, toNickName});
    notificationService.notifyUser(toUserId, NotificationEvent.IN_OUT, "wallets.transferTitle",
            "transfer.received", new Object[]{notyAmount, currencyName});
  }


  @Transactional(rollbackFor = Exception.class)
  public void performTransferCostsToUser(Wallet fromUserWallet, Wallet toUserWallet,
                                                  BigDecimal initialAmount, BigDecimal totalAmount, BigDecimal commissionAmount,
                                         Integer sourceId, TransactionSourceType sourceType, Locale locale) {
    if (totalAmount.compareTo(fromUserWallet.getActiveBalance()) > 0) {
      throw new InvalidAmountException(messageSource.getMessage("transfer.invalidAmount", null, locale));
    }
    if (Integer.compare(fromUserWallet.getCurrencyId(), toUserWallet.getCurrencyId()) !=0) {
      throw new BalanceChangeException("ncorrect wallets");
    }

  }

  @Override
  @Transactional(readOnly = true)
  public List<UserWalletSummaryDto> getUsersWalletsSummaryForPermittedCurrencyList(Integer requesterUserId) {
    return walletDao.getUsersWalletsSummaryNew(requesterUserId);
  }

  @Override
  @Transactional
  public List<OrderDetailDto> getOrderRelatedDataAndBlock(int orderId) {
    return walletDao.getOrderRelatedDataAndBlock(orderId);
  }

  @Override
  @Transactional
  public WalletsForOrderAcceptionDto getWalletsForOrderByOrderIdAndBlock(Integer orderId, Integer userAcceptorId) {
    return walletDao.getWalletsForOrderByOrderIdAndBlock(orderId, userAcceptorId);
  }

  @Override
  @Transactional
  public WalletsForOrderCancelDto getWalletForOrderByOrderIdAndOperationTypeAndBlock(Integer orderId, OperationType operationType) {
    return walletDao.getWalletForOrderByOrderIdAndOperationTypeAndBlock(orderId, operationType);
  }

  @Override
  @Transactional
  public WalletsForOrderCancelDto getWalletForStopOrderByStopOrderIdAndOperationTypeAndBlock(Integer orderId, OperationType operationType, int currencyPairId) {
    return walletDao.getWalletForStopOrderByStopOrderIdAndOperationTypeAndBlock(orderId, operationType, currencyPairId);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean isUserAllowedToManuallyChangeWalletBalance(String adminEmail, int walletHolderUserId) {
    return walletDao.isUserAllowedToManuallyChangeWalletBalance(userService.getIdByEmail(adminEmail), walletHolderUserId);
  }

}
