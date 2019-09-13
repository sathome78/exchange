package me.exrates.dao;

import me.exrates.model.IEODetails;
import me.exrates.model.enums.IEODetailsStatus;

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

    boolean updateIeoSoldOutTime(int ieoId);

    boolean updateIeoDetailStatus(IEODetailsStatus status, int idIeo);

    boolean isUserAgreeWithPolicy(int userId, int ieoId);

    void setUserAgreeWithPolicy(int userId, int ieoId);

    void insertUserAgreeWithPolicy(int userId, int ieoId);

    String getIeoPolicy(int ieoId);

    void updateIeoPolicy(int ieoId, String text);

    void insertIeoPolicy(int ieoId, String text);
}
