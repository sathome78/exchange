package me.exrates.service.impl.inout;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.service.impl.YandexKassaServiceImpl;
import me.exrates.service.properties.InOutProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;


@Service
@Conditional(MicroserviceConditional.class)
@RequiredArgsConstructor
public class YandexKassaServiceMsImpl extends YandexKassaServiceImpl {

    private static final String API_MERCHANT_YAKASSA_CONFIRM_PAYMENT = "/api/merchant/yakassa/confirmPayment";
    private final InOutProperties properties;
    private final RestTemplate template;
    private final ObjectMapper mapper;


    @Override
    @SneakyThrows
    public boolean confirmPayment(Map<String, String> params) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + API_MERCHANT_YAKASSA_CONFIRM_PAYMENT);

        HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(params));
        return template.exchange(
                builder.toUriString(),
                HttpMethod.POST,
                entity, Boolean.class).getBody();
    }
}
