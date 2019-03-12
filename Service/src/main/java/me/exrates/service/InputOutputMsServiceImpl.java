package me.exrates.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.exrates.dao.UserDao;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.model.dto.CurrencyInputOutputSummaryDto;
import me.exrates.model.dto.InOutReportDto;
import me.exrates.model.dto.TransactionFilterDataDto;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.enums.invoice.InvoiceOperationPermission;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.vo.CacheData;
import me.exrates.model.vo.PaginationWrapper;
import me.exrates.service.properties.InOutProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
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
public class InputOutputMsServiceImpl implements InputOutputService {

    private static final String API_PREPARE_CREDITS_OPERATION = "/api/prepareCreditsOperation";
    private final RestTemplate template;
    private final InOutProperties properties;
    private final UserDao userDao;
    private final ObjectMapper objectMapper;

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
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(properties.getUrl() + API_PREPARE_CREDITS_OPERATION + appendUserId(userEmail));
        HttpEntity<?> entity = new HttpEntity<>(objectMapper.writeValueAsString(payment), prepareHeaders());
        ResponseEntity<Optional<CreditsOperation>> response = template.exchange(
                builder.toUriString(),
                HttpMethod.POST,
                entity, new ParameterizedTypeReference<Optional<CreditsOperation>>() {});

        return response.getBody();
    }

    private String appendUserIdAndLocale(String userEmail, Locale locale) {
        return appendUserId(userEmail) + "&locale="+locale;
    }

    private String appendUserId(String userEmail) {
        return appendUserId(userDao.getIdByEmail(userEmail));
    }

    private String appendUserId(int userId) {
        return "?user_id=" + userId;
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
    public Integer getUserInputOutputHistoryCount(TransactionFilterDataDto filter, Locale locale) {
        return null;
    }

    @Override
    public List<MyInputOutputHistoryDto> getUserInputOutputHistory(TransactionFilterDataDto filter, Locale locale) {
        return null;
    }

    private HttpHeaders prepareHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.add(properties.getTokenName(), properties.getTokenValue());
        return headers;
    }
}
