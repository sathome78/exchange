package me.exrates.controller.merchants;

import me.exrates.model.Payment;
import me.exrates.service.MerchantService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Locale;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Controller
@RequestMapping("/merchants/ethereum/{merchantName}")
public class EthereumController {

//    @Autowired
//    @Qualifier("ethereumServiceImpl")
//    EthereumCommonService ethereumService;
//
//    @Autowired
//    @Qualifier("ethereumClassicServiceImpl")
//    EthereumCommonService ethereumClassicService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    MerchantService merchantService;

    private final Logger LOG = LogManager.getLogger("merchant");


    @RequestMapping(value = "/payment/prepare", method = POST)
    public ResponseEntity<Map<String, String>> preparePayment(final @RequestBody Payment payment,
                                                              final @PathVariable String merchantName,
                                                              final Principal principal,
                                                              final Locale locale) {
        /*if (!merchantService.checkInputRequestsLimit(payment.getCurrency(), principal.getName())){
            final Map<String,String> error = new HashMap<>();
            error.put("error", messageSource.getMessage("merchants.InputRequestsLimit", null, locale));

            return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
        }

        final String email = principal.getName();
        final CreditsOperation creditsOperation = merchantService
                .prepareCreditsOperation(payment, email)
                .orElseThrow(InvalidAmountException::new);
        try {
            String account = "";
            if (merchantName.equals("ethereum")){
                account = ethereumService.createAddress(creditsOperation);
            }else if (merchantName.equals("ethereum_classic")){
                account = ethereumClassicService.createAddress(creditsOperation);
            }

            final String notification = merchantService
                    .sendDepositNotification(account,email ,locale, creditsOperation, "merchants.depositNotification.body");
            final HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Content-Type", "text/plain; charset=utf-8");
            Map<String,String> responseMap = new TreeMap<>();
            responseMap.put("notification", notification);

            responseMap.put("qr", account);


            return new ResponseEntity<>(responseMap, HttpStatus.OK);
        } catch (Exception e) {
            final Map<String,String> error = new HashMap<>();
            error.put("error", messageSource.getMessage("merchants.incorrectPaymentDetails", null, locale));
            LOG.error(e);
            return new ResponseEntity<>(error, NOT_FOUND);
        }*/
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
