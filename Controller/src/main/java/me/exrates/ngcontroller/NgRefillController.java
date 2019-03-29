package me.exrates.ngcontroller;

import me.exrates.controller.exception.ErrorInfo;
import me.exrates.dao.exception.RefillAddressException;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Currency;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.Payment;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.RefillRequestParamsDto;
import me.exrates.model.dto.ngDto.RefillOnConfirmationDto;
import me.exrates.model.dto.ngDto.RefillPageDataDto;
import me.exrates.model.enums.MerchantProcessType;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.invoice.RefillStatusEnum;
import me.exrates.model.exceptions.InvoiceActionIsProhibitedForCurrencyPermissionOperationException;
import me.exrates.model.exceptions.InvoiceActionIsProhibitedForNotHolderException;
import me.exrates.model.ngExceptions.NgCurrencyNotFoundException;
import me.exrates.model.ngExceptions.NgRefillException;
import me.exrates.service.*;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.exception.MerchantNotFoundException;
import me.exrates.service.exception.MerchantServiceNotFoundException;
import me.exrates.service.exception.process.NotEnoughUserWalletMoneyException;
import me.exrates.service.exception.invoice.InvoiceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static me.exrates.model.enums.OperationType.INPUT;
import static me.exrates.model.enums.UserCommentTopicEnum.REFILL_CURRENCY_WARNING;
import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.CREATE_BY_USER;

