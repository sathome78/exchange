package me.exrates.service.achain;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Currency;
import me.exrates.model.Merchant;
import me.exrates.model.dto.RefillRequestAcceptDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.WithdrawMerchantOperationDto;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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
public class AchainServiceImpl implements AchainService {

    private final NodeService nodeService;
    private final CurrencyService currencyService;
    private final MerchantService merchantService;
    private final RefillService refillService;
    private final MessageSource messageSource;
    private final BigDecimal ACT_COMISSION = new BigDecimal(0.01).setScale(2, RoundingMode.HALF_UP);
    private final BigDecimal TOKENS_COMISSION = new BigDecimal(0.1).setScale(2, RoundingMode.HALF_UP);
    private static final String MERCHANT_NAME = "ACHAIN";

    @Autowired
    public AchainServiceImpl(NodeService nodeService, CurrencyService currencyService, MerchantService merchantService, RefillService refillService, MessageSource messageSource) {
        this.nodeService = nodeService;
        this.currencyService = currencyService;
        this.merchantService = merchantService;
        this.refillService = refillService;
        this.messageSource = messageSource;
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
            put("address",  generated);
            put("message", message);
            put("qr", fullAddress);
        }};
    }

    private String generateRandomSymbolsAndAddToAddress() {
        return RandomStringUtils.random(32, false ,true);
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
        log.info("try to accept payment {}", requestAcceptDto);
        try {
            refillService.autoAcceptRefillRequest(requestAcceptDto);
        } catch (RefillRequestAppropriateNotFoundException e) {
            log.debug("RefillRequestNotFountException: " + params);
            Integer requestId = refillService.createRefillRequestByFact(requestAcceptDto);
            requestAcceptDto.setRequestId(requestId);
            refillService.autoAcceptRefillRequest(requestAcceptDto);
        }
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
}
