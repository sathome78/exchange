package me.exrates.service.referral;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.ReferralLinkDao;
import me.exrates.dao.ReferralRequestDao;
import me.exrates.dao.ReferralRequestTransferDao;
import me.exrates.dao.ReferralTransactionDao;
import me.exrates.model.CompanyWallet;
import me.exrates.model.Currency;
import me.exrates.model.Transaction;
import me.exrates.model.User;
import me.exrates.model.Wallet;
import me.exrates.model.dto.referral.ReferralIncomeDto;
import me.exrates.model.dto.referral.ReferralStructureDto;
import me.exrates.model.dto.referral.enums.ReferralLevel;
import me.exrates.model.enums.ActionType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.UserStatus;
import me.exrates.model.referral.ReferralLink;
import me.exrates.model.referral.ReferralRequest;
import me.exrates.model.referral.ReferralRequestTransfer;
import me.exrates.model.referral.ReferralTransaction;
import me.exrates.model.referral.enums.ReferralProcessStatus;
import me.exrates.model.referral.enums.ReferralRequestStatus;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.service.CompanyWalletService;
import me.exrates.service.CurrencyService;
import me.exrates.service.TransactionService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import org.apache.commons.lang3.StringUtils;
import org.jboss.aerogear.security.otp.api.Base32;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@PropertySource({"classpath:referral.properties"})
@Service
@Log4j2(topic = "referral_log")
public class ReferralServiceImpl implements ReferralService {
    private final static int CHUNK = 20;
    private final static String USD = "USD";
    private final static String BTC = "BTC";
    private final static String USDT = "USDT";
    private final static List<String> CURRENCIES = Arrays.asList(USD, BTC, USDT);
    private final static List<String> CURRENCIES_EXTENDED_LIST = Arrays.asList(USD, BTC, USDT, "ETH", "EUR", "TUSD", "RUB",
            "IDR", "AED", "TRY", "VND", "UAH", "CNY", "NGN", "TRX", "BTT");

    private final CurrencyService currencyService;
    private final UserService userService;
    private final ReferralLinkDao referralLinkDao;
    private final CompanyWalletService companyWalletService;
    private final WalletService walletService;
    private final ReferralTransactionDao referralTransactionDao;
    private final ReferralRequestDao referralRequestDao;
    private final TransactionService transactionService;
    private final ReferralRequestTransferDao referralRequestTransferDao;

    private final BigDecimal firstLevelCommissionPercent;
    private final BigDecimal secondLevelCommissionPercent;
    private final BigDecimal thirdLevelCommissionPercent;
    private final String defaultNameStructure;
    private final String referralUrl;
    private final Integer referralLinkMaxCount;

    @Autowired
    public ReferralServiceImpl(CurrencyService currencyService,
                               UserService userService,
                               ReferralLinkDao referralLinkDao,
                               CompanyWalletService companyWalletService,
                               WalletService walletService,
                               ReferralTransactionDao referralTransactionDao,
                               ReferralRequestDao referralRequestDao,
                               TransactionService transactionService,
                               ReferralRequestTransferDao referralRequestTransferDao,
                               @Value("${referral.first_level_commission}") BigDecimal firstLevelCommissionPercent,
                               @Value("${referral.second_level_commission}") BigDecimal secondLevelCommissionPercent,
                               @Value("${referral.third_level_commission}") BigDecimal thirdLevelCommissionPercent,
                               @Value("${referral.link.name.default}") String defaultNameStructure,
                               @Value("${referral.url}") String referralUrl,
                               @Value("${referral.link.max.count}") Integer referralLinkMaxCount) {
        this.currencyService = currencyService;
        this.userService = userService;
        this.referralLinkDao = referralLinkDao;
        this.companyWalletService = companyWalletService;
        this.walletService = walletService;
        this.referralTransactionDao = referralTransactionDao;
        this.referralRequestDao = referralRequestDao;
        this.transactionService = transactionService;
        this.referralRequestTransferDao = referralRequestTransferDao;
        this.firstLevelCommissionPercent = firstLevelCommissionPercent;
        this.secondLevelCommissionPercent = secondLevelCommissionPercent;
        this.thirdLevelCommissionPercent = thirdLevelCommissionPercent;
        this.defaultNameStructure = defaultNameStructure;
        this.referralUrl = referralUrl;
        this.referralLinkMaxCount = referralLinkMaxCount;
    }

