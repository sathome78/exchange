package me.exrates.service.impl.inout;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.exrates.dao.exception.notfound.UserNotFoundException;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.User;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.model.dto.CurrencyInputOutputSummaryDto;
import me.exrates.model.dto.InOutReportDto;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.enums.invoice.InvoiceOperationPermission;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.vo.CacheData;
import me.exrates.model.vo.PaginationWrapper;
import me.exrates.service.InputOutputService;
import me.exrates.service.UserService;
import me.exrates.service.properties.InOutProperties;
import me.exrates.service.util.RequestUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
@Conditional(MicroserviceConditional.class)
@RequiredArgsConstructor
public class InputOutputServiceMsImpl implements InputOutputService {

    private static final String API_PREPARE_CREDITS_OPERATION = "/api/prepareCreditsOperation";
    private final RestTemplate template;
    private final RequestUtil requestUtil;
    private final InOutProperties properties;
    private final ObjectMapper objectMapper;
    private final UserService userService;
    private final MessageSource messageSource;

    @Override
    public List<MyInputOutputHistoryDto> getMyInputOutputHistory(CacheData cacheData, String email, Integer offset, Integer limit, Locale locale) {
        return null;
    }

    @Override
    public List<MyInputOutputHistoryDto> getMyInputOutputHistory(String email, Integer offset, Integer limit, Locale locale) {
        return null;
    }

    @Override
    public PaginationWrapper<List<MyInputOutputHistoryDto>> findUnconfirmedInvoices(String userEmail, String currencyName, Integer limit, Integer offset, Locale locale) {
        return null;
    }

    @Override
    public List<Map<String, Object>> generateAndGetButtonsSet(InvoiceStatus status, InvoiceOperationPermission permittedOperation, boolean authorisedUserIsHolder, Locale locale) {
        return null;
    }

    @Override
    @SneakyThrows
    public Optional<CreditsOperation> prepareCreditsOperation(Payment payment, String userEmail, Locale locale) {
        setUserRecipient(locale, payment);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + API_PREPARE_CREDITS_OPERATION);
        HttpEntity<?> entity = new HttpEntity<>(objectMapper.writeValueAsString(payment), requestUtil.prepareHeaders(userEmail));
        ResponseEntity<Optional<CreditsOperation>> response = template.exchange(
                builder.toUriString(),
                HttpMethod.POST,
                entity, new ParameterizedTypeReference<Optional<CreditsOperation>>() {
                });

        return response.getBody();
    }


    private void setUserRecipient(Locale locale, Payment payment) {
        User userRecipient;
        try {
            if (!StringUtils.isEmpty(payment.getRecipient())) {
                userRecipient = userService.getIdByNickname(payment.getRecipient()) > 0 ?
                        userService.findByNickname(payment.getRecipient()) : userService.findByEmail(payment.getRecipient());
                payment.setUserRecipient(userRecipient);
            }
        } catch (RuntimeException e) {
            throw new UserNotFoundException(messageSource.getMessage("transfer.nonExistentUser", new Object[]{payment.getRecipient()}, locale));
        }
    }

    @Override
    public List<CurrencyInputOutputSummaryDto> getInputOutputSummary(LocalDateTime startTime, LocalDateTime endTime, List<Integer> userRoleIdList) {
        return null;
    }

    @Override
    public List<InOutReportDto> getInputOutputSummaryWithCommissions(LocalDateTime startTime, LocalDateTime endTime, List<Integer> userRoleIdList) {
        return null;
    }

    @Override
    public Integer getUserInputOutputHistoryCount(String userEmail, Integer currencyId, String currencyName, LocalDateTime dateTimeFrom,
                                                  LocalDateTime dateTimeTo, Integer limit, Integer offset, Locale locale) {
        return null;
    }

    @Override
    public List<MyInputOutputHistoryDto> getUserInputOutputHistory(String userEmail, Integer currencyId, String currencyName, LocalDateTime dateTimeFrom,
                                                                   LocalDateTime dateTimeTo, Integer limit, Integer offset, Locale locale) {
        return null;
    }


}
