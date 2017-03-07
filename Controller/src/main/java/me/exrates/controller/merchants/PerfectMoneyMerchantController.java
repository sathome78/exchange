package me.exrates.controller.merchants;

import com.google.gson.Gson;
import me.exrates.model.CreditsOperation;
import me.exrates.model.Payment;
import me.exrates.model.Transaction;
import me.exrates.service.MerchantService;
import me.exrates.service.PerfectMoneyService;
import me.exrates.service.TransactionService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Controller
@RequestMapping("/merchants/perfectmoney")
public class PerfectMoneyMerchantController {

    @Autowired
    private PerfectMoneyService perfectMoneyService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private LocaleResolver localeResolver;


    private static final Logger logger = LogManager.getLogger("merchant");

    @RequestMapping(value = "payment/status",method = RequestMethod.POST)
    public ResponseEntity<Void> statusPayment(final @RequestParam Map<String,String> params,
                                              final RedirectAttributes redir) {

        logger.info("Response: " + params);

        final ResponseEntity<Void> response = new ResponseEntity<>(OK);
        final String hash = perfectMoneyService.computePaymentHash(params);

        logger.info("hash: " + hash);
        Transaction transaction = transactionService.findById(Integer.parseInt(params.get("PAYMENT_ID")));
        Double transactionSum = transaction.getAmount().add(transaction.getCommissionAmount()).doubleValue();

        if (transaction.isProvided()) {
            return response;
        }

        if (params.get("V2_HASH").equals(hash) && Double.parseDouble(params.get("PAYMENT_AMOUNT"))==transactionSum) {
            if (perfectMoneyService.provideTransaction(Integer.parseInt(params.get("PAYMENT_ID")))){
                return response;
            }
        }

        return new ResponseEntity<>(BAD_REQUEST);
    }

    @RequestMapping(value = "payment/prepare",method = RequestMethod.POST)
    public ResponseEntity<Map<String,String>> preparePayment(@RequestBody String body, Principal principal, HttpSession httpSession, final Locale locale) {
        final Payment payment = new Gson().fromJson(body, Payment.class);

        if (!merchantService.checkInputRequestsLimit(payment.getMerchant(), principal.getName())){
            final Map<String,String> error = new HashMap<>();
            error.put("error", messageSource.getMessage("merchants.InputRequestsLimit", null, locale));

            return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
        }

        final Optional<CreditsOperation> creditsOperation = merchantService.prepareCreditsOperation(payment, principal.getName());
        if (!creditsOperation.isPresent()) {
            final Map<String, String> errors = new HashMap<String, String>() {
                {
                    put("error", messageSource.getMessage("merchants.incorrectPaymentDetails", null, locale));
                }
            };
            return new ResponseEntity<>(errors, HttpStatus.NOT_FOUND);
        }
        final Transaction transaction = perfectMoneyService.preparePaymentTransactionRequest(creditsOperation.get());
        final Map<String, String> params = perfectMoneyService.getPerfectMoneyParams(transaction);
        final Object mutex = WebUtils.getSessionMutex(httpSession);
        synchronized (mutex) {
            httpSession.setAttribute("transaction",transaction);
            httpSession.setAttribute("payeeParams", params);
        }
        return new ResponseEntity<>(params,HttpStatus.OK);
    }

    @RequestMapping(value = "payment/success",method = RequestMethod.POST)
    public RedirectView successPayment(@RequestParam Map<String,String> response, HttpSession httpSession,
                                       RedirectAttributes redir, final HttpServletRequest request) {
        logger.info("Response: " + response);
        final Object mutex = WebUtils.getSessionMutex(httpSession);
        final Transaction openTransaction;
        synchronized (mutex) {
            openTransaction = (Transaction) httpSession.getAttribute("transaction");
            httpSession.removeAttribute("transaction");
        }
        final String hash = perfectMoneyService.computePaymentHash(response);
        Transaction transaction = transactionService.findById(Integer.parseInt(response.get("PAYMENT_ID")));
        Double transactionSum = transaction.getAmount().add(transaction.getCommissionAmount()).doubleValue();

        if (response.get("V2_HASH").equals(hash) && Double.parseDouble(response.get("PAYMENT_AMOUNT"))==transactionSum) {
            if (perfectMoneyService.provideTransaction(Integer.parseInt(response.get("PAYMENT_ID")))){
                redir.addAttribute("successNoty", messageSource.getMessage("merchants.successfulBalanceDeposit", merchantService.formatResponseMessage(openTransaction).values().toArray(), localeResolver.resolveLocale(request)));
                return new RedirectView("/dashboard");
            }
        }
        redir.addAttribute("errorNoty", messageSource.getMessage("merchants.incorrectPaymentDetails", null, localeResolver.resolveLocale(request)));
        return new RedirectView("/dashboard");
    }

    @RequestMapping(value = "payment/failure",method = RequestMethod.POST)
    public RedirectView failurePayment(@RequestBody String body,HttpSession httpSession,RedirectAttributes redir, final HttpServletRequest request) {
        final Transaction openTransaction;
        final Object mutex = WebUtils.getSessionMutex(httpSession);
        synchronized (mutex) {
            openTransaction = (Transaction) httpSession.getAttribute("transaction");
            httpSession.removeAttribute("transaction");
            httpSession.removeAttribute("payeeParams");
        }
        perfectMoneyService.invalidateTransaction(openTransaction);
        redir.addAttribute("errorNoty", messageSource.getMessage("merchants.authRejected", null, localeResolver.resolveLocale(request)));
        return new RedirectView("/dashboard");
    }
}
