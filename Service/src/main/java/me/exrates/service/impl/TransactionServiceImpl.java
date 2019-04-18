package me.exrates.service.impl;

import me.exrates.dao.TransactionDao;
import me.exrates.model.CompanyWallet;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.PagingData;
import me.exrates.model.Transaction;
import me.exrates.model.User;
import me.exrates.model.Wallet;
import me.exrates.model.dto.InOutReportDto;
import me.exrates.model.dto.OperationViewDto;
import me.exrates.model.dto.UserSummaryDto;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.AdminTransactionsFilterData;
import me.exrates.model.dto.onlineTableDto.AccountStatementDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.TransactionType;
import me.exrates.model.enums.UserRole;
import me.exrates.model.vo.CacheData;
import me.exrates.service.CompanyWalletService;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.TransactionService;
import me.exrates.service.WalletService;
import me.exrates.service.exception.TransactionPersistException;
import me.exrates.service.exception.TransactionProvidingException;
import me.exrates.service.util.Cache;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ROUND_HALF_UP;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static final Logger LOG = LogManager.getLogger(TransactionServiceImpl.class);

    private final CompanyWalletService companyWalletService;
    private final CurrencyService currencyService;
    private final MerchantService merchantService;
    private final TransactionDao transactionDao;
    private final WalletService walletService;

    @Autowired
    public TransactionServiceImpl(TransactionDao transactionDao,
                                  WalletService walletService,
                                  CompanyWalletService companyWalletService,
                                  MerchantService merchantService,
                                  CurrencyService currencyService) {
        this.transactionDao = transactionDao;
        this.walletService = walletService;
        this.companyWalletService = companyWalletService;
        this.merchantService = merchantService;
        this.currencyService = currencyService;
    }


    @PostConstruct
    public void init() {
        Set<TransactionSourceType> storedTypes = transactionDao.findAllTransactionSourceTypes();
        Set<TransactionSourceType> enumValues = new HashSet<>(Arrays.asList(TransactionSourceType.values()));
        enumValues.removeAll(storedTypes);
        if (!enumValues.isEmpty()) {
            transactionDao.updateStoredTransactionSourceType(enumValues);
        }
    }

    @Override
    public Transaction save(Transaction transaction) {
        return transactionDao.create(transaction);
    }

    @Override
    public boolean save(Collection<Transaction> transactions) {
        transactions.forEach(transactionDao::create);
        return true;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public Transaction createTransactionRequest(CreditsOperation creditsOperation) {
        final Currency currency = creditsOperation.getCurrency();
        final User user = creditsOperation.getUser();
        final String currencyName = currency.getName();

        CompanyWallet companyWallet = companyWalletService.findByCurrency(currency);
        companyWallet = companyWallet == null ? companyWalletService.create(currency) : companyWallet;

        Wallet userWallet = walletService.findByUserAndCurrency(user, currency);
        userWallet = userWallet == null ? walletService.create(user, currency) : userWallet;

        Transaction transaction = new Transaction();
        transaction.setAmount(creditsOperation.getAmount());
        transaction.setCommissionAmount(creditsOperation.getCommissionAmount());
        transaction.setCommission(creditsOperation.getCommission());
        transaction.setCompanyWallet(companyWallet);
        transaction.setUserWallet(userWallet);
        transaction.setCurrency(currency);
        transaction.setDatetime(LocalDateTime.now());
        transaction.setMerchant(creditsOperation.getMerchant());
        transaction.setOperationType(creditsOperation.getOperationType());
        transaction.setProvided(false);
        transaction.setConfirmation((currencyName).equals("BTC") ? -1 : -1);
        transaction.setSourceType(creditsOperation.getTransactionSourceType());
        transaction = transactionDao.create(transaction);
        if (transaction == null) {
            throw new TransactionPersistException("Failed to provide transaction ");
        }
        LOG.info("Transaction created:" + transaction);
        return transaction;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Transaction findById(int id) {
        return transactionDao.findById(id);
    }

    private void updateAmount(Transaction transaction, BigDecimal amount) {
        int scale = currencyService.resolvePrecision(transaction.getCurrency().getName());
        BigDecimal commissionRate = transaction.getCommission().getValue();
        BigDecimal commission = calculateCommissionFromAmpunt(amount, commissionRate, scale);
        final BigDecimal newAmount = amount.subtract(commission).setScale(scale, ROUND_HALF_UP);
        transaction.setCommissionAmount(commission);
        transaction.setAmount(newAmount);
        transactionDao.updateTransactionAmount(transaction.getId(), newAmount, commission);
    }

    private BigDecimal calculateCommissionFromAmpunt(BigDecimal amount, BigDecimal commissionRate, int scale) {
        BigDecimal mass = BigDecimal.valueOf(100L).add(commissionRate);
        return amount.multiply(commissionRate)
                .divide(mass, scale, ROUND_HALF_UP).setScale(scale, ROUND_HALF_UP);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void provideTransaction(Transaction transaction) {
        switch (transaction.getOperationType()) {
            case INPUT:
                walletService.depositActiveBalance(transaction.getUserWallet(), transaction.getAmount());
                companyWalletService.deposit(transaction.getCompanyWallet(), transaction.getAmount(),
                        transaction.getCommissionAmount());
                break;
            case OUTPUT:
                walletService.withdrawReservedBalance(transaction.getUserWallet(), transaction.getAmount().add(transaction.getCommissionAmount()));
                companyWalletService.withdraw(transaction.getCompanyWallet(), transaction.getAmount(),
                        transaction.getCommissionAmount());
                break;
        }
        if (!transactionDao.provide(transaction.getId())) {
            throw new TransactionProvidingException("Failed to provide transaction #" + transaction.getId());
        }
    }

    @Override
    public DataTable<List<OperationViewDto>> showUserOperationHistory(
            Integer requesterUserId,
            Integer userId,
            AdminTransactionsFilterData filterData, DataTableParams dataTableParams, Locale locale) {
        requesterUserId = requesterUserId.equals(userId) ? null : requesterUserId;
        final List<Integer> wallets = walletService.getAllWallets(userId).stream()
                .mapToInt(Wallet::getId)
                .boxed()
                .collect(Collectors.toList());
        final DataTable<List<OperationViewDto>> result = new DataTable<>();
        final PagingData<List<Transaction>> transactions = transactionDao.findAllByUserWallets(requesterUserId, wallets,
                filterData, dataTableParams, locale);
        final List<OperationViewDto> operationViews = new ArrayList<>();
        for (final Transaction t : transactions.getData()) {
            OperationViewDto view = new OperationViewDto();
            view.setDatetime(t.getDatetime());
            view.setAmount(t.getAmount());
            view.setCommissionAmount(t.getCommissionAmount());
            view.setCurrency(t.getCurrency().getName());
            view.setOrder(t.getOrder());
            view.setStatus(merchantService.resolveTransactionStatus(t, locale));
            view.setOperationType(TransactionType.resolveFromOperationTypeAndSource(t.getSourceType(), t.getOperationType(), t.getAmount()));
            view.setMerchant(t.getMerchant() == null ? new Merchant(0, null, t.getSourceType().name()) : t.getMerchant());
            view.setSourceType(t.getSourceType().name());
            view.setSourceId(t.getSourceId());
            setTransactionMerchantAndOrder(view, t);
            operationViews.add(view);
        }
        result.setData(operationViews);
        result.setRecordsFiltered(transactions.getFiltered());
        result.setRecordsTotal(transactions.getTotal());
        return result;
    }

    private void setTransactionMerchantAndOrder(OperationViewDto view, Transaction transaction) {
        TransactionSourceType sourceType = transaction.getSourceType();
        OperationType operationType = transaction.getOperationType();
        BigDecimal amount = transaction.getAmount();
        view.setOperationType(TransactionType.resolveFromOperationTypeAndSource(sourceType, operationType, amount));
        if (sourceType == TransactionSourceType.REFILL || sourceType == TransactionSourceType.WITHDRAW) {
            view.setMerchant(transaction.getMerchant());
        } else {
            view.setMerchant(new Merchant(0, sourceType.name(), sourceType.name()));
        }

    }

    @Override
    public List<AccountStatementDto> getAccountStatement(CacheData cacheData, Integer walletId, Integer offset, Integer limit, Locale locale) {
        List<AccountStatementDto> result = transactionDao.getAccountStatement(walletId, offset, limit, locale);
        if (Cache.checkCache(cacheData, result)) {
            result = new ArrayList<AccountStatementDto>() {{
                add(new AccountStatementDto(false));
            }};
        }
        return result;
    }

    @Override
    public DataTable<List<AccountStatementDto>> getAccountStatementForAdmin(Integer walletId, Integer offset, Integer limit, Locale locale) {
        DataTable<List<AccountStatementDto>> result = new DataTable<>();
        int total = transactionDao.getStatementSize(walletId);
        result.setRecordsFiltered(total);
        result.setRecordsTotal(total);
        result.setData(transactionDao.getAccountStatement(walletId, offset, limit, locale)
                .stream().filter(statement -> statement.getTransactionId() > 0).collect(Collectors.toList()));
        LOG.debug(result);
        return result;
    }

    @Override
    public BigDecimal maxAmount() {
        return transactionDao.maxAmount();
    }

    @Override
    @Transactional
    public boolean setStatusById(Integer trasactionId, Integer statusId) {
        if (trasactionId == 0) return true;
        return transactionDao.setStatusById(trasactionId, statusId);
    }

    @Override
    public List<Transaction> getPayedRefTransactionsByOrderId(int orderId) {
        return transactionDao.getPayedRefTransactionsByOrderId(orderId);
    }

    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    @Override
    public List<InOutReportDto> getInOutSummaryByPeriodAndRoles(LocalDateTime startTime,
                                                                LocalDateTime endTime,
                                                                List<UserRole> userRoles) {
        return transactionDao.getInOutSummaryByPeriodAndRoles(startTime, endTime, userRoles);
    }

    @Transactional(transactionManager = "slaveTxManager", readOnly = true)
    @Override
    public List<UserSummaryDto> getUsersWalletSummaryData(LocalDateTime startTime,
                                                          LocalDateTime endTime,
                                                          String userEmail,
                                                          int requesterId) {
        return transactionDao.getUsersWalletSummaryDataByPeriodAndRoles(startTime, endTime, userEmail, requesterId);
    }
}
