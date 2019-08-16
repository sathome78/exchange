package me.exrates.ngcontroller;

import lombok.extern.log4j.Log4j2;
import me.exrates.controller.annotation.CheckActiveUserStatus;
import me.exrates.model.CurrencyLimit;
import me.exrates.service.annotation.LogIp;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.dao.exception.notfound.UserNotFoundException;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Currency;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.Payment;
import me.exrates.model.User;
import me.exrates.model.Wallet;
import me.exrates.model.constants.ErrorApiTitles;
import me.exrates.model.dto.PinOrderInfoDto;
import me.exrates.model.dto.WithdrawRequestCreateDto;
import me.exrates.model.dto.WithdrawRequestParamsDto;
import me.exrates.model.dto.ngDto.WithdrawDataDto;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.UserEventEnum;
import me.exrates.model.enums.UserRole;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;
import me.exrates.model.ngExceptions.NgDashboardException;
import me.exrates.model.ngExceptions.NgResponseException;
import me.exrates.model.userOperation.enums.UserOperationAuthority;
import me.exrates.security.exception.IncorrectPinException;
import me.exrates.security.service.CheckUserAuthority;
import me.exrates.security.service.SecureService;
import me.exrates.service.CurrencyService;
import me.exrates.service.InputOutputService;
import me.exrates.service.MerchantService;
import me.exrates.service.RequestLimitExceededException;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.WithdrawService;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.exception.UserOperationAccessException;
import me.exrates.service.notifications.G2faService;
import me.exrates.service.userOperation.UserOperationService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.util.Objects.isNull;
import static me.exrates.model.enums.OperationType.OUTPUT;
import static me.exrates.model.enums.UserCommentTopicEnum.WITHDRAW_CURRENCY_WARNING;

