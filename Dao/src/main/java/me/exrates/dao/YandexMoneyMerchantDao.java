package me.exrates.dao;


import me.exrates.model.Payment;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
public interface    YandexMoneyMerchantDao {
    List<String> getAllTokens();
    String getTokenByUserEmail(String email);
    boolean createToken(String token, int userId);
    boolean deleteTokenByUserEmail(String userEmail);
    boolean updateTokenByUserEmail(String userEmail, String newToken);

    int savePayment(Integer currencyId, BigDecimal amount);

    Optional<Payment> getPaymentById(Integer id);

    void deletePayment(Integer id);
}
