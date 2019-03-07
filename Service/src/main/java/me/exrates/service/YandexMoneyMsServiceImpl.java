package me.exrates.service;

import com.yandex.money.api.methods.RequestPayment;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Conditional(MicroserviceConditional.class)
public class YandexMoneyMsServiceImpl implements YandexMoneyService {
    @Override
    public List<String> getAllTokens() {
        return null;
    }

    @Override
    public String getTokenByUserEmail(String userEmail) {
        return null;
    }

    @Override
    public boolean addToken(String token, String email) {
        return false;
    }

    @Override
    public boolean updateTokenByUserEmail(String newToken, String email) {
        return false;
    }

    @Override
    public boolean deleteTokenByUserEmail(String email) {
        return false;
    }

    @Override
    public String getTemporaryAuthCode(String redirectURI) {
        return null;
    }

    @Override
    public String getTemporaryAuthCode() {
        return null;
    }

    @Override
    public Optional<String> getAccessToken(String code) {
        return Optional.empty();
    }

    @Override
    public Optional<RequestPayment> requestPayment(String token, CreditsOperation creditsOperation) {
        return Optional.empty();
    }

    @Override
    public int saveInputPayment(Payment payment) {
        return 0;
    }

    @Override
    public Optional<Payment> getPaymentById(Integer id) {
        return Optional.empty();
    }

    @Override
    public void deletePayment(Integer id) {

    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        return null;
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {

    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        return null;
    }

    @Override
    public boolean isValidDestinationAddress(String address) {
        return false;
    }
}
