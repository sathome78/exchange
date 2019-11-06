package me.exrates.dao;

import me.exrates.model.referral.ReferralLink;

import java.util.List;
import java.util.Optional;

public interface ReferralLinkDao {

    Optional<ReferralLink> findByUserIdAndLink(int userId, String link);

    List<ReferralLink> findByLink(String link);

    List<ReferralLink> findByUserId(int userId);

    boolean createReferralLink(ReferralLink referralLink);

    boolean updateReferralLink(ReferralLink referralLink);

    boolean deleteReferralLink(ReferralLink referralLink);
}
