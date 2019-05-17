package me.exrates.dao;

import me.exrates.model.IEODetails;

import java.math.BigDecimal;
import java.util.Collection;

public interface IeoDetailsRepository {

    IEODetails save(IEODetails ieoDetails);

    IEODetails update(IEODetails ieoDetails);

    IEODetails updateSafe(IEODetails ieoDetails);

    IEODetails findOpenIeoByCurrencyName(String currencyName);

    IEODetails findOne(int ieoId);

    boolean updateAvailableAmount(int ieoId, BigDecimal availableAmount);

    Collection<IEODetails> findAll();

    Collection<IEODetails> findAllExceptForMaker(int userId);

    boolean isCountryRestrictedByIeoId(int idIeo, String countryCode);

    boolean updateIeoStatusesToRunning();

    boolean updateIeoStatusesToTerminated();

    Collection<IEODetails> findAllRunningAndAvailableIeo();
}
