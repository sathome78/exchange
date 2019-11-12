package me.exrates.service.referral;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.ReferralLinkDao;
import me.exrates.dao.ReferralRequestDao;
import me.exrates.dao.ReferralTransactionDao;
import me.exrates.model.CompanyWallet;
import me.exrates.model.Currency;
import me.exrates.model.Transaction;
import me.exrates.model.User;
import me.exrates.model.Wallet;
import me.exrates.model.dto.referral.ReferralStructureDto;
import me.exrates.model.dto.referral.enums.ReferralLevel;
import me.exrates.model.enums.ActionType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.TransactionSourceType;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.UserStatus;
import me.exrates.model.referral.ReferralLink;
import me.exrates.model.referral.ReferralRequest;
import me.exrates.model.referral.ReferralTransaction;
import me.exrates.model.referral.enums.ReferralProcessStatus;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.service.CompanyWalletService;
import me.exrates.service.CurrencyService;
import me.exrates.service.TransactionService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;


@PropertySource({"classpath:referral.properties"})
@Service
@Log4j2(topic = "referral_log")
public class ReferralServiceImpl implements ReferralService {
    private final static int CHUNK = 20;

    private final CurrencyService currencyService;
    private final UserService userService;
    private final ReferralLinkDao referralLinkDao;
    private final CompanyWalletService companyWalletService;
    private final WalletService walletService;
    private final ReferralTransactionDao referralTransactionDao;
    private final ReferralRequestDao referralRequestDao;
    private final TransactionService transactionService;

    private final BigDecimal firstLevelCommissionPercent;
    private final BigDecimal secondLevelCommissionPercent;
    private final BigDecimal thirdLevelCommissionPercent;
    private final String defaultNameStructure;
    private final String referralUrl;

    @Autowired
    public ReferralServiceImpl(CurrencyService currencyService,
                               UserService userService,
                               ReferralLinkDao referralLinkDao,
                               CompanyWalletService companyWalletService,
                               WalletService walletService,
                               ReferralTransactionDao referralTransactionDao,
                               ReferralRequestDao referralRequestDao,
                               TransactionService transactionService,
                               @Value("${referral.first_level_commission}") BigDecimal firstLevelCommissionPercent,
                               @Value("${referral.second_level_commission}") BigDecimal secondLevelCommissionPercent,
                               @Value("${referral.third_level_commission}") BigDecimal thirdLevelCommissionPercent,
                               @Value("${referral.link.name.default}") String defaultNameStructure,
                               @Value("${referral.url}") String referralUrl) {
        this.currencyService = currencyService;
        this.userService = userService;
        this.referralLinkDao = referralLinkDao;
        this.companyWalletService = companyWalletService;
        this.walletService = walletService;
        this.referralTransactionDao = referralTransactionDao;
        this.referralRequestDao = referralRequestDao;
        this.transactionService = transactionService;
        this.firstLevelCommissionPercent = firstLevelCommissionPercent;
        this.secondLevelCommissionPercent = secondLevelCommissionPercent;
        this.thirdLevelCommissionPercent = thirdLevelCommissionPercent;
        this.defaultNameStructure = defaultNameStructure;
        this.referralUrl = referralUrl;
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

    private void processReferralAndCommission(ReferralRequest request) {
        try {
            User user = userService.getUserById(request.getUserId());
            if (StringUtils.isNoneEmpty(user.getInviteReferralLink()) && user.getUserStatus() != UserStatus.DELETED
                    && user.getRole() != UserRole.BOT_TRADER && user.getRole() != UserRole.VIP_USER) {
                Map<ReferralLevel, User> levelUserMap = findAllReferralsV2(user.getInviteReferralLink());
                processReferralPayment(request, levelUserMap);
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
        List<ReferralLink> links = referralLinkDao.findByUserId(user.getId());

        if (links.isEmpty()) {
            ReferralLink link = ReferralLink.builder()
                    .main(true)
                    .name(defaultNameStructure)
                    .link(UUID.randomUUID().toString())
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
                        .build();
                return Collections.singletonList(structureDto);
            } else {
                //todo change ex
                throw new RuntimeException("Something went wrong");
            }
        }

        List<ReferralStructureDto> result = new ArrayList<>(links.size());
        for (ReferralLink link : links) {
        }

        return null;
    }

    private void processReferralPayment(ReferralRequest request, Map<ReferralLevel, User> levelUserMap) {
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
                    .link(user.getInviteReferralLink())
                    .build();

            if (referralTransactionDao.createReferralTransaction(referralTransaction)) {
                walletService.performReferralBalanceUpdate(userWallet.getId(), amount);
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
