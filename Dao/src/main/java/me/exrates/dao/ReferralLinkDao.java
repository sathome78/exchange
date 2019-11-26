package me.exrates.dao;

import me.exrates.model.dto.referral.ReferralIncomeDto;
import me.exrates.model.dto.referral.UserReferralLink;
import me.exrates.model.referral.ReferralLink;

import java.util.List;
import java.util.Optional;

public interface ReferralLinkDao {

    Optional<ReferralLink> findByUserIdAndLink(int userId, String link);

    Optional<ReferralLink> findByLink(String link);

    List<ReferralLink> findByUserId(int userId);

    List<ReferralLink> findByListUserId(List<Integer> userIds);

    List<String> findUsersLinks(List<String> links);

    List<UserReferralLink> findUsersByLink(String link);

    boolean createReferralLink(ReferralLink referralLink);

    boolean updateReferralLink(ReferralLink referralLink);

    boolean deleteReferralLink(ReferralLink referralLink);

    List<ReferralIncomeDto> getReferralsIncomeDto(String email, List<String> currencies);

    Optional<ReferralIncomeDto> getReferralIncomeDto(String email, String currency);
}
