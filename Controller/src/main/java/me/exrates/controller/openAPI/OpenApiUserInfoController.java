package me.exrates.controller.openAPI;

import me.exrates.controller.exception.InvalidNumberParamException;
import me.exrates.model.dto.openAPI.OpenApiCommissionDto;
import me.exrates.model.dto.openAPI.UserOrdersDto;
import me.exrates.model.dto.openAPI.WalletBalanceDto;
import me.exrates.service.OrderService;
import me.exrates.service.WalletService;
import me.exrates.service.exception.CurrencyPairNotFoundException;
import me.exrates.service.exception.api.ErrorCode;
import me.exrates.service.exception.api.InvalidCurrencyPairFormatException;
import me.exrates.service.exception.api.OpenApiError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static me.exrates.service.util.OpenApiUtils.formatCurrencyPairNameParam;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;

@RestController
@RequestMapping("/openapi/v1/user")
public class OpenApiUserInfoController {

    @Autowired
    private WalletService walletService;

    @Autowired
    private OrderService orderService;


    @GetMapping(value = "/balances", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<WalletBalanceDto> userBalances() {
        return walletService.getBalancesForUser();
    }

    @GetMapping(value = "/orders/open", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<UserOrdersDto> userOpenOrders(@RequestParam(value = "currency_pair", required = false) String currencyPair) {

        String currencyPairName = null;
        if (currencyPair != null) {
            currencyPairName = formatCurrencyPairNameParam(currencyPair);
        }
        return orderService.getUserOpenOrders(currencyPairName);
    }

    @GetMapping(value = "/orders/closed", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public List<UserOrdersDto> userClosedOrders(@RequestParam(value = "currency_pair", required = false) String currencyPair,
                                          @RequestParam(required = false) Integer limit,
                                          @RequestParam(required = false) Integer offset) {

        String currencyPairName = null;
        if (currencyPair != null) {
            currencyPairName = formatCurrencyPairNameParam(currencyPair);
        }
        validateNaturalInt(limit);
        validateNaturalInt(offset);
        return orderService.getUserOrdersHistory(currencyPairName, limit, offset);
    }

    private void validateNaturalInt(Integer number) {
        if (number != null && number <= 0) {
            throw new InvalidNumberParamException("Invalid number: " + number);
        }
    }

    @GetMapping(value = "/commissions", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public OpenApiCommissionDto getCommissions() {
        return new OpenApiCommissionDto(orderService.getAllCommissions());
    }


    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(value = AccessDeniedException.class)
    public OpenApiError accessDeniedExceptionHandler(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.ACCESS_DENIED, req.getRequestURL(), exception);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public OpenApiError mismatchArgumentsErrorHandler(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.INVALID_PARAM_VALUE, req.getRequestURL(), exception);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public OpenApiError missingServletRequestParameterHandler(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.MISSING_REQUIRED_PARAM, req.getRequestURL(), exception);
    }

    @ResponseStatus(NOT_ACCEPTABLE)
    @ExceptionHandler(CurrencyPairNotFoundException.class)
    @ResponseBody
    public OpenApiError currencyPairNotFoundExceptionHandler(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.CURRENCY_PAIR_NOT_FOUND, req.getRequestURL(), exception);
    }


    @ResponseStatus(NOT_ACCEPTABLE)
    @ExceptionHandler(InvalidCurrencyPairFormatException.class)
    @ResponseBody
    public OpenApiError invalidCurrencyPairFormatExceptionHandler(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.INVALID_CURRENCY_PAIR_FORMAT, req.getRequestURL(), exception);
    }


    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public OpenApiError OtherErrorsHandler(HttpServletRequest req, Exception exception) {
        return new OpenApiError(ErrorCode.INTERNAL_SERVER_ERROR, req.getRequestURL(), exception);
    }


}
