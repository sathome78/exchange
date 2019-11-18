package me.exrates.controller.merchants;

import lombok.extern.log4j.Log4j2;
import me.exrates.service.NixMoneyService;
import me.exrates.service.exception.RefillRequestAlreadyAcceptedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@Controller
@RequestMapping("/merchants/nixmoney")
@Log4j2(topic = "nixmoney_log")
@PropertySource("classpath:/merchants/nixmoney.properties")
public class NixMoneyMerchantController {

    private final NixMoneyService nixMoneyService;
    private final String redirectUrl;

    @Autowired
    public NixMoneyMerchantController(NixMoneyService nixMoneyService,
                                      @Value("${nixmoney.redirectUrl}") String redirectUrl) {
        this.nixMoneyService = nixMoneyService;
        this.redirectUrl = redirectUrl;
    }

    @RequestMapping(value = "/payment/status", method = RequestMethod.POST)
    public ResponseEntity<Void> statusPayment(@RequestParam Map<String, String> params) {

        final ResponseEntity<Void> responseOK = new ResponseEntity<>(OK);
        log.info("Response nix money: " + params);
        try {
            nixMoneyService.processPayment(params);
            return responseOK;
        } catch (RefillRequestAlreadyAcceptedException e) {
            return responseOK;
        } catch (Exception e) {
            return new ResponseEntity<>(BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/payment/failure", method = RequestMethod.POST)
    public RedirectView failedPayment(@RequestParam Map<String, String> params) {
        String payeeAccount = params.get("PAYEE_ACCOUNT");
        String paymentAmount = params.get("PAYMENT_AMOUNT");
        String paymentUnits = params.get("PAYMENT_UNITS");
        String paymentBatchNum = params.get("PAYMENT_BATCH_NUM");
        String message = String.format("Failed payment payeeAccount %s, paymentAmount %s  paymentUnits %S, paymentBatchNum %s",
                payeeAccount, paymentAmount, paymentUnits, paymentBatchNum);
        log.info(message);
        return new RedirectView(redirectUrl);
    }

    @RequestMapping(value = "/payment/ok", method = RequestMethod.POST)
    public RedirectView successPayment(@RequestParam Map<String, String> response) {
        log.info("Success response: " + response);
        return new RedirectView(redirectUrl);
    }

}
