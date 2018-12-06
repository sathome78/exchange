package me.exrates.ngcontroller;

import com.google.common.base.Preconditions;
import jnr.ffi.annotations.In;
import me.exrates.controller.annotation.CheckActiveUserStatus;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Currency;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.Payment;
import me.exrates.model.User;
import me.exrates.model.Wallet;
import me.exrates.model.dto.WithdrawRequestCreateDto;
import me.exrates.model.dto.WithdrawRequestParamsDto;
import me.exrates.model.dto.ngDto.PinDto;
import me.exrates.model.dto.ngDto.WithdrawDataDto;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.enums.OperationType;
import me.exrates.model.enums.invoice.WithdrawStatusEnum;
import me.exrates.model.userOperation.enums.UserOperationAuthority;
import me.exrates.ngcontroller.exception.NgDashboardException;
import me.exrates.security.exception.IncorrectPinException;
import me.exrates.security.exception.PinCodeCheckNeedException;
import me.exrates.security.service.SecureService;
import me.exrates.service.CurrencyService;
import me.exrates.service.InputOutputService;
import me.exrates.service.MerchantService;
import me.exrates.service.RequestLimitExceededException;
import me.exrates.service.UserService;
import me.exrates.service.WalletService;
import me.exrates.service.WithdrawService;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.exception.UserNotFoundException;
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
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static me.exrates.model.enums.OperationType.OUTPUT;
import static me.exrates.model.enums.UserCommentTopicEnum.WITHDRAW_CURRENCY_WARNING;

@RestController
@RequestMapping(value = "/info/private/v2/balances/withdraw",
        consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,
        produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class NgWithdrawController {

    private static final Logger logger = LoggerFactory.getLogger(NgWithdrawController.class);

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

    // POST: /info/private/v2/balances/withdraw/request/create
    // model:
//    {
//       currency: number,
//       merchant: number,
//       sum: string,
//       destination: string,
//       destinationTag: string,
//       merchantImage: number,
//       operationType: string,
//       recipientBankName: string,
//       recipientBankCode: string,
//       userFullName: string,
//       remark: string,
//       walletNumber: string
//       securityCode: string
//    }
    @CheckActiveUserStatus
    @PostMapping(value = "/request/create")
    public ResponseEntity<Map<String, String>> createWithdrawalRequest(@RequestBody WithdrawRequestParamsDto requestParamsDto) {
        String email = getPrincipalEmail();
        boolean accessToOperationForUser = userOperationService.getStatusAuthorityForUserByOperation(userService.getIdByEmail(email), UserOperationAuthority.OUTPUT);
        if (!accessToOperationForUser) {
            throw new UserOperationAccessException(messageSource.getMessage("merchant.operationNotAvailable", null, Locale.ENGLISH));
        }
        if (!withdrawService.checkOutputRequestsLimit(requestParamsDto.getCurrency(), email)) {
            throw new RequestLimitExceededException(messageSource.getMessage("merchants.OutputRequestsLimit", null, Locale.ENGLISH));
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
                throw new IncorrectPinException("Incorrect Google 2FA oauth code: " + requestParamsDto.getSecurityCode());
            }
        } else {
            if (!userService.checkPin(getPrincipalEmail(), requestParamsDto.getSecurityCode(), NotificationMessageEventEnum.WITHDRAW)) {
                secureService.sendWithdrawPincode(user);
                throw new IncorrectPinException("Incorrect pin: " + requestParamsDto.getSecurityCode());
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
            logger.error("Failed to create withdraw request", e);
            return ResponseEntity.badRequest().build();
        }
    }

    // /info/private/v2/balances/withdraw/merchants/output?currency=BTC
    // response for BTC = https://api.myjson.com/bins/15aa4m
    // response for USD = https://api.myjson.com/bins/v8206
    @GetMapping(value = "/merchants/output")
    public ResponseEntity<WithdrawDataDto> outputCredits(@RequestParam("currency") String currencyName) {
        String email = getPrincipalEmail();
        try {
            OperationType operationType = OUTPUT;
            Currency currency = currencyService.findByName(currencyName);
            Wallet wallet = walletService.findByUserAndCurrency(userService.findByEmail(email), currency);
            BigDecimal minWithdrawSum = currencyService.retrieveMinLimitForRoleAndCurrency(userService.getUserRoleFromSecurityContext(), operationType, currency.getId());
            Integer scaleForCurrency = currencyService.getCurrencyScaleByCurrencyId(currency.getId()).getScaleForWithdraw();
            List<Integer> currenciesId = Collections.singletonList(currency.getId());
            List<MerchantCurrency> merchantCurrencyData = merchantService.getAllUnblockedForOperationTypeByCurrencies(currenciesId, operationType);
            List<String> warningCodeList = currencyService.getWarningForCurrency(currency.getId(), WITHDRAW_CURRENCY_WARNING);
            WithdrawDataDto withdrawDataDto = WithdrawDataDto
                    .builder()
                    .activeBalance(wallet.getActiveBalance())
                    .currenciesId(Collections.singletonList(currency.getId()))
                    .operationType(operationType)
                    .minWithdrawSum(minWithdrawSum)
                    .merchantCurrencyData(merchantCurrencyData)
                    .scaleForCurrency(scaleForCurrency)
                    .warningCodeList(warningCodeList)
                    .build();
            return ResponseEntity.ok(withdrawDataDto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // GET: /info/private/v2/balances/withdraw/request/pin
    @CheckActiveUserStatus
    @GetMapping(value = "/request/pin")
    public ResponseEntity<Void> sendUserPincode() {
        try {
            User user = userService.findByEmail(getPrincipalEmail());
            if (!g2faService.isGoogleAuthenticatorEnable(user.getId())) {
                secureService.sendWithdrawPincode(user);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Failed to send user email", e);
            return ResponseEntity.badRequest().build();
        }
    }

    // Not used for now
//    // POST: /info/private/v2/balances/withdraw/request/pin?pin=123456
//    @CheckActiveUserStatus
//    @PostMapping(value = "/request/pin")
//    @ResponseBody
//    public ResponseEntity<Void> withdrawRequestCheckPin(@RequestParam String pin) {
//        logger.debug("withdraw pin {}", pin);
//        User user = userService.findByEmail(getPrincipalEmail());
//        if (g2faService.isGoogleAuthenticatorEnable(user.getId())) {
//            if (!g2faService.checkGoogle2faVerifyCode(pin, user.getId())) {
//                throw new IncorrectPinException("Incorrect pin: " + pin);
//            }
//        } else {
//            if (!userService.checkPin(getPrincipalEmail(), pin, NotificationMessageEventEnum.WITHDRAW)) {
//                secureService.sendWithdrawPincode(user);
//                throw new IncorrectPinException("Incorrect pin: " + pin);
//            }
//        }
//        return ResponseEntity.ok().build();
//    }

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
