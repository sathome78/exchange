package me.exrates.controller.merchants;

import me.exrates.service.EDCService;
import me.exrates.service.exception.RefillRequestAlreadyAcceptedException;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@Controller
public class EDCController {


    @Autowired
    private EDCService edcService;

    private final Logger LOG = LogManager.getLogger("edc_log");

    @RequestMapping(value = "/merchants/edc/payment/received", method = RequestMethod.POST)
    public ResponseEntity<Void> statusPayment(@RequestBody Map<String, Object> paramsObject) throws RefillRequestAppropriateNotFoundException {

        final ResponseEntity<Void> responseOK = new ResponseEntity<>(OK);
        LOG.info("Response: " + paramsObject);
        try {
            Map<String, String> assetMap = (Map<String, String>) paramsObject.get("asset");
            if (assetMap.get("symbol").equals("EDC")) {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(paramsObject.get("id")));
                params.put("address", String.valueOf(paramsObject.get("address")));
                params.put("amount", String.valueOf(paramsObject.get("amount")));

                edcService.processPayment(params);
                return responseOK;
            } else {
                return new ResponseEntity<>(BAD_REQUEST);
            }
        } catch (RefillRequestAlreadyAcceptedException e) {
            LOG.info("EDC coin. Refill request already accepted exception: {}", e);
            return responseOK;
        } catch (Exception e) {
            LOG.error("EDC coin. Error: {}", e);
            return new ResponseEntity<>(BAD_REQUEST);
        }
    }


}
