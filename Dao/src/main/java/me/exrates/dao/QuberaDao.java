package me.exrates.dao;

import me.exrates.model.QuberaUserData;
import me.exrates.model.dto.qubera.QuberaLog;

import java.util.Map;

public interface QuberaDao {

    Integer findUserIdByAccountNumber(String accountNumber);

    boolean logResponse(QuberaLog requestDto);

    boolean createExternalPaymentLog(QuberaLog requestDto);

    boolean updateExternalPaymentLog(int paymentId, QuberaLog.ExternalPaymentState state);

    boolean saveUserDetails(QuberaUserData userData);

    Map<String, String> getUserDetailsForCurrency(int userId, int currencyId);

    boolean existAccountByUserEmailAndCurrencyName(String email, String currency);

    String getAccountByUserEmail(String email);

    QuberaUserData getUserDataByUserIdAndCurrencyId(int userId, int currencyId);

    QuberaUserData getUserDataByUserEmail(String email);

    boolean updateUserData(QuberaUserData quberaUserData);

    QuberaUserData getUserDataByReference(String reference);
}
