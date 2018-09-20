package me.exrates.controller.merchants;

import com.yandex.money.api.methods.BaseRequestPayment;
import com.yandex.money.api.methods.RequestPayment;
import com.yandex.money.api.utils.Strings;
import me.exrates.model.CreditsOperation;
import me.exrates.model.enums.OperationType;
import me.exrates.service.MerchantService;
import me.exrates.service.YandexMoneyService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@Controller
@RequestMapping("/merchants/yandexmoney")
public class YandexMoneyMerchantController {

    @Autowired
    private YandexMoneyService yandexMoneyService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private LocaleResolver localeResolver;

    private static final Logger logger = LogManager.getLogger("merchant");


    @RequestMapping(value = "/token/authorization", method = RequestMethod.GET)
    public RedirectView yandexMoneyTemporaryAuthorizationCodeRequest() {
        String url = yandexMoneyService.getTemporaryAuthCode();
        logger.debug("Url: " + url);
        return new RedirectView(url);
    }

    @RequestMapping(value = "/token/access")
    public ModelAndView yandexMoneyAccessTokenRequest(@RequestParam(value = "code", required = false) String code,
                                                      @RequestParam(value = "mobile", required = false) boolean mobile,
                                                      @RequestParam(value = "userId", required = false) Integer userId,
                                                      @RequestParam(value = "paymentId", required = false) Integer paymentId,
                                                      RedirectAttributes redir,
                                                      final HttpServletRequest request) {
        logger.debug("Code: " + code);
        if (Strings.isNullOrEmpty(code)) {
            redir.addAttribute("errorNoty", messageSource.getMessage("merchants.authRejected", null, localeResolver.resolveLocale(request)));
            return new ModelAndView("/dashboard");
        }
        final Optional<String> accessToken = yandexMoneyService.getAccessToken(code);
        if (!accessToken.isPresent()) {
            redir.addAttribute("errorNoty", messageSource.getMessage("merchants.authRejected", null, localeResolver.resolveLocale(request)));
            return new ModelAndView("/dashboard");
        }
        if (mobile && userId != null && paymentId != null) {
            return new ModelAndView("redirect:/rest/yandexmoney/payment/process?token=" + accessToken.get()
                    + "&userId=" + userId + "&paymentId=" + paymentId);
        }
        redir.addFlashAttribute("token", accessToken.get());
        return new ModelAndView("redirect:/merchants/yandexmoney/payment/process");
    }

    @RequestMapping(value = "/payment/process")
    public RedirectView processPayment(@ModelAttribute(value = "token") String token, RedirectAttributes redir,
                                       HttpSession httpSession, final HttpServletRequest httpServletRequest) {
        logger.debug("Token: " + token);
        final Object mutex = WebUtils.getSessionMutex(httpSession);
        final CreditsOperation creditsOperation;
        synchronized (mutex) {
            creditsOperation = (CreditsOperation) httpSession.getAttribute("creditsOperation");
            httpSession.removeAttribute("creditsOperation");
        }
        final Optional<RequestPayment> requestPayment = yandexMoneyService.requestPayment(token, creditsOperation);
        final RedirectView successView = new RedirectView("/dashboard");
        if (!requestPayment.isPresent()) {
            final OperationType operationType = creditsOperation.getOperationType();
            final String message = operationType == OperationType.INPUT ? "merchants.successfulBalanceDeposit"
                    : "merchants.successfulBalanceWithdraw";
            redir.addAttribute("successNoty", messageSource.getMessage(message, merchantService.formatResponseMessage(creditsOperation).values().toArray(), localeResolver.resolveLocale(httpServletRequest)));
            return successView;
        }
        final RequestPayment request = requestPayment.get();
        final RedirectView failureView = new RedirectView("/dashboard");
        if (request.status.equals(BaseRequestPayment.Status.REFUSED)) {
            switch (request.error) {
                case PAYEE_NOT_FOUND:
                    redir.addAttribute("errorNoty", messageSource.getMessage("merchants.incorrectPaymentDetails", null, localeResolver.resolveLocale(httpServletRequest)));
                    return failureView;
                case NOT_ENOUGH_FUNDS:
                    redir.addAttribute("errorNoty", messageSource.getMessage("merchants.notEnoughMoney", null, localeResolver.resolveLocale(httpServletRequest)));
                    return failureView;
                case AUTHORIZATION_REJECT:
                    redir.addAttribute("errorNoty", messageSource.getMessage("merchants.authRejected", null, localeResolver.resolveLocale(httpServletRequest)));
                    return failureView;
                case LIMIT_EXCEEDED:
                    redir.addAttribute("errorNoty", messageSource.getMessage("merchants.limitExceed", null, localeResolver.resolveLocale(httpServletRequest)));
                    return failureView;
                case ACCOUNT_BLOCKED:
                    return new RedirectView(request.accountUnblockUri);
                case EXT_ACTION_REQUIRED:
                    return new RedirectView(request.extActionUri);
                default:
                    logger.fatal(request.error);
                    redir.addAttribute("errorNoty", messageSource.getMessage("merchants.internalError", null, localeResolver.resolveLocale(httpServletRequest)));
                    return failureView;
            }
        }
        redir.addAttribute("errorNoty", messageSource.getMessage("merchants.internalError", null, localeResolver.resolveLocale(httpServletRequest)));
        return failureView;
    }
}