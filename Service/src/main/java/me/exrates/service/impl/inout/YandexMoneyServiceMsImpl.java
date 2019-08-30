package me.exrates.service.impl.inout;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yandex.money.api.methods.RequestPayment;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.YandexMoneyService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.properties.InOutProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Log4j2
@Service
@Conditional(MicroserviceConditional.class)
@RequiredArgsConstructor
public class YandexMoneyServiceMsImpl implements YandexMoneyService {

    private static final String API_MERCHANT_GET_TEMPORARY_AUTH_CODE = "/api/merchant/yamoney/getTemporaryAuthCode";
    private static final String API_MERCHANT_GET_ACCESS_TOKEN = "/api/merchant/yamoney/getAccessToken";
    private static final String API_MERCHANT_REQUEST_PAYMENT = "/api/merchant/yamoney/requestPayment";
    private final InOutProperties properties;
    private final ObjectMapper mapper;

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
        RestTemplate restTemplate = new RestTemplate();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + API_MERCHANT_GET_TEMPORARY_AUTH_CODE);

        return restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                HttpEntity.EMPTY, String.class).getBody();
    }

    @Override
    public Optional<String> getAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + API_MERCHANT_GET_ACCESS_TOKEN)
                .queryParam("code", code);

        return Optional.ofNullable(restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                HttpEntity.EMPTY, String.class).getBody());
    }

    @Override
    public Optional<RequestPayment> requestPayment(String token, CreditsOperation creditsOperation) {
        RestTemplate restTemplate = new RestTemplate();
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + API_MERCHANT_REQUEST_PAYMENT)
                .queryParam("token", token);

        try {
            return restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.POST,
                    new HttpEntity<>(mapper.writeValueAsString(creditsOperation)), new ParameterizedTypeReference<Optional<RequestPayment>>(){}).getBody();
        } catch (JsonProcessingException e) {
            log.error("error requestPayment", e);
            throw new RuntimeException(e);
        }
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
