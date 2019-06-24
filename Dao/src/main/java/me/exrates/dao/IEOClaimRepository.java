package me.exrates.dao;

import me.exrates.model.IEOClaim;
import me.exrates.model.IEOResult;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public interface IEOClaimRepository {

    IEOClaim save(IEOClaim ieoClaim);

    boolean updateStatusIEOClaim(int claimId, IEOResult.IEOResultStatus status);

    Collection<Integer> getAllSuccessClaimIdsByIeoId(int claimId);

    List<IEOClaim> getClaimsByIds(List<Integer> ids);

    boolean updateClaim(IEOClaim ieoClaim);

    List<IEOClaim> findUnprocessedIeoClaimsByIeoId(Integer ieoId, int chunk, boolean testIeo);
}