    @Scheduled(fixedDelay = 10000)
    public void processReferralRequests() {
        boolean filled = false;
        Collection<ReferralRequest> requests =
                referralRequestDao.getReferralRequestsByStatus(CHUNK, ReferralProcessStatus.CREATED);
        while (!filled) {
            for (ReferralRequest request : requests) {
                processReferralAndCommission(request);
            }
            filled = true;
        }
    }

    @Scheduled(fixedDelay = 120000) //each 2 minutes
    public void processReferralTransferRequests() {
        ReferralRequestStatus status = ReferralRequestStatus.WAITING_AUTO_POSTING;
        List<ReferralRequestTransfer> waitingRequests =
                referralRequestTransferDao.findByStatus(Collections.singletonList(status));

        for (ReferralRequestTransfer requestTransfer : waitingRequests) {
            processTransferRequest(requestTransfer);
        }
    }

    private void processReferralAndCommission(ReferralRequest request) {
        try {
            User user = userService.getUserById(request.getUserId());
            if (StringUtils.isNoneEmpty(user.getInviteReferralLink()) && user.getUserStatus() != UserStatus.DELETED
                    && user.getRole() != UserRole.BOT_TRADER && user.getRole() != UserRole.VIP_USER) {
                Map<ReferralLevel, User> levelUserMap = findAllReferralsV2(user.getInviteReferralLink());
                processReferralPayment(request, levelUserMap, user);
            }
        } catch (Exception e) {
            referralRequestDao.updateStatusReferralRequest(request.getId(), ReferralProcessStatus.ERROR);
        } finally {
            referralRequestDao.updateStatusReferralRequest(request.getId(), ReferralProcessStatus.PROCESSED);
        }
    }

    @Override
    public void saveReferralRequest(List<ReferralRequest> requests) {
        referralRequestDao.saveReferralRequestsBatch(requests);
    }

    @Override
    public List<ReferralStructureDto> getReferralStructure(String email) {
        User user = userService.findByEmail(email);
        List<ReferralLink> userLinks = referralLinkDao.findByUserId(user.getId());

        if (userLinks.isEmpty()) {
            ReferralLink link = ReferralLink.builder()
                    .main(true)
                    .name(defaultNameStructure)
                    .userId(user.getId())
                    .link(Base32.random())
                    .createdAt(new Date())
                    .build();
            boolean create = referralLinkDao.createReferralLink(link);
            if (create) {
                ReferralStructureDto structureDto = ReferralStructureDto.builder()
                        .name(link.getName())
                        .link(referralUrl + link.getLink())
                        .numberChild(0)
                        .earnedBTC(BigDecimal.ZERO)
                        .earnedUSD(BigDecimal.ZERO)
                        .earnedUSDT(BigDecimal.ZERO)
                        .main(link.isMain())
                        .build();
                return Collections.singletonList(structureDto);
            } else {
                //todo change ex
                throw new RuntimeException("Something went wrong");
            }
        }

        List<ReferralStructureDto> result = new ArrayList<>(userLinks.size());

        for (ReferralLink link : userLinks) {
            int count = 0;
            ReferralStructureDto structureDto = new ReferralStructureDto(link.getName(), referralUrl + link.getLink(), link.isMain());


            List<User> firstReferralUsers = userService.findByInviteReferralLink(link.getLink());
            count += firstReferralUsers.size();
            if (firstReferralUsers.isEmpty()) {
                completeIteration(structureDto, count, result);
                continue;
            }
            List<Integer> usersFromFirstLevel = firstReferralUsers.stream().map(User::getId).collect(Collectors.toList());
            Map<String, BigDecimal> currencyEarnedFirstLevel =
                    referralTransactionDao.getEarnedByUsersFromAndUserToAndCurrencies(usersFromFirstLevel, user.getId(), CURRENCIES);
            addEarnedMapToValues(structureDto, currencyEarnedFirstLevel);
            List<ReferralLink> firstLevelsLinks =
                    referralLinkDao.findByListUserId(firstReferralUsers.stream().map(User::getId).collect(Collectors.toList()));

            for (ReferralLink firstLevelReferralLink : firstLevelsLinks) {
                List<User> secondReferralUsers = userService.findByInviteReferralLink(firstLevelReferralLink.getLink());
                count += secondReferralUsers.size();
                if (secondReferralUsers.isEmpty()) {
                    completeIteration(structureDto, count, result);
                    continue;
                }

                List<Integer> usersFromSecondLevel = secondReferralUsers.stream().map(User::getId).collect(Collectors.toList());
                Map<String, BigDecimal> currencyEarnedSecond =
                        referralTransactionDao.getEarnedByUsersFromAndUserToAndCurrencies(usersFromSecondLevel, user.getId(), CURRENCIES);
                addEarnedMapToValues(structureDto, currencyEarnedSecond);
                List<ReferralLink> secondLevelLinks =
                        referralLinkDao.findByListUserId(secondReferralUsers.stream().map(User::getId).collect(Collectors.toList()));

                for (ReferralLink secondLevelLink : secondLevelLinks) {
                    List<User> thirdReferralUsers = userService.findByInviteReferralLink(secondLevelLink.getLink());
                    count += thirdReferralUsers.size();
                    if (thirdReferralUsers.isEmpty()) {
                        completeIteration(structureDto, count, result);
                        continue;
                    }

                    List<Integer> usersFromThirdLevel = thirdReferralUsers.stream().map(User::getId).collect(Collectors.toList());
                    Map<String, BigDecimal> currencyEarnedThird =
                            referralTransactionDao.getEarnedByUsersFromAndUserToAndCurrencies(usersFromThirdLevel, user.getId(), CURRENCIES);
                    addEarnedMapToValues(structureDto, currencyEarnedThird);
                    completeIteration(structureDto, count, result);
                }
            }
            completeIteration(structureDto, count, result);
        }
        return result;
    }

