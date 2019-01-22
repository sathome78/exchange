package me.exrates.service.tron;

import lombok.Synchronized;
import lombok.extern.log4j.Log4j2;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestAddressDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.RefillRequestPutOnBchExamDto;
import me.exrates.model.dto.TronNewAddressDto;
import me.exrates.model.dto.TronReceivedTransactionDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.CurrencyService;
import me.exrates.service.GtagService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.util.WithdrawUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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
import java.util.stream.Collectors;

@Log4j2(topic = "tron")
@Service
public class TronServiceImpl implements TronService {

    private final static String CURRENCY_NAME = "TRX";
    private final static String MERCHANT_NAME = "TRX";
    private int merchantId;
    private int currencyId;

    private Set<String> addressesHEX = Collections.synchronizedSet(new HashSet<>());

    private final TronNodeService tronNodeService;
    private final RefillService refillService;
    private final CurrencyService currencyService;
    private final MerchantService merchantService;
    private final MessageSource messageSource;
    private final GtagService gtagService;

    @Autowired
    public TronServiceImpl(TronNodeService tronNodeService,
                           RefillService refillService,
                           CurrencyService currencyService,
                           MerchantService merchantService,
                           MessageSource messageSource,
                           GtagService gtagService) {
        this.tronNodeService = tronNodeService;
        this.refillService = refillService;
        this.currencyService = currencyService;
        this.merchantService = merchantService;
        this.messageSource = messageSource;
        this.gtagService = gtagService;
    }

    @Autowired
    private WithdrawUtils withdrawUtils;

    @Override
    public Set<String> getAddressesHEX() {
        return addressesHEX;
    }

    @PostConstruct
    private void init() {
        merchantId = merchantService.findByName(MERCHANT_NAME).getId();
        currencyId = currencyService.findByName(CURRENCY_NAME).getId();
        addressesHEX.addAll(refillService.findAddressDtos(merchantId, currencyId).stream().map(RefillRequestAddressDto::getPubKey).collect(Collectors.toList()));
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
    public RefillRequestAcceptDto createRequest(TronReceivedTransactionDto dto) {
        if (isTransactionDuplicate(dto.getHash(), currencyId, merchantId)) {
            log.error("tron transaction allready received!!! {}", dto);
            throw new RuntimeException("tron transaction allready received!!!");
        }
        RefillRequestAcceptDto requestAcceptDto = RefillRequestAcceptDto.builder()
                .address(dto.getAddressBase58())
                .merchantId(merchantId)
                .currencyId(currencyId)
                .amount(new BigDecimal(dto.getAmount()))
                .merchantTransactionId(dto.getHash())
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
                            .merchantId(merchantId)
                            .currencyId(currencyId)
                            .address(requestAcceptDto.getAddress())
                            .amount(requestAcceptDto.getAmount())
                            .hash(requestAcceptDto.getMerchantTransactionId())
                            .build());
        } catch (RefillRequestAppropriateNotFoundException e) {
            log.error(e);
        }
    }

    @Synchronized
    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        String address = params.get("address");
        String hash = params.get("hash");
        Integer id = Integer.parseInt(params.get("id"));
        Currency currency = currencyService.findByName(CURRENCY_NAME);
        Merchant merchant = merchantService.findByName(MERCHANT_NAME);
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
        final String username = refillService.getUsernameByRequestId(id);

        log.debug("Process of sending data to Google Analytics...");
        gtagService.sendGtagEvents(amount.toString(), currency.getName(), username);
    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) {
        throw new RuntimeException("Tron withdraw method not implemented!");
    }

    @Override
    public int getMerchantId() {
        return merchantId;
    }

    @Override
    public int getCurrencyId() {
        return currencyId;
    }

    private boolean isTransactionDuplicate(String hash, int currencyId, int merchantId) {
        return StringUtils.isEmpty(hash)
                || refillService.getRequestIdByMerchantIdAndCurrencyIdAndHash(merchantId, currencyId, hash).isPresent();
    }

    @Override
    public BigDecimal countSpecCommission(BigDecimal amount, String destinationTag, Integer merchantId) {
        return new BigDecimal(0.1).setScale(3, RoundingMode.HALF_UP);
    }

    @Override
    public boolean isValidDestinationAddress(String address) {

        return withdrawUtils.isValidDestinationAddress(address);
    }

}
