package me.exrates.ngcontroller;

import lombok.extern.log4j.Log4j;
import me.exrates.controller.annotation.CheckActiveUserStatus;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.model.CreditsOperation;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.Payment;
import me.exrates.model.dto.TransferDto;
import me.exrates.model.dto.TransferRequestCreateDto;
import me.exrates.model.dto.TransferRequestFlatDto;
import me.exrates.model.dto.TransferRequestParamsDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.invoice.InvoiceActionTypeEnum;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.enums.invoice.TransferStatusEnum;
import me.exrates.model.userOperation.enums.UserOperationAuthority;
import me.exrates.ngcontroller.exception.NgDashboardException;
import me.exrates.security.exception.IncorrectPinException;
import me.exrates.service.InputOutputService;
import me.exrates.service.MerchantService;
import me.exrates.service.TransferService;
import me.exrates.service.UserService;
import me.exrates.service.exception.IllegalOperationTypeException;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.exception.UserNotFoundException;
import me.exrates.service.exception.UserOperationAccessException;
import me.exrates.service.notifications.G2faService;
import me.exrates.service.userOperation.UserOperationService;
import me.exrates.service.util.CharUtils;
import me.exrates.service.util.RateLimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.PRESENT_VOUCHER;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(value = "/info/private/v2/balances/transfer",
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Log4j
public class NgTransferController {

    private final RateLimitService rateLimitService;
    private final TransferService transferService;
    private final UserService userService;
    private final MerchantService merchantService;
    private final LocaleResolver localeResolver;
    private final UserOperationService userOperationService;
    private final InputOutputService inputOutputService;
    private final G2faService g2faService;
    private final MessageSource messageSource;

    @Autowired
    public NgTransferController(RateLimitService rateLimitService,
                                TransferService transferService,
                                UserService userService,
                                MerchantService merchantService,
                                LocaleResolver localeResolver,
                                UserOperationService userOperationService,
                                InputOutputService inputOutputService,
                                G2faService g2faService,
                                MessageSource messageSource) {
        this.rateLimitService = rateLimitService;
        this.transferService = transferService;
        this.userService = userService;
        this.merchantService = merchantService;
        this.localeResolver = localeResolver;
        this.userOperationService = userOperationService;
        this.inputOutputService = inputOutputService;
        this.g2faService = g2faService;
        this.messageSource = messageSource;
    }

    // /info/private/v2/balances/transfer/accept  PAYLOAD: {"CODE": "kdbfeyue743467"}

    /**
     * this method processes user refill request by using voucher
     *
     * @param params - map KEY - "CODE", VALUE - VOUCHER_CODE
     * @return 200 OK with body { userToNickName: string, currencyId: number,
     * userFromId: number, userToId: number, commission: Commission, notyAmount: string,
     * initialAmount: string, comissionAmount: string },
     * 404 - voucher not found
     * 400 - exceeded limits and or many invoices
     */
    @PostMapping(value = "/accept")
    public ResponseEntity<TransferDto> acceptTransfer(@RequestBody Map<String, String> params) {
        String email = getPrincipalEmail();
        if (!rateLimitService.checkLimitsExceed(email)) {
            log.info("Limits exceeded for user " + email);
            return ResponseEntity.badRequest().build();
        }
        InvoiceActionTypeEnum action = PRESENT_VOUCHER;
        List<InvoiceStatus> requiredStatus = TransferStatusEnum.getAvailableForActionStatusesList(action);
        if (requiredStatus.size() > 1) {
            log.info("To many invoices: " + requiredStatus.size());
            return ResponseEntity.badRequest().build();
        }
        String code = params.getOrDefault("CODE", "");
        Optional<TransferRequestFlatDto> dto = transferService
                .getByHashAndStatus(code, requiredStatus.get(0).getCode(), true);
        if (!dto.isPresent() || !transferService.checkRequest(dto.get(), email)) {
            rateLimitService.registerRequest(email);
            return ResponseEntity.notFound().build();
        }
        TransferRequestFlatDto flatDto = dto.get();
        flatDto.setInitiatorEmail(email);
        TransferDto resDto = transferService.performTransfer(flatDto, Locale.ENGLISH, action);
        return ResponseEntity.ok(resDto);
    }

    /*
     amount: "0.00165000"
     companyCommissionAmount: "0"
     companyCommissionRate: "(0,2%)"
     merchantCommissionAmount: "0.00000330"
     merchantCommissionRate: "(0,2%, но не менее 0 BTC)"
     resultAmount: "0.0016467"
     totalCommissionAmount: "0.0000033"
  */
    @CheckActiveUserStatus
    @RequestMapping(value = "/voucher/inner/commission", method = GET)
    @ResponseBody
    public Map<String, String> getCommissionsForInnerVoucher(
            @RequestParam("amount") BigDecimal amount,
            @RequestParam("currency") Integer currencyId,
            Locale locale) {

        MerchantCurrency merchant = merchantService.findMerchantForInnerTransferByCurrencyId(currencyId);
        Integer userId = userService.getIdByEmail(getPrincipalEmail());
        return transferService.correctAmountAndCalculateCommissionPreliminarily(userId, amount, currencyId,
                merchant.getMerchantId(), locale);
    }

    @CheckActiveUserStatus
    @RequestMapping(value = "/voucher/request/create", method = POST)
    @ResponseBody
    public Map<String, Object> createTransferRequest(@RequestBody TransferRequestParamsDto requestParamsDto,
                                                     HttpServletRequest servletRequest) {
        Integer userId = userService.getIdByEmail(getPrincipalEmail());
        Locale locale = localeResolver.resolveLocale(servletRequest);
        if (requestParamsDto.getOperationType() != OperationType.USER_TRANSFER) {
            throw new IllegalOperationTypeException(requestParamsDto.getOperationType().name());
        }
        boolean accessToOperationForUser = userOperationService.getStatusAuthorityForUserByOperation(userId, UserOperationAuthority.TRANSFER);
        if (!accessToOperationForUser) {
            throw new UserOperationAccessException(messageSource.getMessage("merchant.operationNotAvailable", null, locale));
        }
        if (requestParamsDto.getRecipient() != null && CharUtils.isCyrillic(requestParamsDto.getRecipient())) {
            throw new IllegalArgumentException(messageSource.getMessage(
                    "message.only.latin.symblos", null, locale));
        }
        MerchantCurrency merchant = merchantService.findMerchantForInnerTransferByCurrencyId(requestParamsDto.getCurrency());
        TransferStatusEnum beginStatus = (TransferStatusEnum) TransferStatusEnum.getBeginState();
        Payment payment = new Payment(requestParamsDto.getOperationType());
        payment.setCurrency(requestParamsDto.getCurrency());
        payment.setMerchant(merchant.getMerchantId());
        payment.setSum(requestParamsDto.getSum() == null ? 0 : requestParamsDto.getSum().doubleValue());
        payment.setRecipient(requestParamsDto.getRecipient());
        CreditsOperation creditsOperation = inputOutputService.prepareCreditsOperation(payment, getPrincipalEmail(), locale)
                .orElseThrow(InvalidAmountException::new);
        TransferRequestCreateDto transferRequest = new TransferRequestCreateDto(requestParamsDto, creditsOperation, beginStatus, locale);

        if (!g2faService.checkGoogle2faVerifyCode(requestParamsDto.getPin(), userId)) {
            throw new IncorrectPinException("Incorrect google auth code");
        }
        return transferService.createTransferRequest(transferRequest);
    }

    private String getPrincipalEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({NgDashboardException.class, UserNotFoundException.class,
            UserOperationAccessException.class, IllegalArgumentException.class, IncorrectPinException.class})
    @ResponseBody
    public ErrorInfo OtherErrorsHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }
}