    @Override
    public List<ReferralStructureDto> getChildReferralStructure(String email, Integer userId, int level, String link) {
        if (level == 3) {
            throw new RuntimeException("Cannot get detail from third levels");
        }

        User user = userService.findByEmail(email);

        if (level == 0) {
            List<ReferralStructureDto> result = new ArrayList<>();
            List<User> users = userService.findByInviteReferralLink(link);
            if (users.isEmpty()) {
                return Collections.emptyList();
            }
            for (User firstLevelUser : users) {
                int count = 0;
                ReferralStructureDto structureDto = new ReferralStructureDto(firstLevelUser.getEmail(), level + 1, firstLevelUser.getId());
                List<ReferralLink> firstLevelReferralLinks = referralLinkDao.findByUserId(firstLevelUser.getId());
                if (firstLevelReferralLinks.isEmpty()) {
                    completeIteration(structureDto, count, result);
                    continue;
                }
                List<String> links = firstLevelReferralLinks.stream().map(ReferralLink::getLink).collect(Collectors.toList());
                List<User> usersSecondLevels = userService.findByInviteReferralLink(links);
                count += usersSecondLevels.size();
                Map<String, BigDecimal> currencyEarnedFirstLevel =
                        referralTransactionDao.getEarnedByUsersFromAndUserToAndCurrencies(
                                Collections.singletonList(firstLevelUser.getId()),
                                user.getId(),
                                CURRENCIES);
                addEarnedMapToValues(structureDto, currencyEarnedFirstLevel);
                completeIteration(structureDto, count, result);
            }
            return result;
        }

        if (level == 1 || level == 2) {
            List<ReferralStructureDto> result = new ArrayList<>();
            List<ReferralLink> levelReferralLinks = referralLinkDao.findByUserId(userId);

            for (ReferralLink referralLink : levelReferralLinks) {
                List<User> usersChild = userService.findByInviteReferralLink(referralLink.getLink());
                for (User userChild : usersChild) {
                    ReferralStructureDto structureDto = new ReferralStructureDto(userChild.getEmail(), level + 1, userChild.getId());
                    int count = 0;

                    List<ReferralLink> firstLevelReferralLinks = referralLinkDao.findByUserId(userChild.getId());
                    if (firstLevelReferralLinks.isEmpty()) {
                        completeIteration(structureDto, level == 1 ? count : null, result);
                        continue;
                    }

                    List<String> links = firstLevelReferralLinks.stream().map(ReferralLink::getLink).collect(Collectors.toList());
                    List<User> usersSecondLevels = userService.findByInviteReferralLink(links);
                    count += usersSecondLevels.size();
                    if (usersSecondLevels.isEmpty()) {
                        completeIteration(structureDto, level == 1 ? count : null, result);
                        continue;
                    }

                    Map<String, BigDecimal> currencyEarnedFirstLevel =
                            referralTransactionDao.getEarnedByUsersFromAndUserToAndCurrencies(
                                    Collections.singletonList(userChild.getId()),
                                    user.getId(),
                                    CURRENCIES);

                    addEarnedMapToValues(structureDto, currencyEarnedFirstLevel);
                    completeIteration(structureDto, count, result);
                }
            }
            return result;
        }
        throw new RuntimeException("Cannot get detail for level bigger 3");
    }

