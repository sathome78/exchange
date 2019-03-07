package me.exrates.controller.merchants;

import me.exrates.service.MerchantService;
import me.exrates.service.TransactionService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("/merchants/liqpay")
public class LiqpayMerchantController {

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private TransactionService transactionService;

//    @Autowired
//    private LiqpayService liqpayService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private LocaleResolver localeResolver;

    private static final Logger logger = LogManager.getLogger("merchant");

    private static final String merchantInputErrorPage = "redirect:/merchants/input";

    @RequestMapping(value = "payment/success", method = RequestMethod.POST)
    public RedirectView successPayment(@RequestParam Map<String, String> response, RedirectAttributes redir, final HttpServletRequest request) {

    /*    String signature = response.get("signature");
        String data = response.get("data");;


        data = data.replace("%3D","=");


        Map responseData = liqpayService.getResponse(data);
        logger.info("Response: " + response);

        Transaction transaction = transactionService.findById(Integer.parseInt(String.valueOf(responseData.get("order_id"))));
        Double transactionSum = transaction.getAmount().add(transaction.getCommissionAmount()).doubleValue();

        if ((responseData.get("status").equals("success"))
                && liqpayService.checkHashTransactionByTransactionId(transaction.getId(), (String) responseData.get("info"))
                && Double.parseDouble(String.valueOf(responseData.get("amount")))==transactionSum){

            redir.addAttribute("successNoty", messageSource.getMessage("merchants.successfulBalanceDeposit",
                    merchantService.formatResponseMessage(transaction).values().toArray(), localeResolver.resolveLocale(request)));

            if (!transaction.isProvided()){
                liqpayService.provideTransaction(transaction);
            }

            return new RedirectView("/dashboard");

        }

        redir.addAttribute("errorNoty", messageSource.getMessage("merchants.internalError", null, localeResolver.resolveLocale(request)));

*/
        return new RedirectView("/dashboard");
    }

    @RequestMapping(value = "payment/status", method = RequestMethod.POST)
    public RedirectView statusPayment(@RequestParam Map<String, String> response, RedirectAttributes redir, final HttpServletRequest request) {

        /*String signature = response.get("signature");
        String data = response.get("data");;


        data = data.replace("%3D","=");


        Map responseData = liqpayService.getResponse(data);
        logger.info("Response: " + response);

        Transaction transaction = transactionService.findById(Integer.parseInt(String.valueOf(responseData.get("order_id"))));
        Double transactionSum = transaction.getAmount().add(transaction.getCommissionAmount()).doubleValue();

        if ((responseData.get("status").equals("success"))
                && liqpayService.checkHashTransactionByTransactionId(transaction.getId(), (String) responseData.get("info"))
                && Double.parseDouble(String.valueOf(responseData.get("amount")))==transactionSum){

            redir.addAttribute("successNoty", messageSource.getMessage("merchants.successfulBalanceDeposit",
                    merchantService.formatResponseMessage(transaction).values().toArray(), localeResolver.resolveLocale(request)));
            if (!transaction.isProvided()){
                liqpayService.provideTransaction(transaction);
            }

            return new RedirectView("/dashboard");

        }

        redir.addAttribute("errorNoty", messageSource.getMessage("merchants.internalError", null, localeResolver.resolveLocale(request)));
*/
        return new RedirectView("/dashboard");
    }
}
