package me.exrates.service.impl.inout;

import me.exrates.model.MerchantCurrency;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.model.dto.TransferDto;
import me.exrates.model.dto.TransferRequestCreateDto;
import me.exrates.model.dto.TransferRequestFlatDto;
import me.exrates.model.dto.VoucherAdminTableDto;
import me.exrates.model.dto.dataTable.DataTable;
import me.exrates.model.dto.dataTable.DataTableParams;
import me.exrates.model.dto.filterData.VoucherFilterData;
import me.exrates.model.enums.invoice.InvoiceActionTypeEnum;
import me.exrates.service.TransferService;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
@Conditional(MicroserviceConditional.class)
public class TransferServiceMsImpl implements TransferService {
    @Override
    public Map<String, Object> createTransferRequest(TransferRequestCreateDto request) {
        return null;
    }

    @Override
    public List<MerchantCurrency> retrieveAdditionalParamsForWithdrawForMerchantCurrencies(List<MerchantCurrency> merchantCurrencies) {
        return null;
    }

    @Override
    public void revokeByUser(int requestId, Principal principal) {

    }

    @Override
    public void revokeByAdmin(int requestId, Principal principal) {

    }

    @Override
    public List<TransferRequestFlatDto> getRequestsByMerchantIdAndStatus(int merchantId, List<Integer> statuses) {
        return null;
    }

    @Override
    public TransferRequestFlatDto getFlatById(Integer id) {
        return null;
    }

    @Override
    public Map<String, String> correctAmountAndCalculateCommissionPreliminarily(Integer userId, BigDecimal amount, Integer currencyId, Integer merchantId, Locale locale) {
        return null;
    }

    @Override
    public Optional<TransferRequestFlatDto> getByHashAndStatus(String code, Integer requiredStatus, boolean block) {
        return Optional.empty();
    }

    @Override
    public boolean checkRequest(TransferRequestFlatDto transferRequestFlatDto, String userEmail) {
        return false;
    }

    @Override
    public TransferDto performTransfer(TransferRequestFlatDto transferRequestFlatDto, Locale locale, InvoiceActionTypeEnum action) {
        return null;
    }

    @Override
    public String getUserEmailByTrnasferId(int id) {
        return null;
    }

    @Override
    public DataTable<List<VoucherAdminTableDto>> getAdminVouchersList(DataTableParams dataTableParams, VoucherFilterData withdrawFilterData, String authorizedUserEmail, Locale locale) {
        return null;
    }

    @Override
    public String getHash(Integer id, Principal principal) {
        return null;
    }

    @Override
    public void revokeTransferRequest(Integer requestId) {

    }
}
