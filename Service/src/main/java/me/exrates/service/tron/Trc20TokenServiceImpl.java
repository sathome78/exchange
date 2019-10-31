package me.exrates.service.tron;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.TronNewAddressDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.CurrencyService;
import me.exrates.service.GtagService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.util.WithdrawUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Log4j2 // TODO
public class Trc20TokenServiceImpl implements Trc20TokenService {

    private String merchantName;
    private String currencyName;

    private Set<String> addressesHEX = Collections.synchronizedSet(new HashSet<>());

    @Autowired
    private MerchantService merchantService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private TronNodeService tronNodeService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private RefillService refillService;
    @Autowired
    private GtagService gtagService;
    @Autowired
    private WithdrawUtils withdrawUtils;

    @Override
    public Set<String> getAddressesHEX() {
        return addressesHEX;
    }

    public Trc20TokenServiceImpl(String merchantName, String currencyName){
        this.merchantName = merchantName;
        this.currencyName = currencyName;
    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        TronNewAddressDto dto = tronNodeService.getNewAddress();
        String message = messageSource.getMessage("merchants.refill.btc",
                new Object[]{dto.getAddress()}, request.getLocale());
        addressesHEX.add(dto.getHexAddress());
        return new HashMap<String, String>() {{
            put("address", dto.getAddress());
            put("privKey", dto.getPrivateKey());
            put("pubKey", dto.getHexAddress());
            put("message", message);
            put("qr", dto.getAddress());
        }};
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        String address = params.get("address");
        String hash = params.get("hash");
        Integer id = Integer.parseInt(params.get("id"));
        Integer merchantId = Integer.valueOf(params.get("merchant"));
        Integer currencyId = Integer.valueOf(params.get("currency"));
        Currency currency = currencyService.findById(currencyId);
        Merchant merchant = merchantService.findById(merchantId);
        BigDecimal amount = new BigDecimal(params.get("amount"));
        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .requestId(id)
                .address(address)
                .merchantId(merchant.getId())
                .currencyId(currency.getId())
                .amount(amount)
                .merchantTransactionId(hash)
                .toMainAccountTransferringConfirmNeeded(this.toMainAccountTransferringConfirmNeeded())
                .build();
        refillService.autoAcceptRefillRequest(requestAcceptDto);
        final String gaTag = refillService.getUserGAByRequestId(id);
        log.debug("Process of sending data to Google Analytics...");
        gtagService.sendGtagEvents(amount.toString(), currency.getName(), gaTag);
    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        throw new RuntimeException("not supported");
    }

    @Override
    public boolean isValidDestinationAddress(String address) {
        return withdrawUtils.isValidDestinationAddress(address);
    }

    @Override
    public String getMerchantName() {
        return merchantName;
    }

    public String getCurrencyName() {
        return currencyName;
    }
}
