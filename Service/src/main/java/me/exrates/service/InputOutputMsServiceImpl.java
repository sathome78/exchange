package me.exrates.service;

import lombok.RequiredArgsConstructor;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.model.dto.CurrencyInputOutputSummaryDto;
import me.exrates.model.dto.InOutReportDto;
import me.exrates.model.dto.onlineTableDto.MyInputOutputHistoryDto;
import me.exrates.model.enums.invoice.InvoiceOperationPermission;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.vo.CacheData;
import me.exrates.model.vo.PaginationWrapper;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
@Conditional(MicroserviceConditional.class)
@RequiredArgsConstructor
public class InputOutputMsServiceImpl implements InputOutputService {

    private final RestTemplate restTemplate;

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
    public Optional<CreditsOperation> prepareCreditsOperation(Payment payment, String userEmail, Locale locale) {
        //restTemplate...
        return Optional.empty();
    }

    @Override
    public List<CurrencyInputOutputSummaryDto> getInputOutputSummary(LocalDateTime startTime, LocalDateTime endTime, List<Integer> userRoleIdList) {
        return null;
    }

    @Override
    public List<InOutReportDto> getInputOutputSummaryWithCommissions(LocalDateTime startTime, LocalDateTime endTime, List<Integer> userRoleIdList) {
        return null;
    }
}
