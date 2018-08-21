package me.exrates.controller.openAPI;

import me.exrates.service.exception.api.InvalidCurrencyPairFormatException;
import me.exrates.model.dto.CoinmarketApiDto;
import me.exrates.model.dto.openAPI.CurrencyPairInfoItem;
import me.exrates.model.dto.openAPI.OrderBookItem;
import me.exrates.model.dto.openAPI.OrderHistoryItem;
import me.exrates.model.dto.openAPI.TickerJsonDto;
import me.exrates.model.enums.OrderType;
import me.exrates.service.CurrencyService;
import me.exrates.service.OrderService;
import me.exrates.service.exception.CurrencyPairNotFoundException;
import me.exrates.service.exception.api.ErrorCode;
import me.exrates.service.exception.api.OpenApiError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static me.exrates.service.util.OpenApiUtils.formatCurrencyPairNameParam;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/openapi/v1/public")
public class OpenApiPublicController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CurrencyService currencyService;



    @RequestMapping("/ticker")
    public List<TickerJsonDto> getDailyTicker(
            @RequestParam(value = "currency_pair", required = false) String currencyPair) {
        String currencyPairName = null;
        if (currencyPair != null) {
            currencyPairName = formatCurrencyPairNameParam(currencyPair);
            validateCurrencyPair(currencyPairName);
        }
        return formatCoinmarketData(orderService.getDailyCoinmarketData(currencyPairName));
    }

    private void validateCurrencyPair(String currencyPairName) {
        currencyService.findCurrencyPairIdByName(currencyPairName);
    }


    private List<TickerJsonDto> formatCoinmarketData(List<CoinmarketApiDto> data) {
        return data.stream().map(TickerJsonDto::new).collect(Collectors.toList());
    }

    @RequestMapping("/orderbook/{currency_pair}")
    public Map<OrderType, List<OrderBookItem>> getOrderBook(@PathVariable(value = "currency_pair") String currencyPair,
                                                            @RequestParam(value = "order_type", required = false) OrderType orderType) {
        String currencyPairName = formatCurrencyPairNameParam(currencyPair);
        return orderService.getOrderBook(currencyPairName, orderType);
    }

    @RequestMapping("/history/{currency_pair}")
    public List<OrderHistoryItem> getRecentHistory(@PathVariable(value = "currency_pair") String currencyPair,
                                                   @RequestParam(required = false, defaultValue = "hour") String period) {

        String currencyPairName = formatCurrencyPairNameParam(currencyPair);
        return orderService.getRecentOrderHistory(currencyPairName, period);
    }

    @RequestMapping("/currency_pairs")
    public List<CurrencyPairInfoItem> findActiveCurrencyPairs() {
        return currencyService.findActiveCurrencyPairs();
    }



    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public OpenApiError mismatchArgumentsErrorHandler(HttpServletRequest req, MethodArgumentTypeMismatchException exception) {
        String detail = "Invalid param value : " + exception.getParameter().getParameterName();
        return new OpenApiError(ErrorCode.INVALID_PARAM_VALUE, req.getRequestURL(), detail);
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
