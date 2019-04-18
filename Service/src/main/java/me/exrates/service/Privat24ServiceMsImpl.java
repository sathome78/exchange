package me.exrates.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.exrates.model.CreditsOperation;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.properties.InOutProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
@Conditional(MicroserviceConditional.class)
@RequiredArgsConstructor
public class Privat24ServiceMsImpl implements Privat24Service {
    private static final String API_MERCHANT_PRIVAT_24_CONFIRM_PAYMENT = "/api/merchant/privat24/confirmPayment";
    private final InOutProperties properties;
    private final RestTemplate template;
    private final ObjectMapper mapper;

    @Override
    public Map<String, String> preparePayment(CreditsOperation creditsOperation, String email) {
        return null;
    }

    @Override
    @SneakyThrows
    public boolean confirmPayment(Map<String, String> params, String signature, String payment) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + API_MERCHANT_PRIVAT_24_CONFIRM_PAYMENT)
                .queryParam("signature", signature)
                .queryParam("payment", payment);

        HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(params));
        ResponseEntity<Boolean> response = template.exchange(
                builder.toUriString(),
                HttpMethod.POST,
                entity, Boolean.class);
        return response.getBody();
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
