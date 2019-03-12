package me.exrates.service.omni;

import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.RefillRequestAddressShortDto;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.RefillRequestPutOnBchExamDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.model.dto.merchants.omni.OmniBalanceDto;
import me.exrates.model.dto.merchants.omni.OmniTxDto;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@Conditional(MicroserviceConditional.class)
public class OmniServiceMsImpl implements OmniService {
    @Override
    public void putOnBchExam(RefillRequestPutOnBchExamDto dto) {

    }

    @Override
    public RefillRequestAcceptDto createRequest(String address, String hash, BigDecimal amount) {
        return null;
    }

    @Override
    public void frozeCoins(String address, BigDecimal amount) {

    }

    @Override
    public Merchant getMerchant() {
        return null;
    }

    @Override
    public Currency getCurrency() {
        return null;
    }

    @Override
    public String getWalletPassword() {
        return null;
    }

    @Override
    public OmniBalanceDto getUsdtBalances() {
        return null;
    }

    @Override
    public BigDecimal getBtcBalance() {
        return null;
    }

    @Override
    public Integer getUsdtPropertyId() {
        return null;
    }

    @Override
    public List<OmniTxDto> getAllTransactions() {
        return null;
    }

    @Override
    public List<RefillRequestAddressShortDto> getBlockedAddressesOmni() {
        return null;
    }

    @Override
    public void createRefillRequestAdmin(Map<String, String> params) {

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
