package me.exrates.dao;

import me.exrates.model.referral.ReferralTransaction;

public interface ReferralTransactionDao {
    boolean createReferralTransaction(ReferralTransaction referralTransaction);
}
