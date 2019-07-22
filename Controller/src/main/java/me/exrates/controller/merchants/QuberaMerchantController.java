package me.exrates.controller.merchants;

import me.exrates.controller.exception.ErrorInfo;
import me.exrates.model.User;
import me.exrates.model.constants.ErrorApiTitles;
import me.exrates.model.dto.AccountQuberaResponseDto;
import me.exrates.model.dto.ngDto.PinDtoSimple;
import me.exrates.model.dto.qubera.AccountInfoDto;
import me.exrates.model.dto.qubera.ExternalPaymentShortDto;
import me.exrates.model.dto.qubera.QuberaLog;
import me.exrates.model.dto.qubera.QuberaPaymentInfoDto;
import me.exrates.model.dto.qubera.RequestConfirmExternalPaymentDto;
import me.exrates.model.dto.qubera.responses.ExternalPaymentResponseDto;
import me.exrates.model.enums.NotificationMessageEventEnum;
import me.exrates.model.ngExceptions.NgDashboardException;
import me.exrates.model.ngExceptions.NgResponseException;
import me.exrates.model.ngModel.response.ResponseModel;
import me.exrates.security.exception.IncorrectPinException;
import me.exrates.security.service.SecureService;
import me.exrates.service.InputOutputService;
import me.exrates.service.QuberaService;
import me.exrates.service.UserService;
import me.exrates.service.exception.RefillRequestAlreadyAcceptedException;
import me.exrates.service.notifications.G2faService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;


@RestController
public class QuberaMerchantController {

    private static final Logger logger = LogManager.getLogger(QuberaMerchantController.class);
    private final static String API_PRIVATE_V2 = "/api/private/v2";
    private final QuberaService quberaService;
    private final InputOutputService inputOutputService;
    private final UserService userService;
    private final SecureService secureService;
    private final G2faService g2faService;

    @Autowired
    public QuberaMerchantController(QuberaService quberaService,
                                    InputOutputService inputOutputService,
                                    UserService userService,
                                    SecureService secureService,
                                    G2faService g2faService) {
        this.quberaService = quberaService;
        this.inputOutputService = inputOutputService;
        this.userService = userService;
        this.secureService = secureService;
        this.g2faService = g2faService;
    }

