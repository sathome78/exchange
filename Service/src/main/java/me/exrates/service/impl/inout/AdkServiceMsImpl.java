package me.exrates.service.impl.inout;

import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.model.dto.*;
import me.exrates.model.dto.merchants.btc.BtcPaymentResultDetailedDto;
import me.exrates.model.dto.merchants.btc.BtcWalletPaymentItemDto;
import me.exrates.service.MerchantService;
import me.exrates.service.aidos.AdkService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@Conditional(MicroserviceConditional.class)
public class AdkServiceMsImpl implements AdkService {
    @Override
    public Merchant getMerchant() {
        return null;
    }

    @Override
    public Currency getCurrency() {
        return null;
    }

    @Override
    public MerchantService getMerchantService() {
        return null;
    }

    @Override
    public RefillRequestAcceptDto createRequest(String address, String hash, BigDecimal amount) {
        return null;
    }

    @Override
    public void putOnBchExam(RefillRequestAcceptDto requestAcceptDto) {

    }

    @Override
    public String getBalance() {
        return null;
    }

    @Override
    public BtcWalletInfoDto getWalletInfo() {
        return null;
    }

    @Override
    public List<BtcTransactionHistoryDto> listAllTransactions() {
        return null;
    }

    @Override
    public void submitWalletPassword(String password) {

    }

    @Override
    public List<BtcPaymentResultDetailedDto> sendToMany(List<BtcWalletPaymentItemDto> payments) {
        return null;
    }

    @Override
    public String getNewAddressForAdmin() {
        return null;
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
