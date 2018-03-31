package me.exrates.controller.ngContorollers;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import lombok.extern.log4j.Log4j2;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.controller.exception.RequestsLimitExceedException;
import me.exrates.model.*;
import me.exrates.model.Currency;
import me.exrates.model.dto.*;
import me.exrates.model.dto.ngDto.TransferMerchantsDataDto;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.invoice.InvoiceActionTypeEnum;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.enums.invoice.TransferStatusEnum;
import me.exrates.model.exceptions.InvoiceActionIsProhibitedForCurrencyPermissionOperationException;
import me.exrates.model.exceptions.InvoiceActionIsProhibitedForNotHolderException;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.security.exception.IncorrectPinException;
import me.exrates.security.exception.PinCodeCheckNeedException;
import me.exrates.security.service.SecureService;
import me.exrates.service.*;
import me.exrates.service.exception.IllegalOperationTypeException;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.exception.NotEnoughUserWalletMoneyException;
import me.exrates.service.exception.invoice.InvoiceNotFoundException;
import me.exrates.service.util.CharUtils;
import me.exrates.service.util.RateLimitService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;

import static me.exrates.model.enums.OperationType.USER_TRANSFER;
import static me.exrates.model.enums.UserCommentTopicEnum.TRANSFER_CURRENCY_WARNING;
import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.PRESENT_VOUCHER;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by Maks on 08.02.2018.
 */
@Log4j2
@RestController
@RequestMapping("/info/private/transfer")
public class TransferNgController {


    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private UserService userService;
    @Autowired
    private WalletService walletService;
    @Autowired
    private TransferService transferService;
    @Autowired
    private RateLimitService rateLimitService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private SecureService secureService;
    @Autowired
    private InputOutputService inputOutputService;

    private Map<UUID, TransferRequestCreateDto> unconfirmedtransfers = new ConcurrentReferenceHashMap<>();


    // todo - received dto contains property wallet, which contains user with such properties
    // as e.g.    "id": 36149,
    //            "nickname": "angular",
    //            "email": "angular@i.ua",
    //            "phone": null,
    //            "status": "ACTIVE",
    //            "userStatus": "REGISTERED",
    //            "password": "$2a$10$QpOOkz98DD/PcEzYA8grcem4w61nXNnH93TrT.DgVUJRmxK5xaQB.",
    //            "finpassword": "$2a$10$5dmzXQbWwRNYw9bLTSM.TOq7K.bxOzohrFwOWk26YMSxfHPl8MM0.",
    //            "regdate": "2018-03-06",
    //            "confirmPassword": null,
    //            "confirmFinPassword": null,
    //            "readRules": false,
    //            "role": "ADMINISTRATOR",
    //            "parentEmail": "kd3@bigmir.net",
    //            "prefferedLang": null,
    //            "ip": null
    // to consider if its necessary !!!!!
    // as to me property wallet is redundant as well as payment

    @RequestMapping(value = "/currency/{name}", method = GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public TransferMerchantsDataDto findAllTransferMerchantCurrencies(@PathVariable("name") String currencyName) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        OperationType operationType = USER_TRANSFER;
        Currency currency = currencyService.findByName(currencyName);
        TransferMerchantsDataDto dto = new TransferMerchantsDataDto();
        dto.setCurrency(currency);
        Wallet wallet = walletService.findByUserAndCurrency(userService.findByEmail(userEmail), currency);
        dto.setWallet(wallet);
        dto.setBalance(BigDecimalProcessing.formatNonePoint(wallet.getActiveBalance(), false));
        Payment payment = new Payment();
        payment.setOperationType(operationType);
        dto.setPayment(payment);
        BigDecimal minTransferSum = currencyService.retrieveMinLimitForRoleAndCurrency(userService.getUserRoleFromSecurityContext(), operationType, currency.getId());
        dto.setMinTransferSum(minTransferSum);
        Integer scaleForCurrency = currencyService.getCurrencyScaleByCurrencyId(currency.getId()).getScaleForWithdraw();
        dto.setScaleForCurrency(scaleForCurrency);
        List<Integer> currenciesId = Collections.singletonList(currency.getId());
        List<MerchantCurrency> merchantCurrencyData = merchantService.getAllUnblockedForOperationTypeByCurrencies(currenciesId, operationType);
        transferService.retrieveAdditionalParamsForWithdrawForMerchantCurrencies(merchantCurrencyData);
        dto.setMerchantCurrencyData(merchantCurrencyData);
     /* List<String> initialWarningCodeList = currencyService.getWarningForCurrency(currency.getId(), INITIAL_TRANSFER_CURRENCY_WARNING);
      modelAndView.addObject("initialWarningCodeList", initialWarningCodeList);*/
        List<String> warningCodeList = currencyService.getWarningsByTopic(TRANSFER_CURRENCY_WARNING);
        dto.setWarningCodeList(warningCodeList);/*todo: what is it?*/
        return dto;
    }

