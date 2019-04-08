package me.exrates.dao;

import me.exrates.model.IEOClaim;
import me.exrates.model.IEOResult;

import java.util.Collection;

public interface IEOClaimRepository {

    IEOClaim save(IEOClaim ieoClaim);

    Collection<IEOClaim> findUnprocessedIeoClaims();

    boolean updateStatusIEOClaim(int claimId, IEOResult.IEOResultStatus status);
}

