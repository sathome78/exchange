package me.exrates.controller.ngContorollers;

import lombok.extern.log4j.Log4j2;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.model.*;
import me.exrates.model.Currency;
import me.exrates.model.dto.ngDto.RefillPageDataDto;
import me.exrates.model.dto.ngDto.WithdrawDataDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.service.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

import static me.exrates.model.enums.OperationType.INPUT;
import static me.exrates.model.enums.OperationType.OUTPUT;
import static me.exrates.model.enums.UserCommentTopicEnum.REFILL_CURRENCY_WARNING;
import static me.exrates.model.enums.UserCommentTopicEnum.WITHDRAW_CURRENCY_WARNING;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Maks on 08.02.2018.
 */
@Log4j2
@RequestMapping("/info")
@RestController
public class InputOutputNgController {

    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private RefillService refillService;
    @Autowired
    private WithdrawService withdrawService;
    @Autowired
    private UserService userService;
    @Autowired
    private WalletService walletService;


    @RequestMapping(value = "/private/merchants/input", method = GET)
    public RefillPageDataDto inputCredits(@RequestParam("currency") String currencyName) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        RefillPageDataDto response = new RefillPageDataDto();
        OperationType operationType = INPUT;
        Currency currency = currencyService.findByName(currencyName);
        response.setCurrency(currency);
        Payment payment = new Payment();
        payment.setOperationType(operationType);
        response.setPayment(payment);
        BigDecimal minRefillSum = currencyService.retrieveMinLimitForRoleAndCurrency(userService.getUserRoleFromSecurityContext(), operationType, currency.getId());
        response.setMinRefillSum(minRefillSum);
        Integer scaleForCurrency = currencyService.getCurrencyScaleByCurrencyId(currency.getId()).getScaleForRefill();
        response.setScaleForCurrency(scaleForCurrency);
        List<Integer> currenciesId = Collections.singletonList(currency.getId());
        List<MerchantCurrency> merchantCurrencyData = merchantService.getAllUnblockedForOperationTypeByCurrencies(currenciesId, operationType);
        refillService.retrieveAddressAndAdditionalParamsForRefillForMerchantCurrencies(merchantCurrencyData, userEmail);
        response.setMerchantCurrencyData(merchantCurrencyData);
        List<String> warningCodeList = currencyService.getWarningForCurrency(currency.getId(), REFILL_CURRENCY_WARNING);
        response.setWarningCodeList(warningCodeList);
        response.setIsaMountInputNeeded(merchantCurrencyData.size() > 0
                && !merchantCurrencyData.get(0).getProcessType().equals("CRYPTO"));
        return response;
    }

    @RequestMapping(value = "/merchants/output", method = GET)
    public WithdrawDataDto outputCredits(
            @RequestParam("currency") String currencyName) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        WithdrawDataDto response = new WithdrawDataDto();
        OperationType operationType = OUTPUT;
        Currency currency = currencyService.findByName(currencyName);
        response.setCurrency(currency);
        Wallet wallet = walletService.findByUserAndCurrency(userService.findByEmail(userEmail), currency);
        response.setWallet(wallet);
        response.setBalance(BigDecimalProcessing.formatNonePoint(wallet.getActiveBalance(), false));
        Payment payment = new Payment();
        payment.setOperationType(operationType);
        response.setPayment(payment);
        BigDecimal minWithdrawSum = currencyService.retrieveMinLimitForRoleAndCurrency(userService.getUserRoleFromSecurityContext(), operationType, currency.getId());
        response.setMinWithdrawSum(minWithdrawSum);
        Integer scaleForCurrency = currencyService.getCurrencyScaleByCurrencyId(currency.getId()).getScaleForWithdraw();
        response.setScaleForCurrency(scaleForCurrency);
        List<Integer> currenciesId = Collections.singletonList(currency.getId());
        List<MerchantCurrency> merchantCurrencyData = merchantService.getAllUnblockedForOperationTypeByCurrencies(currenciesId, operationType);
        withdrawService.retrieveAddressAndAdditionalParamsForWithdrawForMerchantCurrencies(merchantCurrencyData);
        response.setMerchantCurrencyData(merchantCurrencyData);
        List<String> warningCodeList = currencyService.getWarningForCurrency(currency.getId(), WITHDRAW_CURRENCY_WARNING);
        response.setWarningCodeList(warningCodeList);
        return response;
    }



    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ErrorInfo OtherErrorsHandler(HttpServletRequest req, Exception exception) {
        log.error(ExceptionUtils.getStackTrace(exception));
        return new ErrorInfo(req.getRequestURL(), exception);
    }

}
