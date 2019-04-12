package me.exrates.ngcontroller;

import lombok.extern.log4j.Log4j;
import me.exrates.controller.annotation.CheckActiveUserStatus;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.dao.exception.notfound.UserNotFoundException;
import me.exrates.model.CreditsOperation;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.Payment;
import me.exrates.model.User;
import me.exrates.model.constants.ErrorApiTitles;
import me.exrates.model.dto.PinOrderInfoDto;
import me.exrates.model.dto.TransferDto;
import me.exrates.model.dto.TransferRequestCreateDto;
import me.exrates.model.dto.TransferRequestFlatDto;
import me.exrates.model.dto.TransferRequestParamsDto;
import me.exrates.model.enums.MerchantProcessType;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.TransferTypeVoucher;
import me.exrates.model.enums.invoice.InvoiceActionTypeEnum;
import me.exrates.model.enums.invoice.InvoiceStatus;
import me.exrates.model.enums.invoice.TransferStatusEnum;
import me.exrates.model.exceptions.UnsupportedTransferProcessTypeException;
import me.exrates.model.ngExceptions.NgDashboardException;
import me.exrates.model.ngExceptions.NgResponseException;
import me.exrates.model.ngModel.response.ResponseCustomError;
import me.exrates.model.ngModel.response.ResponseModel;
import me.exrates.model.userOperation.enums.UserOperationAuthority;
import me.exrates.security.exception.IncorrectPinException;
import me.exrates.security.service.CheckUserAuthority;
import me.exrates.security.service.SecureService;
import me.exrates.service.CurrencyService;
import me.exrates.service.InputOutputService;
import me.exrates.service.MerchantService;
import me.exrates.service.TransferService;
import me.exrates.service.UserService;
import me.exrates.service.exception.IllegalOperationTypeException;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.exception.UserOperationAccessException;
import me.exrates.service.notifications.G2faService;
import me.exrates.service.userOperation.UserOperationService;
import me.exrates.service.util.CharUtils;
import me.exrates.service.util.RateLimitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.PRESENT_VOUCHER;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@PreAuthorize("!hasRole('ICO_MARKET_MAKER')")
@RequestMapping(value = "/api/private/v2/balances/transfer",
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@Log4j
@PropertySource(value = {"classpath:/angular.properties"})
public class NgTransferController {

    private static final Logger logger = LoggerFactory.getLogger(NgTransferController.class);

    private final RateLimitService rateLimitService;
    private final TransferService transferService;
    private final UserService userService;
    private final MerchantService merchantService;
    private final LocaleResolver localeResolver;
    private final UserOperationService userOperationService;
    private final InputOutputService inputOutputService;
    private final MessageSource messageSource;
    private final G2faService g2faService;
    private final SecureService secureService;
    private final CurrencyService currencyService;

    @Value("${dev.mode}")
    private boolean DEV_MODE;

    @Autowired
    public NgTransferController(RateLimitService rateLimitService,
                                TransferService transferService,
                                UserService userService,
                                MerchantService merchantService,
                                LocaleResolver localeResolver,
                                UserOperationService userOperationService,
                                InputOutputService inputOutputService,
                                MessageSource messageSource,
                                SecureService secureService,
                                G2faService g2faService,
                                CurrencyService currencyService) {
        this.rateLimitService = rateLimitService;
        this.transferService = transferService;
        this.userService = userService;
        this.merchantService = merchantService;
        this.localeResolver = localeResolver;
        this.userOperationService = userOperationService;
        this.inputOutputService = inputOutputService;
        this.messageSource = messageSource;
        this.g2faService = g2faService;
        this.secureService = secureService;
        this.currencyService = currencyService;
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
    @CheckActiveUserStatus
    @PostMapping(value = "/accept")
    @CheckUserAuthority(authority = UserOperationAuthority.TRANSFER)
    public ResponseEntity<TransferDto> acceptTransfer(@RequestBody Map<String, String> params) {
        String email = getPrincipalEmail();
        if (!rateLimitService.checkLimitsExceed(email)) {
            log.info("Limits exceeded for user " + email);
            String message = String.format("Limits exceeded for user %s", email);
            throw new NgResponseException(ErrorApiTitles.FAILED_ACCEPT_TRANSFER, message);
        }
        InvoiceActionTypeEnum action = PRESENT_VOUCHER;
        List<InvoiceStatus> requiredStatus = TransferStatusEnum.getAvailableForActionStatusesList(action);
        if (requiredStatus.size() > 1) {
            log.info("To many invoices: " + requiredStatus.size());
            String message = String.format("To many invoices: %s", requiredStatus.size());
            throw new NgResponseException(ErrorApiTitles.FAILED_ACCEPT_TRANSFER, message);
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
    @RequestMapping(value = "/voucher/commission", method = GET)
    @ResponseBody
    public Map<String, String> getCommissionsForInnerVoucher(
            @RequestParam("amount") BigDecimal amount,
            @RequestParam("currency") Integer currencyId,
            @RequestParam("type") String type,
            Locale locale) {
        TransferTypeVoucher transferType = TransferTypeVoucher.convert(type);
        MerchantCurrency merchant = merchantService.findMerchantForTransferByCurrencyId(currencyId, transferType);
        Integer userId = userService.getIdByEmail(getPrincipalEmail());
        return transferService.correctAmountAndCalculateCommissionPreliminarily(userId, amount, currencyId,
                merchant.getMerchantId(), locale);
    }

    @CheckActiveUserStatus
    @RequestMapping(value = "/voucher/request/create", method = POST)
    @CheckUserAuthority(authority = UserOperationAuthority.TRANSFER)
    @ResponseBody
    public Map<String, Object> createTransferRequest(@RequestBody TransferRequestParamsDto requestParamsDto,
                                                     HttpServletRequest servletRequest) {
        String email = getPrincipalEmail();
        Integer userId = userService.getIdByEmail(email);
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
        User user = userService.findByEmail(email);
        if (g2faService.isGoogleAuthenticatorEnable(user.getId())) {
            if (!g2faService.checkGoogle2faVerifyCode(requestParamsDto.getPin(), user.getId())) {
                throw new IncorrectPinException("Incorrect Google 2FA oauth code: " + requestParamsDto.getPin());
            }
        } else {
            if (!userService.checkPin(getPrincipalEmail(), requestParamsDto.getPin(), NotificationMessageEventEnum.WITHDRAW)) {
                secureService.sendWithdrawPincode(user);
                throw new IncorrectPinException("Incorrect pin: " + requestParamsDto.getPin());
            }
        }

        TransferTypeVoucher transferType = TransferTypeVoucher.convert(requestParamsDto.getType());
        MerchantCurrency merchant = merchantService.findMerchantForTransferByCurrencyId(requestParamsDto.getCurrency(),
                transferType);
        TransferStatusEnum beginStatus = (TransferStatusEnum) TransferStatusEnum.getBeginState();
        Payment payment = new Payment(requestParamsDto.getOperationType());
        payment.setCurrency(requestParamsDto.getCurrency());
        payment.setMerchant(merchant.getMerchantId());
        payment.setSum(requestParamsDto.getSum() == null ? 0 : requestParamsDto.getSum().doubleValue());
        payment.setRecipient(requestParamsDto.getRecipient());
        CreditsOperation creditsOperation = inputOutputService.prepareCreditsOperation(payment, email, locale)
                .orElseThrow(InvalidAmountException::new);
        requestParamsDto.setMerchant(merchant.getMerchantId());
        TransferRequestCreateDto transferRequest = new TransferRequestCreateDto(requestParamsDto, creditsOperation, beginStatus, locale);
        return transferService.createTransferRequest(transferRequest);
    }

    @GetMapping("/check_email")
    public ResponseModel<Boolean> checkEmailForTransfer(@RequestParam("email") String email) {
        String principalEmail = getPrincipalEmail();

        if (email.equalsIgnoreCase(principalEmail)) {
            ResponseCustomError error =
                    new ResponseCustomError(messageSource.getMessage("transfer.email.yourself", null, Locale.ENGLISH));
            return new ResponseModel<>(false, error);
        }

        if (!userService.userExistByEmail(email)) {
            ResponseCustomError error = new ResponseCustomError(messageSource.getMessage("transfer.email.not_found",
                    null, Locale.ENGLISH));
            return new ResponseModel<>(false, error);
        }
        return new ResponseModel<>(true);

    }

    @GetMapping("/get_minimal_sum")
    public ResponseModel getMinimalTransferSum(@RequestParam("currency_id") int currencyId,
                                               @RequestParam("type") String type) {
        BigDecimal minSum = BigDecimal.ZERO;
        TransferTypeVoucher transferType = TransferTypeVoucher.convert(type);
        MerchantCurrency merchant = merchantService.findMerchantForTransferByCurrencyId(currencyId,
                transferType);

        if (merchant != null) {
            minSum = merchant.getMinSum();
        }

        return new ResponseModel<>(minSum);
    }

    @GetMapping("/currencies")
    public ResponseModel getAllCurrenciesForTransfer() {
        return new ResponseModel<>(currencyService.getCurrencies(MerchantProcessType.TRANSFER));
    }

    @CheckActiveUserStatus
    @PostMapping(value = "/request/pin")
    @CheckUserAuthority(authority = UserOperationAuthority.TRANSFER)
    public ResponseEntity<Void> sendUserPinCode(@RequestBody @Valid PinOrderInfoDto pinOrderInfoDto) {
        try {
            User user = userService.findByEmail(getPrincipalEmail());
            if (!g2faService.isGoogleAuthenticatorEnable(user.getId())) {
                secureService.sendTransferPinCode(user, pinOrderInfoDto.getAmount().toPlainString(),
                        pinOrderInfoDto.getCurrencyName());
                return ResponseEntity.status(HttpStatus.CREATED).build();
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Failed to send user email", e);
            String message = String.format("Failed to send user email");
            throw new NgResponseException(ErrorApiTitles.FAILED_TO_SEND_USER_EMAIL, message);
        }
    }

    private String getPrincipalEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({NgDashboardException.class, UserNotFoundException.class,
            UserOperationAccessException.class, IllegalArgumentException.class, IncorrectPinException.class,
            UnsupportedTransferProcessTypeException.class})
    @ResponseBody
    public ErrorInfo OtherErrorsHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }
}
