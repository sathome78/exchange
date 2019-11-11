package me.exrates.service.referral;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.ReferralLinkDao;
import me.exrates.dao.ReferralTransactionDao;
import me.exrates.model.CompanyWallet;
import me.exrates.model.Currency;
import me.exrates.model.User;
import me.exrates.model.Wallet;
import me.exrates.model.dto.referral.enums.ReferralLevel;
import me.exrates.model.enums.ActionType;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.UserStatus;
import me.exrates.model.referral.ReferralLink;
import me.exrates.model.referral.ReferralTransaction;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.service.CompanyWalletService;
import me.exrates.service.CurrencyService;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@PropertySource({"classpath:referral.properties"})
@Service
@Log4j2(topic = "referral_log")
public class ReferralServiceImpl implements ReferralService {

    private final CurrencyService currencyService;
    private final UserService userService;
    private final ReferralLinkDao referralLinkDao;
    private final CompanyWalletService companyWalletService;
    private final WalletService walletService;
    private final ReferralTransactionDao referralTransactionDao;

    private final BigDecimal firstLevelCommissionPercent;
    private final BigDecimal secondLevelCommissionPercent;
    private final BigDecimal thirdLevelCommissionPercent;

    @Autowired
    public ReferralServiceImpl(CurrencyService currencyService,
                               UserService userService,
                               ReferralLinkDao referralLinkDao,
                               CompanyWalletService companyWalletService,
                               WalletService walletService,
                               ReferralTransactionDao referralTransactionDao,
                               @Value("${referral.first_level_commission}") BigDecimal firstLevelCommissionPercent,
                               @Value("${referral.second_level_commission}") BigDecimal secondLevelCommissionPercent,
                               @Value("${referral.third_level_commission}") BigDecimal thirdLevelCommissionPercent) {
        this.currencyService = currencyService;
        this.userService = userService;
        this.referralLinkDao = referralLinkDao;
        this.companyWalletService = companyWalletService;
        this.walletService = walletService;
        this.referralTransactionDao = referralTransactionDao;
        this.firstLevelCommissionPercent = firstLevelCommissionPercent;
        this.secondLevelCommissionPercent = secondLevelCommissionPercent;
        this.thirdLevelCommissionPercent = thirdLevelCommissionPercent;
    }

    @Override
    public void processReferralAndCommission(int currencyId, BigDecimal totalCommission, int userId) {
        User user = userService.getUserById(userId);
        if (StringUtils.isEmpty(user.getInviteReferralLink()) || user.getUserStatus() == UserStatus.DELETED
                || user.getRole() == UserRole.BOT_TRADER || user.getRole() == UserRole.VIP_USER) {
//            provideWithOutReferrals(currencyId, totalCommission, user);
        } else {
            Map<ReferralLevel, User> levelUserMap = findAllReferralsV2(user.getInviteReferralLink());
            processReferralPayment(currencyId, totalCommission, levelUserMap);
        }
    }

    private void processReferralPayment(int currencyId, BigDecimal totalCommission, Map<ReferralLevel, User> levelUserMap) {
        Currency currency = currencyService.findById(currencyId);
        CompanyWallet cWallet = companyWalletService.findByCurrency(currency);

        for (Map.Entry<ReferralLevel, User> levelUserEntry : levelUserMap.entrySet()) {
            User user = levelUserEntry.getValue();
            ReferralLevel level = levelUserEntry.getKey();
            log.info("Starting perform referral transaction {}, {}", level, user.getEmail());
            Wallet userWallet = walletService.findByUserAndCurrency(user.getId(), currencyId);
            BigDecimal percentCommission = definePercentCommissionByReferralLevel(level);
            final BigDecimal amount = BigDecimalProcessing.doAction(totalCommission, percentCommission, ActionType.MULTIPLY_PERCENT, RoundingMode.HALF_DOWN);
            totalCommission = BigDecimalProcessing.doAction(totalCommission, amount, ActionType.DEVIDE, RoundingMode.HALF_DOWN);

            if (totalCommission.compareTo(BigDecimal.ZERO) < 0) {
                log.error("Total commission must be more ZERO {}, currency amount commission {}", totalCommission, amount);
                throw new RuntimeException("Something bad, total commission must be more ZERO");
            }

            ReferralTransaction referralTransaction = ReferralTransaction.builder()
                    .amount(amount)
                    .currencyId(currencyId)
                    .currencyName(currency.getName())
                    .link(user.getInviteReferralLink())
                    .build();


            if (referralTransactionDao.createReferralTransaction(referralTransaction)) {
                walletService.performReferralBalanceUpdate(userWallet.getId(), amount);
            }
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
