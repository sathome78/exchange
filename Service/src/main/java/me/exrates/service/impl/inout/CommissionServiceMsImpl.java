package me.exrates.service.impl.inout;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import me.exrates.dao.CommissionDao;
import me.exrates.model.Commission;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.model.dto.CommissionDataDto;
import me.exrates.model.dto.CommissionShortEditDto;
import me.exrates.model.dto.EditMerchantCommissionDto;
import me.exrates.model.dto.NormalizeAmountDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserRole;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.service.*;
import me.exrates.service.exception.InoutMicroserviceInternalServerException;
import me.exrates.service.impl.CommissionServiceImpl;
import me.exrates.service.merchantStrategy.MerchantServiceContext;
import me.exrates.service.properties.InOutProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static me.exrates.model.enums.ActionType.MULTIPLY_PERCENT;
import static me.exrates.model.enums.OperationType.INPUT;

@Log4j2
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

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

        HttpEntity<?> entity = null;
        try {
            entity = new HttpEntity<>(mapper.writeValueAsString(normalizeAmountDto), headers);
        } catch (JsonProcessingException e) {
            log.error("error normalizeAmountAndCalculateCommission", e);
            throw new RuntimeException(e);
        }
        ResponseEntity<CommissionDataDto> response;
        try {
            response = template.exchange(
                    builder.toUriString(),
                    HttpMethod.POST,
                    entity, new ParameterizedTypeReference<CommissionDataDto>() {});
            return response.getBody();

        } catch (Exception ex){
            log.error(ex);
            throw new InoutMicroserviceInternalServerException(ex.getMessage());
        }
    }

}
