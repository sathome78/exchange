package me.exrates.service.referral;

import java.math.BigDecimal;

public interface ReferralService {
    void processReferralAndCommission(int currencyId, BigDecimal totalCommission, int userId);
}
