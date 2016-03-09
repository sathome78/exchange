package me.exrates.controller.merchants;

import me.exrates.service.exception.InvalidPayeeWalletException;
import me.exrates.service.exception.MerchantInternalException;
import me.exrates.service.exception.NotEnoughCompanyWalletMoneyException;
import me.exrates.service.exception.NotEnoughUserWalletMoneyException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Denis Savin (pilgrimm333@gmail.com)
 */
@ControllerAdvice
public class MerchantsExceptionHandlingAdvice {

    @ExceptionHandler(MerchantInternalException.class)
    public RedirectView handleInternal(MerchantInternalException e, HttpServletRequest request,HttpServletResponse response) {
        final String view = "/merchants/".concat(
                e.getMessage()
                        .endsWith("Input") ? "input" : "output"
        );
        final RedirectView redirectView = new RedirectView(view);
        final FlashMap flashMap = RequestContextUtils.getOutputFlashMap(request);
        if (flashMap != null) {
            flashMap.put("error","merchants.internalError");
        }
        return redirectView;
    }

    @ExceptionHandler(NotEnoughUserWalletMoneyException.class)
    public RedirectView handleNotEnoughMoney(NotEnoughUserWalletMoneyException e, HttpServletRequest request,HttpServletResponse response) {
        final RedirectView redirectView = new RedirectView("/merchants/output");
        final FlashMap flashMap = RequestContextUtils.getOutputFlashMap(request);
        if (flashMap != null) {
            flashMap.put("error","merchants.notEnoughMoney");
        }
        return redirectView;
    }


    //// TODO: 3/8/16 In this case block user acc and tell him about it
    @ExceptionHandler(NotEnoughCompanyWalletMoneyException.class)
    public RedirectView handleNotEnoughUserWalletMoneyException(NotEnoughUserWalletMoneyException e, HttpServletRequest request,HttpServletResponse response) {
        final RedirectView redirectView = new RedirectView("/merchants/output");
        final FlashMap flashMap = RequestContextUtils.getOutputFlashMap(request);
        if (flashMap != null) {
            flashMap.put("error","merchants.notEnoughMoney");
        }
        return redirectView;
    }

    @ExceptionHandler(InvalidPayeeWalletException.class)
    public RedirectView handleInvalidPayeeWalletException(InvalidPayeeWalletException e, HttpServletRequest request,HttpServletResponse response) {
        final RedirectView redirectView = new RedirectView("/merchants/output");
        final FlashMap flashMap = RequestContextUtils.getOutputFlashMap(request);
        if (flashMap != null) {
            flashMap.put("error","merchants.incorrectPaymentDetails");
        }
        return redirectView;
    }

//    @ExceptionHandler(InvalidAmountException.class)
//    public RedirectView handleInvalidPayeeWalletException(InvalidAmountException e, HttpServletRequest request,HttpServletResponse response) {
//        final RedirectView redirectView = new RedirectView("/merchants/output");
//        final FlashMap flashMap = RequestContextUtils.getOutputFlashMap(request);
//        if (flashMap != null) {
//            flashMap.put("error","merchants.incorrectPaymentDetails");
//        }
//        return redirectView;
//    }


}