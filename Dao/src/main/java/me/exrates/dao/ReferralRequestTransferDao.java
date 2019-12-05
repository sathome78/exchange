package me.exrates.dao;

import me.exrates.model.referral.ReferralRequestTransfer;
import me.exrates.model.referral.enums.ReferralRequestStatus;

import java.util.List;

public interface ReferralRequestTransferDao {
    ReferralRequestTransfer createReferralRequestTransfer(ReferralRequestTransfer referralRequestTransfer);

    boolean updateReferralRequestTransfer(ReferralRequestTransfer referralRequestTransfer);

    List<ReferralRequestTransfer> findByStatus(List<ReferralRequestStatus> status);
}
