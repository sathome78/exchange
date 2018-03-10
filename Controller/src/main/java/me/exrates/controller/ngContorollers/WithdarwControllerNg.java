package me.exrates.controller.ngContorollers;

import com.google.common.base.Preconditions;
import lombok.extern.log4j.Log4j2;
import me.exrates.controller.annotation.FinPassCheck;
import me.exrates.controller.exception.CheckFinPassException;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.controller.exception.WrongOrderKeyException;
import me.exrates.model.*;
import me.exrates.model.Currency;
import me.exrates.model.dto.OrderCreateDto;
import me.exrates.model.dto.WithdrawRequestCreateDto;
import me.exrates.model.dto.WithdrawRequestParamsDto;
import me.exrates.model.dto.ngDto.WithdrawDataDto;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;
import me.exrates.model.exceptions.InvoiceActionIsProhibitedForCurrencyPermissionOperationException;
import me.exrates.model.exceptions.InvoiceActionIsProhibitedForNotHolderException;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.security.exception.IncorrectPinException;
import me.exrates.security.exception.PinCodeCheckNeedException;
import me.exrates.security.service.SecureService;
import me.exrates.security.service.SecureServiceImpl;
import me.exrates.service.*;
import me.exrates.service.exception.*;
import me.exrates.service.exception.invoice.MerchantException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;

import static me.exrates.model.enums.OperationType.OUTPUT;
import static me.exrates.model.enums.UserCommentTopicEnum.WITHDRAW_CURRENCY_WARNING;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by Maks on 07.03.2018.
 */
@Log4j2
@RequestMapping("/info/private/withdraw")
@RestController
public class WithdarwControllerNg {

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private RefillService refillService;
    @Autowired
    private WithdrawService withdrawService;
    @Autowired
    private UserService userService;
    @Autowired
    private WalletService walletService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private InputOutputService inputOutputService;
    @Autowired
    private SecureService secureServiceImpl;


    private Map<UUID, WithdrawRequestCreateDto> unconfirmedWithdraws = new ConcurrentReferenceHashMap<>();