@RestController
@RequestMapping(value = "/api/private/v2/balances/refill",
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class NgRefillController {

    private static final Logger logger = LoggerFactory.getLogger(NgRefillController.class);

    private final CurrencyService currencyService;
    private final InputOutputService inputOutputService;
    private final MerchantService merchantService;
    private final MessageSource messageSource;
    private final RefillService refillService;
    private final UserService userService;

    private final GtagRefillService gtagRefillService;

    @Autowired
    public NgRefillController(CurrencyService currencyService,
                              InputOutputService inputOutputService,
                              UserService userService,
                              MerchantService merchantService,
                              MessageSource messageSource,
                              RefillService refillService, GtagRefillService gtagRefillService) {
        this.currencyService = currencyService;
        this.inputOutputService = inputOutputService;
        this.userService = userService;
        this.merchantService = merchantService;
        this.messageSource = messageSource;
        this.refillService = refillService;
        this.gtagRefillService = gtagRefillService;
    }

    // /info/private/v2/balances/refill/crypto-currencies

    /**
     * @return set of unique currencies which market is BTC or ETH
     */
    @GetMapping("/crypto-currencies")
    @ResponseBody
    public List<Currency> getCryptoCurrencies() {
        try {
            return currencyService.getCurrencies(MerchantProcessType.CRYPTO)
                    .stream()
                    .filter(o-> !o.getName().equalsIgnoreCase("rub"))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Failed to get all hashed currency names");
            return Collections.emptyList();
        }
    }

    // /info/private/v2/balances/refill/fiat-currencies

    /**
     * @return set of unique currencies names which market is FIAT
     */
    @GetMapping("/fiat-currencies")
    @ResponseBody
    public List<Currency> getFiatCurrencies() {
        try {
            return currencyService.getCurrencies(MerchantProcessType.MERCHANT, MerchantProcessType.INVOICE);
        } catch (Exception e) {
            logger.error("Failed to get all fiat names");
            return Collections.emptyList();
        }
    }



    @GetMapping("/afgssr/gtag")
    public Map<String, String> getGtagRequests() {
        Map<String, String> response = new HashMap<>();
        try {
            String principalEmail = getPrincipalEmail();
            response.put("count", String.valueOf(gtagRefillService.getUserRequests(principalEmail)));
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @DeleteMapping("/afgssr/gtag")
    public Map<String,String> resetGtagRequests(){
        Map<String, String> response = new HashMap<>();
        try {
            String principalEmail = getPrincipalEmail();
            gtagRefillService.resetCount(principalEmail);
            response.put("reset", "true");
            return response;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    // /api/private/v2/balances/refill/merchants/input?currency=${currencyName}

    /**
     * Return merchant to get necessary refill fields specified by currency name
     *
     * @param currencyName - currency name
     * @return merchant data for selected currency name
     */
    @GetMapping(value = "/merchants/input")
    public RefillPageDataDto inputCredits(@RequestParam("currency") String currencyName) {
        Currency currency = currencyService.findByName(currencyName);
        if (currency == null) {
            logger.error("Failed to find currency for name: " + currencyName);
            throw new NgCurrencyNotFoundException("Currency not found for name: " + currencyName);
        }
        RefillPageDataDto response = new RefillPageDataDto();
        OperationType operationType = INPUT;
        response.setCurrency(currency);
        Payment payment = new Payment();
        payment.setOperationType(operationType);
        response.setPayment(payment);
        BigDecimal minRefillSum =
                currencyService.retrieveMinLimitForRoleAndCurrency(userService.getUserRoleFromSecurityContext(),
                        operationType, currency.getId());
        response.setMinRefillSum(minRefillSum);
        Integer scaleForCurrency = currencyService.getCurrencyScaleByCurrencyId(currency.getId()).getScaleForRefill();
        response.setScaleForCurrency(scaleForCurrency);
        List<Integer> currenciesId = Collections.singletonList(currency.getId());
        List<MerchantCurrency> merchantCurrencyData =
                merchantService.getAllUnblockedForOperationTypeByCurrencies(currenciesId, operationType);
        refillService.retrieveAddressAndAdditionalParamsForRefillForMerchantCurrencies(merchantCurrencyData, getPrincipalEmail());
        response.setMerchantCurrencyData(merchantCurrencyData);
        List<String> warningCodeList = currencyService.getWarningForCurrency(currency.getId(), REFILL_CURRENCY_WARNING);
        response.setWarningCodeList(warningCodeList);
        response.setIsaMountInputNeeded(merchantCurrencyData.size() > 0
                && !merchantCurrencyData.get(0).getProcessType().equals("CRYPTO"));
        return response;
    }

    @PostMapping(value = "/request/create")
    public ResponseEntity<Map<String, Object>> createRefillRequest(
            @RequestBody RefillRequestParamsDto requestParamsDto) {
        Locale locale = userService.getUserLocaleForMobile(getPrincipalEmail());
        if (requestParamsDto.getOperationType() != INPUT) {
            logger.warn("Failed to process refill request operation type is not INPUT, but " + requestParamsDto.getOperationType());
            throw new NgRefillException("Request operation type is not INPUT, but " + requestParamsDto.getOperationType());
        }
        // todo check logic
        if (!refillService.checkInputRequestsLimit(requestParamsDto.getCurrency(), getPrincipalEmail())) {
            String message = "Failed to process refill request as number of tries exceeded ";
            logger.warn(message);
            throw new NgRefillException(message);
        }
        Boolean forceGenerateNewAddress = requestParamsDto.getGenerateNewAddress() != null && requestParamsDto.getGenerateNewAddress();
        if (!forceGenerateNewAddress) {
            Optional<String> address = refillService.getAddressByMerchantIdAndCurrencyIdAndUserId(
                    requestParamsDto.getMerchant(),
                    requestParamsDto.getCurrency(),
                    userService.getIdByEmail(getPrincipalEmail())
            );
            if (address.isPresent()) {
                String message = messageSource.getMessage("refill.messageAboutCurrentAddress", new String[]{address.get()}, locale);
                HashMap<String, Object> response = new HashMap<String, Object>() {{
                    put("address", address.get());
                    put("message", message);
                    put("qr", address.get());
                }};
                HashMap<String, Object> result = new HashMap<String, Object>() {{
                    put("params", response);
                }};

                return ResponseEntity.ok(result);
            }
        }
        RefillStatusEnum beginStatus = (RefillStatusEnum) RefillStatusEnum.X_STATE.nextState(CREATE_BY_USER);
        Payment payment = new Payment(INPUT);
        payment.setCurrency(requestParamsDto.getCurrency());
        payment.setMerchant(requestParamsDto.getMerchant());
        payment.setSum(requestParamsDto.getSum() == null ? 0 : requestParamsDto.getSum().doubleValue());
        CreditsOperation creditsOperation = inputOutputService.prepareCreditsOperation(payment, getPrincipalEmail(), locale)
                .orElseThrow(InvalidAmountException::new);
        RefillRequestCreateDto request = new RefillRequestCreateDto(requestParamsDto, creditsOperation, beginStatus, locale);
        try {
            Map<String, Object> response = refillService.createRefillRequest(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to create refill request", e);
            throw new NgRefillException(e.getMessage());
        }
    }

    // apiUrl/info/private/v2/balances/refill/requests_on_confirmation/1
    @GetMapping(value = "/requests_on_confirmation/{currencyId}")
    public List<RefillOnConfirmationDto> getRefillConfirmationsForCurrencyy(@PathVariable Integer currencyId) {
        try {
            List<RefillOnConfirmationDto> confirmationRefills = refillService.getOnConfirmationRefills(getPrincipalEmail(), currencyId);
            return confirmationRefills;
        } catch (Exception e) {
            logger.error("Failed to get requests on confirmation", e);
            return Collections.emptyList();
        }
    }

    private String getPrincipalEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE) // 406
    @ExceptionHandler({InvoiceNotFoundException.class, NgCurrencyNotFoundException.class,
            NotEnoughUserWalletMoneyException.class, NgRefillException.class, RefillAddressException.class,
            NotEnoughUserWalletMoneyException.class, NgRefillException.class, RefillAddressException.class,
            MerchantNotFoundException.class, MerchantServiceNotFoundException.class})
    @ResponseBody
    public ErrorInfo NotFoundExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler({
            InvoiceActionIsProhibitedForCurrencyPermissionOperationException.class,
            InvoiceActionIsProhibitedForNotHolderException.class
    })
    @ResponseBody
    public ErrorInfo ForbiddenExceptionHandler(HttpServletRequest req, Exception exception) {
        logger.error("This operation is forbidden", exception);
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    // added new branch
}