    @RequestMapping(value = "/request/create", method = POST)
    @ResponseBody
    public Map<String, Object> createTransferRequest(
            @RequestBody TransferRequestParamsDto requestParamsDto) throws UnsupportedEncodingException {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Locale locale = userService.getUserLocaleForMobile(userEmail);
        if (requestParamsDto.getOperationType() != USER_TRANSFER) {
            throw new IllegalOperationTypeException(requestParamsDto.getOperationType().name());
        }
        if (requestParamsDto.getRecipient() != null && CharUtils.isCyrillic(requestParamsDto.getRecipient())) {
            throw new IllegalArgumentException(messageSource.getMessage(
                    "message.only.latin.symblos", null, locale));
        }
        TransferStatusEnum beginStatus = (TransferStatusEnum) TransferStatusEnum.getBeginState();
        Payment payment = new Payment(requestParamsDto.getOperationType());
        payment.setCurrency(requestParamsDto.getCurrency());
        payment.setMerchant(requestParamsDto.getMerchant());
        payment.setSum(requestParamsDto.getSum() == null ? 0 : requestParamsDto.getSum().doubleValue());
        payment.setRecipient(requestParamsDto.getRecipient());
        CreditsOperation creditsOperation = inputOutputService.prepareCreditsOperation(payment, userEmail)
                .orElseThrow(InvalidAmountException::new);
        TransferRequestCreateDto transferRequest = new TransferRequestCreateDto(requestParamsDto, creditsOperation, beginStatus, locale);
        try {
            secureService.checkEventAdditionalPin(locale, userEmail,
                    NotificationMessageEventEnum.TRANSFER, getAmountWithCurrency(transferRequest));
        } catch (PinCodeCheckNeedException e) {
            UUID orderKey = UUID.randomUUID();
            unconfirmedtransfers.put(orderKey, transferRequest);
            e.setUuid(orderKey.toString());
            throw e;
        }
        return transferService.createTransferRequest(transferRequest);
    }

    @RequestMapping(value = "/accept", method = POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> acceptTransfer(@Valid @RequestBody TransferCodeDto codeDto) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Locale locale = userService.getUserLocaleForMobile(userEmail);
        log.debug("code {}", codeDto);
        /*todo: uncomment on prod*/
       /* if (!rateLimitService.checkLimitsExceed(userEmail)) {
            throw new RequestsLimitExceedException();
        }*/
        InvoiceActionTypeEnum action = PRESENT_VOUCHER;
        List<InvoiceStatus> requiredStatus = TransferStatusEnum.getAvailableForActionStatusesList(action);
        if(requiredStatus.size() > 1) {
            throw new RuntimeException("voucher processing error");
        }
        Optional<TransferRequestFlatDto> dto =  transferService
                .getByHashAndStatus(codeDto.getCode(), requiredStatus.get(0).getCode(), true);
        if (!dto.isPresent() || !transferService.checkRequest(dto.get(), userEmail)) {
            rateLimitService.registerRequest(userEmail);
            throw new InvoiceNotFoundException(messageSource.getMessage(
                    "voucher.invoice.not.found", null, locale));
        }
        TransferRequestFlatDto flatDto = dto.get();
        flatDto.setInitiatorEmail(userEmail);
        transferService.performTransfer(flatDto, locale, action);
        JsonObject result =  new JsonObject();
        result.addProperty("result", messageSource.getMessage("message.receive.voucher" ,
                new String[]{BigDecimalProcessing.formatLocaleFixedDecimal(flatDto.getAmount(), locale, 4),
                        currencyService.getCurrencyName(flatDto.getCurrencyId())}, locale));
        return new ResponseEntity<>(result.toString(), HttpStatus.OK);
    }

