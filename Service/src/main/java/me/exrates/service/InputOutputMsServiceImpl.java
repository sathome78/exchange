package me.exrates.service;

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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
@Conditional(MicroserviceConditional.class)
public class InputOutputMsServiceImpl implements InputOutputService {
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
