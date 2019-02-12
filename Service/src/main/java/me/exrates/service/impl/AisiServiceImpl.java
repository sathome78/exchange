package me.exrates.service.impl;

import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.AisiCurrencyService;
import me.exrates.service.AisiService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import java.util.HashMap;
import java.util.Map;

public class AisiServiceImpl implements AisiService {

    @Autowired
    private final AisiCurrencyService aisiCurrencyService;

    @Autowired
    private MessageSource messageSource;

    public AisiServiceImpl(AisiCurrencyService aisiCurrencyService) {
        this.aisiCurrencyService = aisiCurrencyService;
    }


    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        String address = aisiCurrencyService.generateNewAddress();
        String message = messageSource.getMessage("merchants.refill.aisi",
                new Object[] {address}, request.getLocale());
        return new HashMap<String, String>(){{
            put("address", address);
            put("message", message);
            put("qr", address);
        }};
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
