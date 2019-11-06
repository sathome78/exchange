package me.exrates.dao;

import me.exrates.model.referral.ReferralTransaction;

import java.util.List;

public interface ReferralTransactionDao {
    ReferralTransaction createReferralTransaction(ReferralTransaction referralTransaction);

    List<ReferralTransaction> findTransactionByUserIdAndLink(int userId, String link);

}
