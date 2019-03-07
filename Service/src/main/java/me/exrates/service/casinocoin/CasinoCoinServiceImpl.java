package me.exrates.service.casinocoin;

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
import me.exrates.service.exception.CheckDestinationTagException;
import me.exrates.service.exception.MerchantInternalException;
import me.exrates.service.exception.NotImplimentedMethod;
import me.exrates.service.exception.WithdrawRequestPostException;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.util.WithdrawUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

@Log4j2(topic = "casinocoin_log")
@Service
@PropertySource("classpath:/merchants/casinocoin.properties")
@Conditional(MonolitConditional.class)
public class CasinoCoinServiceImpl implements CasinoCoinService {

    private static final String DESTINATION_TAG_ERR_MSG = "message.casinocoin.tagError";

    @Value("${casinocoin.ticker}")
    private String casinoCoinTicker;

    @Value("${casinocoin.max.tag.destination.digits}")
    private int maxTagDestinationDigits;

    @Value("${casinocoin.account.address}")
    private String mainAddress;

    @Autowired
    private CasinoCoinTransactionService casinoCoinTransactionService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private RefillService refillService;
    @Autowired
    private WithdrawUtils withdrawUtils;
    @Autowired
    private GtagService gtagService;

    private Merchant merchant;
    private Currency currency;

    @PostConstruct
    public void init(){
        currency = currencyService.findByName(casinoCoinTicker);
        merchant = merchantService.findByName(casinoCoinTicker);
    }

    @Override
    public void onTransactionReceive(String hash, Integer destinationTag, String amount) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("hash", hash);
        paramsMap.put("address", String.valueOf(destinationTag));
        paramsMap.put("amount", amount);

        this.processPayment(paramsMap);
    }

    private boolean checkTransactionForDuplicate(String hash) {
        return StringUtils.isEmpty(hash) || refillService.getRequestIdByMerchantIdAndCurrencyIdAndHash(merchant.getId(), currency.getId(), hash).isPresent();
    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        throw new NotImplimentedMethod("CasinoCoin | Not implimented method");
    }

    /*generate 9 digits(Unsigned Integer) for identifying payment */
    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        Integer destinationTag = generateUniqDestinationTag(request.getUserId());
        String message = messageSource.getMessage("merchants.refill.csc",
                new String[]{mainAddress, destinationTag.toString()}, request.getLocale());
        DecimalFormat myFormatter = new DecimalFormat("###.##");
        return new HashMap<String, String>() {{
            put("address", myFormatter.format(destinationTag));
            put("message", message);
        }};
    }

    @Synchronized
    @Override
    public void processPayment(Map<String, String> params) {
        String address = params.get("address");
        String hash = params.get("hash");

        if (checkTransactionForDuplicate(hash)) {
            log.warn("*** CSC *** transaction {} already accepted", hash);
            return;
        }

        BigDecimal amount = casinoCoinTransactionService.normalizeAmountToDecimal(params.get("amount"));

        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .address(address)
                .merchantId(merchant.getId())
                .currencyId(currency.getId())
                .amount(amount)
                .merchantTransactionId(hash)
                .toMainAccountTransferringConfirmNeeded(this.toMainAccountTransferringConfirmNeeded())
                .build();

        int requestId = refillService.createAndAutoAcceptRefillRequest(requestAcceptDto);

        final String username = refillService.getUsernameByRequestId(requestId);

        log.debug("Process of sending data to Google Analytics...");
        gtagService.sendGtagEvents(amount.toString(), currency.getName(), username);
    }

    private Integer generateUniqDestinationTag(int userId) {
        Optional<Integer> id;
        int destinationTag;
        do {
            destinationTag = generateDestinationTag(userId);
            id = refillService.getRequestIdReadyForAutoAcceptByAddressAndMerchantIdAndCurrencyId(String.valueOf(destinationTag),
                    currency.getId(), merchant.getId());
        } while (id.isPresent());

        return destinationTag;
    }

    private Integer generateDestinationTag(int userId) {
        String idInString = String.valueOf(userId);
        int randomNumberLength = maxTagDestinationDigits - idInString.length();
        if (randomNumberLength < 0) {
            throw new MerchantInternalException("error generating new destination tag for ripple" + userId);
        }
        String randomIntInstring = String.valueOf(100000000 + new Random().nextInt(100000000));
        return Integer.valueOf(idInString.concat(randomIntInstring.substring(0, randomNumberLength)));
    }

    /*must bee only 32 bit number = 0 - 4294967295*/
    @Override
    public void checkDestinationTag(String destinationTag) {
        if (!(org.apache.commons.lang.math.NumberUtils.isDigits(destinationTag)
                && Long.valueOf(destinationTag) <= 4294967295L)) {
            throw new CheckDestinationTagException(DESTINATION_TAG_ERR_MSG, additionalWithdrawFieldName());
        }
    }

    @Override
    public boolean isValidDestinationAddress(String address) {
        return withdrawUtils.isValidDestinationAddress(mainAddress, address);
    }

    @Override
    public String getMainAddress() {
        return mainAddress;
    }
}
