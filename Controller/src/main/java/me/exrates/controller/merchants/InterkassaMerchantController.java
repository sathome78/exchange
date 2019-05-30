package me.exrates.controller.merchants;

import lombok.extern.log4j.Log4j2;
import me.exrates.service.InterkassaService;
import me.exrates.service.exception.RefillRequestAlreadyAcceptedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@Controller
@RequestMapping("/merchants/interkassa")
@Log4j2
public class InterkassaMerchantController {

    @Autowired
    private InterkassaService interkassaService;

    @PostMapping(value = "/payment/status")
    public ResponseEntity<Void> statusPayment(@RequestParam Map<String, String> params) {
        log.info("Response: " + params);
        try {
            interkassaService.processPayment(params);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RefillRequestAlreadyAcceptedException ex) {
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/payment/success")
    public RedirectView successPayment(@RequestParam Map<String, String> response) {
        log.debug(response);
        return new RedirectView("/dashboard");
    }
}
