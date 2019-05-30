package me.exrates.controller.merchants;

import lombok.extern.log4j.Log4j2;
import me.exrates.service.NixMoneyService;
import me.exrates.service.exception.RefillRequestAlreadyAcceptedException;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
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
@Log4j2
public class NixMoneyMerchantController {

    @Autowired
    private NixMoneyService nixMoneyService;


    @RequestMapping(value = "payment/success", method = RequestMethod.POST)
    public ResponseEntity<Void> statusPayment(@RequestParam Map<String, String> params) throws RefillRequestAppropriateNotFoundException {

        final ResponseEntity<Void> responseOK = new ResponseEntity<>(OK);
        log.info("Response: " + params);
        try {
            nixMoneyService.processPayment(params);
            return responseOK;
        } catch (RefillRequestAlreadyAcceptedException e) {
            return responseOK;
        } catch (Exception e) {
            return new ResponseEntity<>(BAD_REQUEST);
        }
    }

    @RequestMapping(value = "payment/ok", method = RequestMethod.POST)
    public RedirectView successPayment(@RequestParam Map<String, String> response) {
        log.debug(response);
        return new RedirectView("/dashboard");
    }

}