    @Override
    public boolean updateReferralName(String email, String link, String name) {
        User user = userService.findByEmail(email);
        ReferralLink referralLink = referralLinkDao.findByUserIdAndLink(user.getId(), link)
                .orElseThrow(() -> new RuntimeException("This link doesnt relate to this user"));

        referralLink.setName(name);
        return referralLinkDao.updateReferralLink(referralLink);
    }

    @Override
    public ReferralStructureDto createReferralLink(String email, String name) {
        User user = userService.findByEmail(email);
        List<ReferralLink> links = referralLinkDao.findByUserId(user.getId());
        if (links.size() > referralLinkMaxCount) {
            throw new RuntimeException("You have 50 referral links already.");
        }

        ReferralLink link = ReferralLink.builder()
                .main(false)
                .name(name)
                .userId(user.getId())
                .link(Base32.random())
                .createdAt(new Date())
                .build();
        boolean create = referralLinkDao.createReferralLink(link);
        if (!create) {
            throw new RuntimeException("Error while creating new link");
        }
        return ReferralStructureDto.builder()
                .name(link.getName())
                .link(referralUrl + link.getLink())
                .numberChild(0)
                .earnedBTC(BigDecimal.ZERO)
                .earnedUSD(BigDecimal.ZERO)
                .earnedUSDT(BigDecimal.ZERO)
                .build();
    }

    @Override
    public List<ReferralIncomeDto> getReferralIncome(String email) {
        List<ReferralIncomeDto> referralsIncomeDto =
                referralLinkDao.getReferralsIncomeDto(email, CURRENCIES_EXTENDED_LIST);

        referralsIncomeDto.forEach(referralIncomeDto -> {
            BigDecimal referralBalance = referralIncomeDto.getReferralBalance();
            BigDecimal cupIncome = referralIncomeDto.getCupIncome();
            if (referralBalance.compareTo(cupIncome) > 0) {
                referralIncomeDto.setAvailable(true);
            } else {
                referralIncomeDto.setAvailable(false);
                BigDecimal diff =
                        BigDecimalProcessing.doAction(cupIncome, referralBalance, ActionType.SUBTRACT, RoundingMode.HALF_DOWN);
                referralIncomeDto.setLeftForCup(diff);
            }
        });

        return referralsIncomeDto;
    }