    @PostMapping(value = "/merchants/qubera/payment/status", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> statusPayment(@RequestBody QuberaLog requestDto) {
        Map<String, String> params = requestDto.getParams();
        logger.info("Response: " + params);
        quberaService.logResponse(requestDto);
        try {
            quberaService.sendNotification(requestDto);
            return ResponseEntity.ok("Thank you");
        } catch (RefillRequestAlreadyAcceptedException e) {
            return ResponseEntity.ok("Thank you");
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

//    @PostMapping(value = API_PRIVATE_V2 + "/merchants/qubera/payment/create", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseModel<?> createPaymentToMasterAccount(@RequestBody RefillRequestParamsDto requestParamsDto) {
//        Locale locale = Locale.ENGLISH;
//        RefillStatusEnum beginStatus = (RefillStatusEnum) RefillStatusEnum.X_STATE.nextState(CREATE_BY_USER);
//        Payment payment = new Payment(INPUT);
//        payment.setCurrency(requestParamsDto.getCurrency());
//        payment.setMerchant(requestParamsDto.getMerchant());
//        payment.setSum(requestParamsDto.getSum() == null ? 0 : requestParamsDto.getSum().doubleValue());
//        CreditsOperation creditsOperation = inputOutputService.prepareCreditsOperation(payment, getPrincipalEmail(), locale)
//                .orElseThrow(InvalidAmountException::new);
//        RefillRequestCreateDto request = new RefillRequestCreateDto(requestParamsDto, creditsOperation, beginStatus, locale);
//        Map<String, String> refill = quberaService.refill(request);
//        return new ResponseModel<>(refill);
//    }


    @PostMapping(value = API_PRIVATE_V2 + "/merchants/qubera/account/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<AccountQuberaResponseDto> createBankAccount(@RequestBody @Valid PinDtoSimple pinDto) {
        if (!userService.checkPin(getPrincipalEmail(), pinDto.getPin(), NotificationMessageEventEnum.QUBERA_ACCOUNT)) {
            User user = userService.findByEmail(getPrincipalEmail());
            secureService.sendPinCodeForCreateQuberaAccount(user);
            throw new IncorrectPinException("Incorrect pin: " + pinDto.getPin());
        }
        AccountQuberaResponseDto result = quberaService.createAccount(getPrincipalEmail());
        return new ResponseModel<>(result);
    }

    @GetMapping(value = API_PRIVATE_V2 + "/merchants/qubera/info")
    public ResponseModel<QuberaPaymentInfoDto> getInfoForPayment() {
        String email = getPrincipalEmail();
        return new ResponseModel<>(quberaService.getInfoForPayment(email));
    }

    @GetMapping(value = API_PRIVATE_V2 + "/merchants/qubera/account/check/{currency}")
    public ResponseModel<Boolean> checkUserAccountExist(@PathVariable("currency") String currency) {
        return new ResponseModel<>(quberaService.checkAccountExist(getPrincipalEmail(), currency));
    }

    @GetMapping(value = API_PRIVATE_V2 + "/merchants/qubera/account/info")
    public ResponseModel<AccountInfoDto> getUserAccountBalanceInfo() {
        AccountInfoDto result = quberaService.getInfoAccount(getPrincipalEmail());
        return new ResponseModel<>(result);
    }

    @GetMapping(value = API_PRIVATE_V2 + "/merchants/qubera/verification_status")
    public ResponseModel<String> getUserVerificationStatus() {
        String result = quberaService.getUserVerificationStatus(getPrincipalEmail());
        return new ResponseModel<>(result);
    }

    @SuppressWarnings("Duplicated")
    @PostMapping(value = API_PRIVATE_V2 + "/merchants/qubera/request/pin")
    public ResponseEntity<Void> sendUserPinCodeForCreateAccount() {
        try {
            User user = userService.findByEmail(getPrincipalEmail());
            if (!g2faService.isGoogleAuthenticatorEnable(user.getId())) {
                secureService.sendPinCodeForCreateQuberaAccount(user);
                return ResponseEntity.status(HttpStatus.CREATED).build();
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Failed to send pin code on user email", e);
            String message = "Failed to send pin code on user email";
            throw new NgResponseException(ErrorApiTitles.FAILED_TO_SEND_PIN_CODE_ON_USER_EMAIL, message);
        }
    }

    @PostMapping(value = API_PRIVATE_V2 + "/merchants/qubera/payment/external")
    public ResponseModel<?> createExternalPayment(@RequestBody ExternalPaymentShortDto externalPaymentDto) {
        ExternalPaymentResponseDto response =
                quberaService.createExternalPayment(externalPaymentDto, getPrincipalEmail());
        return new ResponseModel<>(response);
    }

    @PutMapping(value = API_PRIVATE_V2 + "/merchants/qubera/payment/external/confirm")
    public ResponseModel<?> confirmExternalPayment(@RequestBody @Valid RequestConfirmExternalPaymentDto requestDto) {
        if (!userService.checkPin(getPrincipalEmail(), requestDto.getPin(), NotificationMessageEventEnum.WITHDRAW)) {
            User user = userService.findByEmail(getPrincipalEmail());
            secureService.sendPinCodeForCreateQuberaAccount(user);
            throw new IncorrectPinException("Incorrect pin: " + requestDto.getPin());
        }
        return new ResponseModel<>(quberaService.confirmExternalPayment(requestDto.getPaymentId()));
    }

    private String getPrincipalEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({NgDashboardException.class})
    @ResponseBody
    public ErrorInfo OtherErrorsHandler(HttpServletRequest req, Exception exception) {
        return new ErrorInfo(req.getRequestURL(), exception);
    }
}
