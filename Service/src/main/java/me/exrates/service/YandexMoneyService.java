package me.exrates.service;

import com.yandex.money.api.methods.RequestPayment;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.service.merchantStrategy.IMerchantService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Service
public interface YandexMoneyService extends IMerchantService {

    List<String> getAllTokens();

    String getTokenByUserEmail(String userEmail);

    boolean addToken(String token, String email);

    boolean updateTokenByUserEmail(String newToken, String email);

    boolean deleteTokenByUserEmail(String email);

    String getTemporaryAuthCode(String redirectURI);

    String getTemporaryAuthCode();

    Optional<String> getAccessToken(String code);

    Optional<RequestPayment> requestPayment(String token, CreditsOperation creditsOperation);

    int saveInputPayment(Payment payment);

    Optional<Payment> getPaymentById(Integer id);

    void deletePayment(Integer id);
}