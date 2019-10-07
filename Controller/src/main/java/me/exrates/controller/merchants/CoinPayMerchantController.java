package me.exrates.controller.merchants;

import lombok.extern.log4j.Log4j2;
import me.exrates.service.CoinPayMerchantService;
import me.exrates.service.exception.RefillRequestAlreadyAcceptedException;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.exception.WithdrawRequestNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@Log4j2(topic = "coin_pay_log")
@RestController
@RequestMapping("/merchants/coinpay")
public class CoinPayMerchantController {

    @Autowired
    private CoinPayMerchantService coinPayMerchantService;

    @RequestMapping(value = "/payment/status/{id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> statusPayment(@PathVariable("id") String id,
                                              @RequestBody Map<String, String> params
    ) throws RefillRequestAppropriateNotFoundException {
        params.put("id", id);
        final ResponseEntity<Void> responseOK = new ResponseEntity<>(OK);
        log.info("Response from deposit callback: {}", params);
        try {
            coinPayMerchantService.processPayment(params);
            return responseOK;
        } catch (RefillRequestAlreadyAcceptedException e) {
            return responseOK;
        } catch (Exception e) {
            return new ResponseEntity<>(BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/payment/status/withdraw/{id}", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> statusPaymentWithdraw(@PathVariable("id") String id,
                                                      @RequestBody Map<String, String> params) throws RefillRequestAppropriateNotFoundException {
        final ResponseEntity<Void> responseOK = new ResponseEntity<>(OK);
        log.info("Response from withdraw callback: id {}, params {}", id, params);
        try {
            coinPayMerchantService.withdrawProcessCallBack(id, params);
            return responseOK;
        } catch (WithdrawRequestNotFoundException e) {
            return responseOK;
        } catch (Exception e) {
            return new ResponseEntity<>(BAD_REQUEST);
        }
    }

}
