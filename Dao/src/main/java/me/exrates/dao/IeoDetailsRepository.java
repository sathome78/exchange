package me.exrates.dao;

import me.exrates.model.IEODetails;
import me.exrates.model.User;

import java.math.BigDecimal;
import java.util.Collection;

public interface IeoDetailsRepository {

    IEODetails save(IEODetails ieoDetails);

    IEODetails update(IEODetails ieoDetails);

    IEODetails updateSafe(IEODetails ieoDetails);

    Collection<IEODetails> findByCurrencyName(String currencyName);

    IEODetails findOpenIeoByCurrencyName(String currencyName);

    IEODetails findOne(int ieoId);

    boolean updateAvailableAmount(int ieoId, BigDecimal availableAmount);

    BigDecimal getAvailableAmount(int ieoId);

    Collection<IEODetails> findAll();

    Collection<IEODetails> findAllExceptForMaker(int userId);

    boolean isCountryRestrictedByIeoId(int idIeo, String countryCode);
}
