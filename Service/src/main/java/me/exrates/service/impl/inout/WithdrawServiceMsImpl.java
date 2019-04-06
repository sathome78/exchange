package me.exrates.service.impl.inout;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.model.dto.WithdrawRequestCreateDto;
import me.exrates.model.dto.WithdrawableDataDto;
import me.exrates.service.UserService;
import me.exrates.service.impl.WithdrawServiceImpl;
import me.exrates.service.properties.InOutProperties;
import me.exrates.service.util.RequestUtil;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
@Conditional(MicroserviceConditional.class)
public class WithdrawServiceMsImpl extends WithdrawServiceImpl {

    private static final String API_WITHDRAW_REQUEST_CREATE = "/api/withdraw/request/create";

    public static final String API_WITHDRAW_CHECK_OUTPUT_REQUESTS_LIMIT = "/api/withdraw/checkOutputRequestsLimit";
    public static final String API_WITHDRAW_GET_ADDITIONAL_SERVICE_DATA = "/api/withdraw/getAdditionalServiceData/";
    private static final String API_WITHDRAW_RETRIEVE_ADDRESS_AND_ADDITIONAL_PARAMS_FOR_WITHDRAW_FOR_MERCHANT_CURRENCIES = "/api/withdraw/retrieveAddressAndAdditionalParamsForWithdrawForMerchantCurrencies";
    private final ObjectMapper objectMapper;
    private final RestTemplate template;
    private final InOutProperties properties;
    private final RequestUtil requestUtil;
    private final UserService userService;

    public WithdrawServiceMsImpl(ObjectMapper objectMapper, RestTemplate template, InOutProperties properties, RequestUtil requestUtil, UserService userService) {
        this.objectMapper = objectMapper;
        this.template = template;
        this.properties = properties;
        this.requestUtil = requestUtil;
        this.userService = userService;
    }

    @Override
    @SneakyThrows
    public Map<String, String> createWithdrawalRequest(WithdrawRequestCreateDto requestCreateDto, Locale locale) {
        requestCreateDto.setUserEmail(userService.getEmailById(requestCreateDto.getUserId()));
                UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + API_WITHDRAW_REQUEST_CREATE);
        HttpEntity<?> entity = new HttpEntity<>(objectMapper.writeValueAsString(requestCreateDto), requestUtil.prepareHeaders(locale));
        ResponseEntity<Map<String, String>> response = template.exchange(
                builder.toUriString(),
                HttpMethod.POST,
                entity, new ParameterizedTypeReference<Map<String, String>>() {});

        return response.getBody();
    }

    @Override
    public boolean checkOutputRequestsLimit(int merchantId, String email) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + API_WITHDRAW_CHECK_OUTPUT_REQUESTS_LIMIT)
                .queryParam("merchant_id", merchantId);
        HttpEntity<?> entity = new HttpEntity<>(requestUtil.prepareHeaders(email));
        ResponseEntity<Boolean> response = template.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                entity, Boolean.class);

        return response.getBody();
    }

    @Override
    public void setAdditionalData(MerchantCurrency merchantCurrency) {
        WithdrawableDataDto dto = template.getForObject(properties.getUrl() + API_WITHDRAW_GET_ADDITIONAL_SERVICE_DATA + merchantCurrency.getMerchantId(), WithdrawableDataDto.class);
        if (dto.getAdditionalTagForWithdrawAddressIsUsed()) {
            merchantCurrency.setAdditionalTagForWithdrawAddressIsUsed(true);
            merchantCurrency.setAdditionalFieldName(dto.getAdditionalWithdrawFieldName());
        } else {
            merchantCurrency.setAdditionalTagForWithdrawAddressIsUsed(false);
        }
    }

    @Override
    @SneakyThrows
    public List<MerchantCurrency> retrieveAddressAndAdditionalParamsForWithdrawForMerchantCurrencies(List<MerchantCurrency> merchantCurrencies) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + API_WITHDRAW_RETRIEVE_ADDRESS_AND_ADDITIONAL_PARAMS_FOR_WITHDRAW_FOR_MERCHANT_CURRENCIES);

        HttpEntity<?> entity = new HttpEntity<>(objectMapper.writeValueAsString(merchantCurrencies));
        ResponseEntity<List<MerchantCurrency>> response = template.exchange(
                builder.toUriString(),
                HttpMethod.POST,
                entity, new ParameterizedTypeReference<List<MerchantCurrency>>() {});

        return response.getBody();
    }
}
