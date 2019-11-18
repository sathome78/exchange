package me.exrates.service.impl;

import com.google.common.collect.ImmutableList;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.CurrencyDao;
import me.exrates.dao.IEOClaimRepository;
import me.exrates.dao.IeoDetailsRepository;
import me.exrates.dao.WalletDao;
import me.exrates.dao.exception.notfound.UserNotFoundException;
import me.exrates.dao.exception.notfound.WalletNotFoundException;
import me.exrates.model.Commission;
import me.exrates.model.CompanyWallet;
import me.exrates.model.Currency;
import me.exrates.model.CurrencyPair;
import me.exrates.model.Email;
import me.exrates.model.IEOClaim;
import me.exrates.model.IEODetails;
import me.exrates.model.IEOResult;
import me.exrates.model.Transaction;
import me.exrates.model.User;
import me.exrates.model.Wallet;
import me.exrates.model.dto.ExternalReservedWalletAddressDto;
import me.exrates.model.dto.ExternalWalletBalancesDto;
import me.exrates.model.dto.InternalWalletBalancesDto;
import me.exrates.model.dto.MyWalletConfirmationDetailDto;
import me.exrates.model.dto.OrderDetailDto;
import me.exrates.model.dto.TransferDto;
import me.exrates.model.dto.UserGroupBalanceDto;
import me.exrates.model.dto.UserRoleTotalBalancesReportDto;
import me.exrates.model.dto.UserWalletSummaryDto;
import me.exrates.model.dto.WalletFormattedDto;
import me.exrates.model.dto.WalletsForOrderAcceptionDto;
import me.exrates.model.dto.WalletsForOrderCancelDto;
import me.exrates.model.dto.api.BalanceDto;
import me.exrates.model.dto.api.RateDto;
import me.exrates.model.dto.mobileApiDto.dashboard.MyWalletsStatisticsApiDto;
import me.exrates.model.dto.onlineTableDto.MyWalletsDetailedDto;
import me.exrates.model.dto.onlineTableDto.MyWalletsStatisticsDto;
import me.exrates.model.dto.openAPI.WalletBalanceDto;
import me.exrates.model.enums.ActionType;
import me.exrates.model.enums.CurrencyPairType;
import me.exrates.model.enums.NotificationEvent;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.ReportGroupUserRole;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.WalletTransferStatus;
import me.exrates.model.enums.invoice.FreecoinsStatusEnum;
import me.exrates.model.enums.invoice.IeoStatusEnum;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.enums.invoice.RefillStatusEnum;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.model.vo.CacheData;
import me.exrates.model.vo.WalletOperationData;
import me.exrates.service.CommissionService;
import me.exrates.service.CompanyWalletService;
import me.exrates.service.CurrencyService;
import me.exrates.service.NotificationService;
import me.exrates.service.SendMailService;
import me.exrates.service.TransactionService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.api.ExchangeApi;
import me.exrates.service.api.WalletsApi;
import me.exrates.service.exception.BalanceChangeException;
import me.exrates.service.exception.ForbiddenOperationException;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.exception.process.NotEnoughUserWalletMoneyException;
import me.exrates.service.util.Cache;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ROUND_HALF_UP;
import static java.math.BigDecimal.ZERO;
import static java.util.Comparator.comparing;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import static me.exrates.model.enums.OperationType.INPUT;
import static me.exrates.model.enums.WalletTransferStatus.SUCCESS;
import static me.exrates.model.vo.WalletOperationData.BalanceType.ACTIVE;

