package me.exrates.controller.merchants;

import me.exrates.controller.exception.ErrorInfo;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.dto.AccountCreateDto;
import me.exrates.model.dto.AccountQuberaResponseDto;
import me.exrates.model.dto.RefillRequestCreateDto;
import me.exrates.model.dto.RefillRequestParamsDto;
import me.exrates.model.dto.qubera.AccountInfoDto;
import me.exrates.model.dto.qubera.QuberaPaymentInfoDto;
import me.exrates.model.dto.qubera.QuberaRequestDto;
import me.exrates.model.enums.invoice.RefillStatusEnum;
import me.exrates.model.ngExceptions.NgDashboardException;
import me.exrates.model.ngModel.response.ResponseModel;
import me.exrates.service.InputOutputService;
import me.exrates.service.QuberaService;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.exception.RefillRequestAlreadyAcceptedException;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Map;

import static me.exrates.model.enums.OperationType.INPUT;
import static me.exrates.model.enums.invoice.InvoiceActionTypeEnum.CREATE_BY_USER;


@RestController
public class QuberaMerchantController {

    private static final Logger logger = LogManager.getLogger(QuberaMerchantController.class);
    private final static String API_PRIVATE_V2 = "/api/private/v2";
    private final QuberaService quberaService;
    private final InputOutputService inputOutputService;

    @Autowired
    public QuberaMerchantController(QuberaService quberaService,
                                    InputOutputService inputOutputService) {
        this.quberaService = quberaService;
        this.inputOutputService = inputOutputService;
    }

    @PostMapping(value = "/merchants/qubera/payment/status", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> statusPayment(@RequestBody QuberaRequestDto requestDto) {
        logger.info("Response: " + requestDto.getParams());
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

    @PostMapping(value = API_PRIVATE_V2 + "/merchants/qubera/payment/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<?> createPaymentToMasterAccount(@RequestBody RefillRequestParamsDto requestParamsDto) {
        Locale locale = Locale.ENGLISH;
        RefillStatusEnum beginStatus = (RefillStatusEnum) RefillStatusEnum.X_STATE.nextState(CREATE_BY_USER);
        Payment payment = new Payment(INPUT);
        payment.setCurrency(requestParamsDto.getCurrency());
        payment.setMerchant(requestParamsDto.getMerchant());
        payment.setSum(requestParamsDto.getSum() == null ? 0 : requestParamsDto.getSum().doubleValue());
        CreditsOperation creditsOperation = inputOutputService.prepareCreditsOperation(payment, getPrincipalEmail(), locale)
                .orElseThrow(InvalidAmountException::new);
        RefillRequestCreateDto request = new RefillRequestCreateDto(requestParamsDto, creditsOperation, beginStatus, locale);
        Map<String, String> refill = quberaService.refill(request);
        return new ResponseModel<>(refill);
    }


    @PostMapping(value = API_PRIVATE_V2 + "/merchants/qubera/account/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<AccountQuberaResponseDto> createBankAccount() {
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

//    @PostMapping(value = "/merchants/qubera/payment/internal", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseModel<?> createInternalPayment() {
//        return null;
//    }
//
//    @PostMapping(value = "/merchants/qubera/payment/external", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseModel<?> createExternalPayment() {
//        return null;
//    }
//
//    @PostMapping(value = API_PRIVATE_V2 + "/merchants/qubera/payment/toMaster", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseModel<ResponsePaymentDto> createPaymentToMaster(@RequestBody @Valid PaymentRequestDto paymentRequestDto) {
//        ResponsePaymentDto result = quberaService.createPaymentToMaster(getPrincipalEmail(), paymentRequestDto);
//        return new ResponseModel<>(result);
//    }
//
//    @PostMapping(value = "/merchants/qubera/payment/fromMaster", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseModel<ResponsePaymentDto> createPaymentFromMaster(@RequestBody @Valid PaymentRequestDto paymentRequestDto) {
//        ResponsePaymentDto result = quberaService.createPaymentFromMater(getPrincipalEmail(), paymentRequestDto);
//        return new ResponseModel<>(result);
//    }
//
//    @PutMapping(value = "/merchants/qubera/confirm/{paymentId}/toMaster")
//    public ResponseModel<String> confirmPaymentToMaster(@PathVariable Integer paymentId) {
//        return new ResponseModel<>(quberaService.confirmPaymentToMaster(paymentId));
//    }
//
//    @PutMapping(value = "/merchants/qubera/confirm/{paymentId}/fromMaster")
//    public ResponseModel<String> confirmPaymentFromMaster(@PathVariable Integer paymentId) {
//        return new ResponseModel<>(quberaService.confirmPaymentFRomMaster(paymentId));
//    }

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
