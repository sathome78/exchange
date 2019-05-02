package me.exrates.service.tron;

import lombok.RequiredArgsConstructor;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.TronReceivedTransactionDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
@Conditional(MicroserviceConditional.class)
@RequiredArgsConstructor
//Class for method countSpecCommission

public class TronServiceMsImpl implements TronService {

    @Override
    public Set<String> getAddressesHEX() {
        return null;
    }

    @Override
    public RefillRequestAcceptDto createRequest(TronReceivedTransactionDto dto) {
        return null;
    }

    @Override
    public void createAndPutOnBchExam(TronReceivedTransactionDto tronDto) {

    }

    @Override
    public int getMerchantId() {
        return 0;
    }

    @Override
    public int getCurrencyId() {
        return 0;
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
