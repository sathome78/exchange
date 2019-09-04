package me.exrates.controller.merchants;

import me.exrates.service.CoinPayMerchantService;
import me.exrates.service.exception.RefillRequestAlreadyAcceptedException;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Collections;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@Controller
@RequestMapping("/merchant/coinpay")
public class CoinPayMerchantController {

    private static final Logger logger = LogManager.getLogger("merchant");
    @Autowired
    private CoinPayMerchantService coinPayMerchantService;

    @RequestMapping(value = "/payment/status/{id}", method = RequestMethod.GET)
    public ResponseEntity<Void> statusPayment(@PathVariable("id") String id) throws RefillRequestAppropriateNotFoundException {
        Map<String, String> params = Collections.singletonMap("id", id);
        final ResponseEntity<Void> responseOK = new ResponseEntity<>(OK);
        logger.info("Response: " + params);
        try {
            coinPayMerchantService.processPayment(params);
            return responseOK;
        } catch (RefillRequestAlreadyAcceptedException e) {
            return responseOK;
        } catch (Exception e) {
            return new ResponseEntity<>(BAD_REQUEST);
        }
    }

}
