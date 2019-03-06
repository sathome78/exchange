package me.exrates.controller.merchants;

import me.exrates.model.dto.QuberaRequestDto;
import me.exrates.service.QuberaService;
import me.exrates.service.exception.RefillRequestAlreadyAcceptedException;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@Controller
public class QuberaMerchantController {

    private final QuberaService quberaService;

    private static final Logger logger = LogManager.getLogger(QuberaMerchantController.class);

    @Autowired
    public QuberaMerchantController(QuberaService quberaService) {
        this.quberaService = quberaService;
    }

    @RequestMapping(value = "/merchants/qubera/payment/status", method = RequestMethod.POST)
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

}
