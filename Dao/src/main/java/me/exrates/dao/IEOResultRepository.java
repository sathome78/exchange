package me.exrates.dao;

import me.exrates.model.IEOClaim;
import me.exrates.model.IEOResult;

public interface IEOResultRepository {

    IEOResult save(IEOResult ieoResult);

    boolean isAlreadyStarted(IEOClaim ieoClaim);
}
