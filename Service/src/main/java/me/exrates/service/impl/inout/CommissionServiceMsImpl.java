package me.exrates.service.impl.inout;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.model.dto.CommissionDataDto;
import me.exrates.model.dto.NormalizeAmountDto;
import me.exrates.model.enums.OperationType;
import me.exrates.service.UserService;
import me.exrates.service.impl.CommissionServiceImpl;
import me.exrates.service.merchantStrategy.MerchantServiceContext;
import me.exrates.service.properties.InOutProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Conditional(MicroserviceConditional.class)
public class CommissionServiceMsImpl extends CommissionServiceImpl {
    public static final String API_COMMISSION_NORMALIZE_AMOUNT_AND_CALCULATE_COMMISSION = "/api/commission/normalizeAmountAndCalculateCommission";

    @Autowired
    UserService userService;

    @Autowired
    MerchantServiceContext merchantServiceContext;

    private final InOutProperties properties;
    private final RestTemplate template;
    private final ObjectMapper mapper;

    @Override
    @Transactional
    @SneakyThrows
    public CommissionDataDto normalizeAmountAndCalculateCommission(Integer userId,
                                                                   BigDecimal amount,
                                                                   OperationType type,
                                                                   Integer currencyId,
                                                                   Integer merchantId, String destinationTag) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + API_COMMISSION_NORMALIZE_AMOUNT_AND_CALCULATE_COMMISSION);

        NormalizeAmountDto normalizeAmountDto = NormalizeAmountDto.builder()
                .userId(userId)
                .amount(amount)
                .type(type)
                .currencyId(currencyId)
                .merchantId(merchantId)
                .destinationTag(destinationTag)
                .userRole(userService.getUserRoleFromDB(userId)).build();

        HttpEntity<?> entity = new HttpEntity<>(mapper.writeValueAsString(normalizeAmountDto));
        ResponseEntity<CommissionDataDto> response = template.exchange(
                builder.toUriString(),
                HttpMethod.POST,
                entity, new ParameterizedTypeReference<CommissionDataDto>() {});

        return response.getBody();
    }

}
