package me.exrates.service;

import com.google.common.collect.Maps;
import me.exrates.dao.QuberaDao;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.QuberaRequestDto;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.model.enums.invoice.RefillStatusEnum;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.exception.RefillRequestIdNeededException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class QuberaServiceImpl implements QuberaService {

    private static final Logger logger = org.apache.log4j.LogManager.getLogger(QuberaServiceImpl.class);

    private final CurrencyService currencyService;
    private final GtagService gtagService;
    private final MerchantService merchantService;
    private final RefillService refillService;
    private final QuberaDao quberaDao;

    @Autowired
    public QuberaServiceImpl(CurrencyService currencyService,
                             GtagService gtagService,
                             MerchantService merchantService,
                             RefillService refillService,
                             QuberaDao quberaDao) {
        this.currencyService = currencyService;
        this.gtagService = gtagService;
        this.merchantService = merchantService;
        this.refillService = refillService;
        this.quberaDao = quberaDao;
    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        Integer requestId = request.getId();
        if (requestId == null) {
            throw new RefillRequestIdNeededException(request.toString());
        }
        Map<String, String> details = quberaDao.getUserDetailsForCurrency(request.getUserId(), request.getCurrencyId());
        Map<String, String> refillParams = Maps.newHashMap();
        String iban = details.getOrDefault("iban", "");
        String accountNumber = details.getOrDefault("accountNumber", "");
        refillParams.put("iban", iban);
        refillParams.put("currency", request.getCurrencyName());
        refillParams.put("accountNumber", accountNumber);
        return refillParams;
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        Currency currency = currencyService.findByName(params.get("currency"));
        Merchant merchant = merchantService.findByName("Qubera");
        int userId = quberaDao.findUserIdByAccountNumber(params.get("accountNumber"));

        String paymentAmount = params.getOrDefault("paymentAmount", "0");
        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .requestId(0)
                .merchantId(merchant.getId())
                .currencyId(currency.getId())
                .amount(new BigDecimal(paymentAmount))
                .merchantTransactionId(params.get("paymentId"))
                .toMainAccountTransferringConfirmNeeded(this.toMainAccountTransferringConfirmNeeded())
                .build();
        Integer requestId = refillService.createRefillRequestByFact(requestAcceptDto, userId, 0, RefillStatusEnum.ACCEPTED_AUTO);
        requestAcceptDto.setRequestId(requestId);

        refillService.autoAcceptRefillRequest(requestAcceptDto);
        // todo send notification to transfer to master account

        final String username = refillService.getUsernameByRequestId(requestId);

        logger.debug("Process of sending data to Google Analytics...");
        gtagService.sendGtagEvents(paymentAmount, currency.getName(), username);
    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        return null;
    }

    @Override
    public boolean isValidDestinationAddress(String address) {
        return false;
    }

    @Override
    public boolean logResponse(QuberaRequestDto requestDto) {
        return quberaDao.logResponse(requestDto);
    }
}
