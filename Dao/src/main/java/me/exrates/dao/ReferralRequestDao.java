package me.exrates.dao;

import me.exrates.model.referral.ReferralRequest;
import me.exrates.model.referral.enums.ReferralProcessStatus;

import java.util.List;

public interface ReferralRequestDao {
    void saveReferralRequestsBatch(List<ReferralRequest> requests);

    List<ReferralRequest> getReferralRequestsByStatus(int chunk, ReferralProcessStatus status);

    boolean updateStatusReferralRequest(int id, ReferralProcessStatus status);
}
