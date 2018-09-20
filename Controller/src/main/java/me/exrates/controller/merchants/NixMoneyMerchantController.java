package me.exrates.controller.merchants;

import me.exrates.service.NixMoneyService;
import me.exrates.service.exception.RefillRequestAlreadyAcceptedException;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
public class NixMoneyMerchantController {

    @Autowired
    private NixMoneyService nixMoneyService;

    private static final Logger logger = LogManager.getLogger("merchant");

    @RequestMapping(value = "payment/status", method = RequestMethod.POST)
    public ResponseEntity<Void> statusPayment(@RequestParam Map<String, String> params) throws RefillRequestAppropriateNotFoundException {

        final ResponseEntity<Void> responseOK = new ResponseEntity<>(OK);
        logger.info("Response: " + params);
        try {
            nixMoneyService.processPayment(params);
            return responseOK;
        } catch (RefillRequestAlreadyAcceptedException e) {
            return responseOK;
        } catch (Exception e) {
            return new ResponseEntity<>(BAD_REQUEST);
        }
    }

    @RequestMapping(value = "payment/success", method = RequestMethod.POST)
    public RedirectView successPayment(@RequestParam Map<String, String> response) {
        logger.debug(response);
        return new RedirectView("/dashboard");
    }

}