    @Transactional
    @Override
    public boolean createTransferRequest(String email, String currency) {
        ReferralIncomeDto referralIncomeDto = referralLinkDao
                .getReferralIncomeDto(email, currency)
                .orElseThrow(() -> new RuntimeException("Not exist currency referral balance for currency " + currency));
        BigDecimal referralBalance = referralIncomeDto.getReferralBalance();
        if (referralBalance.compareTo(referralIncomeDto.getCupIncome()) <= 0) {
            throw new RuntimeException("Not enough balance for transfer balance to active balance");
        }

        BigDecimal manualConfirmAboveSum = referralIncomeDto.getManualConfirmAboveSum();
        ReferralRequestStatus status;
        if (referralBalance.compareTo(manualConfirmAboveSum) > 0) {
            status = ReferralRequestStatus.WAITING_MANUAL_POSTING;
        } else {
            status = ReferralRequestStatus.WAITING_AUTO_POSTING;
        }

        ReferralRequestTransfer requestTransfer = ReferralRequestTransfer.builder()
                .amount(referralBalance)
                .currencyId(referralIncomeDto.getCurrencyId())
                .currencyName(referralIncomeDto.getCurrencyName())
                .status(status)
                .userId(referralIncomeDto.getUserId())
                .build();
        boolean result = referralRequestTransferDao.createReferralRequestTransfer(requestTransfer).getId() > 0;
        if (result) {
            Wallet wallet = walletService.findByUserAndCurrency(referralIncomeDto.getUserId(), currency);
            walletService.updateReferralBalance(wallet.getId(), BigDecimal.ZERO);
        }
        return result;
    }

    private void processTransferRequest(ReferralRequestTransfer requestTransfer) {
        Wallet wallet =
                walletService.findByUserAndCurrency(requestTransfer.getUserId(), requestTransfer.getCurrencyId());
        Currency currency = currencyService.findById(requestTransfer.getCurrencyId());
        if (walletService.transferReferralBalanceToActive(wallet.getId(), requestTransfer.getAmount())) {
            Transaction transaction = Transaction.builder()
                    .userWallet(wallet)
                    .currency(currency)
                    .operationType(OperationType.INPUT)
                    .sourceType(TransactionSourceType.REFERRAL)
                    .sourceId(requestTransfer.getId())
                    .activeBalanceBefore(wallet.getActiveBalance())
                    .commissionAmount(BigDecimal.ZERO)
                    .build();
            transaction = transactionService.save(transaction);

            if (transaction.getId() > 0) {
                requestTransfer.setStatus(ReferralRequestStatus.POSTED_AUTO);
                referralRequestTransferDao.updateReferralRequestTransfer(requestTransfer);
            } else {
                requestTransfer.setStatus(ReferralRequestStatus.DECLINED_ERROR);
                referralRequestTransferDao.updateReferralRequestTransfer(requestTransfer);
            }
        }
    }

    private void completeIteration(ReferralStructureDto structureDto, Integer count, List<ReferralStructureDto> result) {
        structureDto.setNumberChild(count);
        result.add(structureDto);
    }

    private void addEarnedMapToValues(ReferralStructureDto referralStructureDto,
                                      Map<String, BigDecimal> earned) {
        BigDecimal earnedBTC = referralStructureDto.getEarnedBTC();
        referralStructureDto.setEarnedBTC(earnedBTC.add(earned.getOrDefault(BTC, BigDecimal.ZERO)));

        BigDecimal earnedUSD = referralStructureDto.getEarnedUSD();
        referralStructureDto.setEarnedUSD(earnedUSD.add(earned.getOrDefault(USD, BigDecimal.ZERO)));

        BigDecimal earnedOnUSDT = referralStructureDto.getEarnedUSDT();
        referralStructureDto.setEarnedUSDT(earnedOnUSDT.add(earned.getOrDefault(USDT, BigDecimal.ZERO)));
    }

