package me.exrates.controller.merchants;

import me.exrates.controller.exception.ErrorInfo;
import me.exrates.model.dto.AccountCreateDto;
import me.exrates.model.dto.QuberaRequestDto;
import me.exrates.model.ngExceptions.NgDashboardException;
import me.exrates.model.ngModel.response.ResponseModel;
import me.exrates.service.QuberaService;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
public class QuberaMerchantController {

    private static final Logger logger = LogManager.getLogger(QuberaMerchantController.class);
    private final QuberaService quberaService;

    private final static String PRIVATE_KYC = "/api/private/v2";

    @Autowired
    public QuberaMerchantController(QuberaService quberaService) {
        this.quberaService = quberaService;
    }

    @PostMapping(value = "/merchants/qubera/payment/status", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> statusPayment(@RequestBody QuberaRequestDto requestDto) {
        logger.info("Response: " + requestDto.getParams());
        quberaService.logResponse(requestDto);
        try {
            if (!(requestDto.getState().equalsIgnoreCase("Rejected"))) {
                quberaService.processPayment(requestDto.getParams());
            } else {
                // todo the payment was rejected
            }
            return ResponseEntity.ok("Thank you");
        } catch (RefillRequestAlreadyAcceptedException e) {
            return ResponseEntity.ok("Thank you");
        } catch (Exception e) {
            return new ResponseEntity<>(BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/merchants/qubera/payment/success", method = RequestMethod.GET)
    public ResponseEntity successPayment(@RequestParam Map<String, String> response) {
        logger.debug(response);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = PRIVATE_KYC +  "/merchants/qubera/account/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<?> createAccount(@RequestBody @Valid AccountCreateDto accountCreateDto) {
        accountCreateDto.setEmail(getPrincipalEmail());
        quberaService.createAccount(accountCreateDto);
        return null;
    }

    @GetMapping(value = "/merchants/qubera/account/check")
    public ResponseModel<?> checkUserAccountExist() {
        return null;
    }

    @GetMapping(value = "/merchants/qubera/account/info")
    public ResponseModel<?> getUserAccountInfo() {
        return null;
    }

    @PostMapping(value = "/merchants/qubera/payment/internal", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<?> createInternalPayment() {
        return null;
    }

    @PostMapping(value = "/merchants/qubera/payment/external", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<?> createExternalPayment() {
        return null;
    }

    @PostMapping(value = "/merchants/qubera/payment/toMaster", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<?> createPaymentToMaster() {
        return null;
    }

    @PostMapping(value = "/merchants/qubera/payment/fromMaster", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseModel<?> createPaymentFromMaster() {
        return null;
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
