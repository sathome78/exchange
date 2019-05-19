package me.exrates.controller.merchants;

import lombok.extern.log4j.Log4j2;
import me.exrates.service.exception.RefillRequestAlreadyAcceptedException;
import me.exrates.service.exception.RefillRequestAppropriateNotFoundException;
import me.exrates.service.exception.RefillRequestMemoIsNullException;
import me.exrates.service.exception.UsdxApiException;
import me.exrates.service.usdx.UsdxService;
import me.exrates.service.usdx.model.UsdxTransaction;
import me.exrates.service.usdx.model.enums.UsdxTransactionStatus;
import me.exrates.service.usdx.model.enums.UsdxTransactionType;
import me.exrates.service.usdx.model.enums.UsdxWalletAsset;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@Log4j2
@Controller
public class LightHouseController {

    @Autowired
    private UsdxService usdxService;

    @RequestMapping(value = "/merchants/lht/payment/received", method = RequestMethod.POST)
    public ResponseEntity<Void> statusPayment(@RequestBody UsdxTransaction usdxTransaction, HttpServletRequest request) {

        log.info("Response from USDX Wallet: " + usdxTransaction);

        try {
            if(usdxTransaction.getCurrency().equals(UsdxWalletAsset.LHT)){

                if (usdxTransaction.getType().equals(UsdxTransactionType.INCOMING)
                        && usdxTransaction.getStatus().equals(UsdxTransactionStatus.SUCCESS)){

                Map<String, String> params = new HashMap<>();
                params.put("transferId", String.valueOf(usdxTransaction.getTransferId()));
                params.put("memo", String.valueOf(usdxTransaction.getMemo()));
                params.put("amount", String.valueOf(usdxTransaction.getAmount()));

                //For security reason must get header from request and check it
                usdxService.checkHeaderOnValidForSecurity(request.getHeader(usdxService.getUsdxRestApiService().getSecurityHeaderName()), usdxTransaction);

                usdxService.processPayment(params);
                }
            } else {
                log.info("USDX Wallet recieve transaction with bad currency. Currency: " + usdxTransaction.getCurrency());
            }
            return ResponseEntity.ok().build();
            
        } catch (RefillRequestAlreadyAcceptedException | RefillRequestMemoIsNullException exception) {
            return ResponseEntity.ok().build();
        } catch (UsdxApiException usdxApiException){
            log.error("USDX Wallet. API Exception. Error in controller with object: " + usdxTransaction + " | Error: " + usdxApiException);
            return new ResponseEntity<>(BAD_REQUEST);
        } catch (Exception e) {
            log.error("USDX Wallet. Error in controller with object: " + usdxTransaction + " | Error: " + e);
            return new ResponseEntity<>(INTERNAL_SERVER_ERROR);
        }
    }


}
