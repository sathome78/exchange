package me.exrates.service.impl.inout;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yandex.money.api.methods.RequestPayment;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.exrates.model.CreditsOperation;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.service.impl.YandexMoneyServiceImpl;
import me.exrates.service.properties.InOutProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

@Service
@Conditional(MicroserviceConditional.class)
@RequiredArgsConstructor
public class YandexMoneyServiceMsImpl extends YandexMoneyServiceImpl {

    private static final String API_MERCHANT_GET_TEMPORARY_AUTH_CODE = "/api/merchant/yamoney/getTemporaryAuthCode";
    private static final String API_MERCHANT_GET_ACCESS_TOKEN = "/api/merchant/yamoney/getAccessToken";
    private static final String API_MERCHANT_REQUEST_PAYMENT = "/api/merchant/yamoney/requestPayment";
    private final InOutProperties properties;
    private final RestTemplate template;
    private final ObjectMapper mapper;

    @Override
    public String getTemporaryAuthCode() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + API_MERCHANT_GET_TEMPORARY_AUTH_CODE);

        return template.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                HttpEntity.EMPTY, String.class).getBody();
    }

    @Override
    public Optional<String> getAccessToken(String code) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + API_MERCHANT_GET_ACCESS_TOKEN)
                .queryParam("code", code);


        return Optional.ofNullable(template.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                HttpEntity.EMPTY, String.class).getBody());
    }

    @Override
    @SneakyThrows
    public Optional<RequestPayment> requestPayment(String token, CreditsOperation creditsOperation) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + API_MERCHANT_REQUEST_PAYMENT)
                .queryParam("token", token);

        return template.exchange(
                builder.toUriString(),
                HttpMethod.POST,
                new HttpEntity<>(mapper.writeValueAsString(creditsOperation)), new ParameterizedTypeReference<Optional<RequestPayment>>(){}).getBody();
    }
}
