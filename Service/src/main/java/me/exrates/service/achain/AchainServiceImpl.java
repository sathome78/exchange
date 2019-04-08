package me.exrates.service.achain;

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
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Maks on 14.06.2018.
 */
@Log4j2(topic = "achain")
@Service
@Conditional(MonolitConditional.class)
public class AchainServiceImpl implements AchainService {

    private final BigDecimal ACT_COMISSION = new BigDecimal(0.01).setScale(2, RoundingMode.HALF_UP);
    private final BigDecimal TOKENS_COMISSION = new BigDecimal(0.1).setScale(2, RoundingMode.HALF_UP);
    public static final String MERCHANT_NAME = "ACHAIN";

    private final NodeService nodeService;
    private final CurrencyService currencyService;
    private final MerchantService merchantService;
    private final RefillService refillService;
    private final MessageSource messageSource;
    private final GtagService gtagService;

    @Autowired
    public AchainServiceImpl(NodeService nodeService,
                             CurrencyService currencyService,
                             MerchantService merchantService,
                             RefillService refillService,
                             MessageSource messageSource,
                             GtagService gtagService) {
        this.nodeService = nodeService;
        this.currencyService = currencyService;
        this.merchantService = merchantService;
        this.refillService = refillService;
        this.messageSource = messageSource;
        this.gtagService = gtagService;
    }

    @Override
    public String getMainAddress() {
        return nodeService.getMainAccountAddress();
    }

    @Override
    public Map<String, String> withdraw(WithdrawMerchantOperationDto withdrawMerchantOperationDto) throws Exception {
        /*autowithdraw not implemented*/
        throw new RuntimeException("autowithdraw not supported");
    }

    @Override
    public Map<String, String> refill(RefillRequestCreateDto request) {
        String generated = generateRandomSymbolsAndAddToAddress();
        String fullAddress = nodeService.getMainAccountAddress().concat(generated);
        String message = messageSource.getMessage("merchants.refill.btc",
                new Object[]{fullAddress}, request.getLocale());
        return new HashMap<String, String>() {{
            put("address", generated);
            put("message", message);
            put("qr", fullAddress);
        }};
    }

    private String generateRandomSymbolsAndAddToAddress() {
        return RandomStringUtils.random(32, false, true);
    }

    @Override
    public void processPayment(Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        String address = params.get("address");
        String hash = params.get("hash");
        Currency currency = currencyService.findByName(params.get("currency"));
        Merchant merchant = merchantService.findByName(params.get("merchant"));
        BigDecimal amount = new BigDecimal(params.get("amount"));
        if (isTransactionDuplicate(hash, currency.getId(), merchant.getId())) {
            log.warn("achain tx duplicated {}", hash);
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
        log.info("try to accept payment {}", requestAcceptDto);
        try {
            requestId = refillService.getRequestId(requestAcceptDto);
            requestAcceptDto.setRequestId(requestId);

            refillService.autoAcceptRefillRequest(requestAcceptDto);
        } catch (RefillRequestAppropriateNotFoundException e) {
            log.debug("RefillRequestNotFountException: " + params);
            requestId = refillService.createRefillRequestByFact(requestAcceptDto);
            requestAcceptDto.setRequestId(requestId);

            refillService.autoAcceptRefillRequest(requestAcceptDto);
        }
        final String username = refillService.getUsernameByRequestId(requestId);

        log.debug("Process of sending data to Google Analytics...");
        gtagService.sendGtagEvents(amount.toString(), currency.getName(), username);
    }

    private boolean isTransactionDuplicate(String hash, int currencyId, int merchantId) {
        return StringUtils.isEmpty(hash)
                || refillService.getRequestIdByMerchantIdAndCurrencyIdAndHash(merchantId, currencyId, hash).isPresent();
    }

    @Override
    public BigDecimal countSpecCommission(BigDecimal amount, String destinationTag, Integer merchantId) {
        log.debug("comission merchant {}", merchantId);
        Merchant merchant = merchantService.findById(merchantId);
        if (merchant.getName().equals(MERCHANT_NAME)) {
            return ACT_COMISSION;
        }
        return TOKENS_COMISSION;
    }

    @Override
    public boolean isValidDestinationAddress(String address) {

        return !address.startsWith(nodeService.getMainAccountAddress());
    }

}
