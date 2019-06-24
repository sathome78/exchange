package me.exrates.service.eos;

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
import me.exrates.service.util.CryptoUtils;
import me.exrates.service.util.WithdrawUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Log4j2(topic = "eos_log")
@Service
@Conditional(MonolitConditional.class)
@PropertySource("classpath:/merchants/eos.properties")
public class EosServiceImpl implements EosService {

    private static final String CURRENCY_NAME = "EOS";
    private Merchant merchant;
    private Currency currency;

    @Value("${eos.main.address}")
    private String mainAddress;

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private RefillService refillService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private GtagService gtagService;
    @Autowired
    private WithdrawUtils withdrawUtils;


    @PostConstruct
    public void init() {
        currency = currencyService.findByName(CURRENCY_NAME);
        merchant = merchantService.findByName(CURRENCY_NAME);
    }

    @Override
    public String getMainAddress(){
        return mainAddress;
    }

    @Transactional
    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        String destinationTag = CryptoUtils.generateDestinationTag(request.getUserId(),
                9 + request.getUserId().toString().length(), currency.getName());
        String message = messageSource.getMessage("merchants.refill.xlm",
                new Object[]{mainAddress, destinationTag}, request.getLocale());
        return new HashMap<String, String>() {{
            put("address", destinationTag);
            put("message", message);
            put("qr", mainAddress);
        }};
    }


    @Synchronized
    @Override
    public void processPayment(Map<String, String> params) {
        String address = params.get("address");
        String hash = params.get("hash");
        if (checkTransactionForDuplicate(hash)) {
            log.warn("*** eos *** transaction {} already accepted", hash);
            return;
        }
        BigDecimal amount = new BigDecimal(params.get("amount"));
        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .address(address)
                .merchantId(merchant.getId())
                .currencyId(currency.getId())
                .amount(amount)
                .merchantTransactionId(hash)
                .toMainAccountTransferringConfirmNeeded(this.toMainAccountTransferringConfirmNeeded())
                .build();

        refillService.createAndAutoAcceptRefillRequest(requestAcceptDto);
    }

    @Override
    public void checkDestinationTag(String destinationTag) {
      // It checks by EOS Coin
    }

    @Override
    public boolean isValidDestinationAddress(String address) {
        return withdrawUtils.isValidDestinationAddress(mainAddress, address);
    }

    private boolean checkTransactionForDuplicate(String hash) {
        return StringUtils.isEmpty(hash) || refillService.getRequestIdByMerchantIdAndCurrencyIdAndHash(merchant.getId(), currency.getId(), hash).isPresent();
    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        throw new RuntimeException("not supported");
    }

}