    private void processReferralPayment(ReferralRequest request, Map<ReferralLevel, User> levelUserMap, User userFrom) {
        Currency currency = currencyService.findById(request.getCurrencyId());
        CompanyWallet cWallet = companyWalletService.findByCurrency(currency);
        BigDecimal totalCommission = request.getAmount();
        List<Transaction> transactions = new ArrayList<>();

        for (Map.Entry<ReferralLevel, User> levelUserEntry : levelUserMap.entrySet()) {
            User user = levelUserEntry.getValue();
            ReferralLevel level = levelUserEntry.getKey();
            log.info("Starting perform referral transaction {}, {}", level, user.getEmail());
            Wallet userWallet = walletService.findByUserAndCurrency(user.getId(), currency.getId());
            BigDecimal percentCommission = definePercentCommissionByReferralLevel(level);
            final BigDecimal amount = BigDecimalProcessing.doAction(totalCommission, percentCommission, ActionType.MULTIPLY_PERCENT, RoundingMode.HALF_DOWN);
            totalCommission = BigDecimalProcessing.doAction(totalCommission, amount, ActionType.DEVIDE, RoundingMode.HALF_DOWN);

            if (totalCommission.compareTo(BigDecimal.ZERO) < 0) {
                log.error("Total commission must be more ZERO {}, currency amount commission {}", totalCommission, amount);
                throw new RuntimeException("Something bad, total commission must be more ZERO");
            }

            ReferralTransaction referralTransaction = ReferralTransaction.builder()
                    .amount(amount)
                    .currencyId(currency.getId())
                    .currencyName(currency.getName())
                    .userIdFrom(userFrom.getId())
                    .userIdTo(user.getId())
                    .build();

            if (referralTransactionDao.createReferralTransaction(referralTransaction)) {
                walletService.performReferralBalanceUpdate(userWallet.getId(), amount, ActionType.ADD);
                companyWalletService.withdrawReservedBalance(cWallet, amount);
            }

            Transaction transaction = Transaction.builder()
                    .amount(amount)
                    .userWallet(userWallet)
                    .commissionAmount(BigDecimal.ZERO)
                    .operationType(OperationType.REFERRAL)
                    .currency(currency)
                    .sourceType(TransactionSourceType.ORDER)
                    .sourceId(request.getOrderId())
                    .datetime(LocalDateTime.now())
                    .build();
            transactions.add(transaction);
        }

        if (transactions.size() > 0) {
            transactionService.save(transactions);
        }
    }

    private Map<ReferralLevel, User> findAllReferrals(String referralLink) {
        Map<ReferralLevel, User> result = new HashMap<>(3);
        ReferralLink firstLevelLink = referralLinkDao.findByLink(referralLink)
                .orElseThrow(() -> new RuntimeException("First level referral link not fount"));

        User firstLevelUser = userService.getUserById(firstLevelLink.getUserId());
        result.put(ReferralLevel.FIRST, firstLevelUser);
        String secondLevelLinkString = firstLevelUser.getInviteReferralLink();
        if (Objects.isNull(secondLevelLinkString)) {
            return result;
        }

        ReferralLink secondLevelLink = referralLinkDao.findByLink(secondLevelLinkString).orElseThrow(
                () -> new RuntimeException("Second level referral link not fount"));

        User secondLevelUser = userService.getUserById(secondLevelLink.getUserId());
        result.put(ReferralLevel.SECOND, secondLevelUser);
        if (Objects.isNull(secondLevelUser.getInviteReferralLink())) {
            return result;
        }

        String thirdLevelString = secondLevelUser.getInviteReferralLink();
        ReferralLink thirdLevelLink = referralLinkDao.findByLink(thirdLevelString).orElseThrow(
                () -> new RuntimeException("Third level referral link not fount"));
        User thirdLevelUser = userService.getUserById(thirdLevelLink.getUserId());
        result.put(ReferralLevel.THIRD, thirdLevelUser);
        return result;
    }

    private Map<ReferralLevel, User> findAllReferralsV2(String referralLinkString) {
        Map<ReferralLevel, User> result = new HashMap<>(3);
        String nextLink = referralLinkString;
        for (ReferralLevel referralLevel : ReferralLevel.values()) {
            if (StringUtils.isEmpty(nextLink)) {
                break;
            }

            ReferralLink referralLink = referralLinkDao.findByLink(nextLink)
                    .orElseThrow(() -> new RuntimeException(
                            String.format("%s level referral link not fount", referralLevel.name())));

            User user = userService.getUserById(referralLink.getUserId());
            if (user.getUserStatus() == UserStatus.DELETED) {
                continue;
            }
            result.put(referralLevel, user);
            nextLink = user.getInviteReferralLink();
        }

        return result;
    }

    private BigDecimal definePercentCommissionByReferralLevel(ReferralLevel level) {
        switch (level) {
            case FIRST:
                return firstLevelCommissionPercent;
            case SECOND:
                return secondLevelCommissionPercent;
            case THIRD:
                return thirdLevelCommissionPercent;
            default:
                throw new RuntimeException("Not defined type referral type " + level.name());
        }
    }
}
