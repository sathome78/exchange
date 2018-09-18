package me.exrates.controller.merchants;

import me.exrates.model.Transaction;
import me.exrates.service.MerchantService;
import me.exrates.service.Privat24Service;
import me.exrates.service.TransactionService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@Controller
@RequestMapping("/merchants/privat24")
public class Privat24MerchantController {
    @Autowired
    private MerchantService merchantService;

    @Autowired
    private Privat24Service privat24Service;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private LocaleResolver localeResolver;

    private static final Logger LOG = LogManager.getLogger("merchant");

    @RequestMapping(value = "payment/status", method = RequestMethod.POST)
    public ResponseEntity<Void> statusPayment(final @RequestParam Map<String, String> params,
                                              final RedirectAttributes redir) {

        LOG.debug("Begin method: statusPayment.");
        final ResponseEntity<Void> response = new ResponseEntity<>(OK);
        LOG.info("Response: " + params);

        String signature = params.get("signature");
        String payment = params.get("payment");
        LOG.debug("Get status payment: " + payment);
        String[] arrayResponse = payment.split("&");
        Map<String, String> mapResponse = new HashMap<>();
        for (String value : arrayResponse) {
            mapResponse.put(value.split("=")[0], value.split("=")[1]);
        }


        if (privat24Service.confirmPayment(mapResponse, signature, payment)) {
            return response;
        }


        return new ResponseEntity<>(BAD_REQUEST);
    }

    @RequestMapping(value = "payment/success", method = RequestMethod.POST)
    public RedirectView successPayment(@RequestParam Map<String, String> response, RedirectAttributes redir, final HttpServletRequest request) {

        LOG.debug("Begin method: successPayment.");

        String signature = response.get("signature");
        String payment = response.get("payment");
        String[] arrayResponse = payment.split("&");
        Map<String, String> mapResponse = new HashMap<>();
        for (String value : arrayResponse) {
            mapResponse.put(value.split("=")[0], value.split("=")[1]);
        }
        LOG.info("Response: " + response);

        Transaction transaction;
        try {
            transaction = transactionService.findById(Integer.parseInt(mapResponse.get("order")));
            if (!transaction.isProvided()) {
                if (!privat24Service.confirmPayment(mapResponse, signature, payment)) {

                    redir.addAttribute("errorNoty", messageSource.getMessage("merchants.authRejected", null, localeResolver.resolveLocale(request)));
                    return new RedirectView("/dashboard");
                }
            }
        } catch (EmptyResultDataAccessException e) {
            LOG.error(e);
            redir.addAttribute("errorNoty", messageSource.getMessage("merchants.incorrectPaymentDetails", null, localeResolver.resolveLocale(request)));
            return new RedirectView("/dashboard");
        }

        redir.addAttribute("successNoty", messageSource.getMessage("merchants.successfulBalanceDeposit",
                merchantService.formatResponseMessage(transaction).values().toArray(), localeResolver.resolveLocale(request)));

        return new RedirectView("/dashboard");
    }

}