@Log4j2
@PropertySource(value = {"classpath:/freecoins.properties"})
@Service
@Transactional
public class WalletServiceImpl implements WalletService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

    private static final int decimalPlaces = 9;

    @Value("${free-coins.balance-holder.email}")
    private String holderEmail;

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
    private ExchangeApi exchangeApi;
    @Autowired
    private WalletsApi walletsApi;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private IeoDetailsRepository ieoDetailsRepository;
    @Autowired
    private IEOClaimRepository ieoClaimRepository;
    @Autowired
    private CurrencyDao currencyDao;

    @Autowired
    private SendMailService sendMailService;


    @Override
    public void balanceRepresentation(final Wallet wallet) {
        wallet
                .setActiveBalance(wallet.getActiveBalance());
//				.setScale(currencyService.resolvePrecision(wallet.getName()), ROUND_CEILING));
    }

    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    @Override
    public List<Wallet> getAllForNotHiddenCurWallets(int userId) {
        final List<Wallet> wallets = walletDao.findAllForNotHiddenCurByUser(userId);
        wallets.forEach(this::balanceRepresentation);
        return wallets;
    }

    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    @Override
    public List<Wallet> getAllWallets(int userId) {
        final List<Wallet> wallets = walletDao.findAllByUser(userId);
        wallets.forEach(this::balanceRepresentation);
        return wallets;
    }

    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    @Override
    public List<WalletFormattedDto> getAllUserWalletsForAdminDetailed(Integer userId) {
        return walletDao.getAllUserWalletsForAdminDetailed(userId,
                WithdrawStatusEnum.getEndStatesSet()
                        .stream()
                        .map(InvoiceStatus::getCode)
                        .collect(Collectors.toList()),
                WithdrawStatusEnum.getEndStatesSet()
                        .stream()
                        .filter(InvoiceStatus::isSuccessEndStatus)
                        .map(InvoiceStatus::getCode)
                        .collect(Collectors.toList()),
                RefillStatusEnum.getEndStatesSet()
                        .stream()
                        .filter(InvoiceStatus::isSuccessEndStatus)
                        .map(InvoiceStatus::getCode)
                        .collect(Collectors.toList()));
    }


    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    @Override
    public List<MyWalletsDetailedDto> getAllWalletsForUserDetailed(CacheData cacheData,
                                                                   String email, Locale locale) {
        List<Integer> withdrawStatusIdForWhichMoneyIsReserved = WithdrawStatusEnum.getEndStatesSet()
                .stream()
                .map(InvoiceStatus::getCode)
                .collect(Collectors.toList());
        List<MyWalletsDetailedDto> result = walletDao.getAllWalletsForUserDetailed(email, withdrawStatusIdForWhichMoneyIsReserved, locale);
        if (Cache.checkCache(cacheData, result)) {
            result = new ArrayList<MyWalletsDetailedDto>() {{
                add(new MyWalletsDetailedDto(false));
            }};
        }
        return result;
    }

    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    @Override
    public List<MyWalletsStatisticsDto> getAllWalletsForUserReduced(CacheData cacheData, String email, CurrencyPairType type) {
        List<CurrencyPair> pairList = currencyService.getAllCurrencyPairs(type);
        Set<Integer> currencies = pairList
                .stream()
                .map(p -> p.getCurrency2().getId())
                .collect(Collectors.toSet());
        currencies.addAll(pairList
                .stream()
                .map(p -> p.getCurrency1().getId())
                .collect(Collectors.toSet()));
        return walletDao.getAllWalletsForUserAndCurrenciesReduced(email, currencies);
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
        boolean result = balance.compareTo(amountForCheck) >= 0;
        if (!result) {
            log.error(String.format("Not enough wallet money: wallet id %s, actual amount %s but needed %s", walletId,
                    BigDecimalProcessing.formatNonePoint(balance, false),
                    BigDecimalProcessing.formatNonePoint(amountForCheck, false)));
        }
        return result;
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
    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    public MyWalletsStatisticsApiDto getUserWalletShortStatistics(int walletId) {
        return walletDao.getWalletShortStatistics(walletId);
    }

    /*
     * Methods defined below are overloaded versions of dashboard info supplier methods.
     * They are supposed to use with REST API which is stateless and cannot use session-based caching.
     * */


    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    @Override
    public List<MyWalletsDetailedDto> getAllWalletsForUserDetailed(String email, List<Integer> currencyIds, Locale locale) {
        List<Integer> withdrawStatusIdForWhichMoneyIsReserved = WithdrawStatusEnum.getEndStatesSet()
                .stream()
                .map(InvoiceStatus::getCode)
                .collect(Collectors.toList());
        return walletDao.getAllWalletsForUserDetailed(email, currencyIds, withdrawStatusIdForWhichMoneyIsReserved, locale);
    }

    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    @Override
    public List<MyWalletsStatisticsDto> getAllWalletsForUserReduced(String email) {
        return walletDao.getAllWalletsForUserReduced(email);
    }

    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    @Override
    public List<WalletBalanceDto> getBalancesForUser() {
        String userEmail = userService.getUserEmailFromSecurityContext();
        return walletDao.getBalancesForUser(userEmail);
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
    @Transactional
    public void withdrawcommissionToUser(Integer userId, Integer currencyId, BigDecimal amount, String adminEmail) {
        if (amount.compareTo(ZERO) <= 0) {
            throw new InvalidAmountException();
        }

        int userWalletId = getWalletId(userId, currencyId);
        WalletOperationData walletOperationData = new WalletOperationData();
        walletOperationData.setOperationType(INPUT);
        walletOperationData.setWalletId(userWalletId);
        walletOperationData.setAmount(amount);
        walletOperationData.setBalanceType(ACTIVE);
        walletOperationData.setCommission(null);
        walletOperationData.setCommissionAmount(ZERO);
        walletOperationData.setSourceType(TransactionSourceType.ACCRUAL);
        walletOperationData.setSourceId(null);
        String description = "Withdraw commission to user";
        walletOperationData.setDescription(description);
        WalletTransferStatus walletTransferStatus = walletBalanceChange(walletOperationData);
        if (walletTransferStatus != SUCCESS) {
            throw new BalanceChangeException(walletTransferStatus.name());
        }

        CompanyWallet companyWallet = companyWalletService.findByCurrency(currencyService.getById(currencyId));
        companyWalletService.substractCommissionBalanceById(companyWallet.getId(), amount);
        companyWallet = companyWalletService.findByCurrency(currencyService.getById(currencyId));
        if (companyWallet.getCommissionBalance().compareTo(ZERO) <= 0) {
            throw new BalanceChangeException("commission is not enought for operation");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TransferDto transferCostsToUser(Integer fromUserWalletId, Integer toUserId, BigDecimal amount,
                                           BigDecimal commissionAmount, Locale locale, int sourceId) {
        if (amount.signum() <= 0) {
            throw new InvalidAmountException(messageSource.getMessage("transfer.negativeAmount", null, locale));
        }
        Wallet fromUserWallet = walletDao.findById(fromUserWalletId);
        int currencyId = fromUserWallet.getCurrencyId();
        BigDecimal inputAmount = BigDecimalProcessing.doAction(amount, commissionAmount, ActionType.SUBTRACT);
        log.debug(commissionAmount.toString());
        log.debug(inputAmount.toString());
        if (inputAmount.compareTo(fromUserWallet.getActiveBalance()) > 0) {
            throw new InvalidAmountException(messageSource.getMessage("transfer.invalidAmount", null, locale));
        }
        Wallet toUserWallet = walletDao.findByUserAndCurrency(toUserId, currencyId);
        if (toUserWallet == null) {
            throw new WalletNotFoundException(messageSource.getMessage("transfer.walletNotFound", null, locale));
        }
        changeWalletActiveBalance(amount, fromUserWallet, OperationType.OUTPUT,
                TransactionSourceType.USER_TRANSFER, commissionAmount, sourceId);
        changeWalletActiveBalance(inputAmount, toUserWallet, OperationType.INPUT,
                TransactionSourceType.USER_TRANSFER, BigDecimal.ZERO, sourceId);
        String notyAmount = inputAmount.setScale(decimalPlaces, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString();
        return TransferDto.builder()
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
    public String transferCostsToUser(Integer userId, Integer fromUserWalletId, Integer toUserId, BigDecimal amount,
                                      BigDecimal comission, Locale locale, int sourceId) {
        User toUser = userService.getUserById(toUserId);
        String toUserNickname = toUser.getEmail();
        if (toUserId == 0) {
            throw new UserNotFoundException(messageSource.getMessage("transfer.userNotFound", new Object[]{toUserNickname}, locale));
        }
        TransferDto dto = transferCostsToUser(fromUserWalletId, toUserId, amount, comission, locale, sourceId);
        String currencyName = currencyService.getCurrencyName(dto.getCurrencyId());
        String result = messageSource.getMessage("transfer.successful", new Object[]{dto.getNotyAmount(), currencyName, toUserNickname}, locale);
        sendNotificationsAboutTransfer(userId, dto.getNotyAmount(), currencyName, dto.getUserToId(), toUserNickname);
        return result;
    }


    private void sendNotificationsAboutTransfer(int fromUserId, String notyAmount, String currencyName, int toUserId, String toNickName) {
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
        if (Integer.compare(fromUserWallet.getCurrencyId(), toUserWallet.getCurrencyId()) != 0) {
            throw new BalanceChangeException("ncorrect wallets");
        }

    }

    @Override
    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    public List<UserWalletSummaryDto> getUsersWalletsSummaryForPermittedCurrencyList(Integer requesterUserId, List<Integer> roleIds) {
        List<UserWalletSummaryDto> roleFilteredPaginData = walletDao.getUsersWalletsSummaryNew(requesterUserId, roleIds);

        List<UserWalletSummaryDto> userWalletSummaryDtos = new ArrayList<>();
        for (UserWalletSummaryDto item : roleFilteredPaginData) {
            if (!userWalletSummaryDtos.contains(item)) {
                userWalletSummaryDtos.add(new UserWalletSummaryDto(item));
            } else {
                UserWalletSummaryDto storedItem = userWalletSummaryDtos
                        .stream()
                        .filter(e -> e.equals(item))
                        .findAny()
                        .get();
                storedItem.increment(item);
            }
        }
        userWalletSummaryDtos.forEach(UserWalletSummaryDto::calculate);

        return userWalletSummaryDtos;
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
        CurrencyPair currencyPair = currencyService.findCurrencyPairById(currencyPairId);
        return walletDao.getWalletForStopOrderByStopOrderIdAndOperationTypeAndBlock(orderId, operationType, currencyPair);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserAllowedToManuallyChangeWalletBalance(String adminEmail, int walletHolderUserId) {
        return walletDao.isUserAllowedToManuallyChangeWalletBalance(userService.getIdByEmail(adminEmail), walletHolderUserId);
    }


    @Override
    public List<UserRoleTotalBalancesReportDto<ReportGroupUserRole>> getWalletBalancesSummaryByGroups() {
        Supplier<Map<String, BigDecimal>> balancesMapSupplier = () -> Arrays.stream(ReportGroupUserRole.values())
                .collect(Collectors.toMap(Enum::name, val -> BigDecimal.ZERO));
        return walletDao.getWalletBalancesSummaryByGroups()
                .stream()
                .collect(Collectors.groupingBy(UserGroupBalanceDto::getCurAndId)).entrySet()
                .stream()
                .map(entry -> new UserRoleTotalBalancesReportDto<>(entry.getKey().getCurrency(), entry.getKey().getId(), entry.getValue()
                        .stream()
                        .collect(Collectors.toMap(dto -> dto.getReportGroupUserRole().name(),
                                UserGroupBalanceDto::getTotalBalance, (oldValue, newValue) -> newValue,
                                balancesMapSupplier)), ReportGroupUserRole.class))
                .sorted(comparing(UserRoleTotalBalancesReportDto::getCurId))
                .collect(Collectors.toList());

    }


    @Override
    public int getWalletIdAndBlock(Integer userId, Integer currencyId) {
        return walletDao.getWalletIdAndBlock(userId, currencyId);
    }

    @Transactional
    @Override
    public void updateExternalMainWalletBalances() {
        StopWatch stopWatch = StopWatch.createStarted();
        log.info("Process of updating external main wallets start...");

        List<Currency> currencies = currencyService.getAllCurrencies();

        final Map<String, RateDto> rates = exchangeApi.getRates();
        final Map<String, BalanceDto> balances = walletsApi.getBalances();
        final Map<String, ExternalWalletBalancesDto> mainBalancesMap = walletDao.getExternalMainWalletBalances()
                .stream()
                .collect(toMap(
                        ExternalWalletBalancesDto::getCurrencyName,
                        Function.identity()
                ));

        if (rates.isEmpty() || balances.isEmpty() || mainBalancesMap.isEmpty()) {
            log.info("Exchange or wallet api did not return any data");
            return;
        }

        for (Currency currency : currencies) {
            final String currencyName = currency.getName();

            RateDto rateDto = rates.getOrDefault(currencyName, RateDto.zeroRate(currencyName));
            BalanceDto balanceDto = balances.getOrDefault(currencyName, BalanceDto.zeroBalance(currencyName));

            BigDecimal usdRate = rateDto.getUsdRate();
            BigDecimal btcRate = rateDto.getBtcRate();

            BigDecimal mainBalance = balanceDto.getBalance();
            LocalDateTime lastBalanceUpdate = balanceDto.getLastUpdatedAt();

            ExternalWalletBalancesDto exWallet = mainBalancesMap.get(currencyName);

            if (isNull(exWallet)) {
                continue;
            }
            ExternalWalletBalancesDto.Builder builder = exWallet.toBuilder()
                    .usdRate(usdRate)
                    .btcRate(btcRate)
                    .mainBalance(mainBalance);

            if (nonNull(lastBalanceUpdate)) {
                builder.lastUpdatedDate(lastBalanceUpdate);
            }
            exWallet = builder.build();
            walletDao.updateExternalMainWalletBalances(exWallet);
        }
        log.info("Process of updating external main wallets end... Time: {}", stopWatch.getTime(TimeUnit.MILLISECONDS));
    }

    @Transactional
    @Override
    public void updateExternalReservedWalletBalances() {
        StopWatch stopWatch = StopWatch.createStarted();
        log.info("Process of updating external reserved wallets start...");

        final Map<String, BigDecimal> reservedBalances = walletsApi.getReservedBalances();

        if (reservedBalances.isEmpty()) {
            log.info("Wallet api did not return any data");
            return;
        }

        for (Map.Entry<String, BigDecimal> entry : reservedBalances.entrySet()) {
            final String compositeKey = entry.getKey();
            final BigDecimal balance = entry.getValue();

            String[] data = compositeKey.split("\\|\\|");
            final String currencySymbol = data[0];
            final String walletAddress = data[1];
            final LocalDateTime lastReservedBalanceUpdate = StringUtils.isNotEmpty(data[3])
                    ? LocalDateTime.parse(data[3], FORMATTER)
                    : null;

            Currency currency = currencyService.findByName(currencySymbol);

            walletDao.updateExternalReservedWalletBalances(currency.getId(), walletAddress, balance, lastReservedBalanceUpdate);
        }
        log.info("Process of updating external reserved wallets end... Time: {}", stopWatch.getTime(TimeUnit.MILLISECONDS));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ExternalWalletBalancesDto> getExternalWalletBalances() {
        return walletDao.getExternalMainWalletBalances();
    }

    @Transactional
    @Override
    public void updateInternalWalletBalances() {
        StopWatch stopWatch = StopWatch.createStarted();
        log.info("Process of updating internal wallets start...");

        List<Currency> currencies = currencyService.getAllCurrencies();

        final Map<String, RateDto> rates = exchangeApi.getRates();
        final Map<String, List<InternalWalletBalancesDto>> balances = this.getWalletBalances()
                .stream()
                .collect(groupingBy(InternalWalletBalancesDto::getCurrencyName));

        if (rates.isEmpty() || balances.isEmpty()) {
            log.info("Exchange or wallet api did not return any data");
            return;
        }

        for (Currency currency : currencies) {
            final String currencyName = currency.getName();

            RateDto rateDto = rates.getOrDefault(currencyName, RateDto.zeroRate(currencyName));
            List<InternalWalletBalancesDto> balancesByRoles = balances.get(currencyName);

            if (isNull(balancesByRoles)) {
                continue;
            }
            final BigDecimal usdRate = rateDto.getUsdRate();
            final BigDecimal btcRate = rateDto.getBtcRate();

            for (InternalWalletBalancesDto balance : balancesByRoles) {
                balance = balance.toBuilder()
                        .usdRate(usdRate)
                        .btcRate(btcRate)
                        .build();
                walletDao.updateInternalWalletBalances(balance);
            }
        }
        log.info("Process of updating internal wallets end... Time: {}", stopWatch.getTime(TimeUnit.MILLISECONDS));
    }

    @Transactional(readOnly = true)
    @Override
    public List<InternalWalletBalancesDto> getInternalWalletBalances() {
        return walletDao.getInternalWalletBalances();
    }

    @Transactional(readOnly = true)
    @Override
    public List<InternalWalletBalancesDto> getWalletBalances() {
        return walletDao.getWalletBalances();
    }

    @Override
    public void createWalletAddress(int currencyId) {
        walletDao.createReservedWalletAddress(currencyId);
    }

    @Override
    public void deleteWalletAddress(int id, int currencyId, String walletAddress) {
        walletDao.deleteReservedWalletAddress(id, currencyId);

        final String currencySymbol = currencyService.getCurrencyName(currencyId);

        boolean deleted = walletsApi.deleteReservedWallet(currencySymbol, walletAddress);

        log.info("External reserved address [{}:{}] {}",
                currencySymbol,
                walletAddress,
                deleted ? "have been deleted" : "have not been deleted");
    }

    @Override
    public void updateWalletAddress(ExternalReservedWalletAddressDto externalReservedWalletAddressDto, boolean isSavedAsAddress) {
        walletDao.updateReservedWalletAddress(externalReservedWalletAddressDto);

        if (isSavedAsAddress) {
            final int currencyId = externalReservedWalletAddressDto.getCurrencyId();
            final String walletAddress = externalReservedWalletAddressDto.getWalletAddress();

            final String currencySymbol = currencyService.getCurrencyName(currencyId);

            boolean saved = walletsApi.addReservedWallet(currencySymbol, walletAddress);

            log.info("External reserved address [{}:{}] {}",
                    currencySymbol,
                    walletAddress,
                    saved ? "have been saved" : "have not been saved");
        }
    }

    @Override
    public boolean updateSignOfCertaintyForCurrency(int currencyId, boolean signOfCertainty) {
        return walletDao.updateSignOfCertaintyForCurrency(currencyId, signOfCertainty);
    }

    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    @Override
    public List<ExternalReservedWalletAddressDto> getReservedWalletsByCurrencyId(String currencyId) {
        return walletDao.getReservedWalletsByCurrencyId(currencyId);
    }

    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    @Override
    public BigDecimal retrieveSummaryUSD() {
        return walletDao.retrieveSummaryUSD();
    }

    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    @Override
    public BigDecimal retrieveSummaryBTC() {
        return walletDao.retrieveSummaryBTC();
    }

    @Override
    public BigDecimal getExternalReservedWalletBalance(Integer currencyId, String walletAddress) {
        Currency currency = currencyService.findById(currencyId);
        if (isNull(currency)) {
            log.info("Currency with id: {} not found", currencyId);
            return null;
        }
        return walletsApi.getBalanceByCurrencyAndWallet(currency.getName(), walletAddress);
    }

    @Override
    public Wallet findByUserAndCurrency(int userId, int currencyId) {
        return walletDao.findByUserAndCurrency(userId, currencyId);
    }

    @Override
    public Wallet findByUserAndCurrency(int userId, String currencyName) {
        return walletDao.findByUserAndCurrency(userId, currencyName);
    }

    @Override
    public Map<String, Wallet> findAllByUserAndCurrencyNames(int userId, Collection<String> currencyNames) {
        List<Currency> currencies = currencyDao.findAllByNames(currencyNames);
        List<Wallet> wallets = walletDao.findAllByUser(userId);
        Map<String, Wallet> userWallets = new HashMap<>(currencies.size());
        currencies.forEach(currency -> {
            Wallet wallet = wallets.stream().filter(w -> w.getCurrencyId() == currency.getId()).findFirst().orElse(null);
            userWallets.put(currency.getName(), wallet);
        });
        return userWallets;
    }

    @Override
    public boolean reserveUserBtcForIeo(int userId, BigDecimal amountInBtc) {
        int currencyId = currencyService.findByName("BTC").getId();
        return walletDao.reserveUserBtcForIeo(userId, amountInBtc, currencyId);
    }

    @Override
    public boolean rollbackUserBtcForIeo(int userId, BigDecimal amountInBtc) {
        int currencyId = currencyService.findByName("BTC").getId();
        return walletDao.rollbackUserBtcForIeo(userId, amountInBtc, currencyId);
    }

    @Override
    @Transactional()
    public boolean performIeoTransfer(IEOClaim ieoClaim) {
        Wallet makerBtcWallet = walletDao.findByUserAndCurrency(ieoClaim.getMakerId(), "BTC");
        if (makerBtcWallet == null) {
            int currencyId = currencyService.findByName("BTC").getId();
            makerBtcWallet = walletDao.createWallet(ieoClaim.getMakerId(), currencyId);
        }
        Wallet userBtcWallet = walletDao.findByUserAndCurrency(ieoClaim.getUserId(), "BTC");
        Wallet userIeoWallet = walletDao.findByUserAndCurrency(ieoClaim.getUserId(), ieoClaim.getCurrencyName());
        if (userIeoWallet == null) {
            int currencyId = currencyService.findByName(ieoClaim.getCurrencyName()).getId();
            userIeoWallet = walletDao.createWallet(ieoClaim.getUserId(), currencyId);
        }

        BigDecimal makerBtcInitialAmount = Objects.isNull(makerBtcWallet.getIeoReserved())
                ? ZERO
                : makerBtcWallet.getIeoReserved();
        makerBtcWallet.setIeoReserved(makerBtcInitialAmount.add(ieoClaim.getPriceInBtc()));

        BigDecimal updateActiveUserBalanceBtc = userBtcWallet.getActiveBalance().subtract(ieoClaim.getPriceInBtc());
        userBtcWallet.setActiveBalance(updateActiveUserBalanceBtc);

        BigDecimal userIeoInitialAmount = userIeoWallet.getActiveBalance();
        userIeoWallet.setActiveBalance(userIeoInitialAmount.add(ieoClaim.getAmount()));

        boolean updateResult = walletDao.update(makerBtcWallet)
                && walletDao.update(userBtcWallet)
                && walletDao.update(userIeoWallet);
        log.info("PerformIeoTransfer(), claimID {}, result update wallet {}", ieoClaim.getId(), updateResult);
        if (updateResult) {
            writeTransactionsAsyncForPerformIeo(ieoClaim, makerBtcInitialAmount, makerBtcWallet,
                    userIeoInitialAmount, userIeoWallet, userBtcWallet, IeoStatusEnum.PROCESSED_BY_CLAIM);
        }
        return updateResult;
    }

    private void writeTransactionsAsyncForPerformIeo(IEOClaim ieoClaim, BigDecimal makerBtcInitialAmount, Wallet makerBtcWallet,
                                                     BigDecimal userIeoInitialAmount, Wallet userIeoWallet, Wallet userMainWallet, IeoStatusEnum statusEnum) {
        Transaction makerBtcTransaction = prepareTransaction(makerBtcInitialAmount, ieoClaim.getPriceInBtc(), makerBtcWallet, ieoClaim, statusEnum, OperationType.INPUT);
        Transaction userBtcTransaction = prepareUserBtcTransaction(userMainWallet, ieoClaim, statusEnum, OperationType.OUTPUT);
        Transaction userIeoTransaction = prepareTransaction(userIeoInitialAmount, ieoClaim.getAmount(), userIeoWallet, ieoClaim, statusEnum, OperationType.INPUT);
        transactionService.save(ImmutableList.of(makerBtcTransaction, userBtcTransaction, userIeoTransaction));
    }

    private void writeTransactionsAsyncForReveryIeo(IEOClaim ieoClaim, BigDecimal makerBtcInitialAmount, Wallet makerBtcWallet,
                                                    BigDecimal userIeoInitialAmount, Wallet userIeoWallet, Wallet userMainWallet, IeoStatusEnum statusEnum) {
        Transaction makerBtcTransaction = prepareTransaction(makerBtcInitialAmount, ieoClaim.getPriceInBtc(), makerBtcWallet, ieoClaim, statusEnum, OperationType.OUTPUT);
        Transaction userBtcTransaction = prepareUserBtcTransaction(userMainWallet, ieoClaim, statusEnum, INPUT);
        Transaction userIeoTransaction = prepareTransaction(userIeoInitialAmount, ieoClaim.getAmount(), userIeoWallet, ieoClaim, statusEnum, OperationType.OUTPUT);
        transactionService.save(ImmutableList.of(makerBtcTransaction, userBtcTransaction, userIeoTransaction));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean performFreecoinsGiveawayProcess(String currencyName, BigDecimal amount, String creatorEmail) {
        Currency currency = currencyService.findByName(currencyName);

        final int creatorId = userService.getIdByEmail(creatorEmail);
        Wallet creatorWallet = walletDao.findByUserAndCurrency(creatorId, currency.getId());
        if (isNull(creatorWallet)) {
            throw new WalletNotFoundException(String.format("Wallet did not find by user: %s and currency: %s", creatorEmail, currencyName));
        }
        BigDecimal activeBalance = creatorWallet.getActiveBalance();
        if (isNull(activeBalance) || activeBalance.compareTo(amount) < 0) {
            throw new NotEnoughUserWalletMoneyException(String.format("Required amount of coins: %s larger then active balance %s", amount.toPlainString(), activeBalance.toPlainString()));
        }

        final int holderId = userService.getIdByEmail(holderEmail);
        Wallet holderWallet = walletDao.findByUserAndCurrency(holderId, currency.getId());
        if (isNull(holderWallet)) {
            holderWallet = walletDao.createWallet(holderId, currency.getId());
        }

        creatorWallet.setActiveBalance(activeBalance.subtract(amount));
        holderWallet.setActiveBalance(holderWallet.getActiveBalance().add(amount));

        boolean updateResult = walletDao.update(creatorWallet)
                && walletDao.update(holderWallet);

        log.debug("The result of updating wallets is: {}", updateResult);

        if (updateResult) {
            prepareTransactionsAndSave(currency, amount, creatorWallet, holderWallet);
        }
        return updateResult;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean performFreecoinsGiveawayRevokeProcess(String currencyName, BigDecimal revokeAmount, String creatorEmail) {
        Currency currency = currencyService.findByName(currencyName);

        final int holderId = userService.getIdByEmail(holderEmail);
        Wallet holderWallet = walletDao.findByUserAndCurrency(holderId, currency.getId());
        if (isNull(holderWallet)) {
            throw new WalletNotFoundException(String.format("Wallet did not find by user: %s and currency: %s", holderEmail, currencyName));
        }
        BigDecimal activeBalance = holderWallet.getActiveBalance();
        if (isNull(activeBalance) || activeBalance.compareTo(revokeAmount) < 0) {
            throw new NotEnoughUserWalletMoneyException(String.format("Required amount of coins: %s larger then active balance %s", revokeAmount.toPlainString(), activeBalance.toPlainString()));
        }

        final int creatorId = userService.getIdByEmail(creatorEmail);
        Wallet creatorWallet = walletDao.findByUserAndCurrency(creatorId, currency.getId());
        if (isNull(creatorWallet)) {
            creatorWallet = walletDao.createWallet(creatorId, currency.getId());
        }

        holderWallet.setActiveBalance(activeBalance.subtract(revokeAmount));
        creatorWallet.setActiveBalance(creatorWallet.getActiveBalance().add(revokeAmount));

        boolean updateResult = walletDao.update(holderWallet)
                && walletDao.update(creatorWallet);

        log.debug("The result of updating wallets is: {}", updateResult);

        if (updateResult) {
            prepareTransactionsAndSave(currency, revokeAmount, holderWallet, creatorWallet);
        }
        return updateResult;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean performFreecoinsReceiveProcess(String currencyName, BigDecimal partialAmount, String receiverEmail) {
        Currency currency = currencyService.findByName(currencyName);

        final int holderId = userService.getIdByEmail(holderEmail);
        Wallet holderWallet = walletDao.findByUserAndCurrency(holderId, currency.getId());
        if (isNull(holderWallet)) {
            throw new WalletNotFoundException(String.format("Wallet did not find by user: %s and currency: %s", holderEmail, currencyName));
        }
        BigDecimal activeBalance = holderWallet.getActiveBalance();
        if (isNull(activeBalance) || activeBalance.compareTo(partialAmount) < 0) {
            throw new NotEnoughUserWalletMoneyException(String.format("Required amount of coins: %s larger then active balance %s", partialAmount.toPlainString(), activeBalance.toPlainString()));
        }

        final int receiverId = userService.getIdByEmail(receiverEmail);
        Wallet receiverWallet = walletDao.findByUserAndCurrency(receiverId, currency.getId());
        if (isNull(receiverWallet)) {
            receiverWallet = walletDao.createWallet(receiverId, currency.getId());
        }

        holderWallet.setActiveBalance(activeBalance.subtract(partialAmount));
        receiverWallet.setActiveBalance(receiverWallet.getActiveBalance().add(partialAmount));

        boolean updateResult = walletDao.update(holderWallet)
                && walletDao.update(receiverWallet);

        log.debug("The result of updating wallets is: {}", updateResult);

        if (updateResult) {
            prepareTransactionsAndSave(currency, partialAmount, holderWallet, receiverWallet);
        }
        return updateResult;
    }

    private void prepareTransactionsAndSave(Currency currency, BigDecimal amount, Wallet firstWallet, Wallet secondWallet) {
        Transaction firstTransaction = prepareTransaction(currency, amount, firstWallet, OperationType.OUTPUT);
        Transaction secondTransaction = prepareTransaction(currency, amount, secondWallet, OperationType.INPUT);

        ImmutableList.of(firstTransaction, secondTransaction).forEach(transaction -> transactionService.save(transaction));
    }

    private Transaction prepareTransaction(Currency currency, BigDecimal amount, Wallet wallet, OperationType operationType) {
        final String description = String.format("FREE_COIN_TRANSFER: %s %s %s", operationType.name(), amount.toPlainString(), currency.getName());

        return Transaction
                .builder()
                .userWallet(wallet)
                .amount(amount)
                .commissionAmount(BigDecimal.ZERO)
                .operationType(operationType)
                .currency(currency)
                .datetime(LocalDateTime.now())
                .activeBalanceBefore(operationType == OperationType.OUTPUT
                        ? wallet.getActiveBalance().add(amount)
                        : wallet.getActiveBalance().subtract(amount))
                .reservedBalanceBefore(wallet.getReservedBalance())
                .sourceType(TransactionSourceType.FREE_COINS_TRANSFER)
                .invoiceStatus(FreecoinsStatusEnum.CREATED)
                .description(description)
                .provided(true)
                .build();
    }

    @Override
    public BigDecimal getAvailableAmountInBtcLocked(int userId, int currencyId) {
        return walletDao.getAvailableAmountInBtcLocked(userId, currencyId);
    }

    @Override
    public Map<String, String> findUserCurrencyBalances(User user) {
        List<String> ieoCurrencyNames = ieoDetailsRepository.findAll()
                .stream()
                .map(IEODetails::getCurrencyName)
                .collect(Collectors.toList());
        return walletDao.findUserCurrencyBalances(user, ieoCurrencyNames);
    }

    @Override
    public BigDecimal findUserCurrencyBalance(IEOClaim ieoClaim) {
        return walletDao.findUserCurrencyBalance(ieoClaim);
    }

    @Override
    @Transactional()
    public boolean performIeoRollbackTransfer(IEOClaim ieoClaim) {

        Wallet userBtcWallet = walletDao.findByUserAndCurrency(ieoClaim.getUserId(), "BTC");
        Wallet userIeoWallet = walletDao.findByUserAndCurrency(ieoClaim.getUserId(), ieoClaim.getCurrencyName());

        Wallet makerBtcWallet = walletDao.findByUserAndCurrency(ieoClaim.getMakerId(), "BTC");

        BigDecimal userBtcInitActiveBalance = userBtcWallet.getActiveBalance();
        userBtcWallet.setActiveBalance(userBtcInitActiveBalance.add(ieoClaim.getPriceInBtc()));

        BigDecimal userIeoWalletActiveBalance = userIeoWallet.getActiveBalance();
        userIeoWallet.setActiveBalance(userIeoWalletActiveBalance.subtract(ieoClaim.getAmount()));

        BigDecimal makerBtcActiveBalance = makerBtcWallet.getIeoReserved();
        makerBtcWallet.setIeoReserved(makerBtcActiveBalance.subtract(ieoClaim.getPriceInBtc()));

        boolean updateResult = walletDao.update(makerBtcWallet)
                && walletDao.update(userBtcWallet)
                && walletDao.update(userIeoWallet);
        if (updateResult) {
            final Wallet makerWallet = makerBtcWallet;
            final Wallet userWallet = userIeoWallet;
            final Wallet userMainWallet = userBtcWallet;

            ieoClaimRepository.updateStatusIEOClaim(ieoClaim.getId(), IEOResult.IEOResultStatus.REVOKED);

            CompletableFuture.runAsync(() -> writeTransactionsAsyncForReveryIeo(ieoClaim, makerBtcActiveBalance, makerWallet,
                    userIeoWalletActiveBalance, userWallet, userMainWallet, IeoStatusEnum.REVOKED_BY_IEO_FAILURE));

            User user = userService.getUserById(ieoClaim.getUserId());
            Email email = new Email();
            email.setTo(user.getEmail());
            email.setSubject("Revert IEO");
            email.setMessage(String.format("<p style=\"MAX-WIDTH: 347px; FONT-FAMILY: Roboto; COLOR: #000000; MARGIN: auto auto 2.15em;font-weight: normal; font-size: 16px; line-height: 19px; text-align: center;\">" +
                    "<span style=\"font-weight: 600;\">IEO</span> for %s has been canceled. All funds returned</p>", ieoClaim.getCurrencyName()));
            Properties properties = new Properties();
            properties.setProperty("public_id", user.getPublicId());
            email.setProperties(properties);

            sendMailService.sendMail(email);
        }
        return updateResult;
    }

    @Override
    public boolean moveBalanceFromIeoReservedToActive(int userId, String currencyName) {
        Wallet wallet = walletDao.findByUserAndCurrency(userId, currencyName);
        BigDecimal ieoReservedBalance = wallet.getIeoReserved();
        BigDecimal activeBalance = wallet.getActiveBalance();

        wallet.setActiveBalance(activeBalance.add(ieoReservedBalance));
        wallet.setIeoReserved(BigDecimal.ZERO);

        boolean result = walletDao.update(wallet);

        if (result) {
            Currency currency = currencyService.findById(wallet.getCurrencyId());
            Transaction transaction = Transaction
                    .builder()
                    .userWallet(wallet)
                    .amount(ieoReservedBalance)
                    .commissionAmount(ZERO)
                    .operationType(OperationType.INPUT)
                    .invoiceStatus(IeoStatusEnum.SUCCESS_IEO)
                    .currency(currency)
                    .datetime(LocalDateTime.now())
                    .activeBalanceBefore(activeBalance)
                    .reservedBalanceBefore(ieoReservedBalance)
                    .sourceType(TransactionSourceType.IEO)
                    .description("Success IEO processing, finish process.")
                    .build();
            transactionService.save(transaction);
        }
        return result;
    }

    @Override
    public BigDecimal getActiveBalanceAndBlockByWalletId(Integer walletId) {
        return walletDao.getActiveBalanceAndBlockByWalletId(walletId);
    }

    @Transactional(readOnly = true)
    @Override
    public BigDecimal getActiveBalanceByUserAndCurrency(String email, Integer currencyId) {
        return walletDao.getActiveBalanceByUserAndCurrency(email, currencyId);
    }

    @Transactional(propagation = Propagation.NESTED)
    @Override
    public boolean performReferralBalanceUpdate(int walletId, BigDecimal amount, ActionType type) {
        Wallet wallet = walletDao.findByIdAndLock(walletId);
        BigDecimal oldReferralBalance = wallet.getReferralBalance();
        BigDecimal newReferralBalance =
                BigDecimalProcessing.doAction(oldReferralBalance, amount, type, RoundingMode.HALF_DOWN);
        wallet.setReferralBalance(newReferralBalance);
        return walletDao.update(wallet);
    }

    @Override
    public boolean transferReferralBalanceToActive(int walletId, BigDecimal amount) {
        Wallet wallet = walletDao.findByIdAndLock(walletId);
        BigDecimal activeBalance = wallet.getActiveBalance();
        BigDecimal newActiveBalance = BigDecimalProcessing.doAction(activeBalance, amount, ActionType.ADD, RoundingMode.HALF_DOWN);
        wallet.setActiveBalance(newActiveBalance);
        return walletDao.update(wallet);
    }

    @Override
    public boolean updateReferralBalance(int walletId, BigDecimal amount) {
        Wallet wallet = walletDao.findByIdAndLock(walletId);
        wallet.setReferralBalance(amount);
        return walletDao.update(wallet);
    }

    private Transaction prepareTransaction(BigDecimal initialAmount, BigDecimal amount, Wallet wallet,
                                           IEOClaim ieoClaim, InvoiceStatus status, OperationType operationType) {
        Currency currency = currencyService.findById(wallet.getCurrencyId());
        String description = "Purchase of " + ieoClaim.getAmount().toPlainString() + " " + ieoClaim.getCurrencyName() + " within IEO: "
                + "1 " + ieoClaim.getCurrencyName() + " x " + ieoClaim.getRate() + " BTC";
        return Transaction
                .builder()
                .userWallet(wallet)
                .amount(amount)
                .commissionAmount(ZERO)
                .operationType(operationType)
                .invoiceStatus(status)
                .currency(currency)
                .datetime(LocalDateTime.now())
                .activeBalanceBefore(initialAmount)
                .reservedBalanceBefore(wallet.getReservedBalance())
                .sourceType(TransactionSourceType.IEO)
                .description(description)
                .build();
    }

    private Transaction prepareUserBtcTransaction(Wallet wallet, IEOClaim ieoClaim, InvoiceStatus status,
                                                  OperationType operationType) {
        Currency currency = currencyService.findById(wallet.getCurrencyId());
        String description = "Purchase of " + ieoClaim.getAmount().toPlainString() + " " + ieoClaim.getCurrencyName() + " within IEO: "
                + "1 " + ieoClaim.getCurrencyName() + " x " + ieoClaim.getRate() + " BTC";
        return Transaction
                .builder()
                .userWallet(wallet)
                .amount(ieoClaim.getPriceInBtc())
                .commissionAmount(ZERO)
                .operationType(operationType)
                .currency(currency)
                .datetime(LocalDateTime.now())
                .activeBalanceBefore(wallet.getActiveBalance().add(ieoClaim.getPriceInBtc()))
                .reservedBalanceBefore(wallet.getReservedBalance())
                .sourceType(TransactionSourceType.IEO)
                .invoiceStatus(status)
                .description(description)
                .build();
    }
}
