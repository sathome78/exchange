package me.exrates.service.autist;

import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.RefillRequestPutOnBchExamDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.MerchantInternalException;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service("aunitServiceImpl")
@PropertySource("classpath:/merchants/aunit.properties")
//@Log4j2(topic = "aunit")
public class AunitServiceImpl implements AunitService {

    private @Value("${aunit.mainAddress}")String systemAddress;

    private final MessageSource messageSource;
    private final RefillService refillService;


    static final String AUNIT_CURRENCY = "AUNIT";
    static final String AUNIT_MERCHANT = "AUNIT";
    private static final int MAX_TAG_DESTINATION_DIGITS = 9;
    private final Merchant merchant;
    private final Currency currency;

    @Autowired
    public AunitServiceImpl(MerchantService merchantService, CurrencyService currencyService, MessageSource messageSource, RefillService refillService) {
        this.messageSource = messageSource;
        this.refillService = refillService;
        currency = currencyService.findByName(AUNIT_CURRENCY);
        merchant = merchantService.findByName(AUNIT_MERCHANT);
    }

    @Override
    public Merchant getMerchant() {
        return merchant;
    }

    @Override
    public Currency getCurrency() {
        return currency;
    }

    /*generate 9 digits(Unsigned Integer) for identifying payment */
    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        Integer destinationTag = generateUniqDestinationTag(request.getUserId());
        System.out.println("desitanion tag = " + destinationTag);
        String message = messageSource.getMessage("merchants.refill.xrp",
                new String[]{systemAddress, destinationTag.toString()}, request.getLocale());
        return new HashMap<String, String>() {{
            put("address", String.valueOf(destinationTag));
            put("message", message);
            put("qr", systemAddress);
        }};
    }


    private Integer generateUniqDestinationTag(int userId) {
        Optional<Integer> id;
        int destinationTag;
        do {
            destinationTag = generateDestinationTag(userId);
            id = refillService.getRequestIdReadyForAutoAccept(String.valueOf(destinationTag), //wtf
                    currency.getId(), merchant.getId());
        } while (id.isPresent());
        return destinationTag;
    }

    private Integer generateDestinationTag(int userId) {
        String idInString = String.valueOf(userId);
        int randomNumberLength = MAX_TAG_DESTINATION_DIGITS - idInString.length();
        if (randomNumberLength < 0) {
            throw new MerchantInternalException("error generating new destination tag for aunit" + userId);
        }
        String randomIntInstring = String.valueOf(100000000 + new Random().nextInt(100000000));
        return Integer.valueOf(idInString.concat(randomIntInstring.substring(0, randomNumberLength)));
    }

//    @Override
//    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
//        String address = params.get("accountAddress");
//        String hash = params.get("hash");
//        BigDecimal amount = new BigDecimal(params.get("amount"));
//        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
//                .address(address)
//                .merchantId(merchant.getId())
//                .currencyId(currency.getId())
//                .amount(amount)
//                .merchantTransactionId(hash)
//                .toMainAccountTransferringConfirmNeeded(this.toMainAccountTransferringConfirmNeeded())
//                .build();
//        refillService.autoAcceptRefillRequest(requestAcceptDto);
//    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        String address = params.get("address");
        String hash = params.get("hash");
        BigDecimal amount = new BigDecimal(params.get("amount"));
        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .address(address)
                .merchantId(merchant.getId())
                .currencyId(currency.getId())
                .amount(amount)
                .merchantTransactionId(hash)
                .toMainAccountTransferringConfirmNeeded(this.toMainAccountTransferringConfirmNeeded())
                .build();
        try {
            refillService.autoAcceptRefillRequest(requestAcceptDto);
        } catch (RefillRequestAppropriateNotFoundException e) {
//            log.debug("RefillRequestNotFountException: " + params);
            System.out.println("RefillRequestNotFountException: " + params);
            Integer requestId = refillService.createRefillRequestByFact(requestAcceptDto);
            requestAcceptDto.setRequestId(requestId);
            refillService.autoAcceptRefillRequest(requestAcceptDto);
        }
    }

    @Override
    public RefillRequestAcceptDto createRequest(String hash, String address, BigDecimal amount) {
//        if (isTransactionDuplicate(hash, currency.getId(), merchant.getId())) {
////            log.error("aunit transaction allready received!!! {}", hash);
//            System.out.println("aunit transaction allready received!! " + hash);
//            throw new RuntimeException("aunit transaction allready received!!!");
//        }
        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .address(address)
                .merchantId(merchant.getId())
                .currencyId(currency.getId())
                .amount(amount)
                .merchantTransactionId(hash)
                .toMainAccountTransferringConfirmNeeded(this.toMainAccountTransferringConfirmNeeded())
                .build();
        Integer requestId = refillService.createRefillRequestByFact(requestAcceptDto);
        requestAcceptDto.setRequestId(requestId);
        return requestAcceptDto;
    }

    @Override
    public void putOnBchExam(RefillRequestAcceptDto requestAcceptDto) {
        try {
            refillService.putOnBchExamRefillRequest(
                    RefillRequestPutOnBchExamDto.builder()
                            .requestId(requestAcceptDto.getRequestId())
                            .merchantId(merchant.getId())
                            .currencyId(currency.getId())
                            .address(requestAcceptDto.getAddress())
                            .amount(requestAcceptDto.getAmount())
                            .hash(requestAcceptDto.getMerchantTransactionId())
                            .build());
        } catch (RefillRequestAppropriateNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) {
        throw new RuntimeException("Not supported");
    }

    @Override
    public String getMainAddress() {
        return systemAddress;
    }

//    private boolean isTransactionDuplicate(String hash, int currencyId, int merchantId) {
//        return StringUtils.isEmpty(hash)
//                || refillService.getRequestIdByMerchantIdAndCurrencyIdAndHash(merchantId, currencyId, hash).isPresent();
//    }

//    //Example for decrypting memo
//    public static void main(String[] args) throws NoSuchAlgorithmException {
//        String s = decryptBTSmemo("5JZ4ZrZ7GXKGKVgqJ6ZKHNDfJAe2K1B58sUVHspA9iLQ3UBG6Lh",
//                "{\"from\":\"AUNIT7k3nL56J7hh2yGHgWTUk9bGdjG2LL1S7egQDJYZ71MQtU3CqB5\",\"to\":\"AUNIT6Y1omrtPmYEHBaK7gdAeqdGASPariaCXGm83Phjc2NDEuxYfzV\",\"nonce\":\"394359322886950\",\"message\":\"5cb68485625d5a9e95ad47d10f422bcf\"}");
//        System.out.println(s);
//    }
}
