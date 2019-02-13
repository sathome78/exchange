package me.exrates.service.impl;

import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.*;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class AisiServiceImpl implements AisiService {

    private final static String MERCHANT_NAME = "AISI";
    private final static String CURRENCY_NAME = "AISI";

    @Autowired
    private MerchantService merchantService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private RefillService refillService;

    @Autowired
    private final AisiCurrencyService aisiCurrencyService;

    @Autowired
    private MessageSource messageSource;

    private Merchant merchant;
    private Currency currency;

    @PostConstruct
    public void init() {
        currency = currencyService.findByName(CURRENCY_NAME);
        merchant = merchantService.findByName(MERCHANT_NAME);
    }

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
            put("message", message + " " + address);
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
