package me.exrates.ngcontroller;

import me.exrates.model.Currency;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.Payment;
import me.exrates.model.dto.ngDto.RefillPageDataDto;
import me.exrates.model.enums.OperationType;
import me.exrates.service.CurrencyService;
import me.exrates.service.MerchantService;
import me.exrates.service.RefillService;
import me.exrates.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static me.exrates.model.enums.OperationType.INPUT;
import static me.exrates.model.enums.UserCommentTopicEnum.REFILL_CURRENCY_WARNING;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
@RequestMapping(value = "/info/private/v2/balances/refill",
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class NgRefillController {

    private static final Logger logger = LoggerFactory.getLogger(NgRefillController.class);

    private final CurrencyService currencyService;
    private final MerchantService merchantService;
    private final RefillService refillService;
    private final UserService userService;

    @Autowired
    public NgRefillController(CurrencyService currencyService,
                              UserService userService,
                              MerchantService merchantService,
                              RefillService refillService) {
        this.currencyService = currencyService;
        this.userService = userService;
        this.merchantService = merchantService;
        this.refillService = refillService;
    }

    // /info/private/v2/balances/refill/hash-pair-names

    /**
     *
     * @return set of unique currency pair names which market is BTC or ETH
     */
    @GetMapping("/hash-pair-names")
    @ResponseBody
    public Set<String> getHashedPairs() {
        try {
            return currencyService.getHashedCurrencyNames();
        } catch (Exception e) {
            logger.error("Failed to get all hashed currency names");
            return Collections.emptySet();
        }
    }

    @GetMapping(value = "/merchants/input")
    public RefillPageDataDto inputCredits(@RequestParam("currency") String currencyName) {
        String userEmail = getPrincipalEmail();
        RefillPageDataDto response = new RefillPageDataDto();
        OperationType operationType = INPUT;
        Currency currency = currencyService.findByName(currencyName);
        response.setCurrency(currency);
        Payment payment = new Payment();
        payment.setOperationType(operationType);
        response.setPayment(payment);
        BigDecimal minRefillSum = currencyService.retrieveMinLimitForRoleAndCurrency(userService.getUserRoleFromSecurityContext(), operationType, currency.getId());
        response.setMinRefillSum(minRefillSum);
        Integer scaleForCurrency = currencyService.getCurrencyScaleByCurrencyId(currency.getId()).getScaleForRefill();
        response.setScaleForCurrency(scaleForCurrency);
        List<Integer> currenciesId = Collections.singletonList(currency.getId());
        List<MerchantCurrency> merchantCurrencyData = merchantService.getAllUnblockedForOperationTypeByCurrencies(currenciesId, operationType);
        refillService.retrieveAddressAndAdditionalParamsForRefillForMerchantCurrencies(merchantCurrencyData, userEmail);
        response.setMerchantCurrencyData(merchantCurrencyData);
        List<String> warningCodeList = currencyService.getWarningForCurrency(currency.getId(), REFILL_CURRENCY_WARNING);
        response.setWarningCodeList(warningCodeList);
        response.setIsaMountInputNeeded(merchantCurrencyData.size() > 0
                && !merchantCurrencyData.get(0).getProcessType().equals("CRYPTO"));
        return response;
    }

    private String getPrincipalEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
