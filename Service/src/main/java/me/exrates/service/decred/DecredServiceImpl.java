package me.exrates.service.decred;

import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.condition.MonolitConditional;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.CurrencyService;
import me.exrates.service.GtagService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.decred.rpc.Api;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.util.WithdrawUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Log4j2(topic = "decred")
@Service
@Conditional(MonolitConditional.class)
public class DecredServiceImpl implements DecredService {

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private DecredGrpcService decredGrpcService;
    @Autowired
    private RefillService refillService;
    @Autowired
    private WithdrawUtils withdrawUtils;
    @Autowired
    private GtagService gtagService;

    private Merchant merchant;
    private Currency currency;

    private static final String MERCHANT_name = "DCR";

    private Set<String> addresses = Collections.synchronizedSet(new HashSet<>());

    @PostConstruct
    public void init() {
        currency = currencyService.findByName(MERCHANT_name);
        merchant = merchantService.findByName(MERCHANT_name);
        addresses.addAll(refillService.findAllAddresses(merchant.getId(), currency.getId()));
    }

    @Override
    public Set<String> getAddresses() {
        return addresses;
    }

    private void addAddress(String address) {
        addresses.add(address);
    }

    @Override
    public Merchant getMerchant() {
        return merchant;
    }

    @Override
    public Currency getCurrency() {
        return currency;
    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        Api.NextAddressResponse response = decredGrpcService.getNewAddress();
        String message = messageSource.getMessage("merchants.refill.btc",
                new Object[]{response.getAddress()}, request.getLocale());
        addAddress(response.getAddress());
        return new HashMap<String, String>() {
            {
                put("address", response.getAddress());
                put("pubKey", response.getPublicKey());
                put("message", message);
                put("qr", response.getAddress());
            }
        };
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        String address = params.get("address");
        String hash = params.get("hash");
        BigDecimal amount = new BigDecimal(params.get("amount")).setScale(8, RoundingMode.HALF_UP);
        if (checkTransactionForDuplicate(hash)) {
            log.warn("decred tx duplicated {}", hash);
            return;
        }
        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .address(address)
                .merchantId(merchant.getId())
                .currencyId(currency.getId())
                .amount(amount)
                .merchantTransactionId(hash)
                .toMainAccountTransferringConfirmNeeded(this.toMainAccountTransferringConfirmNeeded())
                .build();

        Integer requestId;
        try {
            requestId = refillService.getRequestId(requestAcceptDto);
            requestAcceptDto.setRequestId(requestId);

            refillService.autoAcceptRefillRequest(requestAcceptDto);
        } catch (RefillRequestAppropriateNotFoundException e) {
            log.debug("RefillRequestAppropriateNotFoundException: " + params);
            requestId = refillService.createRefillRequestByFact(requestAcceptDto);
            requestAcceptDto.setRequestId(requestId);

            refillService.autoAcceptRefillRequest(requestAcceptDto);
        }
        final String gaTag = refillService.getUserGAByRequestId(requestId);

        log.debug("Process of sending data to Google Analytics...");
        gtagService.sendGtagEvents(amount.toString(), currency.getName(), gaTag);
    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        throw new RuntimeException("Not implemented ");
    }

    @Override
    public boolean isValidDestinationAddress(String address) {

        return withdrawUtils.isValidDestinationAddress(address);
    }

    @Synchronized
    private boolean checkTransactionForDuplicate(String hash) {
        return StringUtils.isEmpty(hash) || refillService.getRequestIdByMerchantIdAndCurrencyIdAndHash(merchant.getId(), currency.getId(),
                hash).isPresent();
    }


}
