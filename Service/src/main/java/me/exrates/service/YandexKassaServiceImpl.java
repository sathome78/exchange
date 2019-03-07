package me.exrates.service;

import me.exrates.model.CreditsOperation;
import me.exrates.model.condition.MicroserviceConditional;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
@Conditional(MicroserviceConditional.class)
public class YandexKassaServiceImpl implements YandexKassaService {
    @Override
    public Map<String, String> preparePayment(CreditsOperation creditsOperation, String email) {
        return null;
    }

    @Override
    public boolean confirmPayment(Map<String, String> params) {
        return false;
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