    @RequestMapping(value = "/merchants/output", method = GET)
    public WithdrawDataDto outputCredits(
            @RequestParam("currency") String currencyName) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        WithdrawDataDto response = new WithdrawDataDto();
        OperationType operationType = OUTPUT;
        Currency currency = currencyService.findByName(currencyName);
        response.setCurrency(currency);
        Wallet wallet = walletService.findByUserAndCurrency(userService.findByEmail(userEmail), currency);
        response.setWallet(wallet);
        response.setBalance(BigDecimalProcessing.formatNonePoint(wallet.getActiveBalance(), false));
        Payment payment = new Payment();
        payment.setOperationType(operationType);
        response.setPayment(payment);
        BigDecimal minWithdrawSum = currencyService.retrieveMinLimitForRoleAndCurrency(userService.getUserRoleFromSecurityContext(), operationType, currency.getId());
        response.setMinWithdrawSum(minWithdrawSum);
        Integer scaleForCurrency = currencyService.getCurrencyScaleByCurrencyId(currency.getId()).getScaleForWithdraw();
        response.setScaleForCurrency(scaleForCurrency);
        List<Integer> currenciesId = Collections.singletonList(currency.getId());
        List<MerchantCurrency> merchantCurrencyData = merchantService.getAllUnblockedForOperationTypeByCurrencies(currenciesId, operationType);
        withdrawService.retrieveAddressAndAdditionalParamsForWithdrawForMerchantCurrencies(merchantCurrencyData);
        response.setMerchantCurrencyData(merchantCurrencyData);
        List<String> warningCodeList = currencyService.getWarningForCurrency(currency.getId(), WITHDRAW_CURRENCY_WARNING);
        response.setWarningCodeList(warningCodeList);
        return response;
    }

    @FinPassCheck
    @RequestMapping(value = "/request/create", method = POST)
    @ResponseBody
    public Map<String, String> createWithdrawalRequest(
            @RequestBody WithdrawRequestParamsDto requestParamsDto) throws UnsupportedEncodingException {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Locale locale = userService.getUserLocaleForMobile(userEmail);
        if (!withdrawService.checkOutputRequestsLimit(requestParamsDto.getCurrency(), userEmail)) {
            throw new RequestLimitExceededException(messageSource.getMessage("merchants.OutputRequestsLimit", null, locale));
        }
        if (!StringUtils.isEmpty(requestParamsDto.getDestinationTag())) {
            merchantService.checkDestinationTag(requestParamsDto.getMerchant(), requestParamsDto.getDestinationTag());
        }
        WithdrawStatusEnum beginStatus = (WithdrawStatusEnum) WithdrawStatusEnum.getBeginState();
        Payment payment = new Payment(OUTPUT);
        payment.setCurrency(requestParamsDto.getCurrency());
        payment.setMerchant(requestParamsDto.getMerchant());
        payment.setSum(requestParamsDto.getSum().doubleValue());
        payment.setDestination(requestParamsDto.getDestination());
        payment.setDestinationTag(requestParamsDto.getDestinationTag());
        CreditsOperation creditsOperation = inputOutputService.prepareCreditsOperation(payment, userEmail)
                .orElseThrow(InvalidAmountException::new);
        WithdrawRequestCreateDto withdrawRequestCreateDto = new WithdrawRequestCreateDto(requestParamsDto, creditsOperation, beginStatus);
        try {
            secureServiceImpl.checkEventAdditionalPin(locale, userEmail,
                    NotificationMessageEventEnum.WITHDRAW, getAmountWithCurrency(withdrawRequestCreateDto));
        } catch (PinCodeCheckNeedException e) {
            UUID orderKey = UUID.randomUUID();
            unconfirmedWithdraws.put(orderKey, withdrawRequestCreateDto);
            e.setUuid(orderKey.toString());
            throw e;
        }
        return withdrawService.createWithdrawalRequest(withdrawRequestCreateDto, locale);
    }


    @RequestMapping(value = "/withdraw/request/pin", method = POST)
    @ResponseBody
    public Map<String, String> withdrawRequestCheckPin(
            @RequestParam String pin, @RequestParam String key) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Locale locale = userService.getUserLocaleForMobile(userEmail);
        UUID keyUUID = UUID.fromString(key);
        WithdrawRequestCreateDto withdrawRequestCreateDto = unconfirmedWithdraws.get(keyUUID);
        Preconditions.checkArgument(pin.length() > 2 && pin.length() < 15);
        Preconditions.checkNotNull(withdrawRequestCreateDto, "No order found by key");
        if (userService.checkPin(userEmail, pin, NotificationMessageEventEnum.WITHDRAW)) {
            Preconditions.checkArgument(withdrawRequestCreateDto.getUserEmail().equals(userEmail), "Yhe withdrawal is not yours");
            unconfirmedWithdraws.remove(keyUUID);
            return withdrawService.createWithdrawalRequest(withdrawRequestCreateDto, locale);
        } else {
            String res = secureServiceImpl.resendEventPin(locale, userEmail,
                    NotificationMessageEventEnum.WITHDRAW, getAmountWithCurrency(withdrawRequestCreateDto));
            throw new IncorrectPinException(res);
        }
    }

    private String getAmountWithCurrency(WithdrawRequestCreateDto dto) {
        return new StringJoiner(" ", dto.getAmount().toString(), dto.getCurrencyName()).toString();
    }

    @RequestMapping(value = "/withdraw/request/revoke", method = POST)
    @ResponseBody
    public void revokeWithdrawRequest(
            @RequestParam Integer id) {
        /*todo check for only own requests*/
        withdrawService.revokeWithdrawalRequest(id);
    }

    /*todo check where this metod used?*/
    @RequestMapping(value = "/withdraw/banks", method = GET)
    @ResponseBody
    public List<ClientBank> getBankListForCurrency(
            @RequestParam Integer currencyId) {
        return withdrawService.findClientBanksForCurrency(currencyId);
    }

    @RequestMapping(value = "/withdraw/commission", method = GET)
    @ResponseBody
    public Map<String, String> getCommissions(
            @RequestParam("amount") BigDecimal amount,
            @RequestParam("currency") Integer currencyId,
            @RequestParam("merchant") Integer merchantId,
            @RequestParam(value = "memo", required = false) String memo,
            Principal principal,
            Locale locale) {
        Integer userId = userService.getIdByEmail(principal.getName());
        if (!StringUtils.isEmpty(memo)) {
            merchantService.checkDestinationTag(merchantId, memo);
        }
        return withdrawService.correctAmountAndCalculateCommissionPreliminarily(userId, amount, currencyId, merchantId, locale, memo);
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
            RequestLimitExceededException.class
    })
    @ResponseBody
    public ErrorInfo RequestLimitExceededExceptionHandler(HttpServletRequest req, Exception exception) {
        log.error(exception);
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler({
            CheckDestinationTagException.class
    })
    @ResponseBody
    public ErrorInfo CheckDestinationTagExceptionHandler(HttpServletRequest req, Exception exception) {
        log.error(exception);
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Locale locale = userService.getUserLocaleForMobile(userEmail);
        return new ErrorInfo(req.getRequestURL(),
                exception, messageSource.getMessage(exception.getMessage(),
                new String[]{((CheckDestinationTagException) exception).getFieldName()}, locale));
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler({
            NotEnoughUserWalletMoneyException.class
    })
    @ResponseBody
    public ErrorInfo NotEnoughUserWalletMoneyExceptionHandler(HttpServletRequest req, Exception exception) {
        log.error(exception);
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Locale locale = userService.getUserLocaleForMobile(userEmail);
        return new ErrorInfo(req.getRequestURL(), exception, messageSource
                .getMessage("merchants.notEnoughWalletMoney", null,  locale));
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler({AbsentFinPasswordException.class, NotConfirmedFinPasswordException.class, WrongFinPasswordException.class, CheckFinPassException.class})
    @ResponseBody
    public ErrorInfo finPassExceptionHandler(HttpServletRequest req, Exception exception) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Locale locale = userService.getUserLocaleForMobile(userEmail);
        return new ErrorInfo(req.getRequestURL(), exception, messageSource.getMessage(((MerchantException)(exception)).getReason(), null,  locale));
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler({IncorrectPinException.class})
    @ResponseBody
    public ErrorInfo incorrectPinExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception, exception.getMessage());
    }


    @ResponseStatus(HttpStatus.ACCEPTED)
    @ExceptionHandler({PinCodeCheckNeedException.class})
    @ResponseBody
    public ErrorInfo pinCodeCheckNeedExceptionHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception, exception.getMessage(), ((PinCodeCheckNeedException)exception).getUuid());
    }


    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ErrorInfo OtherErrorsHandler(HttpServletRequest req, Exception exception) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Locale locale = userService.getUserLocaleForMobile(userEmail);
        if (exception instanceof MerchantException) {
            return new ErrorInfo(req.getRequestURL(), exception,
                    messageSource.getMessage(((MerchantException)(exception)).getReason(), null,  locale));
        }
        log.error(ExceptionUtils.getStackTrace(exception));
        return new ErrorInfo(req.getRequestURL(), exception);
    }
}
