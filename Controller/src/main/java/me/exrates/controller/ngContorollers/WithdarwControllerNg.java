package me.exrates.controller.ngContorollers;

import lombok.extern.log4j.Log4j2;
import me.exrates.controller.exception.ErrorInfo;
import me.exrates.model.Currency;
import me.exrates.model.MerchantCurrency;
import me.exrates.model.Payment;
import me.exrates.model.Wallet;
import me.exrates.model.dto.ngDto.WithdrawDataDto;
import me.exrates.model.enums.OperationType;
import me.exrates.model.util.BigDecimalProcessing;
import me.exrates.service.*;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static me.exrates.model.enums.OperationType.OUTPUT;
import static me.exrates.model.enums.UserCommentTopicEnum.WITHDRAW_CURRENCY_WARNING;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by Maks on 07.03.2018.
 */
@Log4j2
@RequestMapping("/info")
@RestController
public class WithdarwControllerNg {

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
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private InputOutputService inputOutputService;

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
