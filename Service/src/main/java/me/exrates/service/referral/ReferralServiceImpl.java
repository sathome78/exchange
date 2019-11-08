package me.exrates.service.referral;

import lombok.extern.log4j.Log4j2;
import me.exrates.dao.ReferralLinkDao;
import me.exrates.model.User;
import me.exrates.model.dto.referral.enums.ReferralLevel;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.UserStatus;
import me.exrates.model.referral.ReferralLink;
import me.exrates.service.CompanyWalletService;
import me.exrates.service.CurrencyService;
import me.exrates.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    private final BigDecimal firstLevelCommissionPercent;
    private final BigDecimal secondLevelCommissionPercent;
    private final BigDecimal thirdLevelCommissionPercent;

    @Autowired
    public ReferralServiceImpl(CurrencyService currencyService,
                               UserService userService,
                               ReferralLinkDao referralLinkDao,
                               CompanyWalletService companyWalletService,
                               @Value("${referral.first_level_commission}") BigDecimal firstLevelCommissionPercent,
                               @Value("${referral.second_level_commission}") BigDecimal secondLevelCommissionPercent,
                               @Value("${referral.third_level_commission}") BigDecimal thirdLevelCommissionPercent) {
        this.currencyService = currencyService;
        this.userService = userService;
        this.referralLinkDao = referralLinkDao;
        this.companyWalletService = companyWalletService;
        this.firstLevelCommissionPercent = firstLevelCommissionPercent;
        this.secondLevelCommissionPercent = secondLevelCommissionPercent;
        this.thirdLevelCommissionPercent = thirdLevelCommissionPercent;
    }

    @Override
    public void processReferralAndCommission(int currencyId, BigDecimal totalCommission, int userId) {
        User user = userService.getUserById(userId);
        if (StringUtils.isEmpty(user.getInviteReferralLink()) || user.getUserStatus() == UserStatus.DELETED
                || user.getRole() == UserRole.BOT_TRADER || user.getRole() == UserRole.VIP_USER) {
            provideWithOutReferrals(currencyId, totalCommission, user);
        } else {
            Map<ReferralLevel, User> levelUserMap = findAllReferralsV2(user.getInviteReferralLink());
            provideWithReferrals(currencyId, totalCommission, levelUserMap);
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

    private
}
