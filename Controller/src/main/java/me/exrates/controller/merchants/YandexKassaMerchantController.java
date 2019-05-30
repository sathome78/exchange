package me.exrates.controller.merchants;

import lombok.extern.log4j.Log4j2;
import me.exrates.model.Transaction;
import me.exrates.service.MerchantService;
import me.exrates.service.TransactionService;
import me.exrates.service.YandexKassaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@Controller
@RequestMapping("/merchants/yandex_kassa")
@Log4j2
public class YandexKassaMerchantController {
    @Autowired
    private MerchantService merchantService;

    @Autowired
    private YandexKassaService yandexKassaService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private LocaleResolver localeResolver;

    @RequestMapping(value = "payment/status", method = RequestMethod.POST)
    public ResponseEntity<Void> statusPayment(final @RequestParam Map<String, String> params) {

        log.debug("Begin method: statusPayment.");
        final ResponseEntity<Void> response = new ResponseEntity<>(OK);
        log.info("Response: " + params);

        if (yandexKassaService.confirmPayment(params)) {
            return response;
        }

        return new ResponseEntity<>(BAD_REQUEST);
    }

    @RequestMapping(value = "payment/success", method = RequestMethod.GET)
    public RedirectView successPayment(@RequestParam final Map<String, String> response, final RedirectAttributes redir, final HttpServletRequest request) {

        log.debug("Begin method: successPayment.");
        Transaction transaction = transactionService.findById(Integer.parseInt(response.get("orderNumber")));
        log.info("Response: " + response);

        if (transaction.isProvided()) {

            redir.addAttribute("successNoty", messageSource.getMessage("merchants.successfulBalanceDeposit",
                    merchantService.formatResponseMessage(transaction).values().toArray(), localeResolver.resolveLocale(request)));

            return new RedirectView("/dashboard");

        }

        redir.addAttribute("errorNoty", messageSource.getMessage("merchants.internalError", null, localeResolver.resolveLocale(request)));

        return new RedirectView("/dashboard");
    }

}
