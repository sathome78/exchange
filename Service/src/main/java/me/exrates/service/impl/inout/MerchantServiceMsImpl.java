package me.exrates.service.impl.inout;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.service.exception.CheckDestinationTagException;
import me.exrates.service.impl.MerchantServiceImpl;
import me.exrates.service.properties.InOutProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.ParameterizedTypeReference;
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
public class MerchantServiceMsImpl extends MerchantServiceImpl {
    private static final String API_CHECK_DESTINATION_TAG = "/api/merchant/checkDestinationTag";
    public static final String API_GET_WALLET_BALANCE_BY_CURRENCY_NAME = "/api/getWalletBalanceByCurrencyName";
    private static final String API_MERCHANT_IS_VALID_DESTINATION_ADDRESS = "/api/merchant/isValidDestinationAddress";

    private final ObjectMapper objectMapper;
    private final RestTemplate template;
    private final InOutProperties properties;

    @Override
    @SneakyThrows
    public void checkDestinationTag(Integer merchantId, String memo) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + API_CHECK_DESTINATION_TAG)
                .queryParam("merchant_id", merchantId)
                .queryParam("memo", memo);

        ResponseEntity<String> response = template.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                HttpEntity.EMPTY, String.class);

        if(response.getStatusCodeValue() == 400){
            throw objectMapper.readValue(response.getBody(), CheckDestinationTagException.class);
        }
    }

    @Override
    public boolean isValidDestinationAddress(Integer merchantId, String address) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + API_MERCHANT_IS_VALID_DESTINATION_ADDRESS)
                .queryParam("merchantId", merchantId)
                .queryParam("address", address);

        ResponseEntity<Boolean> response = template.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                HttpEntity.EMPTY, Boolean.class);

        return response.getBody();
    }

    @Override
    public Map<String, String> getWalletBalanceByCurrencyName(String currencyName, String token, String address) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + API_GET_WALLET_BALANCE_BY_CURRENCY_NAME)
                .queryParam("currency", currencyName)
                .queryParam("token", token)
                .queryParam("address", address);

        ResponseEntity<Map<String, String>> response = template.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                HttpEntity.EMPTY, new ParameterizedTypeReference<Map<String, String>>() {});

        return response.getBody();
    }
}
