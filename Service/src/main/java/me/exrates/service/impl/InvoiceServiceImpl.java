package me.exrates.service.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.CurrencyService;
import me.exrates.service.InvoiceService;
import me.exrates.service.MerchantService;
import me.exrates.service.exception.NotApplicableException;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.util.WithdrawUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;


@Service
@Log4j2
@Conditional(MonolitConditional.class)
public class InvoiceServiceImpl implements InvoiceService {

  @Autowired
  private MessageSource messageSource;
  @Autowired
  private MerchantService merchantService;
  @Autowired
  private CurrencyService currencyService;
  @Autowired
  private WithdrawUtils withdrawUtils;

  @Override
  @Transactional
  public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
    throw new NotApplicableException("for " + withdrawMerchantOperationDto);
  }

  @Override
  @Transactional
  public Map<String, String> refill(RefillRequestCreateDto request) {
    String toWallet = String.format("%s: %s - %s",
        request.getRefillRequestParam().getRecipientBankName(),
        request.getAddress(),
        request.getRefillRequestParam().getRecipient());
    String message = messageSource.getMessage("merchants.refill.invoice",
        new String[]{request.getAmount().toPlainString().concat(currencyService.getCurrencyName(request.getCurrencyId()))
                , toWallet}, request.getLocale());
    return new HashMap<String, String>() {{
      put("message", message);
      put("walletNumber", request.getAddress());
    }};
  }

  @Override
  public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
    throw new NotApplicableException("for " + params);
  }

  @Override
  public boolean isValidDestinationAddress(String address) {

    return withdrawUtils.isValidDestinationAddress(address);
  }

}

