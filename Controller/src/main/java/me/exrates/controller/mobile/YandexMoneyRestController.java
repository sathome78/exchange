package me.exrates.controller.mobile;

import com.yandex.money.api.methods.BaseRequestPayment;
import com.yandex.money.api.methods.RequestPayment;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.service.InputOutputService;
import me.exrates.service.MerchantService;
import me.exrates.service.UserService;
import me.exrates.service.YandexMoneyService;
import me.exrates.service.exception.InvalidAmountException;
import me.exrates.service.exception.MerchantInternalException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;
import java.util.Optional;

/**
 * Created by OLEG on 26.10.2016.
 */
@RestController
public class YandexMoneyRestController {
    private static final Logger logger = LogManager.getLogger(YandexMoneyRestController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private YandexMoneyService yandexMoneyService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private InputOutputService inputOutputService;

   /* @RequestMapping(value = "/rest/yandexmoney/payment/process", method = RequestMethod.GET)
    public ResponseEntity<String> processYandexPayment(@RequestParam String token,
                                                       @RequestParam Integer userId,
                                                       @RequestParam Integer paymentId) {
        logger.debug("token: " + token);
        String email = userService.getUserById(userId).getEmail();
        Locale userLocale = userService.getUserLocaleForMobile(email);

        Payment payment = yandexMoneyService.getPaymentById(paymentId).orElseThrow(()
                -> new MerchantInternalException(messageSource.getMessage("merchants.authRejected", null, userLocale)));

        final CreditsOperation creditsOperation = inputOutputService
                .prepareCreditsOperation(payment, email, userLocale)
                .orElseThrow(InvalidAmountException::new);
        final Optional<RequestPayment> requestPayment = yandexMoneyService.requestPayment(token, creditsOperation);
        if (!requestPayment.isPresent()) {
            yandexMoneyService.deletePayment(paymentId);
            final String message = "merchants.successfulBalanceDeposit";
            return new ResponseEntity<>(messageSource.getMessage(message, merchantService.formatResponseMessage(creditsOperation).values().toArray(), userLocale), HttpStatus.OK);

        }
        final RequestPayment request = requestPayment.get();
        if (request.status.equals(BaseRequestPayment.Status.REFUSED)) {
            switch (request.error) {
                case PAYEE_NOT_FOUND:
                    throw new MerchantInternalException(messageSource.getMessage("merchants.incorrectPaymentDetails", null, userLocale));
                case NOT_ENOUGH_FUNDS:
                    throw new MerchantInternalException(messageSource.getMessage("merchants.notEnoughMoney", null, userLocale));
                case AUTHORIZATION_REJECT:
                    throw new MerchantInternalException(messageSource.getMessage("merchants.authRejected", null, userLocale));
                case LIMIT_EXCEEDED:
                    throw new MerchantInternalException(messageSource.getMessage("merchants.limitExceed", null, userLocale));
                case ACCOUNT_BLOCKED:
                    return new ResponseEntity<>(request.accountUnblockUri, HttpStatus.FOUND);
                case EXT_ACTION_REQUIRED:
                    return new ResponseEntity<>(request.extActionUri, HttpStatus.FOUND);
                default:
                    logger.fatal(request.error);
                    throw new MerchantInternalException(messageSource.getMessage("merchants.internalError", null, userLocale));
            }
        }

        throw new MerchantInternalException(messageSource.getMessage("merchants.internalError", null, userLocale));
    }*/


}
