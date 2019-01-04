package me.exrates.service.autist;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.RefillRequestPutOnBchExamDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.CurrencyService;
import me.exrates.service.GtagService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.MerchantInternalException;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.util.WithdrawUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import static me.exrates.service.autist.MemoDecryptor.decryptBTSmemo;

@Service("aunitServiceImpl")
@PropertySource("classpath:/merchants/aunit.properties")
@Log4j2(topic = "aunit")
public class AunitServiceImpl implements AunitService {

    private @Value("${aunit.mainAddress}")
    String systemAddress;

    private final MessageSource messageSource;
    private final RefillService refillService;
    private final Merchant merchant;
    private final Currency currency;
    private final WithdrawUtils withdrawUtils;
    private final GtagService gtagService;

    static final String AUNIT_CURRENCY = "AUNIT";
    static final String AUNIT_MERCHANT = "AUNIT";
    private static final int MAX_TAG_DESTINATION_DIGITS = 9;

    @Autowired
    public AunitServiceImpl(MerchantService merchantService,
                            CurrencyService currencyService,
                            MessageSource messageSource,
                            RefillService refillService,
                            WithdrawUtils withdrawUtils,
                            GtagService gtagService) {
        this.messageSource = messageSource;
        this.refillService = refillService;
        this.withdrawUtils = withdrawUtils;
        currency = currencyService.findByName(AUNIT_CURRENCY);
        merchant = merchantService.findByName(AUNIT_MERCHANT);
        this.gtagService = gtagService;
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
        Integer destinationTag = generateUniqDestinationTag(request.getUserId());
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
            id = refillService.getRequestIdReadyForAutoAcceptByAddressAndMerchantIdAndCurrencyId(String.valueOf(destinationTag), //wtf
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

        Integer requestId;
        try {
            requestId = refillService.getRequestId(requestAcceptDto);
            requestAcceptDto.setRequestId(requestId);

            refillService.autoAcceptRefillRequest(requestAcceptDto);
        } catch (RefillRequestAppropriateNotFoundException e) {
            requestId = setIdAndAccept(requestAcceptDto);
        }
        final String username = refillService.getUsernameByRequestId(requestId);

        log.debug("Process of sending data to Google Analytics...");
        gtagService.sendGtagEvents(amount.toString(), currency.getName(), username);
    }

    private Integer setIdAndAccept(RefillRequestAcceptDto requestAcceptDto) throws RefillRequestAppropriateNotFoundException {
        try {
            Integer requestId = refillService.createRefillRequestByFact(requestAcceptDto);
            requestAcceptDto.setRequestId(requestId);

            refillService.autoAcceptRefillRequest(requestAcceptDto);
            return requestId;
        } catch (Exception e) {
            log.error(e);
            throw e;
        }
    }

    @Override
    public RefillRequestAcceptDto createRequest(String hash, String address, BigDecimal amount) {
        if (isTransactionDuplicate(hash, currency.getId(), merchant.getId())) {
            log.error("aunit transaction allready received!!! {}", hash);
            throw new RuntimeException("aunit transaction allready received!!!");
        }
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

    private boolean isTransactionDuplicate(String hash, int currencyId, int merchantId) {
        return StringUtils.isEmpty(hash)
                || refillService.getRequestIdByMerchantIdAndCurrencyIdAndHash(merchantId, currencyId, hash).isPresent();
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

    @Override
    public boolean isValidDestinationAddress(String address) {
        return withdrawUtils.isValidDestinationAddress(address);
    }

//    private boolean isTransactionDuplicate(String hash, int currencyId, int merchantId) {
//        return StringUtils.isEmpty(hash)
//                || refillService.getRequestIdByMerchantIdAndCurrencyIdAndHash(merchantId, currencyId, hash).isPresent();
//    }

    //Example for decrypting memo
    public static void main(String[] args) throws NoSuchAlgorithmException {
        String s = decryptBTSmemo("5Js88n7mstj3oetaWvmr2s6aYdd8Tfp6P55sCAidkDdaxFhzAAv", "{\"from\":\"AUNIT7k3nL56J7hh2yGHgWTUk9bGdjG2LL1S7egQDJYZ71MQtU3CqB5\",\"to\":\"AUNIT83A7sYcCZvVMphurvQPbGtw6BFHFxPFDZfKCJDqzcAeSfPrSgR\",\"nonce\":\"394474453593373\",\"message\":\"a3a22532efe98f3ab7d31d50761079d6\"}");

        System.out.println(s);
    }

}
