package me.exrates.service.impl;

import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.IcoService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class IcoServiceImpl implements IcoService {

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        throw new RuntimeException("not implemented");
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        throw new RuntimeException("not implemented");
    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        throw new RuntimeException("not implemented");
    }


}
