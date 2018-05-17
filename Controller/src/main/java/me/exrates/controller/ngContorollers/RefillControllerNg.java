package me.exrates.controller.ngContorollers;

import lombok.extern.log4j.Log4j2;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.model.*;
import me.exrates.model.Currency;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.RefillRequestFlatDto;
import me.exrates.model.dto.RefillRequestParamsDto;
import me.exrates.model.dto.ngDto.RefillPageDataDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.invoice.RefillStatusEnum;
import me.exrates.model.exceptions.InvoiceActionIsProhibitedForCurrencyPermissionOperationException;
import me.exrates.model.exceptions.InvoiceActionIsProhibitedForNotHolderException;
import me.exrates.model.vo.InvoiceConfirmData;
import me.exrates.service.*;
import me.exrates.service.exception.IllegalOperationTypeException;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.exception.NotEnoughUserWalletMoneyException;
import me.exrates.service.exception.invoice.InvalidAccountException;
import me.exrates.service.exception.invoice.InvoiceNotFoundException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;

import static me.exrates.model.enums.OperationType.INPUT;
import static me.exrates.model.enums.UserCommentTopicEnum.REFILL_CURRENCY_WARNING;
import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.CREATE_BY_USER;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by Maks on 07.03.2018.
 */
@Log4j2
@RequestMapping("/info/private/refill")
@RestController
public class RefillControllerNg {

    private final CurrencyService currencyService;
    private final MerchantService merchantService;
    private final RefillService refillService;
    private final UserService userService;
    private final MessageSource messageSource;
    private final InputOutputService inputOutputService;

    @Autowired
    public RefillControllerNg(CurrencyService currencyService, MerchantService merchantService, RefillService refillService, UserService userService, MessageSource messageSource, InputOutputService inputOutputService) {
        this.currencyService = currencyService;
        this.merchantService = merchantService;
        this.refillService = refillService;
        this.userService = userService;
        this.messageSource = messageSource;
        this.inputOutputService = inputOutputService;
    }

    @RequestMapping(value = "/merchants/input", method = GET)
    public RefillPageDataDto inputCredits(@RequestParam("currency") String currencyName) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
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

    @RequestMapping(value = "/request/create", method = POST)
    @ResponseBody
    public Map<String, Object> createRefillRequest(
            @RequestBody RefillRequestParamsDto requestParamsDto) throws UnsupportedEncodingException {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Locale locale = userService.getUserLocaleForMobile(userEmail);
        if (requestParamsDto.getOperationType() != INPUT) {
            throw new IllegalOperationTypeException(requestParamsDto.getOperationType().name());
        }
        if (!refillService.checkInputRequestsLimit(requestParamsDto.getCurrency(), userEmail)) {
            throw new RequestLimitExceededException(messageSource.getMessage("merchants.InputRequestsLimit", null, locale));
        }
        Boolean forceGenerateNewAddress = requestParamsDto.getGenerateNewAddress() != null && requestParamsDto.getGenerateNewAddress();
        if (!forceGenerateNewAddress) {
            Optional<String> address = refillService.getAddressByMerchantIdAndCurrencyIdAndUserId(
                    requestParamsDto.getMerchant(),
                    requestParamsDto.getCurrency(),
                    userService.getIdByEmail(userEmail)
            );
            if (address.isPresent()) {
                String message = messageSource.getMessage("refill.messageAboutCurrentAddress", new String[]{address.get()}, locale);
                return new HashMap<String, Object>() {{
                    put("address", address.get());
                    put("message", message);
                    put("qr", address.get());
                }};
            }
        }
        RefillStatusEnum beginStatus = (RefillStatusEnum) RefillStatusEnum.X_STATE.nextState(CREATE_BY_USER);
        Payment payment = new Payment(INPUT);
        payment.setCurrency(requestParamsDto.getCurrency());
        payment.setMerchant(requestParamsDto.getMerchant());
        payment.setSum(requestParamsDto.getSum() == null ? 0 : requestParamsDto.getSum().doubleValue());
        CreditsOperation creditsOperation = inputOutputService.prepareCreditsOperation(payment, userEmail)
                .orElseThrow(InvalidAmountException::new);
        RefillRequestCreateDto request = new RefillRequestCreateDto(requestParamsDto, creditsOperation, beginStatus, locale);
        return refillService.createRefillRequest(request);
    }

    @RequestMapping(value = "/request/revoke", method = POST)
    @ResponseBody
    public void revokeWithdrawRequest(
            @RequestParam Integer id) {
        /*todo revoke only own requests*/
        refillService.revokeRefillRequest(id);
    }

    /*used for invoices*/
    @RequestMapping(value = "/invoice/request/info", method = GET)
    @ResponseBody
    public RefillRequestFlatDto getInfoRefill(
            @RequestParam Integer id) {
        /*get only own info*/
        return refillService.getFlatById(id);
    }

    /*used for invoices*/
    @RequestMapping(value = "/invoice/request/confirm", method = POST)
    @ResponseBody
    public void confirmRefillRequest(
            InvoiceConfirmData invoiceConfirmData,
            Locale locale) {
        refillService.confirmRefillRequest(invoiceConfirmData, locale);
    }

    /*used for invoices*/
    @RequestMapping(value = "/invoice/banks", method = GET)
    @ResponseBody
    public List<InvoiceBank> getBankListForCurrency(
            @RequestParam Integer currencyId) {
        List<InvoiceBank> banks = refillService.findBanksForCurrency(currencyId);
        banks.forEach(bank -> {
            if (bank.getBankDetails() != null) {
                bank.setBankDetails(bank.getBankDetails().replaceAll("\n", "<br/>"));
            }
        });
        return banks;
    }

    @RequestMapping(value = "/commission", method = GET)
    @ResponseBody
    public Map<String, String> getCommissions(
            @RequestParam("amount") BigDecimal amount,
            @RequestParam("currency") Integer currencyId,
            @RequestParam("merchant") Integer merchantId) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Locale locale = userService.getUserLocaleForMobile(userEmail);
        Integer userId = userService.getIdByEmail(userEmail);
        return refillService.correctAmountAndCalculateCommission(userId, amount, currencyId, merchantId, locale);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(InvoiceNotFoundException.class)
    @ResponseBody
    public ErrorInfo NotFoundExceptionHandler(HttpServletRequest req, Exception exception) {
        log.error(exception);
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler({
            InvoiceActionIsProhibitedForCurrencyPermissionOperationException.class,
            InvoiceActionIsProhibitedForNotHolderException.class
    })
    @ResponseBody
    public ErrorInfo ForbiddenExceptionHandler(HttpServletRequest req, Exception exception) {
        log.error(exception);
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler({
            NotEnoughUserWalletMoneyException.class,
    })
    @ResponseBody
    public ErrorInfo NotAcceptableExceptionHandler(HttpServletRequest req, Exception exception) {
        log.error(exception);
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler({
            InvalidAccountException.class,
    })
    @ResponseBody
    public ErrorInfo ForbiddenExceptionHandler(HttpServletRequest req, InvalidAccountException exception) {
        log.error(exception);
        return new ErrorInfo(req.getRequestURL(), exception, exception.getReason());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ErrorInfo OtherErrorsHandler(HttpServletRequest req, Exception exception) {
        log.error(ExceptionUtils.getStackTrace(exception));
        return new ErrorInfo(req.getRequestURL(), exception);
    }
}