@Log4j2
@RestController
@PreAuthorize("!hasRole('ICO_MARKET_MAKER')")
@RequestMapping(value = "/api/private/v2/balances/withdraw",
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class NgWithdrawController {

    private final CurrencyService currencyService;
    private final G2faService g2faService;
    private final InputOutputService inputOutputService;
    private final MerchantService merchantService;
    private final MessageSource messageSource;
    private final SecureService secureService;
    private final UserOperationService userOperationService;
    private final UserService userService;
    private final WalletService walletService;
    private final WithdrawService withdrawService;

    @Autowired
    public NgWithdrawController(CurrencyService currencyService,
                                G2faService g2faService,
                                InputOutputService inputOutputService,
                                MerchantService merchantService,
                                MessageSource messageSource,
                                SecureService secureService,
                                UserOperationService userOperationService,
                                UserService userService,
                                WalletService walletService,
                                WithdrawService withdrawService) {
        this.currencyService = currencyService;
        this.g2faService = g2faService;
        this.inputOutputService = inputOutputService;
        this.merchantService = merchantService;
        this.messageSource = messageSource;
        this.secureService = secureService;
        this.userOperationService = userOperationService;
        this.userService = userService;
        this.walletService = walletService;
        this.withdrawService = withdrawService;
    }

    // POST: /api/private/v2/balances/withdraw/request/create
    @LogIp(event = UserEventEnum.WITHDRAW)
    @CheckActiveUserStatus
    @PostMapping(value = "/request/create")
    @CheckUserAuthority(authority = UserOperationAuthority.OUTPUT)
    public ResponseEntity<Map<String, String>> createWithdrawalRequest(@RequestBody WithdrawRequestParamsDto requestParamsDto) {
        String email = getPrincipalEmail();
        boolean accessToOperationForUser = userOperationService.getStatusAuthorityForUserByOperation(userService.getIdByEmail(email), UserOperationAuthority.OUTPUT);
        if (!accessToOperationForUser) {
            throw new UserOperationAccessException();
        }
        if (!withdrawService.checkOutputRequestsLimit(requestParamsDto.getCurrency(), email, requestParamsDto.getSum())) {
            throw new RequestLimitExceededException();
        }
        if (!StringUtils.isEmpty(requestParamsDto.getDestinationTag())) {
            merchantService.checkDestinationTag(requestParamsDto.getMerchant(), requestParamsDto.getDestinationTag());
        }
        if (StringUtils.isEmpty(requestParamsDto.getSecurityCode())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        User user = userService.findByEmail(email);
        if (g2faService.isGoogleAuthenticatorEnable(user.getId())) {
            if (!g2faService.checkGoogle2faVerifyCode(requestParamsDto.getSecurityCode(), user.getId())) {
                throw new IncorrectPinException();
            }
        } else {
            if (!userService.checkPin(getPrincipalEmail(), requestParamsDto.getSecurityCode(), NotificationMessageEventEnum.WITHDRAW)) {
                Currency currency = currencyService.getById(requestParamsDto.getCurrency());
                secureService.sendWithdrawPinCode(user, requestParamsDto.getSum().toPlainString(), currency.getName());
                throw new IncorrectPinException();
            }
        }
        try {
            WithdrawStatusEnum beginStatus = (WithdrawStatusEnum) WithdrawStatusEnum.getBeginState();
            Payment payment = new Payment(OUTPUT);
            payment.setCurrency(requestParamsDto.getCurrency());
            payment.setMerchant(requestParamsDto.getMerchant());
            payment.setSum(requestParamsDto.getSum().doubleValue());
            payment.setDestination(requestParamsDto.getDestination());
            payment.setDestinationTag(requestParamsDto.getDestinationTag());
            CreditsOperation creditsOperation = inputOutputService.prepareCreditsOperation(payment, email, Locale.ENGLISH)
                    .orElseThrow(InvalidAmountException::new);
            WithdrawRequestCreateDto withdrawRequestCreateDto = new WithdrawRequestCreateDto(requestParamsDto, creditsOperation, beginStatus);
            Map<String, String> withdrawalResponse = withdrawService.createWithdrawalRequest(withdrawRequestCreateDto, Locale.ENGLISH);
            return ResponseEntity.ok(withdrawalResponse);
        } catch (InvalidAmountException e) {
            log.error("Failed to create withdraw request", e);
            throw new NgResponseException(ErrorApiTitles.FAILED_TO_CREATE_WITHDRAW_REQUEST, e.getMessage());
        }
    }

    // /api /private/v2/balances/withdraw/merchants/output?currency=BTC
    // response for BTC = https://api.myjson.com/bins/15aa4m
    // response for USD = https://api.myjson.com/bins/v8206
    @GetMapping(value = "/merchants/output")
    @CheckUserAuthority(authority = UserOperationAuthority.OUTPUT)
    public ResponseEntity<WithdrawDataDto> outputCredits(@RequestParam("currency") String currencyName) {
        String email = getPrincipalEmail();
        try {
            OperationType operationType = OUTPUT;

            Currency currency = currencyService.findByName(currencyName);
            User user = userService.findByEmail(email);
            Wallet wallet = walletService.findByUserAndCurrency(user, currency);
            UserRole userRole = userService.getUserRoleFromSecurityContext();

            CurrencyLimit currencyLimit = currencyService.getCurrencyLimit(currency.getId(), operationType.type, userRole.getRole());
            BigDecimal withdrawnToday = withdrawService.getDailyWithdrawalSum(user.getEmail(), currency.getId());
            Integer scaleForCurrency = currencyService.getCurrencyScaleByCurrencyId(currency.getId()).getScaleForWithdraw();
            List<Integer> currenciesId = Collections.singletonList(currency.getId());
            List<MerchantCurrency> merchantCurrencyData = merchantService.getAllUnblockedForOperationTypeByCurrencies(currenciesId, operationType);

            //check additional field and fill it
            for (MerchantCurrency merchantCurrency : merchantCurrencyData) {
                withdrawService.setAdditionalData(merchantCurrency, user);
            }

            List<String> warningCodeList = currencyService.getWarningForCurrency(currency.getId(), WITHDRAW_CURRENCY_WARNING);

            BigDecimal leftRequestSum = withdrawService.getLeftOutputRequestsCount(currency.getId(), email).max(BigDecimal.ZERO);

            WithdrawDataDto withdrawDataDto = WithdrawDataDto
                    .builder()
                    .activeBalance(isNull(wallet) ? BigDecimal.ZERO : wallet.getActiveBalance())
                    .currenciesId(Collections.singletonList(currency.getId()))
                    .operationType(operationType)
                    .minWithdrawSum(currencyLimit.getMinSum())
                    .maxDailyRequestSum(currencyLimit.getMaxDailyRequest())
                    .leftRequestSum(leftRequestSum)
                    .maxDailyWithdrawAmount(currencyLimit.getMaxSum())
                    .withdrawnToday(withdrawnToday)
                    .leftDailyWithdrawAmount((currencyLimit.getMaxSum().subtract(withdrawnToday)).max(BigDecimal.ZERO))
                    .merchantCurrencyData(merchantCurrencyData)
                    .scaleForCurrency(scaleForCurrency)
                    .warningCodeList(warningCodeList)
                    .build();
            return ResponseEntity.ok(withdrawDataDto);
        } catch (Exception ex) {
            log.error("outputCredits error:", ex);
            throw new NgResponseException(ErrorApiTitles.FAILED_OUTPUT_CREDITS, ex.getMessage());
        }
    }

    // GET: /api/private/v2/balances/withdraw/request/pin
    // 201 - pincode is sent to user email
    // 200 - no pincode use google oauth
    @CheckActiveUserStatus
    @PostMapping(value = "/request/pin")
    @CheckUserAuthority(authority = UserOperationAuthority.OUTPUT)
    public ResponseEntity<Void> sendUserPinCode(@RequestBody @Valid PinOrderInfoDto pinOrderInfoDto) {
        try {
            User user = userService.findByEmail(getPrincipalEmail());
            if (!g2faService.isGoogleAuthenticatorEnable(user.getId())) {
                secureService.sendWithdrawPinCode(user, pinOrderInfoDto.getAmount().toPlainString(),
                        pinOrderInfoDto.getCurrencyName());
                return ResponseEntity.status(HttpStatus.CREATED).build();
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to send pin code on user email", e);
            throw new NgResponseException(ErrorApiTitles.FAILED_TO_SEND_PIN_CODE_ON_USER_EMAIL, "error.send_message_user");
        }
    }

    @GetMapping("/commission")
    public Map<String, String> getCommissions(
            @RequestParam("amount") BigDecimal amount,
            @RequestParam("currency") Integer currencyId,
            @RequestParam("merchant") Integer merchantId,
            @RequestParam(value = "memo", required = false) String memo) {
        Integer userId = userService.getIdByEmail(getPrincipalEmail());
        if (!StringUtils.isEmpty(memo)) {
            merchantService.checkDestinationTag(merchantId, memo);
        }
        return withdrawService.correctAmountAndCalculateCommissionPreliminarily(userId, amount, currencyId,
                merchantId, Locale.ENGLISH, memo);
    }

    private String getPrincipalEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({NgDashboardException.class, UserNotFoundException.class, InvalidAmountException.class,
            UserOperationAccessException.class, IllegalArgumentException.class, IncorrectPinException.class})
    @ResponseBody
    public ErrorInfo OtherErrorsHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }
}
