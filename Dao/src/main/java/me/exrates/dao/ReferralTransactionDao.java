package me.exrates.dao;

import me.exrates.model.referral.ReferralTransaction;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ReferralTransactionDao {

    boolean createReferralTransaction(ReferralTransaction referralTransaction);

    Map<String, BigDecimal> getEarnedByUsersFromAndUserToAndCurrencies(List<Integer> userFrom, int userTo, List<String> currencies);
}