    @RequestMapping(value = "/request/pin", method = POST)
    @ResponseBody
    public Map<String, Object> withdrawRequestCheckPin(@RequestBody Map <String, String> body) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Locale locale = userService.getUserLocaleForMobile(userEmail);
        UUID keyUUID = UUID.fromString(body.get("key"));
        TransferRequestCreateDto withdrawRequestCreateDto = unconfirmedtransfers.get(keyUUID);
        Preconditions.checkArgument(body.get("pin").length() > 2 && body.get("pin").length() < 15);
        Preconditions.checkNotNull(withdrawRequestCreateDto, "No order found by key");
        if (userService.checkPin(userEmail, body.get("pin"), NotificationMessageEventEnum.TRANSFER)) {
            Preconditions.checkArgument(withdrawRequestCreateDto.getUserEmail().equals(userEmail), "Yhe withdrawal is not yours");
            unconfirmedtransfers.remove(keyUUID);
            return transferService.createTransferRequest(withdrawRequestCreateDto);
        } else {
            String res = secureService.resendEventPin(locale, userEmail,
                    NotificationMessageEventEnum.TRANSFER, getAmountWithCurrency(withdrawRequestCreateDto));
            throw new IncorrectPinException(res);
        }
    }

    private String getAmountWithCurrency(TransferRequestCreateDto dto) {
        return new StringJoiner(" ", dto.getAmount().toString(), dto.getCurrencyName()).toString();
    }


    @RequestMapping(value = "/request/hash/{id}", method = POST, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getHashForUser(@PathVariable Integer id) {
        try {
            String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            String hash = transferService.getHash(id, userEmail);
            return ResponseEntity.ok(hash);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value = "/request/revoke/{id}", method = POST)
    public ResponseEntity<Void> revokeVoucherByUser(@PathVariable Integer id) {
        try {
            String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
            transferService.revokeByUser(id, userEmail);
            return  ResponseEntity.ok().build();
        } catch (Exception exc) {
            return  ResponseEntity.badRequest().build();
        }
    }

    @RequestMapping(value = "/commission", method = GET)
    @ResponseBody
    public Map<String, String> getCommissions(
            @RequestParam("amount") BigDecimal amount,
            @RequestParam("currency") Integer currencyId,
            @RequestParam("merchant") Integer merchant) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Locale locale = userService.getUserLocaleForMobile(userEmail);
        Integer userId = userService.getIdByEmail(userEmail);
        return transferService.correctAmountAndCalculateCommissionPreliminarily(userId, amount, currencyId, merchant, locale);
    }

    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    @ExceptionHandler(InvoiceNotFoundException.class)
    @ResponseBody
    public ErrorInfo NotFoundExceptionHandler(HttpServletRequest req, Exception exception) {
        log.error(exception);
        return new ErrorInfo(req.getRequestURL(), exception);
    }

    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    @ExceptionHandler(RequestsLimitExceedException.class)
    @ResponseBody
    public ErrorInfo RequestsLimitExceedExceptionHandler(HttpServletRequest req, Exception exception) {
        log.error(exception);
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Locale locale = userService.getUserLocaleForMobile(userEmail);
        return new ErrorInfo(req.getRequestURL(), exception,
                messageSource.getMessage("voucher.request.limit.exceed", null, locale));
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
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        Locale locale = userService.getUserLocaleForMobile(userEmail);
        return new ErrorInfo(req.getRequestURL(), exception, messageSource
                .getMessage("merchants.notEnoughWalletMoney", null,  locale));
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
        log.error(ExceptionUtils.getStackTrace(exception));
        return new ErrorInfo(req.getRequestURL(), exception);
    }
}
